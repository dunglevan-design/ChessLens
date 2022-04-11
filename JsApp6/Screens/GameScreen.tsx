import { View, Text, TouchableOpacity, NativeEventEmitter } from "react-native";
import React, { useEffect, useRef, useState } from "react";
import {
  JavaCameraControlModule,
  JavaCameraView,
} from "../native/OpenCVCamera";
import { useAuth } from "../components/ContextProviders/AuthContext";
import { useSocket } from "../components/ContextProviders/SocketContext";
import { config } from "../components/utils/types";
import { Chess } from "chess.js";

import Tts from 'react-native-tts';

Tts.setDefaultLanguage('en-GB')

const STAGE = {
  CHECK_CORNERS: 0,
  PUTTING_PIECES: 1,
  WAITING_FOR_OPPONENT_JOIN: 2,
  GAME_STARTED: 3,
  WAITING_FOR_MY_MOVE: 4,
  WAITING_FOR_OPPONENT_MOVE: 5,
};

const GameScreen = ({ route, navigation }) => {
  const { user } = useAuth();
  const { message, sendMessage } = useSocket();
  const { config } = route.params;
  const [stage, setStage] = useState(0);
  const [movehistory, setMoveHistory] = useState([]);
  let setupMessage = "";
  const ref = useRef(null);
  const chessRef = useRef(new Chess());
  const gameId = useRef(0);

  let eventListener;

  useEffect(() => {
    const eventEmitter = new NativeEventEmitter(JavaCameraControlModule);
    eventListener = eventEmitter.addListener("MovePredictionFound", (e) =>
      MovePredictionFoundHandler(e)
    );

    return () => eventListener.remove();
  }, []);

  const MovePredictionFoundHandler = (e) => {
    const chess = chessRef.current;
    const data = e.moves;
    const movearr = JSON.parse(data);
    const bestmove = movearr[0].move;
    const bestprob = movearr[0].prob;
    console.log("move: ", bestmove, ", prob: ", bestprob);
    /**
     * Try play all the moves on Chessjs
     * If all move fail then set ProcessingMode to PredictMymove or Wait hand enter screen again.
     */
    let i = 0;
    let movePlayed = false;
    while (i < movearr.length && !movePlayed) {
      const movestr = movearr[i].move;
      const from = movestr.substr(0, 2);
      const to = movestr.substr(4, 5);

      console.log("from :", from);
      console.log("to : ", to);
      const moveobj = chess.move({ from: from, to: to });
      // If move sucess play on lichess.
      if (moveobj) {
        const action = {
          type: "move",
          data: {
            move: from.concat(to),
            game: gameId.current,
          },
        };
        sendMessage(action);
        movePlayed = true;
        setMoveHistory(chess.history({ verbose: false }));
      }
      console.log(chess.history({ verbose: false }));
      i = i + 1;
    }

    /**
     * If move success.
     * Play move on Lichess
     * SaveInitialFrame
     * On Opponentmove:
     * update chessjs state
     * processingMode = waithandenterScreen(opponent) -> waithandleavescreen(Opponent) -> saveCurrentFrameToInitialFrame.
     */
  };

  //Update UI every render base on stage
  switch (stage) {
    case STAGE.CHECK_CORNERS:
      setupMessage =
        "Place the camera still at the side of the board, check if the board corners are drawn correctly";
      break;
    case STAGE.PUTTING_PIECES:
      setupMessage =
        "Now place the pieces on the board, white on the right side, black on the left side";
      break;
    case STAGE.WAITING_FOR_OPPONENT_JOIN:
      setupMessage = "Waiting for opponent ...";
      break;
    case STAGE.GAME_STARTED:
      setupMessage =
        "Game started, initial frame has been saved, waiting for your move";

    case STAGE.WAITING_FOR_MY_MOVE:
      setupMessage =
        "Game started, initial frame has been saved, waiting for your move";
    default:
  }

  //Running JavaCameraModule commands based on stages
  useEffect(() => {
    (async function () {
      switch (stage) {
        case STAGE.CHECK_CORNERS:
          CheckCorners();
          break;
        case STAGE.PUTTING_PIECES:
          SaveCorners();
          break;
        case STAGE.WAITING_FOR_OPPONENT_JOIN:
          if (config.gametype === "invite") {
            const action = {
              type: "challengeDirectly",
              data: {
                username: config.username,
              },
            };
            console.log("challenge ", config.username);
            sendMessage(action);
          }
          break;
        case STAGE.GAME_STARTED:
          SaveInitialFrameAndPredictMyMove();
          break;
        // case STAGE.WAITING_FOR_MY_MOVE:
        //   PredictMyMove();
        //   break;
        // case STAGE.WAITING_FOR_OPPONENT_MOVE:
        //   PredictOpponentMove();
        //   break;
        default:
          console.log("nothnig");
      }
    })();
  }, [stage]);

  //Running commands based on events sent back from sockets
  useEffect(() => {
    switch (message?.action) {
      case "StartGame":
        console.log("StartGame");
        chessRef.current = new Chess();
        gameId.current = message.data.gameid;
        setStage(STAGE.GAME_STARTED);
        break;
      case "opponentMove":
        console.log("Playing opponent move");
        const moveobj = chessRef.current.move(message.data, {sloppy: true});
        if (moveobj) {
          console.log("update opponent move sucess");
          Tts.speak(moveobj.from + "to" + moveobj.to);
          setMoveHistory(chessRef.current.history({ verbose: false }));
          SaveInitialFrameAndPredictMyMove();
        }
        break;
      default:
    }
  }, [message]);

  const Back = () => {
    if (stage !== 0 && stage < 3) {
      setStage((stage) => stage - 1);
    }
  };

  const Next = () => {
    setStage((stage) => stage + 1);
  };

  const CheckCorners = () => {
    JavaCameraControlModule.PassCommand("CheckCorners");
  };

  const SaveCorners = () => {
    JavaCameraControlModule.PassCommand("SaveCorners");
  };

  const SaveInitialFrameAndPredictMyMove = () => {
    console.log("save initial frame getting called?");
    JavaCameraControlModule.PassCommand("SaveInitialFrameAndPredictMyMove");
  };

  // const PredictMyMove = () => {
  //   JavaCameraControlModule.PassCommand("PredictMyMove");
  // };

  // const PredictOpponentMove = () => {
  //   JavaCameraControlModule.PassCommand("PredictOpponentMove");
  // };

  return (
    <View style={{ flex: 1 }}>
      <View style={{ height: "50%" }}>
        <JavaCameraView
          ref={ref}
          //@ts-ignore
          style={{ width: "100%", height: "100%", backgroundColor: "blue" }}
        />
      </View>
      <View style={{ padding: 10 }}>
        <Text style={{ fontSize: 20 }}>{setupMessage}</Text>
        {stage < 2 && (
          <View
            style={{ flexDirection: "row", justifyContent: "space-around" }}
          >
            <TouchableOpacity onPress={Back}>
              <Text
                style={{
                  fontSize: 20,
                  fontWeight: "600",
                  color: "rgb(50,150,255)",
                }}
              >
                Back
              </Text>
            </TouchableOpacity>
            <TouchableOpacity onPress={Next}>
              <Text
                style={{
                  fontSize: 20,
                  fontWeight: "600",
                  color: "rgb(50,150,255)",
                }}
              >
                Next
              </Text>
            </TouchableOpacity>
          </View>
        )}
        <Text style = {{fontSize: 24, fontWeight: "700"}}>Moves</Text>
        <Text style = {{fontSize: 20}}>
          {movehistory}
        </Text>
      </View>
    </View>
  );
};

export default GameScreen;
