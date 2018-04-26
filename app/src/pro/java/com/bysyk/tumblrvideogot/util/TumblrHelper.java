package com.bysyk.tumblrvideogot.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.URLUtil;

import com.bysyk.tumblrvideogot.bean.MetaBean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TumblrHelper {
    private static final String API_URLGOT = "http://api.urlgot.com/parse/url";
    private static final String API_PARSEVIDEO = "https://parsevideo.com/api.php";
    private static final String URL_PARSEVIDEO = "https://parsevideo.com/";

    private static Pattern patternParsevideoHash = Pattern.compile("var hash = \"([\\da-zA-Z]+)\";");

    public static void findVideoUrl(@NonNull MetaBean bean) {
        getVideoUrlFromUrlgot(bean);
        if (bean.getVideoUrl() == null) {
            getVideoUrlFromParsevideo(bean);
        }
    }

    private static void getVideoUrlFromUrlgot(@NonNull MetaBean bean) {
        Log.d("test", "getVideoUrlFromUrlgot()");
        if (bean.getPostUrl() == null) {
            return;
        }
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("mediaUrl", bean.getPostUrlEncoded());
        try {
            String jsonData = HttpUtil.postForm(API_URLGOT, paramMap);
            Log.d("test", "urlgot json: " + jsonData);
            JSONObject jo = new JSONObject(jsonData);
            if ("0".equals(jo.getString("error"))) {
                jo = jo.getJSONArray("mediaList").getJSONObject(0);
                JSONArray ja = jo.getJSONArray("urlList");
                for (int i = 0, len = ja.length(); i < len; ++i) {
                    if (URLUtil.isValidUrl(ja.getString(i))) {
                        bean.setVideoUrl(ja.getString(i));
                        break;
                    }
                }
            } else {
                bean.setException("urlgot.com: " + jsonData);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            bean.setException(e.getMessage());
        }
    }

    private static void getVideoUrlFromParsevideo(@NonNull MetaBean bean) {
        Log.d("test", "getVideoUrlFromParsevideo()");
        if (bean.getPostUrl() == null) {
            return;
        }
        try {
            String hash = getParsevideoHash();
            Log.d("test", "hash: " + hash);
            if (hash == null) {
                bean.setException("parsevideo.com: cannot get hash");
                return;
            }
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("url", bean.getPostUrlEncoded());
            paramMap.put("hash", hash);
            String jsonData = HttpUtil.postForm(API_PARSEVIDEO, paramMap);
            jsonData = Utils.parseEscapedHans(jsonData); // 解码 \\uxxxx 中文
            Log.d("test", "parsevideo json: " + jsonData);
            JSONObject jo = new JSONObject(jsonData);
            if ("ok".equals(jo.getString("status"))) {
                jo = jo.getJSONArray("video").getJSONObject(0);
                bean.setVideoUrl(jo.getString("url"));
            } else {
                bean.setException("parsevideo.com: " + jsonData);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            bean.setException(e.getMessage());
        }
    }

    @Nullable
    private static String getParsevideoHash() throws IOException {
        String page = HttpUtil.getPage(URL_PARSEVIDEO);
        if (page != null) {
            Matcher matcher = patternParsevideoHash.matcher(page);
            if (matcher.find()) {
                return matcher.group(1);
            }
        }
        return null;
    }
}
