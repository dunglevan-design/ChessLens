package com.example.plugindev;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.plugindev.databinding.FragmentFirstBinding;

import org.bytedeco.javacpp.indexer.UByteRawIndexer;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.opencv.global.opencv_imgcodecs;
import org.bytedeco.opencv.opencv_core.Mat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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

            public Mat loadImg(String filename){
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
                    mat = opencv_imgcodecs.imdecode(new Mat(bytes), opencv_imgcodecs.IMREAD_COLOR);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return mat;
            }

            @Override
            public void onClick(View view) {
               Mat E4mat = loadImg("E4.jpg");
               Mat Orignalmat = loadImg("Original.jpg");

               Bitmap E4bitmap = MattobitmapConvert(E4mat);
               imageViewE4.setImageBitmap(E4bitmap);

               Bitmap Originalbitmap = MattobitmapConvert(Orignalmat);
               imageViewOriginal.setImageBitmap(Originalbitmap);

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}