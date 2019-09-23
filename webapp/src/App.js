import React from 'react';
import logo from './logo.svg';
import './App.css';
import NormalPrioritySender from "./NormalPrioritySender";
import HighPrioritySender   from "./HighPrioritySender";

function proc_load() {
  var hashs = {};
  hashs = parse_url_vars(window.location.hash);
  return hashs.id_token;
}

function parse_url_vars(param){
  if( param.length < 1 )
    return {};

  var hash = param;
  if( hash.slice(0, 1) === '#' || hash.slice(0, 1) === '?' )
    hash = hash.slice(1);
  var hashs  = hash.split('&');
  var vars = {};
  for( var i = 0 ; i < hashs.length ; i++ ){
    var array = hashs[i].split('=');
    vars[array[0]] = array[1];
  }

  return vars;
}

function App() {
  var loginUrl = "https://"
      + process.env.REACT_APP_COGNITO_ENDPOINT
      + "/login"
      + "?response_type=token"
      + "&client_id=" + process.env.REACT_APP_COGNITO_CLIENT_ID
      + "&scope=openid%20email"
      + "&redirect_uri=" + process.env.REACT_APP_COGNITO_REDIRECT_URI;
  var idToken = proc_load();
  localStorage.setItem("id_token", idToken);
  return (
    <div className="App">
        <header>
          <a href={loginUrl}>SignIn</a>
          <p/>
          <NormalPrioritySender/>
          <HighPrioritySender/>
        </header>
    </div>
  );
}

export default App;
