package com.bysyk.tumblrvideogot.dlg;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.bysyk.tumblrvideogot.R;

public class AboutDlg extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dlg_title_about)
                .setMessage(R.string.about_desc)
                .setPositiveButton(R.string.dlg_bt_got_it, null)
                .create();
    }

    public static AboutDlg newInstance() {
        return new AboutDlg();
    }
}
