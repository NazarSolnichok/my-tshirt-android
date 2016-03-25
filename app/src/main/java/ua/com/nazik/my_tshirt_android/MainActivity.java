package ua.com.nazik.my_tshirt_android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.design.widget.NavigationView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;


import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.chrisjenx.calligraphy.CalligraphyTypefaceSpan;
import uk.co.chrisjenx.calligraphy.TypefaceUtils;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_CODE = 112;
    private static final int GALLERY_CODE = 117;
    @Bind(R.id.edit_container)
    FrameLayout editLayout;

    @Bind(R.id.tshirt_img)
    ImageView tshirtImg;

    @Bind(R.id.tshirt_container)
    RelativeLayout tshirtContainer;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    private ActionBarDrawerToggle toggle;
    private DrawerHolder drawerHolder;
    private ViewMovingHelper viewMovingHelper = new ViewMovingHelper();

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
        initUI();
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //noinspection deprecation
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initUI() {
        View header = navigationView.getHeaderView(0);
        drawerHolder = new DrawerHolder(header);
    }


    static Uri cameraUri;

    class DrawerHolder {
        public DrawerHolder(View header){
            ButterKnife.bind(this, header);
        }

        @OnClick(R.id.save_btn)
        void saveClick() {
            drawer.closeDrawer(GravityCompat.START);
            Bitmap bmp = MyBitmapHelper.getBitmapFromView(tshirtContainer);
            MyBitmapHelper.saveBitmap(MainActivity.this, bmp);
        }

        @OnClick(R.id.img_btn)
        void addImage() {
            drawer.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_CODE);
        }

        @OnClick(R.id.text_btn)
        void addText() {
            drawer.closeDrawer(GravityCompat.START);
            new MaterialDialog.Builder(MainActivity.this)
                    .title(R.string.input)
                    .content(R.string.input_content)
                    .inputType(InputType.TYPE_CLASS_TEXT/* | InputType.TYPE_TEXT_VARIATION_PASSWORD*/)
                    .input(R.string.input_hint, R.string.input_prefill, new MaterialDialog.InputCallback() {
                        @NonNull
                        @Override
                        public void onInput(MaterialDialog dialog, CharSequence input) {
                            // Do something
                            setText(input);
                        }
                    }).show();
        }

        @OnClick(R.id.camera_btn)
        void takePhoto(){
            drawer.closeDrawer(GravityCompat.START);
            Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "camera.jpg"));
            cameraUri = uri;
            Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(photoIntent, CAMERA_CODE);
        }

        private void setText(CharSequence input) {
            TextView textView = new TextView(MainActivity.this);
            textView.setText(input);
            setTextViewFont(textView, "snap");
            textView.setLayoutParams(
                    new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT));
            viewMovingHelper.initMoveListener(textView);
            editLayout.addView(textView);
            textView.setLayoutParams(
                    new FrameLayout.LayoutParams(editLayout.getWidth() / 2, editLayout.getHeight() / 2));
        }
    }

    void setTextViewFont(TextView textView, String font){
        CharSequence input = textView.getText();
        SpannableStringBuilder sBuilder = new SpannableStringBuilder();
        sBuilder.append(input);
        CalligraphyTypefaceSpan typefaceSpan = new CalligraphyTypefaceSpan(
                TypefaceUtils.load(getAssets(), String.format("fonts/%s.ttf", font)));
        sBuilder.setSpan(typefaceSpan, 0, input.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(sBuilder, TextView.BufferType.SPANNABLE);
    }

    Uri outputUri = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_CODE) {
                Uri imgUri = data.getData();
                //
                Crop.of(imgUri, outputUri).asSquare().start(this);
                //createImage(outputUri);
            }else if (requestCode == CAMERA_CODE){
                Uri imgUri = cameraUri;
                if (imgUri != null) {
                    createImage(imgUri);
                }
            }
        }
        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            doSomethingWithCroppedImage(outputUri);
        }
    }

    private void doSomethingWithCroppedImage(Uri data) {

    }

    private void createImage(Uri imgUri) {
        final ImageView imageView = new ImageView(this);
        imageView.setMaxWidth(100);
        imageView.setMaxHeight(100);
        Picasso.with(this).load(imgUri).resize(100, 100).centerCrop().into(imageView,
                new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        viewMovingHelper.initMoveListener(imageView);
                        editLayout.addView(imageView);
                        imageView.setLayoutParams(
                                new FrameLayout.LayoutParams(editLayout.getWidth() / 2, editLayout.getHeight() / 2));
                    }

                    @Override
                    public void onError() {

                    }
                });
    }

    @OnClick(R.id.footer_font)
    void changeFont(){
        final View currentView = viewMovingHelper.getCurrent();
        if (currentView instanceof TextView){
            new MaterialDialog.Builder(this)
                    .title(R.string.font_dialog)
                    .items(R.array.items)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                            setTextViewFont(((TextView) currentView), text.toString());
                        }
                    })
                    .show();
        }
    }

    @OnClick(R.id.footer_color)
    void changeColor(){
        final View currentView = viewMovingHelper.getCurrent();
        if (currentView instanceof TextView) {
            final int[] colors = getResources().getIntArray(R.array.default_rainbow);
            ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                    colors,
                    ContextCompat.getColor(this, R.color.flamingo),
                    5, // Number of columns
                    ColorPickerDialog.SIZE_SMALL);

            dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

                @Override
                public void onColorSelected(int color) {

                    ((TextView)currentView).setTextColor(color);
                }
            });
            dialog.show(getFragmentManager(), "color_dialog_test");
        }
    }

    @OnClick(R.id.footer_edit)
    void eitText(){
        final View currentView = viewMovingHelper.getCurrent();
        if (true) {
            final int[] colors = getResources().getIntArray(R.array.default_rainbow);
            ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                    colors,
                    ContextCompat.getColor(this, R.color.flamingo),
                    5, // Number of columns
                    ColorPickerDialog.SIZE_SMALL);

            dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {

                @Override
                public void onColorSelected(int color) {

                    tshirtImg.setColorFilter(color);
                }
            });
            dialog.show(getFragmentManager(), "color_dialog_test");
        }
    }
}
