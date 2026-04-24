package com.example.cosmicexplorer;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShootingStarManager {

    private static final int MAX_STARS   = 3;
    private static final int SPAWN_EVERY = 280; // frames between spawns (~11s at 25fps)

    private List<ShootingStar> active = new ArrayList<>();
    private Random rng   = new Random();
    private int frameCount = 0;
    private Paint paint  = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static class ShootingStar {
        float x, y;         // current head position
        float vx, vy;       // velocity per frame
        float length;       // trail length in pixels
        float alpha;        // 0–255
        float life;         // 0–1, decreases each frame
        float tailFade;     // used for gradient

        ShootingStar(float x, float y, float vx, float vy, float length) {
            this.x = x; this.y = y;
            this.vx = vx; this.vy = vy;
            this.length = length;
            this.alpha = 255f;
            this.life = 1.0f;
        }
    }

    public void update(int screenW, int screenH) {
        frameCount++;

        // Spawn new shooting star periodically
        if (frameCount % SPAWN_EVERY == 0 && active.size() < MAX_STARS) {
            spawnStar(screenW, screenH);
        }

        // Update existing stars
        List<ShootingStar> toRemove = new ArrayList<>();
        for (ShootingStar s : active) {
            s.x    += s.vx;
            s.y    += s.vy;
            s.life -= 0.018f;
            s.alpha = s.life * 255f;

            if (s.life <= 0 || s.x < -200 || s.x > screenW + 200
                    || s.y < -200 || s.y > screenH + 200) {
                toRemove.add(s);
            }
        }
        active.removeAll(toRemove);
    }

    private void spawnStar(int w, int h) {
        // Start from top or left edge, streak diagonally downward
        float startX = rng.nextFloat() * w;
        float startY = rng.nextFloat() * (h * 0.5f); // upper half

        float angle = (float)(Math.toRadians(30 + rng.nextFloat() * 30)); // 30–60°
        float speed = 12f + rng.nextFloat() * 10f;
        float vx    = (float)(Math.cos(angle) * speed);
        float vy    = (float)(Math.sin(angle) * speed);
        float len   = 80f + rng.nextFloat() * 120f;

        active.add(new ShootingStar(startX, startY, vx, vy, len));
    }

    public void draw(Canvas canvas, boolean nightMode) {
        for (ShootingStar s : active) {
            if (s.alpha <= 0) continue;

            float tailX = s.x - s.vx * (s.length / Math.max(1f,
                    (float)Math.hypot(s.vx, s.vy)));
            float tailY = s.y - s.vy * (s.length / Math.max(1f,
                    (float)Math.hypot(s.vx, s.vy)));

            int headColor = nightMode
                    ? Color.argb((int)s.alpha, 255, 100, 50)
                    : Color.argb((int)s.alpha, 255, 255, 255);
            int tailColor = Color.argb(0, 255, 255, 255);

            LinearGradient grad = new LinearGradient(
                    s.x, s.y, tailX, tailY,
                    headColor, tailColor,
                    Shader.TileMode.CLAMP);
            paint.setShader(grad);
            paint.setStrokeWidth(2.5f);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(s.x, s.y, tailX, tailY, paint);

            // Bright head dot
            Paint headPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            headPaint.setColor(headColor);
            headPaint.setShader(null);
            canvas.drawCircle(s.x, s.y, 3f, headPaint);
        }
    }
}
