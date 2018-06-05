package com.sarriaroman.PhotoViewer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoActivity extends Activity {
    private PhotoViewAttacher mAttacher;

    private ImageView photo;

    private ImageButton closeBtn;
    private ImageButton shareBtn;
    private ProgressBar loadingBar;

    private TextView titleTxt;

    private String mImage;
    private String mTitle;
    private boolean mShare;
    private File mTempImage;
    private int shareBtnVisibility;

    public static JSONArray mArgs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getApplication().getResources().getIdentifier("activity_photo", "layout", getApplication().getPackageName()));

        // Load the Views
        findViews();

        try {
            this.mImage = mArgs.getString(0);
            this.mTitle = mArgs.getString(1);
            this.mShare = mArgs.getBoolean(2);
            //Set the share button visibility
            shareBtnVisibility = this.mShare ? View.VISIBLE : View.INVISIBLE;


        } catch (JSONException exception) {
            shareBtnVisibility = View.INVISIBLE;
        }
        shareBtn.setVisibility(shareBtnVisibility);
        //Change the activity title
        if (!mTitle.equals("")) {
            titleTxt.setText(mTitle);
        }

        loadImage();

        // Set Button Listeners
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 24) {
                    try {
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Uri imageUri;
                if (mTempImage == null) {
                    mTempImage = getLocalBitmapFileFromView(photo);
                }

                imageUri = Uri.fromFile(mTempImage);

                if (imageUri != null) {
                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);

                    sharingIntent.setType("image/*");
                    sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

                    startActivity(Intent.createChooser(sharingIntent, "Share"));
                }
            }
        });

    }

    /**
     * Find and Connect Views
     */
    private void findViews() {
        // Buttons first
        closeBtn = (ImageButton) findViewById(getApplication().getResources().getIdentifier("closeBtn", "id", getApplication().getPackageName()));
        shareBtn = (ImageButton) findViewById(getApplication().getResources().getIdentifier("shareBtn", "id", getApplication().getPackageName()));

        //ProgressBar
        loadingBar = (ProgressBar) findViewById(getApplication().getResources().getIdentifier("loadingBar", "id", getApplication().getPackageName()));
        // Photo Container
        photo = (ImageView) findViewById(getApplication().getResources().getIdentifier("photoView", "id", getApplication().getPackageName()));
        mAttacher = new PhotoViewAttacher(photo);

        // Title TextView
        titleTxt = (TextView) findViewById(getApplication().getResources().getIdentifier("titleTxt", "id", getApplication().getPackageName()));
    }

    /**
     * Get the current Activity
     *
     * @return
     */
    private Activity getActivity() {
        return this;
    }

    /**
     * Hide Loading when showing the photo. Update the PhotoView Attacher
     */
    private void hideLoadingAndUpdate() {
        photo.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.INVISIBLE);
        shareBtn.setVisibility(shareBtnVisibility);

        mAttacher.update();
    }

    /**
     * Load the image using Picasso
     */
    private void loadImage() {
        if (mImage.startsWith("http") || mImage.startsWith("file")) {
            Picasso.with(this)
                    .load(mImage)
                    .fit()
                    .centerInside()
                    .into(photo, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            hideLoadingAndUpdate();
                        }

                        @Override
                        public void onError() {
                            Toast.makeText(getActivity(), "Error loading image.", Toast.LENGTH_LONG).show();

                            finish();
                        }
                    });
        } else if (mImage.startsWith("data:image")) {

            new AsyncTask<Void, Void, File>() {

                protected File doInBackground(Void... params) {
                    String base64Image = mImage.substring(mImage.indexOf(",") + 1);
                    return getLocalBitmapFileFromString(base64Image);
                }

                protected void onPostExecute(File file) {
                    mTempImage = file;
                    Picasso.with(PhotoActivity.this)
                            .load(mTempImage)
                            .fit()
                            .centerCrop()
                            .into(photo, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    hideLoadingAndUpdate();
                                }

                                @Override
                                public void onError() {
                                    Toast.makeText(getActivity(), "Error loading image.", Toast.LENGTH_LONG).show();

                                    finish();
                                }
                            });
                }
            }.execute();

        } else {
            photo.setImageURI(Uri.parse(mImage));

            hideLoadingAndUpdate();
        }
    }

    public void onDestroy() {
        if (mTempImage != null) {
            mTempImage.delete();
        }
        super.onDestroy();
    }


    public File getLocalBitmapFileFromString(String base64) {
        File file;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream output = new FileOutputStream(file);
            byte[] decoded = Base64.decode(base64, Base64.DEFAULT);
            output.write(decoded);
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            file = null;
        }
        return file;
    }

    /**
     * Create Local Image due to Restrictions
     *
     * @param imageView
     * @return
     */
    public File getLocalBitmapFileFromView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp;

        if (drawable instanceof BitmapDrawable) {
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }

        // Store image to default external storage directory
        File file;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();

        } catch (IOException e) {
            file = null;
            e.printStackTrace();
        }
        return file;
    }

}
