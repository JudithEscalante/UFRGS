package com.app.labvistilt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.DialogFragment;

public class BoxDialogFragment  extends DialogFragment {
    public static BoxDialogFragment newInstance(String msg) {
        BoxDialogFragment fragment = new BoxDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg); // set msg here
        fragment.setArguments(bundle);

        return fragment;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle("Copie o seu tempo no formulário")
                //.setMessage("Você deseja avançar para próximo teste?")
                .setMessage(getArguments().getString("msg"))
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing (will close dialog)
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("dialog", "cancel");
                        Log.i("FragmentAlertDialog", "Negative click!");
                        //dialogAnswer = "cancel";
                        //startActivity(intent);
                        dismiss();
                    }
                })
                .setPositiveButton(android.R.string.yes,  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do something
                        //openMainActivity();
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        intent.putExtra("dialog", "ok");
                        Log.i("FragmentAlertDialog", "Positive click!");
                        //dialogAnswer = "ok";
                        //startActivity(intent);

                    }
                })
                .create();
    }

    public void openMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("option", "small");
        startActivity(intent);
    }


}