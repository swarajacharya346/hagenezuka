package com.example.sih;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {

    private TextView breedName, confidence;
    private CardView breedInfoCard;
    private Button confirmBtn, retakeBtn;
    private String breed;
    private float conf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        breedName = findViewById(R.id.breedName);
        confidence = findViewById(R.id.confidence);
        breedInfoCard = findViewById(R.id.breedInfoCard);
        confirmBtn = findViewById(R.id.confirmBtn);
        retakeBtn = findViewById(R.id.retakeBtn);

        breed = getIntent().getStringExtra("breed");
        conf = getIntent().getFloatExtra("confidence", 0);

        breedName.setText("Breed: " + breed);
        confidence.setText("Confidence: " + String.format("%.2f", conf * 100) + "%");

        breedInfoCard.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, BreedDetailsActivity.class);
            intent.putExtra("breed", breed);
            startActivity(intent);
        });

        confirmBtn.setOnClickListener(v -> {
            // TODO: Save to SQLite / Cloud
            finish();
        });

        retakeBtn.setOnClickListener(v -> finish());
    }
}
