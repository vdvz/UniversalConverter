package com.example.UniversalConverter;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.logging.Logger;

@SpringBootTest
@WebAppConfiguration
class UniversalConverterApplicationTests {

	private final Logger logger = Logger.getLogger("UniversalConverterApplicationTests.class");

	protected MockMvc mvc;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@BeforeEach
	public void init(){
		mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void firstRequest() throws Exception {
		String uri = "/convert";

		JSONObject req = new JSONObject();
		req.put("from","b").put("to","d");
		logger.info("Sending string " + req.toString());

		MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri).contentType(MediaType.APPLICATION_JSON).content(req.toString())).andReturn();

		int status = mvcResult.getResponse().getStatus();
		logger.info("Status is " + status);
	}

	@Test
	void processRequest(){

	}

}
