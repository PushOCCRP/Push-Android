package com.push.meydan;

import android.app.Application;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;
import com.infobip.push.Configuration;
import com.infobip.push.Notification;
import com.infobip.push.PushClient;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Zed on 6/30/2015.
 */
public class PushApplication extends Application {


    Configuration pushConfig;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        //Initialize push
        pushConfig =
                Configuration.customConfiguration(this)
                        .applicationId("vid29hw7lict")
                        .applicationSecret("1bhs7bisy7ptc2a")
                        .projectNumber("754262668085")
                        .production(false).build();
        /*pushConfig =
                Configuration.customConfiguration(this)
                        .applicationId("pykzgrffppzx")
                        .applicationSecret("gfzywlo6rdt8g4d")
                        .projectNumber("772215886083")
                        .production(false).build();
*/
        PushClient.configure(this, pushConfig);

        Notification.DefaultNotificationHandler.smallIconResource(com.push.meydan.R.mipmap.ic_launcher);

        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra("is_notification", true);
        Notification.DefaultNotificationHandler.onNotificationClickIntent(i, Notification.DefaultNotificationHandler.NotificationType.plain);
        Notification.DefaultNotificationHandler.onNotificationClickIntent(i, Notification.DefaultNotificationHandler.NotificationType.media);

        PushClient.start();
    }
}
