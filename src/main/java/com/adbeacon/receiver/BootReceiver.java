package com.adbeacon.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adbeacon.AdbeaconService;
import com.adbeacon.Adbeacon;

import org.altbeacon.beacon.BeaconManager;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Adbeacon.isAllowService(context)) {
            try {
                if (BeaconManager.getInstanceForApplication(context).checkAvailability())
                    context.startService(new Intent(context, AdbeaconService.class));
            } catch (RuntimeException e) {
            }
        }
    }
} 
