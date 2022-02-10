import { Box, Button, Center, Image } from 'native-base';
import React from 'react';
import { useAuth } from '../components/ContextProviders/AuthContext';
import * as Linking from 'expo-linking';

const LoginScreen = () => {
  const {LoginWithLichess} = useAuth();

  const Testlinking = async() => {
      const linkurl = "com.nativeappreact://app/home";
      console.log(await Linking.openURL(linkurl));
  }
  return (
      <Box flex = {1} alignItems = 'center' justifyContent = 'center' background={"#040033"}>
          <Center>
              <Image size={"lg"} background= "#040033" alt = "logo" source = {{uri: "https://www.clipartmax.com/png/middle/397-3970363_chess-piece-pawn-queen-knight-chess-piece-pawn-queen-knight.png"}} ></Image>
          </Center>
          <Button width = {200} mb = {3} onPress={() => LoginWithLichess()}>Login With Lichess</Button>
          <Button width = {200} onPress = {() => Testlinking()}>Login as Guest</Button>
      </Box>
  );
};

export default LoginScreen;
