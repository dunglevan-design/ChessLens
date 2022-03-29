package com.nativeappreact;
import static com.nativeappreact.utils.yuv420ToBitmap;
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

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;

import org.bytedeco.opencv.global.opencv_imgcodecs;
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









        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();

        return "hello" + timeElapsed;
    }
}
