package com.qceda.module.blog.repo;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import com.qceda.module.blog.entity.Blog;

/**
 * Blog Repo/DAO
 * 
 * @author vudooman
 *
 */
@Repository
public interface BlogRepo extends Neo4jRepository<Blog, Long> {

}
