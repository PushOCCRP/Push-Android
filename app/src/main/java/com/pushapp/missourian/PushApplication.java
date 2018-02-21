package com.pushapp.missourian;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;

import com.crashlytics.android.Crashlytics;
import com.pushapp.missourian.model.ImageQueueSingleton;
import com.pushapp.missourian.util.AnalyticsManager;
import com.pushapp.missourian.util.Language;
import com.pushapp.missourian.util.NotificationManager;
import com.pushapp.missourian.util.SyncManager;
import com.pushapp.missourian.util.Foreground;

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

        Language.setDeviceToSavedLanguage(getApplicationContext());

        //Initialize push
        NotificationManager.getNotificationManager().registerForNotifications(this);

        // Set up the sync manager
        SyncManager.getSyncManager().setApplicationContext(getApplicationContext());


        Intent i = new Intent(this, HomeActivity.class);
        i.putExtra("is_notification", true);
        //otification.DefaultNotificationHandler.onNotificationClickIntent(i, Notification.DefaultNotificationHandler.NotificationType.plain);
        //Notification.DefaultNotificationHandler.onNotificationClickIntent(i, Notification.DefaultNotificationHandler.NotificationType.media);

        //PushClient.start();
    }

    @Override
    protected void attachBaseContext(Context base) {
        Language.setDeviceToSavedLanguage(base);
        super.attachBaseContext(base);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Language.setDeviceToSavedLanguage(getApplicationContext());
    }

}
