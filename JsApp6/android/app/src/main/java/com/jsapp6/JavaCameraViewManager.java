package com.jsapp6;

import android.view.SurfaceView;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Mat;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.view.SurfaceView;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class JavaCameraViewManager extends SimpleViewManager<FrameLayout>
        implements CameraBridgeViewBase.CvCameraViewListener2 {
    public static final String REACT_CLASS = "JavaCameraView";
    public CameraBridgeViewBase cameraBridgeViewBase;
    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat mGray;
    @Override
    public String getName() {
        return REACT_CLASS;
    }
    @Override
    public FrameLayout createViewInstance(ThemedReactContext context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final FrameLayout preview = (FrameLayout) inflater.inflate(R.layout.camera_layout, null);
        WeakReference<ViewGroup> layoutRef = new WeakReference<ViewGroup>(preview); // constructor parameters pass it
        cameraBridgeViewBase = (CameraBridgeViewBase) preview.findViewById(R.id.camera_view);
        cameraBridgeViewBase.enableView();
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        cameraBridgeViewBase.setCvCameraViewListener(this);
        return preview;
    }
    @Override
    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        mGray = new Mat(height, width, CvType.CV_8UC1);
        System.out.println("opencv started");
    }
    @Override
    public void onCameraViewStopped() {
    }
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();

        //Core.transpose(mGray, mGray);
        //Core.flip(mGray, mGray, -1); // rotates Mat to portrait
        Mat newm = new Mat();
        Mat rsnewm = new Mat();
        Core.rotate(mRgba, newm, Core.ROTATE_90_CLOCKWISE);
        Imgproc.resize(newm, rsnewm, new Size(newm.height(), newm.width()));
        System.out.println("opencv camera frame"+ mRgba.size().toString());
// Frame processing code here
        return rsnewm;
    }


}