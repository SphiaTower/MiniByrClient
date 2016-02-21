package com.example.qingunext.app.util_global;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;
import com.example.qingunext.app.QingUApp;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Voyager on 9/26/2015.
 */
public class NetworkBase {
    public static final int READ_TIMEOUT_MILLIS = 10000;
    public static final int CONNECT_TIMEOUT_MILLIS = 15000;
    private static final String TAG = "NetworkBase";

    public static void initCredential(String username, String password) {
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });
    }

    public static String downloadJson(String url) throws IOException {
        if (QingUApp.DEBUG)
            Log.d(TAG, "downloadJson() called with " + "url = [" + url + "]");
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        try {
            conn.setReadTimeout(READ_TIMEOUT_MILLIS /* milliseconds */);
            conn.setConnectTimeout(CONNECT_TIMEOUT_MILLIS /* milliseconds */);
            conn.setDoInput(true);
            // Enable cache
            conn.setUseCaches(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "*/*");
            // add reuqest header
            conn.setRequestMethod("GET");
//        int responseCode = conn.getResponseCode();
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            // todo potential bug of NPE
            return readStream(inputStream);
        } finally {
            conn.disconnect();
        }
    }

    public static String post(String url, List<Pair<String, String>> params) throws IOException {
        if (QingUApp.DEBUG) Log.d(TAG, "post() called with " + "url = [" + url + "], params = [" + params + "]");
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        try {
            conn.setReadTimeout(READ_TIMEOUT_MILLIS /* milliseconds */);
            conn.setConnectTimeout(CONNECT_TIMEOUT_MILLIS /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(0); //todo ?
/*            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");*/
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            return readStream(in);
        } finally {
            conn.disconnect();
        }
    }

    private static String getQuery(List<Pair<String, String>> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Pair<String, String> pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.first, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.second, "UTF-8"));
        }

        return result.toString();
    }

    public static String post(String url, String... nameValuePairs) throws IOException {
        if (nameValuePairs.length % 2 != 0) throw new AssertionError();
        List<Pair<String, String>> params = new ArrayList<>();
        for (int i = 0; i < nameValuePairs.length; i += 2) {
            params.add(new Pair<>(nameValuePairs[i], nameValuePairs[i + 1]));
        }
        return post(url, params);
    }

    private static String readStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }

    public NetworkType getNetworkType(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return NetworkType.UNAVAILABLE;
        } else {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return NetworkType.WIFI;
                default:
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                            return NetworkType.WAP;
                        default:
                            return NetworkType.NET;
                    }
            }

        }
    }

    public enum NetworkType {
        /**
         * WAP: 2G network, including cdma and gprs
         * NET: 3G or LET
         */
        UNAVAILABLE, WIFI, WAP, NET
    }
}
