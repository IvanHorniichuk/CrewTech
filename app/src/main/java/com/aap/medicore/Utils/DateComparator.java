package com.aap.medicore.Utils;

import com.aap.medicore.Models.AssignedIncidencesModel;

import java.util.Comparator;

public class DateComparator implements Comparator<AssignedIncidencesModel> {

    @Override
    public int compare(AssignedIncidencesModel o1, AssignedIncidencesModel o2) {
        return o1.getObjDate().compareTo(o2.getObjDate());
    }

}
