package com.qceda.module.blog.repo;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qceda.module.blog.entity.BlogComment;

/**
 * Blog Comment Repo/DAO
 * 
 * @author vudooman
 *
 */
@Repository
public interface BlogCommentRepo extends Neo4jRepository<BlogComment, Long> {
	@Query("MATCH (u:User)-[co:COMMENTED_ON]->(b:Blog) WHERE ID(b) = {blogId} RETURN u, co, b")
	List<BlogComment> findAllBlogId(@Param("blogId") Long blogId);
}