package com.qceda.module.blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qceda.module.blog.entity.Blog;
import com.qceda.module.blog.entity.BlogComment;
import com.qceda.module.blog.entity.BlogSpace;
import com.qceda.module.blog.service.BlogService;
import com.qceda.module.blog.util.RequestHelper;
import com.qceda.module.blog.wso.ServiceResponse;

/**
 * Controller to handle all blog related requests
 * 
 * @author vtran
 *
 */
@RestController
@RequestMapping(value = "/api")
public class BlogController {

	@Autowired
	private BlogService blogService;

	/**
	 * Create a new blog space
	 * 
	 * @param space
	 * @return
	 */
	@PostMapping("/blogspaces")
	public ResponseEntity<?> createBlogSpace(@RequestBody BlogSpace space) {
		final BlogService service = this.blogService;

		return RequestHelper.authRequiredRequest((user) -> {
			ServiceResponse<BlogSpace> res = service.createSpace(user, space);
			if (res.isSuccess()) {
				return new ResponseEntity<>(res.getResult(), HttpStatus.OK);
			}
			return RequestHelper.handleServiceFailure(res);
		});
	}

	/**
	 * Create a new blog in a given space
	 * 
	 * @param blogSpaceId
	 * @param blog
	 * @return
	 */
	@PostMapping("/blogspaces/{blogSpaceId}/blogs")
	public ResponseEntity<?> createBlog(@PathVariable("blogSpaceId") long blogSpaceId, @RequestBody Blog blog) {
		final BlogService service = this.blogService;
		return RequestHelper.authRequiredRequest((user) -> {
			blog.setSpace(new BlogSpace());
			blog.getSpace().setId(blogSpaceId);
			ServiceResponse<Blog> res = service.createBlog(user, blog);
			if (res.isSuccess()) {
				return new ResponseEntity<>(res.getResult(), HttpStatus.OK);
			}
			return RequestHelper.handleServiceFailure(res);
		});
	}

	/**
	 * Create comment for a given blog
	 * 
	 * @param blogId
	 * @param comment
	 * @return
	 */
	@PostMapping("/blogs/{blogId}/comments")
	public ResponseEntity<?> createComment(@PathVariable("blogId") long blogId, @RequestBody BlogComment comment) {
		final BlogService service = this.blogService;

		return RequestHelper.authRequiredRequest((user) -> {
			comment.setBlog(new Blog());
			comment.getBlog().setId(blogId);
			ServiceResponse<BlogComment> res = service.createComment(user, comment);
			if (res.isSuccess()) {
				return new ResponseEntity<>(res.getResult(), HttpStatus.OK);
			}
			return RequestHelper.handleServiceFailure(res);
		});
	}

	/**
	 * Get all comments for a given blog
	 * 
	 * @param blogId
	 * @return
	 */
	@GetMapping("/blogs/{blogId}/comments")
	public ResponseEntity<?> getComments(@PathVariable("blogId") long blogId) {
		ServiceResponse<List<BlogComment>> res = this.blogService.getComments(blogId);
		if (res.isSuccess()) {
			return new ResponseEntity<>(res.getResult(), HttpStatus.OK);
		}
		return RequestHelper.handleServiceFailure(res);
	}

	/**
	 * Get all blog spaces
	 * 
	 * @return
	 */
	@GetMapping("/blogspaces")
	public ResponseEntity<?> getBlogspaces() {
		ServiceResponse<List<BlogSpace>> res = this.blogService.getBlogspaces(null);
		if (res.isSuccess()) {
			return new ResponseEntity<>(res.getResult(), HttpStatus.OK);
		}
		return RequestHelper.handleServiceFailure(res);
	}

	/**
	 * Get context user blog spaces
	 * 
	 * @return
	 */
	@GetMapping("/myspaces")
	public ResponseEntity<?> getMyspaces() {

		final BlogService service = this.blogService;
		return RequestHelper.authRequiredRequest((user) -> {
			ServiceResponse<List<BlogSpace>> res = service.getBlogspaces(user);
			if (res.isSuccess()) {
				return new ResponseEntity<>(res.getResult(), HttpStatus.OK);
			}
			return RequestHelper.handleServiceFailure(res);
		});
	}

	/**
	 * Get all blogs
	 * 
	 * @return
	 */
	@GetMapping("/blogs")
	public ResponseEntity<?> getBlogs() {
		ServiceResponse<List<Blog>> res = this.blogService.getBlogs();
		if (res.isSuccess()) {
			return new ResponseEntity<>(res.getResult(), HttpStatus.OK);
		}
		return RequestHelper.handleServiceFailure(res);
	}
}
