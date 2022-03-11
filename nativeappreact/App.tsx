import "react-native-gesture-handler";
import "react-native-reanimated";

import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { NativeBaseProvider } from "native-base";
import AuthContext from "./components/ContextProviders/AuthContext";
import StackNavigator from "./components/StackNavigator";

import * as Linking from "expo-linking";
import SocketContext from "./components/ContextProviders/SocketContext";

const prefix = Linking.createURL("app");

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
    <NavigationContainer linking={linking}>
      <NativeBaseProvider>
        <SocketContext>
          <AuthContext>
            <StackNavigator />
          </AuthContext>
        </SocketContext>
      </NativeBaseProvider>
    </NavigationContainer>
  );
}
