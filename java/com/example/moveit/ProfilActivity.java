package com.example.moveit;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfilActivity extends AppCompatActivity {
    private static final int SIGN_OUT_TASK = 10;
    private static final int DELETE_USER_TASK = 20;

    private SharedPreferences sharedPref;
    EditText fname, lname, phone;
    TextView mail;
    Button modifyEmailButton, modifyPasswordButton, cancelModifyEmail, cancelModifyPassword, confirmationModification, btnRetour,
        deleteAccountButton;
    LinearLayout modifyEmailFields, modifyPasswordFields;
    Spinner prefix;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        sharedPref = getApplicationContext().getSharedPreferences("profile_saveFile", Context.MODE_PRIVATE);
        modifyEmailFields = findViewById(R.id.modify_email_fields);
        modifyPasswordFields = findViewById(R.id.modify_password_fields);

        fillFields();

        setButtons();
    }

    private void fillFields() {
        fname = findViewById(R.id.fname);
        lname = findViewById(R.id.lname);
        phone = findViewById(R.id.profil_phone);

        mail = findViewById(R.id.profil_email);

        prefix = findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adapterPrefix = ArrayAdapter.createFromResource(this, R.array.prefix, android.R.layout.simple_spinner_item);
        adapterPrefix.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prefix.setAdapter(adapterPrefix);

        fname.setText(sharedPref.getString("fname", "?"));
        lname.setText(sharedPref.getString("lname", "?"));
        phone.setText(sharedPref.getString("phone", "?"));
        mail.setText(sharedPref.getString("mail", "?"));
        prefix.setSelection(Integer.parseInt(sharedPref.getString("prefix", "0")));
    }

    private void setButtons() {
        modifyEmailButton = findViewById(R.id.modify_email);
        modifyPasswordButton = findViewById(R.id.modify_password);
        cancelModifyEmail = findViewById(R.id.cancel_modify_email);
        cancelModifyPassword = findViewById(R.id.cancel_modify_password);
        btnRetour = findViewById(R.id.ActivityProfil_retour);
        confirmationModification = findViewById(R.id.btn_confirm_modificationProfile);
        deleteAccountButton = findViewById(R.id.delete_account);

        modifyEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyEmailButton.setVisibility(View.INVISIBLE);
                modifyEmailFields.setVisibility(View.VISIBLE);
                modifyEmailFields.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            }
        });

        modifyPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyPasswordButton.setVisibility(View.INVISIBLE);
                modifyPasswordFields.setVisibility(View.VISIBLE);
                modifyPasswordFields.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            }
        });

        cancelModifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyEmailFields.setVisibility(View.INVISIBLE);
                modifyEmailFields.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                modifyEmailButton.setVisibility(View.VISIBLE);
            }
        });

        cancelModifyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyPasswordFields.setVisibility(View.INVISIBLE);
                modifyPasswordFields.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
                modifyPasswordButton.setVisibility(View.VISIBLE);
            }
        });

        btnRetour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfilActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        confirmationModification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPref.edit()
                        .putString("fname", fname.getText().toString())
                        .putString("lname", lname.getText().toString())
                        .putString("phone", phone.getText().toString())
                        .putString("prefix",String.valueOf(prefix.getSelectedItemPosition()))
                        .putString("mail", mail.getText().toString()).apply();

                Intent intent = new Intent(ProfilActivity.this, HomeActivity.class);
                startActivity(intent);

            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProfilActivity.this)
                        .setMessage("Are you sure you want to delete your profil?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("Delete account", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUserFromFirebase();
                            }
                        }).show();
            }
        });
    }

    private void deleteUserFromFirebase(){
        if (this.getCurrentUser() != null) {
            AuthUI.getInstance()
                    .delete(this)
                    .addOnSuccessListener(this, this.updateUIAfterRESTRequestsCompleted(DELETE_USER_TASK));
        }
    }


    // --------------------
    // TOOLS
    // --------------------


    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }
    // --------------------
    // UI
    // --------------------



    private OnSuccessListener<Void> updateUIAfterRESTRequestsCompleted(final int origin){
        return new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                switch (origin){
                    case SIGN_OUT_TASK:
                        finish();
                        break;
                    case DELETE_USER_TASK:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

}
