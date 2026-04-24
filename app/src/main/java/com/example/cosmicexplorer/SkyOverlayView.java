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
    private List<StarCatalog.Star> stars;
    private List<StarCatalog.Constellation> constellations;

    // Orientation from sensors (smoothed)
    private double azimuth  = 0;
    private double altitude = 30;

    // Manual pan offset (drag)
    private float panOffsetAz  = 0;
    private float panOffsetAlt = 0;

    // FOV (pinch zoom)
    private float fov = 60f;
    private static final float FOV_MIN = 15f;
    private static final float FOV_MAX = 120f;

    private String searchQuery = "";

    // Gesture detectors
    private ScaleGestureDetector scaleDetector;
    private GestureDetector      gestureDetector;

    // Last drag position
    private float lastTouchX, lastTouchY;
    private boolean isDragging = false;

    // Star tap callback
    public interface OnStarTappedListener {
        void onStarTapped(StarCatalog.Star star, float screenX, float screenY);
    }
    private OnStarTappedListener starTappedListener;

    // Shooting stars
    private ShootingStarManager shootingStarManager;

    // Low-pass filter state
    private double smoothAz  = 0;
    private double smoothAlt = 30;
    private static final float SMOOTH_FACTOR = 0.12f; // lower = smoother

    public SkyOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SkyOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SkyOverlayView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        setBackgroundColor(0x00000000);
        renderer             = new SkyRenderer(context);
        stars                = StarCatalog.getStars();
        constellations       = StarCatalog.getConstellations();
        shootingStarManager  = new ShootingStarManager();

        // ── Pinch to zoom ──────────────────────────────────
        scaleDetector = new ScaleGestureDetector(context,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        fov /= detector.getScaleFactor();
                        fov = Math.max(FOV_MIN, Math.min(FOV_MAX, fov));
                        invalidate();
                        return true;
                    }
                });

        // ── Drag to pan + tap to identify star ────────────
        gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                            float distanceX, float distanceY) {
                        // Convert pixels → degrees based on current FOV
                        float degPerPx = fov / getWidth();
                        panOffsetAz  += distanceX * degPerPx;
                        panOffsetAlt += distanceY * degPerPx;
                        // Clamp altitude offset
                        panOffsetAlt = Math.max(-80f, Math.min(80f, panOffsetAlt));
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

    // ── Handle star tap ────────────────────────────────────
    private void handleStarTap(float tapX, float tapY) {
        if (starTappedListener == null) return;

        double effectiveAz  = smoothAz  + panOffsetAz;
        double effectiveAlt = smoothAlt - panOffsetAlt;

        StarCatalog.Star closest = null;
        float closestDist = 60f; // tap radius in pixels

        for (StarCatalog.Star star : stars) {
            float[] pos = AstroMath.project(
                    star.ra, star.dec,
                    effectiveAz, effectiveAlt,
                    getWidth(), getHeight(), fov);
            if (pos == null) continue;
            float dist = (float) Math.hypot(tapX - pos[0], tapY - pos[1]);
            if (dist < closestDist) {
                closestDist = dist;
                closest = star;
            }
        }

        if (closest != null) {
            float[] pos = AstroMath.project(
                    closest.ra, closest.dec,
                    effectiveAz, effectiveAlt,
                    getWidth(), getHeight(), fov);
            if (pos != null)
                starTappedListener.onStarTapped(closest, pos[0], pos[1]);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        return true;
    }

    // ── Called every frame for shooting stars ─────────────
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        double effectiveAz  = smoothAz  + panOffsetAz;
        double effectiveAlt = smoothAlt - panOffsetAlt;

        renderer.render(
                canvas, stars, constellations,
                effectiveAz, effectiveAlt,
                getWidth(), getHeight(),
                fov, searchQuery,
                shootingStarManager
        );

        // Keep animating for shooting stars + twinkling
        postInvalidateDelayed(40); // ~25fps
    }

    // ── Smooth sensor update ───────────────────────────────
    public void updateOrientation(double rawAz, double rawAlt) {
        // Low-pass filter — smooth out sensor jitter
        smoothAz  = smoothAz  + SMOOTH_FACTOR * (rawAz  - smoothAz);
        smoothAlt = smoothAlt + SMOOTH_FACTOR * (rawAlt - smoothAlt);
        this.azimuth  = rawAz;
        this.altitude = rawAlt;
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
}