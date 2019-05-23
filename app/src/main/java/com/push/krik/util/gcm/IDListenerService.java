package com.push.krik.util.gcm;

//import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.push.krik.util.NotificationManager;

/**
 * Created by christopher on 7/13/16.
 */

public class IDListenerService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        NotificationManager.getNotificationManager().registerForNotifications(getApplicationContext());
    }
}