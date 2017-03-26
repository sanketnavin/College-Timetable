package com.spit.timetable.timetablespit;

import com.google.firebase.database.Exclude;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sanket.navin on 15-03-2017.
 */

public class Lecture {

    private static String weekdays[] = new String[] {"Sunday", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday"
    };


    private Subject subject;
    private Faculty faculty;
    private int day;
    private String dayName;
    private int startTime;
    private boolean doubleLecture;

    public Lecture() {
    }

    public Lecture(Subject subject, Faculty faculty, int day, int startTime, boolean doubleLecture) {
        this.subject = subject;
        this.faculty = faculty;
        this.day = day;
        this.dayName = weekdays[day-1];
        this.startTime = startTime;
        this.doubleLecture = doubleLecture;
    }

    public String getTitle() {
        return String.format("%s\n%s\n%s", this.getSubject().getCode(), this.getFaculty().getCode(), this.getSubject().getmClass().getRoom());
    }

    public String findLec() {
        return this.getTitle() + Integer.toString(this.startTime) + Integer.toString(this.getDay());
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public int getDay() {
        return day;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public boolean isDoubleLecture() {
        return doubleLecture;
    }

    public void setDoubleLecture(boolean doubleLecture) {
        this.doubleLecture = doubleLecture;
    }

    @Override
    public String toString() {
        return "Lecture{" +
                "subject=" + subject.getCode() +
                ", faculty=" + faculty.getCode() +
                ", dayName='" + dayName + '\'' +
                ", startTime=" + startTime +
                '}';
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("subject", subject);
        result.put("faculty", faculty);
        result.put("day", day);
        result.put("dayName", dayName);
        result.put("startTime", startTime);
        result.put("doubleLecture", doubleLecture);

        return result;
    }
}
