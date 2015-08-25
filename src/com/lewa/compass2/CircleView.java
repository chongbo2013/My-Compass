package com.lewa.compass2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CircleView extends View {
    String TAG = "CircleView";
    private int step = 0;
    Point CurrentPoint = new Point();
    Paint paint1 = new Paint();
    Paint paint2 = new Paint();
    Paint paint3 = new Paint();
    Paint paint4 = new Paint();

    public CircleView(Context context) {
        super(context);
    }

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        CurrentPoint.x = getHeight() / 2;
        CurrentPoint.y = getHeight() / 2;
        init_paint(paint1);
        init_paint(paint2);
        init_paint(paint3);
        init_paint(paint4);
        switch (step) {
        case 1:
        paint1.setColor(android.graphics.Color.WHITE);

            break;
        case 2:
            paint1.setColor(android.graphics.Color.WHITE);
            paint2.setColor(android.graphics.Color.WHITE);
            break;
        case 3:
            paint1.setColor(android.graphics.Color.WHITE);
            paint2.setColor(android.graphics.Color.WHITE);
            paint3.setColor(android.graphics.Color.WHITE);
            break;
        case 4:
            paint1.setColor(android.graphics.Color.WHITE);
            paint2.setColor(android.graphics.Color.WHITE);
            paint3.setColor(android.graphics.Color.WHITE);
            paint4.setColor(android.graphics.Color.WHITE);
            break;
        }

        canvas.drawCircle(CurrentPoint.x, CurrentPoint.y,
                UnitTransformUtil.dip2px(getContext(), 89), paint1);
        canvas.drawCircle(CurrentPoint.x, CurrentPoint.y,
                UnitTransformUtil.dip2px(getContext(), 100), paint2);
        canvas.drawCircle(CurrentPoint.x, CurrentPoint.y,
                UnitTransformUtil.dip2px(getContext(), 111), paint3);
        canvas.drawCircle(CurrentPoint.x, CurrentPoint.y,
                UnitTransformUtil.dip2px(getContext(), 122), paint4);
        canvas.save();
        canvas.restore();
    }

    void init_paint(Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(UnitTransformUtil.dip2px(getContext(), 1));
        paint.setAntiAlias(true);
        paint.setColor(0x4fFFFFFF);

    }

    public void updateStep(int step) {
        this.step = step;
        Log.i(TAG, "step" + step);
        invalidate();
    }

}
