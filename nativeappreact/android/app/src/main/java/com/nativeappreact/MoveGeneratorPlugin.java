package com.nativeappreact;
import static com.nativeappreact.utils.MattobitmapConvert;
import static com.nativeappreact.utils.yuv420ToMat;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.ImageProxy;

import com.facebook.react.bridge.ReactApplicationContext;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

import org.bytedeco.javacpp.indexer.DoubleRawIndexer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;

import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Size;
import org.opencv.imgproc.Imgproc;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MoveGeneratorPlugin extends FrameProcessorPlugin{

    Context context;
    public MoveGeneratorPlugin(
            ReactApplicationContext context
    ) {
        super("GenerateMove");
        this.context = context.getApplicationContext();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public Object callback(@NonNull androidx.camera.core.ImageProxy image, @NonNull Object[] params) {

        Instant start = Instant.now();
        ImageProxy prevImage = (ImageProxy) params[0];
        @SuppressLint("UnsafeOptInUsageError") Mat currFrame = yuv420ToMat(image.getImage(), context);
        @SuppressLint("UnsafeOptInUsageError") Mat prevFrame = yuv420ToMat(prevImage.getImage(), context);

        /**
         * Perspective transform
         */

        Mat corners = makeCornerMat((Double) params[1], (Double) params[2], (Double) params[3], (Double) params[4], (Double) params[5],
                (Double) params[6], (Double) params[7], (Double) params[8]);

        Mat newmat = new Mat(4,2, opencv_core.CV_64FC1, new Scalar(0));
        DoubleRawIndexer indexer = newmat.createIndexer();
        indexer.put(0,0, 800);
        indexer.put(0,1, 0);

        indexer.put(1,0, 800);
        indexer.put(1,1, 800);

        indexer.put(2,0, 0);
        indexer.put(2,1, 0);

        indexer.put(3,0, 0);
        indexer.put(3,1, 800);

        Mat perspective = opencv_imgproc.getPerspectiveTransform(corners, newmat);
        Mat birdeyeCurrFrame = new Mat();
        Mat birdeyePrevFrame = new Mat();
        opencv_imgproc.warpPerspective(currFrame, birdeyeCurrFrame, perspective, new Size(800, 800));
        opencv_imgproc.warpPerspective(prevFrame, birdeyePrevFrame, perspective, new Size(800, 800));

        Bitmap bmpcurr = MattobitmapConvert(birdeyeCurrFrame);
        Bitmap bmpprev = MattobitmapConvert(birdeyePrevFrame);



        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();

        return "hello" + timeElapsed;
    }

    public Mat makeCornerMat(Double corner1x, Double corner1y, Double corner2x, Double corner2y, Double corner3x,  Double corner3y, Double corner4x, Double corner4y){
        Mat corners = new Mat(4,2, opencv_core.CV_64FC1, new Scalar(0));
        DoubleRawIndexer cornersIndexer = corners.createIndexer();
        cornersIndexer.put(0,0, corner1x);
        cornersIndexer.put(0,1, corner1y);

        cornersIndexer.put(1,0, corner2x);
        cornersIndexer.put(1,1, corner2y);

        cornersIndexer.put(2,0, corner3x);
        cornersIndexer.put(2,1, corner3y);

        cornersIndexer.put(3,0, corner4x);
        cornersIndexer.put(3,1, corner4y);

        return corners;
    }
}
