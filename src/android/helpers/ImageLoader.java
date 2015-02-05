package com.speryans.PhotoViewer.helpers.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import com.speryans.PhotoViewer.helpers.Utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
    
    private HashMap<String, Bitmap> cache = new HashMap<String, Bitmap>();
    
    private File cacheDir;
    private int stub_id = 0;
    
    public ImageLoader(Context context, int placeholder_image ) {
    	this.stub_id = placeholder_image;
    	
        photoLoaderThread.setPriority(Thread.NORM_PRIORITY-1);
        
        //Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(), context.getPackageName());
        else
            cacheDir=context.getCacheDir();
        
        if(!cacheDir.exists())
            cacheDir.mkdirs();
    }
    
    public void displayImage(String url, ImageView imageView, int quality) {
    	displayImage(url, imageView, quality, null);
    }
    
    public void displayImage(String url, ImageView imageView, int quality, ImageListener il) {
    	try {
    		if(cache.containsKey(url)) {
    			imageView.setImageBitmap( cache.get(url) );
    		} else {
    			queuePhoto(url, imageView, quality, il);
    			imageView.setImageResource(stub_id);
    		}    
    	} catch( Exception e ) {
    		Log.e("Gallery", e.toString() , e);
    	}
    }
        
    private void queuePhoto(String url, ImageView imageView, int quality, ImageListener il)
    {
        //This ImageView may be used for other images before. So there may be some old tasks in the queue. We need to discard them. 
        photosQueue.Clean(imageView);
        PhotoToLoad p=new PhotoToLoad(url, imageView, quality, il);
        synchronized(photosQueue.photosToLoad){
            photosQueue.photosToLoad.push(p);
            photosQueue.photosToLoad.notifyAll();
        }
        
        //start thread if it's not started yet
        if(photoLoaderThread.getState() == Thread.State.NEW)
            photoLoaderThread.start();
    }
    
    private Bitmap getBitmap(String url, int quality) 
    {
        //I identify images by hashcode. Not a perfect solution, good for the demo.
        String filename=String.valueOf(url.hashCode());
        File f=new File(cacheDir, filename);
        
        //from SD cache
        Bitmap b = decodeFile(f, quality);
        if(b!=null)
            return b;
        
        //from web
        try {
            Bitmap bitmap=null;
            URL curl = new URL(url);
            InputStream is= null; //.openStream();
            HttpURLConnection.setFollowRedirects(true);
            HttpURLConnection urlConnection = (HttpURLConnection) curl.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            String locHeader = urlConnection.getHeaderField("Location");
            if (locHeader != null) {
            	Log.i("Gallery", locHeader);
            	
            	is = new URL(locHeader).openStream();
                OutputStream os = new FileOutputStream(f);
                Utils.CopyStream(is, os);
                os.close();
                bitmap = decodeFile(f, quality);
                return bitmap;
            } else {
            	is = urlConnection.getInputStream();
            	OutputStream os = new FileOutputStream(f);
            	Utils.CopyStream(is, os);
            	os.close();
            	bitmap = decodeFile(f, quality);
            	return bitmap;
            }
        } catch (Exception ex){
           ex.printStackTrace();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f, int quality){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);
            
            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE=quality;
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale++;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public int quality;

        public ImageListener listener = null;
        
        public PhotoToLoad(String u, ImageView i, int q, ImageListener il){
            url=u; 
            imageView=i;
            quality = q;
            listener = il;
        }
    }
    
    PhotosQueue photosQueue=new PhotosQueue();
    
    public void stopThread()
    {
        photoLoaderThread.interrupt();
    }
    
    //stores list of photos to download
    class PhotosQueue
    {
        private Stack<PhotoToLoad> photosToLoad=new Stack<PhotoToLoad>();
        
        //removes all instances of this ImageView
        public void Clean(ImageView image)
        {
            for(int j=0 ;j<photosToLoad.size();){
                if(photosToLoad.get(j).imageView==image)
                    photosToLoad.remove(j);
                else
                    ++j;
            }
        }
    }
    
    class PhotosLoader extends Thread {
        public void run() {
            try {
                while(true)
                {
                    //thread waits until there are any images to load in the queue
                    if(photosQueue.photosToLoad.size()==0)
                        synchronized(photosQueue.photosToLoad){
                            photosQueue.photosToLoad.wait();
                        }
                    if(photosQueue.photosToLoad.size()!=0)
                    {
                        PhotoToLoad photoToLoad;
                        synchronized(photosQueue.photosToLoad){
                            photoToLoad=photosQueue.photosToLoad.pop();
                        }
                        Bitmap bmp=getBitmap(photoToLoad.url, photoToLoad.quality);
                        cache.put(photoToLoad.url, bmp);
                        
                            BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad.imageView, photoToLoad.url, photoToLoad.listener);
                            Activity a=(Activity)photoToLoad.imageView.getContext();
                            a.runOnUiThread(bd);
                    }
                    if(Thread.interrupted())
                        break;
                }
            } catch (InterruptedException e) {
                //allow thread to exit
            }
        }
    }
    
    PhotosLoader photoLoaderThread = new PhotosLoader();
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        ImageView imageView;
        String url;
        ImageListener listener;
        public BitmapDisplayer(Bitmap b, ImageView i, String u, ImageListener il){bitmap=b;imageView=i;url=u;listener=il;}
        public void run()
        {
            if(bitmap!=null) {
            	imageView.setImageBitmap( bitmap );
            	
            	if( listener != null ) {
            		listener.imageLoaded(url);
            	}
            } else
                imageView.setImageResource(stub_id);
        }
    }

    public void clearCache() {
        //clear memory cache
        cache.clear();
        
        //clear SD cache
        File[] files=cacheDir.listFiles();
        for(File f:files)
            f.delete();
    }

    public interface ImageListener {
    	public void imageLoaded( String url );
    }
}
