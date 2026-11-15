package com.bechir.mushaf;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings;
import android.webkit.WebResourceRequest;
import android.content.Intent;
import android.net.Uri;
import android.graphics.Color;
public class MainActivity extends Activity {
 WebView web;
 @Override public void onCreate(Bundle b) {
 super.onCreate(b);
 getWindow().setStatusBarColor(Color.rgb(8,34,29));
 getWindow().setNavigationBarColor(Color.rgb(8,34,29));
 getWindow().getDecorView().setSystemUiVisibility(0);
 web=new WebView(this);setContentView(web);
 WebSettings settings=web.getSettings();settings.setJavaScriptEnabled(true);settings.setDomStorageEnabled(true);settings.setAllowFileAccess(false);settings.setCacheMode(WebSettings.LOAD_DEFAULT);settings.setMediaPlaybackRequiresUserGesture(false);
 web.setWebChromeClient(new WebChromeClient());
 web.setWebViewClient(new WebViewClient(){@Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){Uri uri=request.getUrl();if("gueddich46-lab.github.io".equals(uri.getHost()))return false;startActivity(new Intent(Intent.ACTION_VIEW,uri));return true;}});
 web.loadUrl("https://gueddich46-lab.github.io/Bechir-store/bechir-mushaf-test/?app=android1");
 }
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}