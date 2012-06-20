package com.bytopia.oboobs.fragments.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.bytopia.oboobs.R;

public class NetworkErrorDialog extends SherlockDialogFragment {

	public static NetworkErrorDialog newInstance(int number) {
		NetworkErrorDialog frag = new NetworkErrorDialog();
		Bundle args = new Bundle();
		args.putInt("number", number);
		frag.setRetainInstance(true);
		frag.setArguments(args);
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final int provNumber = getArguments().getInt("number");

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.error)
				.setMessage(R.string.network_error_happen)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						getSherlockActivity().getSupportActionBar()
								.setSelectedNavigationItem(provNumber);
						dialog.dismiss();
					}
				})
				.setNegativeButton(android.R.string.cancel,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();

							}
						}).create();
	}
}