package com.push.cins.util;

import java.security.MessageDigest;

/**
 * Created by christopher on 7/22/16.
 */
public class AeSimpleSHA1 {
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static String SHA1(String text) {

        MessageDigest md;

        try {
            md = MessageDigest.getInstance("SHA-1");
            md.update(text.getBytes("iso-8859-1"), 0, text.length());
        } catch (Exception e) {
            return null;
        }

        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }
}
