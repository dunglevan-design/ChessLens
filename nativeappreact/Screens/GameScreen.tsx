/***
 * This screen first checks the camera. Set up the board. Then start a game
 * with passed configurations
 */

import { View, Text, StyleSheet, Dimensions } from "react-native";

import React, { useEffect, useState } from "react";
import {
  Camera,
  useCameraDevices,
  useFrameProcessor,
} from "react-native-vision-camera";
import { Badge, Box, Button, Center, Image, Modal } from "native-base";
import { useAuth } from "../components/ContextProviders/AuthContext";
import axios from "axios";
import { CheckCamera, GenerateMove } from "../utils/FrameProcessorPlugins";
import Animated, {
  useAnimatedStyle,
  useSharedValue,
} from "react-native-reanimated";
import Svg, { Line } from "react-native-svg";
import { useSocket } from "../components/ContextProviders/SocketContext";

const windowWidth = Dimensions.get("window").width;
const windowHeight = Dimensions.get("window").height;

const GameScreen = ({ route, navigation }) => {
  const { user } = useAuth();
  const { message, sendMessage } = useSocket();
  const { config }: { config: config } = route.params;
  const devices = useCameraDevices();
  const device = devices.back;

  const [permission, setPermission] = useState(false);
  const [appmessage, setAppMessage] = useState("");
  const [finishmessage, setFinishMessage] = useState("");
  const [finishedSetup, setfinishedSetup] = useState(false);
  const [btntitle, setBtnTitle] = useState("Next");
  const [setupMessage, setSetupMessage] = useState(
    "Place the camera still at the side of the board, check if the board corners are drawn correctly"
  );
  const [setupStage, setSetupStage] = useState("CheckCorners");

  const corner1 = useSharedValue({ x: 0, y: 0 });
  const corner2 = useSharedValue({ x: 0, y: 0 });
  const corner3 = useSharedValue({ x: 0, y: 0 });
  const corner4 = useSharedValue({ x: 0, y: 0 });

  const requestPermissionAsync = async () => {
    const newPermission = await Camera.requestCameraPermission();
    if (newPermission === "authorized") {
      setPermission(true);
      setAppMessage("");
    } else {
      setAppMessage("you need to enable camera to use this feature.");
    }
  };

  useEffect(() => {
    if (!permission) {
      requestPermissionAsync();
    }
  }, []);

  /**
   * Check if theres change in the message from socket. Handle accordingly
   */
  useEffect(() => {
    if (message) {
      switch (message.action) {
        case "startGame":
          console.log("start game");
          setSetupStage("GameStarted")
          break;
        default:
          console.log("nothing here");
      }
    }
  }, [message]);

  const initGame = async () => {
    /**
     * Render game view on top of the camera.
     * for testing, camera view for now
     */
    setFinishMessage("game started");
    console.log("config: ", config);
  };

  const FinishSetup = async () => {
    /**
     * Init game
     */

    switch (setupStage) {
      case "CheckCorners":
        setSetupStage("PutPieces");
        setSetupMessage(
          "Now place pieces on the board, white on the right side and black on the left side"
        );
        break;
      case "PutPieces":
        setSetupStage("Finished");
        setSetupMessage("Waiting for opponent ...");

        if (config.gametype === "invite") {
          const action = {
            type: "challengeDirectly",
            data: {
              username: config.username,
            },
          };
          sendMessage(action);
        }

        break;
    }
  };

  const frameProcessor = useFrameProcessor(
    (frame) => {
      "worklet";
      // Draw Corners.
      /**
       * Frames are rotated by 90deg
       */

      switch (setupStage){
        case "CheckCorners":
          const xscale = windowWidth / frame.height;
          const yscale = windowWidth / 0.5625 / frame.width;
          const corners = JSON.parse(CheckCamera(frame));

          console.log("xscale: ", xscale);
          console.log("yscale: ", yscale);
          console.log("framewidth: ", frame.width);
          console.log("framehgeight: ", frame.height);

          corner1.value = {
            x: corners[0][0] * yscale,
            y: corners[0][1] * xscale,
          };
          corner2.value = {
            x: corners[1][0] * yscale,
            y: corners[1][1] * xscale,
          };
          corner3.value = {
            x: corners[2][0] * yscale,
            y: corners[2][1] * xscale,
          };
          corner4.value = {
            x: corners[3][0] * yscale,
            y: corners[3][1] * xscale,
          };
          break;

        /**
         * 
         */
        case "GameStarted"
      }    
      // const generatedMove = GenerateMove(frame);
      // console.log("generated Move: ", generatedMove);
      // console.log(frame.height, windowHeight)
      // console.log(frame.width, windowWidth)
    },
    [corner1, corner2, corner3, corner4]
  );

  const corner1Style = useAnimatedStyle(
    () => ({
      position: "absolute",
      width: 10,
      height: 10,
      backgroundColor: "red",
      borderRadius: 10,
      top: corner1.value.x - 5,
      right: corner1.value.y - 5,
    }),
    [corner1]
  );
  const corner2Style = useAnimatedStyle(
    () => ({
      position: "absolute",
      width: 10,
      height: 10,
      backgroundColor: "red",
      borderRadius: 10,
      top: corner2.value.x - 5,
      right: corner2.value.y - 5,
    }),
    [corner2]
  );
  const corner3Style = useAnimatedStyle(
    () => ({
      position: "absolute",
      width: 10,
      height: 10,
      backgroundColor: "red",
      borderRadius: 10,
      top: corner3.value.x - 5,
      right: corner3.value.y - 5,
    }),
    [corner3]
  );
  const corner4Style = useAnimatedStyle(
    () => ({
      position: "absolute",
      width: 10,
      height: 10,
      backgroundColor: "red",
      borderRadius: 10,
      top: corner4.value.x - 5,
      right: corner4.value.y - 5,
    }),
    [corner4]
  );
  return (
    <>
      <View style={{ flex: 1 }}>
        {!permission ? (
          <Loading />
        ) : (
          <>
            {device ? (
              <Camera
                style={[
                  StyleSheet.absoluteFill,
                  { aspectRatio: 0.5625, height: windowWidth / 0.5625 },
                ]}
                device={device}
                isActive={true}
                frameProcessor={frameProcessor}
                frameProcessorFps={0.5}
              ></Camera>
            ) : (
              <></>
            )}
            <View style={{ backgroundColor: "rgba(0,0,0,0.4)" }}>
              <Text style={{ fontSize: 20, fontWeight: "600", color: "white" }}>
                {setupMessage}
              </Text>
            </View>
            <Box justifyContent={"center"} alignItems="center" top={"60%"}>
              <Button
                rounded={"full"}
                alignSelf={"center"}
                position="relative"
                size={"lg"}
                onPress={FinishSetup}
                zIndex={10}
              >
                {btntitle}
              </Button>
            </Box>
          </>
        )}
        <Animated.View style={corner1Style} />
        <Animated.View style={corner2Style} />
        <Animated.View style={corner3Style} />
        <Animated.View style={corner4Style} />
      </View>
    </>
  );
};

const Loading = () => {
  return (
    <View style={{ flex: 1, backgroundColor: "#040033" }}>
      <Center>
        <Image
          size="md"
          source={require("../public/loading.gif")}
          alt="loading gif"
        />
      </Center>
    </View>
  );
};
export default GameScreen;
