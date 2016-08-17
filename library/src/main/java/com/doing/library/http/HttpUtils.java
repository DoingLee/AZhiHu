package com.doing.library.http;

import android.content.Context;
import android.support.annotation.Nullable;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.lzy.okhttputils.request.BaseRequest;

import java.util.Objects;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Doing on 2016/8/9 0009.
 *
 * 对OkHttpUtils（https://github.com/jeasonlzy0216/OkHttpUtils）的封装
 */

/*
在Application中初始化initOkHttpUtils：

    protected void initOkHttpUtils(){
        OkHttpUtils.init(this);
        OkHttpUtils.getInstance()
                .debug("OkHttpUtils")                                              //是否打开调试
                .setConnectTimeout(OkHttpUtils.DEFAULT_MILLISECONDS)               //全局的连接超时时间
                .setReadTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)                  //全局的读取超时时间
                .setWriteTimeOut(OkHttpUtils.DEFAULT_MILLISECONDS)              //全局的写入超时时间
//                .setCookieStore(new MemoryCookieStore())                           //cookie使用内存缓存（app退出后，cookie消失）
                .setCookieStore(new PersistentCookieStore())  ;                     //cookie持久化存储，如果cookie不过期，则一直有效
//                .addCommonHeaders(headers)                                         //设置全局公共头
//                .addCommonParams(params)                                          //设置全局公共参数
    }

 */

/*
    使用：
    回调接口：HttpCallBack

    简单的get请求：
    public static void httpGetString(String url, Object cancelTag, final HttpCallBack httpCallBack)

    取消请求：
    public static void cancelWithTag(Object cancelTag)
 */
public class HttpUtils
{
    public static void getString(String url, Object cancelTag, final HttpCallBack httpCallBack){
        OkHttpUtils.get(url)     // 请求方式和请求url
                .tag(cancelTag)                       // 请求的 tag, 主要用于取消对应的请求
//                .cacheKey("cacheKey")            // 设置当前请求的缓存key,建议每个不同功能的请求设置一个
//                .cacheMode(CacheMode.DEFAULT)    // 缓存模式，详细请看缓存介绍
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(BaseRequest request) {
                        super.onBefore(request);
                        httpCallBack.onBefore();
                    }

                    @Override
                    public void onResponse(boolean isFromCache, String string, Request request, @Nullable Response response) {
                        httpCallBack.onResponse(string);
                    }

                    @Override
                    public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
                        super.onError(isFromCache, call, response, e);
                        httpCallBack.onError();
                    }
                });
    }

    /**
     * 根据 Tag 取消请求
     * @param cancelTag 请求时传入的Tag
     */
    public static void cancelWithTag(Object cancelTag){
        OkHttpUtils.getInstance().cancelTag(cancelTag);
    }

    /**
     * Http请求回调接口
     */
    public interface HttpCallBack{
        //以下回调均在UI线程
        void onBefore(); //网络请求真正执行前回调
        void onResponse(String response); //网络请求成功的回调
        void onError();//网络请求失败的回调
    }
}
