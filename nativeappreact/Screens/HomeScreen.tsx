import { Center, HStack, Image, Text, View } from "native-base";
import React from "react";
import { useAuth } from "../components/ContextProviders/AuthContext";

const HomeScreen = ({ navigation }) => {
  const { user } = useAuth();
  return (
    <View flex={1} background = "#040033">
      
      
    </View>
  );
};

export default HomeScreen;
