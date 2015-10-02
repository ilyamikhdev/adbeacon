package com.adbeacon.model;

/**
 * Created by PC-1 on 27.07.2015.
 */
public class BeaconText {
    public Result result;

    public class Result {
        public int error;
        public String error_message;
        public Data data;
    }

    public class Data {
        public int id;
        public String text;
        public String title;
        public String url;
    }
}