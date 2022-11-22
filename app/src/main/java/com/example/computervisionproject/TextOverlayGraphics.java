package com.example.computervisionproject;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.computervisionproject.camera.CameraOverlay;
import com.google.android.gms.vision.text.TextBlock;

public class TextOverlayGraphics extends CameraOverlay.OverlayGraphic {
    private static final float BOX_STROKE_WIDTH = 5.0f;

    private final Paint mBoxPaint;
    private volatile TextBlock mText;

    TextOverlayGraphics(CameraOverlay overlay) {
        super(overlay);
        final int selectedColor = Color.YELLOW;
        mBoxPaint = new Paint();
        mBoxPaint.setColor(selectedColor);
        mBoxPaint.setStyle(Paint.Style.STROKE);
        mBoxPaint.setStrokeWidth(BOX_STROKE_WIDTH);
    }

    void updateTextBlock(TextBlock textBlock) {
        mText = textBlock;
        postInvalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mText == null) {
            return;
        }

        float left = translateX(mText.getBoundingBox().left);
        float top = translateY(mText.getBoundingBox().top);
        float right = translateX(mText.getBoundingBox().right);
        float bottom = translateY(mText.getBoundingBox().bottom);
        canvas.drawRect(left, top, right, bottom, mBoxPaint);
    }
}
