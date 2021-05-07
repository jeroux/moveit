package com.example.moveit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ConfirmationFragment.NoticeDialogListener, PopupMenu.OnMenuItemClickListener {

    private final String NAME = this.getClass().getCanonicalName();
    private HomeActivity activity;
    private ArrayList<ConstraintLayout> listVehicles = new ArrayList<>();
    private LinearLayout layout;
    private Button add_vehicle, alarm;
    SharedPreferences sharedPref;
    int vehiclesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        activity = this;
        sharedPref = getApplicationContext().getSharedPreferences("vehicules_saveFile", Context.MODE_PRIVATE);


        vehiclesCount = sharedPref.getInt("vehicules_count", 0);

        for(int i = 0; i < vehiclesCount; i++) {
            createVehicleView(i);
        }

        add_vehicle = findViewById(R.id.add_vehicle_home);
        add_vehicle.setOnClickListener(new OnClickaddVehicle());

        alarm = findViewById(R.id.alarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alarm.alarm(HomeActivity.this, view);
            }
        });
    }

    protected void createVehicleView(int i) {

        ConstraintLayout layoutVehicle;
        String text;

        ImageButton delete, edit;

        //Log.i(NAME, "sharePref: "+sharedPref.toString());

        layout = findViewById(R.id.list_vehicles_home);


        // Create a vehicle and modify the text in the switch widget
        layoutVehicle =  (ConstraintLayout)LayoutInflater.from(this).inflate(R.layout.vehicle, null);

        Switch plaque =  (Switch)Tools.getViewsByTag(layoutVehicle, "switch").get(0);
        text = sharedPref.getString("plate"+i, "numero par défaut");
        plaque.setText(text);

        //add tag's on the delete and edit buttons

        delete = (ImageButton) Tools.getViewsByTag(layoutVehicle, "deleteVehicles").get(0);
        delete.setTag(String.valueOf(i));
        delete.setOnClickListener(new OnDeleteVehicleListener());

        edit = (ImageButton) Tools.getViewsByTag(layoutVehicle, "editVehicles").get(0);
        edit.setTag(String.valueOf(i));
        edit.setOnClickListener(new OnEditVehicleListener());

        layout.addView(layoutVehicle, i);

        listVehicles.add(layoutVehicle);


    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        String tag = dialog.getTag();
        Log.i(NAME, "Tag delete: " + tag);

        // remove the vehicle from model of the activity and view
        assert tag != null;
        int id = Integer.parseInt(tag);
        listVehicles.remove(id);
        layout.removeViewAt(id);

        // remove the vehicle from the application model
        // minus by 1 the name of all the next vehicles in the application model
        String plate, mark, model, type;
        int i;
        for(i = id; i < vehiclesCount-1; i++){
            plate = sharedPref.getString("plate"+(i+1), null);
            mark = sharedPref.getString("mark"+(i+1), null);
            model = sharedPref.getString("model"+(i+1), null);
            type = sharedPref.getString("type"+(i+1), null);

            sharedPref.edit().remove("plate"+i)
                    .remove("model"+i).remove("mark"+i).remove("type"+i).apply();

            sharedPref.edit()
                    .putString("plate"+i, plate).putString("mark"+i, mark).putString("model"+i, model).putString("type"+i, type)
                    .apply();
        }

        //and delete the last vehicle
        i++;
        sharedPref.edit().remove("plate"+i)
                .remove("model"+i).remove("mark"+i).remove("type"+i).apply();


        // minus by 1 the count vehicle in the appllication model
        sharedPref.edit().remove("vehicules_count").putInt("vehicules_count", vehiclesCount-1).apply();

        vehiclesCount--;

        minusTags(id);

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        return;
    }


    private class OnDeleteVehicleListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // show Dialog ConfirmationFragment for confirmation
            ConfirmationFragment confirmationFragment = new ConfirmationFragment();
            confirmationFragment.show(getSupportFragmentManager(), String.valueOf(view.getTag()));
        }
    }

    private void minusTags(int id) {
        ConstraintLayout vehicle;
        ArrayList<View> views;
        ImageButton button;

        for(int i = id; i < vehiclesCount; i++){
            vehicle = (ConstraintLayout)layout.getChildAt(i);
            views = Tools.getViewsByTag(vehicle, String.valueOf(i+1));

            for (View view: views
                 ) {
                button = (ImageButton)view;
                button.setTag(String.valueOf(i));
            }
        }
    }

    private class OnEditVehicleListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            /*
             * Remove the vehicle view of the edit button clicked,
             * Create a new vehicle_cardview_home view who take the place of the vehicle view deleted
             */
            String tag = String.valueOf(view.getTag());
            Log.i(NAME, "Tag edit: " + tag);

            int index = Integer.parseInt(tag);

            layout.removeViewAt(index);

            VehiculeCardviewHome vehiculeCardviewHome = new VehiculeCardviewHome(activity, view, false);

            ConstraintLayout layoutVehicle = vehiculeCardviewHome.getLayout();

            layout.addView(layoutVehicle, index);

            Log.i(NAME, "Add vehicle_cardview_home");

        }

    }



    public ArrayList<ConstraintLayout> getListVehicles() {
        return listVehicles;
    }

    public LinearLayout getLayout() {
        return layout;
    }

    private class OnClickaddVehicle implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.i(NAME, "ajout vehicle");
            if (layout != null) {
                view.setTag(String.valueOf(layout.getChildCount() - 1));
            } else {
                view.setTag("0");
            }
            VehiculeCardviewHome vehiculeCardviewHome = new VehiculeCardviewHome(activity, view, true);
            ConstraintLayout layoutVehicle = vehiculeCardviewHome.getLayout();

            listVehicles.add(layoutVehicle);
            if (layout != null) {
                layout.addView(layoutVehicle, layout.getChildCount() - 1);
            } else {
                layout.addView(layoutVehicle, 0);
            }
            layout.refreshDrawableState();

        }
    }

    public void popMenu(View view){
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu, popupMenu.getMenu());
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        Class destination = HomeActivity.class;
        Boolean retour;
        switch (menuItem.getItemId()){
            case R.id.menu_btn_home:
                destination = HomeActivity.class;
                retour = true;
            case R.id.menu_btn_account:
                // TODO
                destination = ProfilActivity.class;
                retour = true;
            case R.id.menu_btn_faq:
                // TODO
                //destination = FAQActivity.class;
                retour = true;
            case R.id.menu_btn_contact:
                // TODO
                //destination = ContactActivity.class;
                retour = true;
            case R.id.menu_btn_logout:
                // TODO
                retour = true;
            default:
                retour = false;
        }
        Log.i(NAME, "Redirection vers: "+destination.getCanonicalName());
        Intent intent = new Intent(HomeActivity.this, destination);
        startActivity(intent);
        return retour;
    }
}
