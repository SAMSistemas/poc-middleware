package ar.com.samsistemas.middleware.solicitadortramite.routebuilder;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import static org.apache.activemq.camel.component.ActiveMQComponent.*;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.HashMap;
import java.util.Map;


@Startup
@ApplicationScoped
@ContextName("SolicitadorTramite-context")
public class SolicitadorTramiteRouteBuilder extends RouteBuilder {

	@Override
	public void configure() {

		/** Bind JMS connection with camel context **/

		getContext().addComponent("activemq", activeMQComponent("tcp://localhost:61616"));

		/** Define XML to JSON dataformat **/

		XmlJsonDataFormat xmlJsonFormat = new XmlJsonDataFormat();
		xmlJsonFormat.setEncoding("UTF-8");
		xmlJsonFormat.setForceTopLevelObject(true);
		xmlJsonFormat.setTrimSpaces(true);
		xmlJsonFormat.setRootName("tramite");
		xmlJsonFormat.setSkipNamespaces(true);
		xmlJsonFormat.setRemoveNamespacePrefixes(true);

		/** Exception handling **/

		onException(Exception.class)
			.log(LoggingLevel.ERROR, "ar.com.samsistemas.services.tramites.routebuilder", "SolicitadorTramite exception: ${exception}")
			.handled(true)
			.setFaultBody(simple("SolicitadorTramite exception: ${exception}"));

		/** Servlet that accepts JSON/SOAP **/

		from("servlet:/?servletName=SolicitadorTramiteServlet")
			.choice()
				.when(header(Exchange.CONTENT_TYPE).isEqualTo("application/json")) //JSON
					.to("direct:jsonRequest")
				.when(header(Exchange.CONTENT_TYPE).in("text/xml","application/xml","application/soap+xml"))  //SOAP
					.to("direct:soapRequest")
				.when(header("wsdl"))
					.to("language:simple:resource:classpath:/wsdl/SolicitadorTramite.wsdl")
				.otherwise()
					.log(LoggingLevel.ERROR, "ar.com.samsistemas.services.tramites.routebuilder", "El servicio espera JSON o XML")
					.setFaultBody(constant("Content-Type incorrecto. El servicio espera JSON o XML"))
			.end();

		/** JSON Routes **/

		from("direct:jsonRequest")
			.choice()
				.when(header(Exchange.HTTP_METHOD).isEqualTo("GET"))
					.to("direct:jsonGet")
				.when(header(Exchange.HTTP_METHOD).isEqualTo("POST"))
					.to("direct:jsonPost")
				.otherwise()
					.log(LoggingLevel.ERROR, "ar.com.samsistemas.services.tramites.routebuilder", "Metodo HTTP incorrecto")
					.setFaultBody(constant("Metodo HTTP incorrecto. Solo GET y POST válidos"))
			.end();

		from("direct:jsonGet")
			.process(this::getRequestParameter)
//			.to("sql:"+getQuery()+"?dataSource=java:jboss/jdbc/mysql" +
//					"&outputClass=ar.com.samsistemas.middleware.solicitadortramite.entities.TramiteSocioDTO") TODO: mock
			.marshal().json(JsonLibrary.Jackson);

		from("direct:jsonPost")
			.unmarshal(xmlJsonFormat)
			.log(LoggingLevel.DEBUG, "ar.com.samsistemas.services.tramites.routebuilder", "JSON to XML.. ${in.body}")
			.to("direct:xmlToMQ")
			.marshal(xmlJsonFormat)
			.log(LoggingLevel.DEBUG, "ar.com.samsistemas.services.tramites.routebuilder", "XML to JSON.. ${in.body}");


		/** SOAP Routes **/

		from("direct:soapRequest")
			.choice()
				.when(header(Exchange.HTTP_METHOD).isEqualTo("POST"))
					.to("xslt://xslt/removeSoapEnvelope.xslt")
					.log(LoggingLevel.DEBUG, "ar.com.samsistemas.services.tramites.routebuilder", "SOAP to XML.. ${in.body}")
					.to("direct:xmlToMQ")
					.to("xslt://xslt/addSoapEnvelope.xslt")
					.log(LoggingLevel.DEBUG, "ar.com.samsistemas.services.tramites.routebuilder", "XML to SOAP.. ${in.body}")
				.otherwise()
					.log(LoggingLevel.ERROR, "ar.com.samsistemas.services.tramites.routebuilder", "Metodo HTTP incorrecto")
					.setFaultBody(constant("Metodo HTTP incorrecto. Solo GET y POST válidos"))
			.end();


		/** Common Routes **/

		from("direct:xmlToMQ")
			.to("activemq:TRAMITES?exchangePattern=InOnly")
			.setHeader("status", constant(200))
			.setHeader("descripcion", constant("El tramite fue enviado para ser procesado"))
			.to("xslt://xslt/response.xslt");

	}

	private void getRequestParameter(Exchange exchange) {

		Map<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("contrato_socio", exchange.getIn().getHeader("socio", String.class));

		exchange.getOut().setBody(queryParameters);
	}


	private String getQuery(){
		return "SELECT * FROM poc_middleware.TramiteSocio " +
				"WHERE id_socio = (SELECT id FROM poc_middleware.Socio s WHERE s.contrato = :#contrato_socio)";
	}

}