package com.push.cins.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.androidquery.AQuery;
import com.push.cins.interfaces.CacheManager.ImageCacheDelegate;
import com.push.cins.model.ImageQueueSingleton;
import com.push.cins.util.CacheManager;
import com.push.cins.util.DateUtil;
import com.push.cins.R;
import com.push.cins.model.Article;
import com.push.cins.util.Language;
import com.push.cins.util.SettingsManager;
import com.push.cins.util.TypefaceManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Bryan Lamtoo.
 */
public class PostListAdapter extends ArrayAdapter<Article> implements ImageCacheDelegate {

    private List<Article> items;
    private AQuery aq;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
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
        final ViewHolder holder;

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

        holder.postImage.setVisibility(View.INVISIBLE);

        holder.postTitle.setTypeface(fontManager.getRobotoMedium());
        holder.postTitle.setText(items.get(position).getHeadline());

        if(items.get(position).getImages().size()>0) {
            CacheManager.getInstance(getContext()).loadBitmap(items.get(position).getImages().get(0).get("url"), holder.postImage, this);
        } else {
            holder.postImage.setVisibility(View.INVISIBLE);
        }
        try {
            Date date = sdf.parse(String.valueOf(items.get(position).getPublishDate()));
            if(items.get(position).getAuthor().length() > 0 && SettingsManager.shouldShowAuthor(getContext())) {
                String seperator = Language.bylineSeperator(this.getContext());
                String text = DateUtil.setTime(getContext(), date.getTime(), true) + seperator + items.get(position).getAuthor();
                holder.postDate.setText(text);
            } else {
                holder.postDate.setText(DateUtil.setTime(getContext(), date.getTime(), true));
            }

        }catch (Exception e){

        }

        return view;
    }



    public PostListAdapter(Context context, int layout,List<Article> items) {
        super(context, layout,items);
        this.items = items;
        this.layout = layout;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        aq = new AQuery(context);
        // init Typeface
        fontManager = new TypefaceManager(context.getAssets());
    }


    // ImageCacheDelegate

    public void didLoadImage(Bitmap bitmap, ImageView imageView, String url) {
        imageView.setImageBitmap(bitmap);
        imageView.setVisibility(View.VISIBLE);
    }

    public void errorLoadingImage(ImageView imageView, String url) {
        imageView.setImageResource(R.drawable.fallback);
        imageView.setVisibility(View.VISIBLE);
    }


}
