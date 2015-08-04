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

package com.push.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.google.gson.Gson;
import com.infobip.push.Notification;
import com.push.app.adapter.PostFragmentAdapter;
import com.push.app.adapter.PostListAdapter;
import com.push.app.fragment.AboutPage;
import com.push.app.fragment.DonatePage;
import com.push.app.interfaces.OnFragmentInteractionListener;
import com.push.app.interfaces.RestApi;
import com.push.app.model.Article;
import com.push.app.model.ArticlePost;
import com.push.app.util.Contants;
import com.push.app.util.DateUtil;
import com.push.app.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;
import java.util.TimeZone;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener, OnFragmentInteractionListener {

    private Toolbar mToolbarView;
    private ObservableListView mListView;
    private AQuery aq;
    static boolean isSearchShown = false;
    private MenuItem mSearchAction;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentStack = new Stack<>();
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);


         aq = new AQuery(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        isNotification = getIntent().getBooleanExtra("is_notification", false);

        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_main_swipe_refresh_layout);
        mHomeLayout = (FrameLayout)findViewById(R.id.mHomeLayout);
        mSearchView = (FrameLayout)findViewById(R.id.mSearchView);

        mToolbarView =(Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        //Setup the drawer
        setUpDrawer();

        mListView = (ObservableListView) findViewById(R.id.mList);
        initViews();

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
                }
            }catch (JSONException ex){
                Utils.log("Error loading post -> " + ex.getMessage());
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
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
                .setEndpoint(Contants.SERVER_URL).build();
        //creating a service for adapter with our GET class
        restAPI = restAdapter.create(RestApi.class);
    }

    private void initViews() {

        LinearLayout firstItemView = (LinearLayout) getLayoutInflater().inflate(R.layout.first_item_view, null);
        firstItemHeadline = (TextView) firstItemView.findViewById(R.id.firstPostHeadline);
        firstItemDescription = (TextView) firstItemView.findViewById(R.id.postDescription);
        firstItemDateandAuthor = (TextView) firstItemView.findViewById(R.id.first_post_Date);
        firstPostImage = (ImageView) firstItemView. findViewById(R.id.firstPostImage);
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

    /**
     * Load new posts
     */
    private void checkForNewContent() {

        if(Online()){

            if(firstRun)findViewById(R.id.downloadProgress).setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(true);
                restAPI.getArticles(new Callback<ArticlePost>() {
                    @Override
                    public void success(ArticlePost articlePost, Response response) {
                        cachePosts("Articles",articlePost);
                        displayArticles(articlePost);
                        updateLastLoadTime();
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(firstRun){
                            findViewById(R.id.downloadProgress).setVisibility(View.GONE);
                            firstRun = false;
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(firstRun){
                            findViewById(R.id.downloadProgress).setVisibility(View.GONE);
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
//            firstItemView.setVisibility(View.VISIBLE);

            final ArrayList<Article> mPosts = new ArrayList<>();
            mPosts.addAll(recentPosts.getResults());
            PostFragmentAdapter.postItems = mPosts;

            if (recentPosts.getResults().get(0).getImageUrls().size() > 0) {
                aq.id(firstPostImage).progress(R.id.image_progress).image(recentPosts.getResults().get(0).getImageUrls().get(0), true, true, 0, R.drawable.fallback,null, AQuery.FADE_IN);
            }
            firstItemHeadline.setText(recentPosts.getResults().get(0).getHeadline());
            firstItemDescription.setText(recentPosts.getResults().get(0).getDescription());
            firstItemDateandAuthor.setText(DateUtil.setTime(DateUtil.postsDatePublishedFormatter.parse(String.valueOf(recentPosts.getResults().get(0).getPublishDate())).getTime()) + " by " + recentPosts.getResults().get(0).getAuthor());
            //remove it from the list
            recentPosts.getResults().remove(0);


            PostListAdapter mListAdapter = new PostListAdapter(this, R.layout.list_news_item, recentPosts.getResults());
            this.mListView.setAdapter(mListAdapter);
            mSwipeRefreshLayout.setRefreshing(false);


            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent i = new Intent(HomeActivity.this, DetailPostActivity.class);

                    i.putExtra("postPosition", position);
                    i.putExtra("postTitle", mPosts.get(position).getHeadline());
                    i.putExtra("description",mPosts.get(position).getDescription());

                    startActivity(i);
                }
            });

            //TODO @Bryan  uncommented this. Causes detail to page to keep opening endlessly after notification when you press back key
//            if (isNotification) { //This is a way of displaying a test article from push - To be removed in production
//                Utils.log("Displaying test article = notification");
//                mListView.performItemClick(mListAdapter.getView(0, null, null), 0, mListAdapter.getItemId(0));
//            }
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

    private void setUpDrawer() {
        FragmentDrawer drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbarView);
        drawerFragment.setDrawerListener(this);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

        displayView(position);
    }

    private void displayView(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                this.mHomeLayout.setVisibility(View.VISIBLE);
                this.mSearchView.setVisibility(View.GONE);
                title = getResources().getString(R.string.app_name);

                Fragment frag = fragmentManager.findFragmentByTag("Fragment");
                if(frag != null)
                    fragmentTransaction.remove(frag).commit();
                break;
            case 1:
                fragment = new DonatePage();
                title = getString(R.string.title_item_one);
                break;
            case 2:
                fragment = new AboutPage();
                title = getString(R.string.title_item_two);
                break;
            default:
                break;
        }

        if (fragment != null) {
            fragmentTransaction.replace(R.id.container_body, fragment, "Fragment");
            fragmentTransaction.commit();
            this.mSearchView.setVisibility(View.GONE);
            this.mHomeLayout.setVisibility(View.GONE);
        }

        // set the toolbar title
       setActionBarTitle(title);
    }

    void setActionBarTitle(String title){
        try {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayShowCustomEnabled(false); //disable the search TextView on the actionbar
            getSupportActionBar().setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.mipmap.ic_search_white));

            isSearchOpened = false;
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {


            Fragment frag = getSupportFragmentManager().findFragmentByTag("Fragment");
            if(frag != null) {
                getSupportFragmentManager().beginTransaction().remove(frag).commit();
                this.mHomeLayout.setVisibility(View.VISIBLE);
                setActionBarTitle(getString(R.string.app_name));
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
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.action_search){
            handleMenuSearch();
        }else if(id==R.id.action_refresh_list){
            checkForNewContent();
        }

        return super.onOptionsItemSelected(item);
    }

    void updateViews(boolean visibility){
        isSearchShown = visibility;
        if(visibility) {
            this.mSearchView.setVisibility(View.VISIBLE);
        }
        else{
            //hides the keyboard
            if(null != editSearch )
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
            this.mSearchView.setVisibility(View.GONE);
            showDefaultActionBAr();

        }


    }

    void showDefaultActionBAr(){
        ActionBar action = getSupportActionBar(); //get the actionbar
        action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
        action.setDisplayShowTitleEnabled(true); //show the title in the action bar


        //add the search icon in the action bar
        mSearchAction.setIcon(getResources().getDrawable(R.mipmap.ic_search_white));

        isSearchOpened = false;

    }

    private void handleMenuSearch() {


        ActionBar action = getSupportActionBar(); //get the actionbar

        if(isSearchOpened){ //test if the search is open
            updateViews(false);

            //hides the keyboard
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
            showDefaultActionBAr();
        } else { //open the search entry

            updateViews(true);

   if(((ObservableListView)findViewById(R.id.searchList)).getAdapter() !=null && !((ObservableListView)findViewById(R.id.searchList)).getAdapter().isEmpty())
                ((TextView)findViewById(R.id.searchResults)).setText(getString(R.string.recent_results));


            action.setDisplayShowCustomEnabled(true); //enable it to display a
            View view = getLayoutInflater().inflate(R.layout.search_bar,
                    null);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT);
            action.setCustomView(view,layoutParams);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title
            Toolbar parent = (Toolbar) view.getParent();
            parent.setContentInsetsAbsolute(0, 0);
            editSearch = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor



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
            mSearchAction.setIcon(getResources().getDrawable(R.mipmap.ic_action_cancel));
            isSearchOpened = true;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    private void doSearch(String searchString) {
        findViewById(R.id.searchProgress).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.searchResults)).setText(getString(R.string.searching));

        restAPI.searchArticles(searchString, 20150501, 20150505, 2, 5, new Callback<ArticlePost>() {
            @Override
            public void success(final ArticlePost articlePost, Response response) {
                if (articlePost.getTotalResults() > 0) {
                    displaySearchResults(articlePost);
                } else {
                    Toast.makeText(HomeActivity.this, "No results to match your search", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("SEARCH ERROR","Search Failed",error);
                Toast.makeText(HomeActivity.this, "No results to display", Toast.LENGTH_LONG).show();
                findViewById(R.id.searchProgress).setVisibility(View.GONE);
                ((TextView)findViewById(R.id.searchResults)).setText(getString(R.string.no_search_results));
                updateViews(true);
            }
        });




    }

    private void displaySearchResults(final ArticlePost articlePost) {
        findViewById(R.id.searchProgress).setVisibility(View.GONE);
        ((TextView)findViewById(R.id.searchResults)).setText(getString(R.string.search_results));
        PostListAdapter mSearchAdapter = new PostListAdapter(HomeActivity.this, R.layout.list_news_item, articlePost.getResults());
        ((ObservableListView)findViewById(R.id.searchList)).setAdapter(mSearchAdapter);
        ((ObservableListView)findViewById(R.id.searchList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(HomeActivity.this, DetailPostActivity.class);
                PostFragmentAdapter.postItems.clear();
                PostFragmentAdapter.postItems.add(articlePost.getResults().get(position));
                i.putExtra("postPosition", position);
                i.putExtra("postTitle", articlePost.getResults().get(position).getHeadline());
                i.putExtra("description", articlePost.getResults().get(position).getDescription());

                // store the new time in the preferences file
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("searchClicked",true);
                editor.commit();

                cachePosts("searchResults",articlePost);//cache these results

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

}
