package com.qceda.module.blog.controller;

import static com.qceda.module.blog.service.AuthService.PASSWORD_REQUIRED;
import static com.qceda.module.blog.service.AuthService.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.google.gson.Gson;
import com.qceda.module.blog.AppProps;
import com.qceda.module.blog.wso.UsernamePassword;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

	@Autowired
	private MockMvc mvc;

	@Test
	public void testCreateAccountEmptyBody() throws Exception {
		mvc.perform(MockMvcRequestBuilders.post("/api/account").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testCreateAccountUsernameOnly() throws Exception {

		UsernamePassword wso = new UsernamePassword();
		Gson gson = new Gson();
		wso.setUsername("myName");
		mvc.perform(MockMvcRequestBuilders.post("/api/account").content(gson.toJson(wso))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.failureMessage", is(PASSWORD_REQUIRED)));
	}

	@Test
	public void testCreateAccountPasswordOnly() throws Exception {

		UsernamePassword wso = new UsernamePassword();
		Gson gson = new Gson();
		wso.setPassword("myPW");
		mvc.perform(MockMvcRequestBuilders.post("/api/account").content(gson.toJson(wso))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest()).andExpect(jsonPath("$.failureMessage", is(USERNAME_REQUIRED)));
	}

	/**
	 * Only do this test for embedded DB that would be used in a development
	 * settings to prevent db data change. On a production environment, there should
	 * be integration testing done that can manage data changes.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateAccount() throws Exception {

		String dbUri = AppProps.getInstance().getProperty(AppProps.DB_URI);
		if (dbUri.indexOf("file://") == 0) {
			Gson gson = new Gson();

			UsernamePassword wso = new UsernamePassword();
			wso.setUsername("myUsername");
			wso.setPassword("myPW");

			String json = gson.toJson(wso);
			// Should not be able to login
			mvc.perform(MockMvcRequestBuilders.post("/api/login").content(json)
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isUnauthorized());

			// Should be able to create account
			mvc.perform(MockMvcRequestBuilders.post("/api/account").content(json)
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

			// Should be able to login after account created
			mvc.perform(MockMvcRequestBuilders.post("/api/login").content(json)
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());

			// Should not be able to create duplicate account
			mvc.perform(MockMvcRequestBuilders.post("/api/account").content(json)
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest()).andExpect(jsonPath("$.failureMessage", is(USER_EXISTS)));

		}
	}

}
