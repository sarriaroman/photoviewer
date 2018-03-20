package com.sarriaroman.PhotoViewer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoMultipleActivity extends Activity {
    private PhotoViewAttacher mAttacher;

    private ImageView photo;
    private ViewPager view_pager;
    private ImageButton closeBtn;
    private ImageButton shareBtn;
    private ProgressBar progressBar1;
    private TextView titleTxt;

    private JSONObject options;
    private JSONArray jsonArray;
    private int current_position = 0;
    CustomPagerAdapter mCustomPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getApplication().getResources().getIdentifier("activity_multiple_photo", "layout", getApplication().getPackageName()));

        try {
            options = new JSONObject(this.getIntent().getStringExtra("options"));
            current_position = Integer.parseInt(this.getIntent().getStringExtra("title"));
            jsonArray = options.optJSONArray("img_array");
            Log.e("PhotoMulitple", "jsonArray----" + jsonArray);
        } catch (JSONException exception) {
        }
        // Load the Views
        findViews();

    }

    /**
     * Find and Connect Views
     */
    private void findViews(View itemView) {
        // Buttons first
        closeBtn = (ImageButton) itemView.findViewById(getApplication().getResources().getIdentifier("closeBtn", "id", getApplication().getPackageName()));
        progressBar1 = (ProgressBar) itemView.findViewById(getApplication().getResources().getIdentifier("progressBar1", "id", getApplication().getPackageName()));
        shareBtn = (ImageButton) itemView.findViewById(getApplication().getResources().getIdentifier("shareBtn", "id", getApplication().getPackageName()));
        shareBtn.setVisibility(View.INVISIBLE);
        // Photo Container
        photo = (ImageView) itemView.findViewById(getApplication().getResources().getIdentifier("photoView", "id", getApplication().getPackageName()));
        mAttacher = new PhotoViewAttacher(photo);

        // Title TextView
        titleTxt = (TextView) itemView.findViewById(getApplication().getResources().getIdentifier("titleTxt", "id", getApplication().getPackageName()));
    }

    private void findViews() {
        view_pager = (ViewPager) findViewById(getApplication().getResources().getIdentifier("view_pager", "id", getApplication().getPackageName()));
        if (jsonArray != null && jsonArray.length() > 0) {
            mCustomPagerAdapter = new CustomPagerAdapter(PhotoMultipleActivity.this);
            view_pager.setAdapter(mCustomPagerAdapter);
            view_pager.setCurrentItem(current_position);
        }
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
        progressBar1.setVisibility(View.GONE);
        shareBtn.setVisibility(View.INVISIBLE);

        mAttacher.update();
    }

    /**
     * Load the image using Picasso
     */
    private void loadImage(String imageUrl) {
        if (imageUrl.startsWith("http")) {
            Picasso.with(this)
                    .load(imageUrl)
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
        } else if (imageUrl.startsWith("data:image")) {
            String base64String = imageUrl.substring(imageUrl.indexOf(",") + 1);
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            photo.setImageBitmap(decodedByte);
            hideLoadingAndUpdate();
        } else {
            photo.setImageURI(Uri.parse(imageUrl));
            hideLoadingAndUpdate();
        }
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return jsonArray.length();
        }

        /*@Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }*/

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.activity_photo, container, false);
            findViews(itemView);
            Log.e("PhotoMulitple", "instantiateItem----");
            try {
                Log.e("PhotoMulitple", "position----" + position);
                if (jsonArray != null && jsonArray.length() > 0) {
                    final String imageUrl = jsonArray.optJSONObject(position).optString("url");
                    loadImage(imageUrl);
                    String actTitle = jsonArray.optJSONObject(position).optString("title");
                    if (!actTitle.equals("")) {
                        titleTxt.setText(actTitle);
                    }
                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });

                    container.addView(itemView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((FrameLayout) object);
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((FrameLayout) object);
        }
    }

}
