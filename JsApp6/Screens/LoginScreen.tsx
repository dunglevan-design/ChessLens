import React, { useState } from "react";
import { useAuth } from "../components/ContextProviders/AuthContext";
import * as Linking from "expo-linking";
import {
  Image,
  Text,
  TextComponent,
  ToastAndroid,
  TouchableOpacity,
  View,
} from "react-native";
import { Camera } from "expo-camera";

const LoginScreen = () => {
  const { LoginWithLichess } = useAuth();
  const [permission, setPermission] = useState(false)


  const _requestCameraPermission = async() => {
    const { status } = await Camera.requestCameraPermissionsAsync();
    if (status === "granted"){
        setPermission(status === 'granted');
        ToastAndroid.show("Camera permission granted", ToastAndroid.SHORT)
    }
  }

  const Testlinking = async () => {
    const linkurl = "com.jsapp6://app/home";
    console.log(await Linking.openURL(linkurl));
  };
  return (
    <View style={{ flex: 1, padding: 10, backgroundColor: "#f8f8f8" }}>
      <View style={{ position: "absolute", width: "100%", bottom: -150 }}>
        <Image
          style={{ resizeMode: "contain", opacity: 0.9 }}
          resizeMode="contain"
          source={require("../public/logo.jpg")}
        ></Image>
      </View>

      <Text style={{ fontSize: 40, fontWeight: "700", marginBottom: 10 }}>
        Welcome to Chess Lens
      </Text>

      <View>
        <Text style={{ fontSize: 16, fontWeight: "400", marginBottom: 1 }}>
          Playing <Text style={{ fontWeight: "700" }}>OTB Chess</Text> online
          through the lens of your device
        </Text>
        <Text style={{ fontSize: 16, fontWeight: "400", marginBottom: 1 }}>
          Chess Lens needs{" "}
          <Text style={{ fontWeight: "700" }}>Camera permission</Text>
          <TouchableOpacity onPress={() => _requestCameraPermission()}>
            <Text style={{ color: "rgb(50,150,255)", fontWeight: "700" }}>
              Grant acess
            </Text>
          </TouchableOpacity>
        </Text>

        <Text style={{ fontSize: 16, fontWeight: "400", marginBottom: 1 }}>
          Chess Lens needs{" "}
          <Text style={{ fontWeight: "700" }}>a Lichess account</Text>
          <TouchableOpacity onPress = {() => LoginWithLichess()}>
            <Text style={{ color: "rgb(50,150,255)", fontWeight: "700" }}>
              Login with Lichess
            </Text>
          </TouchableOpacity>
        </Text>
      </View>
    </View>
  );
};

export default LoginScreen;
