import type { Frame } from "react-native-vision-camera";

declare global {
    var __GenerateMove: (
        frame: Frame
    ) => string | string[] | undefined | null
}


export function GenerateMove(frame:Frame): string | string[] | undefined | null {
    "worklet";
    return __GenerateMove(frame);
}