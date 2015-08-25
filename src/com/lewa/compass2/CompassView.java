package com.lewa.compass2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


public class CompassView extends ImageView {
    float mDirection;
    private Drawable compass;
    private String north = getResources().getString(R.string.north_tv);
    private String east = getResources().getString(R.string.east_tv);
    private String south = getResources().getString(R.string.south_tv);
    private String west = getResources().getString(R.string.west_tv);
   
    public CompassView(Context context) {
        super(context);
        mDirection = 0.0f;
        compass = null;
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDirection = 0.0f;
        compass = null;
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDirection = 0.0f;
        compass = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
         Point CurrentPoint = new Point();
        if (compass == null) {
            compass = getDrawable();
            compass.setBounds(0, 0, getWidth(), getHeight());
        }
        canvas.save();
        canvas.rotate(mDirection, getWidth() / 2, getHeight() / 2);
        compass.draw(canvas);
        canvas.restore();
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0x6FFFFFFF);
        paint.setStrokeWidth(1);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.DEFAULT);
        
        paint.setTextSize(getResources().getDimension(R.dimen.Direction_size));
        int Height = this.getHeight();
        int Top = this.getTop();
        CurrentPoint.x = (int) (Height/2 -getResources().getDimension(R.dimen.x_offset));
        CurrentPoint.y = (int) (Top+Height/2 +getResources().getDimension(R.dimen.y_offset));
        canvas.drawText(east, (int) (CurrentPoint.x+(Height/2 - getResources().getDimension(R.dimen.Direction_Radius))*Math.cos(Math.toRadians(0+mDirection))), (int) (CurrentPoint.y+(Height/2 - getResources().getDimension(R.dimen.Direction_Radius))*Math.sin(Math.toRadians(0+mDirection))), paint);
        canvas.drawText(south, (int) (CurrentPoint.x+(Height/2 - getResources().getDimension(R.dimen.Direction_Radius))*Math.cos(Math.toRadians(90+mDirection))), (int) (CurrentPoint.y+(Height/2 - getResources().getDimension(R.dimen.Direction_Radius))*Math.sin(Math.toRadians(90+mDirection))), paint);
        canvas.drawText(west, (int) (CurrentPoint.x+(Height/2 - getResources().getDimension(R.dimen.Direction_Radius))*Math.cos(Math.toRadians(180+mDirection))), (int) (CurrentPoint.y+(Height/2 - getResources().getDimension(R.dimen.Direction_Radius))*Math.sin(Math.toRadians(180+mDirection))), paint);
        canvas.drawText(north, (int) (CurrentPoint.x+(Height/2 - getResources().getDimension(R.dimen.Direction_Radius))*Math.cos(Math.toRadians(270+mDirection))), (int) (CurrentPoint.y+(Height/2 - getResources().getDimension(R.dimen.Direction_Radius))*Math.sin(Math.toRadians(270+mDirection))), paint);
    }
    public void updateDirection(float direction) {
        mDirection = direction;
        invalidate();
    }

}
