package com.qceda.module.blog;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * App specific properties. Will first load from classpath properties file
 * 'app.properties'. Will override classpath propeties with startup property
 * call 'env.props.file' if given. Finally will override individual propety if
 * given in startup.
 * 
 * @author vudooman
 *
 */
public class AppProps {

	private static Logger logger = LoggerFactory.getLogger(AppProps.class);

	public static final String NEO4J_URI = "neo4j.URI";
	public static final String NEO4J_CLEAN = "neo4j.clean";
	public static final String JWT_SECRET = "jwt.secret";
	public static final String TOKEN_TTL = "token.ttl";

	private static class AppPropsHolder {
		public static AppProps INSTANCE = new AppProps(System.getProperties());
	}

	public static AppProps getInstance() {
		return AppPropsHolder.INSTANCE;
	}

	private Properties props;

	protected AppProps(Properties systemProps) {
		this.props = new Properties();

		// Classpath app properties
		try (final InputStream stream = this.getClass().getResourceAsStream("app.properties")) {
			props.load(stream);
		} catch (IOException ex) {
			logger.error("Could not load app properties", ex);
		}

		// Environmental props file
		String envProps = systemProps.getProperty("env.props.file");
		if (envProps != null && envProps.length() > 0) {
			File envFile = new File(envProps);
			if (envFile.exists()) {
				try (final FileReader reader = new FileReader(envFile)) {
					props.load(reader);
				} catch (IOException ex) {
					logger.error("Could not load environment properties file", ex);
				}
			} else {
				logger.error(String.format("Enviroment file '%s' does not exist", envProps));
			}
		}

		// Individual prop from startup
		this.setPropFromSystem(systemProps, props, NEO4J_URI);
		this.setPropFromSystem(systemProps, props, NEO4J_CLEAN);
		this.setPropFromSystem(systemProps, props, JWT_SECRET);
		this.setPropFromSystem(systemProps, props, TOKEN_TTL);
	}

	/**
	 * Load prop from system if available
	 * 
	 * @param props
	 * @param name
	 */
	private void setPropFromSystem(Properties systemProps, Properties props, String name) {
		String sysProp = systemProps.getProperty(name);
		if (sysProp != null && sysProp.length() > 0) {
			props.setProperty(name, sysProp);
		}
	}

	public String getProperty(String key) {
		return this.props.getProperty(key);
	}
}
