package com.jos.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
    GoogleApiClient.ConnectionCallbacks {
    private FirebaseAuth mAuth;
    private EditText email, password;
    private FloatingActionButton fab;
    private Button sib;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton gsib;
    private Intent iGoAccount;

    private static int RC_SIGN_IN_GOOGLE = 2300;
    private static int RC_GO_ACCOUNTG = 2100;
    private static int RC_GO_ACCOUNTF = 2133;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Views
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        sib = (Button) findViewById(R.id.b_signIn);
        gsib = (SignInButton) findViewById(R.id.b_google);
        iGoAccount = new Intent(MainActivity.this, ShowAccountG.class);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestProfile()
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //Firebase instance
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("TAG", "APP SSTART");

        //neccesary to handle googleapiclient connections everytime we wanna perfom any call to the api
        mGoogleApiClient.connect();

        //SIGN IN
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                createUser(v);
            }
        });

        //LOG IN
        sib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn(v);
            }
        });

        //SIGN IN WITH GOOGLE
        //gsib.setColorScheme(SignInButton.COLOR_LIGHT);
        //gsib.setSize(SignInButton.SIZE_STANDARD);
        gsib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.b_google:
                        signInWithGoogle();
                        break;
                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInWithGoogle(result);

        } else if (requestCode == RC_GO_ACCOUNTG) {
            Log.d("TAG", "APP RESULT INTENT");
            //mGoogleApiClient.connect();
            if(data.getBooleanExtra("revokeaccess",false)==true)
            revokeAccessG();
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void createUser(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        final View va = v;
        String[] credential = getCredential();

        if (checkCredential(credential)) {
            mAuth.createUserWithEmailAndPassword(credential[0], credential[1])
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                Snackbar.make(va, "Awesome, sign in done!", BaseTransientBottomBar.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void logIn(View v) {

        final InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        final View va = v;
        String[] credential = getCredential();

        if (checkCredential(credential))
            mAuth.signInWithEmailAndPassword(credential[0], credential[1])
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                imm.hideSoftInputFromWindow(va.getWindowToken(), 0);
                                Snackbar.make(va, "Yes, your are an User", BaseTransientBottomBar.LENGTH_SHORT).show();
                                startActivityForResult(iGoAccount, RC_GO_ACCOUNTF);
                            } else
                                Toast.makeText(getApplicationContext(),
                                        "Nej, your aren't a user." +
                                                " Sign in first. The pink button!", Toast.LENGTH_SHORT).show();
                        }
                    });

    }

    private String[] getCredential() {
        String mEmail = email.getText().toString();
        String mPassword = password.getText().toString();

        return new String[]{mEmail, mPassword};
    }

    private boolean checkCredential(String[] credential) {

        if (TextUtils.isEmpty(credential[0])) {
            email.setError("Put an email fellow!!");
            return false;
        } else if (TextUtils.isEmpty(credential[1])) {
            password.setError("Ohh you forgot the password fellow!!");
            return false;
        } else
            return true;

    }

    private void signInWithGoogle() {
        Intent signInGoogleIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInGoogleIntent, RC_SIGN_IN_GOOGLE);
    }

    private void handleSignInWithGoogle(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            fireBaseAuthWithGoogle(acct);
        }
    }

    private void fireBaseAuthWithGoogle(GoogleSignInAccount acct) {
        //Make Firebase credential object from Google SignIn authorization. Use the google account id Token
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        //Sign in Firebase with that Google credential
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    startActivityForResult(iGoAccount, RC_GO_ACCOUNTG);
                } else
                    Toast.makeText(MainActivity.this, "Authentification failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void revokeAccessG() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess())
                    Log.d("TAG REVOKE ACCESS","REVOKE ACCESS OK");
                else
                    Log.d("TAG REVOKE ACCESS","REVOKE ACCESS NOK");
            }
        });

    }

}