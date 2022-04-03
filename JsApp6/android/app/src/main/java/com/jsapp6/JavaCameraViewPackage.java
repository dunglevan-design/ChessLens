package com.jsapp6;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class JavaCameraViewPackage implements ReactPackage {
    private JavaCameraViewManager javaCameraViewManager;

    @Override
    public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
        if (javaCameraViewManager == null){
            javaCameraViewManager = new JavaCameraViewManager();
        }
        return Arrays.<NativeModule>asList(
                new JavaCameraModule(reactContext, javaCameraViewManager));
    }
    @Override
    public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
        if (javaCameraViewManager == null) {
            javaCameraViewManager = new JavaCameraViewManager();
        }
        return Collections.<ViewManager>singletonList(
                javaCameraViewManager
        );
    }
}