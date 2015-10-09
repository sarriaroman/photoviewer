package com.speryans.PhotoViewer;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;

/**
 * This class echoes a string called from JavaScript.
 */
public class PhotoViewer extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("show")) {

			Intent i = new Intent(this.cordova.getActivity(), com.speryans.PhotoViewer.PhotoActivity.PhotoActivity.class);

			i.putExtra("url", args.getString(0));
            i.putExtra("title", args.getString(1));
            
			this.cordova.getActivity().startActivity(i);

			callbackContext.success("");

            return true;
        }
        return false;
    }
}
