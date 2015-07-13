package com.push.app.fragment;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.nineoldandroids.view.ViewHelper;
import com.push.app.ObservableList.ObservableScrollView;
import com.push.app.ObservableList.ObservableScrollViewCallbacks;
import com.push.app.ObservableList.ScrollState;
import com.push.app.ObservableList.ScrollUtils;
import com.push.app.R;
import com.push.app.model.AttachmentType;
import com.push.app.model.Post;

import com.push.app.ObservableList.ObservableScrollViewCallbacks;
import com.push.app.util.ImageGetter;
import com.push.app.util.ImageUtil;

public final class DetailPost extends Fragment implements ObservableScrollViewCallbacks{
    private static final String KEY_CONTENT = "TestFragment:Content";
    private static final String KEY_TITLE = "TestFragment:Title";
    private static final String KEY_URL = "TestFragment:Desc";
    private static final String KEY_DATE = "TestFragment:Date";

    private Post postItem;
    private String postBody;
    private String postTitle;
    private long postDate;
    private String postImageUrl = null;
    private View rootView;
    private TextView mPostTitle;
    private TextView mPostAuthor;
    private TextView mPostDescription;
    private WebView mPostBody;
    private ImageView mpostImage;
    private AQuery aq;
    private Context mContext;
    private WebSettings webSettings;
    private TextView mContent;

    //    private View mImageView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    public static DetailPost newInstance(Context mContext, Post postItem) {
        DetailPost fragment = new DetailPost();
        fragment.mContext = mContext;
        fragment.postItem = postItem;
        fragment.postBody = postItem.getContentString();
        fragment.postTitle = postItem.getTitle();
//        fragment.postDate = postItem.getPublishedDate();

        //TODO Uncomment this for production
      /*  if (postItem.getAttachments().size() > 0) {

            AttachmentType currentAttachment = postItem
                    .getAttachments().get(0).getMediumSize();
            if (currentAttachment != null) {
                fragment.postImageUrl = currentAttachment.getUrl();
            }

        }*/
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
            postDate = savedInstanceState.getLong(KEY_DATE);
            postImageUrl = savedInstanceState.getString(KEY_URL);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Initialise views
        rootView = inflater.inflate(R.layout.fragment_detail_post, container,false);
        mPostAuthor = (TextView) rootView.findViewById(R.id.postAuthor);
        mContent = (TextView) rootView.findViewById(R.id.postContent);

        mPostTitle = (TextView)rootView.findViewById(R.id.postHeadline);
        mpostImage = (ImageView)rootView.findViewById(R.id.postImage);



        mScrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);



        //Set the values
        mPostTitle.setText(postTitle);
        mpostImage.setVisibility(View.VISIBLE); //TODO change visibility to GONE
        mContent.setText(Html.fromHtml(postBody, new ImageGetter(mContent, getActivity()), null));

            if (postImageUrl != null)
                aq.id(mpostImage).image(postImageUrl);



        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, postBody);
        outState.putString(KEY_URL, postImageUrl);
        outState.putString(KEY_TITLE, postTitle);
        outState.putLong(KEY_DATE, postDate);

    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        ViewHelper.setTranslationY(mpostImage, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }
}
