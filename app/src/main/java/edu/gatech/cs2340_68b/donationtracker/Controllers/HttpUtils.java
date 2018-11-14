package edu.gatech.cs2340_68b.donationtracker.Controllers;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;

@SuppressWarnings("UtilityClass")
public class HttpUtils {
    // Uncomment for local testing
//    private static final String BASE_URL = "http://10.0.2.2:5000";

    // URL to hosting server
    private static final String BASE_URL = "https://donation-tracker-server-heroku.herokuapp.com";

    private static final AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url,
                           RequestParams params,
                           ResponseHandlerInterface responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void postForm(String url,
                                RequestParams params,
                                ResponseHandlerInterface responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void postJson(Context c,
                                String url, HttpEntity entity,
                                ResponseHandlerInterface handler) {
        client.post(c, getAbsoluteUrl(url),
                entity, "application/json", handler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
