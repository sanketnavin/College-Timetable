package com.spit.timetable.timetablespit;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sanket.navin on 23-03-2017.
 */

public class DetailsDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final Bundle bundle = getArguments();

        View view = inflater.inflate(R.layout.fragment_dialog, null);

        TextView fac_title = (TextView) view.findViewById(R.id.fac_fullname);
        fac_title.setText(bundle.getString("facultyName"));

        TextView sub_title = (TextView) view.findViewById(R.id.fragment_sub_title);
        sub_title.setText(bundle.getString("subjectName"));

        TextView class_title = (TextView) view.findViewById(R.id.fragment_class_name);
        class_title.setText(bundle.getString("className"));

        TextView room_no = (TextView) view.findViewById(R.id.fragment_room_no);
        room_no.setText(bundle.getString("roomNo"));

        TextView start_time = (TextView) view.findViewById(R.id.start_time);
        start_time.setText(Integer.toString(bundle.getInt("startTime")));

        TextView end_time = (TextView) view.findViewById(R.id.end_time);
        if(bundle.getBoolean("isDouble"))
            end_time.setText(Integer.toString(bundle.getInt("startTime")+2));
        else
            end_time.setText(Integer.toString(bundle.getInt("startTime")+1));

        if(bundle.getBoolean("isFaculty")) {

            builder.setView(view)
                    .setPositiveButton("Modify", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getActivity(), ModifyLectureActivity.class);
                            intent.putExtra("lecBundle", bundle);
                            startActivity(intent);
                        }
                    });

        }
        builder.setView(view)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }
}
