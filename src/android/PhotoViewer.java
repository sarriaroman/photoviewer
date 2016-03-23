package com.sarriaroman.PhotoViewer;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;

/**
 * Class to Open PhotoViewer with the Required Parameters from Cordova
 *
 * - URL
 * - Title
 */
public class PhotoViewer extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("show")) {

			Intent i = new Intent(this.cordova.getActivity(), com.sarriaroman.PhotoViewer.PhotoActivity.class);

			i.putExtra("url", args.getString(0));
            i.putExtra("title", args.getString(1));
            i.putExtra("options", args.optJSONObject(2).toString());

			this.cordova.getActivity().startActivity(i);

			callbackContext.success("");

            return true;
        }
        return false;
    }
}
