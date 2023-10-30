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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class ViewModel {
    ArrayList<Bitmap> filteredImages;
    ActivityResultLauncher<Intent> selectionActivityLauncher;
    AppCompatActivity activity;
    ArrayList<Bitmap> selectedImages;
    ArrayList<float[]> selectedImagesOutput;
    Consumer<ArrayList<Bitmap>> updateViewCallback;
    ModelClass modelClass;
    boolean processedFlag = false;
    String[] labels;

    public ViewModel(AppCompatActivity activity, Consumer<ArrayList<Bitmap>> updateViewCallback) {

        this.activity = activity;
        this.updateViewCallback = updateViewCallback;
        selectionActivityLauncher = activity.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::onSelectFinished);
        modelClass = new ModelClass(activity);
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(activity.getAssets().open("labels.txt")));
            int labelCount = 1001;

            labels = new String[labelCount];

            int i = 0;
            String line;

            while ((line = bufferedReader.readLine()) != null && i < labelCount) {
                labels[i] = line;
                i++;
            }
            bufferedReader.close(); // Close the reader when done.
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        filteredImages = new ArrayList<>();
        int minDistance = Integer.MAX_VALUE;
        int closestLabelIndex = 0;
        float detectionThreshold = 10.0f;

        for (int i = 0; i < labels.length; i++) {
            int distance = modifiedLevenshteinDistance(className, labels[i]);
            if (distance < minDistance) {
                minDistance = distance;
                closestLabelIndex = i;
            }
        }
        System.out.println("Closest label:" + labels[closestLabelIndex]);

        for (int i = 0; i < selectedImagesOutput.size(); i++) {
            float labelValue = selectedImagesOutput.get(i)[closestLabelIndex];
            if (labelValue >= detectionThreshold) {
                filteredImages.add(selectedImages.get(i));
            }
            System.out.println("Label value: " + labelValue + " " + i);
        }

        updateViewCallback.accept(filteredImages);
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

            for (int i = 0; i < selectedImagesOutput.size(); i++) {
                float labelValue = 0;
                int maxValueIndex = 0;
                for (int j = 0; j < labels.length; j++) {
                    float currentValue = selectedImagesOutput.get(i)[j];
                    if (currentValue > labelValue) {
                        labelValue = currentValue;
                        maxValueIndex = j;
                    }
                }
                System.out.println("Label value for image " + i + " is " + labelValue + " for label:" + labels[maxValueIndex]);
            }

            processedFlag = true;
        }).start();
    }

    private static int modifiedLevenshteinDistance(String str1, String str2) {
        int[][] dp = new int[str1.length() + 1][str2.length() + 1];

        for (int i = 0; i <= str1.length(); i++) {
            for (int j = 0; j <= str2.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                } else if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(Math.min(
                                    dp[i - 1][j] + 1, // Deletion in str1
                                    dp[i][j - 1] + 1), // Deletion in str2
                            dp[i - 1][j - 1] + cost);
                }
            }
        }

        return dp[str1.length()][str2.length()];
    }
}

