package com.aap.medicore.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aap.medicore.Models.Option;
import com.aap.medicore.Models.StateModel;
import com.aap.medicore.R;
import com.aap.medicore.Utils.CustomTextView;

import java.util.ArrayList;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {

    private final LayoutInflater mInflater;
    private final Context mContext;
    private final ArrayList<StateModel> list;
    private final int mResource;

    public SpinnerAdapter(@NonNull Context context, @LayoutRes int resource,
                          @NonNull ArrayList objects) {
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
        tvEventName.setText(list.get(position).getTitle());

        return view;
    }
}