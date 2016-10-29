package com.push.rise.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.androidquery.AQuery;
import com.push.rise.HomeActivity;
import com.push.rise.interfaces.CacheManager.ImageCacheDelegate;
import com.push.rise.model.ImageQueueSingleton;
import com.push.rise.util.CacheManager;
import com.push.rise.util.DateUtil;
import com.push.rise.R;
import com.push.rise.model.Article;
import com.push.rise.util.Language;
import com.push.rise.util.SettingsManager;
import com.push.rise.util.TypefaceManager;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Bryan Lamtoo.
 */
public class PostListAdapter extends ArrayAdapter<Article> implements ImageCacheDelegate {

    private List<Article> itemsList;
    private HashMap<String, ArrayList<Article>> itemsHash;
    private List<String> categories;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    private LayoutInflater inflater;
    private TypefaceManager fontManager;

    static private String mainViewTag = "mainViewTag";
    static private String subViewTag = "subViewTag";
    static private String headerViewTag = "headerViewTag";

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Let's figure out what type of view we'll need, so we don't have to recreate the view object
        // If not necessary. Saves a lot of CPU cycles.

        View cell = new View(getContext());
        Article article;

        // If there are no categories we just go by normal ordering
        if(itemsHash == null) {
            // If it's the top view, initialize a first_item_view
            if(position == 0){
                if(convertView != null && convertView.getTag() != mainViewTag){
                    cell = inflater.inflate(R.layout.first_item_view, null);
                    cell.setTag(mainViewTag);
                } else {
                    cell = convertView;
                }
            } else {
                // Otherwise, stick with a normal list
                if(convertView == null) {
                    cell = inflater.inflate(R.layout.list_news_item, null);
                    cell.setTag(subViewTag);
                } else {
                    cell = convertView;
                }
            }

            article = itemsList.get(position);
        } else {
            // We're dealing with categories here
            // First we need to figure out if we're in a header
            List items = this.items();
            Object item = items.get(position);
            if(item.getClass() == String.class) {
                if(convertView == null || convertView.getTag() != headerViewTag){
                    cell = new TextView(getContext());
                    cell.setPadding(10,10,10,10);
                    cell.setTag(headerViewTag);
                } else {
                    cell = convertView;
                }

                TextView textView = (TextView)cell;
                textView.setText((String)item);

                return textView;
            } else {
                article = (Article)item;

                // We check if the previous element is a string
                Object previousElement = this.items().get(position - 1);
                if(previousElement.getClass() == String.class){
                    if(convertView == null || convertView.getTag() != mainViewTag){
                        cell = inflater.inflate(R.layout.first_item_view, null);
                        cell.setTag(mainViewTag);
                    } else {
                        cell = convertView;
                    }
                } else {
                    // Otherwise, stick with a normal list
                    if(convertView == null || convertView.getTag() != subViewTag) {
                        cell = inflater.inflate(R.layout.list_news_item, null);
                        cell.setTag(subViewTag);
                    } else {
                        cell = convertView;
                    }
                }

            }
        }

        // Set images
        ImageView imageView = (ImageView)cell.findViewById(R.id.post_Image);
        imageView.setVisibility(View.INVISIBLE);

        if(article.getImages().size()>0) {
            CacheManager.getInstance(getContext()).loadBitmap(article.getImages().get(0).get("url"), imageView, this);
        } else {
            imageView.setVisibility(View.INVISIBLE);
        }

        TextView titleView = (TextView)cell.findViewById(R.id.post_Name);

        titleView.setTypeface(fontManager.getRobotoMedium());
        titleView.setText(article.getHeadline());

        try {
            TextView dateView = (TextView)cell.findViewById(R.id.post_Date);
            Date date = sdf.parse(String.valueOf(article.getPublishDate()));
            if (article.getAuthor().length() > 0 && SettingsManager.shouldShowAuthor(getContext())) {
                String separator = Language.bylineSeperator(this.getContext());
                String text = DateUtil.setTime(getContext(), date.getTime(), true) + separator + article.getAuthor();
                dateView.setText(text);
            } else {
                dateView.setText(DateUtil.setTime(getContext(), date.getTime(), true));
            }
        } catch(Exception e) {}

        return cell;
    }

    public PostListAdapter(HomeActivity activity, int layout, ArrayList<Article> articles){
        super(activity, layout, articles);
    }

    public PostListAdapter(Context context, int layout) {
        super(context, layout);
        this.setInitialVariables(context, layout, categories);
    }

    public PostListAdapter(Context context, int layout, HashMap<String, ArrayList<Article>> items, List categories) {
        super(context, layout);
        this.setInitialVariables(context, layout, categories);

        this.itemsHash = items;
    }

    private void setInitialVariables(Context context, int layout, List categories){
        this.categories = categories;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // init Typeface
        fontManager = new TypefaceManager(context.getAssets());
    }


    public Integer totalItems() {
        if(this.itemsHash == null){
            return this.itemsList.size();
        }

        Integer count = this.categories.size();
        for(String key: this.categories){
            count += this.itemsHash.get(key).size();
        }

        return count;
    }

    public int getCount() {
        return this.totalItems();
    }

    // This returns a List of all the elements needed to show
    // If it's not categorized it just returns the list
    // If it's categorized, the list is flattened with headers in between
    private List items() {
        if(this.itemsHash == null){
            return this.itemsList;
        }

        ArrayList items = new ArrayList();

        for(String key: this.categories){
            items.add(key);
            items.addAll(this.itemsHash.get(key));
        }

        return items;
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
