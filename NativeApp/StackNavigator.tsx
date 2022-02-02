import { View, Text } from 'react-native';
import React from 'react';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import LoginScreen from './pages/LoginScreen';
import HomeScreen from './pages/HomeScreen';
import GameScreen from './pages/GameScreen';


const Stack = createNativeStackNavigator();

const StackNavigator = () => {
  return (
    <>
        <Stack.Navigator>
            <Stack.Screen name = {"Login"} component = {LoginScreen}></Stack.Screen>

            <Stack.Screen name = {"Home"} component = {HomeScreen}></Stack.Screen>
            <Stack.Screen name = {"Game"} component = {GameScreen}></Stack.Screen>
        </Stack.Navigator>
    </>
  );
};

export default StackNavigator;
