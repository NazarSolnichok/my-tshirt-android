package ua.com.nazik.my_tshirt_android;

import android.view.MotionEvent;
import android.view.View;

public class ViewMovingHelper {

    View current;

    public View getCurrent() {
        return current;
    }

    public void initMoveListener(final View view1) {
        view1.setOnTouchListener(new View.OnTouchListener() {
            float x = 0;
            float y = 0;
            Boolean b = false;
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                current = view;
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
