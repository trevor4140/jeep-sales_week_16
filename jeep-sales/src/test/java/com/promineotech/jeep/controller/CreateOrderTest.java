package com.promineotech.jeep.controller;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.promineotech.jeep.entity.JeepModel;
import com.promineotech.jeep.entity.Order;

import lombok.Getter;
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)

@ActiveProfiles("test")

@Sql(scripts = {
    "classpath:migrations/V1.0__Jeep_Schema.sql",
    "classpath:migrations/V1.1__Jeep_Data.sql"},
    config = @SqlConfig(encoding = "utf-8"))
class CreateOrderTest {
	@Autowired
	@Getter
	private TestRestTemplate restTemplate;
	@LocalServerPort
	private int serverPort;
	@Test
	void testCreatOrderReturnsSuccess() {
		String body = createOrderBody();
		
		String uri = String.format("http://localhost:%d/orders", serverPort);
		
		HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<String> bodyEntity = new HttpEntity<>(body, headers);
		ResponseEntity<Order> response = 
				restTemplate.exchange(uri, HttpMethod.POST, bodyEntity, Order.class);
		
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();

		Order order = response.getBody();
		assertThat(order.getCustomer().getCustomerId()).isEqualTo("IGNATOV_GISELLA");
		assertThat(order.getModel().getModelId()).isEqualTo(JeepModel.CHEROKEE);
		assertThat(order.getModel().getTrimLevel()).isEqualTo("Trailhawk");
		assertThat(order.getModel().getNumDoors()).isEqualTo(4);
		assertThat(order.getColor().getColorId()).isEqualTo("EXT_VELVET_RED");
		assertThat(order.getEngine().getEngineId()).isEqualTo("3_6_HYBRID");
		assertThat(order.getTire().getTireId()).isEqualTo("265_BRIDGESTONE");
		assertThat(order.getOptions()).hasSize(7);

	}

	/**
	   * 
	   * @return
	   */
	  protected String createOrderBody() {
	    // @formatter:off
	    return "{\n"
	        + " \"customer\":\"IGNATOV_GISELLA\", \n"
	        + " \"model\":\"CHEROKEE\", \n"
	        + " \"trim\":\"Trailhawk\", \n"
	        + " \"doors\":\"4\", \n"
	        + " \"color\":\"EXT_VELVET_RED\",\n"
	        + " \"engine\":\"3_6_HYBRID\", \n"
	        + " \"tire\":\"265_BRIDGESTONE\", \n"
	        + " \"options\":[\n"
	        + "     \"DOOR_WARRIOR_MIRROR\",\n"
	        + "     \"EXT_DUAL_UPPER_PREMIUM\", \n"
	        + "     \"INT_MOPAR_GRAB\", \n"
	        + "     \"INT_MOPAR_COLR\", \n"
	        + "     \"TOP_MOPAR_SKY\", \n"
	        + "     \"WHEEL_TACTIK_MATTE\", \n"
	        + "     \"EXT_MOPAR_CAMERA\" \n"
	        + " ]\n"
	        + "}";
	    //formatter:on
	  }

}
