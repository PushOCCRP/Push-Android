package com.push.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.push.app.R;
import com.push.app.model.Post;

public final class DetailPost extends Fragment {
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

    public static DetailPost newInstance(Post postItem) {
        DetailPost fragment = new DetailPost();

        fragment.postItem = postItem;

        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
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


        //Set the values
        mPostTitle.setText(postItem.getTitle());
        mPostBody.loadData(postItem.getContentString(),"text/html", "UTF-8");

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
}
