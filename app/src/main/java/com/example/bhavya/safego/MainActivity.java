package com.example.bhavya.safego;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Created by bhavya on 4/4/18.
 */


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account==null)
        {
            Intent intent = new Intent(MainActivity.this,LoggedOut.class);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(MainActivity.this,LoggedIn.class);
            intent.putExtra("account",account);
            startActivity(intent);
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
