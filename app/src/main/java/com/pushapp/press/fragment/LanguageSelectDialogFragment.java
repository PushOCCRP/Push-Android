package com.pushapp.press.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.pushapp.press.R;
import com.pushapp.press.util.Language;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;

/**
 * Created by christopher on 1/4/16.
 */
public class LanguageSelectDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.choose_language)
                .setNegativeButton(R.string.cancel, null);

        // Sort the languages in alphabetic order
        final Set languages = Language.getAppLanguages(getActivity());

        // The joys of Java casting, why are there fourteen types of lists?
        // This literally just massages everything into being comprehensible collections and lists
        ArrayList<String> languageArray = new ArrayList(Arrays.asList(languages.toArray()));

        //Also, it sorts them
        Collections.sort(languageArray);
        final CharSequence[] languageChars = languageArray.toArray(new CharSequence[languages.size()]);

        int index = -1;
        Locale currentLocale = Language.getLanguage(getActivity());

        ArrayList<String> fullLanguageArrayList = new ArrayList<>();

        // This loop gets the index to be selected and also get the correct full language name for display
        for(int i = 0; i < languageChars.length; i++) {
            CharSequence languageSequence = languageChars[i];

            if(currentLocale.getLanguage().equals(languageSequence.toString())){
                index = i;
            }

            Locale locale = Language.languageForTag(languageSequence.toString());
            //Serbian has both Latin and Cyrillic, since this is rare, Android of course only
            //support Cyrillic, the same one that no one really uses
            String displayLanguage;
            if(!locale.getLanguage().equals("sr")) {
                displayLanguage = locale.getDisplayLanguage(locale);
            } else {
                displayLanguage = "Srpski";
            }

            fullLanguageArrayList.add(displayLanguage);
        }

        // More massaging
        CharSequence[] fullLanguageCharSequence = fullLanguageArrayList.toArray(new CharSequence[fullLanguageArrayList.size()]);

        // Finally set the choices
        builder.setSingleChoiceItems(fullLanguageCharSequence, index, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String locale = languageChars[which].toString();
                Locale l = Language.languageForTag(locale);
                Language.setLanguage(getActivity(), l);

                dismiss();
            }
        });

        // Create the AlertDialog object and return it
        return builder.create();
    }

}
