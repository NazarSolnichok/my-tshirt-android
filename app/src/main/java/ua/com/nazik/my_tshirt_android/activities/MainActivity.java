package ua.com.nazik.my_tshirt_android.activities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.software.shell.viewmover.configuration.MovingParams;
import com.software.shell.viewmover.movers.ViewMover;
import com.software.shell.viewmover.movers.ViewMoverFactory;

import ua.com.nazik.my_tshirt_android.R;

public class MainActivity extends AppCompatActivity {

    ViewMover mover;
    MovingParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FrameLayout relativeLayout = (FrameLayout) findViewById(R.id.root);

        for(int i=0; i< relativeLayout.getChildCount(); ++i) {
            final View view1 = relativeLayout.getChildAt(i);
            view1.setOnTouchListener(new View.OnTouchListener() {
                float x = 0;
                float y = 0;
                Boolean b = false;

                @Override
                public boolean onTouch(View view, MotionEvent event) {

                    if (view != view1)
                        return false;
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_MOVE:
                            if (b) {
                                view.setX(view.getX() + event.getRawX() - x);
                                view.setY(view.getY() + event.getRawY() - y);
                                x = event.getRawX();
                                y = event.getRawY();
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                            b = false;
                            break;

                        case MotionEvent.ACTION_DOWN:
                            x = event.getRawX();
                            y = event.getRawY();
                            b = true;
                            view.clearFocus();
                            break;
                    }

                    return true;
                }
            });
        }





    }


}
