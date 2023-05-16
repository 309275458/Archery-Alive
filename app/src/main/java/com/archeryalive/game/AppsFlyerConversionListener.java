package com.archeryalive.game;

import android.util.Log;
import java.util.Map;
import java.util.Objects;

/**
 * Description:
 *
 * @author: roy
 * Time: 2022/2/1 2:30 下午
 * Modifier:
 * Fix Description:
 * Version:
 */
public class AppsFlyerConversionListener implements com.appsflyer.AppsFlyerConversionListener {
    public static final String LOG_TAG = "AppsFlyer";
    public static final String DL_ATTRS = "dl_attrs";
    Map<String, Object> conversionData = null;

    @Override
    public void onConversionDataSuccess(Map<String, Object> conversionDataMap) {
        for (String attrName : conversionDataMap.keySet()) {
            Log.d(LOG_TAG, "Conversion attribute: " + attrName + " = " + conversionDataMap.get(attrName));
        }
        String status = Objects.requireNonNull(conversionDataMap.get("af_status")).toString();
        if ("Non-organic".equals(status)) {
            if (Objects.requireNonNull(conversionDataMap.get("is_first_launch")).toString().equals("true")) {
                Log.d(LOG_TAG, "Conversion: First Launch");
            } else {
                Log.d(LOG_TAG, "Conversion: Not First Launch");
            }
        } else {
            Log.d(LOG_TAG, "Conversion: This is an organic install.");
        }
        conversionData = conversionDataMap;
    }

    @Override
    public void onConversionDataFail(String errorMessage) {
        Log.d(LOG_TAG, "error getting conversion data: " + errorMessage);
    }

    @Override
    public void onAppOpenAttribution(Map<String, String> attributionData) {
        Log.d(LOG_TAG, "onAppOpenAttribution: This is fake call.");
    }

    @Override
    public void onAttributionFailure(String errorMessage) {
        Log.d(LOG_TAG, "error onAttributionFailure : " + errorMessage);
    }

}
