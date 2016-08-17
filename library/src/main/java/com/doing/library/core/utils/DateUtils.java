/**
 * Copyright 2014 Zhenguo Jin
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.doing.library.core.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *日期工具类
 *
 * created by Doing
 */

/*
     System.currentTimeMillis(); // 返回long类型：the current time in milliseconds since January 1, 1970 00:00:00.0 UTC.
     Date date = new Date(System.currentTimeMillis());

     注意：以下使用long类型表示时间的均默认为the current time in milliseconds since January 1, 1970 00:00:00.0 UTC.

     获取当前系统时间：
     public static String getSystemTime(String datePattern)
     public static Date getSystemTime()

     Date、Millis（long类型time）、String之间的的转换：
     public static String dateToString(Date date, String pattern)
     public static Date stringToDate(String dateStr, String pattern)
     public static Date millisToDate(long time)
     public static String millisToDateString(long time, String pattern)
     public static long dateStringToMillis(String dateStr, String pattern)

     从日期中获取具体域：
     public static int getYear(Date date)
     public static int getMonth(Date date)
     public static int getDay(Date date)

     转换为日常交流表达时间：
     public static String transformToEasyDay(long time)
     public static String transformToEasyMinute(long time)

 */

public final class DateUtils {

    /**
     * 日期pattern类型
     */
    public static final String yyyyMMDD = "yyyy-MM-dd";
    public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
    public static final String HHmmss = "HH:mm:ss";
    public static final String LOCALE_DATE_FORMAT = "yyyy年M月d日 HH:mm:ss";
    public static final String DB_DATA_FORMAT = "yyyy-MM-DD HH:mm:ss";
    public static final String NEWS_ITEM_DATE_FORMAT = "hh:mm M月d日 yyyy";

    /**
     * 获取当前系统时间
     * @param datePattern 需要输出的日期格式, eg."yyyy-MM-dd HH:mm:ss"
     * @return 当前系统时间
     */
    public static String getSystemTime(String datePattern){
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        return sdf.format(new Date(System.currentTimeMillis()));
    }

    public static Date getSystemTime(){
        return new Date(System.currentTimeMillis());
    }

    /**
     * 将Date类型转换为日期字符串
     *
     * @param date Date对象，通常是new Date(System.currentTimeMillis();
     * @param pattern 需要的日期格式，eg. "yyyy-MM-dd HH:mm:ss"
     * @return 按照需求格式的日期字符串
     */
    public static String dateToString(Date date, String pattern) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            return df.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将日期字符串转换为Date类型
     *
     * @param dateStr 日期字符串  eg. "2016-08-01 08:00:00"
     * @param pattern    日期字符串格式  eg. "yyyy-MM-dd HH:mm:ss"
     * @return Date对象
     */
    public static Date stringToDate(String dateStr, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;

    }

    /**
     *
     * @param dateStr  日期字符串  eg. "2016-08-01 08:00:00"
     * @param pattern  日期字符串格式  eg. "yyyy-MM-dd HH:mm:ss"
     * @return  The  number of milliseconds since Jan. 1, 1970, midnight GMT.
     */
    public static long dateStringToMillis(String dateStr, String pattern){
        Date date = stringToDate(dateStr,pattern);
        return date.getTime();
    }

    public static Date millisToDate(long time){
        Date date = new Date(time);
        return date;
    }

    public static String millisToDateString(long time, String pattern){
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return  sdf.format(new Date(time));
    }

    /**
     * 得到年
     *
     * @param date Date对象
     * @return 年
     */
    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.YEAR);
    }

    /**
     * 得到月
     *
     * @param date Date对象
     * @return 月
     */
    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) + 1;

    }

    /**
     * 得到日
     *
     * @param date Date对象
     * @return 日
     */
    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 将time in milliseconds since January 1, 1970 00:00:00.0 UTC
     * 转为更容易理解的方式：今天, 昨天, 前天, 明天，后天，XXXX-XX-XX, ...
     *
     * @param time 需要转换的时间， in milliseconds since January 1, 1970 00:00:00.0 UTC.
     * @return 转换成：今天 / 昨天 / 前天/ 明天 / 后天 / XXXX-XX-XX, ...
     */
    public static String transformToEasyDay(long time) {
        long oneDay = 24 * 60 * 60 * 1000;
        Calendar current = Calendar.getInstance();
        Calendar today = Calendar.getInstance();    //今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        long todayStartTime = today.getTimeInMillis(); //今天的00：00：00的Time： the number of milliseconds since Jan. 1, 1970, midnight GMT

        if (time >= todayStartTime && time < todayStartTime + oneDay) { // today
            return "今天";
        } else if (time >= todayStartTime - oneDay && time < todayStartTime) { // yesterday
            return "昨天";
        } else if (time >= todayStartTime - oneDay * 2 && time < todayStartTime - oneDay) { // the day before yesterday
            return "前天";
        } else if (time > todayStartTime + oneDay){
            return "明天";
        } else if (time > todayStartTime + oneDay * 2) { // future
            return "后天";
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date(time);
            return dateFormat.format(date);
        }
    }

    /**
     *
     * @param time  需要转换的时间， in milliseconds since January 1, 1970 00:00:00.0 UTC.
     * @return  转换成：n分钟前 / 今天 HH:mm / 昨天 HH:mm  / 前天 HH:mm / yyyy-MM-dd HH:mm
     */
    public static String transformToEasyMinute(long time) {
        long oneDay = 24 * 60 * 60 * 1000;
        long curTime = System.currentTimeMillis();

        Calendar today = Calendar.getInstance();    //今天
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        long todayStartTime = today.getTimeInMillis();

        if (time >= todayStartTime) {
            long d = (curTime - time) / 1000;  //毫秒转为秒
            if (d <= 60) {
                return "1分钟前";
            } else if (d <= 60 * 60) {
                long m = d / 60;
                if (m <= 0)  m = 1;
                return m + "分钟前";
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("今天 HH:mm");
                Date date = new Date(time);
                String dateStr = dateFormat.format(date);
//                if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
//                    dateStr = dateStr.replace(" 0", " ");
//                }
                return dateStr;
            }
        } else {
            if (time < todayStartTime && time > todayStartTime - oneDay) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("昨天 HH:mm");
                Date date = new Date(time);
                String dateStr = dateFormat.format(date);
//                if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
//                    dateStr = dateStr.replace(" 0", " ");
//                }
                return dateStr;
            } else if (time < todayStartTime - oneDay && time > todayStartTime - 2 * oneDay) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("前天 HH:mm");
                Date date = new Date(time);
                String dateStr = dateFormat.format(date);
//                if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
//                    dateStr = dateStr.replace(" 0", " ");
//                }
                return dateStr;
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = new Date(time);
                String dateStr = dateFormat.format(date);
//                if (!TextUtils.isEmpty(dateStr) && dateStr.contains(" 0")) {
//                    dateStr = dateStr.replace(" 0", " ");
//                }
                return dateStr;
            }
        }
    }

}
