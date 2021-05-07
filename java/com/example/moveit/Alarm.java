package com.example.moveit;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

class Alarm {
    private static final String NAME = Alarm.class.getCanonicalName();
    static final String URL = "http://yhhcrxa.cluster020.hosting.ovh.net";
    //static final String URL = "https://quadrifid-brains.000webhostapp.com";
    //static final String URL = "http://2a02:a03f:3eaa:8800:e11f:a261:a131:d17d:80//C:/wamp64/www/moveit/index.php";
    static final int TAILLE_MESSAGE = 11;

    static void alarm(Activity activity, View view) {

        String urlAlarm =URL + "?alarm=false";

        final Button button = (Button) view;
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(activity.getBaseContext());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlAlarm,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first TAILLE_MESSAGE characters of the response string.
                        button.setText("Response is: " + response);
                        Log.i(NAME, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("That didn't work!" + error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
