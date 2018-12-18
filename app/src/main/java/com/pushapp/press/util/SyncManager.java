package com.pushapp.press.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.internal.LinkedTreeMap;
import com.pushapp.press.R;
import com.pushapp.press.interfaces.RestApi;
import com.pushapp.press.interfaces.SyncManager.ArticleDelegate;
import com.pushapp.press.interfaces.SyncManager.ArticlesDelegate;
import com.pushapp.press.model.Article;
import com.pushapp.press.model.ArticlePost;
import com.pushapp.press.model.Category;
import com.pushapp.press.model.PushImage;
import com.pushapp.press.model.PushVideo;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.crashlytics.android.core.CrashlyticsCore.TAG;

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

                       Gson gson1 = new GsonBuilder()
                                .setExclusionStrategies(new ExclusionStrategy() {
                                    @Override
                                    public boolean shouldSkipField(FieldAttributes f) {
                                        return f.getDeclaringClass().equals(RealmObject.class);
                                    }

                                    @Override
                                    public boolean shouldSkipClass(Class<?> clazz) {
                                        return false;
                                    }
                                })
                                .registerTypeAdapter(Article.class, new ArticleSerializer())
                                .registerTypeAdapter(PushImage.class, new PushImageSerializer())
                                .registerTypeAdapter(PushVideo.class, new PushVideoSerializer())
                                .create();

                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();

                        HashMap<String, ArrayList<LinkedTreeMap>> categories = gson1.fromJson(gson1.toJsonTree(articlePost.getResults()), HashMap.class);




                        for (String tempArrayListKey : categories.keySet()) {
                            //Now we need to cycle through each hash key and add it back to the list
                            //Category category = reconstructCategoryFromJSON(categories.get(tempArrayListKey));
                            //category.setCategory(tempArrayListKey);
                            //category.setLanguage((String) categories.get(tempArrayListKey).get(0).get("language"));

                            //Log.i("categorie",categories.get(tempArrayListKey).toString());
                            RealmList<Article> tempArticles = new RealmList<>();

                            ArrayList<LinkedTreeMap> categoryList = (ArrayList<LinkedTreeMap>) categories.get(tempArrayListKey);
                            for (LinkedTreeMap jsonArticle : (ArrayList<LinkedTreeMap>)categories.get(tempArrayListKey)) {
                                Article article = reconstructArticleFromJSON(jsonArticle);
                                tempArticles.add(article);

                            }

                            Category category = new Category(tempArrayListKey,"en",tempArticles);
                            realm.copyToRealmOrUpdate(category);


                            ArrayList<Article> tempArticles1 = reconstructArticleArrayFromJSON(categoryList);
                            articles.put(tempArrayListKey, tempArticles1);
                           // Category category = new Category(tempArrayListKey,"en",articles);
                            //realm.copyToRealmOrUpdate(category);

                        }
                        realm.commitTransaction();

                        Log.i("Realm", realm.getPath());


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

    public void logLargeString(String str) {
        if(str.length() > 3000) {
            Log.i(TAG, str.substring(0, 3000));
            logLargeString(str.substring(3000));
        } else {
            Log.i(TAG, str); // continuation
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
//
        } catch (Exception e){
            return null;
        }
        //return null;
    }

    public ArrayList<Category> getCachedCategories(String key) {
      try {
            Realm realm = Realm.getDefaultInstance();
            RealmQuery<Category> query = realm.where(Category.class);

            // Java type flipping is such a huge pain...
            RealmResults<Category> categories = query.findAll();//.sort("publish_date", Sort.DESCENDING);
            ArrayList<Category> array = new ArrayList<Category>();
           array.addAll(categories);
            return array;

       } catch (Exception e){
            return null;
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
        Gson gson  = new GsonBuilder().serializeNulls()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);

                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .registerTypeAdapter(Article.class, new ArticleSerializer())
                .registerTypeAdapter(PushImage.class, new PushImageSerializer())
                .registerTypeAdapter(PushVideo.class, new PushVideoSerializer())
                .registerTypeAdapter(String.class, new StringConverter())
                .create();

        Article articleTest = new Article();
        try {
            String gsonTest = gson.toJson(json);

             articleTest = gson.fromJson(gsonTest, Article.class);
        }catch (Exception e){
            Log.e("exception",e.getLocalizedMessage());
        }
       Article article =gson.fromJson(gson.toJson(json),Article.class);

        return article;
    }


    private Gson gson(){
        if(this.gson == null){
            this.gson = new GsonBuilder()
                    .setExclusionStrategies(new ExclusionStrategy() {
                        @Override
                        public boolean shouldSkipField(FieldAttributes f) {
                            return f.getDeclaringClass().equals(RealmObject.class);
                        }

                        @Override
                        public boolean shouldSkipClass(Class<?> clazz) {
                            return false;
                        }
                    })
                    .registerTypeAdapter(Article.class, new ArticleSerializer())
                    .registerTypeAdapter(PushImage.class, new PushImageSerializer())
                    .registerTypeAdapter(PushVideo.class, new PushVideoSerializer())
                    .create();
            gson.serializeNulls();
        }

        return this.gson;
    }

}


