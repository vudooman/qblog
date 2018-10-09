import React, { Component } from 'react';
import './App.css';
import Home from './Home';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import AppNavbar from './components/AppNavbar';
import LoginContext from './components/LoginContext';
import BlogList from './BlogList';

class App extends Component {
  constructor () {
    super();
    let authToken = null, user = null;
  
    // check if the session info is still there session storage
    let loginInfo = window.sessionStorage.getItem('loginInfo');
    if(loginInfo) {
      loginInfo = JSON.parse(loginInfo);
      authToken = loginInfo.authToken;
      user = loginInfo.user;
    }
    this.state = {
      loginInfo: {
        authToken: authToken,
        user: user
      }
    }
    this.handleLogin = this.handleLogin.bind(this);
    this.handleLogout = this.handleLogout.bind(this);
    this.handleApiCall = this.handleApiCall.bind(this);
  }
  
  handleLogout() {
  	window.sessionStorage.removeItem('loginInfo');
 	this.setState({
      loginInfo: {
    	authToken: null,
    	user: null
      }
    });
  }
  
  handleLogin(username, password) {
      const self = this;
  	  return new Promise((resolve, reject) => {
  	    fetch('api/login', {
  	      method: 'POST',
  	      headers: {
            Accept: 'application/json',
            'Content-Type': 'application/json'
          },
  	      body: JSON.stringify({
            username: username.toLowerCase(),
            password: password
          })
  	    })
      	  .then(response => {
      	    let json = null;
      	    if(response.status === 200) {
      	  	  json = response.json();
      	    }
      	    return json;
      	  })
      	  .then(response => {
      	 	if(response && response.authToken && response.authToken.token) {
      	 	  window.sessionStorage.setItem('loginInfo', JSON.stringify({
                authToken: response.authToken.token,
                user: response.user
              }));
      	 	  self.setState({
      	 	    loginInfo: {
      	 	    	authToken: response.authToken.token,
      	 	    	user: response.user
      	 	    }
      	 	  });
      	 	  resolve();
      	 	} else {
      	 	  reject("Invalid username/password");
      	 	}
      	  });
  	  });
  }
  
  /**
   * Wrapper for fetch to handle non-success status as well as to update auth token
   */
  handleApiCall(api, conf) {
  	conf = conf || {};
  	if(!conf.headers) {
  	  conf.headers = {};
  	}
  	if(this.state.loginInfo.authToken) {
  	  conf.headers.Authorization = 'Bearer ' + this.state.loginInfo.authToken;
  	}
  	conf.headers.Accept = 'application/json';
  	conf.headers['Content-Type'] = 'application/json';
  	
    return new Promise((resolve, reject) => {
      let handled = false;
      fetch(api, conf)
  	    .then(response => {
  	      if(response.status === 200) {
  	  	    // Check if auth token has been updated
  	  	    const authHeader = response.headers.get("authorization");
  	  	    if(authHeader) {
  	  	      const authHeaderParts = authHeader.split(' ');
  	  	      if(authHeaderParts.length > 1) {
  	  	        this.setState({
  	  	          loginInfo: {
  	  	            authToken: authHeaderParts[1]
  	  	          }
  	  	        });
  	        	window.sessionStorage.setItem('loginInfo', JSON.stringify(this.state.loginInfo));
  	  	      }
  	  	    }
  	      } else if(response.status === 401) {
  	        // Auth token is no longer valid, remove and reload app
  	        window.sessionStorage.removeItem('loginInfo');
  	        alert("Your session has timed out.  Please log in again to redo your last task.");
  	        window.location.reload();
  	        handled = true;
  	      } else if(response.status === 500) {
  	      	alert("Unexpected error has happened.  Please try again at another time.");
  	      	handled = true;
  	      }
  	      return response;
  	    })
  	    .then(response => {
  	      if(response.status === 200 || response.status === 204) {
  	 		resolve(response);
  	      } else {
  	    	reject({
  	    	  handled,
  	    	  response
  	    	});
  	      }
  	    });
    });
  }
  
  render() {
    return (
      <Router>
       	<LoginContext.Provider value={this.state.loginInfo}>
        	<AppNavbar loginHandler={this.handleLogin} 
        	  	logoutHandler={this.handleLogout} apiHandler={this.handleApiCall} />
	        <Switch>
	          <Route path='/' exact={true} component={() => <Home apiHandler={this.handleApiCall} />}/>
	          <Route path='/blogs' exact={true} component={() => <BlogList apiHandler={this.handleApiCall} />}/>
	        </Switch>
        </LoginContext.Provider>
      </Router>
    )
  }
}

export default App;