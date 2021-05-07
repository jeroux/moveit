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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class SaveVehicules implements Runnable {
    private final String NAME = this.getClass().getCanonicalName();

    private Context context;
    private ArrayList<VehiculeCreationContent.CardVehicule> vehicules;
    private SignInVehiculActivity activity;

    SaveVehicules(Context context, ArrayList<VehiculeCreationContent.CardVehicule> vehicules, SignInVehiculActivity activity) {
        this.context = context;
        this.vehicules = vehicules;
        this.activity = activity;
    }


    @Override
    public void run() {

        saveLocal();
        saveServer();

    }

    private void saveServer() {
        String urlString = Alarm.URL; // URL to call

        StringBuilder builder = new StringBuilder();
        builder.append(urlString).append("?");

        //
        int vehicleNbr = 0;
        HashMap<String, String> param = new HashMap<String, String>();
        for (VehiculeCreationContent.CardVehicule vehicle: vehicules
        ) {
            builder.append("plate" + vehicleNbr + "=" + vehicle.getPlate() + "&");
            System.out.println("plate"+vehicleNbr +" => " + vehicle.getPlate());
            builder.append("mark" + vehicleNbr + "=" + vehicle.getMark() + "&");
            System.out.println("mark"+vehicleNbr +" => " + vehicle.getMark());
            builder.append("model" + vehicleNbr + "=" + vehicle.getModel() + "&");
            System.out.println("model"+vehicleNbr +" => " + vehicle.getModel());
            builder.append("type" + vehicleNbr + "=" + vehicle.getType() + "&");
            System.out.println("type"+vehicleNbr +" => " + vehicle.getType());
            builder.append("id_owner" + vehicleNbr + "=1&");
            vehicleNbr++;
        }
        param.put("nbrVehicles", String.valueOf(vehicleNbr));
        System.out.println("nbrVehicles: " + vehicleNbr);


        //
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
        queue.stop();

    }

    /*private void saveServer() {
        String urlString = Alarm.URL; // URL to call

        RequestQueue queue = Volley.newRequestQueue(context);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlString,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Response is: " + response);
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
        })

        {
            @Override
            protected Map<String, String> getParams(){
                int vehicleNbr = 0;
                HashMap<String, String> param = new HashMap<String, String>();
                for (VehiculeCreationContent.CardVehicule vehicle: vehicules
                     ) {
                    param.put("plate"+vehicleNbr, vehicle.getPlate());
                    System.out.println("plate"+vehicleNbr +" => " + vehicle.getPlate());
                    param.put("mark"+vehicleNbr, vehicle.getMark());
                    System.out.println("mark"+vehicleNbr +" => " + vehicle.getMark());
                    param.put("model"+vehicleNbr, vehicle.getModel());
                    System.out.println("model"+vehicleNbr +" => " + vehicle.getModel());
                    param.put("type"+vehicleNbr, vehicle.getType());
                    System.out.println("type"+vehicleNbr +" => " + vehicle.getType());
                    param.put("id_owner"+vehicleNbr, "1");
                    vehicleNbr++;
                }
                param.put("nbrVehicles", String.valueOf(vehicleNbr));
                System.out.println("nbrVehicles: " + vehicleNbr);
                return param;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
        activity.setRequestQueue(queue);

    }
    */
    private void saveLocal() {
        SharedPreferences sharedPref = context.getSharedPreferences(
                "vehicules_saveFile", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("vehicules_count", vehicules.size());

        for (int i = 0; i < vehicules.size(); i++) {
            VehiculeCreationContent.CardVehicule vehicule = vehicules.get(i);
            editor.putString("plate" + i, vehicule.getPlate());
            editor.putString("mark" + i, vehicule.getMark());
            editor.putString("model" + i, vehicule.getModel());
            editor.putString("type" + i, vehicule.getType());

        }

        editor.apply();
    }
}
