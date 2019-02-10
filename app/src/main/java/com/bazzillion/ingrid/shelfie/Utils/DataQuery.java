package com.bazzillion.ingrid.shelfie.Utils;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bazzillion.ingrid.shelfie.Base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public final class DataQuery {

    private static final String URI_BASE = "https://shelfie-18c58.firebaseio.com/";
    private static final String KEY_BASE = "base.json";
    private static final String KEY_INGREDIENT = "Ingredient.json";
    private static final String PARAM_ORDER_BY = "orderBy";
    private static final String VALUE_NAME = "\"name\"";
    private static final String PARAM_EQUAL_TO = "equalTo";
    private static final String LOG_TAG = DataQuery.class.getSimpleName();

    private static URL buildUrl(String baseName, String key) {
        URL url = null;
        Uri.Builder uri = Uri.parse(URI_BASE)
                .buildUpon()
                .appendEncodedPath(key)
                .appendQueryParameter(PARAM_ORDER_BY, VALUE_NAME)
                .appendQueryParameter(PARAM_EQUAL_TO, "\"" + baseName + "\"");
        try {
            url = new URL(uri.build().toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.i(LOG_TAG, uri.build().toString());
        return url;
    }

    private static InputStream makeHttpRequest(URL url){
        HttpURLConnection httpURLConnection;
        InputStream inputStream = null;
        int responseCode;
        if (url != null) {
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();
                responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    Log.e(LOG_TAG, "Request impossible. Response code: " + responseCode);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        }
        return inputStream;
    }

    private static String readFromStream(InputStream inputStream) {
        StringBuilder jsonResponse = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            try {
                line = bufferedReader.readLine();
                while (line != null) {
                    jsonResponse.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonResponse.toString();
    }

//    private static Base extractBaseFromJson(String jsonResponse){
//        if (!TextUtils.isEmpty(jsonResponse)) {
//            try {
//                JSONObject response = new JSONObject(jsonResponse);
//                JSONArray results = response.optJSONArray(KEY_RESULTS);
//                for (int i = 0; i < results.length(); i++) {

//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//    }

//    public static Base getBaseByName(String baseName){
//        URL url = buildUrl(baseName);
//        InputStream inputStream = makeHttpRequest(url);
//        String jsonResponse = readFromStream(inputStream);
//        Log.i(LOG_TAG, jsonResponse);
//        return extractBaseFromJson(jsonResponse);
//    }

}
