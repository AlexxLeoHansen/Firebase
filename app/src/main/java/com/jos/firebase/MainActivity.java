package com.jos.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText email,password;
    private FloatingActionButton fab;
    private Button sib;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        sib = (Button) findViewById(R.id.b_signIn);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
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

            }

    @Override
    public void onStop() {
        super.onStop();

    }

    protected void createUser(View v) {
        InputMethodManager imm =(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
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
                                Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
    protected void logIn(View v) {

        final InputMethodManager imm =(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        final View va = v;
        String[] credential = getCredential();
        final Intent i = new Intent(MainActivity.this,ShowAccount.class);

        if (checkCredential(credential)) {
            mAuth.signInWithEmailAndPassword(credential[0], credential[1])
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                imm.hideSoftInputFromWindow(va.getWindowToken(), 0);
                                Snackbar.make(va, "Yes, your are an User", BaseTransientBottomBar.LENGTH_SHORT).show();
                                startActivity(i);
                            }
                            else
                                Toast.makeText(getApplicationContext(),
                                        "Nej, your aren't a user." +
                                                " Sign in first. The pink button!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }
    protected String[] getCredential(){
        String mEmail = email.getText().toString();
        String mPassword = password.getText().toString();

        return new String[]{mEmail,mPassword};
    }
    protected boolean checkCredential(String[] credential){

        if (TextUtils.isEmpty(credential[0])) {
            email.setError("Put an email fellow!!");
            return false;
        }
        else if (TextUtils.isEmpty(credential[1])) {
            password.setError("Ohh you forgot the password fellow!!");
            return false;
        }
        else
            return true;

    }
}


