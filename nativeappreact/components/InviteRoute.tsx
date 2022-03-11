import { View, Text } from "react-native";
import React, { useEffect, useState } from "react";
import {  HStack, IconButton, Input } from "native-base";
import { MaterialCommunityIcons } from "@expo/vector-icons";
import { Feather } from "@expo/vector-icons";
import { useAuth } from "./ContextProviders/AuthContext";
import { useSocket } from "./ContextProviders/SocketContext";
const InviteRoute = () => {
  const { user } = useAuth();
  const [url, setURL] = useState("");
  const [player, setPlayer] = useState("");
  const {message, sendMessage} = useSocket()

  const InvitePlayer = async () => {
    // TODO: sending invite message to backend
    console.log(message)


    
  }

  return (
    <View style={{ flex: 1 }}>
      <Text
        style={{
          color: "white",
          fontWeight: "500",
          padding: 6,
          marginBottom: 10,
          marginVertical: 20,
        }}
      >
        TO INVITE SOME ONE TO PLAY, GIVE THIS URL
      </Text>
      <HStack>
        <Input mx={3} w="75%" maxWidth={300} value={url} color="#fff" />
        <IconButton
          icon={<Feather name="copy" size={24} color="white" />}
          size={"md"}
          bg={"purple.500"}
        />
      </HStack>

      {/* Invite Lichess player ID directly */}
      <Text
        style={{
          color: "white",
          fontWeight: "500",
          padding: 6,
          marginBottom: 10,
          marginVertical: 20,
        }}
      >
        OR INVITE A LICHESS PLAYER
      </Text>
      <HStack>
        <Input
          mx={3}
          w="75%"
          maxWidth={300}
          value={player}
          color="#fff"
          onChangeText={(text) => setPlayer(text)}
        />
        <IconButton
          icon={
            <MaterialCommunityIcons
              name="sword-cross"
              size={24}
              color="white"
            />
          }
          size={"md"}
          bg={"purple.500"}
          onPress={InvitePlayer}
        />
      </HStack>
    </View>
  );
};

export default InviteRoute;
