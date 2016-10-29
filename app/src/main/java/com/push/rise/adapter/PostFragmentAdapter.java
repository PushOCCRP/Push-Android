package com.push.rise.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.push.rise.fragment.DetailPost;
import com.push.rise.model.Article;

import java.util.ArrayList;
import java.util.HashMap;

public class PostFragmentAdapter extends FragmentPagerAdapter  {
    public static HashMap<String, ArrayList<Article>> postItems;
    public static ArrayList<String> categories;

    private int mCount;
    private Context mContext;

    public PostFragmentAdapter(Context mContext,FragmentManager fm) {
        super(fm);

        this.mContext = mContext;

        if(postItems != null){
            Integer count = 0;
            for(String key: postItems.keySet()){
                count++;
                count += postItems.get(key).size();
            }
           mCount = count;
        }

    }

    @Override
    public Fragment getItem(int position) {
        //First let's get what category we're going to be in. For the moment we're going to assume there are always ten stories
        Integer categoryIndex = position % 10;

        return DetailPost.newInstance(mContext, postItems.get(position / postItems.size()).get(position % postItems.size()));
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return PostFragmentAdapter.postItems.get(position / postItems.size()).get(position % postItems.size()).getHeadline();
    }


    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}