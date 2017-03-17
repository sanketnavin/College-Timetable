package com.spit.timetable.timetablespit;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by sanket.navin on 12-03-2017.
 */

public class SubjectFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference subjectdatabaseReference;
    private ChildEventListener childEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_subject, container, false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        subjectdatabaseReference = firebaseDatabase.getReference().child("subjects");
        databaseReference = firebaseDatabase.getReference().child("class");

        final EditText sub_name = (EditText) view.findViewById(R.id.sub_name);
        final EditText sub_code = (EditText) view.findViewById(R.id.sub_code);
        final EditText semester = (EditText) view.findViewById(R.id.semester);
        final Spinner sItems = (Spinner) view.findViewById(R.id.class_select);
        Button add_button = (Button) view.findViewById(R.id.sub_button);

        final List<String> spinnerArray =  new ArrayList<String>();
        final List<Class> classArray =  new ArrayList<Class>();


        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Testing Subject", "data found");
                Class mclass = dataSnapshot.getValue(Class.class);
                classArray.add(mclass);
                Log.v("Sample", mclass.getCode().toString());
                spinnerArray.add(mclass.getCode().toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinnerArray);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                sItems.setAdapter(adapter);

            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override public void onCancelled(DatabaseError databaseError) { }
        };

        databaseReference.addChildEventListener(childEventListener);
        Log.v("Sample", spinnerArray.toString());


        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class c = new Class();
                String selected = sItems.getSelectedItem().toString();
                for(int i=0;i<classArray.size();i++) {
                    if(classArray.get(i).getCode()==selected) {
                        c = classArray.get(i);
                    }
                }
                Subject subject = new Subject(sub_name.getText().toString(), sub_code.getText().toString(),
                        semester.getText().toString(), c);

                subjectdatabaseReference.push().setValue(subject);

                sub_name.setText("");
                sub_code.setText("");
                semester.setText("");
                Toast.makeText(getActivity(), "Subject " + subject.getCode().toString() + " was added.", LENGTH_SHORT).show();
            }
        });


        return view;
    }

    @Override
    public void onPause() {
        databaseReference.removeEventListener(childEventListener);
        super.onPause();
    }
}
