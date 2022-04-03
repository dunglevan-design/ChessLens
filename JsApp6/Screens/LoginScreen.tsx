import React from "react";
import { useAuth } from "../components/ContextProviders/AuthContext";
import * as Linking from "expo-linking";
import {
  Image,
  Text,
  TextComponent,
  TouchableOpacity,
  View,
} from "react-native";

const LoginScreen = () => {
  const { LoginWithLichess } = useAuth();

  const Testlinking = async () => {
    const linkurl = "com.jsapp6://app/home";
    console.log(await Linking.openURL(linkurl));
  };
  return (
    <View style={{ flex: 1, padding: 10 }}>
      <View style={{ position: "absolute", width: "100%", bottom: 0 }}>
        <Image
          style={{ resizeMode: "contain", opacity: 0.6 }}
          resizeMode="contain"
          source={require("../public/visioncamera.png")}
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
          <TouchableOpacity>
            <Text style={{ color: "rgb(50,150,255)", fontWeight: "700" }}>
              Grant acess
            </Text>
          </TouchableOpacity>
        </Text>

        <Text style={{ fontSize: 16, fontWeight: "400", marginBottom: 1 }}>
          Chess Lens needs{" "}
          <Text style={{ fontWeight: "700" }}>a Lichess account</Text>
          <TouchableOpacity>
            <Text style={{ color: "rgb(50,150,255)", fontWeight: "700" }} onPress = {() => LoginWithLichess()}>
              Login with Lichess
            </Text>
          </TouchableOpacity>
        </Text>
      </View>
    </View>
  );
};

export default LoginScreen;
