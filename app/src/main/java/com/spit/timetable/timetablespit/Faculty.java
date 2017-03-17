package com.spit.timetable.timetablespit;

/**
 * Created by sanket.navin on 12-03-2017.
 */

public class Faculty {

    private String name;
    private String code;
    private String dept;

    public Faculty() {
    }

    public Faculty(String name, String code, String dept) {
        this.name = name;
        this.code = code;
        this.dept = dept;
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

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }
}
