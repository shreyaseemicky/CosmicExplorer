package com.example.cosmicexplorer;

public class AstroMath {

    public static float[] project(
            double starRaDeg,
            double starDecDeg,
            double azimuthDeg,
            double altitudeDeg,
            int screenW,
            int screenH,
            float fovDeg) {

        // Delta from where phone points
        double dAz  = starRaDeg  - azimuthDeg;
        double dAlt = starDecDeg - altitudeDeg;

        // ✅ Normalize azimuth delta to [-180, 180]
        while (dAz >  180) dAz -= 360;
        while (dAz < -180) dAz += 360;

        // ✅ Use a wider acceptance window so stars show
        float halfFovW = fovDeg * 0.7f;
        float halfFovH = fovDeg * 0.9f;

        if (Math.abs(dAz)  > halfFovW) return null;
        if (Math.abs(dAlt) > halfFovH) return null;

        // Map to screen pixels
        float x = (float)(screenW / 2.0
                + (dAz  / halfFovW) * (screenW / 2.0));
        float y = (float)(screenH / 2.0
                - (dAlt / halfFovH) * (screenH / 2.0));

        return new float[]{x, y};
    }

    // ✅ Bigger stars for bright objects
    public static float starRadius(float magnitude) {
        float r = 16f - magnitude * 2.5f;
        return Math.max(2.5f, Math.min(r, 20f));
    }
}