package com.adbeacon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;

import org.altbeacon.beacon.BeaconManager;

import java.util.UUID;

public class Adbeacon {
    public static final int REQUEST_ENABLE_BT = 151002;

    public static String getDeviceUUID(Context context) {
        String deviceId = null;
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);//getBaseContext

            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            deviceId = deviceUuid.toString();

            LOG.i(deviceId);
        } catch (Exception e) {
            LOG.e(e.getMessage());
        }
        return deviceId;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
        }
        return sb.toString();
    }

    public static boolean isAllowService(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2
//                && BuildSettings.isBeta(context)
//                && !Utils.isServiceRunning(context, KupandaService.class)
                )
            return true;
        else
            return false;
    }

    public static boolean verifyBluetooth(Activity activity) {
        boolean result = false;
        if (activity == null)
            return false;
        try {
            if (!BeaconManager.getInstanceForApplication(activity).checkAvailability()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                result = false;
            } else {
                result = true;
            }
        } catch (RuntimeException e) {
            result = false;
        }

        return result;
    }

    public static void startScan(Activity activity) {
        activity.startService(new Intent(activity, AdbeaconService.class));
    }
}
