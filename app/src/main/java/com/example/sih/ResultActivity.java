package com.example.sih;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private AIModelHelper aiHelper;
    private ImageView resultImage;
    private TextView breedName, confidence;
    private Button confirmBtn, retakeBtn;
    private static final int INPUT_SIZE = 224; // must match your model

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
            bmp = Bitmap.createBitmap(INPUT_SIZE, INPUT_SIZE, Bitmap.Config.ARGB_8888);
        }
        resultImage.setImageBitmap(bmp);

        // Load AI model and labels dynamically
        try {
            aiHelper = new AIModelHelper(this, "model.tflite", "labels.txt", INPUT_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "AI model load failed!", Toast.LENGTH_SHORT).show();
            aiHelper = null;
        }

        // Make prediction
        if (aiHelper != null) {
            AIModelHelper.Prediction prediction = aiHelper.predict(bmp);
            breedName.setText("Breed: " + prediction.label);
            confidence.setText(String.format("Confidence: %.2f%%", prediction.confidence * 100));
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
