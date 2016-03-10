package com.push.meydan.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.text.Html;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.push.meydan.YouTubeActivity;
import com.push.meydan.util.AnalyticsManager;
import com.push.meydan.util.DateUtil;
import com.push.meydan.R;
import com.push.meydan.model.Article;
import com.push.meydan.util.Language;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.text.util.Linkify;
import android.text.method.LinkMovementMethod;

public final class DetailPost extends Fragment implements ObservableScrollViewCallbacks {
    private static final String KEY_CONTENT = "TestFragment:Content";
    private static final String KEY_TITLE = "TestFragment:Title";
    private static final String KEY_URL = "TestFragment:Desc";
    private static final String KEY_DATE = "TestFragment:Date";
    private static final String KEY_AUTHOR = "TestFragment:Author";
    static final String REGEX = "(?!<a[^>]*?>)(http[^\\s]+)(?![^<]*?</a>)";

    private String postBody;
    private String postTitle;
    private String postDate;
    private String postAuthor;
    private String postPhotoCaption;

    private View rootView;
    private View mAnchor;
    private TextView mPostTitle;
    private TextView mPostAuthor;
    private TextView mPostDate;
    private TextView mPostDescription;
    private ProgressBar mProgress;
    private WebView mPostBody;
    private ImageView mpostImage;
    private TextView mPhotoCaption;
    private ImageButton mVideoPlayButton;

    public Article postItem;

    private AQuery aq;
    private Context mContext;
    private WebSettings webSettings;
    private TextView mContent;
    ArrayList<String> postImageUrl = new ArrayList<>();
    ArrayList<String> postVideoIds = new ArrayList<>();

    //private View mImageView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    public static DetailPost newInstance(Context mContext, Article postItem) {
        DetailPost fragment = new DetailPost();
        fragment.postItem = postItem;
        fragment.mContext = mContext;
        fragment.postBody = postItem.getBody();
        fragment.postTitle = postItem.getHeadline();

        List<HashMap<String, String>> images = postItem.getImages();
        if(images.size() > 0) {
            fragment.postImageUrl.add(images.get(0).get("url"));
        }

        List<HashMap<String, String>> videos = postItem.getVideos();
        if(videos.size() > 0){
            fragment.postVideoIds.add(videos.get(0).get("youtube_id"));
        }

        fragment.postAuthor = postItem.getAuthor();

        List<String> captions = postItem.getCaptions();
        if(captions.size() > 0) {
            fragment.postPhotoCaption = captions.get(0);
        }

        try {
            Date date = DateUtil.postsDatePublishedFormatter.parse(String.valueOf(postItem.getPublishDate()));
            fragment.postDate = DateUtil.setTime(mContext, date.getTime(), false);
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
        mPhotoCaption = (TextView)rootView.findViewById(R.id.photoCaption);
        mVideoPlayButton = (ImageButton)rootView.findViewById(R.id.videoButton);

        mScrollView = (ObservableScrollView) rootView.findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);

        mParallaxImageHeight = getResources().getDimensionPixelSize(R.dimen.parallax_image_height);

        //Set the values
        mPostTitle.setText(postTitle);
        mpostImage.setVisibility(View.GONE);
        if(postBody != null && postBody.length() > 0 ){
            mContent.setMovementMethod(LinkMovementMethod.getInstance());
            mContent.setText(Html.fromHtml(postBody));
            // Match any set of characters starting with `http` but not in `<a></a>` tag
            Linkify.addLinks(mContent, Pattern.compile(REGEX), "https://");
            Linkify.addLinks(mContent, Pattern.compile(REGEX), "http://");
        }

        if(postAuthor != null && postAuthor.length() > 0) {
            String seperator = Language.bylineSeperator(this.getActivity().getApplicationContext());
            mPostDate.setText(postDate + seperator + postAuthor);
        } else {
            mPostDate.setText(postDate);
        }

        mPostAuthor.setText(postAuthor);




        if(postPhotoCaption != null){
            mPhotoCaption.setTextColor(Color.LTGRAY);
            mPhotoCaption.setText(postPhotoCaption);
        } else {
            mPhotoCaption.setVisibility(View.GONE);
        }

        if(postVideoIds == null || postVideoIds.size() < 1){
            mVideoPlayButton.setVisibility(View.GONE);
        } else {
            mVideoPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openVideo();
                    view.setVisibility(View.VISIBLE);
                }
            });
        }

        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        if (postImageUrl != null) {
            if (postImageUrl.size() > 0) {

                //aq.id(mpostImage).progress(R.id.image_progress).image(postImageUrl.get(0), true, true, 0, R.drawable.fallback, null, AQuery.FADE_IN);
                aq.id(mpostImage).progress(R.id.image_progress).image(postImageUrl.get(0), true, true, 0, R.drawable.fallback, new BitmapAjaxCallback() {

                            @Override
                            public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                                iv.setVisibility(View.VISIBLE);
                                RelativeLayout parentLayout = (RelativeLayout)iv.getParent();
                                RelativeLayout parentParentLayout = (RelativeLayout)parentLayout.getParent();
                                View anchor = (View) parentParentLayout.findViewById(R.id.anchor);

                                WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
                                Display display = wm.getDefaultDisplay();
                                Point size = new Point();
                                display.getSize(size);

                                int imageWidth = dpToPx(bm.getWidth());
                                int imageHeight = dpToPx(bm.getHeight());


                                // Here we do the ratio math
                                // Get the new width of the image when in the view
                                float ratio = (float)size.x / (float)imageWidth;
                                int newHeight = Math.round(imageHeight * ratio);

                                if(newHeight > size.x){
                                    newHeight = size.x;
                                }

                                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size.x, newHeight);
                                parentLayout.setLayoutParams(layoutParams);
                                anchor.setLayoutParams(layoutParams);
                                iv.setImageBitmap(bm);
                            }
                        }
                );

            } else {
                //If there are no images hide the anchor and loader
                mProgress.setVisibility(View.GONE);
                mAnchor.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            AnalyticsManager.startTimerForContentView(this, this.postTitle);
        } else {
            AnalyticsManager.endTimerForContentView(this, this.postTitle);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_CONTENT, postBody);
        outState.putStringArrayList(KEY_URL, postImageUrl);
        outState.putString(KEY_TITLE, postTitle);
        outState.putString(KEY_DATE, postDate);
        outState.putString(KEY_AUTHOR, postAuthor);
    }

    public void openVideo(){
        if(postVideoIds.size() < 1){
            return;
        }

        Intent i = new Intent(getActivity(), YouTubeActivity.class);
        i.putExtra("videoId", postVideoIds.get(0));
        startActivity(i);
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


    private int dpToPx(int dp) {
        float density = getActivity().getResources().getDisplayMetrics().density;
        float pixel = dp * density;
        return Math.round(pixel);
    }
}

