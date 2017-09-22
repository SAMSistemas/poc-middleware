package ar.com.samsistemas.middleware.solicitadortramite.routebuilder;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import static org.apache.activemq.camel.component.ActiveMQComponent.*;

import ar.com.samsistemas.middleware.solicitadortramite.entities.TramiteSocioDTO;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.dataformat.xmljson.XmlJsonDataFormat;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.apache.camel.model.dataformat.JsonLibrary;

import java.util.HashMap;
import java.util.Map;


@Startup
@ApplicationScoped
@ContextName("SolicitadorTramite-context")
public class SolicitadorTramiteRouteBuilder extends RouteBuilder {

	@Override
	public void configure() {

		// Conecto con la instancia de ActiveMQ
		getContext().addComponent("activemq", activeMQComponent("tcp://localhost:61616"));

//		JaxbDataFormat dbDataFormat = new JaxbDataFormat(true);
//		dbDataFormat.setContextPath(TramiteSocioDTO.class.getPackage().getName());
//		dbDataFormat.setPartClass(TramiteSocioDTO.class.getName());

		XmlJsonDataFormat xmlJsonFormat = new XmlJsonDataFormat();
		xmlJsonFormat.setEncoding("UTF-8");
		xmlJsonFormat.setForceTopLevelObject(true);
		xmlJsonFormat.setTrimSpaces(true);
		xmlJsonFormat.setRootName("tramite");
		xmlJsonFormat.setSkipNamespaces(true);
		xmlJsonFormat.setRemoveNamespacePrefixes(true);

		// Manejo de excepciones
		onException(Exception.class)
			.log(LoggingLevel.ERROR, "ar.com.samsistemas.services.tramites.routebuilder", "SolicitadorTramite exception: ${exception}")
			.handled(true)
			.setFaultBody(simple("SolicitadorTramite exception: ${exception}"));

		//Expongo el servicio en un servlet que acepta JSON/SOAP
		from("servlet:/?servletName=SolicitadorTramiteServlet")
			.choice()
				.when(header(Exchange.CONTENT_TYPE).isEqualTo("application/json"))
					.to("direct:jsonRequest")
				.when(header(Exchange.CONTENT_TYPE).in("text/xml","application/soap+xml"))
					.to("direct:soapRequest")
				.otherwise()
					.log(LoggingLevel.ERROR, "ar.com.samsistemas.services.tramites.routebuilder", "El servicio espera JSON o XML")
					.setBody(constant("El servicio espera JSON o XML"))
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
					.setBody(constant("Metodo HTTP incorrecto. Solo GET y POST válidos"))
			.end();

		from("direct:jsonGet")
			.process(this::getRequestParameter)
			.to("sql:"+getQuery()+"?dataSource=java:jboss/jdbc/mysql" +
					"&outputClass=ar.com.samsistemas.middleware.solicitadortramite.entities.TramiteSocioDTO")
			.marshal().json(JsonLibrary.Gson);

		from("direct:jsonPost")
			.unmarshal(xmlJsonFormat)
			.to("direct:xmlToMQ")
			.marshal(xmlJsonFormat);


		/** SOAP Routes **/

		from("direct:soapRequest")
			.choice()
				.when(header(Exchange.HTTP_METHOD).isEqualTo("GET"))
					.to("language:simple:resource:classpath:/wsdl/SolicitadorTramite.wsdl")
				.when(header(Exchange.HTTP_METHOD).isEqualTo("POST"))
					.to("xslt://xslt/removeSoapEnvelope.xslt")
					.to("direct:xmlToMQ")
				.otherwise()
					.log(LoggingLevel.ERROR, "ar.com.samsistemas.services.tramites.routebuilder", "Metodo HTTP incorrecto")
					.setBody(constant("Metodo HTTP incorrecto. Solo GET y POST válidos"))
			.end();


		/** Common Routes **/

		from("direct:xmlToMQ")
			.to("activemq:TRAMITES")
			.setHeader("status", constant(200))
			.setHeader("descripcion", constant("El tramite fue enviado para ser procesado"))
			.to("xslt://xslt/response.xslt");

	}

	private void getRequestParameter(Exchange exchange) {

		String path = exchange.getIn().getHeader(Exchange.HTTP_PATH, String.class);

		Map<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("contrato_socio", path.substring(path.lastIndexOf("/")));

		exchange.getOut().setBody(queryParameters);
	}


	private String getQuery(){
		return "SELECT * FROM poc_middleware.TramiteSocioDTO " +
				"WHERE id_socio = (SELECT id FROM poc_middleware.Socio s WHERE s.contrato = :#contrato_socio)";
	}

}