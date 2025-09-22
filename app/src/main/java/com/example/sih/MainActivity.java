package com.example.sih;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button openCameraBtn, historyBtn, profileBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        openCameraBtn = findViewById(R.id.openCameraBtn);
        historyBtn = findViewById(R.id.historyBtn);
        profileBtn = findViewById(R.id.profileBtn);

        // Go to CameraFragment via CameraActivity (wrapper activity)
        openCameraBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            startActivity(intent);
        });

        // Example: Navigate to History screen (make HistoryActivity later)
        historyBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        // Example: Navigate to Profile screen (make ProfileActivity later)
        profileBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
