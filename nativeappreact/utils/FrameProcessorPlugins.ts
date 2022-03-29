import type { Frame } from "react-native-vision-camera";

declare global {
  var __CheckCamera: (frame: Frame) => string;
  var __GenerateMove: (frame: Frame) => string | string[] | undefined | null;
}

export function GenerateMove(
  frame: Frame,
  prevFrame: Frame,
  corner1x: number,
  corner1y: number,
  corner2x: number,
  corner2y: number,
  corner3x: number,
  corner3y: number,
  corner4x: number,
  corner4y: number
): string | string[] | undefined | null {
  "worklet";
  return __GenerateMove(frame);
}

export function CheckCamera(frame: Frame) {
  "worklet";
  return __CheckCamera(frame);
}
