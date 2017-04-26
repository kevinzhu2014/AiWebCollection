package com.cocolab.common.aiwebcollection;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cocolab.common.aiwebcollection.activity.QRScanActivity;
import com.cocolab.common.aiwebcollection.adapter.SubscribeListAdapter;
import com.cocolab.common.aiwebcollection.model.Subscribe;
import com.cocolab.common.aiwebcollection.pool.QDThreadPool;
import com.cocolab.common.aiwebcollection.utils.ActionUtilProcess;
import com.cocolab.common.aiwebcollection.utils.HtmlStorageHelper;
import com.cocolab.common.aiwebcollection.utils.PublicData;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int MSG_WHAT_REFRESH_LIST = 1;
    private static final int MSG_WHAT_RELOAD_LIST = 2;

    private ListView listView;

    private HtmlStorageHelper helper;
    private List<Subscribe> subscribeList = new ArrayList<>();
    private SubscribeListAdapter adapter;

    private static WeakHandler mHandler;

    class WeakHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_WHAT_REFRESH_LIST:
                    if(adapter == null) {
                        adapter = new SubscribeListAdapter(MainActivity.this, subscribeList);
                        listView.setAdapter(adapter);
                    }else{
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case MSG_WHAT_RELOAD_LIST:
                    reloadWebCollectionData();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new WeakHandler();
        helper = new HtmlStorageHelper(this);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloadWebCollectionData();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打开扫描
                startActivity(new Intent(MainActivity.this, QRScanActivity.class));
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
                String htmlFilePath = PublicData.getInstance().mAppPath + "/" + "download" + "/" + contentId + "/" + "index.html";
                ActionUtilProcess.openUrl(MainActivity.this, "file://" + htmlFilePath);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if(subscribeList.size() <= position){
                    return false;
                }
                confirmDelete(position);

                return true;
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
        //getMenuInflater().inflate(R.menu.main, menu);
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

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initNavigationItems() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu rootMenu = navigationView.getMenu();
        if (rootMenu != null) {
            rootMenu.add(1, 1, 0, "我的主页");
            rootMenu.add(2, 2, 1, "关于我们");
        }
    }

    private void reloadWebCollectionData(){
        QDThreadPool.getInstance(QDThreadPool.PRIORITY_MEDIUM).submit(new Runnable() {
            @Override
            public void run() {
                List<Subscribe> subscribes = helper.getHtmlList();
                if(subscribes != null){
                    subscribeList.clear();
                    subscribeList.addAll(subscribes);
                    mHandler.sendEmptyMessage(MSG_WHAT_REFRESH_LIST);
                }
            }
        });
    }

    private void confirmDelete(final int position){
        if(position > subscribeList.size() -1){
            return;
        }
        final Subscribe subscribe = subscribeList.get(position);
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setTitle("确认删除");
        builder.setMessage(subscribe.title);
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                QDThreadPool.getInstance(QDThreadPool.PRIORITY_MEDIUM).submit(new Runnable() {
                    @Override
                    public void run() {
                        helper.deleteHtml(subscribe.contentId);
                        //删除后更新UI
                        subscribeList.remove(position);
                        mHandler.sendEmptyMessage(MSG_WHAT_REFRESH_LIST);
                    }
                });
            }
        });
        builder.show();
    }
}
