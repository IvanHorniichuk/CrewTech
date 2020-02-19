package com.aap.medicore.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Activities.TaskDetails;
import com.aap.medicore.Models.AssignedIncidencesModel;
import com.aap.medicore.Models.TaskList;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.ItemClickListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class AdapterTasksList extends RecyclerView.Adapter<AdapterTasksList.ViewHolder> {
    List<AssignedIncidencesModel> list = new ArrayList<>();
    Context mContext;
    private long mLastClickTime = 0;
    List<Bitmap> bitmapArray = new ArrayList<Bitmap>();
    //    Response<TasksListResponse> response;
    private String taskLocation, name, date, time;

    public AdapterTasksList(List<AssignedIncidencesModel> list, Context mContext, List<Bitmap> bitmapArray/*, Response<TasksListResponse> response*/) {
        this.list = list;
        this.mContext = mContext;
        this.bitmapArray = bitmapArray;
//        this.response = response;
        Log.e("ImagesListSize", list.size() + "");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_row_tasks, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    public void setItems(ArrayList<AssignedIncidencesModel> list) {
        this.list = list;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Gson gson = new Gson();
        String json = list.get(position).getJson();
        final TaskList obj = gson.fromJson(json, TaskList.class);
        name = obj.getName();
        holder.tvHeading.setText(name);
//        holder.tvHeading.setText(response.body().getTaskList().get(position).getName());
        taskLocation = obj.getFromFacitity();
        if (obj.getToFacility() != null && !obj.getToFacility().isEmpty())
            taskLocation = taskLocation + ":" + obj.getToFacility();
        holder.tvDetails.setText(taskLocation);
//        holder.tvDetails.setText(response.body().getTaskList().get(position).getFromFacitity() + " : " + response.body().getTaskList().get(position).getToFacility());
        time = obj.getJobDateTime();
        holder.tvTime.setText(time);
        if (obj.getJobDate() != null) {
            date = (String) obj.getJobDate();
//            date = date.substring(0, 10);
//            holder.tvDate.setText(date);
        }
//        holder.tvTime.setText(response.body().getTaskList().get(position).getJobDateTime());
//        notifyDataSetChanged();
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (bitmapArray.size() > 0) {

//                    img_bm = bitmapArray.get(position);

                } else {
                }


                Intent i = new Intent(mContext, TaskDetails.class);
                i.putExtra(Constants.task_id, obj.getIncidencId() + "");
                i.putExtra(Constants.task_location, taskLocation);
                i.putExtra(Constants.task_witness, obj.getOrderNo() + "");
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ItemClickListener clickListener;
        ImageView tvGo;
        CustomTextView tvHeading, tvDetails, tvTime/*, tvDate*/;

        public ViewHolder(View view) {
            super(view);

            view.setTag(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            this.tvGo = view.findViewById(R.id.tvGo);
            this.tvHeading = view.findViewById(R.id.tvHeading);
            this.tvDetails = view.findViewById(R.id.tvDetails);
            this.tvTime = view.findViewById(R.id.tvTime);
//            this.tvDate = view.findViewById(R.id.tvDate);
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