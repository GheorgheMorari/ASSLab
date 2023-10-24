package com.example.asslab;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asslab.ml.MobilenetV110224Quant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity {

    Button selectBtn, predictBtn;
    TextView result_text_view;
    Bitmap bitmap;
    Bitmap scaled_bitmap;
    ImageView imageView;
    NNetwork network;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectBtn = findViewById(R.id.selectBtn);
        predictBtn = findViewById(R.id.predictBtn);
        result_text_view = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView);
        network = new NNetwork(224, 224, MainActivity.this);


        ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                try {
                    assert data != null;
                    Uri uri = data.getData();
                    ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), uri);
                    bitmap = ImageDecoder.decodeBitmap(source);
                    bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    scaled_bitmap = Bitmap.createScaledBitmap(bitmap, network.model_input_width, network.model_input_height, true);
                    imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        selectBtn.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            resultLauncher.launch(intent);
        });

        predictBtn.setOnClickListener(v -> {
            scaled_bitmap = Bitmap.createScaledBitmap(bitmap, network.model_input_width, network.model_input_height, true);
            scaled_bitmap = scaled_bitmap.copy(Bitmap.Config.ARGB_8888, true);
            TensorImage asdf = TensorImage.fromBitmap(scaled_bitmap);
            ByteBuffer buf =  asdf.getBuffer();
            result_text_view.setText(network.run(buf)[0]+"");

        });


    }
}

