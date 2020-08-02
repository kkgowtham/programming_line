package com.programming.line;

public interface WebViewListener{
    void pageStarted();
    void pageFinished();
    void onErrorOccurred(String error);
}