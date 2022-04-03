import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import LoginScreen from "../Screens/LoginScreen";
import HomeScreen from "../Screens/HomeScreen";
import { useAuth } from "./ContextProviders/AuthContext";
import GameScreen from "../Screens/GameScreen";

const Stack = createNativeStackNavigator();
const StackNavigator = () => {
  const { user } = useAuth();
  return (
    <>
      <Stack.Navigator initialRouteName="Login">
        <Stack.Group
          screenOptions={{ headerShown: false, headerStyle: {} }}
        >
          {!user ? (
            <Stack.Screen name="Login" component={LoginScreen} />
          ) : (
            <>
              <Stack.Screen name="Home" component={HomeScreen} />
              <Stack.Screen
                name="Game"
                component={GameScreen}
              />
            </>
          )}
        </Stack.Group>
      </Stack.Navigator>
    </>
  );
};

export default StackNavigator;
