package com.hypnotriod.web_view_helper;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationDialog.NavigationDialogListener {

    static final String KEY_RECENT_URLS = "RECENT_URLS";
    static final String KEY_FULL_SCREEN = "FULL_SCREEN";
    static final String KEY_HIDE_NAVIGATION = "HIDE_NAVIGATION";
    static final String KEY_LAYOUT_NO_LIMITS = "LAYOUT_NO_LIMITS";

    WebView webView;
    String currentUrlAddress = "http://";
    ArrayList<String> recentURLs;
    boolean fullScreen = false;
    boolean hideNavigation = false;
    boolean layoutNoLimits = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
        setContentView(R.layout.activity_main);
        initWebView();
        launchWebView();
        updateSystemUiLayout();
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

    @Override
    public void onToggleFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
        updateSystemUiLayout();
        saveData();
    }

    @Override
    public void onToggleHideNavigation(boolean hideNavigation) {
        this.hideNavigation = hideNavigation;
        updateSystemUiLayout();
        saveData();
    }

    @Override
    public void onToggleLayoutNoLimits(boolean layoutNoLimits) {
        this.layoutNoLimits = layoutNoLimits;
        updateSystemUiLayout();
        saveData();
    }

    @Override
    public void onNavigationDialogDismiss() {
    }

    public void onNavigationDialogExit() {
        webView.stopLoading();
        webView.destroy();
        finish();
    }

    private void showNavigationDialog() {
        NavigationDialog navigationDialog = new NavigationDialog();
        navigationDialog.setSettings(
                currentUrlAddress,
                recentURLs,
                fullScreen,
                hideNavigation,
                layoutNoLimits,
                this);
        navigationDialog.show(getFragmentManager(), null);
    }

    @SuppressLint("SetJavaScriptEnabled")
    void initWebView() {
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setUserAgentString(System.getProperty("http.agent"));
        WebView.setWebContentsDebuggingEnabled(true);
    }

    void loadData() {
        Type padVOsArrayListType = new TypeToken<ArrayList<String>>() {
        }.getType();
        recentURLs = PreferenceConnector.readObject(this, KEY_RECENT_URLS, padVOsArrayListType);
        if (recentURLs == null) recentURLs = new ArrayList<>();
        hideNavigation = PreferenceConnector.readBoolean(this, KEY_HIDE_NAVIGATION, false);
        fullScreen = PreferenceConnector.readBoolean(this, KEY_FULL_SCREEN, false);
        layoutNoLimits = PreferenceConnector.readBoolean(this, KEY_LAYOUT_NO_LIMITS, false);
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
        PreferenceConnector.writeBoolean(this, KEY_FULL_SCREEN, fullScreen);
        PreferenceConnector.writeBoolean(this, KEY_HIDE_NAVIGATION, hideNavigation);
        PreferenceConnector.writeBoolean(this, KEY_LAYOUT_NO_LIMITS, layoutNoLimits);
    }

    private void openURL(String url) {
        currentUrlAddress = url;
        while (recentURLs.contains(url)) recentURLs.remove(url);
        recentURLs.add(0, url);
        saveData();

        webView.clearCache(true);
        webView.loadUrl(url);
    }

    private void updateSystemUiLayout() {
        if (layoutNoLimits) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                getWindow().setFlags(WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS,
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            }
        }
        int systemUiVisibility = 0;
        if (fullScreen) {
            systemUiVisibility |= View.SYSTEM_UI_FLAG_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            getWindow().setNavigationBarColor(android.graphics.Color.TRANSPARENT);
        }
        if (hideNavigation) {
            systemUiVisibility |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        }
        getWindow().getDecorView().setSystemUiVisibility(systemUiVisibility);
    }
}