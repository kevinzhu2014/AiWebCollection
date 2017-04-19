package com.cocolab.common.aiwebcollection.model;

/**
 * Created by zhushengui on 2017/4/19.
 */

public class Subscribe {
    public static final int FILE_DOWNLOADED = 1;

    public String contentId;
    public String title;
    public int type;

    public Subscribe(String contentId, String title, int type){
        this.contentId = contentId;
        this.title = title;
        this.type = type;
    }
}
