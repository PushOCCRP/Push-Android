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
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.nineoldandroids.view.ViewHelper;
import com.push.app.ObservableList.BaseActivity;
import com.push.app.ObservableList.ObservableListView;
import com.push.app.ObservableList.ObservableScrollViewCallbacks;
import com.push.app.ObservableList.ScrollState;
import com.push.app.ObservableList.ScrollUtils;
import com.push.app.adapter.PostFragmentAdapter;
import com.push.app.adapter.PostListAdapter;
import com.push.app.fragment.AboutPage;
import com.push.app.fragment.DonatePage;
import com.push.app.interfaces.OnFragmentInteractionListener;
import com.push.app.model.Post;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.push.app.util.Contants.WORDPRESS_SERVER_URL;
import static com.push.app.util.Contants.WORDPRES_SLIDER_MAX_POSTS;

public class HomeActivity extends BaseActivity implements ObservableScrollViewCallbacks,FragmentDrawer.FragmentDrawerListener,OnFragmentInteractionListener {

    private View mImageView;
    private Toolbar mToolbarView;
    private View mListBackgroundView;
    private ObservableListView mListView;
    private int mParallaxImageHeight;

    private FragmentDrawer drawerFragment;
    private AQuery aq;

    /**
     * URL to fetch Wordpress recent posts by given category
     */
    private String WORDPRESS_FETCH_RECENT_POSTS_URL = "%s?json=get_recent_posts";
    private ArrayList<Post> recentPosts;
    private PostListAdapter mListAdapter;
    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText editSearch;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView firstItemHeadline;
    private TextView firstItemDate;
    private FrameLayout mHomeLayout;
    private View header;
//    private LinearLayout mFirstItemDescLayout;
    


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

         aq = new AQuery(this);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

       
        mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_main_swipe_refresh_layout);
        mHomeLayout = (FrameLayout)findViewById(R.id.mHomeLayout);

        mImageView = findViewById(R.id.FirstItem);
        mToolbarView =(Toolbar) findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.colorPrimary)));
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        //Setup the drawer
        setUpDrawer();

        mListView = (ObservableListView) findViewById(R.id.mList);
        mListView.setScrollViewCallbacks(this);

        initViews();
        // Set padding view for ListView. This is the flexible space.
        View paddingView = new View(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                mParallaxImageHeight);
        paddingView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        paddingView.setClickable(false);

        mListView.addHeaderView(paddingView);
//        setDummyData(mListView);

        // mListBackgroundView makes ListView's background except header view.
        mListBackgroundView = findViewById(R.id.list_background);



        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(Online())
                checkForNewContent(false);
                else {
                    Toast.makeText(HomeActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        //Display files from Cache
        displayFromCache();



    }

    private void initViews() {
        firstItemHeadline = (TextView)findViewById(R.id.firstPostHeadline);
        firstItemDate = (TextView)findViewById(R.id.first_post_Date);



        LayoutInflater inflater = getLayoutInflater();
         header = inflater.inflate(R.layout.first_list_item, mListView, false);


    }

    private void displayFromCache() {
        //TODO
        //add code for loading cached posts here

        String cachedJSON = getCachedPosts("json");
        if(cachedJSON !=null){
            loadFromCache(cachedJSON);
            checkForNewContent(false);
        }else
        checkForNewContent(true);
    }

    public void cachePosts(String key, String value) {
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            editor.commit();

        }
    }

    public String getCachedPosts(String key) {
        sharedPreferences = getSharedPreferences("preferences", Activity.MODE_PRIVATE);
        String res = null;
        if (sharedPreferences != null) {
            res = sharedPreferences.getString(key, null);
        }
        return res;
    }

    /**
     * Load new posts
     */
    private void checkForNewContent(boolean refresh) {

        if(Online()){
            WORDPRESS_FETCH_RECENT_POSTS_URL = String.format(WORDPRESS_FETCH_RECENT_POSTS_URL,WORDPRESS_SERVER_URL);
            //Download the news articles
            if(refresh)
            aq.progress(R.id.downloadProgress).ajax(WORDPRESS_FETCH_RECENT_POSTS_URL, JSONObject.class, this, "postDownloadCallBack");
            else
                aq.ajax(WORDPRESS_FETCH_RECENT_POSTS_URL, JSONObject.class, this, "postDownloadCallBack");



        }else{
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show();
        }

    }

    private void loadFromCache(String jsonString){
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONArray items = json.getJSONArray("posts");
            recentPosts = new ArrayList<Post>();
            for (int i = 0; i < items.length(); i++) {
                recentPosts.add(new Post(items.getJSONObject(i)));
            }


            //Display the downloaded data
            displayArticles(recentPosts);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void postDownloadCallBack(String url,JSONObject json,AjaxStatus status){
        if(json != null){
            try {

                //successful ajax call, show status code and json content
//                Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();
                cachePosts("json",json.toString());

                JSONArray items = json.getJSONArray("posts");
                recentPosts = new ArrayList<Post>();
                for (int i = 0; i < items.length(); i++) {
                    recentPosts.add(new Post(items.getJSONObject(i)));
                }

//                showSlidingLoadingView();
//                showNewsListLoading();



                //Display the downloaded data
                displayArticles(recentPosts);
            }catch (Exception e){

            }

        }else{

            //ajax error, show error code
            Toast.makeText(aq.getContext(), "Error:" + status.getCode(), Toast.LENGTH_LONG).show();
        }

    }


    /**
     * Displays the downloaded articles in the list and also populates the slide show
     * @param recentPosts
     */
    private void displayArticles(final ArrayList<Post> recentPosts) {
        ArrayList<Post> sliderPosts = new ArrayList<Post>();
        ArrayList<Post> listPosts = new ArrayList<>();


        for (int i = 0; i < recentPosts.size(); i++) {
            if (recentPosts.get(i).isSliderPost()
                    && sliderPosts.size() < WORDPRES_SLIDER_MAX_POSTS) {
                sliderPosts.add(recentPosts.get(i));
            } else {
                listPosts.add(recentPosts.get(i));
            }
        }

        //Extract the first item from the list
       firstItemHeadline.setText(listPosts.get(0).getTitle());
//        firstItemDate.setText(listPosts.get(0).getPublishedDate());
        //remove it from the list
        listPosts.remove(0);

        this.mListAdapter = new PostListAdapter(this,R.layout.list_news_item,listPosts);
        this.mListView.setAdapter(mListAdapter);
        mSwipeRefreshLayout.setRefreshing(false);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostFragmentAdapter.postItems = recentPosts;
                Intent i = new Intent(HomeActivity.this, DetailPostActivity.class);
                i.putExtra("postPosition",position);
                i.putExtra("postTitle",recentPosts.get(position).getTitle());
                startActivity(i);
            }
        });
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
        }
        else {
            return false;
        }
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onScrollChanged(mListView.getCurrentScrollY(), false, false);
    }

    private void setUpDrawer() {
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbarView);
        drawerFragment.setDrawerListener(this);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, -scrollY / 2);

        // Translate list background
        ViewHelper.setTranslationY(mListBackgroundView, Math.max(0, -scrollY + mParallaxImageHeight));
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
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
//                fragment = new HomeFragment();
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
            mToolbarView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            fragmentTransaction.replace(R.id.container_body, fragment,"Fragment");
            fragmentTransaction.commit();
            this.mHomeLayout.setVisibility(View.GONE);


        }

        // set the toolbar title
        try {
            getSupportActionBar().setTitle(title);
        }catch (NullPointerException e){
            e.printStackTrace();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id==R.id.action_search){
            handleMenuSearch();
        }

        return super.onOptionsItemSelected(item);
    }

    private void handleMenuSearch() {
        ActionBar action = getSupportActionBar(); //get the actionbar
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard

            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);

            //add the search icon in the action bar
            mSearchAction.setIcon(getResources().getDrawable(R.mipmap.ic_search_white));

            isSearchOpened = false;
        } else { //open the search entry

            action.setDisplayShowCustomEnabled(true); //enable it to display a
            // custom view in the action bar.
            action.setCustomView(R.layout.search_bar);//add the custom view
            action.setDisplayShowTitleEnabled(false); //hide the title

            editSearch = (EditText)action.getCustomView().findViewById(R.id.edtSearch); //the text editor

            //this is a listener to do a search when the user clicks on search button
            editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        doSearch();
                        return true;
                    }
                    imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
                    return false;
                }
            });

            editSearch.requestFocus();

            //open the keyboard focused in the edtSearch
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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

    private void doSearch() {

Toast.makeText(this,"Searching",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFragmentInteraction(Uri id) {

    }

    @Override
    public void onFragmentInteraction(String id) {

    }
}
