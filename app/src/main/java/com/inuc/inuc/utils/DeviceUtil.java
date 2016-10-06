package com.inuc.inuc.utils;

import java.util.UUID;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
/**
 * 获取设备各种唯一识别码的工具类
 * @author tongleer.com
 *
 */
public class DeviceUtil {
    /**
     * 获得设备IMEI Device ID，需要权限READ_PHONE_STATE
     * @param context
     * @return
     */
    public static String getIMEIDeviceId(Context context){
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
    }
    /**
     * 获得PseudoUniqueId，此ID在任何Android手机中都有效，
     * 但如果两个手机应用了同样的硬件以及Rom镜像（ROM版本、制造商、CPU型号、以及其他硬件信息），那计算的ID就不是唯一的，出现此类情况一般可以忽略。
     * @return
     */
    public static String getPseudoUniqueId(){
//Build.CPU_ABI过时用Build.SUPPORTED_ABIS代替，不过需要API21
        return "35" +
                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10 ;
    }
    /**
     * 获得Android ID
     * @param context
     * @return
     */
    public static String getAndroidId(Context context){
        return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
    }
    /**
     * 获得Sim Serial Number
     * @param context
     * @return
     */
    public static String getSimSerialNumber(Context context){
//装有SIM卡的设备的获取方式，但对于CDMA设备返回空值
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String simSerialNumber = telephonyManager.getSimSerialNumber();
        if(simSerialNumber==null){
//Android 2.3设备的获取方式
            String serialNumber = Build.SERIAL;
            if(serialNumber!=null){
                simSerialNumber=serialNumber;
            }
        }
        return simSerialNumber;
    }
    /**
     * 获得Wlan中的MAC地址，需要权限ACCESS_WIFI_STATE
     * @param context
     * @return
     */
    public static String getWlanMACAddress(Context context){
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifiManager.getConnectionInfo().getMacAddress();
    }
    /**
     * 获得蓝牙中的MAC地址，需要权限BLUETOOTH
     * @param context
     * @return
     */
//    public static String getBTMACAddress(Context context){
//        BluetoothAdapter bluetoothAdapter = null;
//        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        return bluetoothAdapter.getAddress();
//    }
//    /**
//     * 获得用MD5加密的唯一设备识别号ID
//     * @param context
//     * @return
//     */
//    public static String toMD5UniqueId(Context context){
//        return MD5Util.getStringMD5(
//                getIMEIDeviceId(context)+
//                        getPseudoUniqueId()+
//                        getAndroidId(context)+
//                        getSimSerialNumber(context)+
//                        getWlanMACAddress(context)+
//                        getBTMACAddress(context)
//        );
//    }
    /**
     * 获得用UUID加密的唯一设备识别号ID
     * @param context
     * @return
     */
    public static String toUUIDUniqueId(Context context){
        UUID deviceUuid = new UUID(
                getAndroidId(context).hashCode(),
                ((long)getIMEIDeviceId(context).hashCode() << 32) | getSimSerialNumber(context).hashCode()
        );
        return deviceUuid.toString();
    }
}
