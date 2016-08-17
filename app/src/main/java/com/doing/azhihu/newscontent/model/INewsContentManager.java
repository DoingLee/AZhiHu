package com.doing.azhihu.newscontent.model;

import com.doing.azhihu.data.NewsContentEntity;

/**
 * Created by Doing on 2016/8/16 0016.
 */
public interface INewsContentManager {
    void loadNewsContent(String url, OnLoadNewsContentListener listener );

    interface OnLoadNewsContentListener{
        public void onLoaded(boolean isSuccess, NewsContentEntity newsContent);
    }
}
