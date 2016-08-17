package com.doing.azhihu.newscontent.model;

import com.doing.azhihu.data.NewsContentEntity;
import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Doing on 2016/8/16 0016.
 */
public class NewsContentManager implements INewsContentManager {

    public NewsContentManager(){

    }

    @Override
    public void loadNewsContent(String url, final OnLoadNewsContentListener listener) {
        OkHttpUtils.get(url)     // 请求方式和请求url
                .tag(this)      // 请求的 tag, 主要用于取消对应的请求
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(boolean isFromCache, String result, Request request, Response response) {
                        NewsContentEntity newsContentEntity = parseNewsContentJson(result);
                        listener.onLoaded(true, newsContentEntity);
//                        LogUtils.i(result.toString());
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, Response response, Exception e) {
                        super.onError(isFromCache, call, response, e);
                        listener.onLoaded(false, null);
//                        LogUtils.i("LatestNewsManager: load fail!!");
                    }
                });
    }

    private NewsContentEntity parseNewsContentJson(String result){
        Gson gson = new Gson();
        return gson.fromJson(result, NewsContentEntity.class);
    }
}
