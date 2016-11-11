/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.push.rise;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.push.rise.adapter.PostFragmentAdapter;
import com.push.rise.adapter.PostListAdapter;
import com.push.rise.fragment.CategoryFragment;
import com.push.rise.fragment.DonatePage;
import com.push.rise.fragment.LanguageSelectDialogFragment;
import com.push.rise.interfaces.CacheManager.ImageCacheDelegate;
import com.push.rise.interfaces.OnFragmentInteractionListener;
import com.push.rise.interfaces.RestApi;
import com.push.rise.interfaces.SyncManager.ArticleDelegate;
import com.push.rise.model.Article;
import com.push.rise.model.ArticlePost;
import com.push.rise.model.ImageQueueSingleton;
import com.push.rise.util.AnalyticsManager;
import com.push.rise.util.CacheManager;
import com.push.rise.util.DateUtil;
import com.push.rise.util.Language;
import com.push.rise.util.LanguageListener;
import com.push.rise.util.PromotionsManager;
import com.push.rise.util.SettingsManager;
import com.push.rise.util.SyncManager;
import com.push.rise.interfaces.SyncManager.ArticlesDelegate;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.CrashManagerListener;



public class HomeActivity extends AppCompatActivity implements OnFragmentInteractionListener, LanguageListener, ImageCacheDelegate, ArticlesDelegate {

    private Toolbar mToolbarView;
    private ObservableListView mListView;
    private AQuery aq;
    static boolean isSearchShown = false;
    private MenuItem mSearchAction;
    private MenuItem mAboutAction;
    private boolean isSearchOpened = false;
    private EditText editSearch;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FrameLayout mHomeLayout,mSearchView;
    private boolean isNotification = false;
    private RestApi restAPI;
    private InputMethodManager imm;
    long lastLoadTime = 0;
    private Stack<Fragment> fragmentStack;
    private boolean searchItemClicked = false;
    private boolean firstRun = false;


    @Override

    // Whoa, this is messed up. Needs some serious refactoring.
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.push.rise.R.layout.activity_home);
        fragmentStack = new Stack<>();
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);

        setupWindow();

        boolean appcrashed = false;
        boolean didUserLeft = loadSavedPreferences();

        appcrashed=!didUserLeft;
        if(appcrashed) {
            clearCachedPosts();
        }
        savePreferences(false);

        aq = new AQuery(this);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(com.push.rise.R.id.activity_main_swipe_refresh_layout);
        mHomeLayout = (FrameLayout)findViewById(com.push.rise.R.id.mHomeLayout);
        mSearchView = (FrameLayout)findViewById(com.push.rise.R.id.mSearchView);

        setupActionBar();

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        if(Language.getAppLanguages(this).size() > 1) {
            //Register for language changes
            Language.addListener(this);
        }
        mListView = (ObservableListView) findViewById(R.id.mList);
        initViews();
        mListView.setBackgroundColor(Color.WHITE);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkForNewContent();

            }
        });

        isNotification = getIntent().getBooleanExtra("is_notification", false);
        if(isNotification) {
            //
            // handlePushNotification();
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setupWindow() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        int color = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = this.getColor(R.color.primary_dark_material_dark);
        } else {
            //noinspection deprecation
            color = this.getResources().getColor(R.color.primary_dark_material_dark);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(color);
        }
    }

    public void setupActionBar(){
        setSupportActionBar((Toolbar) findViewById(com.push.rise.R.id.toolbar));

        try {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch(NullPointerException e){}
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.logo);
        getSupportActionBar().setTitle("");
    }

    public boolean loadSavedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean("didUserLeft", true);
    }

    public void savePreferences(boolean value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("didUserLeft", value);
        editor.apply();
    }


    @Override
    protected void onResume() {
        super.onResume();

        savePreferences(false);
        checkForCrashes();
        checkForUpdates();

        lastLoadTime = sharedPreferences.getLong("lastLoadTime", 0);
        //set up rest API
        setUpRestApi();

        //Display any promotions
        setUpPromotions();

        //Display files from Cache
        displayFromCache();

        if(sharedPreferences.getBoolean("searchClicked", false)) {
            updateViews(true);
            HashMap<String, ArrayList<Article>> posts = getCachedPosts("Articles");
            ArrayList<String> categories = getCachedCategories("Categories");
            displayArticles(posts, categories);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("searchClicked",false);
            editor.commit();
        }

        if(isSearchShown){
            handleMenuSearch();
        }

        //throw new RuntimeException("This is a crash");

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        savePreferences(true);
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        savePreferences(true);
    }


    @Override
    public void onUserLeaveHint(){
        super.onUserLeaveHint();
        savePreferences(true);
    }


    private void checkForCrashes() {
        CrashManager.register(this, getResources().getString(R.string.hockey_key), new MyCrashManagerListener());
    }

    // Prevents this from leaking
    private static class MyCrashManagerListener extends CrashManagerListener {
        public boolean shouldAutoUploadCrashes() {
            return true;
        }
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        // UpdateManager.register(this, getResources().getString(R.string.hockey_key));
    }

    private void setUpPromotions() {
        ArrayList promotions = PromotionsManager.getPromotionsManager().promotions(getApplicationContext());

        View promotionView = findViewById(R.id.promotion);

        if(promotionView == null){
            return;
        }

        promotionView.setVisibility(View.INVISIBLE);
        View refreshView = findViewById(R.id.activity_main_swipe_refresh_layout);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) refreshView.getLayoutParams();
        layoutParams.setMargins(0, 0, 0, 0);
        findViewById(R.id.activity_main_swipe_refresh_layout).setLayoutParams(layoutParams);

        if(promotions != null && promotions.size() > 0){

            Map promotion = (Map) promotions.get(0);
            Map titles = (Map) promotion.get("title");
            Map texts = (Map) promotion.get("text");
            Map urls = (Map) promotion.get("urls");

            String language = Language.getLanguage(this).getLanguage();

            if(!titles.containsKey(language) || !texts.containsKey(language) || !urls.containsKey(language)){
                return;
            }

            String title = (String) titles.get(language);
            String text = (String) texts.get(language);
            String url = (String) urls.get(language);

            promotionView.setVisibility(View.VISIBLE);
            layoutParams.setMargins(0, 100, 0, 0);
            findViewById(R.id.activity_main_swipe_refresh_layout).setLayoutParams(layoutParams);

            TextView headlineView = (TextView)findViewById(R.id.promotion_title);
            headlineView.setText(title);

            TextView textView = (TextView)findViewById(R.id.promotion_text);
            textView.setText(text);

            addPromotionTap(findViewById(R.id.promotion), url);

        }
    }

    private void addPromotionTap(View promotionView, final String promotionURL) {
        promotionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(promotionURL));
                startActivity(browserIntent);
            }
        });
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
                .setEndpoint(getResources().getString(R.string.server_url)).build();
        //creating a service for adapter with our GET class
        restAPI = restAdapter.create(RestApi.class);
    }

    private void initViews() {
    }

    private void displayFromCache() {
        HashMap<String, ArrayList<Article>> articles = getCachedPosts("Articles");
        ArrayList<String> categories = getCachedCategories("Categories");

        if(articles != null && articles.size()>0){
            displayArticles(articles, categories);
            if(loadNews()) {
                checkForNewContent();
            }
        }else{
            firstRun = true;
            checkForNewContent();
        }


    }

    public void cachePosts(String key, final HashMap<String, ArrayList<Article>>articles) {
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(articles);
            editor.putString(key, json);
            editor.apply();
        }
    }

    public void cacheCategories(String key, final ArrayList<String> categories){
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(categories);
            editor.putString(key, json);
            editor.apply();
        }
    }

    public void clearCachedPosts() {
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
        }
    }

    public HashMap<String, ArrayList<Article>> getCachedPosts(String key) {
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        HashMap<String, ArrayList<Article>> articles = new HashMap<>();

        // Java type flipping is such a huge pain...
        if (sharedPreferences != null) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(key, "");
            HashMap<String, ArrayList<LinkedTreeMap>> tempArticles = gson.fromJson(json, HashMap.class);
            if(tempArticles == null){
                return null;
            }
            for(String tempKey: tempArticles.keySet()){
                ArrayList<LinkedTreeMap> tempArticleArray = tempArticles.get(tempKey);
                ArrayList<Article> categoryArticles = new ArrayList<>();

                for(LinkedTreeMap articleMap: tempArticleArray){
                    Article article = gson.fromJson(gson.toJsonTree(articleMap), Article.class);
                    categoryArticles.add(article);
                }
                articles.put(tempKey, categoryArticles);
            }
        }

        return articles;
    }

    public ArrayList<String> getCachedCategories(String key) {
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        ArrayList<String> categories = new ArrayList<>();
        if (sharedPreferences != null) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(key, "");
            try {
                ArrayList<LinkedTreeMap> tempCategories = gson.fromJson(json, ArrayList.class);
                if(tempCategories == null){
                    return null;
                }

                for (LinkedTreeMap tempCategory : tempCategories) {
                    categories.add(gson.fromJson(gson.toJsonTree(tempCategory), String.class));
                }
            } catch (Exception e) {
                categories = gson.fromJson(json, ArrayList.class);
            }
        }

        return categories;
    }

    boolean loadNews(){
        long difference = System.currentTimeMillis()
                - (lastLoadTime); // the time since the last load
        if (lastLoadTime == 0 || difference > (60 * 60 * 1000)) {
            Log.d("TIME", "Should Load");
            return true; // trigger a load

        }else {
            Log.d("TIME", "Should not Load");
            return false;
        }
    }

    // Language Listener for if the language changes
    @Override
    public void languageChanged() {
        checkForNewContent();
        setUpPromotions();
    }

    /**
     * Load new posts
     */
    private void checkForNewContent() {

        if(Online()){

            if(firstRun) {
                View downloadProgressView = findViewById(com.push.rise.R.id.downloadProgress);
                if(downloadProgressView != null) {
                    downloadProgressView.setVisibility(View.VISIBLE);
                }
            }

            mSwipeRefreshLayout.setRefreshing(true);
            SyncManager.getSyncManager().articles(this, this);
        }else{
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    public void didRetrieveArticles(Object articles, ArrayList<String> categories, RetrofitError error){
        if(articles == null){
            return;
        }

        HashMap<String, ArrayList<Article>> articlesList = (HashMap<String, ArrayList<Article>>) articles;
        HashMap<String, ArrayList<Article>> articlesListCopy = (HashMap<String, ArrayList<Article>>)articlesList.clone();
        if(error != null){
            mSwipeRefreshLayout.setRefreshing(false);
            if (firstRun) {
                View downloadProgressView = findViewById(com.push.rise.R.id.downloadProgress);
                if(downloadProgressView != null) {
                    downloadProgressView.setVisibility(View.GONE);
                }
                firstRun = false;
            }

            AnalyticsManager.logError(error.getLocalizedMessage());
            Log.e("ERROR", "Failed to parse JSON ", error);
            return;
        }


        //Go through each category and clean up

        for(String key: articlesList.keySet()){
            //Rearrange the posts so that the first article has an image
            //We want this to be the same order everywhere so we do it here
            //If there's none, then we hide the image box
            ArrayList<Article> tempArticles = articlesList.get(key);

            Article intendedTopArticle = null;
            for (Article article : tempArticles) {
                if (article.getImages().size() > 0) {
                    intendedTopArticle = article;
                    break;
                }
            }

            if (intendedTopArticle != null) {
                tempArticles.remove(intendedTopArticle);
                tempArticles.add(0, intendedTopArticle);
            }

            // Now replace the array with the new one
            articlesListCopy.remove(key);
            articlesListCopy.put(key, tempArticles);
        }

        articlesList = articlesListCopy;

        cachePosts("Articles", articlesList);
        cacheCategories("Categories", categories);

        displayArticles(articlesList, categories);
        updateLastLoadTime();
        mSwipeRefreshLayout.setRefreshing(false);

        if (firstRun) {
            View downloadProgressView = findViewById(com.push.rise.R.id.downloadProgress);
            if(downloadProgressView != null) {
                downloadProgressView.setVisibility(View.GONE);
            }
            firstRun = false;
        }
    }

    /**
     * Displays the downloaded articles in the list and also populates the slide show
     * @param articles
     */
    private void displayArticles( final HashMap<String, ArrayList<Article>> articles, ArrayList<String> categories)  {
        //try {
            // If recentPosts is empty, just return
            if(articles.size() < 1){
                return;
            }

        PostListAdapter mListAdapter = new PostListAdapter(this, R.layout.list_news_item, articles, categories, 5);

        final HashMap<String, ArrayList<Article>> mPosts = new HashMap<>(articles);

        // There's a bug where mPosts might be null if there's a bad connection or something
        // In which case PostFragmentAdapter will fail
        PostFragmentAdapter.postItems = mListAdapter.items(true);

        this.mListView.setAdapter(mListAdapter);
        mListAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!isSearchOpened) {
                    Object selectedItem = mListAdapter.getItemAtPosition(position);
                    if(selectedItem.getClass() == String.class){
                        Intent i = new Intent(getApplicationContext(), CategoryActivity.class);
                        i.putExtra("articles", mListAdapter.getArticlesForCategory((String)selectedItem));
                        startActivity(i);
                        return;
                    } else {
                        Article article = (Article) selectedItem;
                        Intent i = new Intent(HomeActivity.this, DetailPostActivity.class);

                        i.putExtra("postPosition", mListAdapter.items(false).indexOf(selectedItem));
                        i.putExtra("postTitle", article.getHeadline());
                        i.putExtra("description", article.getDescription());

                        startActivity(i);
                        AnalyticsManager.logContentView(article.getHeadline(),
                                "Article Opened", Integer.toString(article.getId()));
                    }
                }
            }

        });
    }

    // ImageCacheDelegate
    public void didLoadImage(Bitmap bitmap, ImageView imageView, String url) {
        imageView.setImageBitmap(bitmap);
    }

    public void errorLoadingImage(ImageView imageView, String url) {
        imageView.setImageResource(R.drawable.fallback);
    }


    /**
     * Check if we have a network connection
     * @return boolean
     */
    boolean Online() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        // check that there is an active network
        if (info != null) {
            return info.isConnected();
        }else {
            return false;
        }
    }

    private void displayView(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        switch (position) {
            case 0:
                this.mHomeLayout.setVisibility(View.VISIBLE);
                this.mSearchView.setVisibility(View.GONE);

                Fragment frag = fragmentManager.findFragmentByTag("Fragment");
                if(frag != null)
                    fragmentTransaction.remove(frag).commit();
                break;
            case 1:
                fragment = new DonatePage();
                break;
            case 2:
                Intent i = new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }

        if (fragment != null) {
            fragmentTransaction.replace(com.push.rise.R.id.container_body, fragment, "Fragment");
            fragmentTransaction.commit();
            this.mSearchView.setVisibility(View.GONE);
            this.mHomeLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {


        Fragment frag = getSupportFragmentManager().findFragmentByTag("Fragment");
        if(frag != null) {
            getSupportFragmentManager().beginTransaction().remove(frag).commit();
            this.mHomeLayout.setVisibility(View.VISIBLE);
        }
        else if(isSearchShown) {
            updateViews(false);
        }

        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.push.rise.R.menu.menu_main, menu);

        // Hide the language button if there's less than 2 languages
        if(Language.getAppLanguages(this).size() < 2) {
            menu.getItem(2).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id== R.id.action_search) {
            handleMenuSearch();
        }else if(id== R.id.action_about){
            Intent i = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(i);
        }else if(id== R.id.action_language){
            LanguageSelectDialogFragment newLanguageSelectDialogFragment = new LanguageSelectDialogFragment();
            newLanguageSelectDialogFragment.show(getSupportFragmentManager(), "languageSelect");
        }

        return super.onOptionsItemSelected(item);
    }

    void updateViews(boolean visibility){
        isSearchShown = visibility;
        if(visibility) {
            this.mSearchView.setVisibility(View.VISIBLE);
            if(this.mAboutAction != null) {
                this.mAboutAction.setVisible(false);
            }
            getSupportActionBar().setLogo(null);
        }
        else{
            //hides the keyboard
            if(null != editSearch )
                imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
            this.mSearchView.setVisibility(View.GONE);
            showDefaultActionBar();
        }
    }

    void showDefaultActionBar(){
        ActionBar action = getSupportActionBar(); //get the actionbar
        action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
        action.setDisplayShowTitleEnabled(false); //don't show the title in the action bar
        action.setLogo(com.push.rise.R.mipmap.logo);

        //add the search icon in the action bar
        mSearchAction.setIcon(getResources().getDrawable(com.push.rise.R.mipmap.ic_search_white));
        mAboutAction.setVisible(true);
        isSearchOpened = false;

    }

    private void handleMenuSearch() {


        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open
            updateViews(false);

            //hides the keyboard
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
            showDefaultActionBar();
        } else { //open the search entry

            updateViews(true);

            if(((ObservableListView)findViewById(com.push.rise.R.id.searchList)).getAdapter() !=null && !((ObservableListView)findViewById(com.push.rise.R.id.searchList)).getAdapter().isEmpty()) {
                ((TextView) findViewById(com.push.rise.R.id.searchResults)).setText(getString(com.push.rise.R.string.recent_results));
            }


            action.setDisplayShowCustomEnabled(true); //enable it to display a
            View view = getLayoutInflater().inflate(com.push.rise.R.layout.search_bar,
                    null);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            action.setCustomView(view,layoutParams);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title
            Toolbar parent = (Toolbar) view.getParent();
            parent.setContentInsetsAbsolute(0, 0);
            editSearch = (EditText)action.getCustomView().findViewById(com.push.rise.R.id.edtSearch); //the text editor



            //this is a listener to do a search when the user clicks on search button
            editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                        doSearch(editSearch.getText().toString());
                        editSearch.setText("");
                        return true;
                    }

                    return false;
                }
            });



            editSearch.requestFocus();
            //open the keyboard focused in the edtSearch
            imm.showSoftInput(editSearch, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            //If this is comign back from a search result things can get weird
            if(mSearchAction != null) {
                mSearchAction.setIcon(getResources().getDrawable(com.push.rise.R.mipmap.ic_action_cancel));
            }
            isSearchOpened = true;
            AnalyticsManager.logContentView("Search Opened");
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(com.push.rise.R.id.action_search);
        mAboutAction = menu.findItem(com.push.rise.R.id.action_about);
        return super.onPrepareOptionsMenu(menu);
    }

    private void doSearch(String searchString) {
        findViewById(com.push.rise.R.id.searchProgress).setVisibility(View.VISIBLE);
        ((TextView)findViewById(com.push.rise.R.id.searchResults)).setText(getString(com.push.rise.R.string.searching));

        restAPI.searchArticles(searchString, 20150501, 20150505, 2, 5, Language.getLanguage(this).getLanguage(), new Callback<ArticlePost>() {
            @Override
            public void success(final ArticlePost articlePost, Response response) {
                ArrayList<Article> results = (ArrayList<Article>)articlePost.getResults();
                if (results.size() > 0) {
                    displaySearchResults(results);
                } else {
                    Toast.makeText(HomeActivity.this, "No results to match your search", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("SEARCH ERROR", "Search Failed", error);
                Toast.makeText(HomeActivity.this, "No results to display", Toast.LENGTH_LONG).show();
                findViewById(com.push.rise.R.id.searchProgress).setVisibility(View.GONE);
                ((TextView) findViewById(com.push.rise.R.id.searchResults)).setText(getString(com.push.rise.R.string.no_search_results));
                updateViews(true);
            }
        });

        AnalyticsManager.logSearch(searchString);

    }

    private void displaySearchResults(final ArrayList<Article> articles) {
        findViewById(com.push.rise.R.id.searchProgress).setVisibility(View.GONE);

        ((TextView)findViewById(com.push.rise.R.id.searchResults)).setText(getString(com.push.rise.R.string.search_results));
        PostListAdapter mSearchAdapter = new PostListAdapter(HomeActivity.this, com.push.rise.R.layout.list_news_item, articles);
        ((ObservableListView)findViewById(com.push.rise.R.id.searchList)).setAdapter(mSearchAdapter);
        ((ObservableListView)findViewById(com.push.rise.R.id.searchList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(HomeActivity.this, DetailPostActivity.class);
                if(PostFragmentAdapter.postItems != null) {
                    PostFragmentAdapter.postItems.clear();
                } else {
                    PostFragmentAdapter.postItems = new ArrayList<Article>();
                }

                //PostFragmentAdapter.postItems.add(articles.get(position));
                i.putExtra("postPosition", position);
                i.putExtra("postTitle", articles.get(position).getHeadline());
                i.putExtra("description", articles.get(position).getDescription());

                AnalyticsManager.logContentView(articles.get(position).getHeadline(),
                        "Search Result Opened", Integer.toString(articles.get(position).getId()));

                // store the new time in the preferences file
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("searchClicked", true);
                editor.commit();

                //cachePosts("searchResults", articles);//cache these results

                startActivity(i);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri id) {

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    void updateLastLoadTime() {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        // store the new time in the preferences file
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long time = System.currentTimeMillis(); // unix time of now
        editor.putLong("lastLoadTime", time);
        editor.commit();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        float pixel = dp * density;
        return Math.round(pixel);
    }


}
