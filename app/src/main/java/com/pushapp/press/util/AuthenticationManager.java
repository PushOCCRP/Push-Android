package com.pushapp.press.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.pushapp.press.R;
import com.pushapp.press.interfaces.AuthenticationManager.AuthenticationDelegate;
import com.pushapp.press.interfaces.RestApi;
import com.pushapp.press.model.LoginRequest;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AuthenticationManager {
    public static AuthenticationManager authenticationManager;
    private Context applicationContext;
    private RestApi restAPI;

    private Gson gson;

    private static String preferencesName = "AuthenticationPreference";
    private static String apiKeyPreferenceName = "AutheticationApiKeyPreference";
    private static String usernamePreferenceName = "AutheticationUsernamePreference";

    private AuthenticationManager() {
        // Init
    }

    public static AuthenticationManager getAuthenticationManager() {
        if (authenticationManager == null) {
            authenticationManager = new AuthenticationManager();
        }
        return authenticationManager;
    }

    public void setApplicationContext(Context context) {
        applicationContext = context;
        authenticationManager.setUpRestApi();
    }

    public Boolean isLoggedIn(Context context){
        if(apiKey(context) == null || username(context) == null){
            return false;
        }

        return true;
    }

    public String apiKey(Context context){
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        String apiKey = preferences.getString(apiKeyPreferenceName, null);
        return apiKey;
    }

    public String username(Context context){
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        String username = preferences.getString(usernamePreferenceName, null);
        return username;
    }

    public void login(String username, String password, Context context, final AuthenticationDelegate delegate){
        if(Online()){

            //Get the language currently set, defaults to Azerbaijari ("az")
            Locale locale = Language.getLanguage(context);

            String language = context.getString(R.string.default_language);
            if(locale != null) {
                language = locale.getLanguage();
            }


            HashMap requestParametners = new HashMap();
            requestParametners.put("installation_uuid", AnalyticsManager.installationUUID(context).toString());
            requestParametners.put("language", language);
            requestParametners.put("username", username);
            requestParametners.put("password", password);

            restAPI.login(requestParametners, new Callback<LoginRequest>() {
                @Override
                public void success(LoginRequest loginRequest, Response response){
                    Integer code = loginRequest.getCode();

                    if(code == 0) {
                        delegate.didReceiveErrorOnLogin();
                        return;
                    }

//                    String bodyText = response.getBody().toString();

//                    JsonElement body = gson().fromJson(bodyText, String);
//                    gson()
                    String apiKey = loginRequest.getApiKey();
                    String username = loginRequest.getUsername();

                    SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(apiKeyPreferenceName, apiKey);
                    editor.putString(usernamePreferenceName, username);
                    editor.commit();

                    delegate.didLoginSuccessfully();
                }

                @Override
                public void failure(RetrofitError error) {
                    delegate.didReceiveErrorOnLogin();
                }
            });
        }
    }

    public void logout(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(apiKeyPreferenceName, null);
        editor.putString(usernamePreferenceName, null);
        editor.commit();
    }

    private void setUpRestApi() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestInterceptor.RequestFacade request) {
                request.addHeader("Accept", "application/json; charset=utf-8");
            }
        };

        //create an adapter for retrofit with base url
        RestAdapter restAdapter = new RestAdapter.Builder().setLogLevel(RestAdapter.LogLevel.FULL)
                .setRequestInterceptor(requestInterceptor)
                .setEndpoint(applicationContext.getResources().getString(R.string.server_url)).build();
        //creating a service for adapter with our GET class
        restAPI = restAdapter.create(RestApi.class);
    }

    private boolean Online() {
        ConnectivityManager manager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        // check that there is an active network
        if (info == null) {
            return false;
        }

        return info.isConnected();
    }

    private Gson gson(){
        if(this.gson == null){
            this.gson = new Gson();
        }

        return this.gson;
    }

}
