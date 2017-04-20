package com.cocolab.common.aiwebcollection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cocolab.common.aiwebcollection.activity.BrowerActivity;
import com.cocolab.common.aiwebcollection.adapter.SubscribeListAdapter;
import com.cocolab.common.aiwebcollection.model.Subscribe;
import com.cocolab.common.aiwebcollection.pool.QDThreadPool;
import com.cocolab.common.aiwebcollection.utils.HtmlStorageHelper;
import com.cocolab.common.aiwebcollection.utils.HtmlStorageStateListener;
import com.cocolab.common.aiwebcollection.utils.PublicData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ListView listView;

    private HtmlStorageHelper helper;
    private List<Subscribe> subscribeList = new ArrayList<>();
    private SubscribeListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        helper = new HtmlStorageHelper(this);

        initView();
        loadWebCollectionData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initNavigationItems();

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(subscribeList.size() <= position){
                    return;
                }
                String contentId = subscribeList.get(position).contentId;
                if(contentId == null && "".equals(contentId)){
                    return;
                }
                //打开浏览器，浏览网页
                try {
                    String htmlFilePath = PublicData.getInstance().mAppPath + "/" + "download" + "/" + contentId + "/" + "index.html";
                    Intent intent = new Intent(MainActivity.this, BrowerActivity.class);
                    String content_url = "file://" + htmlFilePath;
                    intent.putExtra("web_url", content_url);
                    startActivity(intent);

                    /*Uri uri = Uri.parse("content://com.android.htmlfileprovider/" + htmlFilePath);
                    Intent intent2 = new Intent();
                    intent2.setData(uri);
                    intent2.setClassName("com.android.htmlviewer", "com.android.htmlviewer.HTMLViewerActivity");
                    startActivity(intent2);*/
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        int id = item.getItemId();

        if (id == 1) {
            helper.setHtmlStorageStateListener(new HtmlStorageStateListener() {
                @Override
                public void onSuccess() {
                    Snackbar.make(drawer, "保存网页成功", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                @Override
                public void onFail(String message) {
                    Snackbar.make(drawer, "保存网页失败", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
            helper.saveHtml("http://blog.csdn.net/yuyuyuyuy/article/details/6547430");
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initNavigationItems() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu rootMenu = navigationView.getMenu();
        if (rootMenu != null) {
            rootMenu.add(0, 1, 0, "保存网页");
            //rootMenu.add(0, 2, 0, "QQ主页");
        }
    }

    private void loadWebCollectionData(){
        QDThreadPool.getInstance(QDThreadPool.PRIORITY_MEDIUM).submit(new Runnable() {
            @Override
            public void run() {
                List<Subscribe> subscribes = helper.getHtmlList();
                if(subscribes != null){
                    subscribeList.clear();
                    subscribeList.addAll(subscribes);

                    adapter = new SubscribeListAdapter(MainActivity.this, subscribeList);
                    listView.setAdapter(adapter);
                }
            }
        });
    }
}
