package com.example.cosmicexplorer;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TourDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour_detail);

        // Get tour index from intent
        int index = getIntent().getIntExtra("tour_index", 0);
        SkyTour.Tour tour = SkyTour.getAllTours()[index];

        // Bind views
        ((TextView) findViewById(R.id.detailEmoji)).setText(tour.emoji);
        ((TextView) findViewById(R.id.detailTitle)).setText(tour.title);
        ((TextView) findViewById(R.id.detailDesc)).setText(tour.description);
        ((TextView) findViewById(R.id.detailFunFact)).setText(tour.funFact);
        ((TextView) findViewById(R.id.detailBestTime)).setText(tour.bestTime);
        ((TextView) findViewById(R.id.detailVisibility)).setText(tour.visibility);

        // Build stars list
        LinearLayout starsList = findViewById(R.id.detailStarsList);
        for (String starName : tour.highlightStars) {
            TextView tv = new TextView(this);
            tv.setText("  ✦  " + starName);
            tv.setTextColor(0xFFAADDFF);
            tv.setTextSize(15f);
            tv.setPadding(0, 10, 0, 10);
            starsList.addView(tv);

            // Divider
            android.view.View divider = new android.view.View(this);
            divider.setBackgroundColor(0x22AADDFF);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1);
            starsList.addView(divider, lp);
        }

        // Back button
        ((ImageButton) findViewById(R.id.backBtn))
                .setOnClickListener(v -> finish());
    }
}
