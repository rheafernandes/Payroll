package com.fileee.utils;

import com.fileee.comparators.NameComparator;
import com.fileee.enums.PayType;
import com.fileee.models.mongo.Staff;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SortUtilTest {

    @Test
    public void testSort() throws Exception {
        List<Staff> sortedArray = SortUtil.sort(getUnsortedStaff(), new NameComparator());
        Assert.assertEquals(sortedArray.get(0).getName(), "Amy");
    }

    @Test
    public void testSortAlreadySorted() throws Exception {
        List<Staff> sortedArray = SortUtil.sort(getSortedStaff(), new NameComparator());
        Assert.assertEquals( sortedArray.get(1).getName(), "Ben");
    }

    private List<Staff> getUnsortedStaff() {
        List<Staff> staffList = new ArrayList<>();
        staffList.add(new Staff("Jamie", PayType.Hourly.name()));
        staffList.add(new Staff("Amy", PayType.Monthly.name()));
        staffList.add(new Staff("Lexie", PayType.Hourly.name()));
        staffList.add(new Staff("Suarez", PayType.Monthly.name()));
        staffList.add(new Staff("Dash", PayType.Monthly.name()));
        staffList.add(new Staff("Shanice", PayType.Hourly.name()));
        staffList.add(new Staff("Lily", PayType.Hourly.name()));
        staffList.add(new Staff("Jamie", PayType.Hourly.name()));
        return staffList;
    }

    private List<Staff> getSortedStaff() {
        List<Staff> staffList = new ArrayList<>();
        staffList.add(new Staff("Amy", PayType.Hourly.name()));
        staffList.add(new Staff("Ben", PayType.Monthly.name()));
        staffList.add(new Staff("Catherine", PayType.Hourly.name()));
        staffList.add(new Staff("Diana", PayType.Monthly.name()));
        staffList.add(new Staff("Enid", PayType.Monthly.name()));
        staffList.add(new Staff("Floyd", PayType.Hourly.name()));
        staffList.add(new Staff("Gerald", PayType.Hourly.name()));
        staffList.add(new Staff("Herold", PayType.Hourly.name()));
        return staffList;
    }



}
