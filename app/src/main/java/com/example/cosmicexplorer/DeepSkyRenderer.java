package com.example.cosmicexplorer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;

import java.util.List;
import java.util.Random;

public class DeepSkyRenderer {

    private Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean nightMode = false;

    public DeepSkyRenderer() {
        labelPaint.setTextSize(22f);
        labelPaint.setColor(0xAAAADDFF);
    }

    public void setNightMode(boolean on) {
        this.nightMode = on;
    }

    public void render(Canvas canvas,
                       List<DeepSkyObject> objects,
                       double azimuth, double altitude,
                       int w, int h, float fov) {

        for (DeepSkyObject dso : objects) {
            float[] pos = AstroMath.project(
                    dso.ra, dso.dec,
                    azimuth, altitude, w, h, fov);
            if (pos == null) continue;

            // Size on screen based on angular size + FOV
            float screenRadius = (dso.angularSize / fov) * w * 0.6f;
            screenRadius = Math.max(8f, Math.min(screenRadius, w * 0.4f));

            switch (dso.type) {
                case GALAXY:
                    drawGalaxy(canvas, pos[0], pos[1],
                            screenRadius, dso);
                    break;
                case NEBULA:
                    drawNebula(canvas, pos[0], pos[1],
                            screenRadius, dso);
                    break;
                case SUPERNOVA_REMNANT:
                    drawSupernovaRemnant(canvas, pos[0], pos[1],
                            screenRadius, dso);
                    break;
                case CLUSTER:
                    drawCluster(canvas, pos[0], pos[1],
                            screenRadius, dso);
                    break;
            }

            // Label
            drawLabel(canvas, pos[0], pos[1],
                    screenRadius, dso.name, dso.type);
        }
    }

    // ── Galaxy — tilted ellipse with bright core ──────────
    private void drawGalaxy(Canvas canvas, float cx, float cy,
                            float r, DeepSkyObject dso) {

        // Outer diffuse glow
        Paint outerGlow = new Paint(Paint.ANTI_ALIAS_FLAG);
        outerGlow.setShader(new RadialGradient(cx, cy, r * 1.8f,
                new int[]{
                        adjustAlpha(dso.glowColor, nightMode ? 0.08f : 0.12f),
                        Color.TRANSPARENT
                },
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP));
        canvas.save();
        canvas.scale(1f, 0.38f, cx, cy); // flatten into ellipse
        canvas.rotate(35f, cx, cy);       // tilt like Andromeda
        canvas.drawCircle(cx, cy, r * 1.8f, outerGlow);
        canvas.restore();

        // Mid disk
        Paint midDisk = new Paint(Paint.ANTI_ALIAS_FLAG);
        midDisk.setShader(new RadialGradient(cx, cy, r,
                new int[]{
                        adjustAlpha(dso.glowColor, nightMode ? 0.18f : 0.28f),
                        adjustAlpha(dso.glowColor, nightMode ? 0.06f : 0.10f),
                        Color.TRANSPARENT
                },
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP));
        canvas.save();
        canvas.scale(1f, 0.35f, cx, cy);
        canvas.rotate(35f, cx, cy);
        canvas.drawCircle(cx, cy, r, midDisk);
        canvas.restore();

        // Bright core
        Paint core = new Paint(Paint.ANTI_ALIAS_FLAG);
        core.setShader(new RadialGradient(cx, cy, r * 0.25f,
                new int[]{
                        adjustAlpha(dso.coreColor, nightMode ? 0.5f : 0.8f),
                        adjustAlpha(dso.coreColor, nightMode ? 0.2f : 0.35f),
                        Color.TRANSPARENT
                },
                new float[]{0f, 0.4f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r * 0.3f, core);

        // Dust lane — dark line across center
        Paint dustLane = new Paint(Paint.ANTI_ALIAS_FLAG);
        dustLane.setColor(0x22000000);
        dustLane.setStrokeWidth(r * 0.08f);
        dustLane.setStyle(Paint.Style.STROKE);
        canvas.save();
        canvas.rotate(35f, cx, cy);
        canvas.drawLine(cx - r * 0.7f, cy, cx + r * 0.7f, cy, dustLane);
        canvas.restore();

        // Scatter tiny cluster stars inside galaxy
        Random rng = new Random(dso.name.hashCode());
        Paint starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        for (int i = 0; i < 40; i++) {
            float angle = rng.nextFloat() * 360f;
            float dist  = rng.nextFloat() * r * 0.8f;
            float sx = cx + (float)(Math.cos(Math.toRadians(angle)) * dist);
            float sy = cy + (float)(Math.sin(Math.toRadians(angle))
                    * dist * 0.35f);
            float sr = 0.5f + rng.nextFloat() * 1.5f;
            int   sa = 30 + rng.nextInt(80);
            starPaint.setColor(Color.argb(sa,
                    (dso.coreColor >> 16) & 0xFF,
                    (dso.coreColor >>  8) & 0xFF,
                    dso.coreColor        & 0xFF));
            canvas.drawCircle(sx, sy, sr, starPaint);
        }

        // Spiral arm hints (for larger renders)
        if (r > 40f) {
            drawSpiralArms(canvas, cx, cy, r, dso);
        }
    }

    private void drawSpiralArms(Canvas canvas, float cx, float cy,
                                float r, DeepSkyObject dso) {
        Paint armPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        armPaint.setStyle(Paint.Style.STROKE);
        armPaint.setStrokeWidth(r * 0.06f);

        Random rng = new Random(dso.name.hashCode() + 1);

        // Two spiral arms
        for (int arm = 0; arm < 2; arm++) {
            float startAngle = arm * 180f;
            android.graphics.Path path = new android.graphics.Path();
            boolean first = true;

            for (float t = 0; t <= 1.0f; t += 0.05f) {
                float angle = (float)(Math.toRadians(
                        startAngle + t * 280f));
                float dist  = r * 0.15f + r * 0.7f * t;
                float px = cx + (float)(Math.cos(angle) * dist);
                float py = cy + (float)(Math.sin(angle)
                        * dist * 0.35f);

                if (first) {
                    path.moveTo(px, py);
                    first = false;
                } else {
                    path.lineTo(px, py);
                }
            }

            int alpha = nightMode ? 25 : 45;
            armPaint.setColor(Color.argb(alpha,
                    (dso.glowColor >> 16) & 0xFF,
                    (dso.glowColor >>  8) & 0xFF,
                    dso.glowColor        & 0xFF));
            canvas.save();
            canvas.rotate(35f, cx, cy);
            canvas.drawPath(path, armPaint);
            canvas.restore();
        }
    }

    // ── Nebula — soft irregular cloud ────────────────────
    private void drawNebula(Canvas canvas, float cx, float cy,
                            float r, DeepSkyObject dso) {
        Random rng = new Random(dso.name.hashCode());

        // Multiple overlapping soft blobs
        for (int blob = 0; blob < 5; blob++) {
            float bx = cx + (rng.nextFloat() - 0.5f) * r * 0.8f;
            float by = cy + (rng.nextFloat() - 0.5f) * r * 0.6f;
            float br = r * (0.3f + rng.nextFloat() * 0.5f);

            int baseAlpha = nightMode ? 30 : 55;
            Paint blobPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            blobPaint.setShader(new RadialGradient(bx, by, br,
                    new int[]{
                            adjustAlpha(dso.glowColor,
                                    (baseAlpha + rng.nextInt(30)) / 255f),
                            Color.TRANSPARENT
                    },
                    new float[]{0f, 1f},
                    Shader.TileMode.CLAMP));
            canvas.drawCircle(bx, by, br, blobPaint);
        }

        // Bright ionized center
        Paint center = new Paint(Paint.ANTI_ALIAS_FLAG);
        center.setShader(new RadialGradient(cx, cy, r * 0.3f,
                new int[]{
                        adjustAlpha(dso.coreColor, nightMode ? 0.4f : 0.65f),
                        Color.TRANSPARENT
                },
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r * 0.3f, center);

        // Bright central star(s)
        Paint starPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        starPaint.setColor(nightMode ? 0xCCFF4422 : 0xCCFFFFFF);
        canvas.drawCircle(cx, cy, 3f, starPaint);
    }

    // ── Supernova remnant — wispy expanding shell ────────
    private void drawSupernovaRemnant(Canvas canvas, float cx, float cy,
                                      float r, DeepSkyObject dso) {
        // Outer filamentary shell
        Paint shellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shellPaint.setStyle(Paint.Style.STROKE);
        shellPaint.setColor(adjustAlpha(dso.glowColor,
                nightMode ? 0.2f : 0.4f));
        shellPaint.setStrokeWidth(r * 0.12f);
        canvas.drawCircle(cx, cy, r * 0.85f, shellPaint);

        // Inner glow
        Paint innerGlow = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerGlow.setShader(new RadialGradient(cx, cy, r,
                new int[]{
                        adjustAlpha(dso.glowColor, nightMode ? 0.15f : 0.25f),
                        adjustAlpha(dso.coreColor, nightMode ? 0.05f : 0.10f),
                        Color.TRANSPARENT
                },
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r, innerGlow);

        // Pulsar dot at center
        Paint pulsar = new Paint(Paint.ANTI_ALIAS_FLAG);
        pulsar.setColor(nightMode ? 0xCCFF6622 : 0xCCAADDFF);
        canvas.drawCircle(cx, cy, 4f, pulsar);

        // Filament streaks
        Random rng = new Random(dso.name.hashCode());
        Paint filPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        filPaint.setStyle(Paint.Style.STROKE);
        filPaint.setStrokeWidth(1.2f);
        for (int i = 0; i < 12; i++) {
            float angle = rng.nextFloat() * 360f;
            float rad   = (float) Math.toRadians(angle);
            float x1    = cx + (float)(Math.cos(rad) * r * 0.4f);
            float y1    = cy + (float)(Math.sin(rad) * r * 0.4f);
            float x2    = cx + (float)(Math.cos(rad) * r * 0.9f);
            float y2    = cy + (float)(Math.sin(rad) * r * 0.9f);
            int alpha   = nightMode ? 40 : 70;
            filPaint.setColor(Color.argb(alpha,
                    (dso.coreColor >> 16) & 0xFF,
                    (dso.coreColor >>  8) & 0xFF,
                    dso.coreColor        & 0xFF));
            canvas.drawLine(x1, y1, x2, y2, filPaint);
        }
    }

    // ── Star cluster — many tightly packed stars ─────────
    private void drawCluster(Canvas canvas, float cx, float cy,
                             float r, DeepSkyObject dso) {
        Random rng = new Random(dso.name.hashCode());

        // Background haze
        Paint haze = new Paint(Paint.ANTI_ALIAS_FLAG);
        haze.setShader(new RadialGradient(cx, cy, r,
                new int[]{
                        adjustAlpha(dso.glowColor, nightMode ? 0.08f : 0.14f),
                        Color.TRANSPARENT
                },
                new float[]{0f, 1f},
                Shader.TileMode.CLAMP));
        canvas.drawCircle(cx, cy, r, haze);

        // Individual cluster stars — colored like the reference image
        int[] clusterColors = {
                0xFFCCDDFF,  // blue-white
                0xFFFFFFFF,  // white
                0xFFFFEECC,  // yellow-white
                0xFFFFCC88,  // orange
                0xFFFF8866,  // red-orange
        };

        int starCount = (int)(20 + r * 0.8f);
        Paint sp = new Paint(Paint.ANTI_ALIAS_FLAG);

        for (int i = 0; i < starCount; i++) {
            // Gaussian-ish distribution — more stars near center
            double angle = rng.nextDouble() * Math.PI * 2;
            double dist  = Math.abs(rng.nextGaussian()) * r * 0.35f;
            if (dist > r) continue;

            float sx = cx + (float)(Math.cos(angle) * dist);
            float sy = cy + (float)(Math.sin(angle) * dist);
            float sr = 0.8f + rng.nextFloat() * 2.5f;

            int colorIndex = rng.nextInt(clusterColors.length);
            int alpha = 120 + rng.nextInt(135);
            int col   = clusterColors[colorIndex];
            sp.setColor(Color.argb(alpha,
                    (col >> 16) & 0xFF,
                    (col >>  8) & 0xFF,
                    col        & 0xFF));

            // Draw glow around brighter stars
            if (sr > 2f) {
                Paint glowP = new Paint(Paint.ANTI_ALIAS_FLAG);
                glowP.setShader(new RadialGradient(sx, sy, sr * 3f,
                        new int[]{Color.argb(60,
                                (col >> 16) & 0xFF,
                                (col >>  8) & 0xFF,
                                col        & 0xFF),
                                Color.TRANSPARENT},
                        new float[]{0f, 1f},
                        Shader.TileMode.CLAMP));
                canvas.drawCircle(sx, sy, sr * 3f, glowP);
            }

            canvas.drawCircle(sx, sy, sr, sp);

            // Cross diffraction spikes for the brightest stars
            if (sr > 2.2f) {
                drawDiffractionSpike(canvas, sx, sy, sr, col, alpha);
            }
        }
    }

    // Diffraction spike (the + shape on bright stars in telescope photos)
    private void drawDiffractionSpike(Canvas canvas, float x, float y,
                                      float r, int color, int alpha) {
        Paint spike = new Paint(Paint.ANTI_ALIAS_FLAG);
        float spikeLen = r * 5f;
        spike.setStrokeWidth(0.8f);

        // Horizontal
        android.graphics.LinearGradient hGrad =
                new android.graphics.LinearGradient(
                        x - spikeLen, y, x + spikeLen, y,
                        new int[]{Color.TRANSPARENT,
                                Color.argb(alpha / 3,
                                        (color >> 16) & 0xFF,
                                        (color >>  8) & 0xFF,
                                        color        & 0xFF),
                                Color.TRANSPARENT},
                        new float[]{0f, 0.5f, 1f},
                        Shader.TileMode.CLAMP);
        spike.setShader(hGrad);
        canvas.drawLine(x - spikeLen, y, x + spikeLen, y, spike);

        // Vertical
        android.graphics.LinearGradient vGrad =
                new android.graphics.LinearGradient(
                        x, y - spikeLen, x, y + spikeLen,
                        new int[]{Color.TRANSPARENT,
                                Color.argb(alpha / 3,
                                        (color >> 16) & 0xFF,
                                        (color >>  8) & 0xFF,
                                        color        & 0xFF),
                                Color.TRANSPARENT},
                        new float[]{0f, 0.5f, 1f},
                        Shader.TileMode.CLAMP);
        spike.setShader(vGrad);
        canvas.drawLine(x, y - spikeLen, x, y + spikeLen, spike);
    }

    // ── DSO label ─────────────────────────────────────────
    private void drawLabel(Canvas canvas, float x, float y,
                           float r, String name, DeepSkyObject.DSType type) {
        String prefix;
        switch (type) {
            case GALAXY:            prefix = "⬭ "; break;
            case NEBULA:            prefix = "☁ "; break;
            case SUPERNOVA_REMNANT: prefix = "💥 "; break;
            case CLUSTER:           prefix = "✦ "; break;
            default:                prefix = "";
        }
        labelPaint.setColor(nightMode ? 0x88FF5522 : 0x88AADDFF);
        labelPaint.setTextSize(20f);
        canvas.drawText(prefix + name,
                x + r + 8f, y, labelPaint);
    }

    // ── Helper: adjust alpha of a color ──────────────────
    private int adjustAlpha(int color, float alpha) {
        int a = Math.min(255, (int)(alpha * 255));
        return (color & 0x00FFFFFF) | (a << 24);
    }
}
