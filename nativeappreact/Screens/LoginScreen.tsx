import { Box, Button } from 'native-base';
import React from 'react';
import { useAuth } from '../components/ContextProviders/AuthContext';
import * as Linking from 'expo-linking';

const LoginScreen = () => {
  const {LoginWithLichess} = useAuth();

  const Testlinking = async() => {
      const linkurl = "nativeappreact://app/home";
      console.log(await Linking.openURL(linkurl));
  }
  return (
      <Box flex = {1} alignItems = 'center' justifyContent = 'center'>
          <Button width = {200} mb = {3} onPress={() => LoginWithLichess()}>Login With Lichess</Button>
          <Button width = {200} onPress = {() => Testlinking()}>Login as Guest</Button>
      </Box>
  );
};

export default LoginScreen;
