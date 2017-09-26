package ar.com.samsistemas.middleware.procesadortramite.routebuilder;

import java.util.HashMap;
import java.util.Map;

import static org.apache.activemq.camel.component.ActiveMQComponent.*;

import ar.com.samsistemas.middleware.procesadortramite.entities.TramiteSocio;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.model.dataformat.JaxbDataFormat;

import javax.annotation.Resource;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.sql.DataSource;

@Startup
@ApplicationScoped
@ContextName("ProcesadorTramite-context")
public class ProcesadorTramiteRouteBuilder extends RouteBuilder {

	@Resource(lookup = "java:jboss/jdbc/mysql")
	private DataSource dataSource;

	@Override
	public void configure() {

		/** Bind JMS connection to camel context **/

		getContext().addComponent("activemq", activeMQComponent("tcp://localhost:61616"));

		/** Bind JDBC datasource to camel context **/

		SimpleRegistry reg = new SimpleRegistry();
		reg.put("mysqlDS", dataSource);

		CompositeRegistry compositeRegistry = new CompositeRegistry();
		compositeRegistry.addRegistry(getContext().getRegistry());
		compositeRegistry.addRegistry(reg);

		DefaultCamelContext ctx = (DefaultCamelContext)getContext();
		ctx.setRegistry(compositeRegistry);

		/** Define XML to POJO dataformat **/

		JaxbDataFormat dataFormat = new JaxbDataFormat(true);
		dataFormat.setContextPath(TramiteSocio.class.getPackage().getName());
		dataFormat.setPartClass(TramiteSocio.class.getName());

		/** Exception handling **/

		onException(Exception.class)
			.log(LoggingLevel.ERROR, "ar.com.samsistemas.services.tramites.routebuilder", "ProcesadorTramite exception: ${exception}")
			.handled(true);

		/** Get message from AMQ and insert into DB **/

		from("activemq:TRAMITES")
			.unmarshal(dataFormat)
			.process(this::procesarTramite)
			.to("sql:"+insertQuery()+"?dataSource=mysqlDS");

	}

	private String insertQuery(){
		return "INSERT INTO poc_middleware.TramiteSocio(tipo,descripcion,estado,timestamp,id_socio) " +
				"VALUES (:#tipo,:#descripcion,:#estado, NOW(), (SELECT id FROM poc_middleware.Socio s WHERE s.contrato = :#contrato_socio))";
	}

	private void procesarTramite(Exchange exchange) {

		TramiteSocio tramite = exchange.getIn().getBody(TramiteSocio.class);

		Map<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("tipo", tramite.getTipo());
		queryParameters.put("descripcion", tramite.getDescripcion());
		queryParameters.put("estado", (Math.random()>0.75)?"RECHAZADO":"CERRADO");
		queryParameters.put("contrato_socio", tramite.getSocioContrato());

		exchange.getOut().setBody(queryParameters);
	}
	
}