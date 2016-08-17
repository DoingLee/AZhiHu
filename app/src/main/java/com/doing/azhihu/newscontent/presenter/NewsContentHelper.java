package com.doing.azhihu.newscontent.presenter;

import com.doing.azhihu.data.Constants;
import com.doing.azhihu.data.NewsContentEntity;
import com.doing.azhihu.newscontent.model.INewsContentManager;
import com.doing.azhihu.newscontent.model.NewsContentManager;
import com.doing.azhihu.newscontent.ui.INewsContentView;

/**
 * Created by Doing on 2016/8/16 0016.
 */
public class NewsContentHelper implements INewsContentHelper {

    INewsContentManager newsContentManager;
    INewsContentView newsContentView;
    public NewsContentHelper(INewsContentView newsContentView){
        this.newsContentView = newsContentView;
        newsContentManager = new NewsContentManager();
    }

    @Override
    public void loadNewsContent(int newsId) {
        newsContentManager.loadNewsContent(Constants.BASE_URL + Constants.CONTENT + newsId,
                new INewsContentManager.OnLoadNewsContentListener() {
            @Override
            public void onLoaded(boolean isSuccess, NewsContentEntity newsContent) {
                newsContentView.upDateView(isSuccess,newsContent);
            }
        });
    }
}
