import { View, Text } from 'react-native'
import React from 'react'
import { JavaCameraView } from '../native/OpenCVCamera'

const HomeScreen = () => {
  return (
    <View style = {{flex: 1}}>
      <Text>Home Screen</Text>
      <JavaCameraView style={{ width: '100%', height: '100%', backgroundColor:'purple'}} /> 
    </View>
  )
}

export default HomeScreen