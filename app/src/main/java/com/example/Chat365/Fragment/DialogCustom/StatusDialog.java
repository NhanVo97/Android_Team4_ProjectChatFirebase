package com.example.Chat365.Fragment.DialogCustom;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.Chat365.R;

public class StatusDialog extends AppCompatDialogFragment {
    private EditText editText;
    private statusListenner statusListenner;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            statusListenner = (statusListenner) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Phai Interface statusListener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.layout_dialogstatus, null);
        builder.setView(view).setTitle("Nhập trạng thái (0/50)").setNegativeButton("HỦY", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setPositiveButton("ĐỒNG Ý", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String EditContent = editText.getText().toString();
                statusListenner.applyTextStatus(EditContent);

            }
        });
        editText = view.findViewById(R.id.edTT);
        return builder.create();
    }

    public interface statusListenner {
        void applyTextStatus(String stt);
    }
}

