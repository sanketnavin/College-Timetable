package com.spit.timetable.timetablespit;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.view.View.GONE;

/**
 * This is a base activity which contains week view and all the codes necessary to initialize the
 * week view.
 */
public class BaseActivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_WEEK_VIEW;
    private WeekView mWeekView;

    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN=1;
    private String mUsername;

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference usersDatabaseReference;
    private ChildEventListener childEventListener;

    private ArrayList<Lecture> lectures = new ArrayList<Lecture>();

    private static String currTimetable = "TE COMPS";
    private FirebaseUser currentUser = null;
    private Boolean isFaculty = Boolean.FALSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        Log.d("FLOW TAG", "onCreate");
        firebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        databaseReference = firebaseDatabase.getReference().child("lectures");
        usersDatabaseReference = firebaseDatabase.getReference().child("users");

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("FLOW TAG", "onAuthStateChanged");
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    mUsername = user.getDisplayName();
                    getUserDetails();
                    Toast.makeText(BaseActivity.this, "You're are now in the app", Toast.LENGTH_SHORT).show();
                } else {
                    onSignedOutCleaner();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN
                    );
                }
            }
        };

        attachChildEventListener();

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);
        mWeekView.setFirstDayOfWeek(2);

        mWeekView.setLimitTime(8, 18);

        // Show a dialog message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("FLOW TAG", "onActivityResult");
        if(requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                currentUser = FirebaseAuth.getInstance().getCurrentUser();

                usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.hasChild(mUsername)) {
                            Intent intent = new Intent(BaseActivity.this, RegistrationActivity.class);
                            startActivity(intent);
//                    usersDatabaseReference.removeEventListener(this);
                        }
                        else {
                            Log.d(mUsername, "was already there");
                            attachChildEventListener();
//                    usersDatabaseReference.removeEventListener(this);
                            Log.d(mUsername, "was already there");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });

                Toast.makeText(this, "Signed IN!", Toast.LENGTH_SHORT).show();
            } else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        Log.d("FLOW TAG", "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("FLOW TAG", "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    public void getUserDetails() {
        Log.d("FLOW TAG", "getUserDetails");
        usersDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mUsername)){

                    currTimetable = (String) dataSnapshot.child(mUsername).child("table").getValue();
                    getSupportActionBar().setTitle(currTimetable);
                    String type = (String) dataSnapshot.child(mUsername).child("type").getValue();
                    Log.d("FLOW TAG", type);
                    if(type.equals("student")){
                        isFaculty=Boolean.FALSE;
                        invalidateOptionsMenu();
                    }
                    else{
                        isFaculty=Boolean.TRUE;
                        invalidateOptionsMenu();
                    }
                }

                mWeekView.notifyDatasetChanged();
                usersDatabaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("FLOW TAG", "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.main, menu);
        if(isFaculty) {
            MenuItem item = menu.findItem(R.id.add_button);
            item.setVisible(Boolean.TRUE);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.add_button:
                Intent intent = new Intent(BaseActivity.this, AddContentActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;

                    mWeekView.setNumberOfVisibleDays(1);
                    mWeekView.setXScrollingSpeed(1.0f);
                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setIsFirstDraw(true);
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(6);
                    mWeekView.setXScrollingSpeed(0.0f);
                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
                    mWeekView.setIsFirstDraw(true);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
//                if (shortDate)
//                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase();
            }

            @Override
            public String interpretTime(int hour) {
                if(hour<12)
                    return hour + " AM";
                else if(hour==12)
                    return hour + " PM";
                else
                    return (hour-12) + " PM";
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    protected String getLectureTitle(Lecture lecture) {
        return String.format("%s\n%s\n%s", lecture.getSubject().getCode(), lecture.getFaculty().getCode(), lecture.getSubject().getmClass().getRoom());
    }

    /*
    * Display the details of the lecture when the timeslot is clicked
    * */
    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        int i;
        for(i=0;i<lectures.size();i++) {
            Log.d("Lecture title", lectures.get(i).findLec());
            if (lectures.get(i).findLec().equals
                    (event.getName() +
                            Integer.toString(event.getStartTime().get(Calendar.HOUR_OF_DAY)) +
                            Integer.toString(event.getStartTime().get(Calendar.DAY_OF_WEEK))
                    )) {
                break;
            }
        }

        Lecture selectedLec = lectures.get(i);

        DialogFragment newFragment = new DetailsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("className", currTimetable);
        bundle.putString("roomNo", selectedLec.getSubject().getmClass().getRoom());
        Log.d("Room title", selectedLec.getSubject().getmClass().getRoom());
        bundle.putInt("startTime", selectedLec.getStartTime());
        bundle.putBoolean("isDouble", selectedLec.isDoubleLecture());
        bundle.putInt("day", selectedLec.getDay());
        bundle.putString("facultyName", selectedLec.getFaculty().getName());
        bundle.putString("subjectName", selectedLec.getSubject().getName());
        bundle.putBoolean("isFaculty", isFaculty);
        newFragment.setArguments(bundle);

        newFragment.show(getFragmentManager(), "LectureDetails");

//        Toast.makeText(this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    /*
    * Delete the lecture when the slot is long pressed
    * */
    @Override
    public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
        Log.d("FLOW TAG", "onEventLongPress");
        Log.d("FLOW TAG", isFaculty.toString());
        if(isFaculty) {

            int i;
            for(i=0;i<lectures.size();i++){
                Log.d("Lecture title", lectures.get(i).findLec());
                if(lectures.get(i).findLec().equals
                    (   event.getName() +
                        Integer.toString(event.getStartTime().get(Calendar.HOUR_OF_DAY)) +
                        Integer.toString(event.getStartTime().get(Calendar.DAY_OF_WEEK))
                    )) {
                    break;
                }
            }

            Log.d("testing delete", Integer.toString(i));
            final Lecture selectedLec = lectures.get(i);
            Log.d("Selected Lec", selectedLec.toString());

            //Query DelQuery = databaseReference.orderByChild("dayName").equalTo(selectedLec.getDayName());

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot lecSnapshot: dataSnapshot.getChildren()) {
                        Lecture lecture = lecSnapshot.getValue(Lecture.class);
                        Log.d("Lecture fetched", lecture.getFaculty().getCode() + lecture.getSubject().getCode());
                        if(lecture.toString().equals(selectedLec.toString())){
                            Log.d("Value removed", lecture.toString());
                            lecSnapshot.getRef().removeValue();
                             lectures.remove(selectedLec);
                        }
                    }
                    mWeekView.notifyDatasetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("Cancelled deletion", "onCancelled", databaseError.toException());
                }
            });
            Toast.makeText(this, "Lecture deleted", Toast.LENGTH_SHORT).show();
        }
        else {

            Toast.makeText(this, "Limited Access", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Log.d("FLOW TAG", "onEmptyViewLongPress");
        Log.d("FLOW TAG", isFaculty.toString());
        if(isFaculty) {

            Intent intent = new Intent(BaseActivity.this, AddLectureActivity.class);
            //        time.add(Calendar.HOUR_OF_DAY, 8);
            intent.putExtra("tableName", currTimetable);
            intent.putExtra("slotTime", time);
            startActivity(intent);
            Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        Log.d(currTimetable, "Inside Month Change");

        /*
        * Get start and end day of the current month
        * */
        for(int i=0;i<lectures.size();i++) {
            Lecture l = lectures.get(i);
            Calendar monthStart = Calendar.getInstance();
            monthStart.set(Calendar.MONTH, newMonth - 1);
            monthStart.set(Calendar.YEAR, newYear);
            monthStart.set(Calendar.DAY_OF_MONTH, 1);
            Calendar monthEnd = (Calendar) monthStart.clone();
            monthEnd.set(Calendar.DAY_OF_MONTH, monthStart.getActualMaximum(Calendar.DAY_OF_MONTH));

            //set start day to the first required weekday of the month
            while((int) monthStart.get(Calendar.DAY_OF_WEEK) != l.getDay() )
                monthStart.add(Calendar.DAY_OF_MONTH, 1);

            //populate the event to all the required weekdays of the month
            while(true) {
                Calendar startTime = (Calendar) monthStart.clone();
                startTime.set(Calendar.HOUR_OF_DAY, l.getStartTime());
                startTime.set(Calendar.MINUTE, 0);
                Calendar endTime = (Calendar) startTime.clone();
                if(l.isDoubleLecture())
                    endTime.add(Calendar.HOUR_OF_DAY, 2);
                else
                    endTime.add(Calendar.HOUR_OF_DAY, 1);
                WeekViewEvent event = new WeekViewEvent(1, getLectureTitle(l), startTime, endTime);
                event.setColor(getResources().getColor(R.color.event_color_01));
                events.add(event);
//                Log.d(currTimetable, "Event added");
                if(monthEnd.get(Calendar.DAY_OF_MONTH) - monthStart.get(Calendar.DAY_OF_MONTH) < 7 ) {
                    break;
                }
                monthStart.add(Calendar.DAY_OF_MONTH, 7);
            }
        }
        return events;
    }

    public WeekView getWeekView() {
        return mWeekView;
    }

    private void onSignedOutCleaner() {
        mUsername = ANONYMOUS;
        detachChildEventListener();
    }

    private void attachChildEventListener() {
        if(childEventListener == null) {

            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("FLOW TAG", "onAuthStateChanged");
                    Log.d("FLOW TAG", currTimetable);

                    View loading_bar = findViewById(R.id.loading_spinner);
                    loading_bar.setVisibility(GONE);

                    Lecture lecture = dataSnapshot.getValue(Lecture.class);
                    Log.d("Data was added", lecture.getFaculty().getCode());
                    if(lecture.getSubject().getmClass().getCode().equals(currTimetable)) {
                        lectures.add(lecture);
                        Log.d(currTimetable, lecture.getFaculty().getCode());
                        mWeekView.notifyDatasetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d("Data has changed", s);
                }

                @Override public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Lecture lecture = dataSnapshot.getValue(Lecture.class);
                    lectures.remove(lecture);
                    mWeekView.notifyDatasetChanged();
                    Log.d("Removed", "Data was removed");
                }
                @Override public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
                @Override public void onCancelled(DatabaseError databaseError) { }
            };
        }
        databaseReference.addChildEventListener(childEventListener);

    }

    private void detachChildEventListener() {
        if(childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }

}
