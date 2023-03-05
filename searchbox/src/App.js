import React, { Component } from 'react';
import './App.css';

class App extends Component {
  state = {searhterm: "" }
 
  handleChange= (event) => {
    this.setState({searhterm: event.target.value});
    
  }
  postUsers =(e) => {
    e.preventDefault(); 
    let term=this.state.searhterm
    fetch(`http://localhost:8080/search/stream?term=${term}`)
    .then(response => response.json())
    .then(data =>console.log("response data:",data) );     
    this.setState({searhterm:""})
  }
  render() {
    return (
      <div className="v-container">        
        <div className="search">
          <input type="text"
            id="name"
            name="searhterm"
            value={this.state.searhterm}
            onChange={this.handleChange}
            className="searchTerm"
            placeholder="Search" />
          <button onClick={this.postUsers} className="searchButton">
            <i className="fa fa-search"></i>
          </button>
        </div>
      </div>
    );
  }
}

export default App;
