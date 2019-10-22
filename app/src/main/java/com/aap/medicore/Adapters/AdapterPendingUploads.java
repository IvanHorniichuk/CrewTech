package com.aap.medicore.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.QueueModel;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.ItemClickListener;
import com.aap.medicore.Utils.TinyDB;

import java.util.ArrayList;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;


public class AdapterPendingUploads extends RecyclerView.Adapter<AdapterPendingUploads.ViewHolder> {
    ArrayList<QueueModel> list = new ArrayList<>();
    Context mContext;
    private long mLastClickTime = 0;
    TinyDB tinyDB;
    DatabaseHandler databaseHandler;

    public AdapterPendingUploads(ArrayList<QueueModel> backgrounds, Context mContext) {
        this.list = backgrounds;
        this.mContext = mContext;
        tinyDB = new TinyDB(mContext);
        databaseHandler = new DatabaseHandler(mContext);
        Log.e("UploadingList", backgrounds.size() + "");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_videos, parent, false);
        return new ViewHolder(itemLayoutView);
    }


    public void setItems(ArrayList<QueueModel> backgrounds) {
        this.list = backgrounds;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        Log.e("STATUS:", "MESSAGE IS: " +list.get(position).getMessage());
        if(list.get(position).getMessage().contains("Your form has been updated from admin panel,")){
            databaseHandler.deleteQueuedIncidenceOnIndidenceId(list.get(position).getId());
            list.remove(position);

        }else{
            holder.tvTitle.setText(list.get(position).getTitle() + "");
            holder.tvMessage.setText(list.get(position).getMessage());

            if (list.get(position).getState().equals(Constants.StateUploadFailed)) {
                holder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_warning));
                holder.ivReload.setVisibility(View.VISIBLE);
                holder.progress.setVisibility(View.GONE);
                holder.pb.setVisibility(View.VISIBLE);
            } else if (list.get(position).getState().equals(Constants.StateUploadSucceeded)) {
                holder.ivStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_checked));
                holder.ivReload.setVisibility(View.GONE);
                holder.progress.setVisibility(View.GONE);
                holder.pb.setVisibility(View.VISIBLE);
            } else if (list.get(position).getState().equals(Constants.StateUploading)) {
                holder.ivStatus.setImageResource(R.drawable.ic_hourglass);
                holder.ivReload.setVisibility(View.GONE);
                holder.progress.setVisibility(View.VISIBLE);
                holder.pb.setVisibility(View.GONE);
            } else {
                holder.ivStatus.setImageResource(R.drawable.ic_hourglass);
                holder.ivReload.setVisibility(View.GONE);
                holder.progress.setVisibility(View.GONE);
                holder.pb.setVisibility(View.VISIBLE);
            }
        }



//        if (list.get(position).getEmail() != null &&  list.get(position).getEmail().isEmpty()){
//            holder.email.setText(list.get(position).getPhone().toString());
//
//        }else{
//            holder.email.setText(list.get(position).getEmail().toString());
//
//        }


        holder.ivReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                holder.ivStatus.setImageResource(R.drawable.ic_hourglass);
//                holder.ivReload.setVisibility(View.GONE);
//
//                holder.progress.setVisibility(View.VISIBLE);
//                holder.pb.setVisibility(View.GONE);

//                Intent intent = new Intent(mContext, UploaderService.class);
//                QueueModel model = new QueueModel();
////                model.setEmail(list.get(position).getEmail());
////                model.setPhone(list.get(position).getPhone());
////                model.setEventName(list.get(position).getEventName());
////                model.setPath(list.get(position).getPath());
////                model.setPosition(position);
//                intent.putExtra("UploadingModel",  model);

//                mContext.startService(intent);
                databaseHandler.deleteQueuedIncidenceOnIndidenceId(list.get(position).getId());
                list.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

            }
        });

    }

    @Override
    public int getItemCount() {

        return (null != list ? list.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ItemClickListener clickListener;
        ImageView ivReload, ivStatus;
        SmoothProgressBar progress;
        RelativeLayout rlError;
        ProgressBar pb;
        CustomTextView tvTitle, tvMessage;

        public ViewHolder(View view) {
            super(view);

            view.setTag(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            this.ivReload = view.findViewById(R.id.ivReload);
            this.ivStatus = view.findViewById(R.id.ivStatus);
            this.tvTitle = view.findViewById(R.id.tvTitle);

            this.progress = view.findViewById(R.id.progress);
            this.rlError = view.findViewById(R.id.rlError);
            this.tvMessage = view.findViewById(R.id.tvMessage);
            pb = view.findViewById(R.id.pb);
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