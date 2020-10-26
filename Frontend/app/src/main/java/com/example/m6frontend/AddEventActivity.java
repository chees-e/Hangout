package com.example.m6frontend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;



// TODO: implement adding additional users
// TODO: check if users are valid
// TODO: fix tap responsiveness of time/date selectors
// TODO: improve location selector (maps integration?)
public class AddEventActivity extends AppCompatActivity {
    private final String TAG = "AddEventActivity";
    private static int AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText eventName;
    private EditText locationName;
    private EditText descriptionName;
    private EditText usersName;

    private EditText startDate;
    private EditText startTime;
    private EditText endDate;
    private EditText endTime;
    private DatePickerDialog datePicker;
    private TimePickerDialog timePicker;

    private Button addEventButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);
        addEventButton = findViewById(R.id.add_event_button);

        eventName = findViewById(R.id.editTextEvent);

        locationName = findViewById(R.id.editTextLocation);
        locationName.setInputType(InputType.TYPE_NULL);

        descriptionName = findViewById(R.id.editTextEventDescription);

        usersName = findViewById(R.id.editTextAddUsers);

        startDate = findViewById(R.id.editTextStartDate);
        startTime = findViewById(R.id.editTextStartTime);
        startDate.setInputType(InputType.TYPE_NULL);
        startTime.setInputType(InputType.TYPE_NULL);

        endDate = findViewById(R.id.editTextEndDate);
        endTime = findViewById(R.id.editTextEndTime);
        endDate.setInputType(InputType.TYPE_NULL);
        endTime.setInputType(InputType.TYPE_NULL);


        // gets location
        Places.initialize(getApplicationContext(), "AIzaSyDSs9dhmEDSx0JH6e_slWAD3-P1l5F6-YY");
        PlacesClient placesClient = Places.createClient(this);
        locationName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

                // Start the autocomplete intent.
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                        .build(getApplicationContext());
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
            }
        });

        // gets start date
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(AddEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                startDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                datePicker.show();

            }
        });

        // gets start time
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                timePicker = new TimePickerDialog(AddEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                startTime.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                timePicker.show();

            }

        });

        // gets end date
        endDate.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_WEEK);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                datePicker = new DatePickerDialog(AddEventActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                endDate.setText(year + "-" + (month + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                datePicker.show();

            }
        });

        // gets end time
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                timePicker = new TimePickerDialog(AddEventActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                endTime.setText(hourOfDay + ":" + minute);
                            }
                        }, hour, minute, true);
                timePicker.show();

            }
        });


        // TODO: connect to backend
        // TODO: error checking
        addEventButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (isEmpty(eventName) || isEmpty(locationName) || isEmpty(descriptionName) ||
                        isEmpty(startDate) || isEmpty(startTime) || isEmpty(endDate) || isEmpty(endTime)) {
                    Toast.makeText(AddEventActivity.this, "Please enter the required fields", Toast.LENGTH_LONG).show();
                    return;
                }
                try {
                    String jsonString = new JSONObject()
                            .put("summary", eventName.getText())
                            .put("location", locationName.getText())
                            .put("description", descriptionName.getText())
                            .put("start", new JSONObject().put("dateTime", startDate.getText() + "T" + startTime.getText()))
                            .put("end", new JSONObject().put("dateTime", endDate.getText() + "T" + endTime.getText()))
                            .put("attendees", new JSONArray().put("email:" + currentUser.getEmail()))
                            .toString();
                    Toast.makeText(AddEventActivity.this, jsonString, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestQueue requestQueue = Volley.newRequestQueue(AddEventActivity.this);
                String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/";
                
               String jsonString = null;
                try {
                     jsonString = new JSONObject()
                            .put("id", currentUser.getUid())
                            .put("name", eventName.getText())
                            .put("desc", descriptionName.getText())
                            .put("start",startDate.getText() + "T" + startTime.getText())
                            .put("end", endDate.getText() + "T" + endTime.getText())
                            .toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO: handle success
                        Log.d(TAG, "Event add successful");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        //TODO: handle failure
                        Log.e(TAG, "Event add failed");
                    }
                });
                requestQueue.add(jsonRequest);
                requestQueue.start();
                finish();
            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                locationName.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isEmpty (EditText text) {
        return TextUtils.isEmpty(text.getText());
    }


}