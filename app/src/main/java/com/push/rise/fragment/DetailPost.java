package com.push.rise.fragment;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;

import android.text.style.URLSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.ImageRequest;
import com.androidquery.AQuery;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.push.rise.YouTubeActivity;
import com.push.rise.interfaces.CacheManager.ImageCacheDelegate;
import com.push.rise.model.ImageQueueSingleton;
import com.push.rise.util.AnalyticsManager;
import com.push.rise.util.CacheManager;
import com.push.rise.util.DateUtil;
import com.push.rise.R;
import com.push.rise.model.Article;
import com.push.rise.util.Language;
import com.push.rise.util.SettingsManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import android.text.util.Linkify;
import android.text.method.LinkMovementMethod;

import static com.push.rise.R.color.transparent;

public final class DetailPost extends Fragment implements ObservableScrollViewCallbacks, ImageCacheDelegate, ComponentCallbacks2 {
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

    HashMap<String, Bitmap> downloadedBitmaps = new HashMap<>();

    //private View mImageView;
    private ObservableScrollView mScrollView;
    private int mParallaxImageHeight;

    // Counter for loading inline images
    private int mImagesBeingFetched = 0;

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
            loadContent();
        }

        boolean showAuthor = SettingsManager.shouldShowAuthor(getContext());
        if(showAuthor && postAuthor != null && postAuthor.length() > 0 ) {
            String seperator = Language.bylineSeperator(this.getActivity().getApplicationContext());
            mPostDate.setText(postDate + seperator + postAuthor);
        } else {
            mPostDate.setText(postDate);
        }

        if(showAuthor) {
            mPostAuthor.setText(postAuthor);
        }

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
                CacheManager.getInstance(getContext()).loadBitmap(postImageUrl.get(0), mpostImage, this);
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

    public void loadContent(){

        final DetailPost finalDetailPost = this;
        String body = postBody;

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            body = postBody.replace("<img", "<font size=3><br /></font><img");
        }

        mContent.setText(Html.fromHtml(body, new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                if(downloadedBitmaps.containsKey(source) && downloadedBitmaps.get(source) != null){

                    //BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.fallback);
                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), downloadedBitmaps.get(source));
                    // We need to scale here
                    int width = mContent.getLayoutParams().width - 25;

                    float scale = (float)width / (float)bitmapDrawable.getBitmap().getWidth();
                    bitmapDrawable.setBounds(0, 0, width, (int)((float)bitmapDrawable.getBitmap().getHeight() * scale));

                    return bitmapDrawable;
                } else {
                    downloadedBitmaps.put(source, null);
                    CacheManager.getInstance(getContext()).loadBitmap(source, null, finalDetailPost);

                    Drawable drawable = new ColorDrawable(Color.TRANSPARENT);
                    return drawable;
                }
            }
        }, null));


        mContent.setMovementMethod(LinkMovementMethod.getInstance());
        mContent.setLinkTextColor(getResources().getColor(R.color.material_blue_grey_800));
        // Match any set of characters starting with `http` but not in `<a></a>` tag
        Linkify.addLinks(mContent, Pattern.compile(REGEX), "https:");
        Linkify.addLinks(mContent, Pattern.compile(REGEX), "http:");
        mContent.invalidate();

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

    public void didLoadImage(Bitmap bitmap, ImageView imageView, String url) {
        if(url == postImageUrl.get(0)) {
            mpostImage.setVisibility(View.VISIBLE);
            RelativeLayout parentLayout = (RelativeLayout) mpostImage.getParent();
            RelativeLayout parentParentLayout = (RelativeLayout) parentLayout.getParent();
            View anchor = parentParentLayout.findViewById(R.id.anchor);

            if(getActivity() == null){
                return;
            }

            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            int imageWidth = dpToPx(bitmap.getWidth());
            int imageHeight = dpToPx(bitmap.getHeight());


            // Here we do the ratio math
            // Get the new width of the image when in the view
            float ratio = (float) size.x / (float) imageWidth;
            int newHeight = Math.round(imageHeight * ratio);

            if (newHeight > size.x) {
                newHeight = size.x;
            }

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size.x, newHeight);
            parentLayout.setLayoutParams(layoutParams);
            anchor.setLayoutParams(layoutParams);
            mpostImage.setImageBitmap(bitmap);
        } else {
            if (downloadedBitmaps.containsKey(url)) {
                downloadedBitmaps.put(url, bitmap);
            }

            Boolean full = true;
            for (String key : downloadedBitmaps.keySet()) {
                if (downloadedBitmaps.get(key) == null) {
                    full = false;
                    break;
                }
            }

            if (full) {
               loadContent();
            }
        }
    }

    public void errorLoadingImage(ImageView imageView, String url) {
        mpostImage.setVisibility(View.GONE);
        mProgress.setVisibility(View.GONE);
        mAnchor.setVisibility(View.GONE);
        mpostImage.setImageResource(R.drawable.fallback);
    }

    public void onTrimMemory (int level) {
        downloadedBitmaps.clear();
    }


}

