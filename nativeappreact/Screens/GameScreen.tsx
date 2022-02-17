import { View, Text, StyleSheet } from "react-native";
import React, { useEffect, useState } from "react";
import { Camera, useCameraDevices } from "react-native-vision-camera";
import { Badge, Box, Button, Center, Image } from "native-base";
import { useAuth } from "../components/ContextProviders/AuthContext";
import axios from "axios"

const GameScreen = ({ route, navigation }) => {
  const {user} = useAuth()
  const { type } = route.params;
  const devices = useCameraDevices();
  const device = devices.back;
  const [permission, setPermission] = useState(false);
  const [message, setMessage] = useState("");
  const [finishmessage, setFinishMessage] = useState("")
  const [finishingSetup, setFinishingSetup] = useState(false)

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
  
  const validate = async() => {
    console.log("good view, lets play")
    return true
  }
  const initGame = async(type) => {
    setFinishMessage("Looking for opponent")
    const gameconfig = {
      rated: false,
      time: 10,
      variant: "standard",  
      increment: 0
    }
    let headersList = {
      "Authorization": `Bearer ${user.accessToken}` 
     }
     
     let reqOptions = {
       url: "https://lichess.org/api/board/seek?",
       method: "POST",
       headers: headersList,
       params: gameconfig,
     }
     
     axios.request(reqOptions).then(function (response) {
       console.log(">>>>>>>>>>>>>>>")
       console.log(response.data);
     })

    
  }
  const FinishSetup = async() => {
    //run checks
    //initialize game
    setFinishingSetup(true)
    setFinishMessage("Checking your camera ...")

    if (await validate()){
      await initGame(type)      
      setFinishingSetup(false)
    };

  }
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
          <Box
          justifyContent={"center"}
          alignItems = "center"
          top={"40%"}
          >
            {!finishingSetup ? (

            <Button
              rounded={"full"}
              alignSelf={"center"} 
              position="relative"
              size={"lg"}
              onPress={() => FinishSetup()}
            >
              Finish
            </Button>
            ) : (
              <Badge colorScheme="info">{finishmessage}</Badge>
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
