package com.example.cosmicexplorer;

public class AstroMath {

    // Convert RA/Dec to Altitude/Azimuth based on device orientation
    // azimuthDeg  = compass heading (0=North, 90=East)
    // altitudeDeg = tilt up from horizon
    // Returns float[]{x, y} in screen pixels, or null if behind viewer

    public static float[] project(
            double starRaDeg,
            double starDecDeg,
            double azimuthDeg,
            double altitudeDeg,
            int screenW,
            int screenH,
            float fovDeg
    ) {
        // Convert star RA/Dec to a direction vector
        // We use a simple approximation mapping RA → azimuth offset, Dec → altitude offset
        // This is not astronomically precise but visually convincing for the app

        double raRad  = Math.toRadians(starRaDeg);
        double decRad = Math.toRadians(starDecDeg);

        // Star direction in sky (simplified equatorial → horizon)
        double starAz  = starRaDeg;        // treat RA as azimuth offset
        double starAlt = starDecDeg;       // treat Dec as altitude

        // Delta from where phone is pointing
        double dAz  = starAz  - azimuthDeg;
        double dAlt = starAlt - altitudeDeg;

        // Normalize dAz to [-180, 180]
        while (dAz >  180) dAz -= 360;
        while (dAz < -180) dAz += 360;

        // If outside FOV, don't draw
        float halfFov = fovDeg / 2f;
        if (Math.abs(dAz) > halfFov * 1.5 || Math.abs(dAlt) > halfFov * 1.2) return null;

        // Map to screen
        float x = (float) (screenW / 2.0 + (dAz  / halfFov) * (screenW / 2.0));
        float y = (float) (screenH / 2.0 - (dAlt / halfFov) * (screenH / 2.0));

        return new float[]{x, y};
    }

    // Star radius on screen based on magnitude (brighter = bigger)
    public static float starRadius(float magnitude) {
        // magnitude scale: -4 (brightest) to 6 (faint)
        float r = 14f - magnitude * 2.2f;
        return Math.max(2f, Math.min(r, 18f));
    }
}