package com.cocolab.common.aiwebcollection.utils;

/**
 * Created by zhushengui on 2017/4/19.
 */

public abstract class HtmlStorageStateListener {
    public abstract void onSuccess();
    public void showDialog(int type){}
    public void onFail(String message){}
}
