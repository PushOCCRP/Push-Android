package com.push.risemd;

import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.infobip.push.Configuration;
import com.infobip.push.Notification;
import com.infobip.push.PushClient;
import com.push.risemd.model.ImageQueueSingleton;
import com.push.risemd.util.AnalyticsManager;
import com.push.risemd.util.Language;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Zed on 6/30/2015.
 */
public class PushApplication extends Application {


    Configuration pushConfig;
    @Override
    public void onCreate() {
        super.onCreate();

        ImageQueueSingleton.getInstance(this);

        Fabric.with(this, new Crashlytics());
        AnalyticsManager.getAnalyticsManager().setAnalyticsManager(AnalyticsManager.AnalyticType.FABRIC, this);

        //Initialize push
        pushConfig =
                Configuration.customConfiguration(this)
                        .applicationId(getResources().getString(R.string.infobip_application_id))
                        .applicationSecret(getResources().getString(R.string.infobip_application_secret))
                        .projectNumber(getResources().getString(R.string.app_number))
                        .production(false).build();
        /*pushConfig =
                Configuration.customConfiguration(this)
                        .applicationId("pykzgrffppzx")
                        .applicationSecret("gfzywlo6rdt8g4d")
                        .projectNumber("772215886083")
                        .production(false).build();
*/
        PushClient.configure(this, pushConfig);

        Notification.DefaultNotificationHandler.smallIconResource(com.push.risemd.R.mipmap.ic_launcher);

        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra("is_notification", true);
        Notification.DefaultNotificationHandler.onNotificationClickIntent(i, Notification.DefaultNotificationHandler.NotificationType.plain);
        Notification.DefaultNotificationHandler.onNotificationClickIntent(i, Notification.DefaultNotificationHandler.NotificationType.media);

        Language.setDeviceToSavedLanguage(getApplicationContext());
        PushClient.start();
    }
}
