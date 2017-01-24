package com.push.meydan;

import android.app.Application;
import android.content.Intent;

import com.crashlytics.android.Crashlytics;
import com.push.meydan.model.ImageQueueSingleton;
import com.push.meydan.util.AnalyticsManager;
import com.push.meydan.util.Language;
import com.push.meydan.util.NotificationManager;
import com.push.meydan.util.SyncManager;
import com.push.meydan.util.Foreground;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Zed on 6/30/2015.
 */
public class PushApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();

        Foreground.init(this);

        ImageQueueSingleton.getInstance(this);

        Fabric.with(this, new Crashlytics());
        AnalyticsManager.getAnalyticsManager().setAnalyticsManager(AnalyticsManager.AnalyticType.FABRIC, this);

        //Initialize push
        NotificationManager.getNotificationManager().registerForNotifications(this);

        // Set up the sync manager
        SyncManager.getSyncManager().setApplicationContext(getApplicationContext());


        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra("is_notification", true);
        //otification.DefaultNotificationHandler.onNotificationClickIntent(i, Notification.DefaultNotificationHandler.NotificationType.plain);
        //Notification.DefaultNotificationHandler.onNotificationClickIntent(i, Notification.DefaultNotificationHandler.NotificationType.media);

        Language.setDeviceToSavedLanguage(getApplicationContext());
        //PushClient.start();
    }
}
