package com.bysyk.tumblrvideogot.bean;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.URLUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MetaBean {
    @NonNull
    private PostUrl postUrl = new PostUrl();

    @NonNull
    private VideoUrl videoUrl = new VideoUrl();

    private boolean isSensitiveMedia;

    private String exception;

    private long timestamp = System.currentTimeMillis();

    private static Pattern patternVideoId = Pattern.compile("^(.+?/tumblr_([\\da-zA-Z]+))(/\\d+)?$");
    private static Pattern patternVideoId2 = Pattern.compile("(^.+?/tumblr_([\\da-zA-Z]+))(_\\d+)?(\\..+)$");

    public void setPostUrl(@Nullable String postUrl) {
        this.postUrl = new PostUrl();
        if (URLUtil.isValidUrl(postUrl)) {
            this.postUrl.url = postUrl;
            this.postUrl.urlEncoded = Uri.encode(postUrl, "@#&=*+-_.,:!?()/~'%");
            this.postUrl.isTumblrPost = !TextUtils.isEmpty(postUrl)
                    && postUrl.contains("tumblr.com/post/");
        }
    }

    public void setVideoUrl(@Nullable String videoUrl) {
        this.videoUrl = new VideoUrl();
        if (URLUtil.isValidUrl(videoUrl)) {
            this.videoUrl.url = videoUrl;
            if (videoUrl.contains("vt.media.tumblr.com")) {
                Matcher matcher = patternVideoId2.matcher(videoUrl);
                if (matcher.matches()) {
                    this.videoUrl.id = matcher.group(2);
                    if (matcher.group(3) != null) {
                        this.videoUrl.url = matcher.group(1) + matcher.group(4);
                        this.videoUrl.isHdSet = true;
                    }
                }
            } else {
                Matcher matcher = patternVideoId.matcher(videoUrl);
                if (matcher.matches()) {
                    this.videoUrl.id = matcher.group(2);
                    if (matcher.group(3) != null) {
                        this.videoUrl.url = matcher.group(1);
                        this.videoUrl.isHdSet = true;
                    }
                }
            }
        }
    }

    public void setSensitiveMedia(boolean sensitiveMedia) {
        isSensitiveMedia = sensitiveMedia;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    @Nullable
    public String getPostUrl() {
        return postUrl.url;
    }

    @Nullable
    public String getPostUrlEncoded() {
        return postUrl.urlEncoded;
    }

    public boolean isTumblrPost() {
        return postUrl.isTumblrPost;
    }

    @Nullable
    public String getVideoUrl() {
        return videoUrl.url;
    }

    @Nullable
    public String getVideoFormat() {
        return videoUrl.format;
    }

    public boolean isHdSet() {
        return videoUrl.isHdSet;
    }

    @NonNull
    public String getVideoName() {
        return "tumblr_"
                + (videoUrl.id != null ? videoUrl.id : String.valueOf(timestamp))
                + videoUrl.format;
    }

    public boolean isSensitiveMedia() {
        return isSensitiveMedia;
    }

    public String getException() {
        return exception;
    }

    class PostUrl {
        // like: https://nice--food.tumblr.com/post/171007601335/ice-cube-apple-pie
        @Nullable
        String url;

        @Nullable
        String urlEncoded;

        boolean isTumblrPost;
    }

    class VideoUrl {
        // like: https://nice--food.tumblr.com/video_file/t:YIB4-vuy4KWM2GGcvcPZ-A/171007601335/tumblr_p4c7qbtemI1wrhoyi/480
        // or like: https://vt.media.tumblr.com/tumblr_p4c7qbtemI1wrhoyi.mp4
        @Nullable
        String url;

        // TODO
        @NonNull
        String format = ".mp4";

        // like: p4c7qbtemI1wrhoyi
        @Nullable
        String id;

        // 压缩视频已替换为原视频
        boolean isHdSet;
    }
}