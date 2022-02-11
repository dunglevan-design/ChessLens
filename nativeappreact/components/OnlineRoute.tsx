import { HStack, Box, Center, Image, View, VStack } from "native-base";
import React from "react";
import { LinearGradient } from "expo-linear-gradient";
import { Text, TouchableOpacity } from "react-native";

const ModeCard = ({ title, desc, svg, color }) => {
  return (
    <Box height={250} width={"46%"} rounded={"lg"} position="relative">
      <TouchableOpacity>
        <LinearGradient
          colors={color}
          style={{
            width: "100%",
            height: "100%",
            borderRadius: 12,
            position: "absolute",
          }}
        ></LinearGradient>
        <HStack space={2} justifyContent="center" alignItems={"center"}>
          <Box width="40%">
            <Image
              opacity={0.9}
              size="xl"
              source={{
                uri: "https://firebasestorage.googleapis.com/v0/b/cheens-ef98b.appspot.com/o/YBA48JN-white-chess-pawn-clipart.png?alt=media&token=b73323b0-933e-4ccd-ba9e-5edfb514e4e4",
              }}
              alt="app logo"
            />
          </Box>
          <Image opacity={0.9} size={"md"} source={{ uri: svg }} alt={title} />
        </HStack>
        <Text
          style={{
            color: "#fff",
            fontWeight: "700",
            fontSize: 18,
            paddingHorizontal: 15,
            paddingTop: 10,
          }}
        >
          {title}
        </Text>
        <Text
          style={{
            color: "#fff",
            fontSize: 14,
            fontWeight: "700",
            paddingHorizontal: 15,
            paddingTop: 5,
            paddingBottom: 20,
          }}
        >
          {desc}
        </Text>
      </TouchableOpacity>
    </Box>
  );
};
const OnlineRoute = () => {
  return (
    <View flex={1}>
      <Text
        style={{
          color: "white",
          fontWeight: "500",
          padding: 6,
          marginBottom: 10,
          marginVertical: 20,
        }}
      >
        MATCH WITH PEOPLE AROUND THE WORLD
      </Text>

      <View flex={1}>
        <VStack>
          <HStack space={3} justifyContent="center">
            <ModeCard
              title="CLASSICAL"
              desc={"30 + 20 mins"}
              svg={
                "https://firebasestorage.googleapis.com/v0/b/cheens-ef98b.appspot.com/o/Classical.png?alt=media&token=5271ea65-dc08-4b60-88aa-c9ac705c4600"
              }
              color={["#E402F8", "rgba(112, 0, 255,0.6)"]}
            />
            <ModeCard
              title="RAPID"
              desc={"15 + 10 mins"}
              svg={
                "https://firebasestorage.googleapis.com/v0/b/cheens-ef98b.appspot.com/o/Rapid.png?alt=media&token=7e494f8e-04d0-4548-97db-4bfce1f0f1cb"
              }
              color={["#4ABF99", "rgba(112, 0, 255,0.6)"]}
            />
          </HStack>
          <HStack space = {3} justifyContent = "center">
            <ModeCard
              title="BLITZ"
              desc={"5 + 3 mins"}
              svg={
                "https://firebasestorage.googleapis.com/v0/b/cheens-ef98b.appspot.com/o/Blitz.png?alt=media&token=78336da1-aee3-4372-8f76-afaba8379a6e"
              }
              color={["#DC4A8C", "rgba(112, 0, 255,0.6)"]}
            />
            <ModeCard
              title="10 mins RAPID"
              desc={"10 mins"}
              svg={
                "https://firebasestorage.googleapis.com/v0/b/cheens-ef98b.appspot.com/o/10mins.png?alt=media&token=3544a4b5-5308-4aee-8b9c-bac9138be11c"
              }
              color={["#F89C4E", "rgba(112, 0, 255,0.6)"]}
            />
          </HStack>
        </VStack>
      </View>
    </View>
  );
};

export default OnlineRoute;
