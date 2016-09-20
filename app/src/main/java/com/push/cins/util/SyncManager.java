package com.push.cins.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;

import com.push.cins.R;
import com.push.cins.interfaces.RestApi;
import com.push.cins.interfaces.SyncManager.ArticleDelegate;
import com.push.cins.model.Article;
import com.push.cins.model.ArticlePost;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by christopher on 7/13/16.
 */

public class SyncManager {
    public static SyncManager syncManager;
    private Context applicationContext;
    private RestApi restAPI;

    private SyncManager() {
        // Init
    }

    public static SyncManager getSyncManager() {
        if (syncManager == null) {
            syncManager = new SyncManager();
        }
        return syncManager;
    }

    public void setApplicationContext(Context context) {
        applicationContext = context;
        syncManager.setUpRestApi();
    }

    public void articleForId(String article_id, Context context, final ArticleDelegate delegate){
        if(Online()){
            restAPI.getArticle(article_id, Language.getLanguage(context).getLanguage(), new Callback<ArticlePost>() {
                @Override
                public void success(ArticlePost articlePost, Response response) {
                    //Rearrange the posts so that the first article has an image
                    //We want this to be the same order everywhere so we do it here
                    //If there's none, then we hide the image box
                    if(articlePost.getResults().size() > 0){
                        delegate.didRetrieveArticle(articlePost.getResults().get(0));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    AnalyticsManager.logError(error.getLocalizedMessage());
                    Log.e("ERROR", "Failed to parse JSON ", error);
                }
            });
        }
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
        if (info != null) {
            return info.isConnected();
        }else {
            return false;
        }
    }

}
