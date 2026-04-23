package com.example.cosmicexplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

public class SkyOverlayView extends View {

    private SkyRenderer renderer;
    private List<StarCatalog.Star> stars;
    private List<StarCatalog.Constellation> constellations;

    private double azimuth     = 0;
    private double altitude    = 30;
    private float  fov         = 90f;
    private String searchQuery = "";

    public SkyOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SkyOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SkyOverlayView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setBackgroundColor(0x00000000);
        renderer       = new SkyRenderer();
        stars          = StarCatalog.getStars();
        constellations = StarCatalog.getConstellations();
    }

    public void updateOrientation(double azimuth, double altitude) {
        this.azimuth  = azimuth;
        this.altitude = altitude;
        invalidate();
    }

    public void setNightMode(boolean on) {
        renderer.setNightMode(on);
        invalidate();
    }

    public void setSearchQuery(String query) {
        this.searchQuery = query;
        invalidate();
    }

    // ✅ Highlight stars for a tour
    public void setTourHighlights(List<String> starNames) {
        renderer.setHighlightedStars(starNames);
        invalidate();
    }

    public void clearTourHighlights() {
        renderer.setHighlightedStars(null);
        invalidate();
    }

    // ✅ Apply sky culture constellation names
    public void setSkyCulture(SkyCulture.Culture culture) {
        renderer.setSkyCulture(culture);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        renderer.render(
                canvas, stars, constellations,
                azimuth, altitude,
                getWidth(), getHeight(),
                fov, searchQuery
        );
    }
}