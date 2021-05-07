package com.example.moveit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.RequestQueue;

import java.util.ArrayList;

/**
 * Save vehicles in the local device after pushing the "finish" button
 *
 */

public class SignInVehiculActivity extends AppCompatActivity {

    private final String NAME = this.getClass().getCanonicalName();

    private final int NBRE_VEHICLES = 10;

    private final ArrayList<VehiculeCreationContent.CardVehicule> vehicules = new ArrayList<>();
    private final ArrayList<CardView> modifications = new ArrayList<>();

    private LinearLayout listVehicles;
    private Button addVehicle, back, finish;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation_vehicule);

        listVehicles = findViewById(R.id.list_vehicle);

        vehicules.add(new VehiculeCreationContent.CardVehicule(vehicules.size()));

        ArrayList<View> deletes = Tools.getViewsByTag(listVehicles, "button_delete");
        for (View delete: deletes
             ) {
            delete.setOnClickListener(new deleteVehiculClickListener());
            Log.i(NAME, "Delete buttons ok");
        }

        //createCards();
        showCards();

        addVehicle = findViewById(R.id.add_vehicle_home);
        addVehicle.setOnClickListener(new addVehicleClickListener());

        back = findViewById(R.id.button_vehicles_creation_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInVehiculActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        finish = findViewById(R.id.button_vehicles_creation_finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //saveVehicles(null);

                //Save vehicles in local device
                SaveVehicules saveVehicules = new SaveVehicules(getApplicationContext(), vehicules, SignInVehiculActivity.this);
                saveVehicules.run();

                Intent intent = new Intent(SignInVehiculActivity.this, ConfirmationActivity.class);
                startActivity(intent);
            }
        });
    }

    private void showCards(){
        ConstraintLayout layout;
        CardView card;
        VehiculeCreationContent.CardVehicule vehicule;
        EditText editText;

        ArrayList<View> constraintList = Tools.getViewsByTag(listVehicles, "constraint_vehicles");
        ArrayList<View> card_vehicle= Tools.getViewsByTag(listVehicles, "card_vehicle");


        for(int i = 0; i < vehicules.size(); i++){
            card = (CardView) card_vehicle.get(i);
            vehicule = vehicules.get(i);

            editText = card.findViewWithTag("plate");
            editText.setText(vehicule.getPlate());

            editText = card.findViewWithTag("mark");
            editText.setText(vehicule.getMark());

            editText = card.findViewWithTag("model");
            editText.setText(vehicule.getModel());

            editText = card.findViewWithTag("type");
            editText.setText(vehicule.getType());

            card.setVisibility(View.VISIBLE);

            card.getChildAt(0).setTag(i);

            layout = (ConstraintLayout)constraintList.get(i);
            layout.setMaxHeight(Integer.MAX_VALUE);

        }

        //the first form has a "i" button instead of the "x" button
        card = (CardView) card_vehicle.get(0);
        Button delete = card.findViewWithTag("button_delete");
        delete.setText("i");
        delete.setClickable(false);

        hideSupernumeraryCards();
    }




    //make the other cards invisible
    private void hideSupernumeraryCards() {
        /*
         *Si 10 cards et 2 vehicules
         *  8 cards à invisibiliser
         *  on commence à l'index 2
         *  et on fini à l'index 9
         */
        ArrayList<View> list = Tools.getViewsByTag(listVehicles, "card_vehicle");
        CardView card;
        ConstraintLayout layout;

        for(int i = vehicules.size(); i < NBRE_VEHICLES; i++){
            card = (CardView) list.get(i);
            card.setVisibility(View.INVISIBLE);

            layout = (ConstraintLayout)card.getParent();
            layout.setMaxHeight(0);

        }
    }

    public void setRequestQueue(RequestQueue queue) {
        requestQueue = queue;
    }


    private class addVehicleClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            saveVehicles(null);
            vehicules.add(new VehiculeCreationContent.CardVehicule(vehicules.size()-1));
            Log.i(NAME, "vehicle count: "+ vehicules.size());
            //createCards();
            showCards();
        }
    }

    private class deleteVehiculClickListener implements View.OnClickListener {

        //We get all the text in the textFields and put the informations in the vehicles cards list
        @Override
        public void onClick(View view) {

            saveVehicles(view);

            //We delete the last one
            vehicules.remove(vehicules.size()-1);

            //refresh the cards
            showCards();
        }
    }

    private void saveVehicles(View view) {

        String plate, mark, model, type;
        int j = 0;

        ArrayList plates = Tools.getViewsByTag(listVehicles, "plate");
        ArrayList marks = Tools.getViewsByTag(listVehicles, "mark");
        ArrayList models = Tools.getViewsByTag(listVehicles, "model");
        ArrayList types = Tools.getViewsByTag(listVehicles, "type");

        int tag;
        if(view != null) {
            tag = (int) ((LinearLayout) view.getParent()).getTag();
        }else{
            tag = -1;
        }
        for (int i = 0; i < vehicules.size(); i++){

            //if the vehicle is the one who will be deleted, we skip it
            //if the vehicle is after the one who will be deleted, the information of the form go for the previous vehicle
            if(i == tag){
                continue;
            }

            VehiculeCreationContent.CardVehicule vehicule = vehicules.get(j);

            plate =((EditText) plates.get(i)).getText().toString();
            mark =((EditText) marks.get(i)).getText().toString();
            model =((EditText) models.get(i)).getText().toString();
            type =((EditText) types.get(i)).getText().toString();

            vehicule.setPlate(plate);
            vehicule.setMark(mark);
            vehicule.setModel(model);
            vehicule.setType(type);

            Log.i(NAME, "Vehicule " + j + ": "+vehicule.toString());

            j++;
        }
    }

    /*
    @Override
    protected void onStop() {
        super.onStop();
        requestQueue.stop();
    }

   */
}
