package com.qceda.module.blog.controller;

import static com.qceda.module.blog.service.BlogService.BLOG_SPACE_INVALID;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
import com.qceda.module.blog.entity.Blog;
import com.qceda.module.blog.entity.BlogSpace;
import com.qceda.module.blog.service.AuthService;
import com.qceda.module.blog.service.BlogService;
import com.qceda.module.blog.wso.ServiceResponse;
import com.qceda.module.blog.wso.UserAuthToken;
import com.qceda.module.blog.wso.UserData;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BlogControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private AuthService authService;

	@Autowired
	private BlogService blogService;

	private String authToken;

	private UserData authUserData;

	private BlogSpace createdSpace;

	@Before
	public void setup() {
		String username = "testuser";
		String password = "test123";
		UserAuthToken authToken = this.authService.getAuthToken(username, password);
		if (authToken == null || authToken.getAuthToken() == null) {
			this.authService.registerAccount(username, password);
			authToken = this.authService.getAuthToken(username, password);
		}
		this.authToken = authToken.getAuthToken().getToken();
		this.authUserData = authToken.getUser();

		BlogSpace space = new BlogSpace();
		space.setTitle(String.format("Antother Test Space %s", System.currentTimeMillis()));
		ServiceResponse<BlogSpace> res = this.blogService.createSpace(this.authUserData, space);
		if (res.isSuccess()) {
			this.createdSpace = res.getResult();
		}

	}

	@After
	public void cleanup() {
		// TODO: cleanup created data
	}

	@Test
	public void testGetBlogs() throws Exception {
		mvc.perform(MockMvcRequestBuilders.get("/api/blogs").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$").isArray());
	}

	@Test
	public void testCreateBlogNoAuth() throws Exception {
		Gson gson = new Gson();

		Blog blog = new Blog();
		blog.setTitle("Test");
		mvc.perform(MockMvcRequestBuilders.post("/api/blogspaces/123/blogs").content(gson.toJson(blog))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isUnauthorized());
	}

	@Test
	public void testCreateBlogInvalidSpace() throws Exception {
		Assert.assertTrue(this.authToken != null && this.authToken.length() > 0);
		Gson gson = new Gson();
		Blog blog = new Blog();
		blog.setTitle("Test");
		mvc.perform(MockMvcRequestBuilders.post("/api/blogspaces/123/blogs").content(gson.toJson(blog))
				.header("Authorization", String.format("Bearer %s", this.authToken))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.failureMessage", is(BLOG_SPACE_INVALID)));
	}

	/**
	 * Only do this test for embedded DB that would be used in a development
	 * settings to prevent db data change. On a production environment, there should
	 * be integration testing done that can manage data changes.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateSpace() throws Exception {

		String dbUri = AppProps.getInstance().getProperty(AppProps.DB_URI);
		if (dbUri.indexOf("file://") == 0) {

			Gson gson = new Gson();

			BlogSpace space = new BlogSpace();
			space.setTitle(String.format("Test Space %s", System.currentTimeMillis()));

			String json = gson.toJson(space);

			mvc.perform(MockMvcRequestBuilders.post("/api/blogspaces").content(json)
					.header("Authorization", String.format("Bearer %s", this.authToken))
					.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
		}

	}

	/**
	 * Only do this test for embedded DB that would be used in a development
	 * settings to prevent db data change. On a production environment, there should
	 * be integration testing done that can manage data changes.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateBlog() throws Exception {

		String dbUri = AppProps.getInstance().getProperty(AppProps.DB_URI);
		if (dbUri.indexOf("file://") == 0) {

			Assert.assertTrue(this.createdSpace != null);

			Gson gson = new Gson();

			Blog blog = new Blog();
			blog.setTitle(String.format("Test Blog %s", System.currentTimeMillis()));
			blog.setDescription("HI");

			String json = gson.toJson(blog);

			mvc.perform(
					MockMvcRequestBuilders.post(String.format("/api/blogspaces/%s/blogs", this.createdSpace.getId()))
							.content(json).header("Authorization", String.format("Bearer %s", this.authToken))
							.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk());
		}
	}
}
