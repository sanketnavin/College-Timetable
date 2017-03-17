package com.spit.timetable.timetablespit;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class AddLectureActivity extends AppCompatActivity {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference lectureDatabaseReference;
    private DatabaseReference subjectDatabaseReference;
    private DatabaseReference facultyDatabaseReference;
    private ChildEventListener subchildEventListener;
    private ChildEventListener facchildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lecture);

        final Bundle bundle = getIntent().getExtras();

        TextView timetable_name = (TextView) findViewById(R.id.timetable_name);
        timetable_name.setText(bundle.getString("tableName"));

        firebaseDatabase = FirebaseDatabase.getInstance();
        lectureDatabaseReference = firebaseDatabase.getReference().child("lectures");
        subjectDatabaseReference = firebaseDatabase.getReference().child("subjects");
        facultyDatabaseReference = firebaseDatabase.getReference().child("faculties");

        final Spinner subjects = (Spinner) findViewById(R.id.subject_select);
        final Spinner faculties = (Spinner) findViewById(R.id.faculty_select);
        final RadioGroup rg = (RadioGroup) findViewById(R.id.slot_select);
        Button add_button = (Button) findViewById(R.id.add_lec_button);

        final List<String> subSpinnerArray =  new ArrayList<String>();
        final List<Subject> subArray =  new ArrayList<Subject>();
        final List<String> facSpinnerArray =  new ArrayList<String>();
        final List<Faculty> facArray =  new ArrayList<Faculty>();


        subchildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Testing Subject", "data found");
                Subject subject= dataSnapshot.getValue(Subject.class);
                subArray.add(subject);
                Log.v("Sample", subject.getCode().toString());
                subSpinnerArray.add(subject.getCode().toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddLectureActivity.this, android.R.layout.simple_spinner_item, subSpinnerArray);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                subjects.setAdapter(adapter);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override public void onCancelled(DatabaseError databaseError) { }
        };



        facchildEventListener= new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.d("Testing Subject", "data found");
                Faculty faculty = dataSnapshot.getValue(Faculty.class);
                facArray.add(faculty);
                Log.v("Sample", faculty.getCode().toString());
                facSpinnerArray.add(faculty.getCode().toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddLectureActivity.this, android.R.layout.simple_spinner_item, facSpinnerArray);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                faculties.setAdapter(adapter);
            }

            @Override public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            @Override public void onChildRemoved(DataSnapshot dataSnapshot) { }
            @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            @Override public void onCancelled(DatabaseError databaseError) { }
        };

        subjectDatabaseReference.addChildEventListener(subchildEventListener);
        facultyDatabaseReference.addChildEventListener(facchildEventListener);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Subject s = new Subject();
                String selected_sub = subjects.getSelectedItem().toString();
                for(int i=0;i<subArray.size();i++)
                    if(subArray.get(i).getCode() == selected_sub) {
                        s = subArray.get(i);
                    }

                Faculty f = new Faculty();
                String selected_fac = faculties.getSelectedItem().toString();
                for(int i=0;i<facArray.size();i++)
                    if(facArray.get(i).getCode() == selected_fac)
                        f = facArray.get(i);

                String value = ((RadioButton)findViewById(rg.getCheckedRadioButtonId() )).getText().toString();
                Boolean doubleLec = Boolean.FALSE;
                if(value.equals("Double Lecture"))
                    doubleLec = Boolean.TRUE;

                Calendar time = (Calendar) bundle.get("slotTime");
                Log.d("My Time", time.toString());
                int hour = time.get(Calendar.HOUR_OF_DAY);
                int day = time.get(Calendar.DAY_OF_WEEK);

                Lecture lec = new Lecture(s, f, day, hour, doubleLec);
                lectureDatabaseReference.push().setValue(lec);

                Toast.makeText(AddLectureActivity.this, "Faculty " + s.getCode() + " was added.", LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void onPause() {
        subjectDatabaseReference.removeEventListener(subchildEventListener);
        facultyDatabaseReference.removeEventListener(facchildEventListener);
        super.onPause();
    }
}
