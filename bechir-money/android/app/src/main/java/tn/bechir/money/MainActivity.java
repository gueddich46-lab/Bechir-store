package tn.bechir.money;
import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;

public class MainActivity extends Activity {
 private WebView web;
 @Override public void onCreate(Bundle state){
  super.onCreate(state);
  getWindow().setStatusBarColor(Color.rgb(245,248,255));
  getWindow().setNavigationBarColor(Color.rgb(245,248,255));
  getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
  FrameLayout root=new FrameLayout(this);
  root.setBackgroundColor(Color.rgb(245,248,255));
  web=new WebView(this);
  root.addView(web,new FrameLayout.LayoutParams(-1,-1));
  root.setOnApplyWindowInsetsListener((view,insets)->{
   int top=insets.getSystemWindowInsetTop();
   int bottom=insets.getSystemWindowInsetBottom();
   view.setPadding(0,top,0,bottom);
   return insets;
  });
  setContentView(root);
  root.requestApplyInsets();
  web.setWebViewClient(new WebViewClient());
  web.setWebChromeClient(new WebChromeClient());
  WebSettings settings=web.getSettings();
  settings.setJavaScriptEnabled(true);
  settings.setDomStorageEnabled(true);
  web.loadUrl("https://gueddich46-lab.github.io/Bechir-store/bechir-money/");
 }
 @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
 @Override protected void onDestroy(){if(web!=null)web.destroy();super.onDestroy();}
}
