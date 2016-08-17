package com.doing.azhihu.newslist.model;

import com.doing.azhihu.data.BeforeNewsEntity;
import com.doing.azhihu.data.LatestNewsEntity;
import com.google.gson.Gson;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Doing on 2016/8/15 0015.
 */
public class LatestNewsManager implements ILatestNewsManager {

    private OnLoadLatestNewsListener onLoadLatestNewsListener;
    private OnLoadBeforeNewsListener onLoadBeforeNewsListener;

    public LatestNewsManager(){

    }

    public void loadLatestNews(String url, OnLoadLatestNewsListener listener){
        onLoadLatestNewsListener = listener;
        OkHttpUtils.get(url)     // 请求方式和请求url
                .tag(this)      // 请求的 tag, 主要用于取消对应的请求
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(boolean isFromCache, String result, Request request, Response response) {
                        LatestNewsEntity latestNewsEntity = parseLatestNewsJson(result);
                        onLoadLatestNewsListener.onLoaded(true, latestNewsEntity);
//                        LogUtils.i(result.toString());
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, Response response, Exception e) {
                        super.onError(isFromCache, call, response, e);
                        onLoadLatestNewsListener.onLoaded(false, null);
//                        LogUtils.i("LatestNewsManager: load fail!!");
                    }
                });
    }

    @Override
    public void loadBeforeNews(String url, OnLoadBeforeNewsListener listener) {
        onLoadBeforeNewsListener = listener;
        OkHttpUtils.get(url)     // 请求方式和请求url
                .tag(this)      // 请求的 tag, 主要用于取消对应的请求
                .execute(new StringCallback() {
                    @Override
                    public void onResponse(boolean isFromCache, String result, Request request, Response response) {
                        BeforeNewsEntity beforeNewsEntity = parseBeforeNewsJson(result);
                        onLoadBeforeNewsListener.onLoaded(true, beforeNewsEntity);
//                        LogUtils.i(result.toString());
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, Response response, Exception e) {
                        super.onError(isFromCache, call, response, e);
                        onLoadBeforeNewsListener.onLoaded(false, null);
//                        LogUtils.i("LatestNewsManager: load fail!!");
                    }
                });
    }

    private LatestNewsEntity parseLatestNewsJson(String result){
        Gson gson = new Gson();
        return gson.fromJson(result, LatestNewsEntity.class);
    }

    private BeforeNewsEntity parseBeforeNewsJson(String result){
        Gson gson = new Gson();
        return gson.fromJson(result, BeforeNewsEntity.class);
    }

}
