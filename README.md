# Adbeacon
Library scans BLE device (Beacons) and displays messages that are downloaded from the server kupanda.ru.
Библиотека сканирует BLE устройства(Beacons) и показывает сообщения, которые загружены с сервера kupanda.ru.

## 1. Add to start\launcher Activity
```java
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Adbeacon.isAllowService(this) && Adbeacon.verifyBluetooth(this)) {
            Adbeacon.startScan(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == Adbeacon.REQUEST_ENABLE_BT) {
                if (resultCode == Activity.RESULT_OK) 
                    Adbeacon.startScan(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
```
## 2. Manifest
```xml
        <service
            android:name="com.adbeacon.AdbeaconService"
            android:enabled="true"
            android:exported="false" />
```
## 3. Done
Adbeacon requires at minimum Android 4.0.
