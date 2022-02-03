import "react-native-gesture-handler";
import "react-native-reanimated";
import { StatusBar } from "expo-status-bar";

import React, { useEffect, useState } from "react";
import { NavigationContainer } from "@react-navigation/native";
import { NativeBaseProvider } from "native-base";
import AuthContext from "./components/ContextProviders/AuthContext";
import StackNavigator from "./components/StackNavigator";

import * as Linking from "expo-linking";

const prefix = Linking.createURL("path");

export default function App() {
  const linking = {
    prefixes: [prefix],
    config: {
      screens: {
        Login: "login",
        Game: "game",
        Home: "home",
      },
    },
  };
  return (
    <NavigationContainer
      linking={linking}
    >
      <NativeBaseProvider>
        <AuthContext>
          <StackNavigator />
        </AuthContext>
      </NativeBaseProvider>
    </NavigationContainer>
  );
}
