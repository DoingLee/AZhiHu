package com.doing.library.core.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;

import com.doing.library.BuildConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Package相关的工具类，App是否安装，是否运行，启用和禁用组件，获取应用签名等
 *
 * User: mcxiaoke
 * Date: 15/5/12
 * Time: 10:11
 */
public final class PackageUtils {

    public static final boolean DEBUG = BuildConfig.DEBUG;
    private static final String TAG = PackageUtils.class.getSimpleName();

    //判断名字为packageName的包是否已安装
    public static boolean isAppInstalled(final Context context, final String packageName) {
        try {
            final PackageManager pm = context.getPackageManager();
            final PackageInfo info = pm.getPackageInfo(packageName, 0);
            return info != null;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 判断context所在进程是否为主进程
     *
     * @param context 需要判断的Context
     * @return
     */
    public static boolean isMainProcess(final Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (RunningAppProcessInfo info : processes) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个Services是否在运行
     *
     * @param context
     * @param cls  需要判断的Services类
     * @return
     */
    public static boolean isServiceRunning(Context context, Class<?> cls) {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningServiceInfo> services = am.getRunningServices(Integer.MAX_VALUE);
        final String className = cls.getName();
        for (RunningServiceInfo service : services) {
            if (className.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断一个App是否在运行
     *
     * @param context
     * @param packageName 需要判断的package的名字
     * @return
     */
    public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        if (apps == null || apps.isEmpty()) {
            return false;
        }
        for (RunningAppProcessInfo app : apps) {
            if (packageName.equals(app.processName)) {
                return true;
            }
        }
        return false;
    }

    public static PackageInfo getCurrentPackageInfo(final Context context) {
        final PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    public static PackageInfo getPackageInfo(final Context context, final String packageName) {
        final PackageManager pm = context.getPackageManager();
        try {
            return pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
        } catch (NameNotFoundException e) {
            return null;
        }
    }

    /**
     * 判断组件是否被禁用：
     * component or application has been explicitly disabled, regardless of what it has specified in its manifest.
     *
     * @param context
     * @param clazz  The Class object of the desired component
     * @return
     */
    public static boolean isDisabled(Context context, Class<?> clazz) {
        ComponentName componentName = new ComponentName(context, clazz);
        PackageManager pm = context.getPackageManager();
        return pm.getComponentEnabledSetting(componentName)
                == PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    }

    /**
     * 判断组件是否被启用：
     * component or application has been explicitly disabled, regardless of what it has specified in its manifest.
     *
     * @param context
     * @param clazz  The Class object of the desired component
     * @return
     */
    public static boolean isEnabled(Context context, Class<?> clazz) {
        ComponentName componentName = new ComponentName(context, clazz);
        PackageManager pm = context.getPackageManager();
        return pm.getComponentEnabledSetting(componentName)
                != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    }

    /**
     * 启用组件
     *
     * @param context
     * @param clazz  The Class object of the desired component
     */
    public static void enableComponent(Context context, Class<?> clazz) {
        setComponentState(context, clazz, true);
    }

    /**
     * 禁用组件
     *
     * @param context
     * @param clazz  The Class object of the desired component
     */
    public static void disableComponent(Context context, Class<?> clazz) {
        setComponentState(context, clazz, false);
    }

    /**
     * 启用 / 禁用组件
     *
     * @param context
     * @param clazz    The Class object of the desired component
     * @param enable  true：启用；false：禁用
     */
    public static void setComponentState(Context context, Class<?> clazz, boolean enable) {
        ComponentName componentName = new ComponentName(context, clazz);
        PackageManager pm = context.getPackageManager();
        final int oldState = pm.getComponentEnabledSetting(componentName);
        final int newState = enable ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
        if (newState != oldState) {
            final int flags = PackageManager.DONT_KILL_APP;
            pm.setComponentEnabledSetting(componentName, newState, flags);
        }
    }


    //获取Package包签名
    @SuppressLint("PackageManagerGetSignatures")
    private static Signature getPackageSignature(Context context) {
        final PackageManager pm = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (Exception ignored) {
        }

        Signature signature = null;
        if (info != null) {
            Signature[] signatures = info.signatures;
            if (signatures != null && signatures.length > 0) {
                signature = signatures[0];
            }
        }

        if (DEBUG) {
            LogUtils.v(TAG, "getSignature() " + signature);
        }
        return signature;
    }

    //获取包签名的sha1算法的hash值
    public static String getSignature(Context context) {
        final Signature signature = getPackageSignature(context);
        if (signature != null) {
            try {
                return CryptoUtils.HASH.sha1(signature.toByteArray());
            } catch (Exception e) {
                LogUtils.e(TAG, "getSignature() ex=" + e);
            }
        }
        return "";
    }

    //获取包签名的字符串格式信息
    public static String getSignatureInfo(Context context) {
        final Signature signature = getPackageSignature(context);
        if (signature == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();
        try {
            final byte[] signatureBytes = signature.toByteArray();
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            final InputStream is = new ByteArrayInputStream(signatureBytes);
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(is);
            final String chars = signature.toCharsString();
            final String hex = CryptoUtils.HEX.encodeHex(signatureBytes, false);
            final String md5 = CryptoUtils.HASH.md5(signatureBytes);
            final String sha1 = CryptoUtils.HASH.sha1(signatureBytes);
            builder.append("SignName:").append(cert.getSigAlgName()).append("\n");
            builder.append("Chars:").append(chars).append("\n");
            builder.append("Hex:").append(hex).append("\n");
            builder.append("MD5:").append(md5).append("\n");
            builder.append("SHA1:").append(sha1).append("\n");
            builder.append("SignNumber:").append(cert.getSerialNumber()).append("\n");
            builder.append("SubjectDN:").append(cert.getSubjectDN().getName()).append("\n");
            builder.append("IssuerDN:").append(cert.getIssuerDN().getName()).append("\n");
        } catch (Exception e) {
            LogUtils.e(TAG, "parseSignature() ex=" + e);
        }

        final String text = builder.toString();

        if (DEBUG) {
            LogUtils.v(TAG, text);
        }

        return text;
    }


}
