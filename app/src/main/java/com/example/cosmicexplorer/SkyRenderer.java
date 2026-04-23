package com.example.cosmicexplorer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;

import java.util.List;

public class SkyRenderer {

    private Paint starPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint labelPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint planetPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint nightPaint  = new Paint();
    private Paint hlPaint     = new Paint(Paint.ANTI_ALIAS_FLAG); // highlight ring

    private boolean nightMode = false;
    private List<String> highlightedStars = null;
    private SkyCulture.Culture currentCulture = SkyCulture.Culture.WESTERN;
    private String[] cultureConstellationNames = SkyCulture.getConstellationNames(SkyCulture.Culture.WESTERN);

    public SkyRenderer() {
        linePaint.setStrokeWidth(1.2f);
        linePaint.setStyle(Paint.Style.STROKE);
        labelPaint.setTextSize(28f);
        labelPaint.setFakeBoldText(true);
        nightPaint.setColor(0xAA330000);
        nightPaint.setStyle(Paint.Style.FILL);
        hlPaint.setStyle(Paint.Style.STROKE);
        hlPaint.setStrokeWidth(3f);
        hlPaint.setColor(0xFFFFFF00);
    }

    public void setNightMode(boolean on)                    { this.nightMode = on; }
    public void setHighlightedStars(List<String> names)     { this.highlightedStars = names; }
    public void setSkyCulture(SkyCulture.Culture culture)   {
        this.currentCulture = culture;
        this.cultureConstellationNames = SkyCulture.getConstellationNames(culture);
    }

    public void render(Canvas canvas,
                       List<StarCatalog.Star> stars,
                       List<StarCatalog.Constellation> constellations,
                       double azimuth, double altitude,
                       int w, int h, float fov, String searchQuery) {
        // ✅ Deep space background — paint tiny background stars for density
        Paint bgStarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgStarPaint.setColor(nightMode ? 0x33FF2200 : 0x44FFFFFF);
        java.util.Random bgRandom = new java.util.Random(42); // fixed seed = same stars every frame
        for (int s = 0; s < 300; s++) {
            float sx = bgRandom.nextFloat() * w;
            float sy = bgRandom.nextFloat() * h;
            float sr = bgRandom.nextFloat() * 1.5f;
            canvas.drawCircle(sx, sy, sr, bgStarPaint);
        }

        if (nightMode) canvas.drawRect(0, 0, w, h, nightPaint);

        float[][] positions = new float[stars.size()][];
        for (int i = 0; i < stars.size(); i++) {
            positions[i] = AstroMath.project(
                    stars.get(i).ra, stars.get(i).dec,
                    azimuth, altitude, w, h, fov);
        }

        drawConstellationLines(canvas, constellations, positions, cultureConstellationNames);

        for (int i = 0; i < stars.size(); i++) {
            float[] pos = positions[i];
            if (pos == null) continue;

            StarCatalog.Star star = stars.get(i);
            float radius = AstroMath.starRadius(star.magnitude);
            boolean isHighlighted = highlightedStars != null
                    && highlightedStars.contains(star.name);
            boolean isSearchMatch = searchQuery != null && !searchQuery.isEmpty()
                    && star.name.toLowerCase().contains(searchQuery.toLowerCase());

            if (star.isPlanet) {
                drawPlanet(canvas, pos[0], pos[1], radius, star, isHighlighted);
            } else {
                drawStar(canvas, pos[0], pos[1], radius, star.color, isHighlighted);
            }

            // ✅ Yellow highlight ring for tour/search
            if (isHighlighted || isSearchMatch) {
                hlPaint.setColor(isSearchMatch ? 0xFFFFFF00 : 0xFF00FFAA);
                canvas.drawCircle(pos[0], pos[1], radius + 8f, hlPaint);
            }

            if (star.magnitude < 1.5f || isSearchMatch || isHighlighted) {
                drawLabel(canvas, pos[0], pos[1], star.name, radius,
                        isSearchMatch, isHighlighted);
            }
        }
    }

    private void drawStar(Canvas canvas, float x, float y,
                          float r, int color, boolean highlighted) {
        int col = nightMode ? 0xFFFF4422 : color;
        Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setShader(new RadialGradient(x, y, r * (highlighted ? 4f : 2.5f),
                new int[]{col, Color.TRANSPARENT},
                new float[]{0f, 1f}, Shader.TileMode.CLAMP));
        canvas.drawCircle(x, y, r * (highlighted ? 4f : 2.5f), glowPaint);
        starPaint.setColor(col);
        canvas.drawCircle(x, y, r, starPaint);
    }

    private void drawPlanet(Canvas canvas, float x, float y,
                            float r, StarCatalog.Star planet, boolean highlighted) {
        Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setColor(planet.color);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(highlighted ? 3f : 2f);
        canvas.drawCircle(x, y, r + (highlighted ? 7f : 4f), ringPaint);
        planetPaint.setColor(planet.color);
        canvas.drawCircle(x, y, r, planetPaint);
        drawLabel(canvas, x, y, planet.name, r, false, highlighted);
    }

    private void drawConstellationLines(Canvas canvas,
                                        List<StarCatalog.Constellation> constellations,
                                        float[][] positions,
                                        String[] cultureNames) {
        for (int ci = 0; ci < constellations.size(); ci++) {
            StarCatalog.Constellation c = constellations.get(ci);

            // Use culture name if available
            String displayName = (cultureNames != null && ci < cultureNames.length)
                    ? cultureNames[ci] : c.name;

            float firstX = -1, firstY = -1;

            for (int i = 0; i < c.starIndices.length - 1; i += 2) {
                int a = c.starIndices[i], b = c.starIndices[i + 1];
                if (a >= positions.length || b >= positions.length) continue;
                float[] pa = positions[a], pb = positions[b];
                if (pa == null || pb == null) continue;

                linePaint.setColor(nightMode ? 0x66FF3300 : 0x4488BBFF);
                canvas.drawLine(pa[0], pa[1], pb[0], pb[1], linePaint);

                if (firstX < 0) { firstX = pa[0]; firstY = pa[1]; }
            }

            // Draw culture constellation name near first star
            if (firstX >= 0) {
                labelPaint.setTextSize(22f);
                labelPaint.setColor(nightMode ? 0xAAFF5500 : 0x99AADDFF);
                canvas.drawText(displayName, firstX + 6, firstY - 16, labelPaint);
            }
        }
    }

    private void drawLabel(Canvas canvas, float x, float y, String name,
                           float r, boolean isSearch, boolean isHighlight) {
        if (isSearch) {
            labelPaint.setColor(0xFFFFFF00);
            labelPaint.setTextSize(34f);
        } else if (isHighlight) {
            labelPaint.setColor(0xFF00FFAA);
            labelPaint.setTextSize(30f);
        } else {
            labelPaint.setColor(nightMode ? 0xFFFF6644 : 0xFFCCDDFF);
            labelPaint.setTextSize(26f);
        }
        canvas.drawText(name, x + r + 6f, y - r, labelPaint);
    }
}