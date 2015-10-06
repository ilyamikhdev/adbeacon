package com.adbeacon.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adbeacon.Adbeacon;
import com.adbeacon.AdbeaconService;

import org.altbeacon.beacon.BeaconManager;

/**
 * Created by mihne on 06.10.2015.
 */
public class UpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Adbeacon.isAllowService(context))
            return;

        try {
            if (Adbeacon.isOnline(context) &&
                    BeaconManager.getInstanceForApplication(context).checkAvailability())
                context.startService(new Intent(context, AdbeaconService.class));
        } catch (RuntimeException e) {
        }
    }
}
