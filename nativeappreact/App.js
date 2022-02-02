import 'react-native-reanimated'
import { StatusBar } from 'expo-status-bar';

import React, { useEffect, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { Camera, useCameraDevices, useFrameProcessor } from 'react-native-vision-camera';
import { labelImage } from 'vision-camera-image-labeler';

export default function App() {

  const [permission, setPermission] = useState(false);
  const devices = useCameraDevices();
  const device = devices.back;
  

  const frameProcessor = useFrameProcessor((frame) => {
    'worklet'
    // const labels = labelImage(frame);
    // console.log(frame);
  }, [])

  useEffect(() =>{

    (async () => {
      const newCameraPermission = await Camera.requestCameraPermission()
      console.log(newCameraPermission)
      setPermission(newCameraPermission=="authorized");
    })()
  },)
  return (
    <View style={styles.container}>
      {permission && device ? (
        <Camera style = {StyleSheet.absoluteFill} device = {device} isActive = {true} frameProcessor={frameProcessor} /> 
      ) : (<Text> NO device</Text>)}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
});
