package com.doing.azhihu;

import android.app.Application;
import android.content.Context;

import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.cookie.store.PersistentCookieStore;

/**
 * Created by Doing on 2016/8/15 0015.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initOkHttpUtils();
    }

    public void initOkHttpUtils(){
        OkHttpUtils.init(this);

        OkHttpUtils.getInstance()
                .setCookieStore(new PersistentCookieStore());   //让框架管理cookie。cookie持久化存储，如果cookie不过期，则一直有效
//                .setCertificates();                   //信任所有证书
    }

    public static Context getContext(){
        return MyApplication.getContext();
    }
}
