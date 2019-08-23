package com.xinye.image.compress.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * 系统App调用
 *
 * @author wangheng
 */
public class SystemAppInvoker {
    private static final String TAG = "SystemAppInvoker";
    /**
     * 打开拨号盘
     * @param context activity
     * @param phone phone
     */
    public static boolean openDialUI(Context context, String phone){
        return openDialUIByTelUri(context,"tel:" + phone);
    }

    /**
     * 根据uri打开拨号盘界面
     * @param context context
     * @param uri 以tel:开头的uri
     * @return 是否成功打开
     */
    public static boolean openDialUIByTelUri(Context context, String uri){
        if(context == null || uri == null){
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        return openSystemAppByIntent(context,intent);
    }

    /**
     * 打开指定app在应用市场的详情页
     * @param context context context
     * @param packageName 应用包名
     * @return 是否成功打开
     */
    public static boolean openMarketAppDetail(Context context, String packageName){
        return openMarketAppDetailByUri(context,"market://details?id=" + packageName);
    }

    /**
     * 根据给定的uri打开指定app的应用市场详情页
     * @param context context
     * @param uri 以market://开头的uri
     * @return 是否成功打开
     */
    public static boolean openMarketAppDetailByUri(Context context, String uri){
        if(context == null || uri == null){
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(uri));
        return openSystemAppByIntent(context,intent);
    }

    private static boolean openSystemAppByIntent(Context context, Intent intent){
        if(context == null || intent == null){
            return false;
        }
        try {
            if (canResolveActivity(context,intent)) {
                if(!(context instanceof Activity)){
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                context.startActivity(intent);
                return true;
            }
        }catch (Exception e){
            Log.e(TAG,"打开其他应用失败:" + e);
        }
        return false;
    }

    public static boolean openBrowser(Context context, String url){
        if(context == null || url == null){
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return openSystemAppByIntent(context,intent);
    }

    public static boolean openUrlWithView(Context context, String url){
        if(context == null || url == null){
            return false;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        return openSystemAppByIntent(context,intent);
    }

    /**
     * 系统能否正确响应给定的意图
     * @param context context
     * @param intent 尝试要打开的意图
     * @return 能否响应
     */
    public static boolean canResolveActivity(Context context , Intent intent){
        if(intent == null || context == null){
            return false;
        }
        return intent.resolveActivity(context.getPackageManager()) != null;
    }

    private static boolean isXiaomi(){
        return "XIAOMI".equalsIgnoreCase(Build.BRAND);
    }

    public static void jumpToAppPermissionActivity(Context context) {
        if(isXiaomi()){
            Intent miuiIntent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            miuiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            miuiIntent.putExtra("extra_pkgname", PackageUtils.getPackageName(context));
            if (canResolveActivity(context,miuiIntent)) {
                context.startActivity(miuiIntent);
                return;
            }
        }
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", PackageUtils.getPackageName(context), null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception ex) {
                // nothing to do
            }
        }
    }
}
