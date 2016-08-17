package com.doing.azhihu.util;

import android.util.Log;

/**
 * Created by Doing on 2016/7/25 0025.
 *
 * Log.v() ：VERBOSE
 * Log.d() ：DEBUG
 * Log.i() ：INFO
 * Log.w() ：WARN
 * Log.e() ：ERROR
 */

/**
 public static void i(Class<?> clz, boolean hasAutoautoTag, String msg)
 public static void d(Class<?> clz, boolean hasAutoautoTag, String msg)
 public static void e(Class<?> clz, boolean hasAutoautoTag, String msg)
 public static void v(Class<?> clz, boolean hasAutoautoTag, String msg)

 public static void i(String msg)
 public static void d(String msg)
 public static void e(String msg)
 public static void v(String msg)

 public static void i(String autoTag, String msg)
 public static void d(String autoTag, String msg)
 public static void e(String autoTag, String msg)
 public static void v(String autoTag, String msg)
 */

public class LogUtils {

    private LogUtils()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean isDebug = true;    // 是否需要打印bug，可以在application的onCreate函数里面初始化
    public static String autoTag = "kkkkk";   //可以自定义设置（主要是方便快速搜索到自己定义的Log信息）


    // ******************************下面四个是类名 + 可选择是否添加默认autoTag 的函数*********************************************/

    public static void i(Class<?> clz, boolean hasAutoautoTag, String msg)
    {
        if (isDebug){
            if(hasAutoautoTag == true)
                Log.i(autoTag + ' ' + clz.getSimpleName(), msg);
            else
                Log.i(clz.getSimpleName(), msg);
        }

    }

    public static void d(Class<?> clz, boolean hasAutoautoTag, String msg)
    {
        if (isDebug){
            if(hasAutoautoTag == true)
                Log.d(autoTag + ' ' + clz.getSimpleName(), msg);
            else
                Log.d(clz.getSimpleName(), msg);
        }

    }

    public static void e(Class<?> clz, boolean hasAutoautoTag, String msg)
    {
        if (isDebug){
            if(hasAutoautoTag == true)
                Log.e(autoTag + ' '+ clz.getSimpleName(), msg);
            else
                Log.e( clz.getSimpleName(), msg);
        }

    }

    public static void v(Class<?> clz, boolean hasAutoautoTag, String msg)
    {
        if (isDebug){
            if(hasAutoautoTag == true)
                Log.v(autoTag + ' '+ clz.getSimpleName(), msg);
            else
                Log.v(clz.getSimpleName(), msg);
        }

    }

    //**********************************************************下面四个是只带默认autoTag的函数********************************/

    public static void i(String msg)
    {
        if (isDebug)
            Log.i(autoTag, msg);
    }

    public static void d(String msg)
    {
        if (isDebug)
            Log.d(autoTag, msg);
    }

    public static void e(String msg)
    {
        if (isDebug)
            Log.e(autoTag, msg);
    }

    public static void v(String msg)
    {
        if (isDebug)
            Log.v(autoTag, msg);
    }


    //*************************************************下面是传入自定义autoTag的函数*********************************************/

    public static void i(String autoTag, String msg)
    {
        if (isDebug)
            Log.i(autoTag, msg);
    }

    public static void d(String autoTag, String msg)
    {
        if (isDebug)
            Log.i(autoTag, msg);
    }

    public static void e(String autoTag, String msg)
    {
        if (isDebug)
            Log.i(autoTag, msg);
    }

    public static void v(String autoTag, String msg)
    {
        if (isDebug)
            Log.i(autoTag, msg);
    }

}

