package com.pushapp.press.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

//import com.google.android.gms.
//import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.firebase.iid.FirebaseInstanceId;
//import com.google.android.gms.iid.InstanceID;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;
import com.pushapp.press.R;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;


import java.io.IOException;
import java.util.HashMap;

import java.util.UUID;


/**
 * Created by christopher on 7/13/16.
 */
public class NotificationManager extends FirebaseInstanceIdService implements  LanguageListener {

    public static NotificationManager notificationManager;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private Context app_context;

    private NotificationManager() {
        // Init
    }

    public static NotificationManager getNotificationManager() {
        if (notificationManager == null) {
            notificationManager = new NotificationManager();
        }
        return notificationManager;
    }

    public void languageChanged() {
        // Unregister with current language
        unregisterWithUniqush();
        // Reregister with new language
        registerForNotifications(app_context);
    }

    public void registerForNotifications(Context context) {
        app_context = context;
        //String token = FirebaseInstanceId.getInstance().getToken();


        new AsyncTask() {
            @Override
            protected String doInBackground(Object... params) {
                String token = FirebaseInstanceId.getInstance().getToken();
                try {
                    Log.i(null, token);

                    registerWithUniqush(token);
                } catch (Exception e){
                    AnalyticsManager.logError("Error refreshing token.");
                }
                return null;
            }

        }.execute(null, null, null);


        Language.addListener(this);
    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        // String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        unregisterWithUniqush();
        registerWithUniqush(refreshedToken);
    }

    private void registerWithUniqush(final String token) {
        String url = app_context.getString(R.string.server_url) + "/notifications/subscribe.json";

        final HashMap options = new HashMap();
        options.put("platform", "android");
        options.put("reg_id", token);
        options.put("language", Language.getLanguage(app_context).getLanguage());
        options.put("dev_id", identifierForInstall(app_context));

        // Extrapolate this out to a generator variable
        Log.w("", "*****************");

        if(app_context.getString(R.string.debug).equals("true")) {
            options.put("sandbox", "true");
            Log.i("", "Sandbox is true");
        } else {
            Log.i("", "Sandbox is false");
        }
        Log.w("", "*****************");

        Gson gson = new Gson();
        String body_json = gson.toJson(options);

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, body_json);
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("", e.toString());
            }
            @Override
            public void onResponse(Response response) throws IOException {
                Log.w("", response.body().string());
                Log.i("", response.toString());

                SharedPreferences preferences = app_context.getSharedPreferences("NotificationPreferences", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", token);
                editor.putString("language", (String)options.get("language"));
                if(options.containsKey("sandbox")) {
                    editor.putString("sandbox", (String) options.get("sandbox"));
                }

                editor.commit();
            }
        });
    }

    private void unregisterWithUniqush() {
        String url = app_context.getString(R.string.server_url) + "/notifications/unsubscribe.json";

        SharedPreferences preferences = app_context.getSharedPreferences("NotificationPreferences", Context.MODE_PRIVATE);
        String token = preferences.getString("token", null);
        String language = preferences.getString("language", null);
        String dev_id = identifierForInstall(app_context);
        String sandbox = preferences.getString("sandbox", null);

        if(token == null){
            return;
        }

        final HashMap options = new HashMap();
        options.put("platform", "android");
        options.put("reg_id", token);
        options.put("language", language);
        options.put("dev_id", dev_id);

        // Extrapolate this out to a generator variable
        if(sandbox != null) {
            options.put("sandbox", sandbox);
        }

        Gson gson = new Gson();
        String body_json = gson.toJson(options);

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, body_json);
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("", e.toString());
            }
            @Override
            public void onResponse(Response response) throws IOException {
                Log.w("", response.body().string());
                Log.i("", response.toString());

                clearSavedSettings();
            }
        });
    }

    private String identifierForInstall(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("NotificationPreferences", Context.MODE_PRIVATE);
        String uuid = preferences.getString("uuid", null);

        if(uuid == null){
            uuid = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("uuid", uuid);
            editor.commit();
        }

        return uuid;
    }

    private void clearSavedSettings(){
        SharedPreferences preferences = app_context.getSharedPreferences("NotificationPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove("token");
        editor.remove("language");
        editor.remove("sandbox");
        editor.commit();
    }


}
