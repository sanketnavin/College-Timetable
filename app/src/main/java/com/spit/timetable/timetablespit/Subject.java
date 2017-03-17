package com.spit.timetable.timetablespit;

/**
 * Created by sanket.navin on 12-03-2017.
 */

public class Subject {

    private String name;
    private String code;
    private String semester;
    private Class mClass;

    public Subject() {
    }

    public Subject(String name, String code, String semester, Class mClass) {
        this.name = name;
        this.code = code;
        this.semester = semester;
        this.mClass = mClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Class getmClass() {
        return mClass;
    }

    public void setmClass(Class mClass) {
        this.mClass = mClass;
    }

}
