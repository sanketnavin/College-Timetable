package com.spit.timetable.timetablespit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by sanket.navin on 12-03-2017.
 */

public class ClassFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_class, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("class");

        final EditText class_name = (EditText) view.findViewById(R.id.class_name);
        final EditText class_dept = (EditText) view.findViewById(R.id.class_dept);
        final EditText class_code = (EditText) view.findViewById(R.id.class_code);
        final EditText class_room = (EditText) view.findViewById(R.id.class_room);
        Button add_button = (Button) view.findViewById(R.id.class_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Class mClass = new Class(class_name.getText().toString(), class_dept.getText().toString(),
                        class_code.getText().toString(), class_room.getText().toString());

                databaseReference.push().setValue(mClass);

                class_code.setText("");
                class_dept.setText("");
                class_name.setText("");
                class_room.setText("");
                Toast.makeText(getActivity(), "Class " + mClass.getCode().toString() + " was added.", LENGTH_SHORT).show();
            }
        });

        return view;
    }



}
