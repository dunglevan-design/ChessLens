import {
  View,
  Text,
  TouchableOpacity,
  findNodeHandle,
  UIManager,
} from "react-native";
import React, { useEffect, useRef, useState } from "react";
import { JavaCameraModule, JavaCameraView } from "../native/OpenCVCamera";
import { useAuth } from "../components/ContextProviders/AuthContext";
import { useSocket } from "../components/ContextProviders/SocketContext";
import { config } from "../components/utils/types";

const STAGE = {
  CHECK_CORNERS: 1,
  WAITING_FOR_OPPONENT_JOIN: 2,
  WAITING_FOR_MOVE: 4,
};

const GameScreen = ({ route, navigation }) => {
  const { user } = useAuth();
  const { message, sendMessage } = useSocket();
  const { config }: { config: config } = route.params;
  const [stage, setStage] = useState();
  const [viewId, setViewId] = useState();
  const ref = useRef(null);

  useEffect(() => {
    const viewId = findNodeHandle(ref.current);
    console.log(viewId);
  }, []);

  const CheckCorners = () => {
    console.log("checking corners")
    JavaCameraModule.PassCommandtoViewManager("CheckCorners")
  
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
        <Text style={{ fontSize: 20 }}>
          Place the camera still at the side of the board, check if the board
          corners are drawn correctly
        </Text>
        <View style={{ flexDirection: "row", justifyContent: "space-around" }}>
          <TouchableOpacity>
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
          <TouchableOpacity>
            <Text
              style={{
                fontSize: 20,
                fontWeight: "600",
                color: "rgb(50,150,255)",
              }}
              onPress={() => CheckCorners()}
            >
              Next
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    </View>
  );
};

export default GameScreen;
