import 'react-native-gesture-handler';
import 'react-native-reanimated'
import { StatusBar } from 'expo-status-bar';

import React, { useEffect, useState } from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { NativeBaseProvider } from 'native-base';
import AuthContext from './components/ContextProviders/AuthContext';
import StackNavigator from './components/StackNavigator';


export default function App() {

  
  return (
    <NavigationContainer>
      <NativeBaseProvider>
        <AuthContext>
          <StackNavigator />
        </AuthContext>
      </NativeBaseProvider>
    </NavigationContainer>      
  );
}

