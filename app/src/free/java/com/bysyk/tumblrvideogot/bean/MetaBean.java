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

    // like: https://www.tumblr.com/video/nice--food/171007601335/700/
    @Nullable
    private String playFrameUrl;

    @NonNull
    private VideoUrl videoUrl = new VideoUrl();

    private boolean isSensitiveMedia;

    private String exception;

    private long timestamp = System.currentTimeMillis();

    private static Pattern patternVideoId = Pattern.compile("^.+?/tumblr_([\\da-zA-Z]+)(?:/(\\d+))?$");

    public void setPostUrl(@Nullable String postUrl) {
        this.postUrl = new PostUrl();
        if (URLUtil.isValidUrl(postUrl)) {
            this.postUrl.url = postUrl;
            this.postUrl.urlEncoded = Uri.encode(postUrl, "@#&=*+-_.,:!?()/~'%");
            this.postUrl.isTumblrPost = !TextUtils.isEmpty(postUrl)
                    && postUrl.contains("tumblr.com/post/");
        }
    }

    public void setPlayFrameUrl(@Nullable String playFrameUrl) {
        this.playFrameUrl = playFrameUrl;
    }

    public void setVideoUrl(@Nullable String videoUrl) {
        this.videoUrl = new VideoUrl();
        if (URLUtil.isValidUrl(videoUrl)) {
            this.videoUrl.url = videoUrl;
            Matcher matcher = patternVideoId.matcher(videoUrl);
            if (matcher.matches()) {
                this.videoUrl.id = matcher.group(1);
                if (matcher.group(2) != null) {
                    this.videoUrl.quality = Integer.parseInt(matcher.group(2));
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
    public String getPlayFrameUrl() {
        return playFrameUrl;
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
        String name = "tumblr_" + (videoUrl.id != null ? videoUrl.id : String.valueOf(timestamp));
        if (videoUrl.quality != 0) {
            name += "_" + videoUrl.quality;
        }
        return name + videoUrl.format;
    }

    public boolean isSensitiveMedia() {
        return isSensitiveMedia;
    }

    public String getException() {
        return exception;
    }

    class PostUrl {
        // Like: https://nice--food.tumblr.com/post/171007601335/ice-cube-apple-pie
        @Nullable
        String url;

        @Nullable
        String urlEncoded;

        boolean isTumblrPost;
    }

    class VideoUrl {
        // like: https://nice--food.tumblr.com/video_file/t:YIB4-vuy4KWM2GGcvcPZ-A/171007601335/tumblr_p4c7qbtemI1wrhoyi/480
        @Nullable
        String url;

        // TODO
        @NonNull
        String format = ".mp4";

        // like: p4c7qbtemI1wrhoyi
        @Nullable
        String id;

        // 压缩程度，0为未压缩
        // like: 480
        int quality;

        // 压缩视频已替换为原视频
        boolean isHdSet;
    }
}
