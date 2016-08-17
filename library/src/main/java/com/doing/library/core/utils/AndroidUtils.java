package com.doing.library.core.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.doing.library.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

/**
可以代替Activity基类中的函数方法

跟Android系统相关的一些工具类，包括文件路径处理，Toast显示，屏幕方向，组件启用禁用，获取App签名信息等，主要方法如下：

     // 是否是合法的文件名
     public static boolean isFilenameSafe(File file)
     // 获取Cache目录
     public static File getCacheDir(Context context)
     // 根据Uri获取真实文件路径
     public static String getPath(final Context context, final Uri uri)
     // SD卡存储空间判断
     public static boolean noSdcard()
     public static long getFreeSpace()
     public static boolean isMediaMounted()
     // 键盘隐藏与显示
     public static void hideSoftKeyboard(Context context, EditText editText)
     public static void showSoftKeyboard(Context context, EditText editText)
     public static void toggleSoftInput(Context context, View view)
     // 显示Toast
     public static void showToast(Context context, int resId)
     public static void showToast(Context context, CharSequence text)
     // 检测相机
     public static boolean hasCamera(Context context)
     // 媒体扫描
     public static void mediaScan(Context context, Uri uri)
     public static void addToMediaStore(Context context, File file)
     // 横竖屏设置
     public static void setFullScreen(final Activity activity,final boolean fullscreen)
     public static void setPortraitOrientation(final Activity activity,final boolean portrait)
     public static void lockScreenOrientation(final Activity context, final boolean portrait)
     // 系统版本判断
     public static boolean hasKitkat()
     public static boolean hasLollipop()
     // 获取系统服务
     public <T> T getSystemService(final Context context, final String name)
     // 重启Activity
     public static void restartActivity(final Activity activity)
     // 获取电池信息
     public static float getBatteryLevel(Intent batteryIntent)
     public static String getBatteryInfo(Intent batteryIntent)
 */


public final class AndroidUtils {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    public static final String TAG = AndroidUtils.class.getSimpleName();

    public static final String FILENAME_NOMEDIA = ".nomedia";//包含这个文件名字的目录可以 Hide your files from the Media Scanner

    public static final int HEAP_SIZE_LARGE = 48 * 1024 * 1024;
    private static final Pattern SAFE_FILENAME_PATTERN = Pattern.compile("[\\w%+,./=_-]+");

    private AndroidUtils() {
    }

    //*********************************************** Android component 相关 ********************************************/
    /**
     *统一使用线程池运行AsyncTask ，即异步并行运行AsyncTask （Android 3.0以上默认AsyncTask串行运行）
     * Execute an {@link AsyncTask} on a thread pool.
     * @param task Task to add.
     * @param args Optional arguments to pass to {@link AsyncTask#execute(Object[])}.
     * @param <T>  Task argument type.
     */
    @SuppressWarnings("unchecked")
    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static <T> void execute(AsyncTask<T, ?, ?> task, T... args) {
        //根据不同版本号执行AsyncTask
        if (Build.VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
            task.execute(args);
        } else {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, args);
        }
    }

    /****************************************  Toast  *******************************************************/
    public static void showToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static void showLongToast(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void showLongToast(Context context, CharSequence text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

    /**
     * 重启一个Activity
     *
     * @param activity Activity
     */
    public static void restartActivity(final Activity activity) {
        Intent intent = activity.getIntent();
        activity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
    }

    //获取系统服务（不用转换类型）
    @SuppressWarnings({"ResourceType", "unchecked"})
    public <T> T getSystemService(final Context context, final String name) {
        return (T) context.getSystemService(name);
    }

    public static boolean isLargeHeap() {
        return Runtime.getRuntime().maxMemory() > HEAP_SIZE_LARGE;
    }

    /**
     * check if free size of SDCARD and CACHE dir is OK
     *
     * @param needSize how much space should release at least
     * @return true if has enough space
     */
    public static boolean noFreeSpace(long needSize) {
        long freeSpace = getFreeSpace();
        return freeSpace < needSize * 3;
    }

    @SuppressWarnings("deprecation")
    public static long getFreeSpace() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
                .getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    //*************************************************  多媒体相关  *********************************************/

    /*
    文件目录相关
     */

    /**
     * Check if a filename is "safe" (no metacharacters元字符 or spaces).
     *
     * @param file The file to check
     */
    public static boolean isFilenameSafe(File file) {
        // Note, we check whether it matches what's known to be safe,
        // rather than what's known to be unsafe.  Non-ASCII, control
        // characters, etc. are all unsafe by default.
        return SAFE_FILENAME_PATTERN.matcher(file.getPath()).matches();
    }

    /**
     * 获取context所在应用的外部缓存目录
     * （并在该目录下创建".nomedia"文件，即不能被Media Scanner搜索到）
     */
    public static File getCacheDir(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File cacheDir = context.getExternalCacheDir(); //ExternalStorage上的缓存目录
            File noMedia = new File(cacheDir, FILENAME_NOMEDIA);
            if (!noMedia.exists()) {
                try {
                    noMedia.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return cacheDir;
        } else {
            return context.getCacheDir();
        }
    }


    public static boolean noSdcard() {
        return !Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    /*
    Provider Uri相关
     */
    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;

        if (DEBUG) {
            Log.v(TAG, "getRealPath() uri=" + uri + " isKitKat=" + isKitKat);
        }

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return uri.getPath();
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /*
     * 检测是否有前置 / 后置 摄像头
     */
    public static boolean hasCamera(Context context) {
        PackageManager pm = context.getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                || pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
    }

    /**
     *启动MediaScanner服务扫描某个文件更新到媒体库
     * （扫描工作是在MediaScanner服务中进行的，因此不会阻塞当前程序进程）
     *  http://droidyue.com/blog/2014/07/12/scan-media-files-in-android-chinese-edition/index.html
     *
     *使用场景例子：把存储在SD卡的图片文件及时刷新到 media database供Gallery查看图片
     * http://blog.csdn.net/happy08god/article/details/9303715
     *
     * @param context
     * @param uri  需要扫描的文件的Uri
     */
    public static void mediaScan(Context context, Uri uri) {
        //ACTION_MEDIA_SCANNER_SCAN_FILE：
        // Broadcast Action:  Request the media scanner to scan a file and add it to the media database.
        //The path to the file is contained in the Intent.mData field.
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    // another media scan way
    /**
     *
     * @param context
     * @param file 需要扫描的文件的路径
     */
    public static void addToMediaStore(Context context, File file) {
        String[] path = new String[]{file.getPath()};
        /**
         *public static void scanFile (Context context, String[] paths, String[] mimeTypes, MediaScannerConnection.OnScanCompletedListener callback)
         * paths        Array of paths to be scanned.
         * mimeTypes	Optional array of MIME types for each path. If mimeType is null, then the mimeType will be inferred from the file extension.
         * callback	    Optional callback through which you can receive the scanned URI and MIME type
         */
        MediaScannerConnection.scanFile(context, path, null, null);
    }

    //check whether the media is available / mounted to Android
    // The media might be mounted to a computer, missing, read-only, or in some other state
    public static boolean isMediaMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    //软键盘相关
    /**
     *关闭软键盘
     * @param context Context对象
     * @param editText 正在Focus输入的EditText
     */
    public static void hideSoftKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        //View.getWindowToken()：Retrieve a unique token identifying the window this view is attached to.
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * 打开软键盘
     * @param context  Context对象
     * @param editText The currently focused view, which would like to receive soft keyboard input.
     */
    public static void showSoftKeyboard(Context context, EditText editText) {
        if (editText.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * view获取焦点后，切换软键盘（打开 -> 关闭 或者 关闭 -> 打开）
     * @param context Context对象
     * @param view  正在获取焦点view
     */
    public static void toggleSoftInput(Context context, View view) {
        if (view.requestFocus())  //view获取焦点
        {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, 0); //SHOW_IMPLICIT、HIDE_IMPLICIT_ONLY
        }
    }


    //屏幕方向相关
    /**
     * 设置是否全屏
     * @param activity
     * @param fullscreen  true：全屏， false:非全屏（有ActionBar等）
     */
    public static void setFullScreen(final Activity activity,
                                     final boolean fullscreen) {
        if (fullscreen) {
            activity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        } else {
            activity.getWindow().addFlags(
                    WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    /**
     * 设置是否保持竖屏
     * @param activity
     * @param portrait true:保持竖屏，false:横竖屏unspecified
     */
    public static void setPortraitOrientation(final Activity activity,
                                              final boolean portrait) {
        if (portrait) {
            //竖屏portrait
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    /**
     * 锁定屏幕方向：1、portrait为true：锁定竖屏；2、portrait为false：锁定为当前屏幕方向
     * @param context
     * @param portrait true：锁定竖屏；false：锁定为当前屏幕方向
     */
    public static void lockScreenOrientation(final Activity context, final boolean portrait) {
        if (portrait) {
            //锁定竖屏
            context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //保持横屏
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                //保持竖屏
                context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    //设施屏幕方向为横竖屏均可（unspecified）
    public static void unlockScreenOrientation(final Activity context) {
        context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    //Intent：电池状态相关信息
    //可以把得到的Intent（电池状态相关信息）传给下面的getBatteryLevel、getBatteryInfo获取具体信息
    public static Intent getBatteryStatus(Context context) {
        Context appContext = context.getApplicationContext();
        return appContext.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    //电量状态
    public static float getBatteryLevel(Intent batteryIntent) {
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        return level / (float) scale;
    }

    //电池信息
    public static String getBatteryInfo(Intent batteryIntent) {
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float) scale;
        return "Battery Info: isCharging=" + isCharging
                + " usbCharge=" + usbCharge + " acCharge=" + acCharge
                + " batteryPct=" + batteryPct;
    }


    //********************************************** 系统版本判断 ***************************************************/

    public static boolean hasIceCreamSandwich() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean isPreIceCreamSandwich() {
        return Build.VERSION.SDK_INT < VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitkat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }

    public static boolean isPreLollipop() {
        return Build.VERSION.SDK_INT < VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP;
    }

//    @TargetApi(11)
//    public static void setStrictMode(boolean enable) {
//        if (!enable) {
//            return;
//        }
//        StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
//                new StrictMode.ThreadPolicy.Builder()
//                        .detectAll()
//                        .penaltyLog();
//        StrictMode.VmPolicy.Builder vmPolicyBuilder =
//                new StrictMode.VmPolicy.Builder()
//                        .detectAll()
//                        .penaltyLog();
//        StrictMode.setThreadPolicy(threadPolicyBuilder.build());
//        StrictMode.setVmPolicy(vmPolicyBuilder.build());
//    }

}
