package com.example.m6frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class ProfileSettingsActivity extends AppCompatActivity {
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    EditText profileLocation;
    private static final String TAG = "ProfileSettingsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        GoogleSignInAccount currentAccount = GoogleSignIn.getLastSignedInAccount(this);
        TextView profileName = findViewById(R.id.profileName);
        TextView profileEmail = findViewById(R.id.profileEmail);
        profileLocation = findViewById(R.id.profileLocation);
        profileLocation.setInputType(InputType.TYPE_NULL);

        // gets location
        Places.initialize(getApplicationContext(), getResources().getString(R.string.GOOGLE_MAPS_API_KEY));

        profileLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getApplicationContext());
                startActivityForResult(intent,AUTOCOMPLETE_REQUEST_CODE );
            }
        });


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

        Button profileConfirm = findViewById(R.id.profileConfirm);
        profileConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileSettingsActivity.this, "Profile Saved", Toast.LENGTH_LONG).show();
            }
        });

        // TODO: get profile picture
        ImageView profilePicture = findViewById(R.id.profileViewPicture);
        Glide.with(this)
                .load(currentAccount.getPhotoUrl())
                .circleCrop()
                .into(profilePicture);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                profileLocation.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.i(TAG, "REQUEST CANCELED");
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}