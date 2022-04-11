import { View, Text } from "react-native";
import React, {
  createContext,
  useContext,
  useEffect,
  useRef,
  useState,
} from "react";
import { useAuth } from "./AuthContext";
import { action, config, msgfromSocketHOC } from "../utils/types";

type context = {
    message: msgfromSocketHOC,
    sendMessage: (message:action) => Promise<void>
}

const Socketcontext = createContext<context>({
  message: null,
  sendMessage: async (message) => {},
});

const SocketContext = ({ children }) => {
  const [message, setMessage] = useState<msgfromSocketHOC>(null);
  const { user } = useAuth();

  // ref so it remains between render
  var ws = useRef(new WebSocket("ws://localhost:8001")).current;

  const sendMessage = async (action) => {
    ws.send(JSON.stringify(action));
  };

  useEffect(() => {
    if (user) {
      const action = {
        type: "signin",
        data: {
          token: user.accessToken,
        },
      };
      sendMessage(action);
    }
  }, [user]);

  // List of consumer funtions on message

  const startGame = (config) => {
    setMessage({
      action: "StartGame",
      data: config,
    });
    
  };

  // End list of consumer functions on message

  useEffect(() => {
    ws.onopen = () => {
      console.log("connection")
      const action = {
        type: "init",
        message: "init connection, user not signed in",
      };
      ws.send(JSON.stringify(action));
    };

    ws.onmessage = ({ data }) => {
      const action = JSON.parse(data);
      console.log("onMessage:", action);
      // TODO: do different thing depends on message type. i.e: set different state to populate the tree
      switch (action.type) {
        case "gameStart":
          const config = action.data;
          startGame(config);
          break;

        case "move": {
          const move = action.data.move
          console.log("received move ooponet", move)
          setMessage({
            action: "opponentMove",
            data: move
          })
          break;
        }

        default:
      }
    };

    ws.onerror = (ev) => {
      console.log(ev);
    };

    ws.onclose = (e) => {
      console.log(e.code, e.reason);
    };
  }, []);

  return (
    <Socketcontext.Provider
      value={{ message: message, sendMessage: sendMessage }}
    >
      {children}
    </Socketcontext.Provider>
  );
};

export const useSocket = () => useContext(Socketcontext);

export default SocketContext;
