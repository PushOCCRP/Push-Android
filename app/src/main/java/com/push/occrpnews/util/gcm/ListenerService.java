package com.push.occrpnews.util.gcm;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.push.occrpnews.DetailPostActivity;
import com.push.occrpnews.NotificationHandler;
import com.push.occrpnews.R;

import com.push.occrpnews.util.Foreground;
import com.push.occrpnews.util.Language;

import java.util.Map;

import static android.os.Build.VERSION_CODES.LOLLIPOP;


/**
 * Created by christopher on 7/13/16.
 */

public class ListenerService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Map data = remoteMessage.getData();

        // There's a small chance that a notification not in the correct language might get sent
        // This will make sure it doesn't get shown.
        String currentLanguage = Language.getLanguage(getApplicationContext()).getLanguage();
        if(data.get("language") == null || !data.get("language").equals(currentLanguage)){
            return;
        }

        String message = (String)data.get("msg");
        Uri sound = null;
        if(!data.containsKey("sound") || data.get("sound").equals("default")) {
            sound = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION);
        }

        final Intent resultIntent = new Intent(this, NotificationHandler.class);
        resultIntent.putExtra("articleId", (String)data.get("article_id"));
        resultIntent.putExtra("message", message);

        if (Foreground.get().isForeground()) {
            resultIntent.putExtra("inForeground", true);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(resultIntent);
        } else {
            BitmapDrawable drawable;
            if(Build.VERSION.SDK_INT < LOLLIPOP){
                drawable = (BitmapDrawable)getResources().getDrawable(R.mipmap.ic_launcher);
            }else {
                drawable= (BitmapDrawable)getResources().getDrawable(R.mipmap.ic_launcher, null);
            }

            Bitmap logo_bitmap = drawable.getBitmap();

            NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(logo_bitmap)
                    .setContentTitle(getApplication().getText(R.string.notification_header))
                    .setContentText(message)
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