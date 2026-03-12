package ru.mirea.khrechkorv.dialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MyProgressDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());

        dialog.setTitle("окно загрузки");
        dialog.setMessage("идёт загрузка");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);

        return dialog;
    }

    @Nullable
    @Override
    public void onCancel(DialogInterface dialog) {
        ((MainActivity)getActivity()).onProgressCanceled();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
