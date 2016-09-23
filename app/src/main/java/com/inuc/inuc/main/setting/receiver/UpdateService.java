package com.inuc.inuc.main.setting.receiver;

/**
 * Created by 景贝贝 on 2016/8/7.
 */


import android.accounts.NetworkErrorException;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;


import com.inuc.inuc.R;
import com.inuc.inuc.main.MainActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

/**
 * Created by 景贝贝 on 2016/8/7.
 */
public class UpdateService extends Service {
    // BT字节参考量
    private static final float SIZE_BT = 1024L;
    // KB字节参考量
    private static final float SIZE_KB = SIZE_BT * 1024.0f;
    // MB字节参考量
    private static final float SIZE_MB = SIZE_KB * 1024.0f;

    private final static int DOWNLOAD_COMPLETE = 1;// 完成
    private final static int DOWNLOAD_NOMEMORY = -1;// 内存异常
    private final static int DOWNLOAD_FAIL = -2;// 失败

    private String appName = null;// 应用名字
    private String appUrl = null;// 应用升级地址
    private File updateDir = null;// 文件目录
    private File updateFile = null;// 升级文件

    // 通知栏
    private NotificationManager updateNotificationManager = null;
    private Notification updateNotification = null;

    private Intent updateIntent = null;// 下载完成
    private PendingIntent updatePendingIntent = null;// 在下载的时候

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        appName = intent.getStringExtra("appname");
        appUrl = intent.getStringExtra("appurl");
        Log.v("下载地址",appUrl);
        updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, MainActivity.class), 0);

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.newlogo)
                .setContentTitle(appName+"正在下载")
                .setContentText("0MB (0%)")
                .setContentIntent(contentIntent)
                .build();// getNotification()

        updateNotificationManager.notify(0, notification);

        new UpdateThread().execute();
    }

    /**
     * 在这里使用了asynctask异步任务来下载
     */
    class UpdateThread extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            return downloadUpdateFile(appUrl);
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            Notification.Builder mbuilder = new Notification.Builder(UpdateService.this);
                  mbuilder .setSmallIcon(R.mipmap.newlogo)
                    .setContentText("");

            if (result == DOWNLOAD_COMPLETE) {
                Log.d("update", "下载成功");
                String cmd = "chmod 777 " + updateFile.getPath();
                try {
                    Runtime.getRuntime().exec(cmd);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.fromFile(updateFile);
                //安装程序
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
                updatePendingIntent = PendingIntent.getActivity(UpdateService.this, 0, installIntent, 0);

                mbuilder.setContentTitle(appName+"下载完成");
                mbuilder.setContentIntent(updatePendingIntent);
                updateNotificationManager.notify(0, mbuilder.build());
                //启动安装程序
                UpdateService.this.startActivity(installIntent);
                updateNotificationManager.cancel(0);
                stopSelf();
            } else if (result == DOWNLOAD_NOMEMORY) {
                //如果内存有问题
                mbuilder.setContentTitle(appName+"下载失败");

                updateNotificationManager.notify(0, mbuilder.build());

                stopSelf();
            } else if (result == DOWNLOAD_FAIL) {
                //下载失败
                mbuilder.setContentTitle(appName+"下载失败");
                updateNotificationManager.notify(0, mbuilder.build());

                stopSelf();

            }
        }

    }

    /**
     * 下载更新程序文件
     * @param downloadUrl   下载地址
     * @return
     */
    private int downloadUpdateFile(String downloadUrl) {
        int count = 0;
        long totalSize = 0;   //总大小
        long downloadSize = 0;   //下载的大小


        HttpURLConnection conn = null;
        try {
            // 利用string url构建URL对象
            URL mURL = new URL(downloadUrl);
            conn = (HttpURLConnection) mURL.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(10000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                totalSize=conn.getContentLength();
                FileOutputStream fos = null;
                InputStream is = conn.getInputStream();

                if(is!=null){
                    String sdpath = Environment.getExternalStorageDirectory() + "/";
                  String mSavePath = sdpath + "download";
                    updateFile=new File(mSavePath,appName + ".apk");
                    fos = new FileOutputStream(updateFile, false);
                    byte buffer[] = new byte[4096];
                    int readsize = 0;
                    Notification.Builder mybuilder = new Notification.Builder(UpdateService.this);
                    mybuilder .setSmallIcon(R.mipmap.newlogo)
                           .setContentTitle(appName+"正在下载");
                    while ((readsize = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, readsize);
                        downloadSize += readsize;
                        if ((count == 0) || (int) (downloadSize * 100 / totalSize) >= count) {
                            count += 5;

                            mybuilder.setContentText(getMsgSpeed(downloadSize,totalSize));
                            updateNotificationManager.notify(0, mybuilder.build());
                        }
                    }
                    fos.flush();
                    if (totalSize >= downloadSize) {
                        return DOWNLOAD_COMPLETE;
                    } else {
                        return DOWNLOAD_FAIL;
                    }
                }
//                String response = getStringFromInputStream(is);
//                return response;
            } else {
                throw new NetworkErrorException("response status is "+responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (conn != null) {
                conn.disconnect();
            }
        }

        return DOWNLOAD_FAIL;
    }


    /**
     * 获取下载进度
     * @param downSize
     * @param allSize
     * @return
     */
    public static String getMsgSpeed(long downSize, long allSize) {
        StringBuffer sBuf = new StringBuffer();
        sBuf.append(getSize(downSize));
        sBuf.append("/");
        sBuf.append(getSize(allSize));
        sBuf.append(" ");
        sBuf.append(getPercentSize(downSize, allSize));
        return sBuf.toString();
    }

    /**
     * 获取大小
     * @param size
     * @return
     */
    public static String getSize(long size) {
        if (size >= 0 && size < SIZE_BT) {
            return (double) (Math.round(size * 10) / 10.0) + "B";
        } else if (size >= SIZE_BT && size < SIZE_KB) {
            return (double) (Math.round((size / SIZE_BT) * 10) / 10.0) + "KB";
        } else if (size >= SIZE_KB && size < SIZE_MB) {
            return (double) (Math.round((size / SIZE_KB) * 10) / 10.0) + "MB";
        }
        return "";
    }

    /**
     * 获取到当前的下载百分比
     * @param downSize   下载大小
     * @param allSize    总共大小
     * @return
     */
    public static String getPercentSize(long downSize, long allSize) {
        String percent = (allSize == 0 ? "0.0" : new DecimalFormat("0.0")
                .format((double) downSize / (double) allSize * 100));
        return "(" + percent + "%)";
    }



}

