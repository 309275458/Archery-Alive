package com.archeryalive.game.sdk;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Description:
 *
 * @author: roy
 * Time: 2022/7/12 16:47
 * Modifier:
 * Fix Description:
 * Version:
 */
public class GpUtil {

    public static boolean redirectGp(Context context, String url) {
        if (context == null || TextUtils.isEmpty(url)) {
            return false;
        }
        try {
            Uri uri = Uri.parse(url);
            Intent intent = null;
            if (url.startsWith("intent://")) {
                try {
                    intent = parseIntent(url);
                    if (intent != null) {
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        return true;
                    }
                } catch (ActivityNotFoundException e) {
                    Log.e("GpUtil", e.getLocalizedMessage());
                    if (intent != null && intent.getExtras() != null) {
                        String ref = intent.getExtras().getString("market_referrer");
                        String marketUrl = "market://details?id=" + intent.getPackage() + "&referrer=" + ref;
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(marketUrl));
                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                        intent.setPackage("com.android.vending");
                        if (intent.resolveActivity(context.getPackageManager()) != null) {
                            context.startActivity(intent);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            } else {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.android.vending");
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            Log.e("GpUtil", e.getLocalizedMessage());
        }
        return false;
    }


    public static boolean isGp(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        try {
            Uri dst = Uri.parse(url);
            if (dst == null) {
                return false;
            }

            String scheme = lowerCase(dst.getScheme());
            String host = lowerCase(dst.getHost());
            return "market".equals(scheme)
                    || "play.google.com".equals(host)
                    || "mobile.gmarket.co.kr".equals(host);
        } catch (Exception e) {
            Log.e("GpUtil", e.getLocalizedMessage());
            return false;
        }
    }

    private static String lowerCase(String s) {
        return s == null ? null : s.toLowerCase();
    }

    public static Intent parseIntent(String url) {
        try {
            int i = url.indexOf("%23Intent&");
            if (i != -1) {
                String before = url.substring(0, i);
                String after = url.substring(i + 3);
                after = after.replace('&', ';');
                url = before + '#' + after;
            }
            return Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (Exception e) {
            Log.e("GpUtil", e.getLocalizedMessage());
        }
        return null;
    }
}
