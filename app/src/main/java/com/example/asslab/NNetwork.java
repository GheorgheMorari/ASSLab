package com.example.asslab;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.asslab.ml.MobilenetV110224Quant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class NNetwork {

    int model_input_width;
    int model_input_height;
    MobilenetV110224Quant model;
    TensorBuffer inputFeature0;

    public NNetwork(int model_input_width, int model_input_height, Context context) {
        this.model_input_width = model_input_width;
        this.model_input_height = model_input_height;
        try {
            this.model = MobilenetV110224Quant.newInstance(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
    }

    float[] run(ByteBuffer scaled_bitmap_buffer) {
        this.inputFeature0.loadBuffer(scaled_bitmap_buffer);
        MobilenetV110224Quant.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
        return outputFeature0.getFloatArray();
    }

//    try {
//        MobilenetV110224Quant model = MobilenetV110224Quant.newInstance(MainActivity.this);
//
//        // Creates inputs for reference.
//        TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
//
//        // Scale image to fit in input
//        inputFeature0.loadBuffer(TensorImage.fromBitmap(scaled_bitmap).getBuffer());
//
//        // Runs model inference and gets result.
//        MobilenetV110224Quant.Outputs outputs = model.process(inputFeature0);
//        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
//
//        // Releases model resources if no longer used.
//        model.close();
//    } catch (
//    IOException e) {
//        // TODO Handle the exception
//    }


}
