package com.push.app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.push.app.fragment.HomeFragment;
import com.push.app.fragment.Item_one;
import com.push.app.fragment.Item_two;
import com.push.app.interfaces.OnFragmentInteractionListener;
import com.push.app.listener.PostListener;
import com.push.app.model.Attachment;
import com.push.app.model.CustomScrollView;
import com.push.app.model.Post;
import com.push.app.util.Contants;
import com.push.app.util.ImageUtil;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.push.app.util.Contants.WORDPRESS_SERVER_URL;
import static com.push.app.util.Contants.WORDPRES_SLIDER_MAX_POSTS;


public class MainActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener,OnFragmentInteractionListener {

    private MenuItem mSearchAction;
    private boolean isSearchOpened = false;
    private EditText editSearch;
    private AQuery aq;

    /**
     * Adapter for the slider
     */
    private PostPagerAdapter mPostAdapter;

    /**
     * Position of the active post in the slider
     */
    private int mPosition;

    /**
     * ViewPager for the slider
     */
    private ViewPager mPostPager;

    /**
     * Indicator of the slider. This is the dots that are shown when the slider
     * changes its position
     */
    private CirclePageIndicator mIndicator;

    private RecyclerView mNewsListView;

    /**
     * Layout for the progress bar for loading the posts
     */
    private LinearLayout mListNewsLoading;

    /**
     * Layout for the progress bar of the loading slider
     */
    private LinearLayout mSliderLoading;
    /**
    * URL to fetch Wordpress recent posts by given category
    */
    private String WORDPRESS_FETCH_RECENT_POSTS_URL = "%s?json=get_recent_posts";

    /**
     * Handler for the slider
     */
    private Handler mSliderHandler;

    /**
     * Height of the device screen
     */
    private Integer mScreenHeight;

    /**
     * Width of the device screen
     */
    private Integer mScreenWidth;

    /**
    * List of posts for the slider
    */
    private ArrayList<Post> recentPosts;
    /**
     * Thread for slider animation
     */
    private Runnable mSliderThread;

    /**
     * Layout for the news
     */
    private LinearLayout mNewsContainer;

    /**
     * Main Layout for the home screen
     */
    private RelativeLayout mHomeScreenLayout;

    /**
     * The actionbar
     */
    private Toolbar mToolbar;

    private FragmentDrawer drawerFragment;

    private NewListAdapter mListAdapter;

    /**
     * ArrayList to hold all download Posts
     * @param Post
     */
    private ArrayList<Post> downloadedPosts;

    /**
     * List of posts for the slider
     */
    private ArrayList<Post> mSliderPosts;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        aq = new AQuery(this);

        //initialise the actionBar
        initActionBar();

        //Setup the drawer
        setUpDrawer();

        initViews();

        //initialize the display components
        initComponents();

        //Display files from Cache
        displayFromCache();
    }

    /**
     * Initializes the Activity views
     */
    private void initViews() {
       /* this.mPullRefreshScrollView = (PullToRefreshScrollView) findViewById(R.id.pull_refresh_scrollview);
        this.mPullRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                loadMorePosts();
                mPullRefreshScrollView.onRefreshComplete();
            }
        });*/

        this.mNewsListView = (RecyclerView) findViewById(R.id.newList);
        this.mNewsListView.setHasFixedSize(true);
        this.mNewsListView.setLayoutManager(new LinearLayoutManager(this));
        this.mNewsListView.setItemAnimator(new DefaultItemAnimator());
        this.mHomeScreenLayout = (RelativeLayout) findViewById(R.id.act_main_rl);
        this.mNewsContainer = (LinearLayout) findViewById(R.id.main_news_container);
        this.mSliderLoading = (LinearLayout) findViewById(R.id.slider_loading);
        this.mListNewsLoading = (LinearLayout) findViewById(R.id.list_news_loading);
        //this.mDotsContainer = (LinearLayout) findViewById(R.id.slider_posts_dots_container);



    }

    private void displayFromCache() {

        checkForNewContent();
    }

    private void checkForNewContent() {

        if(Online()){
            WORDPRESS_FETCH_RECENT_POSTS_URL = String.format(WORDPRESS_FETCH_RECENT_POSTS_URL,WORDPRESS_SERVER_URL);
            //Download the news articles
            aq.ajax(WORDPRESS_FETCH_RECENT_POSTS_URL, JSONObject.class, this,"postDownloadCallBack");

        }else{
            Toast.makeText(this,"Check your internet connection",Toast.LENGTH_LONG).show();
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
        }
        else {
            return false;
        }
    }

    private void setUpDrawer() {
        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
    }

    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public class NewListAdapter extends RecyclerView.Adapter<NewListAdapter.PostViewHolder>{

        public class PostViewHolder extends RecyclerView.ViewHolder {
            CardView cv;
            TextView postName;
            TextView postDescription;
            ImageView postImage;

            PostViewHolder(View itemView) {
                super(itemView);
                cv = (CardView)itemView.findViewById(R.id.cv);
                postName = (TextView)itemView.findViewById(R.id.post_name);
                postDescription = (TextView)itemView.findViewById(R.id.post_Description);
                postImage = (ImageView)itemView.findViewById(R.id.post_Image);
            }
        }

        List<Post> posts;

        NewListAdapter(List<Post> posts){
            this.posts = posts;
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        @Override
        public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_news_item, viewGroup, false);
            PostViewHolder pvh = new PostViewHolder(v);
            return pvh;
        }

        @Override
        public void onBindViewHolder(PostViewHolder personViewHolder, int i) {
            personViewHolder.postName.setText(posts.get(i).getTitle());
            personViewHolder.postDescription.setText(posts.get(i).getContent());
//            personViewHolder.postImage.setImageResource(posts.get(i).get);
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
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

        if(isSearchOpened){ //test if the search is open

            action.setDisplayShowCustomEnabled(false); //disable a custom view inside the actionbar
            action.setDisplayShowTitleEnabled(true); //show the title in the action bar

            //hides the keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
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
                    return false;
                }
            });

            editSearch.requestFocus();

            //open the keyboard focused in the edtSearch
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editSearch, InputMethodManager.SHOW_IMPLICIT);

            //add the close icon
            mSearchAction.setIcon(getResources().getDrawable(R.mipmap.ic_action_cancel));

            isSearchOpened = true;
        }
    }

    @Override
    public void onBackPressed() {
        if(isSearchOpened) {
            handleMenuSearch();
            return;
        }
        super.onBackPressed();
    }

    private void doSearch() {
//
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);

    }

    private void displayView(int position) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                this.mHomeScreenLayout.setVisibility(View.VISIBLE);
//                fragment = new HomeFragment();
                title = getString(R.string.title_home);
                break;
            case 1:
                fragment = new Item_one();
                title = getString(R.string.title_item_one);
                break;
            case 2:
                fragment = new Item_two();
                title = getString(R.string.title_item_two);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
            this.mHomeScreenLayout.setVisibility(View.GONE);

            // set the toolbar title
            try {
                getSupportActionBar().setTitle(title);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    private class PostPagerAdapter extends PagerAdapter {

        /**
         * Inflater for layouts
         */
        private LayoutInflater inflater;

        /**
         * List of posts to be displayed
         */
        private List<Post> posts;

        /**
         * Class constructor
         *
         * @param posts
         *            list of posts to be displayed
         * @param inflater
         *            layout inflater
         */
        public PostPagerAdapter(List<Post> posts, LayoutInflater inflater) {
            this.posts = posts;
            this.inflater = inflater;
        }

        /**
         * Remove a page for the given position. The adapter is responsible for
         * removing the view from its container, although it only must ensure
         * this is done by the time it returns from
         * {@link #finishUpdate(android.view.ViewGroup)}.
         *
         * @param collection
         *            The containing View from which the page will be removed.
         * @param position
         *            The page position to be removed.
         * @param view
         *            The same object that was returned by
         *            {@link #instantiateItem(android.view.View, int)}.
         */
        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((ScrollView) view);
        }

        /**
         * Called when the a change in the shown pages has been completed. At
         * this point you must ensure that all of the pages have actually been
         * added or removed from the container as appropriate.
         *
         * @param arg0
         *            The containing View which is displaying this adapter's
         *            page views.
         */
        @Override
        public void finishUpdate(ViewGroup arg0) {
        }

        /**
         * Return the number of views available.
         */
        @Override
        public int getCount() {
            return posts.size();
        }

        /**
         * Create the page for the given position. The adapter is responsible
         * for adding the view to the container given here, although it only
         * must ensure this is done by the time it returns from
         * {@link #finishUpdate(android.view.ViewGroup)}.
         *
         * @param collection
         *            The containing View in which the page will be shown.
         * @param position
         *            The page position to be instantiated.
         * @return Returns an Object representing the new page. This does not
         *         need to be a View, but can be some other container of the
         *         page.
         */
        @Override
        public Object instantiateItem(ViewGroup collection, int position) {

            CustomScrollView singlePostView = (CustomScrollView) this.inflater
                    .inflate(R.layout.slider_new, null);

            ImageView image = (ImageView) singlePostView
                    .findViewById(R.id.main_new_image);
            
//            if (posts.get(position).getAttachments().size() > 0) {
//                Attachment attachment = posts.get(position).getAttachments()
//                        .get(0);
//                if (attachment != null) {
//                    AttachmentType attachmentType = attachment
//                            .giveMeBestAttachmentForWidth(mScreenWidth);
//                    if (attachmentType != null) {
//						/*new FetchImageByUrl(image, mScreenWidth, false)
//								.execute(attachmentType.getUrl());*/
//                        ImageLoader.getInstance().displayImage(
//                                attachmentType.getUrl(), image, mImageOptions);
//                    }
//
//                }
//            }

            TextView text = (TextView) singlePostView
                    .findViewById(R.id.cell_slider_item_tv);

            text.setText(posts.get(position).getTitle());

            text.setWidth(mScreenWidth);
            text.setOnClickListener(new PostListener(MainActivity.this,
                    mSliderPosts.get(position), position, new Handler()));
            image.setOnClickListener(new PostListener(MainActivity.this,
                    mSliderPosts.get(position), position, new Handler()));
            collection.addView(singlePostView, 0);

            return singlePostView;

        }

        /**
         * Determines whether a page View is associated with a specific key
         * object as returned by instantiateItem(ViewGroup, int). This method is
         * required for a PagerAdapter to function properly.
         *
         * @param view
         *            Page View to check for association with object
         * @param object
         *            Object to check for association with view
         * @return boolean
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        /**
         * Restore any instance state associated with this adapter and its pages
         * that was previously saved by saveState().
         *
         * @param arg0
         *            State previously saved by a call to saveState()
         * @param arg1
         *            A ClassLoader that should be used to instantiate any
         *            restored objects
         */
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        /**
         * Save any instance state associated with this adapter and its pages
         * that should be restored if the current UI state needs to be
         * reconstructed.
         *
         * @return Saved state for this adapter
         */
        @Override
        public Parcelable saveState() {
            return null;
        }

        /**
         * Called to inform the adapter of which item is currently considered to
         * be the "primary", that is the one show to the user as the current
         * page.
         *
         * @param container
         *            The containing View from which the page will be removed.
         * @param position
         *            The page position that is now the primary.
         * @param object
         *            The same object that was returned by instantiateItem(View,
         *            int).
         */
        @Override
        public void setPrimaryItem(ViewGroup container, int position,
                                   Object object) {
            super.setPrimaryItem(container, position, object);

        }

        /**
         * Called when a change in the shown pages is going to start being made.
         *
         * @param container
         *            The containing View which is displaying this adapter's
         *            page views.
         */
        @Override
        public void startUpdate(ViewGroup container) {
        }

    }
    
    
    public void openSelectedPost(Post mPostToLoad, int mPostPosition) {
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    public void postDownloadCallBack(String url,JSONObject json,AjaxStatus status){
        if(json != null){
            try {

                //successful ajax call, show status code and json content
                Toast.makeText(aq.getContext(), status.getCode() + ":" + json.toString(), Toast.LENGTH_LONG).show();

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
    private void displayArticles(ArrayList<Post> recentPosts) {
        ArrayList<Post> sliderPosts = new ArrayList<Post>();
        ArrayList<Post> listPosts = new ArrayList<Post>();

        for (int i = 0; i < recentPosts.size(); i++) {
            if (recentPosts.get(i).isSliderPost()
                    && sliderPosts.size() < WORDPRES_SLIDER_MAX_POSTS) {
                sliderPosts.add(recentPosts.get(i));
            } else {
                listPosts.add(recentPosts.get(i));
            }
        }

        fixSliderPostsItems(sliderPosts);
        fillSlider(sliderPosts);

        this.mListAdapter = new NewListAdapter(listPosts);
        this.mNewsListView.setAdapter(mListAdapter);
    }

    /**
     * Shows the progress bar for loading the slider
     */
    public void showSlidingLoadingView() {
        if (mPostAdapter != null) {
            this.mPostPager.setVisibility(View.INVISIBLE);
        }
		/*if (mDotsContainer != null) {
			this.mDotsContainer.setVisibility(View.INVISIBLE);
		}*/
        this.mSliderLoading.setVisibility(View.VISIBLE);
    }

    /**
     * Shows the progress bar for loading the news list
     */
    public void showNewsListLoading() {
        this.mListNewsLoading.setVisibility(View.VISIBLE);
    }

    /**
     * Initializes the Activity components
     */
    @SuppressWarnings("deprecation")
    private void initComponents() {

        this.mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        this.mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();

        int sliderMinHeight = (int) ((double) this.mScreenWidth / ImageUtil.aspectRationSlider);
        this.mSliderLoading.setMinimumHeight(sliderMinHeight);
        this.mListNewsLoading.setMinimumHeight(this.mScreenHeight
                - sliderMinHeight);
    }

    /**
     * Fixes the posts in the slider
     *
     * @param sliderPosts
     *            list of posts for the slider
     */
    private void fixSliderPostsItems(ArrayList<Post> sliderPosts) {
        for (int i = 0; i < sliderPosts.size(); i++) {
            for (int j = 0; j < sliderPosts.get(i).getAttachments().size(); j++) {
                Attachment currentAttachment = sliderPosts.get(i)
                        .getAttachments().get(0);
                sliderPosts.get(i).getAttachments()
                        .set(0, sliderPosts.get(i).getAttachments().get(j));
                sliderPosts.get(i).getAttachments().set(j, currentAttachment);
                Toast.makeText(this,"Attachment size at position ="+i+" " + sliderPosts.get(i).getAttachments().size(),Toast.LENGTH_LONG).show();
                System.out.println("Attachment size at position ="+i+" "+sliderPosts.get(i).getAttachments().size());
                break;
            }
        }
    }

    /**
     * Fills the slider data
     *
     * @param posts
     *            posts to fill the slider with
     */
    public void fillSlider(ArrayList<Post> posts) {
        if (posts != null && posts.size() > 0) {
            mPosition = 0;
            setUpPagerAdApter(posts);
            this.mPostPager.removeAllViews();
            this.mSliderPosts = posts;
            mPostAdapter = new PostPagerAdapter(posts, getInflater());
            mPostPager.setAdapter(mPostAdapter);
            mPostPager.setOnClickListener(new PostListener(this, mSliderPosts
                    .get(mPosition), mPosition, new Handler()));
            mPostPager.setVisibility(View.VISIBLE);
            if (posts.size() > 0) {
                this.mPostPager.post(new Runnable() {

                    @Override
                    public void run() {
                        fillDotsContainer(mSliderPosts);
                    }
                });

            }
            mPostPager.requestLayout();

            enableSliding();
        }

    }


    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed. The counterpart to
     * onResume().
     *
     * When activity B is launched in front of activity A, this callback will be
     * invoked on A. B will not be created until A's onPause() returns, so be
     * sure to not do anything lengthy here.
     *
     * This callback is mostly used for saving any persistent state the activity
     * is editing, to present a "edit in place" model to the user and making
     * sure nothing is lost if there are not enough resources to start the new
     * activity without first killing this one. This is also a good place to do
     * things like stop animations and other things that consume a noticeable
     * amount of CPU in order to make the switch to the next activity as fast as
     * possible, or to close resources that are exclusive access such as the
     * camera.
     *
     * In situations where the system needs more memory it may kill paused
     * processes to reclaim resources. Because of this, you should be sure that
     * all of your state is saved by the time you return from this function. In
     * general onSaveInstanceState(Bundle) is used to save per-instance state in
     * the activity and this method is used to store global persistent data (in
     * content providers, files, etc.)
     *
     * After receiving this call you will usually receive a following call to
     * onStop() (after the next activity has been resumed and displayed),
     * however in some cases there will be a direct call back to onResume()
     * without going through the stopped state.
     *
     * Derived classes must call through to the super class's implementation of
     * this method. If they do not, an exception will be thrown.
     */
    @Override
    protected void onResume() {
        super.onResume();
        enableSliding();
    }

    /**
     * Called when you are no longer visible to the user. You will next receive
     * either onRestart(), onDestroy(), or nothing, depending on later user
     * activity.
     *
     * Note that this method may never be called, in low memory situations where
     * the system does not have enough memory to keep your activity's process
     * running after its onPause() method is called.
     *
     * Derived classes must call through to the super class's implementation of
     * this method. If they do not, an exception will be thrown.
     */
    @Override
    protected void onStop() {
        super.onStop();
        stopSliding();
    }

    /**
     * Stops the slider
     */
    private void stopSliding() {
        if (mSliderHandler != null) {
            mSliderHandler.removeCallbacksAndMessages(null);
        }

    }

    /**
     * Enables the slider animation
     */
    private void enableSliding() {
        stopSliding();
        mSliderHandler = new Handler();
        mSliderThread = new Runnable() {
            public void run() {
                if (mPostPager != null) {
                    mPostPager.setCurrentItem(mPosition++, true);
                    mIndicator.setCurrentItem(mPosition++);
                }
                if (mPosition > 3) {
                    mPosition = 0;
                }
                mSliderHandler.postDelayed(this, 3000);
            }
        };
        mSliderThread.run();
    }



    /**
     * Fills the container of the slider that contains the dots
     *
     * @param sliderPosts
     *            the posts for the slider
     */
    private void fillDotsContainer(ArrayList<Post> sliderPosts) {
		/*if (this.mDotsContainer != null) {
			this.mDotsContainer.removeAllViews();
		}
		if (sliderPosts.size() > 1) {
			for (int i = 0; i < sliderPosts.size(); i++) {
				ImageView unselectedDot = (ImageView) getInflater().inflate(
						R.layout.slider_dot, null);

				if (i == mPosition) {
					unselectedDot.setImageResource(R.drawable.red_dot);
				}

				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(5, 0, 5, 0);
				unselectedDot.setLayoutParams(lp);

				this.mDotsContainer.addView(unselectedDot);
			}
		}
		this.mDotsContainer.setVisibility(View.VISIBLE);
		this.mDotsContainer.requestLayout();*/

    }


    /**
     * Sets post for the Activity
     *
     * @param posts
     *            list of posts
     */
    private void setPosts(ArrayList<Post> posts) {
        this.mSliderPosts = posts;
    }

    /**
     * Inflater for the layouts
     *
     * @return inflater
     */
    public LayoutInflater getInflater() {
        return (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    /**
     * Sets the position of the current post
     */
    private void setCurrentPostPosition() {

        mPostPager.setCurrentItem(mPosition);
    }


    /**
     * Initializes the <code>PagerAdapter</code> for the slider
     *
     * @param posts
     */
    private void setUpPagerAdApter(ArrayList<Post> posts) {
        setPosts(posts);

        mPostAdapter = new PostPagerAdapter(posts, getInflater());

        mPostPager = (ViewPager) findViewById(R.id.slider_posts_viewpager);
        double pagerImageHeight = (double) mScreenWidth
                / ImageUtil.aspectRationSlider;
        int height = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, (int) pagerImageHeight,
                getResources().getDisplayMetrics());
        mPostPager.getLayoutParams().height = height;

        mPostPager.setAdapter(mPostAdapter);

        mIndicator = (CirclePageIndicator) findViewById(
                R.id.indicator);
        mIndicator.setRadius(getResources().getDisplayMetrics().density * 6);
        mIndicator.setFillColor(getResources().getColor(
                R.color.act_main_cell_date_text_color));
        mIndicator.setViewPager(mPostPager);

        setCurrentPostPosition();

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            /**
             * This method will be invoked when the current page is scrolled,
             * either as part of a programmatically initiated smooth scroll or a
             * user initiated touch scroll.
             *
             * @param arg0
             *            Position index of the first page currently being
             *            displayed. Page position+1 will be visible if
             *            positionOffset is nonzero.
             * @param arg1
             *            Value from [0, 1) indicating the offset from the page
             *            at position.
             * @param arg2
             *            Value in pixels indicating the offset from position.
             */
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            /**
             * Called when the scroll state changes. Useful for discovering when
             * the user begins dragging, when the pager is automatically
             * settling to the current page, or when it is fully stopped/idle.
             *
             * @param arg0
             *            The new scroll state.
             */
            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            /**
             * This method will be invoked when a new page becomes selected.
             * Animation is not necessarily complete.
             *
             * @param selectedPage
             *            Position index of the new selected page.
             *
             */
            @Override
            public void onPageSelected(int selectedPage) {
                setActiveDotForSlider(selectedPage);
                mPosition = selectedPage;
            }
        });

    }

    /**
     * Marks the dot that corresponds to the visible post in the slider in red
     * colour
     *
     * @param position
     *            the position of the visible post in the slider
     */
    public void setActiveDotForSlider(int position) {
		/*if (mDotsContainer.getChildCount() > 0) {

			for (int i = 0; i < mDotsContainer.getChildCount(); i++) {
				ImageView dotToBeReplaced = (ImageView) mDotsContainer
						.getChildAt(i);

				if (i == position) {
					dotToBeReplaced.setImageResource(R.drawable.red_dot);
				} else {
					dotToBeReplaced.setImageResource(R.drawable.black_dot);
				}

				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.setMargins(5, 0, 5, 0);
				dotToBeReplaced.setLayoutParams(lp);

			}
		}*/
    }
}
