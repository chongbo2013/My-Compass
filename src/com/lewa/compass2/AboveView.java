package com.lewa.compass2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class AboveView extends View {

    public AboveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public AboveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AboveView(Context context) {
        super(context);
        init();
    }

    Paint paint;
    Path path;
    float mDirection;

    CompassView compassView;
    private LinearGradient lg_white;

    public void setCompassView(CompassView compassView) {
        this.compassView = compassView;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float rol = 0;
        int vol = 0;
        while (rol < 360) {
            canvas.rotate(2, getResources().getDimension(R.dimen.rad_x), getResources().getDimension(R.dimen.rad_y));
            if (mDirection <= 180) {
                if (rol < mDirection - 2) {
                    paint.setColor(Color.RED);
                    paint.setShader(null);
                } else {
                    paint.setShader(lg_white);
                }
            } else {
                if (rol < mDirection - 2) {
                    paint.setShader(lg_white);
                } else {
                    paint.setShader(null);
                    paint.setColor(Color.RED);
                }
            }

            canvas.drawPath(path, paint);
            rol += 2;
        }
    }

    void init() {
        paint = new Paint();
        paint.setStrokeWidth(1);
        paint.setColor(android.graphics.Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        lg_white = new LinearGradient(0, 0,getResources().getDimension(R.dimen.lg_arg1),
        		getResources().getDimension(R.dimen.lg_arg2), Color.WHITE, Color.BLACK,
                android.graphics.Shader.TileMode.MIRROR);

        paint.setAntiAlias(true);
        path = new Path();
        path.moveTo(getResources().getDimension(R.dimen.start_x), getResources().getDimension(R.dimen.start_y));
        path.lineTo(getResources().getDimension(R.dimen.end_x), getResources().getDimension(R.dimen.end_y));
    }

    void init_config() {

    }

    public void updateDirection(float direction) {
        mDirection = direction;
        invalidate();
    }

}
