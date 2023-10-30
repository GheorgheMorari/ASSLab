package com.example.asslab;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.support.image.TensorImage;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ViewModel {
    // Contains all the images
    // Initiates the inference
    // Filters the images
    // Sends the images back to the view
    ArrayList<Bitmap> filteredImages;
    ActivityResultLauncher<Intent> selectionActivityLauncher;
    AppCompatActivity activity;
    ArrayList<Bitmap> selectedImages;
    ArrayList<float[]> selectedImagesOutput;
    Consumer<ArrayList<Bitmap>> updateViewCallback;
    ModelClass modelClass;
    boolean processedFlag = false;

    public ViewModel(AppCompatActivity activity, Consumer<ArrayList<Bitmap>> updateViewCallback) {
        this.activity = activity;
        this.updateViewCallback = updateViewCallback;
        selectionActivityLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onSelectFinished);
        modelClass = new ModelClass(activity);
    }

    void onSelectFinished(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            if (data != null) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    IntStream.range(0, count).mapToObj(i -> data.getClipData().getItemAt(i).getUri()).forEach(uri -> {
                        try {
                            ImageDecoder.Source source = ImageDecoder.createSource(activity.getContentResolver(), uri);
                            selectedImages.add(ImageDecoder.decodeBitmap(source));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else if (data.getData() != null) {
                    Uri uri = data.getData();
                    try {
                        ImageDecoder.Source source = ImageDecoder.createSource(activity.getContentResolver(), uri);
                        selectedImages.add(ImageDecoder.decodeBitmap(source));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                processedFlag = false;
                updateViewCallback.accept(selectedImages);
                processImagesAsync();
            }
        }
    }

    void getSelectedImages() {
        selectedImages = new ArrayList<>();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // Allow multiple image selection
        selectionActivityLauncher.launch(intent);
    }

    void filterImages(String className) {
        if (className.equals("") || !processedFlag) {
            updateViewCallback.accept(selectedImages); // Show all images if it's not done processing or the class name is empty
            return;
        }
        //TODO
        updateViewCallback.accept(selectedImages);
    }

    void processImagesAsync() {
        new Thread(() -> {
            selectedImagesOutput = new ArrayList<>(selectedImages.size());
            modelClass.allocate(activity);
            for (Bitmap selectedImage : selectedImages) {
                Bitmap scaledSelectedImage = Bitmap.createScaledBitmap(selectedImage, 224, 224, true).copy(Bitmap.Config.ARGB_8888, true);
                TensorImage tensorImage = TensorImage.fromBitmap(scaledSelectedImage);
                ByteBuffer imageByteBuf = tensorImage.getBuffer();
                selectedImagesOutput.add(modelClass.run(imageByteBuf));
            }
            modelClass.free();
            processedFlag = true;
        }).start();
    }

}
