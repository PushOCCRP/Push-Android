package com.push.app;

import android.app.Application;

import com.infobip.push.Configuration;
import com.infobip.push.PushClient;
import com.push.app.util.Utils;

/**
 * Created by Zed on 6/30/2015.
 */
public class PushApplication extends Application {
    @Override
    public void onCreate() {
        Utils.log("Creating application");
        super.onCreate();
        //Initialize push
        Configuration pushLibraryConfiguration =
                Configuration.customConfiguration(this)
                        .applicationId("1779txdc1dyh")
                        .applicationSecret("vrhmh2mw54mitzz")
                        .projectNumber("473631741147")
                        .production(false).build();

        PushClient.configure(this, pushLibraryConfiguration);
        PushClient.start();
    }
}
