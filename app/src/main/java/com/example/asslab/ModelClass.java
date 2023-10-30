package com.example.asslab;

import android.content.Context;

import com.example.asslab.ml.MobilenetV110224Quant;
import com.example.asslab.ml.V3Large22410Uint8;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ModelClass {

    V3Large22410Uint8 model;
    TensorBuffer inputFeature0;

    public ModelClass(Context context) {
        allocate(context);
    }

    float[] run(ByteBuffer scaled_bitmap_buffer) {
        this.inputFeature0.loadBuffer(scaled_bitmap_buffer);
        V3Large22410Uint8.Outputs outputs = model.process(inputFeature0);
        TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
        return outputFeature0.getFloatArray();
    }

    void allocate(Context context) {
        try {
            this.model = V3Large22410Uint8.newInstance(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.UINT8);
    }

    void free() {
        model.close();
    }
}
