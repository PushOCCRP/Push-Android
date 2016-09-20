package com.push.cins.util.gcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.android.gms.iid.InstanceID;
import com.push.cins.DetailPostActivity;
import com.push.cins.HomeActivity;
import com.push.cins.NotificationHandler;
import com.push.cins.R;

import com.push.cins.fragment.HomeFragment;
import com.push.cins.model.Article;
import com.push.cins.util.AnalyticsManager;
import com.push.cins.util.Foreground;
import com.push.cins.util.SyncManager;
import com.push.cins.interfaces.SyncManager.ArticleDelegate;

import java.net.URI;
import java.util.List;

/**
 * Created by christopher on 7/13/16.
 */

public class ListenerService extends GcmListenerService {
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("msg");
        Uri sound = null;
        if(data.get("sound").equals("default")) {
            sound = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION);
        }

        final Intent resultIntent = new Intent(this, NotificationHandler.class);
        resultIntent.putExtra("articleId", data.getString("article_id"));
        resultIntent.putExtra("message", message);

        if (Foreground.get().isForeground()) {
            resultIntent.putExtra("inForeground", true);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(resultIntent);
        } else {
            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(message)
                    .setSound(sound);

            // Creates an explicit intent for an Activity in your app

            // The stack builder object will contain an artificial back stack for the
            // started Activity.
            // This ensures that navigating backward from the Activity leads out of
            // your application to the Home screen.
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack for the Intent (but not the Intent itself)
            stackBuilder.addParentStack(DetailPostActivity.class);
            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =  stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            notificationManager.notify(1, mBuilder.build());
        }



    }

}