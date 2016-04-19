package com.push.risemd;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.infobip.push.Notification;
import com.push.risemd.util.Utils;

import org.json.JSONObject;

/**
 * Created by Zed on 7/15/2015.
 */
public class NotificationHandler extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.push.risemd.R.layout.notification_handler);
        String extra = getIntent().getExtras().getString(Notification.DefaultNotificationHandler.INTENT_EXTRAS_KEY);
        Utils.log("Extras -> " + extra);
        try{
            JSONObject extrasObject = new JSONObject(extra);
            JSONObject extras = extrasObject.getJSONObject("payload").getJSONObject("extras");
            Utils.log("URL -> " + extras.getString("url"));
            Intent i = new Intent(this, HomeActivity.class);
            startActivity(i);
        }catch (Exception ex){
            Utils.log("Error loading post -> " + ex.getMessage());
        }
    }
}
