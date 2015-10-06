package com.adbeacon.api;

import android.util.Base64;

import com.adbeacon.LOG;
import com.adbeacon.model.BeaconArrayResult;
import com.adbeacon.model.BeaconTextResult;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.Client;
import retrofit.client.OkClient;


public class RestClient {
    private final static int TIMEOUT = 30000;
    private final static String API_URL = "http://apimobile.kupanda.ru/api";
    private final static String API_KEY = "ca3e173c79f471cc04d53ce6b349d9cf";
    private final static String BASIC_AUTH = "";

    private static RestClient mInstance;

    private ApiService mApi;

    private RestClient() {
        mApi = getApiInterface(TIMEOUT, getApiEndpoint());
    }

    public static RestClient getInstance() {
        if (mInstance == null) {
            mInstance = new RestClient();
        }

        return mInstance;
    }

    private String getApiEndpoint() {
        return API_URL;
    }

    private Client getClient(final long timeout) {
        final OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(timeout, TimeUnit.MILLISECONDS);
        client.setRetryOnConnectionFailure(true);
        return new OkClient(client);
    }


    private ApiService getApiInterface(final long timeout, final String endpointUrl) {
        return new RestAdapter.Builder()
                .setClient(getClient(timeout))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addHeader("Accept", "application/json");
                        request.addHeader("Authorization", encodeCredentialsForBasicAuthorization());
                    }
                })
                .setEndpoint(endpointUrl)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setLog(new AndroidLog(LOG.TAG))
                .build()
                .create(ApiService.class);
    }

    private String encodeCredentialsForBasicAuthorization() {
        return "Basic " + Base64.encodeToString(BASIC_AUTH.getBytes(), Base64.NO_WRAP);
    }

    public void getNoticeByBeacon(String token, String deviceId, int userId, ArrayList<String> arrayBeacon, Callback<BeaconTextResult> callback) {
        mApi.getBeaconPhrase(API_KEY, "ibeacon_zone_register_vizit", token, deviceId, userId, arrayBeacon, callback);
    }

    public void getNotices(Callback<BeaconArrayResult> callback) {
        mApi.getNotice(API_KEY, "get_ibeacons", callback);
    }

}
