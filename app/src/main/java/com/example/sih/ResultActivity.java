package com.example.sih;

import android.content.Intent;
import android.graphics.Bitmap;
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
        setContentView(R.layout.result_screen); // match your XML filename

        // Bind views
        resultImage = findViewById(R.id.resultImage);
        breedName = findViewById(R.id.breedName);
        confidence = findViewById(R.id.confidence);
        confirmBtn = findViewById(R.id.confirmBtn);
        retakeBtn = findViewById(R.id.retakeBtn);

        // Load AI model and labels
        List<String> labels = Arrays.asList("cat", "dog", "parrot"); // replace with your labels
        try {
            aiHelper = new AIModelHelper(this, "model.tflite", labels);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "AI model load failed!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Example bitmap (replace with actual captured image)
        Bitmap bmp = getCapturedBitmap();
        resultImage.setImageBitmap(bmp);

        // Predict
        AIModelHelper.Prediction prediction = aiHelper.predict(bmp);
        breedName.setText("Breed: " + prediction.label);
        confidence.setText("Confidence: " + prediction.confidence + "%");

        // Confirm button
        confirmBtn.setOnClickListener(v -> {
            Toast.makeText(this, "Result confirmed!", Toast.LENGTH_SHORT).show();
            // Do something like save result
            finish();
        });

        // Retake button
        retakeBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class); // change to your camera activity
            startActivity(intent);
            finish();
        });
    }

    private Bitmap getCapturedBitmap() {
        // TODO: Replace with actual captured image from camera or gallery
        return Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888);
    }
}
