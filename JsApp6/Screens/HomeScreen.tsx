import { useState } from "react";
import { Animated, StatusBar, StyleSheet, TouchableOpacity, useWindowDimensions, View } from "react-native";
import { SceneMap, TabView } from "react-native-tab-view";
import ComputerRoute from "../components/ComputerRoute";
import { useAuth } from "../components/ContextProviders/AuthContext";
import InviteRoute from "../components/InviteRoute";
import OnlineRoute from "../components/OnlineRoute";

const renderScene = SceneMap({
    online: OnlineRoute,
    computer: ComputerRoute,
    invite: InviteRoute,
  });
  
  const HomeScreen = ({ navigation, route }) => {
    const { user } = useAuth();
    const layout = useWindowDimensions();
  
    const [index, setIndex] = useState(0);
    const [routes] = useState([
      { key: "online", title: "ONLINE" },
      { key: "computer", title: "COMPUTER" },
      { key: "invite", title: "INVITE"}
    ]);
    const RenderTabBar = ({ index, routes }) => {
  
      return (
        <View style = {{flexDirection: "row"}}>
          {routes.map((route, i) => {
            const color =
              index === i ? "rgb(50,150,255)" : "black"
  
            const borderColor = index === i ? "rgb(50,150,255)" : "gray"
            return (
              <View
                key = {i} 
                style = {{flex: 1, alignItems: "center", padding: 20, borderBottomColor: borderColor, borderBottomWidth: 3}}
              >
                <TouchableOpacity
                  onPress={() => {
                    setIndex(i);
                  }}
                >
                  <Animated.Text style={{ color }}>{route.title}</Animated.Text>
                </TouchableOpacity>
              </View>
            );
          })}
        </View>
      );
    };
  
    return (
      <View style = {{flex: 1}}>
        <TabView
          navigationState={{ index, routes }}
          renderScene={renderScene}
          onIndexChange={setIndex}
          renderTabBar={() => <RenderTabBar index={index} routes={routes} />}
          initialLayout={{ width: layout.width }}
        />
      </View>
    );
  };


  const styles = StyleSheet.create({
    container: {
      marginTop: StatusBar.currentHeight,
    },
    scene: {
      flex: 1,
    },
  });
  export default HomeScreen;
  