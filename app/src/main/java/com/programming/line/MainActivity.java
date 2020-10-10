package com.programming.line;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

public class MainActivity extends AppCompatActivity implements WebViewListener {

    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout noInternetLayout;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private TextView errorTextView;
    private ImageView splashScreenIv;
    private Button tryAgainButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OneSignal.startInit(this).inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification).unsubscribeWhenNotificationsAreDisabled(true).setNotificationReceivedHandler(new MyNotificationReceivedHandler()).setNotificationOpenedHandler(new MyNotificationOpenedHandler()).init();
        setContentView(R.layout.activity_main);
        splashScreenIv = findViewById(R.id.splash_screen);
        errorTextView = findViewById(R.id.error_message_tv);
        progressBar = findViewById(R.id.progress_bar);
         tryAgainButton = findViewById(R.id.try_again_button);
        linearLayout = findViewById(R.id.linearLayout);
        noInternetLayout = findViewById(R.id.no_internet_layout);
        webView = findViewById(R.id.webview);
        showSplashScreen(true);
        webView.setWebViewClient(new WebViewClient(this, this));
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        loadWebview();
        swipeRefreshLayout = findViewById(R.id.pullfresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadWebview();
            swipeRefreshLayout.setRefreshing(false);
        });
        webView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (webView.getScrollY() == 0) {
                swipeRefreshLayout.setEnabled(true);
            }else{
                swipeRefreshLayout.setEnabled(false);
            }
        });
        tryAgainButton.setOnClickListener(v -> {
            loadWebview();
        });
    }

    private void showProgress(boolean a) {
        if (a)
        {
            progressBar.setVisibility(View.VISIBLE);
        }else{
            linearLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    private  void loadWebview()
    {
        showProgress(true);
        if (checkConnection())
        {
            showNoInternetLayout(false);
            webView.loadUrl("https://programmingline.com");
        }else{
            showProgress(false);
            tryAgainButton.invalidate();
            showSplashScreen(false);
            showNoInternetLayout(true);
        }
    }


    @Override
    public void pageStarted() {
        showProgress(true);
        progressBar.setProgress(0);
    }

    @Override
    public void pageFinished() {
        showProgress(false);
        showSplashScreen(false);
    }

    @Override
    public void onErrorOccurred(String error) {
        showNoInternetLayout(true,error);
    }

    private void showNoInternetLayout(boolean b, String s) {
        showNoInternetLayout(b);
        errorTextView.setText(s);
    }

    private void showNoInternetLayout(boolean a)
    {
        showProgress(false);
        if (a)
        {
            webView.setVisibility(View.GONE);
            noInternetLayout.setVisibility(View.VISIBLE);
        }else{
            webView.setVisibility(View.VISIBLE);
            noInternetLayout.setVisibility(View.GONE);
        }
    }

    private void showSplashScreen(boolean a)
    {
        if (a)
        {
            splashScreenIv.setVisibility(View.VISIBLE);
        }else
        {
            splashScreenIv.setVisibility(View.GONE);
        }
    }

    public boolean checkConnection() {
        final ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) {
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }

}

class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler{
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        Log.d("TAG", "opened");
    }
}

class MyNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler{

    @Override
    public void notificationReceived(OSNotification notification) {
        Log.d("TAG", "Received");
    }
}