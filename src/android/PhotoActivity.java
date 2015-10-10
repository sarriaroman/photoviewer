package com.sarriaroman.PhotoViewer;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class PhotoActivity extends Activity {
	private PhotoViewAttacher mAttacher;

	private ImageView photo;
	private String imageUrl;

	private ImageButton closeBtn;
	private ImageButton shareBtn;

	private TextView titleTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(getApplication().getResources().getIdentifier("activity_photo", "layout", getApplication().getPackageName()));

		// Load the Views
		findViews();

		// Change the Activity Title
		String actTitle = this.getIntent().getStringExtra("title");
		if( !actTitle.equals("") ) {
			titleTxt.setText(actTitle);
		}

		imageUrl = this.getIntent().getStringExtra("url");

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
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);

				sharingIntent.setType("image/*");
				sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imageUrl));

				startActivity(Intent.createChooser(sharingIntent, "Share"));
			}
		});

		loadImage();
	}

	/**
	 * Find and Connect Views
	 *
	 */
	private void findViews() {
		// Buttons first
		closeBtn = (ImageButton) findViewById( getApplication().getResources().getIdentifier("closeBtn", "id", getApplication().getPackageName()) );
		shareBtn = (ImageButton) findViewById( getApplication().getResources().getIdentifier("shareBtn", "id", getApplication().getPackageName()) );

		// Photo Container
		photo = (ImageView) findViewById( getApplication().getResources().getIdentifier("photoView", "id", getApplication().getPackageName()) );
		mAttacher = new PhotoViewAttacher(photo);

		// Title TextView
		titleTxt = (TextView) findViewById( getApplication().getResources().getIdentifier("titleTxt", "id", getApplication().getPackageName()) );
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
		mAttacher.update();
	}

	/**
	 * Load the image using Picasso
	 *
	 */
	private void loadImage() {
		if( imageUrl.startsWith("http") ) {
		Picasso.with(this)
				.load(imageUrl)
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
		} else {
			photo.setImageURI(Uri.parse(imageUrl));

			hideLoadingAndUpdate();
		}
	}

}
