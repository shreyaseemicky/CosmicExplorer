package com.example.cosmicexplorer;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CultureDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture_detail);

        int index = getIntent().getIntExtra("culture_index", 0);
        SkyCulture.CultureData culture = SkyCulture.getAllCultures()[index];

        ((TextView) findViewById(R.id.cultureEmoji)).setText(culture.emoji);
        ((TextView) findViewById(R.id.cultureTitle)).setText(culture.name);
        ((TextView) findViewById(R.id.cultureDesc)).setText(culture.description);
        ((TextView) findViewById(R.id.cultureLore)).setText(culture.lore);

        // Constellation names list
        LinearLayout list = findViewById(R.id.cultureConstellationList);
        String[] names = SkyCulture.getConstellationNames(culture.culture);
        for (String name : names) {
            TextView tv = new TextView(this);
            tv.setText("  ✦  " + name);
            tv.setTextColor(0xFFAADDFF);
            tv.setTextSize(15f);
            tv.setPadding(0, 10, 0, 10);
            list.addView(tv);

            android.view.View divider = new android.view.View(this);
            divider.setBackgroundColor(0x22AADDFF);
            LinearLayout.LayoutParams lp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, 1);
            list.addView(divider, lp);
        }

        ((ImageButton) findViewById(R.id.backBtn))
                .setOnClickListener(v -> finish());
    }
}
