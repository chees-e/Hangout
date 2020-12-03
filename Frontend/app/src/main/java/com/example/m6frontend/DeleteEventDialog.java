package com.example.m6frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import org.json.JSONObject;

public class DeleteEventDialog extends AppCompatDialogFragment {
    private final String TAG = "DeleteEventConfirm";
    private GoogleSignInAccount currentAccount;

    @NonNull
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        Bundle mArgs = getArguments();
        String eventid = mArgs.getString("eventid");
        currentAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Selection")
                .setMessage("Delete Event?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendRequest(eventid);
                        Log.d(TAG, "Event Delete confirmed");
                        Toast.makeText(getContext(), "Event Deleted", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Event Delete unconfirmed");
                    }
                });


        return builder.create();
    }

    private void sendRequest(String id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/user/" + currentAccount.getEmail() + "/event/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "DELETE request success");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(jsonObjectRequest);
        requestQueue.start();
    }
}
