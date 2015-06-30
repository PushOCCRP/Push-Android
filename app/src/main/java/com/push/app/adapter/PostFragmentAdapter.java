package com.push.app.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.push.app.fragment.DetailPost;
import com.push.app.model.Post;

import java.util.ArrayList;

public class PostFragmentAdapter extends FragmentPagerAdapter  {
    public static ArrayList<Post> postItems;

    private int mCount = postItems.size();
    private Context mContext;

    public PostFragmentAdapter(Context mContext,FragmentManager fm) {
        super(fm);

        this.mContext = mContext;
    }

    @Override
    public Fragment getItem(int position) {
        return DetailPost.newInstance(mContext, postItems.get(position % postItems.size()));
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
      return PostFragmentAdapter.postItems.get(position % postItems.size()).getTitle();
    }


    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }
}