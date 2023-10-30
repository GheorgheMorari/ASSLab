package com.example.asslab;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.utils.widget.ImageFilterView;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;


public class ViewClass extends AppCompatActivity {

    Button selectBtn, filterBtn;
    ViewModel viewModel;
    TextInputEditText className;
    ArrayList<Integer> imageViewIds;

    GridLayout imageGrid;
    LinearLayout.LayoutParams defaultLayoutParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewIds = new ArrayList<>();
        selectBtn = findViewById(R.id.selectBtn);
        filterBtn = findViewById(R.id.filterBtn);
        imageGrid = findViewById(R.id.imageGrid);
        className = findViewById(R.id.className);
        viewModel = new ViewModel(this, this::updateView);

        float scale = getResources().getDisplayMetrics().density;
        int dpWidthInPx = (int) (200 * scale);
        int dpHeightInPx = (int) (200 * scale);

        defaultLayoutParams = new LinearLayout.LayoutParams(dpWidthInPx, dpHeightInPx);
        defaultLayoutParams.setMargins(2, 2, 2, 2);
        defaultLayoutParams.gravity = Gravity.CENTER;

        selectBtn.setOnClickListener(v -> {
            viewModel.getSelectedImages();
        });

        filterBtn.setOnClickListener(v -> {
            Editable classNameText = className.getText();
            String classNameString = (classNameText == null ? "" : classNameText.toString());
            viewModel.filterImages(classNameString);
        });
    }

    void updateView(ArrayList<Bitmap> bitmaps) {
        if (bitmaps == null)
            return;
        // Determine if you need to create more ImageFilterViews or remove excess ones
        if (bitmaps.size() > imageViewIds.size()) {
            // Create new ImageFilterViews as needed
            int numViewsToAdd = bitmaps.size() - imageViewIds.size();
            for (int i = 0; i < numViewsToAdd; i++) {
                ImageFilterView imageView = new ImageFilterView(this);
                imageView.setId(View.generateViewId());
                imageView.setLayoutParams(defaultLayoutParams);
                imageViewIds.add(imageView.getId());
                imageGrid.addView(imageView); // Add the new view to the parent layout
            }
        } else {
            // Remove excess ImageFilterViews
            int numViewsToRemove = imageViewIds.size() - bitmaps.size();
            for (int i = 0; i < numViewsToRemove; i++) {
                int viewIdToRemove = imageViewIds.remove(imageViewIds.size() - 1); // Remove the last ID
                imageGrid.removeView(findViewById(viewIdToRemove)); // Remove the corresponding view
            }
        }

        // Set bitmaps for all ImageFilterViews
        for (int i = 0; i < bitmaps.size(); i++) {
            ImageFilterView imageView = (ImageFilterView) findViewById(imageViewIds.get(i));
            if (imageView != null) {
                imageView.setImageBitmap(bitmaps.get(i));
            }
        }
    }
}

