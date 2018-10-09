import React, { Component } from 'react';
import { Button, ListGroup, ListGroupItem, Collapse, Input } from 'reactstrap';
import LoginContext from './components/LoginContext';

class BlogList extends Component {

  constructor(props) {
    super(props);
    this.state = {
    	blogs: [], 
    	isLoading: true, 
    	commentBlogId: null, 
    	collapse: false, 
    	comments: null,
    	newComment: ''
    };
    this.onViewComments = this.onViewComments.bind(this);
    this.onAddComment = this.onAddComment.bind(this);
  }
  
  onAddComment(blogId, loginInfo) {
  	if(loginInfo.authToken && this.state.newComment) {
  	  this.props.apiHandler(`api/blogs/${blogId}/comments`, {
        method: 'POST',
        body: JSON.stringify({
          comment: this.state.newComment
        })
  	  })
  	    .then(async resp => {
  	      const comment = await resp.json();
  	  	  let comments = this.state.comments;
  	  	  if(!comments) {
  	  	    comments = [];
  	  	  }
  	  	  comments.unshift(comment);
  	  	  this.setState({
  	  	    comments,
  	  	    newComment: ''
  	  	  });
  	    }, async err =>  {
          this.setState({
            modalError: 'Could not add comment for unknown reason.  Try again later.'
          });
  	    });
  	}
  }
  
  onViewComments(blogId) {

  	let collapse = !this.state.collapse;
  	if(blogId !== this.state.commentBlogId) {
  		collapse = false;
  
	    this.props.apiHandler('api/blogs/' + blogId+ '/comments', {
	      method: 'GET',
	    })
	    .then(async response => {
	
	        const comments = await response.json();
	        
		  	this.setState({
		  	  commentBlogId: blogId,
		  	  collapse: collapse,
		  	  comments: comments
		  	})
	    }, async err =>  {
          this.setState({
            modalError: 'Could not load comments for unknown reason.  Try again later.'
          });
	    });
  	} else {
	  	this.setState({
	  	  commentBlogId: blogId,
	  	  collapse: collapse
	  	})
  	}
  }
  
  componentDidMount() {
    this.setState({isLoading: true});
    
    this.props.apiHandler('api/blogs', {
      method: 'GET'
    })
      .then(async res => {
      	const data = await res.json();
      	this.setState({blogs: data, isLoading: false});
      });
  }

  render() {

    const {blogs, isLoading, comments} = this.state;

    if (isLoading) {
      return <p>Loading...</p>;
    }
    
    const blogList = (loginInfo) => {
      return blogs.map(blog => {
      
	      let commentUI = null, addCommentUI = null, addBtn = null;
	      if(this.state.commentBlogId === blog.id) {
	        let CommentList = comments == null || comments.length === 0? <ListGroupItem>No comments are available</ListGroupItem> : comments.map(comment => {
	          return (
			    <ListGroupItem key={comment.id}>
		          <div>{comment.comment} <i>by {comment.commenter.name}</i></div>
		        </ListGroupItem>
	          );
	        });
	        
	        if(!this.state.collapse && loginInfo.authToken) {
	          if(this.state.newComment) {
	          	addBtn = <Button color="link" onClick={e => {this.onAddComment(blog.id, loginInfo)}}>Add Comment</Button>
	          } else {
	            addBtn = <Button color="link" disabled>Add Comment</Button>
	          }
	          addCommentUI = (
	            <ListGroupItem>
	              <Input type="textarea" name="newComment" id="newComment" value={this.state.newComment}
              		onChange={e => this.setState({newComment:e.target.value})} />
	              {addBtn}
	            </ListGroupItem>
	          )
	        }
	        
		    commentUI = (
	          <Collapse isOpen={!this.state.collapse}>  
		        <ListGroup>
	        	  {addCommentUI}
		          {CommentList}
		        </ListGroup>
	          </Collapse>
	        );
	      }
	      
	      return <ListGroupItem key={blog.id}>
	        <div><span  style={{fontWeight: 'bold'}}>{blog.title}</span> (Space: {blog.space.title}, Author: {blog.author.name})</div>
	        <div>{blog.description}</div>
	        <Button color="link" onClick={e => {this.onViewComments(blog.id)}}>Comments</Button>
	        {commentUI}
	      </ListGroupItem>
	  });
    }

    return (
      <LoginContext.Consumer>
        {loginInfo => {
          return (
	          <ListGroup>
	            {blogList(loginInfo)}
	          </ListGroup>
          );
        }}
      </LoginContext.Consumer>
    );
  }
}

export default BlogList;