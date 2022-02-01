/**
 * Sample React Native App
 * https://github.com/facebook/react-native
 *
 * Generated with the TypeScript template
 * https://github.com/react-native-community/react-native-template-typescript
 *
 * @format
 */
 import 'react-native-gesture-handler';
 import React, {useEffect, useState} from 'react';
 import {
   SafeAreaView,
   ScrollView,
   StatusBar,
   StyleSheet,
   Text,
   useColorScheme,
   View,
 } from 'react-native';
 import {
   Camera,
   useCameraDevices,
   useFrameProcessor,
 } from 'react-native-vision-camera';
 
 import {
   Colors,
   DebugInstructions,
   Header,
   LearnMoreLinks,
   ReloadInstructions,
 } from 'react-native/Libraries/NewAppScreen';
 import Animated, {
   useSharedValue,
   useAnimatedStyle,
 } from 'react-native-reanimated';
 import {
   gestureHandlerRootHOC,
   GestureHandlerRootView,
 } from 'react-native-gesture-handler';
 
 import { labelImage } from "vision-camera-image-labeler";
 
 const Section: React.FC<{
   title: string;
 }> = ({children, title}) => {
   const isDarkMode = useColorScheme() === 'dark';
   return (
     <View style={styles.sectionContainer}>
       <Text
         style={[
           styles.sectionTitle,
           {
             color: isDarkMode ? Colors.white : Colors.black,
           },
         ]}>
         {title}
       </Text>
       <Text
         style={[
           styles.sectionDescription,
           {
             color: isDarkMode ? Colors.light : Colors.dark,
           },
         ]}>
         {children}
       </Text>
     </View>
   );
 };
 
 const App = gestureHandlerRootHOC(() => {
   const isDarkMode = useColorScheme() === 'dark';
   const [CameraPermission, setCameraPermission] = useState(false);
   const devices = useCameraDevices();
 
   useEffect(() => {
     console.log(devices);
   }, [devices]);
   const device = devices.back;
 
   const GetPermissionAsync = async () => {
     const newCameraPermission = await Camera.requestCameraPermission();
     console.log(newCameraPermission);
     setCameraPermission(newCameraPermission === 'authorized');
   };
   useEffect(() => {
     GetPermissionAsync();
   }, []);
 
   const backgroundStyle = {
     backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
   };
 
   const catBounds = useSharedValue({top: 0, left: 0, right: 0, bottom: 0});
   const frameProcessor = useFrameProcessor(
     frame => {
       'worklet';
 
       // console.log(frame);
       const labels = labelImage(frame);
 
       console.log(labels);
 
     },
     [catBounds],
   );
 
   const boxOverlayStyle = useAnimatedStyle(
     () => ({
       position: 'absolute',
       borderWidth: 1,
       borderColor: 'red',
       ...catBounds.value,
     }),
     [catBounds],
   );
 
   return (
       <SafeAreaView style={backgroundStyle}>
         <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
         <ScrollView
           contentInsetAdjustmentBehavior="automatic"
           style={backgroundStyle}>
           <Header />
           <View
             style={{
               backgroundColor: isDarkMode ? Colors.black : Colors.white,
             }}>
             <Section title="Step One">
               Edit <Text style={styles.highlight}>App.tsx</Text> to change this
               screen and then come back to see your edits.
             </Section>
             <Section title="See Your Changes">
               <ReloadInstructions />
             </Section>
             <Section title="Debug">
               <DebugInstructions />
             </Section>
             <Section title="Learn More">
               Read the docs to discover what to do next:
             </Section>
             <LearnMoreLinks />
           </View>
 
           {device == null ? (
             <Text>Device not ready</Text>
           ) : (
             <>
               <Camera
               style={StyleSheet.absoluteFill}
               device={device}
               isActive={true}
               frameProcessor={frameProcessor}
             />
               <Animated.View style={boxOverlayStyle} />
             </>
           )}
         </ScrollView>
       </SafeAreaView>
   );
 });
 
 const styles = StyleSheet.create({
   sectionContainer: {
     marginTop: 32,
     paddingHorizontal: 24,
   },
   sectionTitle: {
     fontSize: 24,
     fontWeight: '600',
   },
   sectionDescription: {
     marginTop: 8,
     fontSize: 18,
     fontWeight: '400',
   },
   highlight: {
     fontWeight: '700',
   },
 });
 
 export default App;
 