package ua.com.nazik.my_tshirt_android.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Boday-Alfaro on 20.03.2016.
 */
public class MyTextView extends TextView {

    private float mPosX;
    private float mPosY;
    private float mScaleFactor = 1.f;

    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void onDraw(Canvas canvas)
    {
        canvas.translate(mPosX, mPosY);
        super.onDraw(canvas);
    }

    public void changePos (float dx, float dy)
    {
        mPosX += dx;
        mPosY += dy;
        this.invalidate();
    } // end changePos

    public void setPos (float x, float y)
    {
        mPosX = x;
        mPosY = y;
        this.invalidate();
    } // end setPos

}
