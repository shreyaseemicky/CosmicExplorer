package com.example.cosmicexplorer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.RectF;

import java.util.List;
import java.util.Random;

public class SkyRenderer {

    private Paint starPaint      = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint      = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint labelPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint constNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint planetPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint nightPaint     = new Paint();
    private Paint hlPaint        = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint fovPaint       = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint clockPaint     = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint milkyWayPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);

    private boolean    nightMode = false;
    private List<String> highlightedStars = null;
    private SkyCulture.Culture currentCulture =
            SkyCulture.Culture.WESTERN;
    private String[] cultureConstellationNames =
            SkyCulture.getConstellationNames(SkyCulture.Culture.WESTERN);

    private Context context;

    // Twinkle state
    private float[] twinklePhase;
    private Random  twinkleRng = new Random(99);

    public SkyRenderer(Context context) {
        this.context = context;

        linePaint.setStrokeWidth(2f);
        linePaint.setStyle(Paint.Style.STROKE);

        labelPaint.setTextSize(26f);
        labelPaint.setTypeface(
                Typeface.create("sans-serif-light", Typeface.NORMAL));

        constNamePaint.setTextSize(28f);
        constNamePaint.setLetterSpacing(0.25f);
        constNamePaint.setTypeface(
                Typeface.create("sans-serif-light", Typeface.NORMAL));
        constNamePaint.setTextAlign(Paint.Align.CENTER);

        nightPaint.setColor(0xAA330000);
        nightPaint.setStyle(Paint.Style.FILL);

        hlPaint.setStyle(Paint.Style.STROKE);
        hlPaint.setStrokeWidth(3f);

        fovPaint.setTextAlign(Paint.Align.CENTER);
        fovPaint.setTextSize(28f);
        fovPaint.setTypeface(
                Typeface.create("sans-serif-light", Typeface.NORMAL));

        clockPaint.setTextSize(52f);
        clockPaint.setTypeface(
                Typeface.create("sans-serif-light", Typeface.NORMAL));
        clockPaint.setTextAlign(Paint.Align.RIGHT);

        milkyWayPaint.setStyle(Paint.Style.FILL);
        milkyWayPaint.setAntiAlias(true);

        // Init twinkle phases for up to 50 named stars
        twinklePhase = new float[50];
        for (int i = 0; i < twinklePhase.length; i++)
            twinklePhase[i] = twinkleRng.nextFloat() * 6.28f;
    }

    public void setNightMode(boolean on)                  { this.nightMode = on; }
    public void setHighlightedStars(List<String> names)   { this.highlightedStars = names; }
    public void setSkyCulture(SkyCulture.Culture culture) {
        this.currentCulture = culture;
        this.cultureConstellationNames =
                SkyCulture.getConstellationNames(culture);
    }

    public void render(Canvas canvas,
                       List<StarCatalog.Star> stars,
                       List<StarCatalog.Constellation> constellations,
                       double azimuth, double altitude,
                       int w, int h, float fov,
                       String searchQuery,
                       ShootingStarManager shootingStars) {

        // ── 1. Background tiny stars ───────────────────────
        drawBackgroundStars(canvas, w, h);

        // ── 2. Milky Way band ──────────────────────────────
        drawMilkyWay(canvas, w, h, azimuth, altitude);

        // ── 3. Night mode tint ─────────────────────────────
        if (nightMode) canvas.drawRect(0, 0, w, h, nightPaint);

        // ── 4. Shooting stars ──────────────────────────────
        shootingStars.update(w, h);
        shootingStars.draw(canvas, nightMode);

        // ── 5. Project star positions ──────────────────────
        float[][] positions = new float[stars.size()][];
        for (int i = 0; i < stars.size(); i++) {
            positions[i] = AstroMath.project(
                    stars.get(i).ra, stars.get(i).dec,
                    azimuth, altitude, w, h, fov);
        }

        // ── 6. Constellation art + lines ───────────────────
        drawConstellationsWithArt(canvas, constellations, positions, w, h);

        // ── 7. Stars with twinkling ────────────────────────
        for (int i = 0; i < stars.size(); i++) {
            float[] pos = positions[i];
            if (pos == null) continue;

            StarCatalog.Star star = stars.get(i);
            float radius = AstroMath.starRadius(star.magnitude);

            boolean isHighlighted = highlightedStars != null
                    && highlightedStars.contains(star.name);
            boolean isSearchMatch = searchQuery != null
                    && !searchQuery.isEmpty()
                    && star.name.toLowerCase()
                    .contains(searchQuery.toLowerCase());

            // Twinkle — vary radius slightly
            float twinkle = 1f;
            if (i < twinklePhase.length) {
                twinklePhase[i] += 0.04f; // advance phase each frame
                twinkle = 0.85f + 0.15f
                        * (float) Math.sin(twinklePhase[i]);
            }
            float twinkledRadius = radius * twinkle;

            if (star.isPlanet) {
                drawPlanet(canvas, pos[0], pos[1],
                        twinkledRadius, star, isHighlighted);
            } else {
                drawStar(canvas, pos[0], pos[1],
                        twinkledRadius, star.color, isHighlighted);
            }

            // Highlight ring
            if (isHighlighted || isSearchMatch) {
                hlPaint.setColor(isSearchMatch
                        ? 0xFFFFFF00 : 0xFF00FFAA);
                canvas.drawCircle(pos[0], pos[1],
                        twinkledRadius + 8f, hlPaint);
            }

            // Labels
            if (star.magnitude < 1.5f || isSearchMatch || isHighlighted) {
                drawStarLabel(canvas, pos[0], pos[1], star.name,
                        twinkledRadius, isSearchMatch, isHighlighted);
            }
        }

        // ── 8. FOV + Clock ─────────────────────────────────
        fovPaint.setColor(nightMode ? 0x88FF4400 : 0x88AACCDD);
        canvas.drawText(String.format("FOV %.1f°", fov),
                w / 2f, h - 80f, fovPaint);

        clockPaint.setColor(nightMode ? 0xAAFF4400 : 0xAAFFFFFF);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String time = String.format("%02d:%02d",
                cal.get(java.util.Calendar.HOUR_OF_DAY),
                cal.get(java.util.Calendar.MINUTE));
        canvas.drawText(time, w - 24f, h - 24f, clockPaint);
    }

    // ── Milky Way band ─────────────────────────────────────
    private void drawMilkyWay(Canvas canvas, int w, int h,
                              double azimuth, double altitude) {
        // Soft glowing diagonal band
        // Position shifts slightly with azimuth for realism
        float offsetX = (float)(azimuth * 0.8) % w;

        Paint mw = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Outer glow — wide, very faint
        mw.setShader(new RadialGradient(
                w * 0.5f + offsetX * 0.3f, h * 0.4f,
                w * 0.85f,
                new int[]{
                        nightMode ? 0x08FF2200 : 0x0A1A2A4A,
                        nightMode ? 0x04FF1100 : 0x06121828,
                        Color.TRANSPARENT
                },
                new float[]{0f, 0.6f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, mw);

        // Inner core — narrower, brighter
        Paint core = new Paint(Paint.ANTI_ALIAS_FLAG);
        core.setShader(new RadialGradient(
                w * 0.5f + offsetX * 0.2f, h * 0.45f,
                w * 0.35f,
                new int[]{
                        nightMode ? 0x18FF3300 : 0x14243858,
                        Color.TRANSPARENT
                },
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, w, h, core);

        // Dusty star clusters along the band
        Random rng = new Random(77);
        Paint dustPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < 60; i++) {
            float dx = (rng.nextFloat() - 0.5f) * w * 0.7f;
            float dy = (rng.nextFloat() - 0.5f) * h * 0.5f;
            float cx = w * 0.5f + dx + offsetX * 0.1f;
            float cy = h * 0.45f + dy;
            float cr = 1f + rng.nextFloat() * 2f;
            int alpha = 20 + rng.nextInt(40);
            dustPaint.setColor(nightMode
                    ? Color.argb(alpha, 255, 80, 30)
                    : Color.argb(alpha, 180, 200, 255));
            canvas.drawCircle(cx, cy, cr, dustPaint);
        }
    }

    // ── Background star field ──────────────────────────────
    private void drawBackgroundStars(Canvas canvas, int w, int h) {
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Random rng = new Random(42);
        for (int i = 0; i < 450; i++) {
            float sx = rng.nextFloat() * w;
            float sy = rng.nextFloat() * h;
            float sr = 0.3f + rng.nextFloat() * 1.3f;
            int alpha = 50 + rng.nextInt(130);
            bgPaint.setColor(nightMode
                    ? Color.argb(alpha, 180, 60, 20)
                    : Color.argb(alpha, 210, 220, 255));
            canvas.drawCircle(sx, sy, sr, bgPaint);
        }
    }

    // ── Constellation art + lines + name ──────────────────
    private void drawConstellationsWithArt(Canvas canvas,
                                           List<StarCatalog.Constellation> constellations,
                                           float[][] positions,
                                           int w, int h) {
        for (int ci = 0; ci < constellations.size(); ci++) {
            StarCatalog.Constellation c = constellations.get(ci);
            String displayName = (cultureConstellationNames != null
                    && ci < cultureConstellationNames.length)
                    ? cultureConstellationNames[ci] : c.name;

            // Find center
            float sumX = 0, sumY = 0;
            int count = 0;
            for (int idx : c.starIndices) {
                if (idx < positions.length && positions[idx] != null) {
                    sumX += positions[idx][0];
                    sumY += positions[idx][1];
                    count++;
                }
            }
            if (count == 0) continue;

            float cx = sumX / count;
            float cy = sumY / count;

            // Art figure
            if (context != null)
                ConstellationArt.draw(canvas, context,
                        c.name, cx, cy, 340f, nightMode);

            // Lines — cyan like Stellarium
            linePaint.setColor(nightMode ? 0x99FF3300 : 0x99009999);
            linePaint.setStrokeWidth(2.2f);
            for (int i = 0; i < c.starIndices.length - 1; i += 2) {
                int a = c.starIndices[i], b = c.starIndices[i + 1];
                if (a >= positions.length || b >= positions.length) continue;
                float[] pa = positions[a], pb = positions[b];
                if (pa == null || pb == null) continue;
                canvas.drawLine(pa[0], pa[1], pb[0], pb[1], linePaint);
            }

            // Spaced name in center
            if (cx > 0 && cx < w && cy > 0 && cy < h) {
                constNamePaint.setColor(nightMode
                        ? 0xAAFF5500 : 0xAA00CCBB);
                canvas.drawText(spacedText(displayName.toUpperCase()),
                        cx, cy + 20f, constNamePaint);
            }
        }
    }

    private String spacedText(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (i > 0) sb.append("  ");
            sb.append(s.charAt(i));
        }
        return sb.toString();
    }

    private void drawStar(Canvas canvas, float x, float y,
                          float r, int color, boolean highlighted) {
        int col = nightMode ? 0xFFFF4422 : color;
        Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        float glowR = r * (highlighted ? 5f : 3f);
        glowPaint.setShader(new RadialGradient(x, y, glowR,
                new int[]{col, Color.TRANSPARENT},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(x, y, glowR, glowPaint);
        starPaint.setColor(col);
        canvas.drawCircle(x, y, r, starPaint);
    }

    private void drawPlanet(Canvas canvas, float x, float y,
                            float r, StarCatalog.Star planet,
                            boolean highlighted) {
        Paint glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint.setShader(new RadialGradient(x, y, r * 4f,
                new int[]{planet.color, Color.TRANSPARENT},
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(x, y, r * 4f, glowPaint);

        Paint ringPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ringPaint.setColor(planet.color);
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setStrokeWidth(highlighted ? 3f : 1.5f);
        canvas.drawCircle(x, y,
                r + (highlighted ? 7f : 4f), ringPaint);

        planetPaint.setColor(planet.color);
        canvas.drawCircle(x, y, r, planetPaint);
        drawStarLabel(canvas, x, y, planet.name,
                r, false, highlighted);
    }

    private void drawStarLabel(Canvas canvas, float x, float y,
                               String name, float r,
                               boolean isSearch, boolean isHighlight) {
        if (isSearch) {
            labelPaint.setColor(0xFFFFFF00);
            labelPaint.setTextSize(34f);
        } else if (isHighlight) {
            labelPaint.setColor(0xFF00FFAA);
            labelPaint.setTextSize(30f);
        } else {
            labelPaint.setColor(nightMode ? 0xCCFF6644 : 0xCCAADDFF);
            labelPaint.setTextSize(24f);
        }
        canvas.drawText(name, x + r + 6f, y - r, labelPaint);
    }
}