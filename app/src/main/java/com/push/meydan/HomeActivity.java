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

package com.push.meydan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
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
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.SearchEvent;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.google.gson.Gson;
import com.infobip.push.Notification;
import com.push.meydan.adapter.PostFragmentAdapter;
import com.push.meydan.adapter.PostListAdapter;
import com.push.meydan.fragment.DonatePage;
import com.push.meydan.fragment.LanguageSelectDialogFragment;
import com.push.meydan.interfaces.OnFragmentInteractionListener;
import com.push.meydan.interfaces.RestApi;
import com.push.meydan.model.Article;
import com.push.meydan.model.ArticlePost;
import com.push.meydan.util.Contants;
import com.push.meydan.util.DateUtil;
import com.push.meydan.util.Language;
import com.push.meydan.util.LanguageListener;
import com.push.meydan.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Stack;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;



public class HomeActivity extends AppCompatActivity implements OnFragmentInteractionListener, LanguageListener {

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
    private TextView firstItemHeadline,firstItemDescription;
    private ImageView firstPostImage;
    private TextView firstItemDateandAuthor;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.push.meydan.R.layout.activity_home);
        fragmentStack = new Stack<>();
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);


        aq = new AQuery(this);
        setSupportActionBar((Toolbar) findViewById(com.push.meydan.R.id.toolbar));

        isNotification = getIntent().getBooleanExtra("is_notification", false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(com.push.meydan.R.id.activity_main_swipe_refresh_layout);
        mHomeLayout = (FrameLayout)findViewById(com.push.meydan.R.id.mHomeLayout);
        mSearchView = (FrameLayout)findViewById(com.push.meydan.R.id.mSearchView);

        mToolbarView =(Toolbar) findViewById(com.push.meydan.R.id.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(com.push.meydan.R.mipmap.logo);
        getSupportActionBar().setTitle("");
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //Setup the drawer
        //setUpDrawer();

        //Register for language changes
        Language.addListener(this);

        mListView = (ObservableListView) findViewById(com.push.meydan.R.id.mList);
        initViews();
        mListView.setBackgroundColor(Color.WHITE);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                checkForNewContent();

            }
        });

        if(isNotification){
            String extra = getIntent().getExtras().getString(Notification.DefaultNotificationHandler.INTENT_EXTRAS_KEY);
            Utils.log("Extras -> " + extra);
            try{
                JSONObject extrasObject = new JSONObject(extra);
                JSONObject extras = extrasObject.getJSONObject("payload").getJSONObject("extras");
                JSONObject additionalInfo = extras.getJSONObject("additionalInfo");
                Utils.log("Extras payload -> " + extras);
                if(additionalInfo.has("action") && additionalInfo.getString("action").equalsIgnoreCase("donation")) {
                    Utils.log("Displaying donation page");
                    displayView(1);

                    return;
                } else if(additionalInfo.has("article_id")) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    if(restAPI == null){
                        setUpRestApi();
                    }

                    restAPI.getArticle(additionalInfo.getString("article_id"), new Callback<ArticlePost>() {
                        @Override
                        public void success(ArticlePost articlePost, Response response) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            Intent i = new Intent(HomeActivity.this, DetailPostActivity.class);

                            PostFragmentAdapter.postItems.clear();
                            PostFragmentAdapter.postItems.add(articlePost.getResults().get(0));

                            i.putExtra("postPosition", 0);
                            i.putExtra("postTitle", articlePost.getResults().get(0).getHeadline());
                            i.putExtra("description", articlePost.getResults().get(0).getDescription());


                            cachePosts("searchResults", articlePost);//cache these results

                            startActivity(i);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            Log.e("ERROR", "Failed to parse JSON ", error);
                        }

                    });
                }
            }catch (JSONException ex){
                Utils.log("Error loading post -> " + ex.getMessage());
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        checkForCrashes();
        checkForUpdates();

        lastLoadTime = sharedPreferences.getLong("lastLoadTime", 0);
        //set up rest API
        setUpRestApi();

        //Display files from Cache
        displayFromCache();

        if(sharedPreferences.getBoolean("searchClicked", false)) {
            updateViews(true);
            displaySearchResults(getCachedPosts("searchResults"));
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("searchClicked",false);
            editor.commit();
        }
    }

    private void checkForCrashes() {
        CrashManager.register(this, getResources().getString(R.string.hockey_key));
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this, getResources().getString(R.string.hockey_key));
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
        LinearLayout firstItemView = (LinearLayout) getLayoutInflater().inflate(com.push.meydan.R.layout.first_item_view, null);
        firstItemHeadline = (TextView) firstItemView.findViewById(com.push.meydan.R.id.firstPostHeadline);
        firstItemDescription = (TextView) firstItemView.findViewById(com.push.meydan.R.id.postDescription);
        firstItemDateandAuthor = (TextView) firstItemView.findViewById(com.push.meydan.R.id.first_post_Date);
        firstPostImage = (ImageView) firstItemView.findViewById(com.push.meydan.R.id.firstPostImage);
        // This is required to disable header's list selector effect
        mListView.addHeaderView(firstItemView);
        firstItemView.setClickable(false);
    }

    private void displayFromCache() {

        //Download the notification content here
        if(isNotification){
            Utils.log("This is a notification");
            String extra = getIntent().getExtras().getString(Notification.DefaultNotificationHandler.INTENT_EXTRAS_KEY);
            Utils.log("Extras -> " + extra);
            try{
                JSONObject extrasObject = new JSONObject(extra);
                JSONObject extras = extrasObject.getJSONObject("payload").getJSONObject("extras");
                Utils.log("Extras payload -> " + extras.getString("url"));
            }catch (Exception ex){
                Utils.log("Error loading post -> " + ex.getMessage());
            }


            ArticlePost cachedPOSTS = getCachedPosts("Articles");
            if(cachedPOSTS != null && cachedPOSTS.getResults().size()>0){
                displayArticles(cachedPOSTS);
                if(loadNews()) //check whether our load time is due
                checkForNewContent();
            }else{
                firstRun = true;
                checkForNewContent();

            }
        }else{

            ArticlePost cachedPOSTS = getCachedPosts("Articles");
            if(cachedPOSTS != null && cachedPOSTS.getResults().size()>0){
                displayArticles(cachedPOSTS);
                if(loadNews()) //check whether our load time is due
                checkForNewContent();
            }else{
                firstRun = true;
                checkForNewContent();
        }
        }

    }

    public void cachePosts(String key, final ArticlePost articles) {
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(articles);
            editor.putString(key, json);
             editor.commit();

        }
            }



    public ArticlePost getCachedPosts(String key) {
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        ArticlePost articlePost = new ArticlePost();
        if (sharedPreferences != null) {
            Gson gson = new Gson();
            String json = sharedPreferences.getString(key, "");
            articlePost = gson.fromJson(json, ArticlePost.class);
                  }


        return articlePost;
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
    }

    /**
     * Load new posts
     */
    private void checkForNewContent() {

        if(Online()){

            if(firstRun)findViewById(com.push.meydan.R.id.downloadProgress).setVisibility(View.VISIBLE);

            //Get the language currently set, defaults to Azerbaijari ("az")
            Locale locale = Language.getLanguage(getApplicationContext());

            String language = "az";
            if(locale != null) {
                language = locale.getLanguage();
            }

            mSwipeRefreshLayout.setRefreshing(true);
                restAPI.getArticles(language, new Callback<ArticlePost>() {
                    @Override
                    public void success(ArticlePost articlePost, Response response) {
                        //Rearrange the posts so that the first article has an image
                        //We want this to be the same order everywhere so we do it here
                        //If there's none, then we hide the image box
                        Article intendedTopArticle = null;
                        for (Article article : articlePost.getResults()) {
                            if (article.getImages().size() > 0) {
                                intendedTopArticle = article;
                                break;
                            }
                        }

                        if(intendedTopArticle != null) {
                            articlePost.getResults().remove(intendedTopArticle);
                            articlePost.getResults().add(0, intendedTopArticle);
                        }


                        cachePosts("Articles", articlePost);
                        displayArticles(articlePost);
                        updateLastLoadTime();
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (firstRun) {
                            findViewById(com.push.meydan.R.id.downloadProgress).setVisibility(View.GONE);
                            firstRun = false;
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (firstRun) {
                            findViewById(com.push.meydan.R.id.downloadProgress).setVisibility(View.GONE);
                            firstRun = false;
                        }
                        Log.e("ERROR", "Failed to parse JSON ", error);
                    }
                });


        }else{
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    /**
     * Displays the downloaded articles in the list and also populates the slide show
     * @param recentPosts
     */
    private void displayArticles( final ArticlePost recentPosts)  {
        try {

            // If recentPosts is empty, just return
            if(recentPosts.getResults().size() < 1){
                return;
            }

            final ArrayList<Article> mPosts = new ArrayList<>();
            mPosts.addAll(recentPosts.getResults());

            // There's a bug where mPosts might be null if there's a bad connection or something
            // In which case PostFragmentAdapter will f
            PostFragmentAdapter.postItems = mPosts;

            RelativeLayout mainImageHolder = (RelativeLayout) mHomeLayout.findViewById(com.push.meydan.R.id.mainImageHolder);

            if (recentPosts.getResults().get(0).getImages().size() > 0) {
                mainImageHolder.setVisibility(RelativeLayout.VISIBLE);


                aq.id(firstPostImage).progress(com.push.meydan.R.id.image_progress).image(recentPosts.getResults().get(0).getImages().get(0).get("url"), true, true, 0, com.push.meydan.R.drawable.fallback, null, AQuery.FADE_IN);

            } else {
                mainImageHolder.setVisibility(RelativeLayout.GONE);
            }

            firstItemHeadline.setText(recentPosts.getResults().get(0).getHeadline());
            firstItemDescription.setText(recentPosts.getResults().get(0).getDescription());

            if(recentPosts.getResults().get(0).getAuthor().length() > 0) {
                String seperator = Language.bylineSeperator(getApplicationContext());
                firstItemDateandAuthor.setText(DateUtil.setTime(getApplicationContext(), DateUtil.postsDatePublishedFormatter.parse(String.valueOf(recentPosts.getResults().get(0).getPublishDate())).getTime(), true) + seperator + recentPosts.getResults().get(0).getAuthor());
            } else {
                firstItemDateandAuthor.setText(DateUtil.setTime(getApplicationContext(), DateUtil.postsDatePublishedFormatter.parse(String.valueOf(recentPosts.getResults().get(0).getPublishDate())).getTime(), true));
            }
            //remove it from the list
            recentPosts.getResults().remove(0);


            PostListAdapter mListAdapter = new PostListAdapter(this, com.push.meydan.R.layout.list_news_item, recentPosts.getResults());
            this.mListView.setAdapter(mListAdapter);
            mSwipeRefreshLayout.setRefreshing(false);


            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(!isSearchOpened) {
                        Intent i = new Intent(HomeActivity.this, DetailPostActivity.class);

                        i.putExtra("postPosition", position);
                        i.putExtra("postTitle", mPosts.get(position).getHeadline());
                        i.putExtra("description", mPosts.get(position).getDescription());

                        startActivity(i);
                        Answers.getInstance().logContentView(new ContentViewEvent()
                                .putContentName(mPosts.get(position).getHeadline())
                                .putContentType("Article Opened")
                                .putContentId(Integer.toString(mPosts.get(position).getId())));
                    }

                }

            });

        }catch (ParseException e){
            e.printStackTrace();
        }
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
            fragmentTransaction.replace(com.push.meydan.R.id.container_body, fragment, "Fragment");
            fragmentTransaction.commit();
            this.mSearchView.setVisibility(View.GONE);
            this.mHomeLayout.setVisibility(View.GONE);
        }

        // set the toolbar title
       //setActionBarTitle(title);
    }

    /*void setActionBarTitle(String title){
        try {

            getSupportActionBar().setDisplayShowCustomEnabled(false); //disable the search TextView on the actionbar
            getSupportActionBar().setDisplayShowTitleEnabled(false); //show the title in the action bar
            getSupportActionBar().setTitle("");
            //hides the keyboard
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.mipmap.ic_search_white));

            isSearchOpened = false;
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
*/
    @Override
    public void onBackPressed() {


            Fragment frag = getSupportFragmentManager().findFragmentByTag("Fragment");
            if(frag != null) {
                getSupportFragmentManager().beginTransaction().remove(frag).commit();
                this.mHomeLayout.setVisibility(View.VISIBLE);
                //setActionBarTitle(getString(R.string.app_name));
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
        getMenuInflater().inflate(com.push.meydan.R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id== com.push.meydan.R.id.action_search) {
            handleMenuSearch();
        }else if(id== com.push.meydan.R.id.action_about){
            Intent i = new Intent(HomeActivity.this, AboutActivity.class);
            startActivity(i);
        }else if(id== com.push.meydan.R.id.action_language){
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
        action.setLogo(com.push.meydan.R.mipmap.logo);

        //add the search icon in the action bar
        mSearchAction.setIcon(getResources().getDrawable(com.push.meydan.R.mipmap.ic_search_white));
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

            if(((ObservableListView)findViewById(com.push.meydan.R.id.searchList)).getAdapter() !=null && !((ObservableListView)findViewById(com.push.meydan.R.id.searchList)).getAdapter().isEmpty()) {
                ((TextView) findViewById(com.push.meydan.R.id.searchResults)).setText(getString(com.push.meydan.R.string.recent_results));
            }


            action.setDisplayShowCustomEnabled(true); //enable it to display a
            View view = getLayoutInflater().inflate(com.push.meydan.R.layout.search_bar,
                    null);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            action.setCustomView(view,layoutParams);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title
            Toolbar parent = (Toolbar) view.getParent();
            parent.setContentInsetsAbsolute(0, 0);
            editSearch = (EditText)action.getCustomView().findViewById(com.push.meydan.R.id.edtSearch); //the text editor



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
            mSearchAction.setIcon(getResources().getDrawable(com.push.meydan.R.mipmap.ic_action_cancel));
            isSearchOpened = true;
            Answers.getInstance().logContentView(new ContentViewEvent()
                    .putContentType("Search Opened"));

        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(com.push.meydan.R.id.action_search);
        mAboutAction = menu.findItem(com.push.meydan.R.id.action_about);
        return super.onPrepareOptionsMenu(menu);
    }

    private void doSearch(String searchString) {
        findViewById(com.push.meydan.R.id.searchProgress).setVisibility(View.VISIBLE);
        ((TextView)findViewById(com.push.meydan.R.id.searchResults)).setText(getString(com.push.meydan.R.string.searching));

        restAPI.searchArticles(searchString, 20150501, 20150505, 2, 5, new Callback<ArticlePost>() {
            @Override
            public void success(final ArticlePost articlePost, Response response) {
                if (articlePost.getResults().size() > 0) {
                    displaySearchResults(articlePost);
                } else {
                    Toast.makeText(HomeActivity.this, "No results to match your search", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("SEARCH ERROR", "Search Failed", error);
                Toast.makeText(HomeActivity.this, "No results to display", Toast.LENGTH_LONG).show();
                findViewById(com.push.meydan.R.id.searchProgress).setVisibility(View.GONE);
                ((TextView) findViewById(com.push.meydan.R.id.searchResults)).setText(getString(com.push.meydan.R.string.no_search_results));
                updateViews(true);
            }
        });

        Answers.getInstance().logSearch(new SearchEvent()
                .putQuery(searchString));

    }

    private void displaySearchResults(final ArticlePost articlePost) {
        findViewById(com.push.meydan.R.id.searchProgress).setVisibility(View.GONE);
        ((TextView)findViewById(com.push.meydan.R.id.searchResults)).setText(getString(com.push.meydan.R.string.search_results));
        PostListAdapter mSearchAdapter = new PostListAdapter(HomeActivity.this, com.push.meydan.R.layout.list_news_item, articlePost.getResults());
        ((ObservableListView)findViewById(com.push.meydan.R.id.searchList)).setAdapter(mSearchAdapter);
        ((ObservableListView)findViewById(com.push.meydan.R.id.searchList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(HomeActivity.this, DetailPostActivity.class);
                PostFragmentAdapter.postItems.clear();
                PostFragmentAdapter.postItems.add(articlePost.getResults().get(position));
                i.putExtra("postPosition", position);
                i.putExtra("postTitle", articlePost.getResults().get(position).getHeadline());
                i.putExtra("description", articlePost.getResults().get(position).getDescription());

                Answers.getInstance().logContentView(new ContentViewEvent()
                        .putContentName(articlePost.getResults().get(position).getHeadline())
                        .putContentType("Search Result Opened")
                        .putContentId(Integer.toString(articlePost.getResults().get(position).getId())));

                // store the new time in the preferences file
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("searchClicked", true);
                editor.commit();

                cachePosts("searchResults", articlePost);//cache these results

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
