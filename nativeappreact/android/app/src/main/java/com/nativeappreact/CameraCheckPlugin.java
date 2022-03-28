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
import com.mrousavy.camera.frameprocessor.FrameProcessorPlugin;

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
        Floatprint(corners);


        FloatRawIndexer cornerIndexer = corners.createIndexer();
        opencv_imgproc.circle(mat, new Point((int) cornerIndexer.
                        get(0, 0), (int) cornerIndexer.get(0, 1)), 50,
                new Scalar(255, 0, 0, 0), 50, 0, 0);
        opencv_imgproc.circle(mat, new Point((int) cornerIndexer.
                        get(1, 0), (int) cornerIndexer.get(1, 1)), 50,
                new Scalar(0, 255, 0, 0), 50, 0, 0);
        opencv_imgproc.circle(mat, new Point((int) cornerIndexer.
                        get(2, 0), (int) cornerIndexer.get(2, 1)), 50,
                new Scalar(0, 0, 255, 0), 50, 0, 0);
        opencv_imgproc.circle(mat, new Point((int) cornerIndexer.
                        get(3, 0), (int) cornerIndexer.get(3, 1)), 50,
                new Scalar(122, 122, 122, 0), 50, 0, 0);


        opencv_imgproc.circle(mat, new Point(1279,719), 50,
                new Scalar(122, 122, 122, 0), 50, 0, 0);
        Bitmap bitmap = MattobitmapConvert(mat);

        System.out.println("opencv + coompdjf");
        return "Camera Check";
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

    public void Floatprint(Mat mat){
        FloatRawIndexer sI = mat.createIndexer();
        List<List<Integer>> values = new ArrayList<>();

        for(int y = 0; y < mat.rows(); y++){
            List<Integer> rows = new ArrayList<>();
            for (int x = 0; x < mat.cols(); x ++){
                rows.add((int) sI.get(y,x));
            }
            values.add(rows);
        }

        System.out.println("opencv " + values);
    }
}
