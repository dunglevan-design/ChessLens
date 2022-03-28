package com.nativeappreact;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;

import com.facebook.react.bridge.ReactApplicationContext;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

public class CameraCheckPlugin extends FrameProcessorPlugin {
    /**
     * Initializes the native plugin part.
     *
     * Specifies the Frame Processor Plugin's name in the Runtime.
     *             The actual name in the JS Runtime will be prefixed with two underscores (`__`)
     * @param reactContext
     */
    Context context;
    protected CameraCheckPlugin(ReactApplicationContext reactContext) {
        super("CheckCamera");
        this.context = reactContext;
    }

    @Nullable
    @Override
    public Object callback(@NonNull ImageProxy image, @NonNull Object[] params) {
        return "Camera Check";
    }
}
