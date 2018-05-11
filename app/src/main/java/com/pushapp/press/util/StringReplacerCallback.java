// Borrowed with love from https://www.whitebyte.info/programming/string-replace-with-callback-in-java-like-in-javascript

package com.pushapp.press.util;

import java.util.regex.Matcher;

public interface StringReplacerCallback {
    public String replace(Matcher match);
}