package com.nativeappreact;
import static com.nativeappreact.utils.yuv420ToBitmap;



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

    public void print(Mat mat){
        UByteRawIndexer sI = mat.createIndexer();
        List<List<Integer>> values = new ArrayList<>();

        for(int y = 0; y < mat.rows(); y++){
            List<Integer> rows = new ArrayList<>();
            for (int x = 0; x < mat.cols(); x ++){
                rows.add(sI.get(y,x));
            }
            values.add(rows);
        }

        System.out.println("opencv " + values);
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Nullable
    @Override
    public Object callback(@NonNull androidx.camera.core.ImageProxy image, @NonNull Object[] params) {

        Instant start = Instant.now();
        @SuppressLint("UnsafeOptInUsageError") Bitmap bmp = yuv420ToBitmap(image.getImage(), context);
        System.out.println("worked here opencv");
        AndroidFrameConverter converterToFrame = new AndroidFrameConverter();
        Frame frame = converterToFrame.convert(bmp);
        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
        Mat mat = converterToMat.convert(frame);

        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();

        return "hello" + mat.toString() + timeElapsed;
    }
}
