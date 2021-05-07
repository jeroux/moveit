package com.example.moveit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

class SaveProfile implements Runnable {
    private final String NAME = this.getClass().getCanonicalName();
    private Context context;
    private HashMap<String, String> params;
    private SignInActivity activity;

    SaveProfile(Context context, HashMap<String, String> params, SignInActivity activity){
        this.context = context;
        this.params = params;
        this.activity = activity;
    }
    @Override
    public void run() {

        localSave();
        serverSave();

    }

    private void localSave() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                "profile_saveFile", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();

        for (HashMap.Entry<String, String> element : params.entrySet()) {
            editor.putString(element.getKey(), element.getValue());
        }

        editor.apply();
    }

    private void serverSave() {
        String urlString = Alarm.URL; // URL to call

        StringBuilder builder = new StringBuilder();
        builder.append(urlString).append("?");
        for (HashMap.Entry<String, String> element : params.entrySet()) {
            if(!element.getValue().equals(""))
                builder.append(element.getKey()).append("=").append(element.getValue()).append("&");
        }
        builder = builder.deleteCharAt(builder.length()-1);

        Log.i(NAME, builder.toString());

        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, builder.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i(NAME,"Response is: " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(NAME, "That didn't work!");
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null && error.getMessage() != null) {
                    try {
                        Log.i(NAME, error.getMessage());
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        Log.i(NAME, res);

                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    }
                }
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        activity.setQueueRequest(queue);
    }

}
