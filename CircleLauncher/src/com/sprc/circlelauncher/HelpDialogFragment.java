package com.sprc.circlelauncher;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

/**
 * A {@link Dialog} that shows information about the application usage.
 *
 * @author sprc
 */
public class HelpDialogFragment extends DialogFragment {

	public static HelpDialogFragment newInstance() {
		return new HelpDialogFragment();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE));

		View v = inflater.inflate(R.layout.fragment_helpdialog, null);

		return new AlertDialog.Builder(getActivity()).setTitle(R.string.dlg_help_title).setView(v)
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

					}
				}).create();
	}
}
