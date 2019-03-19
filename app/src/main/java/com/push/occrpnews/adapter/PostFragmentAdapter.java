package com.push.occrpnews.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.push.occrpnews.fragment.DetailPost;
import com.push.occrpnews.model.Article;

import java.util.ArrayList;
import java.util.HashMap;

public class PostFragmentAdapter extends FragmentPagerAdapter  {
    public static ArrayList<Article> postItems;

    private int mCount;
    private Context mContext;

    public PostFragmentAdapter(Context mContext,FragmentManager fm) {
        super(fm);

        this.mContext = mContext;

        if(postItems != null){
           mCount = postItems.size();
        }

    }

    @Override
    public Fragment getItem(int position) {
        //First let's get what category we're going to be in. For the moment we're going to assume there are always ten stories
        return DetailPost.newInstance(mContext, postItems.get(position));
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return PostFragmentAdapter.postItems.get(position).getHeadline();
    }


    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}