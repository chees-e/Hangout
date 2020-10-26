package com.example.m6frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


// TODO: add user settings
// TODO: add permission checks
public class Profile extends AppCompatActivity {
    private Button signOutButton;
    private Button chatButton;
    private Button createEventButton;
    private TextView email;
    private Button findEventButton;
    final static String TAG = "Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        email = findViewById(R.id.email);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert currentUser != null;
        Log.d(TAG, "Current User:" + currentUser.getDisplayName());

        GoogleSignInAccount currentAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (currentAccount != null) {
            email.setText(currentAccount.getEmail());
        }

        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        chatButton = findViewById(R.id.chat_button);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(chatIntent);
            }
        });

        createEventButton = findViewById(R.id.eventButton);
        createEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addEventIntent = new Intent(Profile.this, AddEventActivity.class);
                startActivity(addEventIntent);
            }
        });

        findEventButton = findViewById(R.id.find_events_button);
        findEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findEventIntent = new Intent(Profile.this, FindEventActivity.class);
                startActivity(findEventIntent);
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                String token = task.getResult().getToken();
                String message = getString(R.string.token_prefix, token);
                Log.d(TAG, message);
                // TODO: change message to something meaningful
               // Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
            }
        });

        if (checkGooglePlayServices()) {

        } else {
            Log.w(TAG, "Device doesn't have google play services");
        }
    }

    private boolean checkGooglePlayServices() {

        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);

        if (status != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Error");
            // TODO: ask user to update google play services and manage the error
            return false;
        } else {
            Log.i(TAG, "Google play services updated");
            return true;
        }
    }
    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }
}