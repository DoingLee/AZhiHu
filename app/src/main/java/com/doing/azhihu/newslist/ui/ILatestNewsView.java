package com.doing.azhihu.newslist.ui;

import com.doing.azhihu.data.BeforeNewsEntity;
import com.doing.azhihu.data.LatestNewsEntity;

/**
 * Created by Doing on 2016/8/15 0015.
 */
public interface ILatestNewsView {
    // 由presenter调用更新UI
    void upDateView(boolean isSuccess, LatestNewsEntity latestNewsEntity);
    void loadMoreView(boolean isSuccess, BeforeNewsEntity beforeNewsEntity);
}
