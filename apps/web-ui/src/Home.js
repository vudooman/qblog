import React, { Component } from 'react';
import './App.css';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';

class Home extends Component {
  render() {
    return (
      <div>
        <Container fluid>
          <div style={{paddingLeft: 15, marginTop: 30}}>
          	<p>
          		Just click around and view various blogs and comments.  
          	</p>
          	<p>
          		In order to write your own blog or comment, you must be a registered user.
          	</p>
          	<p>
          		Go ahead registration is free.
          	</p>
          	<p>
          		{'// TODO: Have some cool home page content'}
          	</p>
          </div>
          <Button color="link"><Link to="/blogs">View Blogs</Link></Button>
        </Container>
      </div>
    );
  }
}

export default Home;