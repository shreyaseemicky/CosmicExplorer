package com.example.cosmicexplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.List;

public class SkyOverlayView extends View {

    private SkyRenderer renderer;
    private List<StarCatalog.Star>         stars;
    private List<StarCatalog.Constellation> constellations;

    // Smoothed orientation
    private double smoothAz  = 0;
    private double smoothAlt = 30;
    private boolean firstReading = true; // ✅ snap on first reading

    // Manual pan
    private float panOffsetAz  = 0;
    private float panOffsetAlt = 0;

    // FOV
    private float fov = 60f;
    private static final float FOV_MIN = 15f;
    private static final float FOV_MAX = 120f;

    private String searchQuery = "";

    // Gesture
    private ScaleGestureDetector scaleDetector;
    private GestureDetector      gestureDetector;

    // Star tap
    public interface OnStarTappedListener {
        void onStarTapped(StarCatalog.Star star, float screenX, float screenY);
    }
    private OnStarTappedListener starTappedListener;

    // Shooting stars
    private ShootingStarManager shootingStarManager;

    // ✅ Lower = smoother but slower to follow phone
    private static final float SMOOTH_FACTOR = 0.15f;

    public SkyOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs); init(context);
    }
    public SkyOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); init(context);
    }
    public SkyOverlayView(Context context) {
        super(context); init(context);
    }

    private void init(Context context) {
        setBackgroundColor(0x00000000);
        renderer            = new SkyRenderer(context);
        stars               = StarCatalog.getStars();
        constellations      = StarCatalog.getConstellations();
        shootingStarManager = new ShootingStarManager();

        // Pinch to zoom
        scaleDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector d) {
                        fov /= d.getScaleFactor();
                        fov = Math.max(FOV_MIN, Math.min(FOV_MAX, fov));
                        invalidate();
                        return true;
                    }
                });

        // Drag + tap
        gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                            float dX, float dY) {
                        float degPerPx = fov / getWidth();
                        panOffsetAz  += dX * degPerPx;
                        panOffsetAlt += dY * degPerPx;
                        panOffsetAlt  = Math.max(-80f,
                                Math.min(80f, panOffsetAlt));
                        invalidate();
                        return true;
                    }

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        handleStarTap(e.getX(), e.getY());
                        return true;
                    }
                });
    }

    private void handleStarTap(float tapX, float tapY) {
        if (starTappedListener == null) return;

        double effAz  = smoothAz  + panOffsetAz;
        double effAlt = smoothAlt - panOffsetAlt;

        // Check named stars first
        StarCatalog.Star closestStar = null;
        float closestDist = 80f;

        for (StarCatalog.Star star : stars) {
            float[] pos = AstroMath.project(
                    star.ra, star.dec,
                    effAz, effAlt,
                    getWidth(), getHeight(), fov);
            if (pos == null) continue;
            float dist = (float) Math.hypot(tapX - pos[0], tapY - pos[1]);
            if (dist < closestDist) {
                closestDist = dist;
                closestStar = star;
            }
        }

        // Check DSOs — larger tap radius
        for (DeepSkyObject dso : DeepSkyObject.getCatalog()) {
            float[] pos = AstroMath.project(
                    dso.ra, dso.dec,
                    effAz, effAlt,
                    getWidth(), getHeight(), fov);
            if (pos == null) continue;
            float screenR = (dso.angularSize / fov) * getWidth() * 0.6f;
            screenR = Math.max(30f, screenR);
            float dist = (float) Math.hypot(tapX - pos[0], tapY - pos[1]);
            if (dist < screenR && dist < closestDist) {
                // Convert DSO to a fake Star for the card
                StarCatalog.Star fakeStar = new StarCatalog.Star(
                        dso.name, dso.ra, dso.dec,
                        dso.magnitude, false, dso.coreColor);
                closestDist = dist;
                closestStar = fakeStar;
            }
        }

        if (closestStar != null) {
            final StarCatalog.Star result = closestStar;
            float[] pos = AstroMath.project(
                    result.ra, result.dec,
                    effAz, effAlt,
                    getWidth(), getHeight(), fov);
            if (pos != null)
                starTappedListener.onStarTapped(result, pos[0], pos[1]);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        double effAz  = smoothAz  + panOffsetAz;
        double effAlt = smoothAlt - panOffsetAlt;

        renderer.render(
                canvas, stars, constellations,
                effAz, effAlt,
                getWidth(), getHeight(),
                fov, searchQuery,
                shootingStarManager);

        postInvalidateDelayed(40);
    }

    // ✅ Fixed smooth update — snaps on first reading
    public void updateOrientation(double rawAz, double rawAlt) {
        if (firstReading) {
            // ✅ Snap immediately to real sensor value
            smoothAz  = rawAz;
            smoothAlt = rawAlt;
            firstReading = false;
        } else {
            // ✅ Handle azimuth wrap-around (359° → 0°)
            double diff = rawAz - smoothAz;
            if (diff >  180) diff -= 360;
            if (diff < -180) diff += 360;
            smoothAz  = smoothAz + SMOOTH_FACTOR * diff;
            smoothAlt = smoothAlt + SMOOTH_FACTOR * (rawAlt - smoothAlt);
        }

        // Normalize azimuth
        if (smoothAz < 0)   smoothAz += 360;
        if (smoothAz >= 360) smoothAz -= 360;
    }

    public void setNightMode(boolean on) {
        renderer.setNightMode(on);
    }
    public void setSearchQuery(String query) {
        this.searchQuery = query;
    }
    public void setTourHighlights(List<String> starNames) {
        renderer.setHighlightedStars(starNames);
    }
    public void clearTourHighlights() {
        renderer.setHighlightedStars(null);
    }
    public void setSkyCulture(SkyCulture.Culture culture) {
        renderer.setSkyCulture(culture);
    }
    public void setOnStarTappedListener(OnStarTappedListener l) {
        this.starTappedListener = l;
    }
    public float getFov() { return fov; }

    // ✅ Expose smoothed values for compass display
    public double getSmoothedAzimuth()  { return smoothAz; }
    public double getSmoothedAltitude() { return smoothAlt; }
}