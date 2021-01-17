package com.fileee.utils;

import com.fileee.models.mongo.Staff;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SortUtil {

    public static List<Staff> sort(List<Staff> originalList, Comparator<Staff> comparator) {
        for (int i = 0; i < originalList.size() - 1; i++) {
            for (int j = 0; j < originalList.size() - i - 1; j++) {
                int difference = comparator.compare(originalList.get(j), originalList.get(j + 1));
                if (difference > 0) {
                    Collections.swap(originalList, j, j + 1);
                }
            }
        }
        return originalList;
    }
}
