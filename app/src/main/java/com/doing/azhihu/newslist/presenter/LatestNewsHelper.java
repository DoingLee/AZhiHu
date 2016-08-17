package com.doing.azhihu.newslist.presenter;

import com.doing.azhihu.data.BeforeNewsEntity;
import com.doing.azhihu.data.Constants;
import com.doing.azhihu.data.LatestNewsEntity;
import com.doing.azhihu.newslist.model.ILatestNewsManager;
import com.doing.azhihu.newslist.model.LatestNewsManager;
import com.doing.azhihu.newslist.ui.ILatestNewsView;

/**
 * Created by Doing on 2016/8/15 0015.
 */
public class LatestNewsHelper implements ILatestNewsHelper {

    private ILatestNewsView latestNewsView;
    private ILatestNewsManager latestNewsManager;

    public LatestNewsHelper(ILatestNewsView context){
        this.latestNewsView = context;

        latestNewsManager = new LatestNewsManager();
    }

    public void loadLatestNews(){
        latestNewsManager.loadLatestNews(Constants.BASE_URL + Constants.LATEST_NEWS,
                new ILatestNewsManager.OnLoadLatestNewsListener() {
                    @Override
                    public void onLoaded(boolean isSuccess, LatestNewsEntity latestNewsEntity) {
                        latestNewsView.upDateView(isSuccess, latestNewsEntity);
                    }
                });
    }

    @Override
    public void loadBeforeNews(String date) {
        latestNewsManager.loadBeforeNews(Constants.BASE_URL + Constants.BEFORE + date,
                new ILatestNewsManager.OnLoadBeforeNewsListener() {
                    @Override
                    public void onLoaded(boolean isSuccess, BeforeNewsEntity beforeNewsEntity) {
                        latestNewsView.loadMoreView(isSuccess,beforeNewsEntity);
                    }
                });
    }

}
