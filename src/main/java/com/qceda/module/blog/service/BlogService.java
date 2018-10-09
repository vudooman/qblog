package com.qceda.module.blog.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qceda.module.blog.entity.Blog;
import com.qceda.module.blog.entity.BlogComment;
import com.qceda.module.blog.entity.BlogSpace;
import com.qceda.module.blog.entity.User;
import com.qceda.module.blog.repo.BlogCommentRepo;
import com.qceda.module.blog.repo.BlogRepo;
import com.qceda.module.blog.repo.BlogSpaceRepo;
import com.qceda.module.blog.util.LogHelper;
import com.qceda.module.blog.wso.ServiceResponse;
import com.qceda.module.blog.wso.UserData;

/**
 * Main service for blog module. Provide most used services for blogging.
 * 
 * @author vudooman
 *
 */
@Service
public class BlogService {

	private static Logger logger = LoggerFactory.getLogger(BlogService.class);

	public static String BLOG_SPACE_TITLE_NOT_UNIQUE = "BLOG_SPACE_TITLE_NOT_UNIQUE";
	public static String BLOG_SPACE_TITLE_NOT_AVAILABLE = "BLOG_SPACE_TITLE_NOT_AVAILABLE";
	public static String BLOG_TITLE_NOT_AVAILABLE = "BLOG_TITLE_NOT_AVAILABLE";
	public static String BLOG_SPACE_INVALID = "BLOG_SPACE_INVALID";
	public static String BLOG_INVALID = "BLOG_INVALID";

	@Autowired
	private BlogSpaceRepo spaceRepo;

	@Autowired
	private BlogRepo blogRepo;

	@Autowired
	private BlogCommentRepo blogCommentRepo;

	/**
	 * Create a blog space for a given user
	 * 
	 * @param userData
	 * @param space
	 * @return
	 */
	@Transactional
	public ServiceResponse<BlogSpace> createSpace(UserData userData, BlogSpace space) {
		// Title is required
		if (space.getTitle() == null || space.getTitle().length() == 0) {
			return new ServiceResponse<>(ServiceResponse.FailureCode.ValidationFailure, BLOG_SPACE_TITLE_NOT_AVAILABLE);
		}

		ServiceResponse<BlogSpace> res = null;
		try {
			BlogSpace existing = this.spaceRepo.findByUserAndTitle(userData.getId(), space.getTitle());
			if (existing != null) {
				return new ServiceResponse<>(ServiceResponse.FailureCode.ValidationFailure,
						BLOG_SPACE_TITLE_NOT_UNIQUE);
			}

			User user = this.fromUserData(userData);
			space.setCreator(user);

			space = this.spaceRepo.save(space);
			res = new ServiceResponse<>(true, space);
		} catch (Exception ex) {
			String refId = LogHelper.unexpected(logger, ex);
			res = new ServiceResponse<>(ServiceResponse.FailureCode.Unknown, null, refId);
		}

		return res;
	}

	/**
	 * Create a blog entry for a given user
	 * 
	 * @param userData
	 * @param blog
	 * @return
	 */
	@Transactional
	public ServiceResponse<Blog> createBlog(UserData userData, Blog blog) {

		// Title is required
		if (blog.getTitle() == null || blog.getTitle().length() == 0) {
			return new ServiceResponse<>(ServiceResponse.FailureCode.ValidationFailure, BLOG_TITLE_NOT_AVAILABLE);
		}

		// Blog space must be valid
		BlogSpace existing = this.spaceRepo.findByUserAndId(userData.getId(), blog.getSpace().getId());
		if (existing == null) {
			return new ServiceResponse<>(ServiceResponse.FailureCode.InvalidData, BLOG_SPACE_INVALID);
		}
		blog.setSpace(existing);

		ServiceResponse<Blog> res = null;

		try {
			blog.setCreatedTime(System.currentTimeMillis());

			User user = this.fromUserData(userData);
			blog.setAuthor(user);

			blog = this.blogRepo.save(blog);

			res = new ServiceResponse<>(true, blog);
		} catch (Exception ex) {
			String refId = LogHelper.unexpected(logger, ex);
			res = new ServiceResponse<>(ServiceResponse.FailureCode.Unknown, null, refId);
		}
		return res;
	}

	/**
	 * Create comment for a given blog
	 * 
	 * @param userData
	 * @param blogComment
	 * @return
	 */
	@Transactional
	public ServiceResponse<BlogComment> createComment(UserData userData, BlogComment blogComment) {

		Blog existing = this.blogRepo.findById(blogComment.getBlog().getId()).get();
		if (existing == null) {
			return new ServiceResponse<>(ServiceResponse.FailureCode.InvalidData, BLOG_INVALID);
		}

		blogComment.setBlog(existing);

		ServiceResponse<BlogComment> res = null;

		try {
			blogComment.setCreatedTime(System.currentTimeMillis());

			User user = this.fromUserData(userData);
			blogComment.setCommenter(user);

			blogComment = this.blogCommentRepo.save(blogComment);

			res = new ServiceResponse<>(true, blogComment);
		} catch (Exception ex) {
			String refId = LogHelper.unexpected(logger, ex);
			res = new ServiceResponse<>(ServiceResponse.FailureCode.Unknown, null, refId);
		}
		return res;
	}

	/**
	 * Get comments for a given blog
	 * 
	 * @param blogId
	 * @return
	 */
	@Transactional(readOnly = true)
	public ServiceResponse<List<BlogComment>> getComments(long blogId) {

		ServiceResponse<List<BlogComment>> res = null;

		try {
			List<BlogComment> comments = this.blogCommentRepo.findAllBlogId(blogId);

			res = new ServiceResponse<>(true, comments);
		} catch (Exception ex) {
			String refId = LogHelper.unexpected(logger, ex);
			res = new ServiceResponse<>(ServiceResponse.FailureCode.Unknown, null, refId);
		}
		return res;
	}

	/**
	 * Get blogs
	 * 
	 * @return
	 */
	@Transactional(readOnly = true)
	public ServiceResponse<List<Blog>> getBlogs() {
		// TODO: implmement proper paging
		PageRequest paging = PageRequest.of(0, 20);

		ServiceResponse<List<Blog>> res = null;

		try {

			Page<Blog> page = this.blogRepo.findAll(paging);

			List<Blog> blogs = page.getContent();

			res = new ServiceResponse<>(true, blogs);
		} catch (Exception ex) {
			String refId = LogHelper.unexpected(logger, ex);
			res = new ServiceResponse<>(ServiceResponse.FailureCode.Unknown, null, refId);
		}
		return res;
	}

	/**
	 * Get blog spaces. If user info is not given, then all spaces else user's
	 * specific spaces only
	 * 
	 * @param userData
	 * @return
	 */
	@Transactional(readOnly = true)
	public ServiceResponse<List<BlogSpace>> getBlogspaces(UserData userData) {
		PageRequest paging = PageRequest.of(0, 100);

		ServiceResponse<List<BlogSpace>> res = null;

		try {
			List<BlogSpace> items = null;
			if (userData != null && userData.getId() != null) {
				items = this.spaceRepo.findAllByUser(userData.getId());
			} else {
				Page<BlogSpace> page = this.spaceRepo.findAll(paging);
				items = page.getContent();
			}
			res = new ServiceResponse<>(true, items);
		} catch (Exception ex) {
			String refId = LogHelper.unexpected(logger, ex);
			res = new ServiceResponse<>(ServiceResponse.FailureCode.Unknown, null, refId);
		}
		return res;
	}

	/**
	 * User WSO to User Entity for hiding some internal info from clients
	 * 
	 * @param userData
	 * @return
	 */
	private User fromUserData(UserData userData) {
		User user = new User();
		user.setId(userData.getId());
		user.setName(userData.getUsername());
		user.setFullName(userData.getFullName());

		return user;
	}
}
