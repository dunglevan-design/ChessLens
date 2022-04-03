import { View, Text } from "react-native";
import React, { useState } from "react";
import { JavaCameraView } from "../native/OpenCVCamera";
import { useAuth } from "../components/ContextProviders/AuthContext";
import { useSocket } from "../components/ContextProviders/SocketContext";
import { config } from "../components/utils/types";

const STAGE = {
    CHECK_CORNERS : 1,
    WAITING_FOR_OPPONENT_JOIN: 2,
    WAITING_FOR_MOVE: 3,
}

const GameScreen = ({ route, navigation }) => {
  const { user } = useAuth();
  const { message, sendMessage } = useSocket();
  const { config }: { config: config } = route.params;
  const [stage, setStage] = useState()
  return (
    <View style={{ flex: 1 }}>
      <JavaCameraView
        style={{ width: "100%", height: "100%", backgroundColor: "blue" }}
      />
    </View>
  );
};

export default GameScreen;
