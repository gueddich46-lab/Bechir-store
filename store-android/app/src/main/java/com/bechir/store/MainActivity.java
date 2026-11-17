package com.bechir.store;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.graphics.Color;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.widget.Toast;
import android.view.View;
import android.view.WindowInsets;
public class MainActivity extends Activity {
 private WebView web;
 private static final String STORE="https://gueddich46-lab.github.io/Bechir-store/";
 @Override public void onCreate(Bundle state){super.onCreate(state);
 getWindow().setStatusBarColor(Color.WHITE);getWindow().getDecorView().setSystemUiVisibility(android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
 web=new WebView(this);
 // Android 15/16 edge-to-edge: keep the WebView content outside status/navigation bars.
 web.setFitsSystemWindows(false);
 web.setOnApplyWindowInsetsListener((view, insets) -> {
   android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars() | WindowInsets.Type.displayCutout());
   view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
   return insets;
 });
 setContentView(web);
 web.requestApplyInsets();
 web.setWebChromeClient(new WebChromeClient());
 web.setWebViewClient(new WebViewClient(){
  @Override public void onPageFinished(WebView view,String url){
   view.evaluateJavascript("document.documentElement.classList.remove('android-embedded')",null);
  }
  @Override public boolean shouldOverrideUrlLoading(WebView view,String url){return handle(url);}
  @Override public boolean shouldOverrideUrlLoading(WebView view,android.webkit.WebResourceRequest request){return handle(request.getUrl().toString());}
 });
 WebSettings s=web.getSettings();s.setJavaScriptEnabled(true);s.setDomStorageEnabled(true);s.setCacheMode(WebSettings.LOAD_NO_CACHE);
 web.clearCache(true);
 web.setDownloadListener((url,ua,disposition,mime,length)->download(url,ua,disposition,mime));
 web.loadUrl(STORE+"?app_refresh=20261117");
 }
 private boolean handle(String url){
  if(isApkDownload(url)){download(url,null,null,"application/vnd.android.package-archive");return true;}
  Uri uri=Uri.parse(url);
  if("https".equals(uri.getScheme())||"http".equals(uri.getScheme())){
   if("gueddich46-lab.github.io".equalsIgnoreCase(uri.getHost())&&uri.getPath()!=null&&uri.getPath().startsWith("/Bechir-store/"))return false;
   try{startActivity(new Intent(Intent.ACTION_VIEW,uri));}catch(Exception e){Toast.makeText(this,"تعذر فتح الرابط",Toast.LENGTH_LONG).show();}
   return true;
  }
  return false;
 }
 private boolean isApkDownload(String url){
  Uri uri=Uri.parse(url);
  String path=uri.getPath()==null?"":uri.getPath().toLowerCase(java.util.Locale.ROOT);
  String host=uri.getHost()==null?"":uri.getHost().toLowerCase(java.util.Locale.ROOT);
  return "https".equalsIgnoreCase(uri.getScheme()) && (path.endsWith(".apk") || ((host.equals("github.com") || host.equals("www.github.com")) && path.contains("/releases/download/")));
 }
 private void download(String url,String ua,String disposition,String mime){
  try{
   if(!url.startsWith("https://"))throw new IllegalArgumentException("Only HTTPS downloads allowed");
   String filename=URLUtil.guessFileName(url,disposition,"application/vnd.android.package-archive");
   if(!filename.toLowerCase(java.util.Locale.ROOT).endsWith(".apk"))filename="application.apk";
   DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
   request.setTitle(filename);request.setDescription("BECHIR STORE");
   request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
   request.setMimeType("application/vnd.android.package-archive");
   if(ua!=null)request.addRequestHeader("User-Agent",ua);
   String cookies=CookieManager.getInstance().getCookie(url);if(cookies!=null&&!cookies.isEmpty())request.addRequestHeader("Cookie",cookies);
   request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,filename);
   ((DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
   Toast.makeText(this,"بدأ تنزيل "+filename,Toast.LENGTH_LONG).show();
  }catch(Exception e){Toast.makeText(this,"تعذر بدء التنزيل: "+e.getMessage(),Toast.LENGTH_LONG).show();}
 }
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
 @Override protected void onDestroy(){web.destroy();super.onDestroy();}
}
