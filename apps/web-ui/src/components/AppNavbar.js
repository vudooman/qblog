import React, { Component } from 'react';
import { 
  Collapse, Nav, Navbar, NavbarBrand, NavbarToggler, NavItem, NavLink,
  Button, Modal, ModalHeader, ModalBody, ModalFooter , Form, FormGroup,
  Label, Input
} from 'reactstrap';
import { Link } from 'react-router-dom';
import LoginContext from './LoginContext';
import { withRouter } from 'react-router-dom';

class AppNavBar extends Component {
  constructor(props) {
    super(props);
    this.state = {
      isOpen: false, 
      showLogin: false, 
      showSignup: false, 
      showCreateSpace: false,
      showCreateBlog: false,
      username: '',
      password: '',
      passwordConfirm: '',
      blogSpace: '',
      blogTitle: '',
      blogDescription: '',
      blogSpaceId: null,
      myspaces: null,
      modalError: null,
      modalTitle: ''
    };
    this.toggle = this.toggle.bind(this);
    this.logIn = this.logIn.bind(this);
    this.toggleLogin = this.toggleLogin.bind(this);
    this.toggleSignup = this.toggleSignup.bind(this);
    this.toggleCreateSpace = this.toggleCreateSpace.bind(this);
    this.toggleCreateBlog = this.toggleCreateBlog.bind(this);
    this.doLogin = this.doLogin.bind(this);
    this.doAddSpace = this.doAddSpace.bind(this);
    this.doAddBlog = this.doAddBlog.bind(this);
    this.doSignup = this.doSignup.bind(this);
  }
  
  doAddBlog() {
  	if(this.state.blogSpaceId && this.state.blogTitle && this.state.blogDescription) {
  	  this.props.apiHandler('api/blogspaces/' + this.state.blogSpaceId + '/blogs', {
        method: 'POST',
        body: JSON.stringify({
          title: this.state.blogTitle,
          description: this.state.blogDescription
        })
  	  })
  	    .then(() => {
  	  	  window.location.reload(); 
  	    }, err =>  {
          this.setState({
            modalError: 'Could not create blog space for unknown reason.  Try again later.'
          });
  	    });
  	} else {
  	  this.setState({
  	    modalError: 'Blog Space, Title, and Description are required.'
  	  });
  	}
  }
  
  doAddSpace() {
    if(this.state.blogSpace) {
  	  this.props.apiHandler('api/blogspaces', {
        method: 'POST',
        body: JSON.stringify({
          title: this.state.blogSpace
        })
  	  })
  	    .then(() => {
  	  	  this.toggleCreateSpace();
  	    }, async err =>  {
  	        if(err.response.status === 400) {
  	        const json = await err.response.json();
  	        let modalError = null;
  	        if(json.failureMessage === 'BLOG_SPACE_TITLE_NOT_UNIQUE') {
  	          modalError = 'Blog space name is already in use.';
  	        } else {
  	          modalError = 'Could not create blog space for unknown reason.  Try again later.';
  	        }
  	        this.setState({
  	          modalError
  	        });
  	      }
  	    });
  	} else {
  	  this.setState({
  	    modalError: 'Blog space name is required.'
  	  });
  	}
  }
	
  doSignup() {
    if(this.state.username && this.state.password && this.state.passwordConfirm) {
      if(this.state.password === this.state.passwordConfirm) {
        this.props.apiHandler('api/account', {
          method: 'POST',
		  body: JSON.stringify({
		    username: this.state.username,
		    password: this.state.password
	      })
        })
		  .then(() => {
			this.toggleSignup();
			this.doLogin(null, true);
		  }, async err =>  {
		    let modalError = null;
  	        if(err.response.status === 400) {
  	          modalError = 'Username is already in use.';
  	        } else {
  	          modalError = 'Could not create account for unknown reason.  Try again later.1';
  	        }
  	        this.setState({
  	          modalError
  	        });
		  });
		} else {
		  this.setState({
		    modalError: 'Password and Confirm Password do not match.'
	      });
		}
	} else {
      this.setState({
        modalError: 'Username, Password, and Confirm Password are required.'
      });
	}
  }

  doLogin(e, noToggle) {
  	if(this.state.username && this.state.password) {
  	  this.props.loginHandler(this.state.username, this.state.password)
  	    .then(() => {
  	      if(noToggle !== true) {
  	  	  	this.toggleLogin();
  	  	  }
  	    }, err => {
  	  	  this.setState({
  	  	    modalError: err
  	  	  });
  	    });
  	} else {
  	  this.setState({
  	    modalError: 'Username and password are required.'
  	  });
  	}
  }
  
  toggle() {
    this.setState({
      isOpen: !this.state.isOpen
    });
  }

  toggleLogin() {
    this.setState({
      showLogin: !this.state.showLogin,
      modalError: null
    });
  }

  toggleSignup() {
    this.setState({
      showSignup: !this.state.showSignup,
      modalError: null
    });
  }

  toggleCreateSpace() {
    this.setState({
      showCreateSpace: !this.state.showCreateSpace,
      modalError: null
    });
  }

  toggleCreateBlog() {
    this.props.apiHandler('api/myspaces', {
      method: 'GET'
    })
      .then(async response => {
        const spaces = await response.json();
	    this.setState({
	      showCreateBlog: !this.state.showCreateBlog,
	      modalError: null,
	      myspaces: spaces
	    });
      }, async err =>  {
	    this.setState({
		  modalError: 'Could not create blog for unknown reason.  Try again later.'
	    });
      });
  }
  
  logIn(loginInfo) {
	if(loginInfo && loginInfo.authToken) {
	  this.props.logoutHandler();
	} else {
      this.setState({
        showLogin: true
      });
    }
  }
    
  render() {
    const self = this;
    
    return (
      <LoginContext.Consumer>
        {loginInfo => {
          let signUp = null, createBlog = null, createSpace = null, primaryText = null;
          if(loginInfo.authToken) {
          	createBlog = <NavItem><NavLink href="#" onClick={this.toggleCreateBlog}>New Blog</NavLink></NavItem>;
          	createSpace = <NavItem><NavLink href="#" onClick={this.toggleCreateSpace}>New Blog Space</NavLink></NavItem>;
          } else {
          	signUp = <NavItem><NavLink href="#" onClick={this.toggleSignup}>Sign Up</NavLink></NavItem>;
          }
          
          let modalOpen = false, toggleFunc = null, primaryFunc = null, form = null;
          if(this.state.showLogin) {
          	primaryText = 'Login';
          	modalOpen = true;
          	toggleFunc = this.toggleLogin;
          	primaryFunc = this.doLogin;
          	form = (
		      <Form>
		        <FormGroup>
		          <Label for="username">Username</Label>
		          <Input type="text" name="username" id="username" 
              		onChange={e => this.setState({username:e.target.value})} />
		        </FormGroup>
		        <FormGroup>
		          <Label for="password">Password</Label>
		          <Input type="password" name="password" id="password"
              		onChange={e => this.setState({password:e.target.value})}  />
		        </FormGroup>
		      </Form>
          	)
          } else if(this.state.showSignup) {
            primaryText = 'Sign Up';
            modalOpen = true;
          	toggleFunc = this.toggleSignup;
          	primaryFunc = this.doSignup;
          	form = (
		      <Form>
		        <FormGroup>
		          <Label for="username">Username</Label>
		          <Input type="text" name="username" id="username" 
              		onChange={e => this.setState({username:e.target.value})} />
		        </FormGroup>
		        <FormGroup>
		          <Label for="password">Password</Label>
		          <Input type="password" name="password" id="password"
              		onChange={e => this.setState({password:e.target.value})}  />
		        </FormGroup>
		        <FormGroup>
		          <Label for="password">Confirm Password</Label>
		          <Input type="password" name="passwordConfirm" id="passwordConfirm"
              		onChange={e => this.setState({passwordConfirm:e.target.value})}  />
		        </FormGroup>
		      </Form>
          	)
          } else if(this.state.showCreateSpace) {
          	primaryText = 'Add Space';
            modalOpen = true;
          	toggleFunc = this.toggleCreateSpace;
          	primaryFunc = this.doAddSpace;
          	form = (
		      <Form>
		        <FormGroup>
		          <Label for="blogSpace">Name</Label>
		          <Input type="text" name="blogSpace" id="blogSpace" 
              		onChange={e => this.setState({blogSpace: e.target.value})} />
		        </FormGroup>
		      </Form>
          	);
          } else if(this.state.showCreateBlog) {
          	primaryText = 'Add Blog';
            modalOpen = true;
          	toggleFunc = this.toggleCreateBlog;
          	primaryFunc = this.doAddBlog;
          	const MyspacesOptions = this.state.myspaces.map(space => {
	          return <option value={space.id} key={space.id}>{space.title}</option>
	        });
          	form = (
		      <Form>
		        <FormGroup>
		          <Label for="blogSpaceSelect">Blog Space</Label>
		          <Input type="select" name="blogSpaceSelect" id="blogSpaceSelect"
              		onChange={e => this.setState({blogSpaceId: e.target.value})}>
              		<option></option>
		            {MyspacesOptions}
		          </Input>
		        </FormGroup>
		        <FormGroup>
		          <Label for="blogTitle">Title</Label>
		          <Input type="text" name="blogTitle" id="blogTitle" 
              		onChange={e => this.setState({blogTitle: e.target.value})} />
		        </FormGroup>
		        <FormGroup>
		          <Label for="blogDescription">Description</Label>
		          <Input type="textarea" name="blogDescription" id="blogDescription" 
              		onChange={e => this.setState({blogDescription: e.target.value})} />
		        </FormGroup>
		      </Form>
          	);
          }
          
        	 
          return (
          	<div>
            <Navbar color="dark" dark expand="md">
            <NavbarBrand tag={Link} to="/">QCeda Blog</NavbarBrand>
            <NavbarToggler onClick={this.toggle}/>
            <Collapse isOpen={this.state.isOpen} navbar>
              <Nav className="ml-auto" navbar>
                {signUp}
                {createBlog}
                {createSpace}
                <NavItem>
                  <NavLink href="#" onClick={() => self.logIn(loginInfo)}>{loginInfo.authToken? 'Logout' : 'Login'}</NavLink>
                </NavItem>
              </Nav>
            </Collapse>
            </Navbar>
            
	        <Modal isOpen={modalOpen} toggle={this.toggleLogin} className={this.props.className} backdrop='static'>
	          <ModalHeader toggle={this.toggleLogin}>{primaryText}</ModalHeader>
	          <ModalBody>
			      {form}
			      <div style={{color: 'red'}}>{this.state.modalError}</div>
			  </ModalBody>
	          <ModalFooter>
	            <Button color="primary" onClick={primaryFunc}>{primaryText}</Button>{' '}
	            <Button color="secondary" onClick={toggleFunc}>Cancel</Button>
	          </ModalFooter>
	        </Modal>
        
            </div>
          )
        }}
      </LoginContext.Consumer>
    );
  }
}

AppNavBar = withRouter(AppNavBar);

export default AppNavBar;