package com.app.labvistilt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.DialogFragment;

public class BoxDialogFragment  extends DialogFragment {
    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;


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
                        // Send the negative button event back to the host activity
                        mListener.onDialogNegativeClick(BoxDialogFragment.this);

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
                        // Send the positive button event back to the host activity
                        mListener.onDialogPositiveClick(BoxDialogFragment.this);

                    }
                })
                .create();
    }

    public void openMainActivity(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("option", "small");
        startActivity(intent);
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }



    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}