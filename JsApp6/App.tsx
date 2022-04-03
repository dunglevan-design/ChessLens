import { NavigationContainer } from '@react-navigation/native';
import React, { Component } from 'react'
import { View } from 'react-native';
import AuthContext from './components/ContextProviders/AuthContext';
import StackNavigator from './components/StackNavigator';
import * as Linking from "expo-linking";

import { JavaCameraView } from './native/OpenCVCamera';


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
        <AuthContext>
          {/* <SocketContext> */}
            <StackNavigator />
          {/* </SocketContext> */}
        </AuthContext>
    </NavigationContainer>
  );
}
