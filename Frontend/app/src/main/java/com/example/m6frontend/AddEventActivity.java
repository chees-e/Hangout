package com.example.m6frontend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.Status;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AddEventActivity extends AppCompatActivity {
    private final String TAG = "AddEventActivity";
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText eventName;
    private EditText locationName;
    private EditText descriptionName;
    private DateFormat dateFormat =  new SimpleDateFormat("yyyy-MM-dd");
    private DateFormat timeFormat = new SimpleDateFormat("HH:mm");

    private EditText startDate;
    private EditText startTime;
    private EditText endDate;
    private EditText endTime;

    private List<String> attendees = new ArrayList<>();

    private GoogleSignInAccount currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventName = findViewById(R.id.editTextEvent);

        locationName = findViewById(R.id.editTextLocation);
        locationName.setInputType(InputType.TYPE_NULL);

        descriptionName = findViewById(R.id.editTextEventDescription);

        //usersName = findViewById(R.id.editTextAddUsers);

        startDate = findViewById(R.id.editTextStartDate);
        startTime = findViewById(R.id.editTextStartTime);
        startDate.setInputType(InputType.TYPE_NULL);
        startTime.setInputType(InputType.TYPE_NULL);

        endDate = findViewById(R.id.editTextEndDate);
        endTime = findViewById(R.id.editTextEndTime);
        endDate.setInputType(InputType.TYPE_NULL);
        endTime.setInputType(InputType.TYPE_NULL);

        currentAccount = GoogleSignIn.getLastSignedInAccount(this);

        initFriendList();
    }

    private void initFriendList() {
        RequestQueue requestQueue = Volley.newRequestQueue(AddEventActivity.this);
        List<String> friendList = new ArrayList<>();
        List<String> friendids = new ArrayList<>();
        String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/user/" + currentAccount.getEmail();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray friends = response.getJSONArray("friends");

                            for (int i = 0; i < friends.length(); i++) {
                                friendList.add(friends.getJSONObject(i).getString("name"));
                                friendids.add(friends.getJSONObject(i).getString("id"));
                            }
                            afterRequest(friendList, friendids);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "error");

                    }
                });
        requestQueue.add(jsonObjectRequest);
        requestQueue.start();

    }

    private void afterRequest(List<String> friendList, List<String> friendids) {
        if (friendList.size() <= 0) {
            friendList.add("Gabe");
            friendids.add("shawnlu4gd@gmail.com"); //My alt used for testing
        }
        Button addEventButton = findViewById(R.id.add_event_button);

        final MultiSpinner multiSpinner =  findViewById(R.id.addUsersSpinner);
        multiSpinner.setItems(friendList, " ", new MultiSpinner.MultiSpinnerListener() {
            @Override
            public void onItemsSelected(boolean[] selected) {
                Log.i(TAG, String.valueOf(multiSpinner.items));
                for (int i = 0; i < selected.length; i++) {
                    if (selected[i]) {
                        attendees.add(friendids.get(i));
                    }
                }
            }
        });


        // gets location
        Places.initialize(getApplicationContext(), getResources().getString(R.string.GOOGLE_MAPS_API_KEY));

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
        startDate.setOnClickListener(createDateListener(startDate));

        // gets start time
        startTime.setOnClickListener(createTimeListener(startTime));

        // gets end date
        endDate.setOnClickListener(createDateListener(endDate));

        // gets end time
        endTime.setOnClickListener(createTimeListener(endTime));



        addEventButton.setOnClickListener(createAddEventButton());
    }

    private View.OnClickListener createAddEventButton() {
        return v -> {
            if (isEmpty(eventName) || isEmpty(locationName) || isEmpty(descriptionName) ||
                    isEmpty(startDate) || isEmpty(startTime) || isEmpty(endDate) || isEmpty(endTime)) {
                Toast.makeText(AddEventActivity.this, "Please enter the required fields", Toast.LENGTH_LONG).show();
                return;
            }

            Date dateEnd = null;
            Date dateStart = null;
            Date timeEnd = null;
            Date timeStart = null;

            try {
                dateEnd = dateFormat.parse(endDate.getText().toString());
                dateStart = dateFormat.parse(startDate.getText().toString());
                timeEnd = timeFormat.parse(endTime.getText().toString());
                timeStart = timeFormat.parse(startTime.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //Log.i(TAG, dateEnd.toString() + " " +  dateStart.toString() + " " + timeEnd.toString() + " " + timeStart.toString());
            assert dateEnd != null;
            if (dateEnd.before(dateStart)) {
                    Toast.makeText(AddEventActivity.this, "End date is before start date", Toast.LENGTH_LONG).show();
                    return;
                }

            if (dateEnd.equals(dateStart)) {
                assert timeEnd != null;
                if (timeEnd.before(timeStart)) {
                       Toast.makeText(AddEventActivity.this, "End time is before start time", Toast.LENGTH_LONG).show();
                       return;
                   }
            }

            sendToServer();

        };
    }

    private void sendToServer() {
        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String jsonString = null;
        JSONObject jsonObject = null;
        try {

            String start = startTime.getText().toString();
            if (start.length() < 5) {
                start = "0" + start;
            }
            String end = endTime.getText().toString();
            if (end.length() < 5) {
                end = "0" + end;
            }
            jsonString = new JSONObject()
                    .put("host", currentUser.getEmail())
                    .put("name", eventName.getText())
                    .put("location", locationName.getText())
                    .put("description", descriptionName.getText())
                    .put("start",startDate.getText() + "T" + start)
                    .put("end", endDate.getText() + "T" + end)
                    .put("attendees", TextUtils.join("+", attendees))
                    .toString();

            jsonObject = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue requestQueue = Volley.newRequestQueue(AddEventActivity.this);
        String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/event/";

        final JSONObject finalJsonObject = jsonObject;
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.d(TAG, "success" + finalJsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();

                Log.e(TAG, "failed" + finalJsonObject.toString());
            }
        });

        requestQueue.add(jsonRequest);
        requestQueue.start();

        finish();
    }


    private View.OnClickListener createDateListener(final EditText date) {

        return v -> {
            final Calendar calendar = Calendar.getInstance();
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePicker = new DatePickerDialog(AddEventActivity.this,
                    (view, year1, month1, dayOfMonth) -> date.setText(year1 + "-" + (month1 + 1) + "-" + dayOfMonth), year, month, day);
            datePicker.show();

        };
    }

    private View.OnClickListener createTimeListener(final EditText time) {
        return v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePicker = new TimePickerDialog(AddEventActivity.this,
                    (view, hourOfDay, minute1) -> time.setText(hourOfDay + ":" + minute1), hour, minute, true);
            timePicker.show();

        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                locationName.setText(place.getAddress());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

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

    private boolean isEmpty (EditText text) {
        return TextUtils.isEmpty(text.getText());
    }


}