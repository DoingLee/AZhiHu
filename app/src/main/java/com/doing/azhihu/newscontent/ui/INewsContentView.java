package com.doing.azhihu.newscontent.ui;

import com.doing.azhihu.data.NewsContentEntity;

/**
 * Created by Doing on 2016/8/16 0016.
 *
 * 供presenter调用
 */
public interface INewsContentView {
    void upDateView(boolean isSuccess, NewsContentEntity newsContent);
}
