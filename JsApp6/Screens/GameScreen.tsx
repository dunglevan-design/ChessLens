import { View, Text, TouchableOpacity } from "react-native";
import React, { useEffect, useRef, useState } from "react";
import {
  JavaCameraControlModule,
  JavaCameraView,
} from "../native/OpenCVCamera";
import { useAuth } from "../components/ContextProviders/AuthContext";
import { useSocket } from "../components/ContextProviders/SocketContext";
import { config } from "../components/utils/types";

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
  const { config }: { config: config } = route.params;
  const [stage, setStage] = useState(0);
  let setupMessage = "";
  const ref = useRef(null);

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
          SaveInitialFrame();
          setStage(STAGE.WAITING_FOR_MY_MOVE);
          break;
        case STAGE.WAITING_FOR_MY_MOVE:
          PredictMyMove();
          break;
        case STAGE.WAITING_FOR_OPPONENT_MOVE:
          PredictOpponentMove();
          break;
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
        setStage(STAGE.GAME_STARTED);
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

  const SaveInitialFrame = () => {
    JavaCameraControlModule.PassCommand("SaveInitialFrame");
  };

  const PredictMyMove = () => {
    JavaCameraControlModule.PassCommand("PredictMyMove");
  };

  const PredictOpponentMove = () => {
    JavaCameraControlModule.PassCommand("PredictOpponentMove");
  };

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
      </View>
    </View>
  );
};

export default GameScreen;
