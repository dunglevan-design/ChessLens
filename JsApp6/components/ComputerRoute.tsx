import { View, Text, TouchableOpacity } from 'react-native'
import React from 'react'

const ComputerRoute = () => {
    return (
      <View style = {{flex: 1, alignItems: "center", justifyContent: "center"}}>
          <Text style = {{fontWeight: "700", fontSize: 20}}>Play with the computer</Text>
          
          <View style = {{alignItems: "center", justifyContent: "center"}}>
              <TouchableOpacity style={{paddingHorizontal: 10, paddingVertical: 10, borderRadius: 12, marginVertical: 5, backgroundColor: "white"}}><Text style ={{fontSize: 20, color: "black"}}>STOCK FISH 1 (1000)</Text></TouchableOpacity>
              <TouchableOpacity style={{paddingHorizontal: 10, paddingVertical: 10, borderRadius: 12, marginVertical: 5, backgroundColor: "white"}}><Text style ={{fontSize: 20, color: "black"}}>STOCK FISH 1 (1000)</Text></TouchableOpacity>
              <TouchableOpacity style={{paddingHorizontal: 10, paddingVertical: 10, borderRadius: 12, marginVertical: 5, backgroundColor: "white"}}><Text style ={{fontSize: 20, color: "black"}}>STOCK FISH 1 (1000)</Text></TouchableOpacity>
              <TouchableOpacity style={{paddingHorizontal: 10, paddingVertical: 10, borderRadius: 12, marginVertical: 5, backgroundColor: "white"}}><Text style ={{fontSize: 20, color: "black"}}>STOCK FISH 1 (1000)</Text></TouchableOpacity>

          </View>
      </View>
    )
  }
  
  export default ComputerRoute