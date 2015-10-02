package com.adbeacon.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.adbeacon.Adbeacon;
import com.adbeacon.AdbeaconService;
import com.adbeacon.LOG;

import org.altbeacon.beacon.BeaconManager;

public class BluetoothReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    LOG.d("Bluetooth off");
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    LOG.d("Turning Bluetooth off...");
                    break;
                case BluetoothAdapter.STATE_ON:
                    LOG.d("Bluetooth on");
                    if (Adbeacon.isAllowService(context)) {
                        try {
                            if (BeaconManager.getInstanceForApplication(context).checkAvailability())
                                context.startService(new Intent(context, AdbeaconService.class));
                        } catch (RuntimeException e) {
                        }
                    }
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    LOG.d("Turning Bluetooth on...");
                    break;
            }
        }
    }
} 
