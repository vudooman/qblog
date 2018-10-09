package com.qceda.module.blog.repo;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qceda.module.blog.entity.BlogSpace;

/**
 * Blog space Repo/DAO
 * 
 * @author vudooman
 *
 */
@Repository
public interface BlogSpaceRepo extends Neo4jRepository<BlogSpace, Long> {

	@Query("MATCH (bs:BlogSpace)-[cb:CREATED_BY]->(u:User) WHERE ID(u) = {userId} AND bs.title = {title} RETURN bs, cb, u")
	BlogSpace findByUserAndTitle(@Param("userId") Long userId, @Param("title") String title);

	@Query("MATCH (bs:BlogSpace)-[cb:CREATED_BY]->(u:User) WHERE ID(u) = {userId} AND ID(bs) = {blogSpaceId} RETURN bs, cb, u")
	BlogSpace findByUserAndId(@Param("userId") Long userId, @Param("blogSpaceId") Long blogSpaceId);

	@Query("MATCH (bs:BlogSpace)-[cb:CREATED_BY]->(u:User) WHERE ID(u) = {userId} RETURN bs, cb, u")
	List<BlogSpace> findAllByUser(@Param("userId") Long userId);
}
