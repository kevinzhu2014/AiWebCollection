package com.cocolab.common.aiwebcollection.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by zhushengui on 2017/4/19.
 */

public class PublicData {
    private static PublicData publicData;

    public static String mAppPath;

    private PublicData(){
        init();
    }

    public static PublicData getInstance(){
        if(publicData == null){
            publicData = new PublicData();
        }
        return publicData;
    }

    private void init(){
        String appDirName = "AiWebCollection";
        //if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
            mAppPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + appDirName + "/";
        /*}else{
            mAppPath = "/data/data/com.cocolab.common.aiwebcollection/files/" + appDirName + "/";
        }*/
        File appDir = new File(mAppPath);
        if(!appDir.exists()){
            appDir.mkdirs();
        }
    }
}
