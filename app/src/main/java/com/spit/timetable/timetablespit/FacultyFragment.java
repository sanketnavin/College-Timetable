package com.spit.timetable.timetablespit;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by sanket.navin on 12-03-2017.
 */

public class FacultyFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_faculty, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("faculties");

        final EditText faculty_name = (EditText) view.findViewById(R.id.faculty_name);
        final EditText faculty_dept = (EditText) view.findViewById(R.id.faculty_dept);
        final EditText faculty_code = (EditText) view.findViewById(R.id.faculty_code);
        Button add_button = (Button) view.findViewById(R.id.faculty_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Faculty faculty = new Faculty(faculty_name.getText().toString(),
                        faculty_code.getText().toString(), faculty_dept.getText().toString());

                databaseReference.push().setValue(faculty);

                faculty_name.setText("");
                faculty_dept.setText("");
                faculty_code.setText("");
                Toast.makeText(getActivity(), "Faculty " + faculty.getCode().toString() + " was added.", LENGTH_SHORT).show();
            }
        });


        return view;
    }

}
