/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */
import React from 'react';
import 'react-native-gesture-handler';
import {gestureHandlerRootHOC} from 'react-native-gesture-handler';

import {Box, extendTheme, NativeBaseProvider} from 'native-base';
import {NavigationContainer} from '@react-navigation/native';
import StackNavigator from './StackNavigator';
import AuthContext from './components/ContextProviders/AuthContext';

const config = {
  useSystemColorMode: false,
  initialColorMode: 'dark',
};

const Theme = extendTheme({config});

const App = gestureHandlerRootHOC(() => {
  return (
    <NavigationContainer>
      <NativeBaseProvider theme={Theme}>
        <AuthContext>
          <StackNavigator />
        </AuthContext>
      </NativeBaseProvider>
    </NavigationContainer>
  );
});

export default App;
