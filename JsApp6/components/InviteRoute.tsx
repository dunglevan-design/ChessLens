import { View, Text, TextInput, TouchableOpacity, Image } from "react-native";
import React, { useState } from "react";
import { useAuth } from "./ContextProviders/AuthContext";
import { useNavigation } from "@react-navigation/native";
import { MaterialCommunityIcons } from "@expo/vector-icons";
import { Feather } from "@expo/vector-icons";

const InviteRoute = () => {
  const { user } = useAuth();
  const [url, setURL] = useState("");
  const [player, setPlayer] = useState("");
  const navigation = useNavigation();

  const InvitePlayer = async () => {
    /**
     * TODO: navigate to GameScreen with invite config.
     */

    // const action = {
    //   type: "challengeDirectly",
    //   data: {
    //     username: player,
    //   },
    // };
    // sendMessage(action);

    //@ts-ignore
    navigation.navigate("Game", {
      config: {
        gametype: "invite",
        username: player,
        gametime: 10,
        gameincrement: 5,
      },
    });
  };

  return (
    <View style={{ flex: 1, backgroundColor: "#f8f8f8" }}>
      <View style={{ position: "absolute", width: "100%", bottom: -250, backgroundColor: "#f8f8f8" }}>
        <Image
          style={{ resizeMode: "contain", opacity: 0.9 }}
          resizeMode="contain"
          source={require("../public/logo.jpg")}
        ></Image>
      </View>
      <Text
        style={{
          color: "black",
          fontWeight: "500",
          padding: 6,
          marginBottom: 10,
          marginVertical: 20,
        }}
      >
        TO INVITE SOME ONE TO PLAY, GIVE THIS URL
      </Text>
      <View style={{ flexDirection: "row" }}>
        <TextInput
          style={{
            marginHorizontal: 3,
            width: "75%",
            maxWidth: 300,
            color: "black",
            borderColor: "#CECECE",
            borderWidth: 1,
            borderRadius: 10,
            padding: 10,
          }}
          value={url}
        />
        <TouchableOpacity
          style={{
            alignItems: "center",
            justifyContent: "center",
            padding: 3,
            borderColor: "#CECECE",
            borderWidth: 1,
            borderRadius: 10,
            width: 60,
            height: 60,
          }}
        >
          <Feather name="copy" size={24} color="black" />
        </TouchableOpacity>
      </View>

      {/* Invite Lichess player ID directly */}
      <Text
        style={{
          color: "black",
          fontWeight: "500",
          padding: 6,
          marginBottom: 10,
          marginVertical: 20,
        }}
      >
        OR INVITE A LICHESS PLAYER
      </Text>
      <View style={{ flexDirection: "row" }}>
        <TextInput
          style={{
            marginHorizontal: 3,
            width: "75%",
            maxWidth: 300,
            color: "black",
            borderColor: "#CECECE",
            borderWidth: 1,
            borderRadius: 10,
            padding: 10,
            marginRight: 10,
          }}
          value={player}
          onChangeText={(text) => setPlayer(text)}
        />
        <TouchableOpacity
          onPress={InvitePlayer}
          style={{
            alignItems: "center",
            justifyContent: "center",
            padding: 3,
            borderColor: "#CECECE",
            borderWidth: 1,
            borderRadius: 10,
            width: 60,
            height: 60,
          }}
        >
          <MaterialCommunityIcons name="sword-cross" size={24} color="black" />
        </TouchableOpacity>
      </View>
    </View>
  );
};

export default InviteRoute;
