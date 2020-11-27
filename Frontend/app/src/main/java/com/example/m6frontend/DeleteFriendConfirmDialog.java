package com.example.m6frontend;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class DeleteFriendConfirmDialog extends AppCompatDialogFragment {
    private final String TAG = "DeleteFriendConfirm";

    @NonNull
    @Override
    public Dialog onCreateDialog (Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirm Selection")
                .setMessage("Delete Friend?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Friend Delete confirmed");
                        Toast.makeText(getContext(), "Friend Deleted", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Friend Delete unconfirmed");
                    }
                });


        return builder.create();
    }
}
