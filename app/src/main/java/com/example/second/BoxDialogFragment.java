package com.example.second;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

public class BoxDialogFragment  extends DialogFragment {
    private int count = 0;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Vamos aumentar a velocidade?")

                .setMessage("Sure you wanna do this!")
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing (will close dialog)
                    }
                })
                .setPositiveButton(android.R.string.yes,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do something
                        count++;
                        openMainActivitySmall();

                    }
                })
                .create();
    }

    public void openMainActivitySmall(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("option", "small");
        startActivity(intent);
    }


}