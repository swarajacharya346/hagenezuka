package com.example.sih;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class BreedDetailsActivity extends AppCompatActivity {

    private TextView detailBreedName, detailText;
    private ImageView detailBreedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breed_details);

        detailBreedName = findViewById(R.id.detailBreedName);
        detailText = findViewById(R.id.detailText);
        detailBreedImage = findViewById(R.id.detailBreedImage);

        String breed = getIntent().getStringExtra("breed");

        detailBreedName.setText("Breed: " + breed);
        switch (breed.toLowerCase()) {
            case "gir":
                detailText.setText("Gir is a high milk-yield breed from Gujarat. Milk: 12-15 L/day. Temperament: Calm.");
                detailBreedImage.setImageResource(R.drawable.gir_sample);
                break;
            case "sahiwal":
                detailText.setText("Sahiwal is a hardy dairy breed from Punjab. Milk: 8-12 L/day. Temperament: Friendly.");
                detailBreedImage.setImageResource(R.drawable.sahiwal_sample);
                break;
            case "murrah":
                detailText.setText("Murrah is a buffalo breed from Haryana. Milk: 10-12 L/day. Temperament: Active.");
                detailBreedImage.setImageResource(R.drawable.murrah_sample);
                break;
        }
    }
}
