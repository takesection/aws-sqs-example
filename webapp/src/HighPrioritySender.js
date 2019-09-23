import React, { Component } from 'react'

class HighPrioritySender extends Component {
    constructor(props) {
        super(props);
        this.text = "";
        this.handleSubmit = this.handleSubmit.bind(this);
    }

    render() {
        return (
          <div className="HighPrioritySender">
              <form onSubmit={this.handleSubmit}>
                <label>Normal Priority</label>
                <p/>
                <textarea value={this.text}>
                </textarea>
                <p/>
                <input type="submit"/>
              </form>
          </div>
        );
    }

    handleSubmit(e) {
        e.preventDefault();
        var idtoken = localStorage.getItem("id_token");
        var headers = new Headers( { "Content-type" : "application/json", 'Authorization': idtoken } );
        var url = process.env.REACT_APP_APIGATEWAY_ENDPOINT + "/high-priority";
        return fetch(url, {
                method : 'POST',
                body : JSON.stringify({text: this.text}),
                headers: headers
            })
                .then((response) => {
                return response.json();
            });
    }
}

export default HighPrioritySender;