package com.bysyk.tumblrvideogot;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.by_syk.lib.globaltoast.GlobalToast;
import com.bysyk.tumblrvideogot.dlg.AboutDlg;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeActivity extends Activity {
    @BindView(R.id.switch_crawler_on)
    Switch switchCrawlerOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        ButterKnife.bind(this);

        switchCrawlerOn.setChecked(isCrawlerEnabled());

        ((EditText) findViewById(R.id.et_video_id))
                .setText("p4c7qbtemI1wrhoyi");
        ((EditText) findViewById(R.id.et_post_url))
                .setText("https://nice--food.tumblr.com/post/171007601335/ice-cube-apple-pie");
    }

    // after @OnCheckedChanged
    @OnClick(R.id.switch_crawler_on)
    public void onSwitchCrawler() {
        toggleCrawler(switchCrawlerOn.isChecked());
    }

    public void onTestDownload(View view) {
        if (!checkPermission()) {
            return;
        }

        String fileName = "tumblr_"
                + ((EditText) findViewById(R.id.et_video_id)).getText().toString()
                + ".mp4";
        File destFile = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        if (manager != null) {
            DownloadManager.Request request = new DownloadManager
                    .Request(Uri.parse(getTestUrl()));
            request.setMimeType("video/*");
            request.setTitle(fileName);
            request.setDestinationUri(Uri.fromFile(destFile));
            request.setNotificationVisibility(DownloadManager
                    .Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            manager.enqueue(request);
        }
    }

    public void onTestPlay(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(getTestUrl()), "video/*");
        startActivity(intent);
    }

    public void onTestCopyUrl(View view) {
        GlobalToast.copyAndShow(this, getTestUrl());
    }

    public void onTestCrawler(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,
                ((EditText) findViewById(R.id.et_post_url)).getText());
        intent.setType("text/plain");
        startActivity(intent);
    }

    private String getTestUrl() {
        return "https://vtt.tumblr.com/tumblr_"
                + ((EditText) findViewById(R.id.et_video_id)).getText().toString()
                + ".mp4";
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT < 23 || checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        return false;
    }

    private void toggleCrawler(boolean enable) {
        ComponentName module = new ComponentName(getPackageName(), CrawlerActivity.class.getName());
        getPackageManager().setComponentEnabledSetting(module,
                enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private boolean isCrawlerEnabled() {
        ComponentName module = new ComponentName(getPackageName(), CrawlerActivity.class.getName());
        int status = getPackageManager().getComponentEnabledSetting(module);
        return status == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT // 当前配置下默认是启用
                || status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_welcome, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_about) {
            AboutDlg.newInstance().show(getFragmentManager(), "about");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
