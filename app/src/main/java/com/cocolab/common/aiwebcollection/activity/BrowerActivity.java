package com.cocolab.common.aiwebcollection.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cocolab.common.aiwebcollection.pool.QDThreadPool;
import com.cocolab.common.aiwebcollection.utils.HtmlStorageHelper;
import com.cocolab.common.aiwebcollection.utils.HtmlStorageStateListener;
import com.cocolab.common.aiwebcollection.utils.NetworkUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by zhushengui on 2017/4/20.
 */

public class BrowerActivity extends AppCompatActivity {
    private WebView webView;
    private String url;
    private HtmlStorageHelper helper;

    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        url = getIntent().getStringExtra("web_url");

        webView = new WebView(this);
        ViewGroup.LayoutParams lps = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(lps);
        initWebViewSetting();

        setContentView(webView);

        QDThreadPool.getInstance(QDThreadPool.PRIORITY_HIGH).submit(new Runnable() {
            @Override
            public void run() {
                loadWebPage();
            }
        });

        helper = new HtmlStorageHelper(this);
    }

    @Override
    public void onBackPressed(){
        if(webView.canGoBack()){
            webView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0x0001, 0, "缓存到本地");
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item != null) {
            switch(item.getItemId()){
                case android.R.id.home:// 点击返回图标事件
                    this.finish();
                    break;
                case 0x0001:
                    saveToLocal();
                    break;
            }
        }
        return false;
    }

    private void saveToLocal(){
        if(TextUtils.isEmpty(url)){
            return;
        }
        helper.setHtmlStorageStateListener(new HtmlStorageStateListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(webView, "保存网页成功", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onFail(String message) {
                Snackbar.make(webView, "保存网页失败", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        helper.saveHtml(url);
    }

    private void initWebViewSetting(){
        WebSettings webSettings = webView.getSettings();
        webView.getSettings().setDefaultTextEncodingName("utf-8");
        //webSettings.setJavaScriptEnabled(true); //支持JavaScript参数
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSupportZoom(true);  //支持放大缩小
        //webSettings.setBuiltInZoomControls(true); //显示缩放按钮

        String cacheDirPath = this.getFilesDir().getAbsolutePath() + "/" + "webcache"; //缓存路径
        if(url != null && url.startsWith("file://")) {
            int dirEndIndex = url.lastIndexOf("/");
            String WebDir = url.substring(7, dirEndIndex);
            cacheDirPath = WebDir + "/" + "webcache"; //缓存路径
        }
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //缓存模式
        webSettings.setAppCachePath(cacheDirPath); //设置缓存路径
        webSettings.setAppCacheEnabled(true); //开启缓存功能
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http://") || url.startsWith("http://")){
                    if(NetworkUtil.isNetworkAvailable(BrowerActivity.this)){
                        webView.loadUrl(url);
                    }else{
                        //不做任何处理
                    }
                }else{
                    //本地地址
                    //webView.loadUrl(url);
                    return false;
                }
                return true;
            }
        });
    }

    private void loadWebPage(){
        if(url != null && url.startsWith("file://")){
            //本地文件，读取后直接显示html内容
            String filePath = url.substring(7);
            FileInputStream fis = null;
            final StringBuffer htmlContentSB = new StringBuffer();
            try {
                fis = new FileInputStream(new File(filePath));
                byte[] buffer = new byte[1024];
                int len = -1;
                while((len = fis.read(buffer)) != -1){
                    htmlContentSB.append(new String(buffer, 0, len, "utf-8"));
                }
            } catch (FileNotFoundException  e) {
                e.printStackTrace();
            } catch (IOException  e) {
                e.printStackTrace();
            } finally{
                if(fis != null){
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadDataWithBaseURL("file://", htmlContentSB.toString(), "text/html", "utf-8", "about:blank");
                }
            });
        }else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(url);
                }
            });
        }
    }
}
