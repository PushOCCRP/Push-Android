package com.pushapp.press;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.pushapp.press.adapter.PostFragmentAdapter;
import com.pushapp.press.fragment.DetailPost;
import com.pushapp.press.model.Article;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;


public class DetailPostActivity extends AppCompatActivity {
    /**
     * The actionbar
     */
    private Toolbar mToolbar;
    ViewPager mPager;
    PageIndicator mIndicator;
    private PostFragmentAdapter mAdapter;
    private int postPosition;
    private String mPostTitle;
    private String desc;

    @Override

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setContentView(com.pushapp.press.R.layout.activity_detail_post);

        Bundle m = getIntent().getExtras();
        postPosition = m.getInt("postPosition");
        mPostTitle = m.getString("postTitle");
        desc = m.getString("description");

        //initialise the actionBar
        initActionBar();

        mAdapter = new PostFragmentAdapter(this, getSupportFragmentManager());

        mPager = (ViewPager)findViewById(com.pushapp.press.R.id.pager);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(2);
        int pageCount = mPager.getAdapter().getCount();
        mIndicator = (CirclePageIndicator) findViewById(com.pushapp.press.R.id.indicator);
        mIndicator.setViewPager(mPager, postPosition);
        if(pageCount == 1) {
            View mIndicatorView = (View)mIndicator;
            mIndicatorView.setVisibility(View.GONE);
        }


    }


    private void initActionBar() {
        mToolbar = (Toolbar) findViewById(com.pushapp.press.R.id.toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(R.color.primary_dark_material_dark));
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setLogo(com.pushapp.press.R.mipmap.logo);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.pushapp.press.R.menu.menu_detail_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id== com.pushapp.press.R.id.action_share){
            doShare();
        }else if(id==android.R.id.home){
            if(!isTaskRoot()){
                onBackPressed();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void doShare() {

        DetailPost post = (DetailPost)mAdapter.getItem(postPosition);
        Article article = post.postItem;

        final Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, mPostTitle);
        share.putExtra(Intent.EXTRA_TEXT, article.getUrl());

        try {
            startActivity(Intent.createChooser(share, "Select an action"));
        } catch (android.content.ActivityNotFoundException ex) {
            // (handle error)
        }
    }
}
