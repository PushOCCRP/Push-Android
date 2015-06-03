package com.push.app;

import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.push.app.listener.PostListener;
import com.push.app.model.CustomScrollView;
import com.push.app.model.Post;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

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
    private ArrayList<Post> mSliderPosts;

    /**
     * The actionbar
     */
    private Toolbar mToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Display files from Cache

        //initialise the actionBar
        initActionBar();
    }

    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        }

        return super.onOptionsItemSelected(item);
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
         * @return
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
}
