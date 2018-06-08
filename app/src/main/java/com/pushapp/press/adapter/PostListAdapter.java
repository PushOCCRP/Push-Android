package com.pushapp.press.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.pushapp.press.HomeActivity;
import com.pushapp.press.interfaces.CacheManager.ImageCacheDelegate;
import com.pushapp.press.util.DateUtil;
import com.pushapp.press.R;
import com.pushapp.press.model.Article;
import com.pushapp.press.util.Language;
import com.pushapp.press.util.SettingsManager;
import com.pushapp.press.util.TypefaceManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * @author Bryan Lamtoo.
 * Heavily edited by Christopher Guess
 */
public class PostListAdapter extends ArrayAdapter<Article> implements ImageCacheDelegate {
    // Android's "findItemByID request is really really expensive apparently.
    // This is the standard "ViewHolder" pattern which handles the requests at the time
    // The object is created.
    // All references to setting the fields should go through here
    private static class ViewHolderItem {
        ImageView imageView;
        TextView headline;
        String tag;
        TextView dateView;
    }
    private List<Article> itemsList;
    private HashMap<String, ArrayList<Article>> itemsHash;
    private List<String> categories;
    private Integer articlesPerCategory;

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
        ViewHolderItem viewHolder;

        String tag = null;
        if(cell != null){
            ViewHolderItem viewHolderItem = (ViewHolderItem)cell.getTag();
            if(viewHolderItem != null) {
                tag = viewHolderItem.tag;
            } else {
                viewHolderItem = new ViewHolderItem();
                cell.setTag(viewHolderItem);
            }
        }

        // If there are no categories we just go by normal ordering
        if(itemsHash == null) {

            // If it's the top view, initialize a first_item_view
            if(position == 0){
                if(convertView == null || tag != mainViewTag){
                    cell = inflater.inflate(R.layout.first_item_view, null);
                    cell.setTag(mainViewTag);
                } else {
                    cell = convertView;
                }
            } else {
                // Otherwise, stick with a normal list
                if(convertView == null || tag != subViewTag) {
                    cell = inflater.inflate(R.layout.list_news_item, null);
                    cell.setTag(subViewTag);
                } else {
                    cell = convertView;
                }
            }

            try {
                article = itemsList.get(position);
            } catch (Exception e) {
                Gson gson = new Gson();
                article = gson.fromJson(gson.toJsonTree(itemsList.get(position)), Article.class);
            }
        } else {
            // We're dealing with categories here
            // First we need to figure out if we're in a header
            List items = this.items();
            Object item = items.get(position);
            if(item.getClass() == String.class) {
                if(convertView == null || tag != headerViewTag){
                    cell = new TextView(getContext());
                    cell.setPadding(30,30,10,20);
                    cell.setTag(headerViewTag);
                } else {
                    cell = convertView;
                }

                TextView textView = (TextView)cell;
                String category_name = (String)item;
                category_name = category_name.replace("_", " ");
                category_name = category_name.substring(0,1).toUpperCase() + category_name.substring(1);

                textView.setText(category_name);
                textView.setTypeface(fontManager.getAmericanTypewriter());

                return textView;
            } else {
                article = (Article)item;

                // We check if the previous element is a string
                Object previousElement = this.items().get(position - 1);
                if(previousElement.getClass() == String.class){
                    if(convertView == null || tag != mainViewTag){
                        cell = inflater.inflate(R.layout.first_item_view, null);
                        cell.setTag(mainViewTag);
                    } else {
                        cell = convertView;
                    }
                } else {
                    // Otherwise, stick with a normal list
                    if(convertView == null || tag != subViewTag) {
                        cell = inflater.inflate(R.layout.list_news_item, null);
                        cell.setTag(subViewTag);
                    } else {
                        cell = convertView;
                    }
                }

            }
        }

        if(cell.getTag().getClass() != ViewHolderItem.class) {
            viewHolder = new ViewHolderItem();
            viewHolder.imageView = (ImageView) cell.findViewById(R.id.post_Image);
            viewHolder.tag = (String) cell.getTag();
            viewHolder.headline = (TextView) cell.findViewById(R.id.post_Name);
            viewHolder.dateView = (TextView) cell.findViewById(R.id.post_Date);

            cell.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem)cell.getTag();
        }

        // Set images
        ViewHolderItem viewHolderItem = (ViewHolderItem)cell.getTag();
        ImageView imageView = viewHolderItem.imageView;

        try{
            imageView.setVisibility(View.INVISIBLE);
            if(article.getHeaderImage() != null){
                GlideApp.with(cell).load(article.getHeaderImage().url)
                        .placeholder(new ColorDrawable(Color.BLACK))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                imageView.setVisibility(View.GONE);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                imageView.setVisibility(View.VISIBLE);
                                return false;
                            }

                        })
                        .into(imageView);
            } else if(article.getImages().size() > 0) {
                GlideApp.with(cell).load(article.getImages().get(0).url)
                        .placeholder(new ColorDrawable(Color.BLACK))
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                imageView.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                imageView.setVisibility(View.VISIBLE);
                                return false;
                            }

                        })
                        .into(imageView);
            } else {
                imageView.setVisibility(View.GONE);
            }
        } catch(Exception e){
            Log.w("error", "Exception");

        }

        if(viewHolderItem.tag.equals(mainViewTag)){
            TextView description = (TextView)cell.findViewById(R.id.postDescription);
            if(article.getDescription() != null) {
                description.setText(article.getDescription());
            }
        }

        TextView titleView = viewHolderItem.headline;

        try{
            titleView.setTypeface(fontManager.getRobotoMedium());
            titleView.setText(article.getHeadline());
            TextView dateView = viewHolderItem.dateView;
            Date date = sdf.parse(String.valueOf(article.getPublishDate()));
            if (article.getAuthor() != null && article.getAuthor().length() > 0 && SettingsManager.shouldShowAuthor(getContext())) {
                String separator = Language.bylineSeperator(this.getContext());
                String text = DateUtil.setTime(getContext(), date.getTime(), true) + separator + article.getAuthor();
                dateView.setText(text);
            } else {
                dateView.setText(DateUtil.setTime(getContext(), date.getTime(), true));
            }
        } catch(Exception e) {
            return new View(getContext());
        }

        return cell;
    }

    public PostListAdapter(HomeActivity activity, int layout, ArrayList<Article> articles){
        super(activity, layout, articles);
        this.setInitialVariables(getContext(), layout, null, 0);

        this.itemsList = articles;
    }

    public PostListAdapter(Context context, int layout) {
        super(context, layout);
        this.setInitialVariables(context, layout, categories, 0);
    }

    public PostListAdapter(Context context, int layout, ArrayList<Article> items) {
        super(context, layout);
        this.setInitialVariables(context, layout, null, 0);

        this.itemsList = items;
    }

    public PostListAdapter(Context context, int layout, HashMap<String, ArrayList<Article>> items, List categories, Integer articlesPerCategory) {
        super(context, layout);
        this.setInitialVariables(context, layout, categories, articlesPerCategory);

        this.itemsHash = items;
    }

    // If articlesPerCategory is less than 1, just show them all.
    private void setInitialVariables(Context context, int layout, List categories, Integer articlesPerCategory){
        this.categories = categories;
        if(articlesPerCategory < 1){
            articlesPerCategory = Integer.MAX_VALUE;
        }

        this.articlesPerCategory = articlesPerCategory;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // init Typeface
        fontManager = new TypefaceManager(context.getAssets());
    }


    private Integer totalItems() {
        Integer count = 0;

        if(this.itemsHash == null){
            if(this.itemsList != null){
                count = this.itemsList.size();
            }
        } else {
            count = this.categories.size();
            for (String key : this.categories) {
                Integer size = this.itemsHash.get(key).size();
                if (size > articlesPerCategory) {
                    count += articlesPerCategory;
                } else {
                    count += size;
                }
            }
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

            if(this.itemsHash.get(key).size() > articlesPerCategory){
                items.addAll(this.itemsHash.get(key).subList(0, 5));
            } else {
                items.addAll(this.itemsHash.get(key));
            }
        }

        return items;
    }

    public ArrayList<Article> items(Boolean cleanCategories) {
        if(itemsHash == null){
            return (ArrayList<Article>)itemsList;
        }

        List items = items();
        List itemsCopy = items();
        for(Object item: items){
            if(item.getClass() == String.class){
                itemsCopy.remove(item);
            }
        }

        return (ArrayList<Article>) itemsCopy;
    }

    public Object getItemAtPosition(int position) {
        return items().get(position);
    }

    public ArrayList<Article> getArticlesForCategory(String category){
        if(itemsHash != null){
            return itemsHash.get(category);
        }
        return null;
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
