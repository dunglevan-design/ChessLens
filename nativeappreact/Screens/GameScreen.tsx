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
import Animated, { useAnimatedStyle, useSharedValue } from "react-native-reanimated";


const windowWidth = Dimensions.get('window').width;
const windowHeight = Dimensions.get('window').height;

const GameScreen = ({ route, navigation }) => {
  const { user } = useAuth();
  const { config } = route.params;
  const devices = useCameraDevices();
  const device = devices.back;

  const firstcorner = useSharedValue({ top: 700, left: 133})


  const [permission, setPermission] = useState(false);
  const [message, setMessage] = useState("");
  const [finishmessage, setFinishMessage] = useState("");
  const [finishedSetup, setfinishedSetup] = useState(false);

  const requestPermissionAsync = async () => {
    const newPermission = await Camera.requestCameraPermission();
    if (newPermission === "authorized") {
      setPermission(true);
      setMessage("");
    } else {
      setMessage("you need to enable camera to use this feature.");
    }
  };

  useEffect(() => {
    if (!permission) {
      requestPermissionAsync();
    }

  }, []);

  const initGame = async () => {
    /**
     * Render game view on top of the camera.
     * for testing, camera view for now
     */
    setFinishMessage("game started");
    console.log("config: ", config);
  };

  const FinishSetup = async () => {
    //run checks
    //initialize game
    setFinishMessage("Looking for opponents ...");

    
  };

  const frameProcessor = useFrameProcessor((frame) => {
    "worklet";
    // Draw Corners.
    const corners = CheckCamera(frame);
    console.log("corners:", corners)

    // const generatedMove = GenerateMove(frame);
    // console.log("generated Move: ", generatedMove);
    // console.log(frame.height, windowHeight)
    // console.log(frame.width, windowWidth)
  }, []);

  const boxOverlayStyle = useAnimatedStyle(() => ({
    position: 'absolute',
    backgroundColor: "red",
    width: 20,
    height: 20,
    ...firstcorner.value
  }), [firstcorner])

  return (
    <View style={{ flex: 1 }}>
      {!permission ? (
        <Loading />
      ) : (
        <>
          {device ? (
            <Camera
              style={[StyleSheet.absoluteFill, {aspectRatio: 1}]}
              device={device}
              isActive={true}
              frameProcessor={frameProcessor}
              frameProcessorFps={0.1}
              zoom={1}
            ></Camera>
          ) : (
            <></>
          )}
          <Text style={{ fontSize: 20, fontWeight: "600", color: "#f11625" }}>
            Place the camera still at the side of the board, check if the board
            edges are drawn detected correctly
          </Text>

          <Box justifyContent={"center"} alignItems="center" top={"60%"}>
            {!finishedSetup ? (
              <>
                <Button
                  rounded={"full"}
                  alignSelf={"center"}
                  position="relative"
                  size={"lg"}
                  onPress={FinishSetup}
                >
                  Start Game
                </Button>
                {finishmessage !== "" && (
                  <Badge colorScheme="info" rounded={"full"} variant="outline">
                    <Text style={{ fontSize: 30, padding: 10, color: "white" }}>
                      {finishmessage}
                    </Text>
                  </Badge>
                )}
              </>
            ) : (
              <></>
            )}
          </Box>
        </>
      )}
      <Animated.View style={boxOverlayStyle} />
    </View>
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
