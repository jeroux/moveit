package com.example.moveit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private final String NAME = this.getClass().getCanonicalName();
    private RequestQueue queueRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //On choisi +32 comme valeur par défault
        int pos = 3;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Spinner spinnerPrefix = findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapterPrefix = ArrayAdapter.createFromResource(this, R.array.prefix, android.R.layout.simple_spinner_item);

        adapterPrefix.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerPrefix.setAdapter(adapterPrefix);
        spinnerPrefix.setSelection(pos);

        Button nextButton = findViewById(R.id.next_button_signIn);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
                Intent intent = new Intent(SignInActivity.this, SignInVehiculActivity.class);
                startActivity(intent);
            }
        });
    }


    private void save() {
        EditText EfName, ElName, Ephone, Email, Epsw;
        Spinner Eprefix;
        EfName = findViewById(R.id.signIn_firstnamefield);
        ElName = findViewById(R.id.signin_lastnamefield);
        Ephone = findViewById(R.id.signin_phonefield);
        Email = findViewById(R.id.signin_emailfield);
        Epsw = findViewById(R.id.signin_passwordfield);
        Eprefix = findViewById(R.id.spinner);

        String fname, lname, phone, mail, psw, prefix;
        fname = EfName.getText().toString();
        lname = ElName.getText().toString();
        phone = Ephone.getText().toString();
        mail = Email.getText().toString();
        psw = Epsw.getText().toString();
        prefix = String.valueOf(Eprefix.getSelectedItemPosition());

        HashMap<String, String> params = new HashMap<>();
        params.put("fname", fname);
        params.put("lname", lname);
        params.put("phone", phone);
        params.put("mail", mail);
        params.put("psw", psw);
        params.put("prefix", prefix);

        SaveProfile save = new SaveProfile(getApplicationContext(), params, this);
        save.run();

        Log.i(NAME, "Profil saved");
    }

    public void setQueueRequest(RequestQueue queue) {
        queueRequest = queue;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(queueRequest != null)
            queueRequest.stop();
    }
}
