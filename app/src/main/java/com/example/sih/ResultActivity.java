package com.example.sih;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private AIModelHelper aiHelper;
    private ImageView resultImage;
    private TextView breedName, confidence;
    private Button confirmBtn, retakeBtn;
    private static final int INPUT_SIZE = 224;
    private static final float UNKNOWN_THRESHOLD = 0.5f;

    private FirebaseStorage storage;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_screen);

        // Firebase instances
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Bind views
        resultImage = findViewById(R.id.resultImage);
        breedName = findViewById(R.id.breedName);
        confidence = findViewById(R.id.confidence);
        confirmBtn = findViewById(R.id.confirmBtn);
        retakeBtn = findViewById(R.id.retakeBtn);

        // Load image
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

        // Load AI model
        try {
            aiHelper = new AIModelHelper(this, "model.tflite", "labels.txt", INPUT_SIZE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "AI model load failed!", Toast.LENGTH_SHORT).show();
            aiHelper = null;
        }

        // Prediction
        AIModelHelper.Prediction prediction = null;
        if (aiHelper != null) {
            prediction = aiHelper.predict(bmp);

            if (isBlankImage(bmp) || prediction.confidence < UNKNOWN_THRESHOLD || isHumanImage(prediction.label)) {
                breedName.setText("Breed: Unknown");
                confidence.setText("Confidence: 0%");
            } else {
                breedName.setText("Breed: " + prediction.label);
                confidence.setText(String.format("Confidence: %.2f%%", prediction.confidence * 100));
            }
        } else {
            breedName.setText("Breed: Unknown");
            confidence.setText("Confidence: 0%");
        }

        // Confirm button: upload to Firebase
        Bitmap finalBmp = bmp;
        AIModelHelper.Prediction finalPrediction = prediction;
        confirmBtn.setOnClickListener(v -> uploadResult(finalBmp, finalPrediction));

        // Retake button
        retakeBtn.setOnClickListener(v -> finish());
    }

    private void uploadResult(Bitmap bitmap, AIModelHelper.Prediction prediction) {
        // Compress bitmap to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] data = baos.toByteArray();

        // Storage reference
        StorageReference storageRef = storage.getReference()
                .child("results/" + System.currentTimeMillis() + ".jpg");

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl()
                        .addOnSuccessListener(uri -> savePredictionToFirestore(uri.toString(), prediction))
                        .addOnFailureListener(e -> Toast.makeText(ResultActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show()))
                .addOnFailureListener(e -> Toast.makeText(ResultActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    private void savePredictionToFirestore(String imageUrl, AIModelHelper.Prediction prediction) {
        Map<String, Object> resultData = new HashMap<>();
        if (prediction == null || prediction.confidence < UNKNOWN_THRESHOLD) {
            resultData.put("breed", "Unknown");
            resultData.put("confidence", 0);
        } else {
            resultData.put("breed", prediction.label);
            resultData.put("confidence", prediction.confidence * 100);
        }
        resultData.put("imageUrl", imageUrl);
        resultData.put("timestamp", System.currentTimeMillis());

        firestore.collection("predictions")
                .add(resultData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Result uploaded!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to save result", Toast.LENGTH_SHORT).show());
    }

    private boolean isBlankImage(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        long sum = 0;
        for (int pixel : pixels) {
            int r = (pixel >> 16) & 0xFF;
            int g = (pixel >> 8) & 0xFF;
            int b = pixel & 0xFF;
            sum += r + g + b;
        }

        double avg = sum / (double) (pixels.length * 3);
        return avg > 250 || avg < 5;
    }

    private boolean isHumanImage(String label) {
        return label != null && (label.toLowerCase().contains("human") || label.toLowerCase().contains("person"));
    }
}
