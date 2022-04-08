package com.jsapp6;

import android.graphics.Bitmap;
import android.view.SurfaceView;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
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
import org.opencv.calib3d.Calib3d;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JavaCameraViewManager extends SimpleViewManager<FrameLayout>
        implements CameraBridgeViewBase.CvCameraViewListener2 {
    public static final String REACT_CLASS = "JavaCameraView";
    public CameraBridgeViewBase cameraBridgeViewBase;
    public final int COMMAND_CHECK_CORNERS = 1;
    private Mat mRgba;
    private Mat mIntermediateMat;
    private Mat correctedmRgba;
    private Mat correctedmGray;
    private Mat mGray;
    private MatOfPoint2f corners;
    private Mat perspective;
    private Mat newmcorners;
    private Mat hemo;
    private Mat outerCorners;
    private Mat outerCorner1;
    private Mat outerCorner2;
    private Mat outerCorner3;
    private Mat outerCorner4;
    private Mat InitialFrame;
    private Mat InitialFrameRgba;

    private Integer frameCount;

    private ArrayList<Mat> prevFrames;
    private Mat prevFrame;

    private String ProcessingMode = "";


    Mat newm;
    Mat rsnewm;
    Bitmap bmp = null;

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

    public void ReceiveCommand(String command){
        System.out.println("opencv command received");
        ProcessingMode = command;
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
//        mRgba = new Mat(height, width, CvType.CV_8UC4);
//        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
//        mGray = new Mat(height, width, CvType.CV_8UC1);
        newmcorners = new Mat(4,2, CvType.CV_32FC1);
        newmcorners.put(0,0,700);
        newmcorners.put(0,1,100);

        newmcorners.put(1,0,700);
        newmcorners.put(1,1,700);

        newmcorners.put(2,0,100);
        newmcorners.put(2,1,100);

        newmcorners.put(3,0,100);
        newmcorners.put(3,1,700);

        outerCorners = new Mat(3,4, CvType.CV_64FC1, new Scalar(0));
        outerCorners.put(0,0,800);
        outerCorners.put(1,0, 0);
        outerCorners.put(2,0, 1);

        outerCorners.put(0,1, 800);
        outerCorners.put(1,1, 800);
        outerCorners.put(2,1, 1);

        outerCorners.put(0,2, 0);
        outerCorners.put(1,2, 0);
        outerCorners.put(2,2, 1);

        outerCorners.put(0,3, 0);
        outerCorners.put(1,3, 800);
        outerCorners.put(2,3, 1);


        correctedmRgba = new Mat();
        correctedmGray = new Mat();

        prevFrames = new ArrayList<Mat>();
        prevFrame = new Mat();

        frameCount = 0;
        System.out.println("opencv started");
    }
    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        if(mRgba != null) {
            mRgba.release();
        }
        if (mGray != null) {
            mGray.release();
        }
        if (newm != null) {
            newm.release();
        }
        if (rsnewm != null) {
            rsnewm.release();
        }
        if (mIntermediateMat != null) {
            mIntermediateMat.release();
        }
        if (hemo != null){
            hemo.release();
        }

        mRgba = inputFrame.rgba().clone();
        mGray = inputFrame.gray().clone();
        newm = new Mat();
        rsnewm = new Mat();
        //Core.transpose(mGray, mGray);
        //Core.flip(mGray, mGray, -1); // rotates Mat to portrait

        switch (ProcessingMode) {
            case "CheckCorners":
                System.out.println("opencv I am checking corners");
                findInnerCorner(mGray);
                if (mIntermediateMat != null && !mIntermediateMat.empty()) {
                    String log = mIntermediateMat.dump();
                    perspective = Imgproc.getPerspectiveTransform(mIntermediateMat, newmcorners);
                    Imgproc.warpPerspective(mRgba, correctedmRgba, perspective, new Size(800, 800));
//                    bmp = Bitmap.createBitmap(correctedmRgba.cols(), correctedmRgba.rows(), Bitmap.Config.ARGB_8888);
//                    Utils.matToBitmap(correctedmRgba,bmp);
                    hemo = new Mat();
                    Core.gemm(perspective.inv().t(), outerCorners, 1,new Mat(), 0 , hemo, 1 );
                    outerCorner1 = hemo.col(0).mul(new Mat(3,1, CvType.CV_64FC1,
                            new Scalar(1)), 1/hemo.get(2,0)[0]);

                    outerCorner2 = hemo.col(1).mul(new Mat(3,1, CvType.CV_64FC1,
                            new Scalar(1)), 1/hemo.get(2,1)[0]);

                    outerCorner3 = hemo.col(2).mul(new Mat(3,1, CvType.CV_64FC1,
                            new Scalar(1)), 1/hemo.get(2,2)[0]);

                    outerCorner4 = hemo.col(3).mul(new Mat(3,1, CvType.CV_64FC1,
                            new Scalar(1)), 1/hemo.get(2,3)[0]);

                    String outerCorner1log = outerCorner1.dump();
                    String outerCorner2log = outerCorner2.dump();
                    String outerCorner3log = outerCorner3.dump();
                    String outerCorner4log = outerCorner4.dump();

                    Point point1 = new Point(Math.round(outerCorner1.get(0,0)[0]), Math.round(outerCorner1.get(1,0)[0]));
                    Point point2 = new Point(Math.round(outerCorner2.get(0,0)[0]), Math.round(outerCorner2.get(1,0)[0]));
                    Point point3 = new Point(Math.round(outerCorner3.get(0,0)[0]), Math.round(outerCorner3.get(1,0)[0]));
                    Point point4 = new Point(Math.round(outerCorner4.get(0,0)[0]), Math.round(outerCorner4.get(1,0)[0]));
                    Imgproc.line(mRgba, point1, point2,
                            new Scalar(255,0,0), 3);
                    Imgproc.line(mRgba, point2, point4,
                            new Scalar(255,0,0), 3);
                    Imgproc.line(mRgba, point4, point3,
                            new Scalar(255,0,0), 3);
                    Imgproc.line(mRgba, point3, point1,
                            new Scalar(255,0,0), 3);

                    perspective.release();
                    outerCorner1.release();
                    outerCorner2.release();
                    outerCorner3.release();
                    outerCorner4.release();
                }

                else {
                    System.out.println("opencv corners are empty");
                }
                break;
            case "SaveCorners":
                System.out.println("Just saving corners");
                findInnerCorner(mGray);
                if (!mIntermediateMat.empty()) {
                    perspective = Imgproc.getPerspectiveTransform(mIntermediateMat, newmcorners);
                    ProcessingMode = "";
                }
                else {
                    ProcessingMode = "SaveCorners";
                }
                break;

//            case "SaveInitialFrame":
//                System.out.println("opencv Saving initial frame");
//                InitialFrame = mGray.clone();
//                ProcessingMode = "";
//                break;
            case "SaveInitialFrameAndPredictMyMove":
                System.out.println("opencv Saving initial frame");
                InitialFrame = mGray.clone();
                InitialFrameRgba = mRgba.clone();
                ProcessingMode = "PredictMyMove";
                break;

            case "PredictMyMove":
                ProcessingMode = "WaitHandEnterScreen";
                break;

            case "WaitHandEnterScreen":
                WaitHandEnterScreen(mGray);
                break;

            case "WaitHandLeaveScreen":
                WaitHandLeaveScreen(mGray);
                break;

            case "GetMovePrediction":
                GetMovePrediction(mGray);
                break;
            default:
                System.out.println("opencv Currently not processing anything, on standby mode: " + ProcessingMode);
        }
        System.out.println("opencv camera frame"+ mRgba.size().toString());



        // if inner corner found. Draw outer corners and render preview
//        if (correctedmRgba != null && !correctedmRgba.empty()){
//
//            Core.rotate(correctedmRgba, newm, Core.ROTATE_90_CLOCKWISE);
//            Imgproc.resize(newm, rsnewm, mRgba.size());
//            return rsnewm;
//        }


        //Rotate and resize preview
        Core.rotate(mRgba, newm, Core.ROTATE_90_CLOCKWISE);
        Imgproc.resize(newm, rsnewm, mRgba.size());
        return rsnewm;
    }

    private void GetMovePrediction(Mat mGray) {
        System.out.println("Getting Move prediction");
        Mat correctedmInitial = new Mat();
        Mat result = new Mat();
        Mat correctedDiff = new Mat();
        Mat dst = new Mat();
        Mat threshHolded = new Mat();
        Mat blurred = new Mat();
        Mat adaptivethreshHolded = new Mat();
        Mat OtsuthreshHolded = new Mat();

        Imgproc.warpPerspective(mGray, correctedmGray, perspective, new Size(800, 800));
        Imgproc.warpPerspective(InitialFrame, correctedmInitial, perspective, new Size(800, 800));
        Imgproc.warpPerspective(InitialFrameRgba, correctedmRgba, perspective, new Size(800, 800));

        bmp = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Bitmap bmp3 = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Bitmap bmp4 = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Bitmap bmp2 = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        /**
         * Create diff image. Apply closing operation
         * */
        Core.absdiff(mGray, InitialFrame, result);
        Mat kernel = Mat.ones(10,10,CvType.CV_32F);
        Imgproc.morphologyEx(result, dst, Imgproc.MORPH_CLOSE, kernel);
        //Imgproc.morphologyEx(result, dst, Imgproc.MORPH_OPEN, kernel);

        /**
         * Correct perspective
         * */
        Imgproc.warpPerspective(dst, correctedDiff, perspective, new Size(800,800));

        /**
         * threshholded and median blurred
         */
        Imgproc.threshold(correctedDiff, threshHolded, 70, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        Imgproc.medianBlur(threshHolded, blurred, 5);
        Utils.matToBitmap(blurred, bmp);

        /**
         * Find contour
         */
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchey = new Mat();
        Imgproc.findContours(blurred, contours, hierarchey ,Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(correctedmRgba, contours, -1, new Scalar(0, 0, 255), 2, Imgproc.LINE_8, hierarchey, 2, new Point());
        Utils.matToBitmap(correctedmRgba, bmp3);


        /**
         * Find canny on original
         */
        List<MatOfPoint> contours2 = new ArrayList<>();
        Mat hierarchy2 = new Mat();
        Mat blurred2 = new Mat();
        Mat cannyed = new Mat();
        Imgproc.GaussianBlur(correctedmInitial, blurred2,new Size(7,7), 0 ,0);
        Utils.matToBitmap(blurred2, bmp3);

        Imgproc.Canny(blurred2, cannyed, 80, 100, 3);
        Utils.matToBitmap(cannyed, bmp3);

        /**
         * Find contour on cannyed
         */
        Imgproc.findContours(cannyed, contours2, hierarchy2 ,Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE );
        //Imgproc.drawContours(correctedmRgba, contours2, -1, new Scalar(0, 0, 0, 0), 3, Imgproc.LINE_8, hierarchy2, 2, new Point());
        //Utils.matToBitmap(correctedmRgba, bmp4);


        //Canny edge and contour con original. Then match shape.
        /**
         * Match shape contour
         */
        List<Double> rets = new ArrayList<>();
        for (MatOfPoint c : contours2){
            double ret = Imgproc.matchShapes(c, contours.get(0), 1, 0);
            if (ret < 0.1){
                rets.add(ret);
                System.out.println("opencv ret:" + ret);
                List<MatOfPoint> cs = new ArrayList<>();
                cs.add(c);
                //
                Imgproc.drawContours(correctedmRgba, cs, -1, new Scalar(0, 0, 255, 255), 3, Imgproc.LINE_8, new Mat(), 2, new Point());
                Utils.matToBitmap(correctedmRgba, bmp4);
            }
        }



        //get color move coordinates
        ArrayList<Point> squareArr = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Rect reg = new Rect(j * 100, i * 100 , 100 , 100);
                Mat square = new Mat(blurred, reg);
                Utils.matToBitmap(square,bmp2);
                if(Core.mean(square).val[0] > 7000){
                    double val = Core.mean(square).val[0];
                    squareArr.add(new Point(j, i));
                }
                square.release();
            }
        }
        // separate





        result.release();
        correctedDiff.release();
        correctedmInitial.release();
        kernel.release();
        dst.release();
        threshHolded.release();
        adaptivethreshHolded.release();
        blurred.release();
        hierarchey.release();

    }

    private void WaitHandLeaveScreen(Mat mGray) {
        System.out.printf("opencv: waiting for hand leave screen");
        Imgproc.warpPerspective(mGray, correctedmGray, perspective, new Size(800, 800));
        if (frameCount < 5) {
            frameCount ++;
            return;
        }

        //compute av difference with preFrames
        // if av diff smaller some threshhold, the board has become stable again. i.e: no hands etc
        double av = 0f;
        for (Mat frame: prevFrames){
            Mat result = new Mat();
            Core.absdiff(correctedmGray, frame, result);
            Scalar mean = Core.mean(result);
            //String logresult = result.dump();
            result.release();
            av += mean.val[0];
        }
        av = av / prevFrames.size();
        System.out.println("opencv wait for hand leave screen average: " + av);


        if(prevFrames.size() < 5){
            prevFrames.add(correctedmGray.clone());
        }
        else {
            // If av < 1, board view is stable, ready for next move.
            if (av < 1) {
                System.out.println("opencv hand left the screen: " + av);
                ProcessingMode = "GetMovePrediction";
                for (Mat frame: prevFrames){
                    frame.release();
                }
            }
            else {
                System.out.println("opencv hand still in the screen: " + av);
                prevFrames.get(0).release();
                prevFrames.remove(0);
                prevFrames.add(correctedmGray.clone());
            }
        }
        frameCount = 0;
    }

    private void WaitHandEnterScreen(Mat mGray) {
        System.out.println("opencv: waiting for hand enter screen");
        Imgproc.warpPerspective(mGray, correctedmGray, perspective, new Size(800, 800));
        //for debugging
        bmp = Bitmap.createBitmap(correctedmGray.cols(), correctedmGray.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(correctedmGray,bmp);
        //
        if(prevFrame.empty()){
            prevFrame = correctedmGray.clone();
        }
        else{
            Mat result = new Mat();
            Core.absdiff(correctedmGray, prevFrame, result);
            Scalar mean = Core.mean(result);
            //String logresult = result.dump();

            //if diff too big => hand enter screen
            if (mean.val[0] > 6){
                System.out.println("opencv: Hand has enter screen");
                ProcessingMode = "WaitHandLeaveScreen";
                prevFrame.release();
                correctedmGray.release();
            }
            else {
                System.out.println("opencv: hand is not on screen, diff: "+ mean.val[0]);
                prevFrame = correctedmGray.clone();
                correctedmGray.release();
            }
        }

    }

    public CameraBridgeViewBase getJavaCameraInstance() {
        return cameraBridgeViewBase;
    }

    public void findInnerCorner(Mat mat) {
        corners = new MatOfPoint2f();
        boolean found = Calib3d.findChessboardCorners(mat, new Size(7, 7), corners, Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE);
       // Calib3d.drawChessboardCorners(mat, new Size(7,7), corners, found);

        if(!found) return;

        String log =  corners.dump();
        System.out.println("opencv corner Size: " );

        mIntermediateMat = new Mat(4,2, CvType.CV_32FC1);


        double[] corner1 = corners.get(0,0);
        double[] corner2 = corners.get(6,0);
        double[] corner3 = corners.get(42,0);
        double[] corner4 = corners.get(48,0);

        mIntermediateMat.put(0, 0,  (int) Math.round(corner1[0]));
        mIntermediateMat.put(0, 1,  (int)Math.round(corner1[1]));

        mIntermediateMat.put(1, 0,  (int)Math.round(corner2[0]));
        mIntermediateMat.put(1, 1,  (int)Math.round(corner2[1]));

        mIntermediateMat.put(2, 0,  (int)Math.round(corner3[0]));
        mIntermediateMat.put(2, 1,  (int)Math.round(corner3[1]));

        mIntermediateMat.put(3, 0,  (int)Math.round(corner4[0]));
        mIntermediateMat.put(3, 1,  (int)Math.round(corner4[1]));

        String logm = mIntermediateMat.dump();
        System.out.println("opencv inside" + mIntermediateMat);
        corners.release();

    }


}