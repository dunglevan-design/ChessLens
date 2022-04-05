import { requireNativeComponent, View, NativeModules } from 'react-native';
export const {JavaCameraControlModule}= NativeModules;
export const JavaCameraView = requireNativeComponent('JavaCameraView')
