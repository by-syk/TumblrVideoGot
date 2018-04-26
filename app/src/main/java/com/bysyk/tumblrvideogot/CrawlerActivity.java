package com.bysyk.tumblrvideogot;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.by_syk.lib.globaltoast.GlobalToast;
import com.bysyk.tumblrvideogot.bean.MetaBean;
import com.bysyk.tumblrvideogot.util.TumblrHelper;
import com.bysyk.tumblrvideogot.util.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CrawlerActivity extends Activity {
    @BindView(R.id.tv_post_url)
    TextView tvPostUrl;

    @BindView(R.id.tv_video_url)
    TextView tvVideoUrl;

    @BindView(R.id.tv_log)
    TextView tvLog;

    @BindView(R.id.tv_video_quality_hd)
    TextView tvVideoQualityHd;

    @BindView(R.id.pb_find_video)
    ProgressBar pbFindUrl;

    @BindView(R.id.bt_download)
    Button btDownload;

    @BindView(R.id.bt_play)
    Button btPlay;

    @BindView(R.id.view_post_url)
    View viewPostUrl;

    @BindView(R.id.view_video_url)
    View viewVideoUrl;

    @BindView(R.id.view_video_url_in)
    View viewVideoUrlIn;

    @NonNull
    private MetaBean metaBean = new MetaBean();

    private boolean toCopyPostUrlNotLog = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crawler);

        init();
    }

    @OnClick(R.id.bt_download)
    void onDownload() {
        if (!checkPermission()) {
            return;
        }
        if (metaBean.getVideoUrl() == null) {
            return;
        }
        String fileName = metaBean.getVideoName();
        File destFile = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            DownloadManager.Request request = new DownloadManager
                    .Request(Uri.parse(metaBean.getVideoUrl()));
            request.setMimeType("video/*");
            request.setTitle(fileName);
            request.setDestinationUri(Uri.fromFile(destFile));
            request.setNotificationVisibility(DownloadManager
                    .Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            manager.enqueue(request);
        }

        finish();
    }

    @OnClick(R.id.bt_play)
    void onPlay() {
        if (metaBean.getVideoUrl() == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(metaBean.getVideoUrl()), "video/*");
        startActivity(intent);
    }

    @OnClick(R.id.view_post_url)
    void onCopyPostUrl() {
        if (toCopyPostUrlNotLog || metaBean.getException() == null) {
            if (Utils.copy2Clipboard(this, metaBean.getPostUrlEncoded())) {
                GlobalToast.show(this, R.string.toast_post_url_copied);
            }
        } else {
            if (Utils.copy2Clipboard(this, metaBean.getException())) {
                GlobalToast.show(this, R.string.toast_log_copied);
            }
        }
        toCopyPostUrlNotLog = !toCopyPostUrlNotLog;
    }

    @OnClick(R.id.view_video_url)
    void onCopyVideoUrl() {
        if (Utils.copy2Clipboard(this, metaBean.getVideoUrl())) {
            GlobalToast.show(this, R.string.toast_video_url_copied);
        }
    }

    private void init() {
        ButterKnife.bind(this);

        metaBean.setPostUrl(handleSendText(getIntent()));
        tvPostUrl.setText(metaBean.getPostUrl());
        (new CrawlerTask()).execute();
    }

    @Nullable
    private String handleSendText(@NonNull Intent intent) {
        if (!Intent.ACTION_SEND.equals(intent.getAction())
                || !"text/plain".equals(intent.getType())) {
            return null;
        }

//        return intent.getStringExtra(Intent.EXTRA_TEXT);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            CharSequence data = bundle.getCharSequence(Intent.EXTRA_TEXT);
            if (data != null) {
                return data.toString();
            }
        }
        return null;
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        return false;
    }

    class CrawlerTask extends AsyncTask<String, String, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            if (!metaBean.isTumblrPost()) {
                publishProgress(getString(R.string.log_invalid_post_url));
                return false;
            }
            TumblrHelper.findVideoUrl(metaBean);
            if (metaBean.getVideoUrl() == null) {
                if (metaBean.isSensitiveMedia()) {
                    publishProgress(getString(R.string.log_safe_mode_limit));
                } else if (metaBean.getException() != null) {
                    publishProgress(metaBean.getException());
                } else {
                    publishProgress();
                }
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            if (values.length > 0) {
                tvLog.setText(values[0]);
            }
            tvLog.setVisibility(View.VISIBLE);
            pbFindUrl.setIndeterminate(false);
            pbFindUrl.setProgress(0);
        }

        @Override
        protected void onPostExecute(@NonNull Boolean result) {
            super.onPostExecute(result);

            if (isDestroyed()) {
                return;
            }

            if (result && metaBean.getVideoUrl() != null) {
                tvVideoUrl.setText(metaBean.getVideoUrl());
                if (metaBean.isHdSet()) {
                    tvVideoQualityHd.setVisibility(View.VISIBLE);
                }
                viewVideoUrlIn.setVisibility(View.VISIBLE);
                pbFindUrl.setVisibility(View.GONE);
                btDownload.setEnabled(true);
                btPlay.setEnabled(true);
            }
        }
    }
}
