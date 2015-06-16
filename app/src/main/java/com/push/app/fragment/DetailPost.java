package com.push.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.nineoldandroids.view.ViewHelper;
import com.push.app.ObservableList.ObservableScrollView;
import com.push.app.ObservableList.ScrollState;
import com.push.app.ObservableList.ScrollUtils;
import com.push.app.R;
import com.push.app.model.Post;
import com.push.app.ObservableList.ObservableScrollViewCallbacks;

public final class DetailPost extends Fragment implements ObservableScrollViewCallbacks{
    private static final String KEY_CONTENT = "TestFragment:Content";
    private static final String KEY_TITLE = "TestFragment:Title";
    private static final String KEY_DESCRIPTION = "TestFragment:Desc";
    private static final String KEY_DATE = "TestFragment:Date";

    private Post postItem;
    private String postBody;
    private String postTitle;
    private long postDate;
    private String postDescription;
    private View rootView;
    private TextView mPostTitle;
    private TextView mPostAuthor;
    private TextView mPostDescription;
    private WebView mPostBody;
    private ImageView mpostImage;
    private AQuery aq;
    private Context mContext;

    private View mImageView;
    private View mToolbarView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    public static DetailPost newInstance(Context mContext,Post postItem) {
        DetailPost fragment = new DetailPost();
        fragment.mContext = mContext;
        fragment.postItem = postItem;
        fragment.aq = new AQuery(mContext);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
            postBody = savedInstanceState.getString(KEY_CONTENT);
            postTitle = savedInstanceState.getString(KEY_TITLE);
            postDate = savedInstanceState.getLong(KEY_DATE);
            postDescription = savedInstanceState.getString(KEY_DESCRIPTION);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Initialise views
        rootView = inflater.inflate(R.layout.fragment_detail_post, container,false);
        mPostAuthor = (TextView) rootView.findViewById(R.id.postAuthor);
        mPostBody = (WebView)rootView.findViewById(R.id.postBody);

        mPostBody.getSettings().setJavaScriptEnabled(true);
        mPostTitle = (TextView)rootView.findViewById(R.id.postHeadline);
        mpostImage = (ImageView)rootView.findViewById(R.id.postImage);


        mImageView = rootView.findViewById(R.id.postImage);
        mToolbarView = rootView.findViewById(R.id.toolbar);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, getResources().getColor(R.color.colorPrimary)));

        mScrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);


        //Set the values
        mPostTitle.setText(postItem.getTitle());
        mPostBody.loadData(postItem.getContentString(), "text/html", "UTF-8");

//        aq.id(mpostImage).image(imageUrl, true, true, getResources().getDimension(R.dimen), 0);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, postBody);
        outState.putString(KEY_DESCRIPTION, postDescription);
        outState.putString(KEY_TITLE, postTitle);
        outState.putLong(KEY_DATE, postDate);

    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        int baseColor = getResources().getColor(R.color.colorPrimary);
        float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
        mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }
}
