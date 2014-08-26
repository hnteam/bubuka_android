package ru.espepe.bubuka.player.helper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by wolong on 26/08/14.
 */
public class ProgressBackgroundDrawable extends Drawable {
    private Paint mainPaint;
    private Paint borderPaint;
    private float progress;
    private RectF rect;
    public ProgressBackgroundDrawable(float progress) {
        mainPaint = new Paint();
        mainPaint.setColor(Color.parseColor("#00a1fe"));
        mainPaint.setStyle(Paint.Style.FILL);
        mainPaint.setAlpha(25);

        borderPaint = new Paint();
        borderPaint.setStrokeWidth(1.0f);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.parseColor("#cccccc"));
        this.progress = progress;

        rect = new RectF(0, 0, 0, 0);
    }

    @Override
    public void draw(Canvas canvas) {
        rect.set(0, 0, canvas.getWidth() * progress, canvas.getHeight());
        canvas.drawRect(rect, mainPaint);
        canvas.drawRect(rect, borderPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
