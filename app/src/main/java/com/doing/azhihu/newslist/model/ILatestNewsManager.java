package com.doing.azhihu.newslist.model;

import com.doing.azhihu.data.BeforeNewsEntity;
import com.doing.azhihu.data.LatestNewsEntity;

/**
 * Created by Doing on 2016/8/15 0015.
 */
public interface ILatestNewsManager {
    void loadLatestNews(String urlString,OnLoadLatestNewsListener onLoadLatestNewsListener );
    void loadBeforeNews(String urlString, OnLoadBeforeNewsListener onLoadBeforeNewsListener);

    public interface OnLoadLatestNewsListener{
        void onLoaded(boolean isSuccess, LatestNewsEntity latestNewsEntity);
    }

    public interface OnLoadBeforeNewsListener{
        void onLoaded(boolean isSuccess, BeforeNewsEntity beforeNewsEntity);
    }
}
