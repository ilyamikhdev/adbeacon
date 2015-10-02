package com.adbeacon;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.adbeacon.api.IListnerLoadBeacons;
import com.adbeacon.api.RestClient;
import com.adbeacon.model.BeaconText;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdbeaconService extends Service implements BeaconConsumer, RangeNotifier, IListnerLoadBeacons {
    public static final String UUID_BLE = "EBEFD083-70A2-47C8-2015-E7B5634DF524";
    private org.altbeacon.beacon.BeaconManager mBeaconManager;

    private static int NOTIFICATION_ID = 123;
    private NotificationManager mNotificationManager;
    private Region mRegion = new Region("com.adbeacon", Identifier.parse(UUID_BLE), null, null);

    private Timer mTimer;
    private HashSet<Beacon> mBeacons = new HashSet<Beacon>();

    private String mToken = "empty";
    private String mDeviceId = "empty";

    public AdbeaconService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LOG.i("onStartCommand");
        super.onStartCommand(intent, flags, startId);

        //if (mBeaconManager.isBound(this))
        //    mBeaconManager.setBackgroundMode(false);

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        LOG.i("onCreate");
        super.onCreate();

        LOG.enableDebugLogging(getResources().getBoolean(R.bool.is_debug));

        mToken = Adbeacon.getDeviceName();
        mDeviceId = Adbeacon.getDeviceUUID(this);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBeaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
//        mBeaconManager.setBackgroundScanPeriod(3000l);
//        mBeaconManager.setBackgroundBetweenScanPeriod(6000l);
        mBeaconManager.bind(this);
    }

    @Override
    public void onDestroy() {
        LOG.i("onDestroy");
        super.onDestroy();
        mBeaconManager.unbind(this);
        if (mTimer != null)
            mTimer.cancel();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void postNotification(String title, String msg, String url) {
        try {
            //int id = NOTIFICATION_ID;
            int requestId = (int) System.currentTimeMillis();
            PendingIntent pendingIntent;

            //id = NOTIFICATION_ID++;

            Intent notifyIntent = new Intent(Intent.ACTION_VIEW);
            notifyIntent.setData(Uri.parse(url));
            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            pendingIntent = PendingIntent.getActivities(
                    this,
                    requestId,
                    new Intent[]{notifyIntent},
                    PendingIntent.FLAG_UPDATE_CURRENT);


            Notification.Builder builder = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.ic_stat_action_loyalty)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    //.setStyle(new Notification.BigTextStyle().bigText(msg))
                    ;

            Notification notification = new Notification.BigTextStyle(builder)
                    .bigText(msg).build();
//        Notification notification = new Notification.BigPictureStyle(builder)
//                .bigPicture(BitmapFactory.decodeResource(getResources(),
//                                R.drawable.kupanda)).build();

            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_LIGHTS;
            //notification.defaults |= Notification.DEFAULT_VIBRATE;
            mNotificationManager.notify(NOTIFICATION_ID++, notification);


        } catch (Exception e) {
            LOG.e(e.getMessage());
        }
    }

    @Override
    public void onBeaconServiceConnect() {

        mBeaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                LOG.i("I just saw a beacon named " + region.getUniqueId() + " for the first time!");
                //postNotification("Monitor", "Enter Region");
                try {
                    mBeaconManager.startRangingBeaconsInRegion(mRegion);
                    mBeaconManager.setRangeNotifier(AdbeaconService.this);
                    startRange();
                } catch (RemoteException e) {
                }
            }

            @Override
            public void didExitRegion(Region region) {
                LOG.i("I no longer see a beacon named " + region.getUniqueId());
                //postNotification("Monitor", "Exit Region");
                try {
                    mBeaconManager.stopRangingBeaconsInRegion(mRegion);
                } catch (RemoteException e) {
                }
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                LOG.i("I have just switched from seeing/not seeing beacons: " + state);
            }

        });

        try {
            mBeaconManager.startMonitoringBeaconsInRegion(mRegion);
        } catch (RemoteException e) {
        }
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        putBeacons(beacons);
    }

    private synchronized void startRange() {
        try {
            count = 0;
            mBeacons.clear();
            //mBeaconManager.startRangingBeaconsInRegion(mRegion);

            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        mBeaconManager.stopRangingBeaconsInRegion(mRegion);

                        loadBeaconData(mBeacons);
                    } catch (RemoteException e) {
                    }
                }
            }, 5000l);

        } catch (Exception e) {
        }
    }

    int count = 0;

    public synchronized void putBeacons(Collection<Beacon> beacons) {
        LOG.i("count = " + count++);

        if (beacons.size() > 0) {
            for (Beacon beacon : beacons) {
                if (!mBeacons.contains(beacon))
                    mBeacons.add(beacon);
            }
        }
    }

    public synchronized void loadBeaconData(Collection<Beacon> beacons) {
        if (beacons != null && beacons.size() > 0) {
            try {
                mBeaconManager.stopRangingBeaconsInRegion(mRegion);
            } catch (RemoteException e) {
            }

            loadBeaconText(beacons, this);
        }
    }

    public void loadBeaconText(Collection<Beacon> beacons, final IListnerLoadBeacons listner) {
        ArrayList<String> arrayBeacon = new ArrayList<String>();
        if (beacons != null && beacons.size() > 0) {
            for (Beacon beacon : beacons) {
                arrayBeacon.add(beacon.getId1() + ";" + beacon.getId2() + ";" + beacon.getId3());
            }

            Callback<BeaconText> callback = new Callback<BeaconText>() {
                @Override
                public void success(BeaconText itemsResponse, Response response) {
                    if (listner != null) {
                        if (itemsResponse.result != null) {
                            if (itemsResponse.result.error == 0)
                                listner.loaded(itemsResponse.result.data);
                            else {
                                if (getResources().getBoolean(R.bool.is_debug))
                                    postNotification("Ошибка " + itemsResponse.result.error, itemsResponse.result.error_message, "");
                            }
                        }
                    }
                    LOG.i("loadBeaconText successs");
                }

                @Override
                public void failure(RetrofitError error) {
                    LOG.e("loadBeaconText failure");
                    if (getResources().getBoolean(R.bool.is_debug))
                        postNotification("Ошибка загрузки с сервера!", error.getLocalizedMessage(), "");
                }
            };

            RestClient.getInstance().getBeaconPhrase(mToken, mDeviceId, 0, arrayBeacon, callback);
        }

    }

    @Override
    public void loaded(BeaconText.Data data) {
        if (data != null && !TextUtils.isEmpty(data.text))
            postNotification(data.title, data.text, data.url);
    }

}
