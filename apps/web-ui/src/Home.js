import React, { Component } from 'react';
import './App.css';
import { Link } from 'react-router-dom';
import { Button, Container } from 'reactstrap';

class Home extends Component {
  render() {
    return (
      <div>
        <Container fluid>
          <div style={{paddingLeft: 15, marginTop: 30}}>{'// TODO: Have some cool home page content'}</div>
          <Button color="link"><Link to="/blogs">View Blogs</Link></Button>
        </Container>
      </div>
    );
  }
}

export default Home;