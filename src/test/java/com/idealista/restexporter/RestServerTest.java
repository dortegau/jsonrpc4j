package com.idealista.restexporter;

import static org.junit.Assert.*;

import static com.jayway.restassured.RestAssured.*;
import static com.jayway.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jayway.restassured.http.ContentType;

//@ContextConfiguration
//@RunWith(SpringJUnit4ClassRunner.class)
public class RestServerTest extends ServerRunner {

	private static final String REMOTING_DUMMY_SERVICE_JSON = "/remoting/DummyService/getDate";

	private static final int TEST_APP_PORT = 10420;

	@Test
	public void testName() throws Exception {
		given().port(TEST_APP_PORT)
				.get(REMOTING_DUMMY_SERVICE_JSON)
				.then()
				.contentType(ContentType.JSON)
				.statusCode(200);
	}

}