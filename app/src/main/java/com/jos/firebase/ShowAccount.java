package com.jos.firebase;

import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShowAccount extends AppCompatActivity {

    private TextView s_name;
    private Button bDeleteUser, bLogOut;
    private FirebaseAuth mAuth;
    private FirebaseUser userF;
    private Button bReset;

    View.OnClickListener mSnackListener;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_account);

        s_name = (TextView) findViewById(R.id.s_name);
        bDeleteUser = (Button) findViewById(R.id.b_delete);
        bLogOut = (Button) findViewById(R.id.b_logout);
        bReset = (Button) findViewById(R.id.b_resetpass);

        //necess   ary to create a Firebase instance for each activity
        mAuth = FirebaseAuth.getInstance();
        userF = mAuth.getCurrentUser();

        imm =(InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onStart() {
        super.onStart();

        s_name.setText(userF.getEmail());

/*----------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------
    DELETE USER
------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------*/
        bDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(),0); //remove keyboard

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

/*----------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------
    RESET PASSWORD BY EMAIL
------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------*/
        bReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.sendPasswordResetEmail(userF.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Snackbar.make(findViewById(R.id.activity_show_db),"We have sent you " +
                                            "an email for changing the password! Check it", BaseTransientBottomBar.LENGTH_SHORT)
                                            .show();
                                }
                                else
                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG);
                            }
                        });
            }
        });
/*----------------------------------------------------------------------------------------------------------------------------------------
------------------------------------------------------------------------------------------------------------------------------------------
    LOG OUT
------------------------------------------------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------------------------------------------*/

        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Snackbar.make(v,"Aguuurr come back soon :)", BaseTransientBottomBar.LENGTH_LONG)
                        .show();
                finish();
            }
        });
    }

    protected void deleteUser(){

        userF.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    finish();
                }
                else
                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT)
                            .show();
            }
        });
    }
}
