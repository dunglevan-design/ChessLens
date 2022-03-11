import { View, Text } from 'react-native'
import React, { createContext, useContext, useEffect, useRef, useState } from 'react'
import { useAuth } from './AuthContext';


const Socketcontext = createContext({
    message: "",
    sendMessage: async () => {}
})



const SocketContext = ({children}) => { 
    const [message, setMessage] = useState("");
    const {user} = useAuth();

    // ref so it remains between render
    var ws = useRef(new WebSocket("ws://localhost:8001")).current

    const sendMessage = async() => {
        const event = {
            type: "client test",
            message: "test message from the client"
        }
        ws.send(JSON.stringify(event))
    }

    useEffect(() => {
        ws.onopen = () => {
           const event ={
               type: "init",
               message: "init connection, user signed in",
               data: {
                   id: user.id,
                   token: user.accessToken
               }
           } 
           ws.send(JSON.stringify(event))
        } 
        ws.onmessage = (e) => {
            console.log("onMessage:", e.data)
            // TODO: do different thing depends on message type. i.e: set different state to populate the tree
        }
        
        ws.onerror = (ev) => {
            console.log(ev)
        }
        
        ws.onclose = (e) => {
            console.log(e.code, e.reason)
        }
        
    }, [])
    
    

  return (
    <Socketcontext.Provider value = {{message: message, sendMessage: sendMessage}}>
        {children}
    </Socketcontext.Provider>
  )
}


export const useSocket = () => useContext(Socketcontext)

export default SocketContext