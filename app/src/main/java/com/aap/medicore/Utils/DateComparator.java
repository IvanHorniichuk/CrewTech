package com.aap.medicore.Utils;

import com.aap.medicore.Models.AssignedIncidencesModel;

import java.util.Comparator;

public class DateComparator implements Comparator<AssignedIncidencesModel> {

    @Override
    public int compare(AssignedIncidencesModel o1, AssignedIncidencesModel o2) {

        int value1 = o2.getObjDate().compareTo(o1.getObjDate());
        if (value1 == 0) {
            return o2.getJobDateTime().compareTo(o1.getJobDateTime());
        }
        return value1;
    }
}
