package ar.com.samsistemas.middleware.procesadortramite.routebuilder;

import java.util.HashMap;
import java.util.Map;

import static org.apache.activemq.camel.component.ActiveMQComponent.*;

import ar.com.samsistemas.middleware.procesadortramite.entities.TramiteSocio;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;

public class ProcesadorTramiteRouteBuilder extends RouteBuilder {

	@Override
	public void configure() {

		/** Bind JMS connection to camel context **/

		getContext().addComponent("activemq", activeMQComponent("tcp://localhost:61616"));

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
		queryParameters.put("estado", (Math.random()>0.5)?"RECHAZADO":"CERRADO");
		queryParameters.put("contrato_socio", tramite.getSocioContrato());

		exchange.getOut().setBody(queryParameters);
	}
	
}