package com.spit.timetable.timetablespit;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by sanket.navin on 29-01-2017.
 */

public class CategoryAdapter extends FragmentPagerAdapter {

    private String tabTitle[] = new String[] {"Class", "Subject", "Faculty"};

    public CategoryAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            ClassFragment c = new ClassFragment();
            return new ClassFragment();
        } else if (position == 1) {
            return new SubjectFragment();
        } else {
            return new FacultyFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitle[position];
    }
}
