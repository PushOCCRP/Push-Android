package com.push.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.text.Html;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.push.app.R;
import com.push.app.model.Article;

import com.push.app.util.DateUtil;
import com.push.app.util.ImageGetter;

import java.util.ArrayList;
import java.util.Date;

public final class DetailPost extends Fragment implements ObservableScrollViewCallbacks {
    private static final String KEY_CONTENT = "TestFragment:Content";
    private static final String KEY_TITLE = "TestFragment:Title";
    private static final String KEY_URL = "TestFragment:Desc";
    private static final String KEY_DATE = "TestFragment:Date";
    private static final String KEY_AUTHOR = "TestFragment:Author";

    private String postBody;
    private String postTitle;
    private String postDate;
    private String postAuthor;
    private View rootView;
    private View mAnchor;
    private TextView mPostTitle;
    private TextView mPostAuthor;
    private TextView mPostDate;
    private TextView mPostDescription;
    private ProgressBar mProgress;
    private WebView mPostBody;
    private ImageView mpostImage;
    private AQuery aq;
    private Context mContext;
    private WebSettings webSettings;
    private TextView mContent;
    ArrayList<String> postImageUrl = new ArrayList<>();

    //private View mImageView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    public static DetailPost newInstance(Context mContext, Article postItem) {
        DetailPost fragment = new DetailPost();
        fragment.mContext = mContext;
        fragment.postBody = postItem.getBody();
        fragment.postTitle = postItem.getHeadline();
        fragment.postImageUrl.addAll(postItem.getImageUrls());
        fragment.postAuthor = postItem.getAuthor();

        try {
            Date date = DateUtil.postsDatePublishedFormatter.parse(String.valueOf(postItem.getPublishDate()));
            fragment.postDate = DateUtil.setTime(date.getTime());
        }catch (Exception e){

        }
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            aq = new AQuery(mContext);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
//            onScrollChanged(mScrollView.getCurrentScrollY(), false, false);
            postBody = savedInstanceState.getString(KEY_CONTENT);
            postTitle = savedInstanceState.getString(KEY_TITLE);
            postDate = savedInstanceState.getString(KEY_DATE);
            postImageUrl = savedInstanceState.getStringArrayList(KEY_URL);
            postAuthor = savedInstanceState.getString(KEY_AUTHOR);


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Initialise views
        rootView = inflater.inflate(R.layout.fragment_detail_post, container,false);
        mAnchor = (View) rootView.findViewById(R.id.anchor);
        mPostAuthor = (TextView) rootView.findViewById(R.id.postAuthor);
        mContent = (TextView) rootView.findViewById(R.id.postContent);
        mPostDate = (TextView)rootView.findViewById(R.id.post_Date);
        mProgress = (ProgressBar)rootView.findViewById(R.id.image_progress);
        mPostTitle = (TextView)rootView.findViewById(R.id.postHeadline);
        mpostImage = (ImageView)rootView.findViewById(R.id.postImage);



        mScrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        //Set the values
        mPostTitle.setText(postTitle);
        mpostImage.setVisibility(View.GONE);
        if(postBody != null && postBody.length() > 0){
            mContent.setText(Html.fromHtml(postBody));
        }

        if(postAuthor.length() > 0) {
            mPostDate.setText(postDate + " by " + postAuthor);
        } else {
            mPostDate.setText(postDate);
        }

        mPostAuthor.setText(postAuthor);


            if (postImageUrl != null) {
                if (postImageUrl.size() > 1) {
                    aq.id(mpostImage).progress(R.id.image_progress).image(postImageUrl.get(1), true, true, 0, R.drawable.fallback, null, AQuery.FADE_IN);
                } else if(postImageUrl.size() == 1) {
                    aq.id(mpostImage).progress(R.id.image_progress).image(postImageUrl.get(0), true, true, 0, R.drawable.fallback, null, AQuery.FADE_IN);
                } else {
                    //If there are no images hide the anchor and loader
                    mProgress.setVisibility(View.GONE);
                    mAnchor.setVisibility(View.GONE);
                }
            }

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, postBody);
        outState.putStringArrayList(KEY_URL, postImageUrl);
        outState.putString(KEY_TITLE, postTitle);
        outState.putString(KEY_DATE, postDate);
        outState.putString(KEY_AUTHOR,postAuthor);

    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        //ViewHelper.setTranslationY(mpostImage, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }
}
