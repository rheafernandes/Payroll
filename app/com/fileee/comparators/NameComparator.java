package com.fileee.comparators;

import com.fileee.models.mongo.Staff;

import java.util.Comparator;

public class NameComparator implements Comparator<Staff> {

    @Override
    public int compare(Staff o1, Staff o2) {
        return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
    }

}
