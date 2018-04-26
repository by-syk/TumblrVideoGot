package com.bysyk.tumblrvideogot.util;

import android.support.annotation.NonNull;
import android.util.Log;

import com.bysyk.tumblrvideogot.bean.MetaBean;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TumblrHelper {
    private static Pattern patternPlayerFrameUrl = Pattern.compile("<iframe.+?src='(.+?)'");
    private static Pattern patternVideoUrl = Pattern.compile("<source src=\"(.+?)\" type=\"video/(.+?)");

    public static void findVideoUrl(@NonNull MetaBean bean) {
        // TODO 无法解析受安全搜索控制的内容
        getPlayerFrameUrl(bean);
        getVideoSrcUrl(bean);
    }

    private static void getPlayerFrameUrl(@NonNull MetaBean bean) {
        if (bean.getPostUrl() == null) {
            return;
        }
        try {
            String page = HttpUtil.getPage(bean.getPostUrlEncoded());
            if (page == null) {
                return;
            }
            Matcher matcher = patternPlayerFrameUrl.matcher(page);
            if (matcher.find()) {
                bean.setPlayFrameUrl(matcher.group(1));
            } else if (page.contains("id=\"safemode_actions_display\"")) {
                Log.d("test", "blocked by safe mode");
                bean.setSensitiveMedia(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            bean.setException(e.getMessage());
        }
    }

    private static void getVideoSrcUrl(@NonNull MetaBean bean) {
        if (bean.getPlayFrameUrl() == null) {
            return;
        }
        try {
            String page = HttpUtil.getPage(bean.getPlayFrameUrl());
            if (page == null) {
                return;
            }
            Matcher matcher = patternVideoUrl.matcher(page);
            if (matcher.find()) {
                bean.setVideoUrl(matcher.group(1));
//                bean.setVideoFormat(matcher.group(2));
            }
        } catch (IOException e) {
            e.printStackTrace();
            bean.setException(e.getMessage());
        }
    }
}
