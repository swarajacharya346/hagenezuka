package com.example.sih;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private AIModelHelper aiHelper;
    private ImageView resultImage;
    private TextView breedName, confidence;
    private Button confirmBtn, retakeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_screen);

        // Bind views
        resultImage = findViewById(R.id.resultImage);
        breedName = findViewById(R.id.breedName);
        confidence = findViewById(R.id.confidence);
        confirmBtn = findViewById(R.id.confirmBtn);
        retakeBtn = findViewById(R.id.retakeBtn);

        // Load captured bitmap
        Uri imageUri = getIntent().getData();
        Bitmap bmp = null;
        if (imageUri != null) {
            try {
                bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (bmp == null) {
            bmp = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888);
        }

        resultImage.setImageBitmap(bmp);

        // Load AI model
        List<String> labels = Arrays.asList("cat", "dog", "parrot"); // replace with your labels
        try {
            aiHelper = new AIModelHelper(this, "model.tflite", labels);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "AI model load failed!", Toast.LENGTH_SHORT).show();
            aiHelper = null;
        }

        // Predict safely
        if (aiHelper != null) {
            AIModelHelper.Prediction prediction = aiHelper.predict(bmp);
            breedName.setText("Breed: " + prediction.label);
            confidence.setText("Confidence: " + prediction.confidence + "%");
        } else {
            breedName.setText("Breed: Unknown");
            confidence.setText("Confidence: 0%");
        }

        // Confirm button
        confirmBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Result confirmed!", Toast.LENGTH_SHORT).show();
            // TODO: save to Firebase here
            finish();
        });

        // Retake button
        retakeBtn.setOnClickListener(v -> finish()); // Go back to CameraFragment / MainActivity
    }
}
