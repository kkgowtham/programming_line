package com.programming.line;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

import androidx.annotation.RequiresApi;

import java.net.MalformedURLException;
import java.net.URL;

public class WebViewClient extends android.webkit.WebViewClient {

    Context context;
    private WebViewListener webViewListener;
    public WebViewClient(Context context,WebViewListener webViewListener){
        this.context = context;
        this.webViewListener = webViewListener;
    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return handleUrl(url);
    }

    private boolean handleUrl(String url) {
        try {
            URL mUrl = new URL(url);
            if (!mUrl.getHost().equals("programmingline.com")) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
                return true;
            }
            return false;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return handleUrl(request.getUrl().toString());
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        webViewListener.pageStarted();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        webViewListener.pageFinished();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
            webViewListener.onErrorOccurred("Oops!! \n Error occurred");
    }


}
