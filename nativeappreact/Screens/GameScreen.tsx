import { View, Text, StyleSheet } from "react-native";
import React, { useEffect, useState } from "react";
import { Camera, useCameraDevices } from "react-native-vision-camera";
import { Badge, Box, Button, Center, Image } from "native-base";
import { useAuth } from "../components/ContextProviders/AuthContext";
import axios from "axios";

/** All game use this screen
 * TODO: move camera setup to start of the app maybe.
 */
const GameScreen = ({ route, navigation }) => {
  const { user } = useAuth();
  const { config } = route.params;
  const devices = useCameraDevices();
  console.log(devices);
  const device = devices.front;
  console.log(device);
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

  //validate camera view
  /**
   *  TODO: frame processor to approximate camera angle
   */
  const validate = async () => {
    console.log("good view, lets play");
    return true;
  };

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
    setFinishMessage("Checking your camera ...");

    //validate camera view
    if (await validate()) {
      await initGame();
      setfinishedSetup(true);
    }
  };
  return (
    <View style={{ flex: 1 }}>
      {!permission ? (
        <Loading />
      ) : (
        <>
          <Camera
            style={StyleSheet.absoluteFill}
            device={device}
            isActive={true}
          ></Camera>
          <Text style={{ fontSize: 20, fontWeight: "600", color: "#f11625" }}>
            Place the camera where it can see the board clearly
          </Text>
          <Box justifyContent={"center"} alignItems="center" top={"40%"}>
            {!finishedSetup ? (
              <>
                <Button
                  rounded={"full"}
                  alignSelf={"center"}
                  position="relative"
                  size={"lg"}
                  onPress={FinishSetup}
                >
                  Finish
                </Button>
                {finishmessage && (
                  <Badge colorScheme="info">{finishmessage}</Badge>
                )}
              </>
            ) : (
              <></>
            )}
          </Box>
        </>
      )}
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
