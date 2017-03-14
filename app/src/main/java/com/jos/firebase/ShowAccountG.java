package com.jos.firebase;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShowAccountG extends AppCompatActivity{

    private TextView gNameV,gEmailV;
    private Button bDeleteUser, bLogOut, bReset;
    private String gName,gEmail;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    View.OnClickListener mSnackListener;
    private Intent returnI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_account);

        mAuth = FirebaseAuth.getInstance();

        gNameV = (TextView) findViewById(R.id.g_name);
        gEmailV = (TextView) findViewById(R.id.g_mail);
        bDeleteUser = (Button) findViewById(R.id.b_delete);
        bLogOut = (Button) findViewById(R.id.b_logout);
        bReset = (Button) findViewById(R.id.b_resetpass);
        returnI = new Intent();

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null) {

                }
                else {
                   errorgoback();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserData();

        bDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.activity_show_db),"Do you want to delete your account?", BaseTransientBottomBar.LENGTH_LONG)
                        .setAction("DELETE", mSnackListener) //Set a button in the Snackbar
                        .show();
            }
        });
        mSnackListener = new View.OnClickListener(){ //Listener and Click for the Snackbar button
            @Override
            public void onClick(View v) {
                deleteUser();

            }
        };

        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });
    }

    private void getUserData(){
        user = mAuth.getCurrentUser();
        gName = user.getDisplayName();
        gEmail = user.getEmail();
        gNameV.setText(gName);
        gEmailV.setText(gEmail);
    }
    private void errorgoback(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Toast.LENGTH_SHORT);
                    ShowAccountG.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        Toast.makeText(getApplicationContext(), "Error questing Google Sign In", Toast.LENGTH_SHORT);
        thread.start();
    }
    private void deleteUser(){
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    returnI.putExtra("revokeaccess",true);
                    setResult(RESULT_OK,returnI);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }
    private void logOut(){
        mAuth.removeAuthStateListener(mAuthListener);
        mAuth.signOut();
        returnI.putExtra("revokeaccess",false);
        setResult(RESULT_OK,returnI);
        finish();
    }
}

