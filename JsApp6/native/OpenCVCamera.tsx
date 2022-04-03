import { requireNativeComponent, View, NativeModules } from 'react-native';
export const {JavaCameraModule}= NativeModules;
export const JavaCameraView = requireNativeComponent('JavaCameraView')
