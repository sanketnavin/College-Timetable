package com.spit.timetable.timetablespit;

/**
 * Created by sanket.navin on 12-03-2017.
 */

public class Class {

    private String name;
    private String department;
    private String code;
    private String room;

    public Class() {

    }

    public Class(String name, String department, String code, String room) {
        this.name = name;
        this.department = department;
        this.code = code;
        this.room = room;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name= name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}
