package ua.com.nazik.my_tshirt_android.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.NavigationView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ua.com.nazik.my_tshirt_android.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_CODE = 112;
    private static final int GALLERY_CODE = 117;
    @Bind(R.id.edit_container)
    FrameLayout editLayout;

    @Bind(R.id.tshirt_img)
    ImageView tshirtImg;

    @Bind(R.id.tshirt_container)
    RelativeLayout tshirtContainer;

    /*@Bind(R.id.nav_view)
    NavigationView navigationView;*/

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/arial.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

//        initUI();
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        //navigationView.setNavigationItemSelectedListener(this);
        //navigationView.getMenu().getItem(0).setChecked(true);


        /*if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, FeedFragment.newInstance()).commit();
            if (getSupportActionBar() != null)
                getSupportActionBar().setTitle("Home");
            //TODO only at first app run
            drawer.openDrawer(GravityCompat.START);
        }*/

        initEditorViews();
    }



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @OnClick(R.id.save_btn)
    void saveClick(){
        Bitmap bmp = getBitmapFromView(tshirtContainer);
        saveBitmap(bmp);
    }

    @OnClick(R.id.img_btn)
    void addImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY_CODE);
    }

    @OnClick(R.id.text_btn)
    void addText(){
        new MaterialDialog.Builder(this)
                .title(R.string.input)
                .content(R.string.input_content)
                .inputType(InputType.TYPE_CLASS_TEXT/* | InputType.TYPE_TEXT_VARIATION_PASSWORD*/)
                .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                    @NonNull
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                        TextView textView = new TextView(MainActivity.this);
                        textView.setText(input);
                        textView.setLayoutParams(
                                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                        FrameLayout.LayoutParams.WRAP_CONTENT));

                        initMoveListener(textView);
                        editLayout.addView(textView);
                        textView.setLayoutParams(
                                new FrameLayout.LayoutParams(editLayout.getWidth() / 2, editLayout.getHeight() / 2));
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_CODE) {
                Uri imgUri = data.getData();
                createImage(imgUri);
            }
        }
    }

    private void createImage(Uri imgUri) {
        final ImageView imageView = new ImageView(this);
        imageView.setMaxWidth(100);
        imageView.setMaxHeight(100);
        Picasso.with(this).load(imgUri).resize(100, 100).centerCrop().into(imageView,
                new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                initMoveListener(imageView);
                editLayout.addView(imageView);
                //
                //
                imageView.setLayoutParams(
                        new FrameLayout.LayoutParams(editLayout.getWidth()/2, editLayout.getHeight()/2));
            }

            @Override
            public void onError() {

            }
        });



    }

    public static Bitmap getBitmapFromView(View view) {
        //Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
    }

    private void saveBitmap(Bitmap bmp) {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        dir.mkdirs();
        File file = new File(dir, "sketchpad" + 1 + ".png");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                    Toast.makeText(this, "Success", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initEditorViews() {
        for(int i=0; i< editLayout.getChildCount(); ++i) {
            final View view1 = editLayout.getChildAt(i);
            initMoveListener(view1);
        }
    }

    private void initMoveListener(final View view1) {
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
