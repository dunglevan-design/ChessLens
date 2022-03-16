package com.nativeappreact;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.ImageProxy;
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Scalar;

import java.util.ArrayList;
import java.util.List;

public class MoveGeneratorPlugin extends FrameProcessorPlugin{

    public MoveGeneratorPlugin() {
        super("GenerateMove");
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


    @Nullable
    @Override
    public Object callback(@NonNull androidx.camera.core.ImageProxy image, @NonNull Object[] params) {
        Mat testingmat = new Mat(5,1, opencv_core.CV_8UC1 ,new Scalar(1));
        print(testingmat);

        System.out.println(testingmat);
        return "This is from the Move generator Plugnah I am kiddingin";
    }
}
