package com.aap.medicore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aap.medicore.Models.ChecklistFormOption;
import com.aap.medicore.Models.EquipmentChecklistOption;
import com.aap.medicore.Models.VehicleChecklistOption;
import com.aap.medicore.R;
import com.aap.medicore.Utils.CustomTextView;

import java.util.List;

public class EqipmentCheckListSpinnerArrayAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final List<ChecklistFormOption> list;
    private final int mResource;

    public EqipmentCheckListSpinnerArrayAdapter(@NonNull Context context, @LayoutRes int resource,
                                                @NonNull List objects) {
        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        list = objects;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);

        CustomTextView tvEventName = view.findViewById(R.id.tvEventName);
        tvEventName.setText(list.get(position).getLabel());

        return view;
    }

    @Override
    public int getCount() {
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}