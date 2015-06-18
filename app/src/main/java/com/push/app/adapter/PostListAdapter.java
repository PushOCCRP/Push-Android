package com.push.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.push.app.R;
import com.push.app.model.AttachmentType;
import com.push.app.model.Post;
import com.push.app.util.ImageUtil;
import com.push.app.util.TypefaceManager;

import java.util.ArrayList;

/**
 * @author Bryan Lamtoo.
 */
public class PostListAdapter extends ArrayAdapter<Post> {

    private ArrayList<Post> items;
    private AQuery aq;

    String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";

    private int layout;
    private LayoutInflater inflater;
    private TypefaceManager fontManager;
    private Context mContext;
    static class ViewHolder{
        TextView postTitle;
        ImageView postImage;
        TextView postDate;
        RelativeLayout listNewsView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder = null;

        if(convertView == null){

            //initialise the view
            view = inflater.inflate(layout, null);
            holder = new ViewHolder();
            holder.postDate =(TextView) view.findViewById(R.id.post_Date);
            holder.postImage = (ImageView)view.findViewById(R.id.post_Image);
            holder.postTitle = (TextView)view.findViewById(R.id.post_Name);


            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        holder.postTitle.setTypeface(fontManager.getRobotoMedium());
        holder.postTitle.setText(items.get(position).getTitle());

        if(position%2 ==0) {
            holder.postImage.setVisibility(View.GONE);
            aq.id(holder.postImage).image(imageUrl, true, true, 90, 0);
             }else{
            holder.postImage.setVisibility(View.GONE);
        }

        if (items.get(position).getAttachments().size() > 0) {

            AttachmentType currentAttachment = items.get(position)
                    .getAttachments().get(0).getMediumSize();
            if (currentAttachment != null) {
					/*new FetchImageByUrl(image, mScreenWidth, true)
							.execute(currentAttachment.getUrl());*/

                holder.postImage.getLayoutParams().width = ImageUtil.widthForThumbs;
                holder.postImage.getLayoutParams().height = (int) (ImageUtil.widthForThumbs / ImageUtil.aspectRationThumb);

//                ImageLoader.getInstance().displayImage(
//                        currentAttachment.getUrl(), image, mImageOptions);

//                String imageUrl = "http://farm6.static.flickr.com/5035/5802797131_a729dac808_b.jpg";
                Toast.makeText(aq.getContext(), "url:" + currentAttachment.getUrl(), Toast.LENGTH_LONG).show();
                aq.id(holder.postImage).image(currentAttachment.getUrl(), true, true, 200, 0);
            }

        }

        return view;
    }



    public PostListAdapter(Context context, int layout,ArrayList<Post> items) {
        super(context, layout,items);
        this.items = items;
        this.layout = layout;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        aq = new AQuery(context);
        // init Typeface
        fontManager = new TypefaceManager(context.getAssets());
    }


}
