package ua.com.nazik.my_tshirt_android.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ua.com.nazik.my_tshirt_android.R;
import ua.com.nazik.my_tshirt_android.views.MyTextView;

public class MainActivity extends AppCompatActivity implements View.OnFocusChangeListener, View.OnTouchListener {

    private FrameLayout mainContainer;

    TextView _view;
    ViewGroup _root;
    private int _xDelta;
    private int _yDelta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mainContainer = (FrameLayout)findViewById(R.id.main_container);
        _root = (ViewGroup)findViewById(R.id.root);

        _view = new TextView(this);
        _view.setText("TextView!!!!!!!!");

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(150, 50);
        layoutParams.leftMargin = 50;
        layoutParams.topMargin = 50;
        layoutParams.bottomMargin = -250;
        layoutParams.rightMargin = -250;
        _view.setLayoutParams(layoutParams);

        _view.setOnTouchListener(this);
        _root.addView(_view);
    }



    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                view.setLayoutParams(layoutParams);
                break;
        }
        _root.invalidate();
        return false;
    }
}
