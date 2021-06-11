package com.example.nb.battleship;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

public class QuitDialogFragment extends DialogFragment {

    interface MyDialogListener {
        boolean onPositiveBtnClicked();
        boolean onNegativeBtnClicked();
    }

    MyDialogListener callback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            callback = (MyDialogListener)context;
        }catch (ClassCastException ex) {
            throw new ClassCastException("The activity must implement MyDialogListener interface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.msg_title).setMessage(R.string.msg_txt)
                .setPositiveButton(R.string.msg_positive_btn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        callback.onPositiveBtnClicked();
                    }
                }).setNegativeButton(R.string.msg_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callback.onNegativeBtnClicked();
            }
        });
        return builder.create();
    }
}
