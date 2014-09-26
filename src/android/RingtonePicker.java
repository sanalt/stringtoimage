/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
*/
package org.apache.cordova.ringtonepicker;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import android.content.Context;
import android.media.RingtoneManager;
import org.apache.cordova.PluginResult;
import android.util.Log;
import android.content.Intent;
import android.provider.Settings;
import android.net.Uri;
import android.app.Activity;
import android.media.Ringtone;
import org.json.JSONObject;
/**
 * This class provides access to vibration on the device.
 */
public class RingtonePicker extends CordovaPlugin {
	public String notification_uri;
	private CallbackContext callbackContext = null;
    /**
     * Constructor.
     */
    public RingtonePicker() {
    }

    @Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.callbackContext = callbackContext;
    	if (action.equals("getRingtone")) { 
        	Log.d("customPlugin", " getRingtone ");
        	
        	Runnable getRingtone = new Runnable() {

                    @Override
                    public void run() {
                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER); 
   		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone for notifications:");
    	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
    	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
    	notification_uri = Settings.System.getString(cordova.getActivity().getContentResolver(), Settings.System.NOTIFICATION_SOUND);
    	if (notification_uri == null)
    	{
        	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri)null);
    	}
    	else
    	{
        	intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(notification_uri));
    	}
    	cordova.setActivityResultCallback(RingtonePicker.this);
    	cordova.getActivity().startActivityForResult(intent, 5);
                    }
                };
        	this.cordova.getActivity().runOnUiThread(getRingtone);
        	return true;
    	}
   		else {
        	return false;
   	 	}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
     	Log.d("customPlugin", "Calling onActivityResult");
    	if (resultCode == Activity.RESULT_OK && requestCode == 5)
    	{
        	Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
        	Ringtone ringtone = RingtoneManager.getRingtone(this.cordova.getActivity(), uri);
        	String title = ringtone.getTitle(this.cordova.getActivity());
       	 	Log.d("customPlugin", "I picked this ringtone " + uri);
       	 	Log.d("customPlugin", "I picked this ringtone title" + title);
       	 	
        	if (uri != null)
        	{
        		Log.d("customPlugin", "Setting ringtone to  " + notification_uri);
        		String returnText = "{\"ringtone\": {\"title\": \""+title+"\",\"uri\": \""+uri+"\"}}";

        		PluginResult pluginResult = new PluginResult(PluginResult.Status.OK, returnText);
        		pluginResult.setKeepCallback(true);
        		this.callbackContext.sendPluginResult(pluginResult);
        	}
    	}
	}

}
