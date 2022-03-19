package com.example.plugindev;

import static java.lang.Math.abs;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.plugindev.databinding.FragmentFirstBinding;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.indexer.FloatRawIndexer;
import org.bytedeco.javacpp.indexer.UByteIndexer;
import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_calib3d;
import org.bytedeco.opencv.global.opencv_core;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.global.opencv_imgproc;
import org.bytedeco.opencv.opencv_core.Mat;
import org.bytedeco.opencv.opencv_core.Point;
import org.bytedeco.opencv.opencv_core.PointVector;
import org.bytedeco.opencv.opencv_core.Rect;
import org.bytedeco.opencv.opencv_core.Scalar;
import org.bytedeco.opencv.opencv_core.Size;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageViewE4 = (ImageView) getView().findViewById(R.id.imageViewE4);
        ImageView imageViewOriginal = (ImageView) getView().findViewById(R.id.imageViewOriginal);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Mat D4mat = loadImg("OriginalrsD5F5.jpg", false);
               Mat Orignalmat = loadImg("Originalrs.jpg", false);
               Mat board = loadImg("board.jpg", false);
               Mat boardcolor = loadImg("board.jpg", true);
                /**
                 * Getchessboard corner: First, actually lets just start with a perfect image
                 * . i.e chessboard corner = corner of image. done.
                 * (user specify when adjust the camera). Save chessboard corner.
                 * Find perspective using the corner.
                 * Perspective transform.
                 * Extrapolation to find outer corner.


                 * Get 64 regions using rows and cols.
                 * 2D arraylist to save 8x8 matrix of averages / Mat
                 * Subtract 2 Mats
                 * Get the coordinate of the difference
                 */

                ArrayList<Point> corners = (ArrayList<Point>) findCorner(board, imageViewOriginal, imageViewE4);
                for(Point corner : corners){
                    opencv_imgproc.circle(boardcolor, corner, 25, new Scalar(255,0,0,0), 50, 0, 0);
                    System.out.println("opencv : col: " + corner.x() + "row : " + corner.y());
                }

                Mat newmat = new Mat(5,1, opencv_core.CV_8UC2, new Point(255,5));
                UByteRawIndexer indexer = newmat.createIndexer();
                System.out.println("work here opencv");

                System.out.println("opencv:" + indexer.get(0,0,0));
                System.out.println("opencv:" + indexer.get(1,1,1));
                /**
                 * Perspective transform
                 */

                Point newCorner3 = new Point(corners.get(0).x(), corners.get(2).y());
                Point newCorner4 = new Point(corners.get(1).x(), corners.get(1).y());

                Mat newCorners = new Mat(1, 4, opencv_core.CV_8UC1, newCorner4);


                //opencv_imgproc.getPerspectiveTransform()

                Bitmap bmp = MattobitmapConvert(boardcolor);
                imageViewOriginal.setImageBitmap(bmp);

            }
        });
    }

    public Mat loadImg(String filename, boolean withcolor){
        Integer nRead;
        InputStream is = null;
        Mat mat = null;
        try {
            is = getContext().getAssets().open(filename);
            byte[] data = new byte[is.available()];
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            while((nRead = is.read(data,0,data.length)) != -1){
                buffer.write(data, 0, nRead);
            }
            byte[] bytes = buffer.toByteArray();
            if (withcolor) {
                mat = opencv_imgcodecs.imdecode(new Mat(bytes), opencv_imgcodecs.IMREAD_COLOR);
            }
            else {
                mat = opencv_imgcodecs.imdecode(new Mat(bytes), opencv_imgcodecs.IMREAD_GRAYSCALE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mat;
    }

    public Bitmap MattobitmapConvert(Mat mat){

        OpenCVFrameConverter.ToMat converterToMat = new OpenCVFrameConverter.ToMat();
        Frame frame = converterToMat.convert(mat);
        AndroidFrameConverter converterToBitmap = new AndroidFrameConverter();
        Bitmap bitmap = converterToBitmap.convert(frame);

        return bitmap;
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

    private void FindColorChangeCoor(Mat d4mat, Mat orignalmat) {

        Integer nrows = orignalmat.rows();
        Integer ncols = orignalmat.cols();
        Integer sqwidth = ncols/8;
        Integer sqheight = nrows/8;

        Mat CompressedOriginal = new Mat(8,8, opencv_core.CV_8UC1);
        Mat CompressedD4 = new Mat(8,8, opencv_core.CV_8UC1);
        Mat CompressedSubtract = new Mat(8,8, opencv_core.CV_8UC1);
        UByteRawIndexer CompOriginalindexer = CompressedOriginal.createIndexer();
        UByteRawIndexer CompD4indexer = CompressedD4.createIndexer();
        UByteRawIndexer CompSubtractindexer = CompressedD4.createIndexer();


        //rows
        long max1 = 0;
        long max2 = 0;

        // key
        HashMap<Long, Coord> map = new HashMap<>();
        map.put(0L, new Coord(0,0));
        for(int i = 0; i < 8; i ++) {
            //cols
            for (int j = 0; j < 8; j ++){
                Rect reg = new Rect(j * sqwidth, i * sqheight , sqwidth , sqheight);
                Mat squareOriginal = new Mat(orignalmat, reg);
                Mat squareD4 = new Mat(d4mat, reg);
                long averOr = average(squareOriginal);
                long averSq = average(squareD4);
                long dist = abs(averOr - averSq);

                if(dist >= max1) {
                    max2 = max1;
                    map.put(max2, new Coord(map.get(max1).i, map.get(max1).j ) );

                    max1 = dist;
                    map.put(max1, new Coord(i, j));
                }
                else if (dist > max2) {
                    max2 = dist;
                    map.put(max2, new Coord(i,j) );
                }
            }
        }

        System.out.println("opencv: rows, col:" +  map.get(max1).i + " : " + map.get(max1).j );
        System.out.println("opencv: rows, col:" +  map.get(max2).i + " : " + map.get(max2).j );

    }

    private long average(Mat square) {
        UByteRawIndexer indexer = square.createIndexer();
        long totals = 0;
        for(int y = 0; y < square.rows(); y++){
            for (int x = 0; x < square.cols(); x ++){
                long value = indexer.get(y,x);
                totals += value;
                //System.out.println("opencv value : " + value);
            }
        }
        return totals/(square.total());
    }

    /**
     * find inner corners of an empty board, maths to find outer corners. Save coordinate.
     * @param mat
     * @return
     */
    public List<Point> findCorner(Mat mat, ImageView imageViewOriginal, ImageView imageViewE4){
        List<Point> Points = new ArrayList<>();
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

        Points.add(corner1);
        Points.add(corner2);
        Points.add(corner3);
        Points.add(corner4);



        return Points;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}