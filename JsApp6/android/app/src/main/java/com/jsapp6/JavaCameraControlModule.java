package com.jsapp6;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import org.opencv.android.CameraBridgeViewBase;

public class JavaCameraControlModule extends ReactContextBaseJavaModule {
    //private CameraBridgeViewBase cameraBridgeViewBase;
    private JavaCameraViewManager javaCameraViewManager;

    public JavaCameraControlModule(@Nullable ReactApplicationContext reactContext, JavaCameraViewManager javaCameraViewManager) {
        super(reactContext);
        this.javaCameraViewManager = javaCameraViewManager;
    }


    @NonNull
    @Override
    public String getName() {
        return "JavaCameraControlModule";
    }

    @ReactMethod
    public void PassCommand(String command) {
        if (javaCameraViewManager  != null) {
            javaCameraViewManager.ReceiveCommand(command); // <-- Magic
        }
    }
}


