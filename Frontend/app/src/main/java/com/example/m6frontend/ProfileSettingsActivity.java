package com.example.m6frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class ProfileSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        GoogleSignInAccount currentAccount = GoogleSignIn.getLastSignedInAccount(this);
        TextView profileName = findViewById(R.id.profileName);
        TextView profileEmail = findViewById(R.id.profileEmail);
        TextView profileDescription = findViewById(R.id.profileDescription);

        if (currentAccount != null) {
            profileName.setText(currentAccount.getGivenName() + " " + currentAccount.getFamilyName());
            profileEmail.setText(currentAccount.getEmail());
        }

        ImageButton settingsButton = findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(ProfileSettingsActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });



    }
}