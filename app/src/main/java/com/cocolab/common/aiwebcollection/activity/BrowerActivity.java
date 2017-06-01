package com.cocolab.common.aiwebcollection.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.cocolab.common.aiwebcollection.R;
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

public class BrowerActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String url;
    private HtmlStorageHelper helper;
    private ActionBar actionBar;

    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        url = getIntent().getStringExtra("web_url");

        setContentView(R.layout.activity_brower);
        initViews();
        initWebViewSetting();

        helper = new HtmlStorageHelper(this);

        loadWebPageAsync();
    }

    private void initViews(){
        mWebView = (WebView) this.findViewById(R.id.webView);
        mProgressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        mSwipeRefreshLayout = ((SwipeRefreshLayout)this.findViewById(R.id.swipeRefreshLayout));
        mSwipeRefreshLayout.setColorSchemeColors(this.getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    private void loadWebPageAsync(){
        QDThreadPool.getInstance(QDThreadPool.PRIORITY_HIGH).submit(new Runnable() {
            @Override
            public void run() {
                loadWebPage();
            }
        });
    }

    @Override
    public void onBackPressed(){
        if(mWebView.canGoBack()){
            mWebView.goBack();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0x0001, 0, "缓存到本地");
        menu.add(0, 0x0002, 1, "扫描二维码");
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
                case 0x0002:
                    //打开扫描
                    startActivity(new Intent(BrowerActivity.this, QRScanActivity.class));
                    break;
            }
        }
        return false;
    }

    private void saveToLocal(){
        String nowUrl = mWebView.getUrl();
        if(TextUtils.isEmpty(nowUrl)){
            return;
        }
        helper.setHtmlStorageStateListener(new HtmlStorageStateListener() {
            @Override
            public void onSuccess() {
                Snackbar.make(mWebView, "保存网页成功", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onFail(String message) {
                Snackbar.make(mWebView, "保存网页失败", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        helper.saveHtml(nowUrl);
    }

    private void initWebViewSetting(){
        WebSettings webSettings = mWebView.getSettings();
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true); //支持JavaScript参数
        webSettings.setBuiltInZoomControls(false); // 放大缩放按钮
        // 如果访问的页面中有JavaScript，则WebView必须设置支持JavaScript
        webSettings.setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webSettings.setSupportZoom(true);
        // 扩大比例的缩放
        webSettings.setUseWideViewPort(true);
        // 自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setLoadWithOverviewMode(true);

        setWebViewCache();

        mWebView.setWebChromeClient(new MyChromeClient());

        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.startsWith("http://") || url.startsWith("http://")){
                    if(NetworkUtil.isNetworkAvailable(BrowerActivity.this)){
                        mWebView.loadUrl(url);
                    }else{
                        //不做任何处理
                    }
                }else{
                    //本地地址
                    //mWebView.loadUrl(url);
                    return false;
                }
                return true;
            }
        });
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        loadWebPageAsync();
    }

    // 设置加载进度
    public class MyChromeClient extends android.webkit.WebChromeClient
    {
        @Override
        public void onProgressChanged(WebView view, int newProgress)
        {
            mProgressBar.setProgress(newProgress);
            if (newProgress == 100)
            {
                mProgressBar.setVisibility(View.GONE);
            }
            else
            {
                mProgressBar.setVisibility(View.VISIBLE);

            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title)
        {
            super.onReceivedTitle(view, title);
            if(actionBar != null){
                actionBar.setTitle(title);
            }
        }

    }

    /**
     * 设置WebView缓存参数
     */
    private void setWebViewCache(){
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        if (NetworkUtil.isNetworkAvailable(this))
        {
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);// 根据cache-control决定是否从网络上取数据。
        }
        else
        {
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据
        }
        String cacheDirPath = this.getFilesDir().getAbsolutePath() + "/" + "webcache"; //缓存路径
        if(url != null && url.startsWith("file://")) {
            int dirEndIndex = url.lastIndexOf("/");
            String WebDir = url.substring(7, dirEndIndex);
            cacheDirPath = WebDir + "/" + "webcache"; //缓存路径
        }
        webSettings.setAppCachePath(cacheDirPath); //设置缓存路径
        webSettings.setAppCacheEnabled(true); //开启缓存功能

        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        // 开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);
        // 设置数据库缓存路径
        webSettings.setDatabasePath(cacheDirPath);
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
                    mSwipeRefreshLayout.setRefreshing(false);
                    mWebView.loadDataWithBaseURL("file://", htmlContentSB.toString(), "text/html", "utf-8", "about:blank");
                }
            });
        }else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mWebView.loadUrl(url);
                }
            });
        }
    }
}
