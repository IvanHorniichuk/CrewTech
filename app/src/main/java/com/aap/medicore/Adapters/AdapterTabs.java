package com.aap.medicore.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.aap.medicore.Activities.FormSection;
import com.aap.medicore.Activities.Login;
import com.aap.medicore.Models.AddVitalForm;
import com.aap.medicore.Models.AssignedIncidencesModel;
import com.aap.medicore.Models.CheckListForms;
import com.aap.medicore.Models.SelectImagesModel;
import com.aap.medicore.Models.TabsModel;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.ItemClickListener;
import com.aap.medicore.Utils.SettingValues;
import com.aap.medicore.Utils.TinyDB;
import com.google.gson.Gson;
import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;

import static com.aap.medicore.Activities.TaskDetails.handler;
import static com.aap.medicore.Activities.TaskDetails.model1;
import static com.aap.medicore.Activities.TaskDetails.tabsList;


public class AdapterTabs extends RecyclerView.Adapter<AdapterTabs.ViewHolder> {
    ArrayList<TabsModel> list = new ArrayList<>();
    AppCompatActivity mContext;
    private long mLastClickTime = 0;
    String task_id;
    TinyDB tinyDB;
    String taskid,tabid;
    public AdapterTabs(ArrayList<TabsModel> list, AppCompatActivity mContext, String task_id) {
        this.list = list;
        this.mContext = mContext;
        this.task_id = task_id;
        this.tinyDB = new TinyDB(mContext);
        Log.e("ImagesListSize", list.size() + "");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.row_tabs, parent, false);
        return new ViewHolder(itemLayoutView);
    }


    public void setItems(ArrayList<TabsModel> list) {
        this.list = list;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        if (list.get(position).getTitle().equalsIgnoreCase("vital sign form")) {
            Log.d("PLUS", "Vlaue is: " + list.get(position).isPlus());
            holder.plus.setVisibility(View.VISIBLE);
//            if (list.get(position).isPlus()){
//                holder.plus.setBackground(mContext.getDrawable(R.drawable.substract));
//
//            }else{
//                holder.plus.setBackground(mContext.getDrawable(R.drawable.plus));

//            }


        } else {

            holder.plus.setVisibility(View.GONE);
        }

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {

                tabid = list.get(position).getTab_id();

                holder. logoutConfirmDialogBox();
            }


        });
        holder.btnTab.setText(list.get(position).getTitle());
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

            }
        });


        if (list.get(position).getJsonData().isEmpty()) {
            holder.btnTab.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.btnTab.setBackground(mContext.getResources().getDrawable(R.drawable.btn_background_primary_color));
        } else {
            holder.btnTab.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            holder.btnTab.setBackground(mContext.getResources().getDrawable(R.drawable.btn_background_selected));
        }

        holder.btnTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                list.get(position).setJsonData("");
                notifyDataSetChanged();
                Intent i = new Intent(mContext, FormSection.class);
                i.putExtra(Constants.task_id, task_id);
                i.putExtra("position", position + "");
                i.putExtra("tab_id", list.get(position).getTab_id());
                i.putExtra("title", list.get(position).getTitle());
                mContext.startActivityForResult(i, 1100);

            }
        });


    }


    @Override
    public int getItemCount() {

        return (null != list ? list.size() : 0);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ItemClickListener clickListener;
        CustomButton btnTab;
        CustomButton plus;

        public ViewHolder(View view) {
            super(view);

            view.setTag(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            btnTab = view.findViewById(R.id.btnTab);
            plus = view.findViewById(R.id.plus);

        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }



        public void logoutConfirmDialogBox() {
            final Dialog dialog = new Dialog(mContext);
            final CustomButton btnNo, btnYes;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.newform);
            btnYes = (CustomButton) dialog.findViewById(R.id.btnYes);
            btnNo = (CustomButton) dialog.findViewById(R.id.btnNo);
            btnYes.setEnabled(true);
            btnNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btnYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hitVitalApi();
                    dialog.dismiss();
                }
            });
            dialog.show();
        }



        public void hitVitalApi() {
            retrofit2.Call<AddVitalForm> call;
            Log.d("TAB ID", "Tab id is: "+ tabid);
            Log.d("TAB ID", "task id is: "+ task_id);

            call = RetrofitClass.getInstance().getWebRequestsInstance().getvital(tinyDB.getString(Constants.token),tabid,task_id);

            call.enqueue(new Callback<AddVitalForm>() {
                @Override
                public void onResponse(retrofit2.Call<AddVitalForm> call, final Response<AddVitalForm> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus() == 200) {

//                            Toast.makeText(mContext, "200", Toast.LENGTH_SHORT).show();

//                            ArrayList<SelectImagesModel> imgList = new ArrayList<>();
//
////                imgList = handler.getAllTAbImagesOnImageId(list.get(position).getTab_id(), task_id);
//                            Log.d("SIZE","image size is :" + imgList.size());
//                            if(imgList.size() >0)
//                                model1.setSelectedImages(imgList);
////                            model1.setTitle("vital sign form"+" copy");
//                            tabsList.add(model1);
//                            notifyDataSetChanged();

                            model1 = new TabsModel();
                            model1.setTitle(response.body().getForm().getTitle());
                            model1.setTab_id(response.body().getForm().getFormId().toString());
                            model1.setPlus( response.body().getForm().isPlus());
                            model1.setJsonData("");
                            list.add(model1);
                            notifyDataSetChanged();
//                            AssignedIncidencesModel model = new AssignedIncidencesModel();
//                    model = handler.getIncidenceOnId(response.body().getForm().getFormId().toString());
//
//
//                    model.setId(Integer.parseInt(response.body().getForm().getFormId().toString()));
//                    Gson gson = new Gson();
//                    String json = gson.toJson(model.getJson());
//                    model.setJson(json);
//                    handler.addIncidences(model);
//
//                    notifyDataSetChanged();


                        } else if (response.body().getStatus() == 404) {

                        }
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<AddVitalForm> call, Throwable t) {

                }
            });
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