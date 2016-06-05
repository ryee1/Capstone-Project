//The MIT License (MIT)
//
//        Copyright (c) 2015 Maxwell Forest
//
//        Permission is hereby granted, free of charge, to any person obtaining a copy
//        of this software and associated documentation files (the "Software"), to deal
//        in the Software without restriction, including without limitation the rights
//        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
//        copies of the Software, and to permit persons to whom the Software is
//        furnished to do so, subject to the following conditions:
//
//        The above copyright notice and this permission notice shall be included in all
//        copies or substantial portions of the Software.
//
//        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
//        SOFTWARE.

package com.project.richard.insightjournal;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import java.util.concurrent.TimeUnit;


public class TimerView extends View {

    private static final int ARC_START_ANGLE = 270; // 12 o'clock

    private static final float THICKNESS_SCALE = 0.03f;

    private Bitmap mBitmap;
    private Canvas mCanvas;

    private RectF mCircleOuterBounds;
    private RectF mCircleInnerBounds;

    private Paint mCirclePaint;
    private Paint mEraserPaint;

    private float mCircleSweepAngle;

    private ValueAnimator mTimerAnimator;

    public TimerView(Context context) {
        this(context, null);
    }

    public TimerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int circleColor = Color.RED;

        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimerView);
            if (ta != null) {
                circleColor = ta.getColor(R.styleable.TimerView_circleColor, circleColor);
                ta.recycle();
            }
        }

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(circleColor);

        mEraserPaint = new Paint();
        mEraserPaint.setAntiAlias(true);
        mEraserPaint.setColor(Color.TRANSPARENT);
        mEraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // Trick to make the view square
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw || h != oldh) {
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mBitmap.eraseColor(Color.TRANSPARENT);
            mCanvas = new Canvas(mBitmap);
        }

        super.onSizeChanged(w, h, oldw, oldh);
        updateBounds();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mCanvas.drawColor(0, PorterDuff.Mode.CLEAR);

        if (mCircleSweepAngle > 0f) {
            mCanvas.drawArc(mCircleOuterBounds, ARC_START_ANGLE, mCircleSweepAngle, true, mCirclePaint);
            mCanvas.drawOval(mCircleInnerBounds, mEraserPaint);
        }

        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    public void start(int secs) {
        stop();

        mTimerAnimator = ValueAnimator.ofFloat(0f, 1f);
        mTimerAnimator.setDuration(TimeUnit.SECONDS.toMillis(secs));
        mTimerAnimator.setInterpolator(new LinearInterpolator());
        mTimerAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawProgress((float) animation.getAnimatedValue());
            }
        });
        mTimerAnimator.start();
    }

    public void stop() {
        if (mTimerAnimator != null && mTimerAnimator.isRunning()) {
            mTimerAnimator.cancel();
            mTimerAnimator = null;

            drawProgress(0f);
        }
    }

    private void drawProgress(float progress) {
        mCircleSweepAngle = 360 * progress;

        invalidate();
    }

    private void updateBounds() {
        final float thickness = getWidth() * THICKNESS_SCALE;

        mCircleOuterBounds = new RectF(0, 0, getWidth(), getHeight());
        mCircleInnerBounds = new RectF(
                mCircleOuterBounds.left + thickness,
                mCircleOuterBounds.top + thickness,
                mCircleOuterBounds.right - thickness,
                mCircleOuterBounds.bottom - thickness);

        invalidate();
    }
}