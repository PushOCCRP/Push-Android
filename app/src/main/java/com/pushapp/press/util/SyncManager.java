package com.pushapp.press.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.pushapp.press.R;
import com.pushapp.press.interfaces.RestApi;
import com.pushapp.press.interfaces.SyncManager.ArticleDelegate;
import com.pushapp.press.interfaces.SyncManager.ArticlesDelegate;
import com.pushapp.press.model.Article;
import com.pushapp.press.model.ArticlePost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

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

    private Gson gson;

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

    public void articles(Context context, final ArticlesDelegate delegate){
        if(Online()){
            //Get the language currently set, defaults to Azerbaijari ("az")
            Locale locale = Language.getLanguage(context);

            String language = context.getString(R.string.default_language);
            if(locale != null) {
                language = locale.getLanguage();
            }

            restAPI.getArticles(AnalyticsManager.installationUUID(context).toString(), language, true, new Callback<ArticlePost>() {
                @Override
                public void success(ArticlePost articlePost, Response response) {
                    //There's a bunch of type juggling here because of the nested nature of it all
                    Gson gson = gson();

                    HashMap<String, ArrayList<Article>> articles = new HashMap<String, ArrayList<Article>>();

                    // If categories are not enabled
                    if(articlePost.getResults().getClass() == ArrayList.class){
                        ArrayList<Article> tempArticles = new ArrayList<Article>();
                        for (LinkedTreeMap jsonArticle : (ArrayList<LinkedTreeMap>)articlePost.getResults()) {
                            tempArticles.add(reconstructArticleFromJSON(jsonArticle));
                        }
                        delegate.didRetrieveArticles(tempArticles, null, null);
                    } else {
                        HashMap<String, ArrayList<LinkedTreeMap>> categories = gson.fromJson(gson.toJsonTree(articlePost.getResults()), HashMap.class);

                        for (String tempArrayListKey : categories.keySet()) {
                            //Now we need to cycle through each hash key and add it back to the list

                            ArrayList<LinkedTreeMap> categoryList = (ArrayList<LinkedTreeMap>) categories.get(tempArrayListKey);


                            ArrayList<Article> tempArticles = reconstructArticleArrayFromJSON(categoryList);
                            articles.put(tempArrayListKey, tempArticles);
                        }
                        delegate.didRetrieveArticles(articles, articlePost.getCategories(), null);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    delegate.didRetrieveArticles(null, null, error);
                }
            });
        }
    }

    public void articleForId(String article_id, Context context, final ArticleDelegate delegate){
        if(Online()){
            restAPI.getArticle(AnalyticsManager.installationUUID(context).toString(), article_id, Language.getLanguage(context).getLanguage(), new Callback<ArticlePost>() {
                @Override
                public void success(ArticlePost articlePost, Response response) {
                    //Rearrange the posts so that the first article has an image
                    //We want this to be the same order everywhere so we do it here
                    //If there's none, then we hide the image box

                    ArrayList<Article> articles = reconstructArticleArrayFromJSON((ArrayList<LinkedTreeMap>)articlePost.getResults());

                    if(articles.size() > 0){
                        delegate.didRetrieveArticle(articles.get(0));
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
        if (info == null) {
            return false;
        }

        return info.isConnected();
    }

    private ArrayList<Article> reconstructArticleArrayFromJSON(ArrayList<LinkedTreeMap> jsonArray) {
        Gson gson = gson();

        //Now we need to cycle through each hash key and add it back to the list
        ArrayList<Article> tempArticles = new ArrayList<Article>();

        for (LinkedTreeMap linkedTreeMapArticle : (ArrayList<LinkedTreeMap>) gson.fromJson(gson.toJsonTree(jsonArray), ArrayList.class)) {
            Article article = reconstructArticleFromJSON(linkedTreeMapArticle);
            tempArticles.add(article);
        }
        return tempArticles;
    }

    private Article reconstructArticleFromJSON(LinkedTreeMap json) {
        Gson gson = gson();

        Article article = gson.fromJson(gson.toJsonTree(json), Article.class);
        return article;
    }

    private Gson gson(){
        if(this.gson == null){
            this.gson = new Gson();
        }

        return this.gson;
    }

}
