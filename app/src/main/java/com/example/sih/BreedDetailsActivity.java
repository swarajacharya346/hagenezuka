package com.example.sih;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BreedDetailsActivity extends AppCompatActivity {
    private TextView detailBreedName, detailText;
    private ImageView detailBreedImage;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.breed_details);

        detailBreedName = findViewById(R.id.detailBreedName);
        detailText = findViewById(R.id.detailText);
        detailBreedImage = findViewById(R.id.detailBreedImage);
        dbHelper = new DatabaseHelper(this);

        String breed = getIntent().getStringExtra("breed");
        if (breed == null) breed = "Unknown";
        detailBreedName.setText("Breed: " + breed);

        String finalBreed = breed;
        dbHelper.getBreedDetails(breed, new DatabaseHelper.BreedDetailsCallback() {
            @Override
            public void onResult(String description, String imageUrl) {
                detailText.setText(description != null ? description : "No information available.");
                // For demo: use placeholder images in drawable
                if (finalBreed.equalsIgnoreCase("gir")) detailBreedImage.setImageResource(R.drawable.gir_sample);
                else if (finalBreed.equalsIgnoreCase("sahiwal")) detailBreedImage.setImageResource(R.drawable.sahiwal_sample);
                else detailBreedImage.setImageResource(R.drawable.placeholder_cattle);
            }
            @Override public void onFailure(Exception e) { detailText.setText("Failed to load details."); }
        });
    }
}
