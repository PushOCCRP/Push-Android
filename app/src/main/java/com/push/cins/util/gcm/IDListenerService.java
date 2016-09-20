package com.push.cins.util.gcm;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.push.cins.util.NotificationManager;

/**
 * Created by christopher on 7/13/16.
 */

public class IDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        NotificationManager.getNotificationManager().registerForNotifications(getApplicationContext());
    }
}