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
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
public class MainActivity extends Activity {
 WebView web;
 private final ExecutorService downloads=Executors.newSingleThreadExecutor();
 private volatile boolean downloading=false;
 private File audioDir;
 private static final String AUDIO_HOST="bechir-audio.local";
 private File audioFile(int surah,int ayah){return new File(audioDir,String.format(java.util.Locale.US,"%03d%03d.mp3",surah,ayah));}
 private boolean valid(int s,int a){return s>=1&&s<=114&&a>=1&&a<=286;}
 private void js(String code){runOnUiThread(()->web.evaluateJavascript(code,null));}
 public class AudioBridge {
  @JavascriptInterface public boolean has(int s,int a){return valid(s,a)&&audioFile(s,a).length()>1024;}
  @JavascriptInterface public boolean busy(){return downloading;}
  @JavascriptInterface public void downloadSurah(int s,int count){
   if(s<1||s>114||count<1||count>286||downloading)return;
   downloading=true;
   downloads.execute(()->{
    int done=0;
    try{
     for(int a=1;a<=count;a++){
      File dest=audioFile(s,a);
      if(dest.length()<1024){
       File tmp=new File(audioDir,dest.getName()+".part");
       URL url=new URL("https://everyayah.com/data/Husary_128kbps/"+String.format(java.util.Locale.US,"%03d%03d.mp3",s,a));
       HttpURLConnection con=(HttpURLConnection)url.openConnection();
       con.setConnectTimeout(20000);con.setReadTimeout(40000);
       try{
        if(con.getResponseCode()!=200)throw new IOException("HTTP "+con.getResponseCode());
        try(InputStream in=con.getInputStream();OutputStream out=new FileOutputStream(tmp)){
         byte[] buffer=new byte[16384];int n;while((n=in.read(buffer))!=-1)out.write(buffer,0,n);
        }
        if(tmp.length()<1024||!tmp.renameTo(dest))throw new IOException("Audio save failed");
       }finally{con.disconnect();tmp.delete();}
      }
      done++;
      js("window.audioDownloadProgress&&window.audioDownloadProgress("+s+","+done+","+count+",'')");
     }
    }catch(Exception e){
     String msg=org.json.JSONObject.quote(e.getMessage()==null?"تعذر التنزيل":e.getMessage());
     js("window.audioDownloadProgress&&window.audioDownloadProgress("+s+","+done+","+count+","+msg+")");
    }finally{downloading=false;}
   });
  }
 }

 @Override public void onCreate(Bundle b) {
 super.onCreate(b);
 getWindow().setStatusBarColor(Color.rgb(8,34,29));
 getWindow().setNavigationBarColor(Color.rgb(8,34,29));
 getWindow().getDecorView().setSystemUiVisibility(0);
 audioDir=new File(getFilesDir(),"husary-audio");audioDir.mkdirs();
 web=new WebView(this);setContentView(web);
 web.addJavascriptInterface(new AudioBridge(),"BechirAudio");
 WebSettings settings=web.getSettings();settings.setJavaScriptEnabled(true);settings.setDomStorageEnabled(true);settings.setAllowFileAccess(true);settings.setCacheMode(WebSettings.LOAD_DEFAULT);settings.setMediaPlaybackRequiresUserGesture(false);
 web.setWebChromeClient(new WebChromeClient());
 web.setWebViewClient(new WebViewClient(){
  @Override public WebResourceResponse shouldInterceptRequest(WebView view,WebResourceRequest request){
   Uri uri=request.getUrl();
   if("https".equals(uri.getScheme())&&AUDIO_HOST.equals(uri.getHost())){
    try{
     String name=uri.getLastPathSegment();
     if(name==null||!name.matches("[0-9]{6}\\.mp3"))return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));
     File file=new File(audioDir,name);
     if(file.length()>1024)return new WebResourceResponse("audio/mpeg",null,new FileInputStream(file));
    }catch(Exception ignored){}
    return new WebResourceResponse("text/plain","UTF-8",new ByteArrayInputStream(new byte[0]));
   }
   return null;
  }@Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){Uri uri=request.getUrl();if("file".equals(uri.getScheme()))return false;startActivity(new Intent(Intent.ACTION_VIEW,uri));return true;}});
 web.loadUrl("file:///android_asset/index.html");
 }
 @Override protected void onDestroy(){downloads.shutdownNow();web.destroy();super.onDestroy();}
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
}