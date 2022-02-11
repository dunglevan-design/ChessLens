import {
  Box,
  Center,
  HStack,
  Image,
  Pressable,
  Text,
  useColorModeValue,
  View,
} from "native-base";
import React, { useRef } from "react";
import { Animated, useWindowDimensions, StatusBar } from "react-native";
import { SceneMap, TabView } from "react-native-tab-view";
import { useAuth } from "../components/ContextProviders/AuthContext";
import OnlineRoute from "../components/OnlineRoute";


const ComputerRoute = () => (
  <View flex = {1}/>
);

const InviteRoute = () => (
  <View flex = {1}/>
)
const renderScene = SceneMap({
  online: OnlineRoute,
  computer: ComputerRoute,
  invite: InviteRoute,
});

const HomeScreen = ({ navigation }) => {
  const { user } = useAuth();
  const layout = useWindowDimensions();

  const [index, setIndex] = React.useState(0);
  const [routes] = React.useState([
    { key: "online", title: "ONLINE" },
    { key: "computer", title: "COMPUTER" },
    { key: "invite", title: "INVITE"}
  ]);
  const RenderTabBar = ({ index, routes }) => {

    return (
      <Box flexDirection={"row"}>
        {routes.map((route, i) => {
          const color =
            index === i
              ? useColorModeValue("#fff", "#000")
              : useColorModeValue("#a1a1aa", "");

          const borderColor = index === i ? "purple.500" : "gray.400"
          return (
            <Box
              borderBottomWidth={3}
              flex={1}
              alignItems="center"
              p="3"
              key = {i} 
              borderBottomColor={borderColor}
            >
              <Pressable
                onPress={() => {
                  setIndex(i);
                }}
              >
                <Animated.Text style={{ color }}>{route.title}</Animated.Text>
              </Pressable>
            </Box>
          );
        })}
      </Box>
    );
  };

  return (
    <View flex={1} background="#040033">
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
export default HomeScreen;
