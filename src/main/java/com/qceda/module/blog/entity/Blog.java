package com.qceda.module.blog.entity;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Blog {
	@Id
	@GeneratedValue
	private Long id;

	private String title;

	private String description;

	private Long createdTime;

	@Relationship(type = "WRITTEN_FOR")
	private BlogSpace space;

	@Relationship(type = "WRITTEN_BY")
	private User author;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}

	public BlogSpace getSpace() {
		return space;
	}

	public void setSpace(BlogSpace space) {
		this.space = space;
	}
}
