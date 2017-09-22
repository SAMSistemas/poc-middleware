package ar.com.samsistemas.middleware.procesadortramite.routebuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import static org.apache.activemq.camel.component.ActiveMQComponent.*;

import ar.com.samsistemas.middleware.procesadortramite.entities.TramiteSocio;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.model.dataformat.JaxbDataFormat;


@Startup
@ApplicationScoped
@ContextName("ProcesadorTramite-context")
public class ProcesadorTramiteRouteBuilder extends RouteBuilder {

	@Override
	public void configure() {

		// Conecto con la instancia de ActiveMQ
		getContext().addComponent("activemq", activeMQComponent("tcp://localhost:61616"));

		JaxbDataFormat dataFormat = new JaxbDataFormat(true);
		dataFormat.setContextPath(TramiteSocio.class.getPackage().getName());
		dataFormat.setPartClass(TramiteSocio.class.getName());

		// Manejo de excepciones
		onException(Exception.class)
			.log(LoggingLevel.ERROR, "ar.com.samsistemas.services.tramites.routebuilder", "ProcesadorTramite exception: ${exception}")
			.handled(true);

		//Obtengo mensaje de la cola MQ correspondiente a un tramite
		from("activemq:TRAMITES")
			.unmarshal(dataFormat)
			.process(this::procesarTramite)
			.to("sql:"+getQuery()+"?dataSource=java:jboss/jdbc/mysql");

	}

	private String getQuery(){
		return "INSERT INTO poc_middleware.TramiteSocio(tipo,descripcion,estado,timestamp,id_socio) " +
				"VALUES (:#tipo,:#descripcion,:#estado, NOW(), (SELECT id FROM poc_middleware.Socio s WHERE s.contrato = :#contrato_socio))";
	}

	private void procesarTramite(Exchange exchange) {

		TramiteSocio tramite = exchange.getIn().getBody(TramiteSocio.class);

		Map<String, Object> queryParameters = new HashMap<>();
		queryParameters.put("tipo", tramite.getTipo());
		queryParameters.put("descripcion", tramite.getDescripcion());
		queryParameters.put("estado", (Math.random()>0.5)?"ABIERTO":"CERRADO");
		queryParameters.put("contrato_socio", tramite.getSocioContrato());

		exchange.getOut().setBody(queryParameters);
	}
	
}