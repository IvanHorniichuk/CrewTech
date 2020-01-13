package com.aap.medicore.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Activities.TaskDetails;
import com.aap.medicore.R;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.ItemClickListener;

import java.util.ArrayList;

public class AdapterCheckBox extends RecyclerView.Adapter<AdapterCheckBox.ViewHolder> {
    ArrayList<String> list = new ArrayList<>();
    Context mContext;
    private long mLastClickTime = 0;

    public AdapterCheckBox(ArrayList<String> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
        Log.e("ImagesListSize", list.size() + "");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_check_boxs, parent, false);
        return new ViewHolder(itemLayoutView);
    }


    public void setItems(ArrayList<String> list) {
        this.list = list;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

            }
        });
    }

    @Override
    public int getItemCount() {

        return (null != list ? list.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ItemClickListener clickListener;
        AppCompatCheckBox cb;

        public ViewHolder(View view) {
            super(view);

            view.setTag(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.cb = view.findViewById(R.id.cb);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }
}