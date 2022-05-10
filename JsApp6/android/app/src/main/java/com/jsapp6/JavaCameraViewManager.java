package com.jsapp6;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Pair;
import android.view.SurfaceView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.Utils;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
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
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfRect;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.BOWTrainer;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.features2d.FastFeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.calib3d.Calib3d;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JavaCameraViewManager extends SimpleViewManager<FrameLayout>
        implements CameraBridgeViewBase.CvCameraViewListener2 {
    ReactContext context;
    public static final String REACT_CLASS = "JavaCameraView";
    private final int ME = 0;
    private final int OPPONENT = 1;
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
    private int turn = ME;

    private Integer frameCount;

    private ArrayList<Mat> prevFrames;
    private Mat prevFrame;

    private String ProcessingMode = "";


    Mat newm;
    Mat rsnewm;
    Bitmap bmp = null;

    public JavaCameraViewManager(ReactContext context) {
        this.context = context;
    }

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
                findInnerCorner(mRgba);
                if (mIntermediateMat != null && !mIntermediateMat.empty()) {
                    String log = mIntermediateMat.dump();
                    perspective = Imgproc.getPerspectiveTransform(mIntermediateMat, newmcorners);
                    Imgproc.warpPerspective(mRgba, correctedmRgba, perspective, new Size(800, 800));

                    bmp = Bitmap.createBitmap(correctedmRgba.cols(), correctedmRgba.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(correctedmRgba,bmp);

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

                    Bitmap debug = Bitmap.createBitmap(mRgba.cols(), mRgba.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(mRgba,debug);

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

            case "SaveInitialFrameAndPredictMyMove":
                System.out.println("opencv Saving initial frame");
                InitialFrame = mGray.clone();
                InitialFrameRgba = mRgba.clone();
                ProcessingMode = "PredictMyMove";
                break;

            case "PredictMyMove":
                if (InitialFrame == null) {
                    InitialFrame = mGray.clone();
                }
                ProcessingMode = "WaitHandEnterScreen";
                break;

            case "WaitHandEnterScreen":
                if(!prevFrames.isEmpty()) {
                    for (int i = 0; i < prevFrames.size(); i++) {
                        prevFrames.get(i).release();
                        prevFrames.remove(i);
                    }
                }
                WaitHandEnterScreen(mGray);
                break;

            case "WaitHandLeaveScreen":
                WaitHandLeaveScreen(mGray);
                break;

            case "GetMovePrediction":
                GetMovePrediction(mGray);
                ProcessingMode = "";
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

    private void GetMovePrediction(Mat mGray){
        System.out.println("Getting Move prediction");
        ArrayList<Pair<String,Float>> moves = new ArrayList<>();
        Mat result = new Mat();
        Mat correctedDiff = new Mat();
        Mat correctedmInitial = new Mat();
        Mat dst = new Mat();
        Mat threshHolded = new Mat();
        Mat blurred = new Mat();
        bmp = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);

        Bitmap debug2 = Bitmap.createBitmap(mRgba.width(), mRgba.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mRgba, debug2);

        Bitmap debug3 = Bitmap.createBitmap(mRgba.width(), mRgba.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(InitialFrameRgba, debug3);
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
        Bitmap debug1 = Bitmap.createBitmap(dst.width(), dst.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, debug1);

        Bitmap debug5 = Bitmap.createBitmap(correctedDiff.width(), correctedDiff.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(correctedDiff, debug5);

        /**
         * threshholded and median blurred
         */
        Imgproc.threshold(correctedDiff, threshHolded, 70, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
        Imgproc.medianBlur(threshHolded, blurred, 5);
        Utils.matToBitmap(blurred, bmp);

        Mat blurredrgb = new Mat();
        Imgproc.cvtColor(blurred, blurredrgb, Imgproc.COLOR_GRAY2BGR);


        for (int i = 0; i < 8; i++) {
            Imgproc.line(blurredrgb, new Point(100*i, 0), new Point(100*i,799),
                    new Scalar(0,255,0), 1);
            Imgproc.line(blurredrgb, new Point(0, 100*i), new Point(799,100*i),
                    new Scalar(0,255,0), 1);
        }

        Utils.matToBitmap(blurredrgb, bmp);




        //get color move coordinates
        ArrayList<Point> squareArr = new ArrayList<>();
        ArrayList<Point> squares1 = new ArrayList<>();
        ArrayList<Point> squares2 = new ArrayList<>();
        boolean found = false;
        int count = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                while(j < 8 && currentSquareContainsWhite(blurred,j,i)) {
                    found = true;
                    if (count == 0) {
                        squares1.add(new Point(j,i));
                    }
                    else {
                        squares2.add(new Point(j,i));
                    }

                    j++;
                }
                if (found) {
                    count = 1;
                }
            }
        }


        /**
         * Feature matching
         */
        Bitmap bmpresult = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Bitmap bmpgray = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Bitmap bmpInitial = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Mat correctedResult = new Mat();
        Imgproc.warpPerspective(result, correctedResult, perspective, new Size(800, 800));
        Imgproc.warpPerspective(mGray, correctedmGray, perspective, new Size(800, 800));
        Imgproc.warpPerspective(InitialFrame, correctedmInitial, perspective, new Size(800, 800));

        Utils.matToBitmap(correctedResult, bmpresult);
        Utils.matToBitmap(correctedmGray, bmpgray);
        Utils.matToBitmap(correctedmInitial, bmpInitial);

        int reg1Len = squares1.size();
        int reg2Len = squares2.size();

        if (reg2Len > 0) {
            Rect reg1 = new Rect((int) squares1.get(0).x * 100, (int) squares1.get(0).y * 100, 100 * reg1Len, 100);
            Rect reg2 = new Rect((int) squares2.get(0).x * 100, (int) squares2.get(0).y * 100, 100 * reg2Len, 100);

            Mat template1 = new Mat(correctedResult, reg1);
            Mat template2 = new Mat(correctedResult, reg2);
            Mat afterMove1 = new Mat(correctedmGray, reg1);
            Mat afterMove2 = new Mat(correctedmGray, reg2);
            Mat beforeMove1 = new Mat(correctedmInitial, reg1);
            Mat beforeMove2 = new Mat(correctedmInitial, reg2);
            Point From;
            Point To;
            String move;
            Float Reg1IsAfterMove = DetectFeatures(template1, afterMove1) - 0.3f * DetectFeatures(template2, afterMove2);
            Float Reg1IsBeforeMove = DetectFeatures(template1, beforeMove1) - 0.3f * DetectFeatures(template2, beforeMove2);
            if (Reg1IsAfterMove > Reg1IsBeforeMove) {
                To = squares1.get(reg1Len-1);
                From = squares2.get(reg2Len-1);
                move = ComputeChessCoordinate(From, To);
                moves.add(new Pair<>(move, Reg1IsAfterMove));
            }
            else {
                To = squares2.get(reg2Len-1);
                From = squares1.get(reg1Len-1);
                move = ComputeChessCoordinate(From, To);
                moves.add(new Pair<>(move, Reg1IsBeforeMove));
            }
            template1.release();
            template2.release();
            afterMove1.release();
            afterMove2.release();
            beforeMove1.release();
            beforeMove2.release();
        }

        /**
         * When the 2 regions connected.
         */
        else {
            Point From;
            Point To;
            String move;
            System.out.println("2 regions connected");
            From = squares1.get(reg1Len - 1);
            To = squares1.get(reg1Len - 2);
            move = ComputeChessCoordinate(From, To);
            moves.add(new Pair<>(move, 1f));
            move = ComputeChessCoordinate(To, From);
            moves.add(new Pair<>(move, 1f));
        }

        Playmove(moves);

        result.release();
        correctedDiff.release();
        correctedmInitial.release();
        kernel.release();
        dst.release();
        threshHolded.release();
        blurred.release();

    }

    private void Playmove(ArrayList<Pair<String, Float>> moves) {
        WritableMap params = Arguments.createMap();
        JSONArray ja = new JSONArray();
        Integer index = 0;
        for (Pair<String,Float> pair: moves) {
            JSONObject jo = new JSONObject();
            try {
                jo.put("move", pair.first);
                jo.put("prob", pair.second);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ja.put(jo);
        }
        params.putString("moves", ja.toString());
        JavaCameraControlModule.sendEvent(context, "MovePredictionFound", params);
    }


    private String ComputeChessCoordinate(Point from, Point to) {
        HashMap<Integer, String> Files = new HashMap<>();
        Files.put(1,"a");
        Files.put(2,"b");
        Files.put(3,"c");
        Files.put(4,"d");
        Files.put(5,"e");
        Files.put(6,"f");
        Files.put(7,"g");
        Files.put(8,"h");
        int rankFrom = (int) (from.y + 1);
        int rankTo = (int) (to.y + 1);

        int fileTo = (int) (7 - to.x + 1);
        int fileFrom = (int) (7 - from.x + 1);
        String fileStrFrom = Files.get(fileFrom);
        String fileStrTo = Files.get(fileTo);
        return fileStrFrom + rankFrom + "to" + fileStrTo + rankTo;
    }

    private Float DetectFeatures(Mat template, Mat obj) {
        Float certainty;
        Bitmap bmpgray = Bitmap.createBitmap(obj.width(), obj.height(), Bitmap.Config.ARGB_8888);
        Bitmap bmptemplate = Bitmap.createBitmap(template.width(), template.height(), Bitmap.Config.ARGB_8888);
        Mat cannyedobj = new Mat();
        Mat cannyedtemplate = new Mat();
        Imgproc.Canny(obj, cannyedobj, 80, 100, 3);
        Imgproc.Canny(template, cannyedtemplate, 80, 100, 3);
        Utils.matToBitmap(cannyedobj, bmpgray);
        Utils.matToBitmap(cannyedtemplate, bmptemplate);
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

        //OBJ
        ORB featureDetector = ORB.create();
        MatOfKeyPoint objectKeyPoints = new MatOfKeyPoint();
        Mat descriptors = new Mat();
        featureDetector.detect(cannyedobj, objectKeyPoints);
        featureDetector.compute(cannyedobj, objectKeyPoints, descriptors);

        //template
        MatOfKeyPoint templateKeyPoints = new MatOfKeyPoint();
        Mat templatedescriptors = new Mat();
        featureDetector.detect(cannyedtemplate, templateKeyPoints);
        featureDetector.compute(cannyedtemplate, templateKeyPoints, templatedescriptors);

        // Matching
        List<MatOfDMatch> matches = new LinkedList<MatOfDMatch>();
        LinkedList<DMatch> goodMatchesList = new LinkedList<DMatch>();
        matcher.knnMatch(descriptors, templatedescriptors, matches, 2);


        Scalar RED = new Scalar(255,0,0);
        Scalar GREEN = new Scalar(0,255,0);
        float nndrRatio = 0.9f;

        for (int i = 0; i < matches.size(); i++) {
            MatOfDMatch matofDMatch = matches.get(i);
            DMatch[] dmatcharray = matofDMatch.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];

            if (m1.distance <= m2.distance * nndrRatio) {
                goodMatchesList.addLast(m1);
            }
        }
        Mat outImg = new Mat();
        if (goodMatchesList.size() >= 10) {
            String rs = "object found";
            MatOfDMatch goodMatches = new MatOfDMatch();
            goodMatches.fromList(goodMatchesList);
            Features2d.drawMatches(obj, objectKeyPoints, template, templateKeyPoints, goodMatches, outImg, RED, GREEN, new MatOfByte(), Features2d.NOT_DRAW_SINGLE_POINTS);
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            String filename = "featuresdetected.png";
            File file = new File(path, filename);

            Boolean bool = null;
            filename = file.toString();
            bool = Imgcodecs.imwrite(filename, outImg);

            if (bool == true) {

                Log.d("opencv", "SUCCESS writing image to external storage");
            }
            else{
                Log.d("opencv", "Fail writing image to external storage");
            }

            certainty = goodMatchesList.size() / 15f;
            Bitmap bmpoutimg = Bitmap.createBitmap(outImg.width(), outImg.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(outImg, bmpoutimg);

        }

        certainty = goodMatchesList.size() / 15f;
        System.out.println("done");
        return certainty;
    }

    private boolean currentSquareContainsWhite(Mat blurred, int j, int i) {
        Bitmap bmpsquare = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        Rect reg = new Rect(j * 100, i * 100 , 100 , 100);
        Mat square = new Mat(blurred, reg);
        double[] val = Core.mean(square).val;
        Utils.matToBitmap(square, bmpsquare);
        if (Core.mean(square).val[0]/255 > 0.15) {
            square.release();
            return true;
        }
        square.release();
        return false;
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
                if(turn == ME) {
                    ProcessingMode = "GetMovePrediction";
                    turn = OPPONENT;
                }
                else {
                    ProcessingMode = "SaveInitialFrameAndPredictMyMove";
                    turn = ME;
                }
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
        //for debugging
        bmp = Bitmap.createBitmap(mGray.width(), mGray.height(), Bitmap.Config.ARGB_8888);
        Bitmap bmpResult = Bitmap.createBitmap(mGray.width(), mGray.height(), Bitmap.Config.ARGB_8888);
        Bitmap bmpCorrectedResult = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Bitmap bmpdst = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Bitmap bmpBlurred = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
        Bitmap bmpThreshHolded = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888);
//        //
//        if(prevFrame.empty()){
//            prevFrame = mGray.clone();
//        }
            Mat dst = new Mat();
            Mat correctedResult = new Mat();
            Mat result = new Mat();
            Mat threshHolded = new Mat();
            Mat blurred = new Mat();
            Core.absdiff(mGray, InitialFrame, result);
            Imgproc.warpPerspective(result, correctedResult, perspective, new Size(800, 800));
            Mat kernel = Mat.ones(10,10,CvType.CV_32F);
            Imgproc.morphologyEx(correctedResult, dst, Imgproc.MORPH_CLOSE, kernel);
            Imgproc.threshold(dst, threshHolded, 70, 255, Imgproc.THRESH_BINARY);
            Imgproc.medianBlur(threshHolded, blurred, 5);

            Utils.matToBitmap(result, bmpResult);
            Utils.matToBitmap(correctedResult, bmpCorrectedResult);
            Utils.matToBitmap(dst, bmpdst);
            Utils.matToBitmap(threshHolded, bmpThreshHolded);
            Utils.matToBitmap(blurred, bmpBlurred);

            Scalar mean = Core.mean(blurred);
            //String logresult = result.dump();

            //if diff too big => hand enter screen
            if (mean.val[0] > 5){
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

    public CameraBridgeViewBase getJavaCameraInstance() {
        return cameraBridgeViewBase;
    }

    public void findInnerCorner(Mat mat) {
        corners = new MatOfPoint2f();
        boolean found = Calib3d.findChessboardCorners(mat, new Size(7, 7), corners, Calib3d.CALIB_CB_ADAPTIVE_THRESH + Calib3d.CALIB_CB_NORMALIZE_IMAGE);
        Calib3d.drawChessboardCorners(mRgba, new Size(7,7), corners, found);

        Bitmap debug = Bitmap.createBitmap(mRgba.width(), mRgba.height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mRgba, debug);

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