package com.qceda.module.blog.repo;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qceda.module.blog.entity.User;

/**
 * User Repo/DAO
 * 
 * @author vudooman
 *
 */
@Repository
public interface UserRepo extends Neo4jRepository<User, Long> {

	/**
	 * Get user by username or email
	 * 
	 * @param usernameOrEmail
	 * @return
	 */
	@Query("MATCH (u:User)-[aw:AUTHENTICATE_WITH]->(c:Credential) WHERE u.name = {usernameOrEmail} OR u.email = {usernameOrEmail} RETURN u, aw, c")
	User findUserByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);
}