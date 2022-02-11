import { View, Text } from 'react-native'
import React from 'react'
import { Box, Button, Center } from 'native-base'

const ComputerRoute = () => {
  return (
    <View style = {{flex: 1, alignItems: "center", justifyContent: "center"}}>
        <Box alignItems={"center"} justifyContent= "center">
            <Button px = {10} my= {5}><Text style ={{fontSize: 20, color: "#fff"}}>STOCK FISH 1 (1000)</Text></Button>
            <Button px = {10} my= {5}><Text style ={{fontSize: 20, color: "#fff"}}>STOCK FISH 2 (1200)</Text></Button>
            <Button px = {10} my= {5}><Text style ={{fontSize: 20, color: "#fff"}}>STOCK FISH 3 (1500)</Text></Button>
            <Button px = {10} my= {5}><Text style ={{fontSize: 20, color: "#fff"}}>STOCK FISH 4 (1700)</Text></Button>
        </Box>
    </View>
  )
}

export default ComputerRoute