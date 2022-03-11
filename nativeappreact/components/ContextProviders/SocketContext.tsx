import { View, Text } from 'react-native'
import React, { createContext, useContext, useEffect, useRef, useState } from 'react'


const Socketcontext = createContext({
    message: "",
    sendMessage: async () => {}
})



const SocketContext = ({children}) => { 
    const [message, setMessage] = useState("");

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
            console.log("connection opened")
            const event = {
                type: "init test",
                message: "connection opened from the client"

            }
            ws.send(JSON.stringify(event))
            setMessage("server sent a message")
        } 
        ws.onmessage = (e) => {
            console.log("onMessage:", e.data)
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