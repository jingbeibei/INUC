package com.inuc.inuc.main.setting.receiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;


import com.inuc.inuc.R;
import com.inuc.inuc.beans.LastApp;

import java.io.File;

/**
 * Created by 景贝贝 on 2016/8/7.
 */
public class UpdateReceiver extends BroadcastReceiver {
    private AlertDialog.Builder mDialog;
    public static final String UPDATE_ACTION = "wuyinlei_aixinwen";
    public static String DOWNPATH;
    private boolean isShowDialog;
    private int version;
    private LastApp UpdateInformation;
    private String appname;

    public UpdateReceiver() {
    }

    public UpdateReceiver(boolean isShowDialog) {
        super();
        this.isShowDialog = isShowDialog;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        UpdateInformation= (LastApp) intent.getSerializableExtra("LastApp");
        DOWNPATH = getSDPath();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = info.versionCode;//获取版本号
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        //app名字
        appname = context.getResources().getString(R.string.app_name);
//        //服务器版本
//        UpdateInformation.serverVersion = Integer.parseInt(model
//                .getServerVersion());
//        //服务器标志
//        UpdateInformation.serverFlag = Integer.parseInt(model.getServerFlag());
//        //强制升级
//        UpdateInformation.lastForce = Integer.parseInt(model.getLastForce());
//        //升级地址
//        UpdateInformation.updateurl = model.getUpdateurl();
//        //升级信息
//        UpdateInformation.upgradeinfo = model.getUpgradeinfo();

        //检查版本
        checkVersion(context);

    }

    /**
     * 检查版本更新
     *
     * @param context
     */
    public void checkVersion(Context context) {
        //version < UpdateInformation.getVersionID()
        if (version < UpdateInformation.getVersionID()) {
            // 需要进行更新
            //更新
            update(context);
        } else {
            if (isShowDialog) {
                //没有最新版本，不用升级
                noNewVersion(context);
            }
            clearUpateFile(context);
        }
    }

    /**
     * 进行升级
     *
     * @param context
     */
    private void update(Context context) {

        if (UpdateInformation.isForced() == true) {
            //强制升级
            forceUpdate(context);
        } else {
            //正常升级
            normalUpdate(context);
        }

    }

    /**
     * 没有新版本
     *
     * @param context
     */
    private void noNewVersion(final Context context) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("版本更新");
        mDialog.setMessage("当前为最新版本");
        mDialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    /**
     * 强制升级 ，如果不点击确定升级，直接退出应用
     *
     * @param context
     */
    private void forceUpdate(final Context context) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("版本更新");
        mDialog.setMessage(UpdateInformation.getLogContents());

        mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent mIntent = new Intent(context, UpdateService.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mIntent.putExtra("appname", appname);
                mIntent.putExtra("appurl", UpdateInformation.getUrl());
                //启动服务
                context.startService(mIntent);
            }
        }).setNegativeButton("退出", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 直接退出应用
                //ManagerActivity.getInstance().finishActivity();
                System.exit(0);
            }
        }).setCancelable(false).create().show();
    }

    /**
     * 正常升级，用户可以选择是否取消升级
     *
     * @param context
     */
    private void normalUpdate(final Context context) {
        mDialog = new AlertDialog.Builder(context);
        mDialog.setTitle("版本更新");
        mDialog.setMessage(UpdateInformation.getLogContents());
        mDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent mIntent = new Intent(context, UpdateService.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //传递数据
                mIntent.putExtra("appname", appname);
                mIntent.putExtra("appurl", UpdateInformation.getUrl());
                context.startService(mIntent);
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    /**
     * 清理升级文件
     *
     * @param context
     */
    private void clearUpateFile(final Context context) {
        File updateDir;
        File updateFile;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            updateDir = new File(Environment.getExternalStorageDirectory(),
                    DOWNPATH);
        } else {
            updateDir = context.getFilesDir();
        }
        updateFile = new File(updateDir.getPath(), context.getResources()
                .getString(R.string.app_name) + ".apk");
        if (updateFile.exists()) {
            Log.d("update", "升级包存在，删除升级包");
            updateFile.delete();
        } else {
            Log.d("update", "升级包不存在，不用删除升级包");
        }
    }

    public String getSDPath() {
        String sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return sdDir;

    }
}
