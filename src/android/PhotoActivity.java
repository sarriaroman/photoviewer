package com.speryans.PhotoViewer;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.cronos.fullimage.helpers.ImageLoader;
import com.cronos.fullimage.helpers.ImageLoader.ImageListener;
import com.speryans.palmira.R;

public class PhotoActivity extends Activity implements ImageListener {

	private PhotoViewAttacher mAttacher;

	private ImageView photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_photo);
		
		String image = this.getIntent().getStringExtra("url");

		photo = (ImageView) findViewById(R.id.photoView);
		
		ImageLoader image_loader = new ImageLoader(this, android.R.color.transparent);
		image_loader.displayImage(image, photo, 100, this);
		
		mAttacher = new PhotoViewAttacher(photo);
		
		// Just hide the screen controls
		((LinearLayout) this.findViewById(R.id.fullscreen_content_controls)).setVisibility(View.GONE);
	}

	@Override
	public void imageLoaded(String url) {
		photo.setVisibility(View.VISIBLE);
		mAttacher.update();
	}
}
