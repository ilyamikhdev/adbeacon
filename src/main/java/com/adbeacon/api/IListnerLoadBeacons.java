package com.adbeacon.api;

import com.adbeacon.model.BeaconText;

/**
 * Created by mihne on 28.08.2015.
 */
public interface IListnerLoadBeacons {
    void loaded(BeaconText.Data data);
}
