package com.adbeacon.model;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mihne on 06.10.2015.
 */
@DatabaseTable(tableName = "beacon_text")
public class BeaconNotification {

    public BeaconNotification() {
    }

    @DatabaseField(generatedId = false, id = true)
    public int beacon_id;
    @DatabaseField(dataType = DataType.STRING)
    public String text;
    @DatabaseField(dataType = DataType.STRING)
    public String title;
    @DatabaseField(dataType = DataType.STRING)
    public String url;

    @DatabaseField(dataType = DataType.STRING)
    public String uuid;
    @DatabaseField(dataType = DataType.STRING)
    public String minor;
    @DatabaseField(dataType = DataType.STRING)
    public String major;
    @DatabaseField(dataType = DataType.STRING)
    public String enddate;

    @Override
    public boolean equals(Object o) {
        if(o == null)
            return false;
        BeaconNotification in = (BeaconNotification)o;
        return text.equals(in.text) && title.equals(in.title)&& url.equals(in.url) && uuid.equals(in.uuid)
                && minor.equals(in.minor)&& major.equals(in.major)&& enddate.equals(in.enddate);
    }
    @Override
    public int hashCode()
    {
        return beacon_id;
    }
}
