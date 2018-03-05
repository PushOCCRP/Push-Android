package com.pushapp.press.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pushapp.press.R;
import com.pushapp.press.util.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Created by christopher on 10/19/15.
 */

public class AboutFragment extends Fragment {
    //TODO: extract this to a central place
    static final String REGEX = "(?!<a[^>]*?>)(http[^\\s]+)(?![^<]*?</a>)";


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        StringBuilder buf=new StringBuilder();

        InputStream htmlStream = null;
        try {

            String aboutFileName = "about_text-" + Language.getLanguage(getActivity())+".html";
            htmlStream = getActivity().getAssets().open(aboutFileName);

            BufferedReader in = new BufferedReader(new InputStreamReader(htmlStream, "UTF-8"));
            String str;

            while ((str=in.readLine()) != null) {
                buf.append(str);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String html = buf.toString();
        html = cleanUpHTML(html);

        TextView textView = (TextView) rootView.findViewById(R.id.about_text);
        textView.setText(Html.fromHtml(html));

        textView.setMovementMethod(LinkMovementMethod.getInstance());

        Linkify.addLinks(textView, Pattern.compile(REGEX), "https://");
        Linkify.addLinks(textView, Pattern.compile(REGEX), "http://");

        return rootView;
    }

    public String cleanUpHTML(String html) {
        String cleanHtml = formatUnorderedLists(html);
        return cleanHtml;
    }

    public String formatUnorderedLists(String html) {
        String cleanHtml = html.replace("</li>", "<br />");
        cleanHtml = cleanHtml.replace("<ul>", "");
        cleanHtml = cleanHtml.replace("</ul>", "<br />");
        cleanHtml = cleanHtml.replace("<li>", "• ");
        return cleanHtml;
    }

}