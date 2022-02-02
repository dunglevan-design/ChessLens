import { View, Text } from 'react-native';
import React, { useEffect, useState } from 'react';
import useAuth from '../../hooks/useAuth';
var crypto = require("crypto");
var randomstring = require("randomstring");


const Authcontext = React.createContext({
    user: null,
    LoginWithLichess: () => {},
    Logout: () => {},
});
  
const AuthContext:React.FC = ({children}) => {
    // useAuth here

    const [user, setUser] = useState({
        name: "",
        id: "",
    })

    const LoginWithLichess =  () => {
        //trigger webbrowser login PKCE flow
        console.log("sign in with Lichess")
    }

    const Logout = () => {
        //invalidate the token
        console.log("log out");
    }

    return (
        <Authcontext.Provider value = {{user: null, LoginWithLichess: LoginWithLichess, Logout : Logout}}>
            {children}
        </Authcontext.Provider>
    );
};

export default AuthContext;
