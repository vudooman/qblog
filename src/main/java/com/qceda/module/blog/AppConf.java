package com.qceda.module.blog;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.qceda.module.blog.util.PasswordHelper;
import com.qceda.module.blog.util.impl.BCryptPasswordHelperImpl;

/**
 * App Configuration for db, transaction and others
 * 
 * @author vudooman
 *
 */
@Configuration
@EnableTransactionManagement
@ComponentScan("com.qceda.module.blog")
@EnableNeo4jRepositories("com.qceda.module.blog.repo")
public class AppConf {

	private static Logger logger = LoggerFactory.getLogger(AppConf.class);

	@Bean
	public SessionFactory sessionFactory() {
		return new SessionFactory(configuration(), "com.qceda.module.blog");
	}

	@Bean
	public Neo4jTransactionManager transactionManager() throws Exception {
		return new Neo4jTransactionManager(sessionFactory());
	}

	/**
	 * Get from property what kind of DB type (e.g. file, http, bolt, etc.) to use.
	 * If file, previous file DB can be clean for true testing without side-effects
	 * 
	 * @return
	 */
	@Bean
	public org.neo4j.ogm.config.Configuration configuration() {
		String uri = AppProps.getInstance().getProperty(AppProps.NEO4J_URI);
		if (uri.startsWith("file://") && "true".equals(AppProps.getInstance().getProperty(AppProps.NEO4J_CLEAN))) {
			String noProtocol = uri.substring(7);
			File file = new File(noProtocol);
			if (file.exists()) {
				try {
					FileUtils.deleteDirectory(file);
				} catch (IOException ex) {
					logger.error("Could not delete existing Neo4j DB", ex);
				}

			}
		}
		return new org.neo4j.ogm.config.Configuration.Builder().uri(uri).build();
	}

	/**
	 * Strategy for password encryption at rest
	 * 
	 * @return
	 */
	@Bean
	public PasswordHelper passwordHelper() {
		return new BCryptPasswordHelperImpl();
	}
}