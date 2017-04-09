package com.spit.timetable.timetablespit;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by sanket.navin on 04-04-2017.
 */

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference usersDatabaseReference;
    private DatabaseReference classDatabaseReference;
    private ChildEventListener classchildEventListener;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        classDatabaseReference = firebaseDatabase.getReference().child("class");
        usersDatabaseReference = firebaseDatabase.getReference().child("users");

        final FirebaseUser user = firebaseAuth.getCurrentUser();

        TextView welcome_msg = (TextView) findViewById(R.id.welcome_msg);
        final Spinner classes = (Spinner) findViewById(R.id.timetable_spinner);
        Button reg_button = (Button) findViewById(R.id.register_button);

        final List<String> spinnerArray =  new ArrayList<String>();
        final List<Class> classArray =  new ArrayList<Class>();

        welcome_msg.setText("Welcome " + user.getDisplayName());

        classchildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Testing Subject", "data found");
                Class mclass = dataSnapshot.getValue(Class.class);
                classArray.add(mclass);
                Log.v("Sample", mclass.getCode().toString());
                spinnerArray.add(mclass.getCode().toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegistrationActivity.this, android.R.layout.simple_spinner_item, spinnerArray);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                classes.setAdapter(adapter);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override public void onCancelled(DatabaseError databaseError) { }
        };

        classDatabaseReference.addChildEventListener(classchildEventListener);

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Class c = new Class();
                String selected = classes.getSelectedItem().toString();
                for(int i=0;i<classArray.size();i++) {
                    if(classArray.get(i).getCode()==selected) {
                        c = classArray.get(i);
                    }
                }
                usersDatabaseReference.child(user.getDisplayName()).child("email").setValue(user.getEmail());
                usersDatabaseReference.child(user.getDisplayName()).child("type").setValue("student");
                usersDatabaseReference.child(user.getDisplayName()).child("table").setValue(c.getCode());

                Intent intent = new Intent(RegistrationActivity.this, BaseActivity.class);
                RegistrationActivity.this.startActivity(intent);
            }
        });

    }

    @Override
    public void onPause() {
        classDatabaseReference.removeEventListener(classchildEventListener);
        super.onPause();
    }
}
