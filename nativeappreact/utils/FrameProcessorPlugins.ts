import type { Frame } from "react-native-vision-camera";

declare global {
    var __GenerateMove: (
        frame: Frame
    ) => string | string[] | undefined | null
    var __CheckCamera: (frame:Frame) => string | string[] | number
}


export function GenerateMove(frame:Frame): string | string[] | undefined | null {
    "worklet";
    return __GenerateMove(frame);
}

export function CheckCamera(frame:Frame){
    "worklet";
    return __CheckCamera(frame)
}