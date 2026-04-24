package com.example.cosmicexplorer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

public class ConstellationArt {

    // Maps constellation name → drawable resource id
    private static final Map<String, Integer> artMap = new HashMap<>();

    static {
        artMap.put("Orion",           R.drawable.const_orion);
        artMap.put("Summer Triangle", null); // asterism, no figure
        artMap.put("Gemini",          null);
        artMap.put("Scorpius",        R.drawable.const_scorpius);
        artMap.put("Leo",             R.drawable.const_leo);
    }

    public static Integer getDrawable(String constellationName) {
        return artMap.get(constellationName);
    }

    // Draw constellation art centered at a screen position
    public static void draw(Canvas canvas, Context context,
                            String name, float cx, float cy,
                            float sizePx, boolean nightMode) {
        Integer resId = getDrawable(name);
        if (resId == null) return;

        try {
            Drawable d = ContextCompat.getDrawable(context, resId);
            if (d == null) return;

            int half = (int)(sizePx / 2);
            int left   = (int) cx - half;
            int top    = (int) cy - half;
            int right  = (int) cx + half;
            int bottom = (int) cy + half;

            // Clamp to canvas bounds
            if (right < 0 || left > canvas.getWidth()) return;
            if (bottom < 0 || top > canvas.getHeight()) return;

            d.setBounds(left, top, right, bottom);

            // Night mode: draw with red tint by adjusting alpha
            if (nightMode) {
                d.setAlpha(60);
            } else {
                d.setAlpha(45); // subtle, like Stellarium
            }

            d.draw(canvas);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
