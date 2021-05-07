package com.example.moveit;

import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private final String NAME = MainActivity.class.getCanonicalName();
    private String token;
    int isGooglePlayServicesAvailable = ConnectionResult.SERVICE_MISSING;
    private GoogleSignInAccount googleSignInAccount;
    private GoogleSignInClient googleSignInClient;
    private AccountManager am;
    private SignInButton signUpGoogle;
    private FirebaseAuth mAuth;
    private CoordinatorLayout mainFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mainFrame = findViewById(R.id.mainFrame);
        Button logIn = findViewById(R.id.bouton_LogIn);
        Button signUp = findViewById(R.id.bouton_SignUp);
        signUpGoogle = findViewById(R.id.google_signIn);

        Button signUpTest = findViewById(R.id.button_SignUp_tests);

        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });


        signUpGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                googleSignIn();

                //createAccount("email", "password");
            }
        });

        signUpTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignInActivity();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        // check if user is signed with google
        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        //TODO:
        //updateUIWithGoogle(googleSignInAccount);

        // Check if user is signed in (non-null)
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser); //TODO

    }

    private void startSignInActivity(){
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(
                                Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(), //EMAIL
                                        new AuthUI.IdpConfig.GoogleBuilder().build())) // SUPPORT GOOGLE
                        .setIsSmartLockEnabled(false, true)
                        .build(),
                RC_SIGN_IN);
    }

    private void googleSignIn(){
        //TODO check for existing Google account
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();

        googleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);

        googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
        updateUIWithGoogle(googleSignInAccount);

    }

    private void createAccount(String email, String password) {

        mAuth.createUserWithEmailAndPassword("email", "password")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(NAME, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(NAME, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(NAME, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(NAME, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser user) {
        //TODO
        // account is not null the user has already signed in with firebase and can go to next step
        // if(user != null}....

    }

    private void updateUIWithGoogle(GoogleSignInAccount account){
        //TODO
        // account is not null the user has already signed in with google and can go to next step
        // if(account != null}....
    }

    @Override
    protected void onResume() {
        super.onResume();
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        isGooglePlayServicesAvailable = googleApiAvailability.isGooglePlayServicesAvailable(this);
        if(isGooglePlayServicesAvailable != ConnectionResult.SUCCESS){
            Dialog dialog = googleApiAvailability.getErrorDialog(this, isGooglePlayServicesAvailable, ConnectionResult.SUCCESS);
            dialog.show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(this.getClass().getCanonicalName(), "resultat connection: ");
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Snackbar.make(this.mainFrame, getString(R.string.connection_succeed), Snackbar.LENGTH_SHORT).show();
                Log.i(this.getClass().getCanonicalName(), "réussi");
            } else { // ERRORS
                if (response == null) {
                    Snackbar.make(this.mainFrame, getString(R.string.error_authentication_canceled), Snackbar.LENGTH_LONG)
                    .show();
                    Log.i(this.getClass().getCanonicalName(), "echec");
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Log.i(this.getClass().getCanonicalName(), "échec pas de réseau");
                    Snackbar.make(this.mainFrame, getString(R.string.error_no_internet), Snackbar.LENGTH_LONG).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Log.i(this.getClass().getCanonicalName(), "échec erreur inconnue");
                    Snackbar.make(this.mainFrame, getString(R.string.error_unknown_error), Snackbar.LENGTH_LONG).show();
                }
            }
        }
    }


    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        public void run(AccountManagerFuture result) {
            try {
                Bundle bundle = (Bundle) result.getResult();
                token = bundle.getString((AccountManager.KEY_AUTHTOKEN));

                Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, 0);
                    return;
                }
            } catch (AuthenticatorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OperationCanceledException e) {
                e.printStackTrace();
            }

        }

    }


    private class OnError implements Handler.Callback {
        @Override
        public boolean handleMessage(Message message) {
            Log.e(NAME, message.toString());
            return false;
        }
    }
}