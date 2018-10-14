package com.qceda.module.blog;

import static com.qceda.module.blog.AppProps.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests to make sure that application properties are obtain from classpath,
 * environment prop file and individual prop setting.
 * 
 * @author vudooman
 *
 */
public class AppPropsTest {
	private Properties appProps;

	@Before
	public void setup() throws IOException {
		this.appProps = new Properties();

		// Classpath app properties
		try (final InputStream stream = AppProps.class.getResourceAsStream("app.properties")) {
			this.appProps.load(stream);
		}
	}

	@Test
	public void testClasspathOnly() {
		AppProps props = new AppProps(new Properties());
		Assert.assertTrue(this.appProps.getProperty(TOKEN_TTL).equals(props.getProperty(TOKEN_TTL)));
	}

	@Test
	public void testWithStartupProp() {
		String uri = "bolt://neo4j:testpw@localhost:7687";

		Properties sysProps = new Properties();
		sysProps.setProperty(DB_URI, uri);

		AppProps props = new AppProps(sysProps);
		Assert.assertTrue(props.getProperty(DB_URI).equals(uri));
	}
}
