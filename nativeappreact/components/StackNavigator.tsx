import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { createNativeStackNavigator } from "@react-navigation/native-stack";
import { useAuth } from "./ContextProviders/AuthContext";
import LoginScreen from "../Screens/LoginScreen";
import HomeScreen from "../Screens/HomeScreen";
import GameScreen from "../Screens/GameScreen";

const Stack = createNativeStackNavigator();
const StackNavigator = () => {
  const { user } = useAuth();
  return (
    <>
      <Stack.Navigator initialRouteName="Login">
        <Stack.Group screenOptions= { { headerTitleAlign : "center" ,headerTitleStyle : {color: "#fff"}, headerStyle: {
          backgroundColor: "#040033",
        }}}>
          {!user ? (
            <Stack.Screen name="Login" component={LoginScreen} />
          ) : (
            <>
              <Stack.Screen
                options={{ headerShown: false }}
                name="Home"
                component={HomeScreen}
              />
              <Stack.Screen
                options={{ headerShown: false }}
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
