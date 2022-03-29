package com.nativeappreact;

import static com.nativeappreact.utils.yuv420ToBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;

import com.facebook.react.bridge.ReactApplicationContext;
import com.google.gson.Gson;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

import org.bytedeco.javacpp.indexer.DoubleRawIndexer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_calib3d;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

import java.util.ArrayList;
import java.util.List;

import expo.modules.core.MapHelper;

public class CameraCheckPlugin extends FrameProcessorPlugin {
    /**
     * Initializes the native plugin part.
     *
     * Specifies the Frame Processor Plugin's name in the Runtime.
     *             The actual name in the JS Runtime will be prefixed with two underscores (`__`)
     * @param reactContext
     */
    Context context;
    CameraCheckPlugin(ReactApplicationContext reactContext) {
        super("CheckCamera");
        this.context = reactContext;
    }

    /**]
     *
     * @param image The CameraX ImageProxy. Don't call .close() on this, as VisionCamera handles that.
     * @param params
     * @return array of corners coordinates.
     */
    @Nullable
    @Override
    public Object callback(@NonNull ImageProxy image, @NonNull Object[] params) {
        @SuppressLint("UnsafeOptInUsageError") Bitmap bmp = yuv420ToBitmap(image.getImage(), context);
        System.out.println("worked here opencv");
        AndroidFrameConverter converterToFrame = new AndroidFrameConverter();
        Frame frame = converterToFrame.convert(bmp);
        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
        Mat mat = converterToMat.convert(frame);

        Mat corners = findCorner(mat);
        if(corners.empty()) {
            System.out.println("empty corners");
            int [][] placeholders = {{0,0}, {0,0}, {0,0}, {0,0}};
            return placeholders;
        }


        /** Perspective transform */
        Mat newmat = new Mat(4,2, opencv_core.CV_32F, new Scalar(0));
        FloatRawIndexer indexer = newmat.createIndexer();
        indexer.put(0,0, 700);
        indexer.put(0,1, 100);

        indexer.put(1,0, 700);
        indexer.put(1,1, 700);

        indexer.put(2,0, 100);
        indexer.put(2,1, 100);

        indexer.put(3,0, 100);
        indexer.put(3,1, 700);
        Mat perspective = opencv_imgproc.getPerspectiveTransform(corners, newmat);
        Mat transformed = new Mat();
        opencv_imgproc.warpPerspective(mat, transformed, perspective, new Size(800, 800));

        /**
         * Inverse warpPerspective to find outer corners
         */
        Mat outerCorners = new Mat(3,4, opencv_core.CV_64FC1, new Scalar(0));
        Mat RevertedouterCorners = new Mat(3,4, opencv_core.CV_64FC1, new Scalar(0));

        DoubleRawIndexer outerCornerIndexer = outerCorners.createIndexer();
        DoubleRawIndexer RevertedouterCornerIndexer = RevertedouterCorners.createIndexer();

        outerCornerIndexer.put(0,0, 800);
        outerCornerIndexer.put(1,0, 0);
        outerCornerIndexer.put(2,0, 1);

        outerCornerIndexer.put(0,1, 800);
        outerCornerIndexer.put(1,1, 800);
        outerCornerIndexer.put(2,1, 1);

        outerCornerIndexer.put(0,2, 0);
        outerCornerIndexer.put(1,2, 0);
        outerCornerIndexer.put(2,2, 1);

        outerCornerIndexer.put(0,3, 0);
        outerCornerIndexer.put(1,3, 800);
        outerCornerIndexer.put(2,3, 1);

//                Mat testpoint = new Mat(3, 1, opencv_core.CV_64FC1, new Scalar(0));
//                DoubleRawIndexer dindexer = testpoint.createIndexer();
//                dindexer.put(0,0, 0);
//                dindexer.put(1, 0, 0);
//                dindexer.put(2,0,1);

        Mat hemo = new Mat();
        opencv_core.gemm(perspective.inv().asMat().t().asMat(), outerCorners,
                1, new Mat(), 0, hemo,
                1);
        DoubleRawIndexer hemoindexer = hemo.createIndexer();
        Mat outercorner1 = hemo.col(0).mul(new Mat(3,1, opencv_core.CV_64FC1,
                new Scalar(1)), 1/hemoindexer.get(2,0)).asMat();

        Mat outercorner2 = hemo.col(1).mul(new Mat(3,1, opencv_core.CV_64FC1,
                new Scalar(1)), 1/hemoindexer.get(2,1)).asMat();

        Mat outercorner3 = hemo.col(2).mul(new Mat(3,1, opencv_core.CV_64FC1,
                new Scalar(1)), 1/hemoindexer.get(2,2)).asMat();

        Mat outercorner4 = hemo.col(3).mul(new Mat(3,1, opencv_core.CV_64FC1,
                new Scalar(1)), 1/hemoindexer.get(2,3)).asMat();
        DoubleRawIndexer outercorner1indexer = outercorner1.createIndexer();
        DoubleRawIndexer outercorner2indexer = outercorner2.createIndexer();
        DoubleRawIndexer outercorner3indexer = outercorner3.createIndexer();
        DoubleRawIndexer outercorner4indexer = outercorner4.createIndexer();

        int[][] outerCornersArr = {
                {(int)Math.round(outercorner1indexer.get(0,0)), (int)Math.round(outercorner1indexer.get(1,0))},
                {(int)Math.round(outercorner2indexer.get(0,0)), (int)Math.round(outercorner2indexer.get(1,0))},
                {(int)Math.round(outercorner3indexer.get(0,0)), (int)Math.round(outercorner3indexer.get(1,0))},
                {(int)Math.round(outercorner4indexer.get(0,0)), (int)Math.round(outercorner4indexer.get(1,0))},
        };
        return new Gson().toJson(outerCornersArr);
    }

    public Bitmap MattobitmapConvert(Mat mat){

        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
        Frame frame = converterToMat.convert(mat);
        AndroidFrameConverter converterToBitmap = new AndroidFrameConverter();
        Bitmap bitmap = converterToBitmap.convert(frame);

        return bitmap;
    }

    public Mat findCorner(Mat mat){
        Mat Points = new Mat(4,2, opencv_core.CV_32F);
        FloatRawIndexer PointIndexer = Points.createIndexer();
        Mat corners = new Mat();
        boolean found = opencv_calib3d.findChessboardCorners(mat, new Size(7, 7),
                corners, opencv_calib3d.CALIB_CB_ADAPTIVE_THRESH + opencv_calib3d.CALIB_CB_NORMALIZE_IMAGE);

        if (!found) return corners;
        opencv_calib3d.drawChessboardCorners(mat, new Size(7,7), corners, found);

        int x;
        int y;
        Point corner1, corner2, corner3, corner4;
        FloatRawIndexer indexer = corners.createIndexer();
        x = (int) indexer.get(0, 0, 0);
        y = (int) indexer.get(0, 0, 1);
        corner1 = new Point(x,y);

        x = (int) indexer.get(6, 0, 0);
        y = (int) indexer.get(6, 0, 1);
        corner2 = new Point(x,y);
//
        x = (int) indexer.get(42, 0, 0);
        y = (int) indexer.get(42, 0, 1);
        corner3 = new Point(x,y);

        x = (int) indexer.get(48, 0, 0);
        y = (int) indexer.get(48, 0, 1);
        corner4 = new Point(x,y);

        PointIndexer.put(0,0, corner1.x());
        PointIndexer.put(0,1, corner1.y());

        PointIndexer.put(1,0,corner2.x());
        PointIndexer.put(1,1,corner2.y());

        PointIndexer.put(2,0, corner3.x());
        PointIndexer.put(2,1, corner3.y());

        PointIndexer.put(3, 0, corner4.x());
        PointIndexer.put(3, 1, corner4.y());

        return Points;
    }

//
//    public ArrayList<ArrayList<Float>> findCorner(Mat mat){
//        ArrayList<ArrayList<Float>> Points = new ArrayList<>();
//        Mat corners = new Mat();
//        boolean found = opencv_calib3d.findChessboardCorners(mat, new Size(7, 7),
//                corners, opencv_calib3d.CALIB_CB_ADAPTIVE_THRESH + opencv_calib3d.CALIB_CB_NORMALIZE_IMAGE);
//        opencv_calib3d.drawChessboardCorners(mat, new Size(7,7), corners, found);
//
//        Float x;
//        Float y;
//        ArrayList<Float> corner1 = new ArrayList<>(), corner2 = new ArrayList<>() , corner3 = new ArrayList<>(), corner4 = new ArrayList<>();
//        FloatRawIndexer indexer = corners.createIndexer();
//
//        x = indexer.get(0, 0, 0);
//        y = indexer.get(0, 0, 1);
//        corner1.add(x);
//        corner1.add(y);
//
//        x = indexer.get(6, 0, 0);
//        y = indexer.get(6, 0, 1);
//        corner2.add(x);
//        corner2.add(y);
////
//        x = indexer.get(42, 0, 0);
//        y = indexer.get(42, 0, 1);
//        corner3.add(x);
//        corner3.add(y);
//
//        x = indexer.get(48, 0, 0);
//        y = indexer.get(48, 0, 1);
//        corner4.add(x);
//        corner4.add(y);
//
//        Points.add(corner1);
//        Points.add(corner2);
//        Points.add(corner3);
//        Points.add(corner4);
//
//        return Points;
//    }

}
