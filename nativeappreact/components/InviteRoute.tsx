import { View, Text } from "react-native";
import React, { useEffect, useState } from "react";
import { Button, Input } from "native-base";

const InviteRoute = () => {
  const [url, setURL] = useState("");
  const [player, setPlayer] = useState("");

  useEffect(() => {
    console.log("Getting URL");
    setURL("12345");
  }, []);

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
      <Input mx={3} w="75%" maxWidth={300} value={url} color="#fff" />
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

      <Input mx={3} w="75%" maxWidth={300} value={player} color="#fff" onChangeText={(text) => setPlayer(text)} />
      <Button></Button>
    </View>
  );
};

export default InviteRoute;
