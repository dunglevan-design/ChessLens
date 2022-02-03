import React, { useContext, useState } from 'react';


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

export const useAuth = () => useContext(Authcontext);

export default AuthContext;
