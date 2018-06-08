package com.pushapp.press.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
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

            String apiKey = AuthenticationManager.getAuthenticationManager().apiKey(this.applicationContext);
            restAPI.getArticles(AnalyticsManager.installationUUID(context).toString(), apiKey, language, true, new Callback<ArticlePost>() {
                @Override
                public void success(ArticlePost articlePost, Response response) {
                    //There's a bunch of type juggling here because of the nested nature of it all
                    Gson gson = gson();

                    HashMap<String, ArrayList<Article>> articles = new HashMap<String, ArrayList<Article>>();

                    // If categories are not enabled
                    if(articlePost.getResults().getClass() == ArrayList.class){
                        ArrayList<Article> tempArticles = new ArrayList<Article>();
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        for (LinkedTreeMap jsonArticle : (ArrayList<LinkedTreeMap>)articlePost.getResults()) {
                            Article article = reconstructArticleFromJSON(jsonArticle);
                            tempArticles.add(article);
                            realm.copyToRealmOrUpdate(article);
                        }
                        realm.commitTransaction();
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
            String apiKey = AuthenticationManager.getAuthenticationManager().apiKey(this.applicationContext);
            restAPI.getArticle(AnalyticsManager.installationUUID(context).toString(), apiKey, article_id, Language.getLanguage(context).getLanguage(), new Callback<ArticlePost>() {
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

    // Caching!!!


    public Object getCachedPosts(String key) {
//        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);

        try {
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<Article> query = realm.where(Article.class);

            // Java type flipping is such a huge pain...
            RealmResults<Article> articles = query.findAll().sort("publish_date", Sort.DESCENDING);
            ArrayList<Article> array = new ArrayList<>();
            array.addAll(articles.subList(0,9));
            return array;
//            if (tempArticles.getClass() == ArrayList.class) {
//                ArrayList<Article> articles = new ArrayList<Article>();
//                for (LinkedTreeMap articleMap : (ArrayList<LinkedTreeMap>) tempArticles) {
//                    Article article = gson.fromJson(gson.toJsonTree(articleMap), Article.class);
//                    articles.add(article);
//                }
//                return articles;
//            } else {
//                HashMap<String, ArrayList<Article>> articles = new HashMap<>();
//                HashMap<String, ArrayList<LinkedTreeMap>> tempArticlesHash = gson.fromJson(gson.toJsonTree(tempArticles), HashMap.class);
//                for (String tempKey : tempArticlesHash.keySet()) {
//                    ArrayList<LinkedTreeMap> tempArticleArray = tempArticlesHash.get(tempKey);
//                    ArrayList<Article> categoryArticles = new ArrayList<>();
//
//                    for (LinkedTreeMap articleMap : tempArticleArray) {
//                        Article article = gson.fromJson(gson.toJsonTree(articleMap), Article.class);
//                        categoryArticles.add(article);
//                    }
//                    articles.put(tempKey, categoryArticles);
//                }
//                return articles;
//            }
            //}
        } catch (Exception e){
            return null;
        }
        //return null;
    }

    public ArrayList<String> getCachedCategories(String key) {
//        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        ArrayList<String> categories = new ArrayList<>();
//        if (sharedPreferences != null) {
//            Gson gson = new Gson();
//            String json = sharedPreferences.getString(key, "");
//            try {
//                ArrayList<LinkedTreeMap> tempCategories = gson.fromJson(json, ArrayList.class);
//                if(tempCategories == null){
//                    return null;
//                }
//
//                for (LinkedTreeMap tempCategory : tempCategories) {
//                    categories.add(gson.fromJson(gson.toJsonTree(tempCategory), String.class));
//                }
//            } catch (Exception e) {
//                categories = gson.fromJson(json, ArrayList.class);
//            }
//        }

        return categories;
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
