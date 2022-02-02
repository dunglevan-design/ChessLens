import React from 'react';
import { Box, Button, Text } from 'native-base';

const LoginScreen = () => {

  function LoginWithLichess(){
      console.log("login with lichess")
  }
  return (
      <Box flex={1} alignItems={"center"} justifyContent= "center">
          <Button onPress={() => LoginWithLichess()}> Login With Lichess </Button>
      </Box>
  );
};

export default LoginScreen;
