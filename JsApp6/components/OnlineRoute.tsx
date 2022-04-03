import { View, Text, TouchableOpacity, Image } from "react-native";
import React from "react";
import { useNavigation } from "@react-navigation/native";

const ModeCard = ({ title, desc, onPress }) => {
  return (
    <View
      style={{
        height: 250,
        width: "46%",
        borderRadius: 10,
        position: "relative",
        alignContent: "center",
        justifyContent: "center",
        borderWidth: 1,
        borderColor: "#edebe9",
        margin: 2,
        backgroundColor: "rgba(255,255,255,0)"
      }}
    >
      <TouchableOpacity
        onPress={onPress}
        style={{ flex: 1, alignContent: "center", justifyContent: "center" }}
      >
        <Text
          style={{
            color: "black",
            fontWeight: "700",
            fontSize: 18,
            paddingHorizontal: 15,
            paddingTop: 10,
            textAlign: "center",
          }}
        >
          {title}
        </Text>
        <Text
          style={{
            color: "black",
            fontSize: 32,
            fontWeight: "700",
            paddingHorizontal: 15,
            paddingTop: 5,
            paddingBottom: 20,
            textAlign: "center",
          }}
        >
          {desc}
        </Text>
      </TouchableOpacity>
    </View>
  );
};
const OnlineRoute = () => {
  const navigation = useNavigation();
  const createGame = (type: string) => {
    /**
     * send board seek signal. on startGame message, navigate to Game with config
     */
    //@ts-ignore
    // navigation.navigate("Game", {config : config});
  };
  return (
    <View style={{ flex: 1, backgroundColor: "#edebe9" }}>
      <Text
        style={{
          color: "black",
          fontWeight: "700",
          padding: 6,
          marginBottom: 10,
          marginVertical: 20,
        }}
      >
        MATCH WITH PEOPLE AROUND THE WORLD
      </Text>

      <View style={{ flex: 1, padding: 10 }}>
        <View
          style={{
            padding: 1,
            paddingVertical: 10,
            backgroundColor: "white",
            borderRadius: 10,
            position: "relative",
            overflow: "hidden"
            
          }}
        >
          <View style={{ flex: 1,position: "absolute", width: "100%", alignItems: "center", justifyContent: "center" }}>
            <Image
              style={{ resizeMode: "contain", opacity: 0.6 }}
              resizeMode="contain"
              source={require("../public/lichesslogo.png")}
            ></Image>
          </View>
          <View style={{ flexDirection: "row", justifyContent: "center", backgroundColor: "rgba(255,255,255,0)" }}>
            <ModeCard
              title="CLASSICAL"
              desc={"30 + 20"}
              onPress={() => createGame("classical")}
            />
            <ModeCard
              title="RAPID"
              desc={"15 + 10"}
              onPress={() => createGame("classical")}
            />
          </View>
          <View style={{ flexDirection: "row", justifyContent: "center" }}>
            <ModeCard
              title="BLITZ"
              desc={"5 + 3"}
              onPress={() => createGame("classical")}
            />
            <ModeCard
              title="10 mins RAPID"
              desc={"10"}
              onPress={() => createGame("classical")}
            />
          </View>
        </View>
      </View>
    </View>
  );
};

export default OnlineRoute;
