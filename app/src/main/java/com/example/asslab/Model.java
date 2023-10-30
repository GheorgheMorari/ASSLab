package com.example.asslab;

import android.content.Context;

import com.example.asslab.ml.MobilenetV110224Quant;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class Model {

    int model_input_width;
    int model_input_height;
    MobilenetV110224Quant model;
    TensorBuffer inputFeature0;

    public Model(int model_input_width, int model_input_height, Context context) {
        this.model_input_width = model_input_width;
        this.model_input_height = model_input_height;
        allocate(context);
    }

    float[] run(ByteBuffer scaled_bitmap_buffer) {
        this.inputFeature0.loadBuffer(scaled_bitmap_buffer);
        MobilenetV110224Quant.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
        return outputFeature0.getFloatArray();
    }

    void allocate(Context context) {
        try {
            this.model = MobilenetV110224Quant.newInstance(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
    }

    void free() {
        model.close();
    }
}