package com.adbeacon.model;

public class BeaconTextResult {
    public Result result;

    public class Result {
        public int error;
        public String error_message;
        public BeaconNotification data;
    }

}