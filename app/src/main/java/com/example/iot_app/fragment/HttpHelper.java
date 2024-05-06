package com.example.iot_app.fragment;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpHelper {
    private static final String TAG = "HttpHelper";

    // Interface để callback dữ liệu về Fragment
    public interface DataCallback {
        void onDataReceived(JSONObject jsonData);
    }

    public void retrieveTelemetryData(String thingsboardHost, int port, String deviceId, String jtw, long startTs, long endTs, DataCallback callback) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String apiGetTemp = thingsboardHost + "/api/plugins/telemetry/DEVICE/" + deviceId +
                        "/values/timeseries?keys=TEMP,HUMI,SMOKE,FLAME&startTs=" + startTs + "&endTs=" + endTs + "&interval=60000";

                try {
                    URL url = new URL(apiGetTemp);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("X-Authorization", jtw);

                    int responseCode = connection.getResponseCode();

                    if (responseCode == 200) {
                        Scanner scanner = new Scanner(connection.getInputStream());
                        StringBuilder response = new StringBuilder();
                        while (scanner.hasNext()) {
                            response.append(scanner.next());
                        }
                        scanner.close();

                        String jsonResponse = response.toString();
                        Log.d(TAG, "JSON Response: " + jsonResponse);

                        try {
                            // Chuyển đổi chuỗi JSON thành đối tượng JSONObject
                            JSONObject jsonData = new JSONObject(jsonResponse);

                            // Gọi callback để truyền dữ liệu JSON cho Fragment
                            if (callback != null) {
                                callback.onDataReceived(jsonData);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {

                        String errorResponse = "";
                        Scanner scanner = new Scanner(connection.getErrorStream());
                        while (scanner.hasNext()) {
                            errorResponse += scanner.next();
                        }
                        scanner.close();

                        Log.d(TAG, "Failed to retrieve telemetry data. Status code: " + responseCode);
                        Log.d(TAG, "Error response: " + errorResponse);
                    }

                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute();  // Execute AsyncTask
    }
}