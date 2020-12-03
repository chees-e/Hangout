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



public class AddFriendConfirmDialog extends AppCompatDialogFragment {
    private final String TAG = "AddFriendConfirmDialog";
    private GoogleSignInAccount currentAccount;

    @NonNull
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        Bundle mArgs = getArguments();
        String friendid = mArgs.getString("friendid");
        currentAccount = GoogleSignIn.getLastSignedInAccount(getActivity());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Selection")
                .setMessage("Add Friend?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendRequest(friendid);
                        Log.d(TAG, "Friend Add confirmed " + friendid);
                        Toast.makeText(getContext(), "Friend Request Sent: " + friendid, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Friend Add unconfirmed");
                    }
                });


        return builder.create();
    }

    //Maybe check for success?
    private void sendRequest(String id) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        String url = "http://ec2-52-91-35-204.compute-1.amazonaws.com:8081/user/" + currentAccount.getEmail() + "/request/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "POST request success");
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });
        requestQueue.add(jsonObjectRequest);
        requestQueue.start();
    }
}
