package com.speryans.PhotoViewer.PhotoActivity;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.speryans.PhotoViewer.helpers.ImageLoader.ImageLoader;
import com.speryans.PhotoViewer.helpers.ImageLoader.ImageLoader.ImageListener;

public class PhotoActivity extends Activity implements ImageListener {

	private PhotoViewAttacher mAttacher;

	private ImageView photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView( getApplication().getResources().getIdentifier("activity_photo", "layout", getApplication().getPackageName()) );
		
		String image = this.getIntent().getStringExtra("url");

		photo = (ImageView) findViewById( getApplication().getResources().getIdentifier("photoView", "id", getApplication().getPackageName()) );
		
		ImageLoader image_loader = new ImageLoader(this, android.R.color.transparent);
		image_loader.displayImage(image, photo, 100, this);
		
		mAttacher = new PhotoViewAttacher(photo);
		
		// Just hide the screen controls
		((LinearLayout) this.findViewById( getApplication().getResources().getIdentifier("fullscreen_content_controls", "id", getApplication().getPackageName()) )).setVisibility(View.GONE);
	}

	@Override
	public void imageLoaded(String url) {
		photo.setVisibility(View.VISIBLE);
		mAttacher.update();
	}
}
