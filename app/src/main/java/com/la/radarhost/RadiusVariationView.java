package com.la.radarhost;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


public class RadiusVariationView extends View {
    final String TAG = RadiusVariationView.class.getSimpleName();

    Paint mBorderPaint;
    Paint mBorderTextPaint;
    Paint mPointPaint;
    Paint mPointTextPaint;
    Paint mLinePaint;

    float innerTop = getTop()+getPaddingTop();
    float innerBottom = getBottom()-getPaddingBottom();
    float innerLeft = getLeft()+getPaddingLeft();
    float innerRight = getRight()-getPaddingRight();
    float[] Ys;
    float[] drawXs;
    float[] drawYs;

    private float cycleRadius = 10;

    // Global/Outer setting parameters
    private float maxDistance = 20;
    private float minDistance = 0;

    private int numTickY = 4;
    private int numTimeSeries = 101; // numTickX

    private Points mPoints = new Points(numTimeSeries);

    public RadiusVariationView(Context context)
    {
        this(context, null);
    }

    public RadiusVariationView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initView();
    }

    private void initView() {
        mBorderPaint = new Paint();
        mBorderPaint.setColor(Color.RED);
        mBorderTextPaint = new Paint();
        mBorderTextPaint.setColor(Color.RED);
        mPointPaint = new Paint();
        mPointPaint.setColor(Color.GREEN);
        mPointTextPaint = new Paint();
        mPointTextPaint.setColor(Color.GREEN);
        mLinePaint = new Paint();
        mLinePaint.setColor(Color.BLUE);
    }

    private void initDraw() {
        innerTop = getTop()+getPaddingTop();
        innerBottom = getBottom()-getPaddingBottom();
        innerLeft = getLeft()+getPaddingLeft();
        innerRight = getRight()-getPaddingRight();

        Ys = new float[numTimeSeries];
        drawXs = new float[numTimeSeries];
        drawYs = new float[numTimeSeries];

        float delta = getInnerWidth()/(numTimeSeries-1);
        int i = 0;
        while (mPoints.hasNext()) {
            Ys[i] = mPoints.next();
            drawXs[i] = innerLeft + delta*i;
            // there may be a bug when coords[1][i]
            drawYs[i] = innerBottom - (Ys[i]-minDistance)/(maxDistance-minDistance)*getInnerHeight();
            i++;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ////////
        // 1. How to implementation local update?
        ///////
        // location
        int top = getTop();
        int bottom = getBottom();
        int left = getLeft();
        int right = getRight();
        StringBuilder s = new StringBuilder();
        s.append("top: ");
        s.append(top);
        s.append(" bottom: ");
        s.append(bottom);
        s.append(" left: ");
        s.append(left);
        s.append(" right: ");
        s.append(right);
        Log.d(TAG, s.toString());

        // size
        int width = getWidth();
        int height = getHeight();
        int padding = getPaddingLeft();
        s = new StringBuilder();
        s.append("width: ");
        s.append(width);
        s.append(" height: ");
        s.append(height);
        s.append(" padding: ");
        s.append(padding);
        Log.d(TAG, s.toString());

        // common variables
        float sx; // startX
        float sy; // startY
        float ex; // end/stopX
        float ey; // end/stopY

        initDraw();


        // draw axis/inner block border
         drawBorder(canvas);
        // draw points
        drawCycles(canvas);
        // draw lines
        drawLines(canvas);

    }

    private void drawBorder(Canvas canvas) {
        float[] pts = {
                innerLeft,innerBottom,
                innerLeft,innerTop,
                innerLeft,innerTop,
                innerRight,innerTop,
                innerRight,innerTop,
                innerRight,innerBottom,
                innerRight,innerBottom,
                innerLeft,innerBottom
        };
        canvas.drawLines(pts, mBorderPaint);

        float delta = maxDistance / numTickY;
        float realDelta = (innerBottom-innerTop) / numTickY;
        for (int i=0;i<numTickY;i++) {
            canvas.drawText(
                    Float.toString(delta*i),
                    innerLeft+4,
                    innerBottom-realDelta*i,
                    mBorderTextPaint);
        }
    }
    private void drawCycles(Canvas canvas) {
        for (int i=0;i<numTimeSeries;i++) {
            canvas.drawCircle(drawXs[i], drawYs[i], cycleRadius, mPointPaint);
            canvas.drawText(Float.toString(Ys[i]), drawXs[i], drawYs[i]+4, mPointTextPaint);
        }
    }
    private void drawLines(Canvas canvas) {
        for (int i=0;i<numTimeSeries-1;i++) {
            canvas.drawLine(drawXs[i], drawYs[i], drawXs[i+1], drawYs[i+1], mLinePaint);
        }
    }

    private class Points{
        private float[] data;
        private int mark = 0;
        private int iter = 0;


        public Points(int length) {
            data = new float[length];
            Log.d(TAG, "array length:" + data.length);
        }

        void add(float v) {
            data[mark] = v;

            //handle mark
            mark = (mark == numTimeSeries-1) ? 0: ++mark;
        }

        boolean hasNext() {
            if (iter != numTimeSeries) {
                return true;
            }
            iter = 0;
            return false;
        }

        float next() {
            return data[(mark+iter++)%numTimeSeries];
        }

        public float[] getLineData() {
            float[] lineData = new float[numTimeSeries];
            System.arraycopy(data, mark, lineData, 0, numTimeSeries-mark);
            System.arraycopy(data, 0, lineData, numTimeSeries-mark, mark+1);
            return lineData;
        }
    }

    private float getInnerWidth() {
        return innerRight - innerLeft;
    }

    private float getInnerHeight() {
        return innerBottom - innerTop;
    }

    // update the view when a new point coming
    public void update(float newData) {
        mPoints.add(newData);
        invalidate();
    }



}
