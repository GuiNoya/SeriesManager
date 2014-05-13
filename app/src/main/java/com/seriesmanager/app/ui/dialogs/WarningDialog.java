package com.seriesmanager.app.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class WarningDialog extends DialogFragment {

    //private Context mContext;
    private String title;
    private String message;

    public WarningDialog(String title, String message) {
        this.title = title;
        this.message = message;
    }

    /*public ShowSummaryDialog(Context context) {
        super(context);
        mContext = context;
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message);
                /*.setPositiveButton("Add Show", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton("Don't add show", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });*/
        return builder.create();

        //return super.onCreateDialog(savedInstanceState);
    }
}
