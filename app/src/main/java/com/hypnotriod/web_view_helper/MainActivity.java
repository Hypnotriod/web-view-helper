package com.hypnotriod.web_view_helper;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationDialog.NavigationDialogListener {

    static final String KEY_RECENT_URLS = "RECENT_URLS";

    WebView webView;
    String currentUrlAddress = "http://";
    ArrayList<String> recentURLs;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        setContentView(R.layout.activity_main);

        loadData();
        initWebView();
        launchWebView();
    }

    @Override
    public void onBackPressed() {
        showNavigationDialog();
    }

    public void onNavigationDialogURLChosen(String url) {
        openURL(url);
    }

    public void onNavigationDialogItemDelete(int position) {
        saveData();
    }

    public void onNavigationDialogExit() {
        webView.stopLoading();
        webView.destroy();
        finish();
    }

    private void showNavigationDialog() {
        NavigationDialog navigationDialog = new NavigationDialog();
        navigationDialog.setSettings(currentUrlAddress, recentURLs, this);
        navigationDialog.show(getFragmentManager(), null);
    }

    @SuppressLint("SetJavaScriptEnabled")
    void initWebView() {
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        WebView.setWebContentsDebuggingEnabled(true);
    }

    void loadData() {
        Type padVOsArrayListType = new TypeToken<ArrayList<String>>() {
        }.getType();
        recentURLs = PreferenceConnector.readObject(this, KEY_RECENT_URLS, padVOsArrayListType);
        if (recentURLs == null) recentURLs = new ArrayList<>();
    }

    private void launchWebView() {
        try {
            Uri uri = getIntent().getData();
            if (uri != null) {
                openURL(uri.toString());
            } else {
                showNavigationDialog();
            }
        } catch (Exception e) {
            showToast(getResources().getString(R.string.bad_url), Toast.LENGTH_LONG);
        }
    }

    private void showToast(String text, int duration) {
        Toast.makeText(this, text, duration).show();
    }

    private void saveData() {
        PreferenceConnector.writeObject(this, KEY_RECENT_URLS, recentURLs);
    }

    private void openURL(String url) {
        currentUrlAddress = url;
        while (recentURLs.contains(url)) recentURLs.remove(url);
        recentURLs.add(0, url);
        saveData();

        webView.clearCache(true);
        webView.loadUrl(url);
    }
}