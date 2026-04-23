package com.example.cosmicexplorer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    DrawerLayout   drawerLayout;
    SkyOverlayView skyOverlay;
    EditText       searchBar;
    ImageButton    nightModeBtn, menuBtn;
    TextView       compassText;
    LinearLayout   drawerContent;

    SensorManager sensorManager;
    Sensor        accelerometer, magnetometer;
    float[]       gravity, geomagnetic;

    boolean     nightMode = false;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout  = findViewById(R.id.drawerLayout);
        skyOverlay    = findViewById(R.id.skyOverlay);
        searchBar     = findViewById(R.id.searchBar);
        nightModeBtn  = findViewById(R.id.nightModeBtn);
        menuBtn       = findViewById(R.id.menuBtn);
        compassText   = findViewById(R.id.compassText);
        drawerContent = findViewById(R.id.drawerContent);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer  = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        playMusic();
        buildDrawerMenu();

        // ☰ Open drawer
        menuBtn.setOnClickListener(v ->
                drawerLayout.openDrawer(Gravity.START));

        // 🔴 Night mode
        nightModeBtn.setOnClickListener(v -> {
            nightMode = !nightMode;
            skyOverlay.setNightMode(nightMode);
            // Change background to deep red in night mode
            findViewById(R.id.mainContent).setBackgroundColor(
                    nightMode ? 0xFF0A0000 : 0xFF000010);
            Toast.makeText(this,
                    nightMode ? "🔴 Night Mode ON" : "Night Mode OFF",
                    Toast.LENGTH_SHORT).show();
        });

        // 🔍 Search
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int i, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int i, int b, int c) {
                skyOverlay.setSearchQuery(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Exit
        findViewById(R.id.exitBtn).setOnClickListener(v -> finishAffinity());
    }

    // ─────────────────────────────────
    // BUILD DRAWER MENU
    // ─────────────────────────────────
    private void buildDrawerMenu() {
        drawerContent.removeAllViews();

        SkyCulture.CultureData[] cultures = SkyCulture.getAllCultures();
        for (int i = 0; i < cultures.length; i++) {
            final int idx = i;
            SkyCulture.CultureData c = cultures[i];
            View item = LayoutInflater.from(this)
                    .inflate(R.layout.drawer_item, drawerContent, false);
            ((TextView) item).setText(c.emoji + "  " + c.name);
            item.setOnClickListener(v -> {
                skyOverlay.setSkyCulture(c.culture);
                Intent intent = new Intent(this, CultureDetailActivity.class);
                intent.putExtra("culture_index", idx);
                startActivity(intent);
                drawerLayout.closeDrawers();
            });
            drawerContent.addView(item);
        }

        // Tours header
        View tourHeader = LayoutInflater.from(this)
                .inflate(R.layout.drawer_section_header, drawerContent, false);
        ((TextView) tourHeader).setText("  🔭  TOURS");
        drawerContent.addView(tourHeader);

        SkyTour.Tour[] tours = SkyTour.getAllTours();
        for (int i = 0; i < tours.length; i++) {
            final int idx = i;
            SkyTour.Tour t = tours[i];
            View item = LayoutInflater.from(this)
                    .inflate(R.layout.drawer_item, drawerContent, false);
            ((TextView) item).setText(t.emoji + "  " + t.title);
            item.setOnClickListener(v -> {
                skyOverlay.setTourHighlights(t.highlightStars);
                Intent intent = new Intent(this, TourDetailActivity.class);
                intent.putExtra("tour_index", idx);
                startActivity(intent);
                drawerLayout.closeDrawers();
            });
            drawerContent.addView(item);
        }
    }

    // ─────────────────────────────────
    // SENSORS
    // ─────────────────────────────────
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geomagnetic = event.values;

        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9], I = new float[9];
            if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);

                double az  = Math.toDegrees(orientation[0]);
                double alt = -Math.toDegrees(orientation[1]);
                if (az < 0) az += 360;

                skyOverlay.updateOrientation(az, alt);
                compassText.setText(
                        getDirection(az) + "  " + (int) az + "°  ↕" + (int) alt + "°");
            }
        }
    }

    private String getDirection(double d) {
        if (d < 22.5 || d >= 337.5) return "N";
        if (d < 67.5)  return "NE";
        if (d < 112.5) return "E";
        if (d < 157.5) return "SE";
        if (d < 202.5) return "S";
        if (d < 247.5) return "SW";
        if (d < 292.5) return "W";
        return "NW";
    }

    @Override public void onAccuracyChanged(Sensor s, int a) {}

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null)
            sensorManager.registerListener(this, accelerometer,
                    SensorManager.SENSOR_DELAY_GAME);
        if (magnetometer != null)
            sensorManager.registerListener(this, magnetometer,
                    SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    // ─────────────────────────────────
    // MUSIC
    // ─────────────────────────────────
    private void playMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.interstellar);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}