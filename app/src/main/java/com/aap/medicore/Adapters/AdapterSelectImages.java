package com.aap.medicore.Adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Models.SelectImagesModel;
import com.aap.medicore.R;
import com.aap.medicore.Utils.ItemClickListener;
import com.aap.medicore.Utils.TinyDB;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class AdapterSelectImages extends RecyclerView.Adapter<AdapterSelectImages.ViewHolder> {
    ArrayList<SelectImagesModel> list = new ArrayList<>();
    Context mContext;
    TinyDB tinyDB;
    ImageView ivSelectImage;
    private long mLastClickTime = 0;


    public AdapterSelectImages(ArrayList<SelectImagesModel> list, Context mContext, ImageView ivSelectImage) {
        this.list = list;
        this.mContext = mContext;
        tinyDB = new TinyDB(mContext);
        this.ivSelectImage = ivSelectImage;
        Log.e("ListSize", list.size() + "");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_select_images, parent, false);
        return new ViewHolder(itemLayoutView);
    }


    public void setItems(ArrayList<SelectImagesModel> list) {
        this.list = list;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if (list.size() >= 30) {
            ivSelectImage.setVisibility(View.GONE);
        } else {
            ivSelectImage.setVisibility(View.VISIBLE);
        }

        Glide.with(mContext).load(list.get(position).getFinalImage()).into(holder.iv);

        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mLastClickTime = SystemClock.elapsedRealtime();
                list.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);

    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ItemClickListener clickListener;
        ImageView ivRemove, iv;

        public ViewHolder(View view) {
            super(view);

            view.setTag(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);

            this.iv = view.findViewById(R.id.iv);
            this.ivRemove = view.findViewById(R.id.ivRemove);

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