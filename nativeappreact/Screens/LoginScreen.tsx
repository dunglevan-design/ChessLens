import { Box, Button } from 'native-base';
import React from 'react';
import { useAuth } from '../components/ContextProviders/AuthContext';

const LoginScreen = () => {
  const {LoginWithLichess} = useAuth();
  return (
      <Box flex = {1} alignItems = 'center' justifyContent = 'center'>
          <Button width = {200} mb = {3} onPress={() => LoginWithLichess()}>Login With Lichess</Button>
          <Button width = {200}>Login as Guest</Button>
      </Box>
  );
};

export default LoginScreen;
