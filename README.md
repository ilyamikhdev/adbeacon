# Adbeacon
Library scans BLE device (Beacons) and displays messages that are downloaded from the server kupanda.ru.

Библиотека сканирует BLE устройства(Beacons) и показывает сообщения, которые загружены с сервера kupanda.ru.

##Download
Gradle:
```gradle
    repositories {
         maven { url 'https://dl.bintray.com/ilyamikhdev/maven/' }
    }
    dependencies {
        compile 'com.github.ilyamikhdev:adbeacon:1.0.4'
    }
    
```
## 1. Add to launcher Activity
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
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == Adbeacon.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) 
                Adbeacon.startScan(this);
        }
    }
```
## 2. AndroidManifest.xml
```xml
    ...
    <application>
        <service
            android:name="com.adbeacon.AdbeaconService"
            android:enabled="true"
            android:exported="false" />
     </application>
     ...
```
## 3. Done
![alt tag](https://github.com/ilyamikhdev/adbeacon/blob/master/screenshot_example.png?raw=true)
