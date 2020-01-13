package com.aap.medicore.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Activities.EquipmentCheckList;
import com.aap.medicore.Activities.FormSection;
import com.aap.medicore.Fragments.CheckListFragment;
import com.aap.medicore.Models.AdminForms;
import com.aap.medicore.Models.TasksListResponse;
import com.aap.medicore.R;

import java.util.List;

import retrofit2.Callback;
import retrofit2.Response;

public class AdapterAdmin extends RecyclerView.Adapter<AdapterAdmin.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    Response<AdminForms> response;

    // data is passed into the constructor
    public AdapterAdmin(Context context, List<String> data,Response<AdminForms> response) {

        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
       this. context = context;
       this.response = response;

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.adminform_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position);
        holder.myTextView.setText(animal);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,EquipmentCheckList.class);
                i.putExtra("formid", ""+response.body().getVehicleChecklistForm().getForms().get(position).getFormId());
                i.putExtra("formname", ""+response.body().getVehicleChecklistForm().getForms().get(position).getTitle());
                Log.e("form id","Form idddd"+response.body().getVehicleChecklistForm().getForms().get(position).getFormId());
                context.startActivity(i);
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

//    public void setClickListener(FragmentActivity activity) {
//    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        RelativeLayout layout;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
            layout = itemView.findViewById(R.id.layout);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(FragmentActivity itemClickListener) {
        this.mClickListener = (ItemClickListener) itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }}
