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
          <Center position={"absolute"} width = "100%" height={1000}>
              <Image background= "#040033" alt = "logo" size = "2xl" style = {{}} resizeMethod = "scale" resizeMode='contain' source = {{uri: "https://firebasestorage.googleapis.com/v0/b/cheens-ef98b.appspot.com/o/YBA48JN-white-chess-pawn-clipart.png?alt=media&token=b73323b0-933e-4ccd-ba9e-5edfb514e4e4"}} ></Image>
          </Center>
          <Button width = {200} mb = {3} onPress={() => LoginWithLichess()}>Login With Lichess</Button>
          <Button width = {200} onPress = {() => Testlinking()}>Login as Guest</Button>


      </Box>
  );
};



export default LoginScreen;
