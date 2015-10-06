package com.adbeacon.model;

import java.util.ArrayList;

public class BeaconArrayResult {
    public Result result;

    public class Result {
        public int error;
        public String error_message;
        public ArrayList<BeaconNotification> data;
    }

}