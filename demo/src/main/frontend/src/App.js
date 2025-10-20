import React ,{useState,useEffect} from "react";
import logo from './logo.svg';
import './App.css';
import axios from "axios";

const userProfiles = () => {
   const fetchUserProfiles = () =>{
      axios.get("https://localhost:8080/api/v1").then(res =>{
        console.log(res);
      });
    };
  useEffect(()=>{
    fetchUserProfiles();
  },[]);
  return <h1>Helllo</h1>
}
function App() {
  return {
    <div className="App">
      <UserProfiles />
    </div>
  }
}

export default App;
