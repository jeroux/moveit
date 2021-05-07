package com.example.moveit;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class VehiculeCardviewHome {
    private final HomeActivity activity;
    private Context context;
    private SharedPreferences sharedPref;
    private final ConstraintLayout layoutVehicle;
    private EditText plateEdit, markEdit, modelEdit, typeEdit;
    private String tag;
    private int index;
    private boolean newVehicle;


    VehiculeCardviewHome(HomeActivity activity, View view, Boolean newVehicle) {
        this.newVehicle = newVehicle;
        this.activity = activity;
        context = activity.getApplicationContext();

        tag = String.valueOf(view.getTag());
        index = Integer.parseInt(tag);

        layoutVehicle = (ConstraintLayout) LayoutInflater.from(context).inflate(R.layout.vehicule_cardview_home, null);

        sharedPref = context.getSharedPreferences("vehicules_saveFile", Context.MODE_PRIVATE);

        creationButtonConfirmation();
        creationButtonCancel();

        if(newVehicle){
            completingEditTextDefault();
        }else{
            completingEditText();
        }


    }

    private void completingEditTextDefault() {
        plateEdit = (EditText) Tools.getViewsByTag(layoutVehicle, "plate").get(0);
        plateEdit.setHint(R.string.plate_number);

        markEdit = (EditText) Tools.getViewsByTag(layoutVehicle, "mark").get(0);
        markEdit.setHint(R.string.mark_of_vehicule);

        modelEdit = (EditText) Tools.getViewsByTag(layoutVehicle, "model").get(0);
        modelEdit.setHint(R.string.model_of_vehicule);

        typeEdit = (EditText) Tools.getViewsByTag(layoutVehicle, "type").get(0);
        typeEdit.setHint(R.string.vehicule_type);
    }

    private void creationButtonConfirmation() {
        ArrayList<View> list = Tools.getViewsByTag(layoutVehicle, "CONFIRM");
        Button button = (Button) list.get(0);
        button.setTag(tag);
        button.setOnClickListener(new OnClickConfirmationListener());
    }

    class OnClickConfirmationListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            int vehiclesCount = sharedPref.getInt("vehicules_count", 2);
            sharedPref.edit().putString("plate" + tag, plateEdit.getText().toString())
                    .putString("model" + tag, modelEdit.getText().toString())
                    .putString("mark" + tag, markEdit.getText().toString())
                    .putString("type" + tag, typeEdit.getText().toString())
                    .putInt("vehicules_count", ++vehiclesCount).apply();
            activity.getLayout().removeViewAt(index);
            if(!newVehicle){
                activity.getListVehicles().remove(index);
            }else{
                activity.getListVehicles().add(layoutVehicle);
            }
            activity.createVehicleView(index);
        }
    }

    private void creationButtonCancel() {
        ArrayList<View> list = Tools.getViewsByTag(layoutVehicle, "CANCEL");
        Button button = (Button) list.get(0);
        button.setTag(tag);
        button.setOnClickListener(new OnClickCancelListener());
    }

    private class OnClickCancelListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            activity.getLayout().removeViewAt(index);
            if(!newVehicle) {
                activity.getListVehicles().remove(index);
                activity.createVehicleView(index);
            }
        }
    }

    private void completingEditText() {

        String plate = sharedPref.getString("plate" + tag, "plaque");
        String mark = sharedPref.getString("mark" + tag, "marque");
        String model = sharedPref.getString("model" + tag, "modele");
        String type = sharedPref.getString("type" + tag, "type");

        plateEdit = (EditText) Tools.getViewsByTag(layoutVehicle, "plate").get(0);
        plateEdit.setText(plate);

        markEdit = (EditText) Tools.getViewsByTag(layoutVehicle, "mark").get(0);
        markEdit.setText(mark);

        modelEdit = (EditText) Tools.getViewsByTag(layoutVehicle, "model").get(0);
        modelEdit.setText(model);

        typeEdit = (EditText) Tools.getViewsByTag(layoutVehicle, "type").get(0);
        typeEdit.setText(type);
    }

    public ConstraintLayout getLayout() {
        return layoutVehicle;
    }


}

