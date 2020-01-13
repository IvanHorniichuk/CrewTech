package com.aap.medicore.Adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Activities.FormSection;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.AddVitalForm;
import com.aap.medicore.Models.AssignedIncidencesModel;
import com.aap.medicore.Models.Field;
import com.aap.medicore.Models.TabsModel;
import com.aap.medicore.Models.TaskList;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.CircularTextView;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.ItemClickListener;
import com.aap.medicore.Utils.SessionTimeoutDialog;
import com.aap.medicore.Utils.TinyDB;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;

import static com.aap.medicore.Activities.TaskDetails.model1;


public class AdapterTabs extends RecyclerView.Adapter<AdapterTabs.ViewHolder> {
    ArrayList<TabsModel> list = new ArrayList<>();
    BaseActivity mContext;
    private long mLastClickTime = 0;
    String task_id;
    TinyDB tinyDB;
    String tabid;

    private DatabaseHandler databaseHandler;

    public AdapterTabs(ArrayList<TabsModel> list, BaseActivity mContext, String task_id) {
        this.list = list;
        this.mContext = mContext;
        this.task_id = task_id;
        this.tinyDB = new TinyDB(mContext);
        databaseHandler = new DatabaseHandler(mContext);
        Log.e("ImagesListSize", list.size() + "");
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.layout_clinical_form_item, parent, false);
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
//            holder.plus.setVisibility(View.VISIBLE);
//            if (list.get(position).isPlus()){
//                holder.plus.setBackground(mContext.getDrawable(R.drawable.substract));
//
//            }else{
//                holder.plus.setBackground(mContext.getDrawable(R.drawable.plus));

//            }


        } else {

//            holder.plus.setVisibility(View.GONE);
        }


//        holder.plus.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onClick(View view) {
//
//                tabid = list.get(position).getTab_id();
//
//                holder.logoutConfirmDialogBox();
//
////                TabsModel model1 = new TabsModel();
////                model1 = new TabsModel();
////                model1.setTitle(list.get(position).getTitle());
////                model1.setTab_id(list.get(position).getTab_id().toString());
////                model1.setJsonData("");
////                list.add(model1);
////                SettingValues.setTabList(list);
////                holder.btnTab.setText(list.get(position).getTitle());
////                handler.getIncidenceOnId(task_id).setJson(list.get(position).getJsonData());
////                notifyDataSetChanged();
//
//
////                model1 = new TabsModel();
////                model1.setTitle(list.get(position).getTitle());
////                model1.setTab_id(list.get(position).getTab_id());
////                model1.setPlus(true);
//////                model1.setJsonData("");
////                AssignedIncidencesModel model = new AssignedIncidencesModel();
////                model = handler.getIncidenceOnId(list.get(position).getTab_id());
////
////
////                model.setId(Integer.parseInt(list.get(position).getTab_id()));
////                Gson gson = new Gson();
////                String json = gson.toJson(model.getJson());
////                model.setJson(json);
////                handler.addIncidences(model);
////
////                ArrayList<TabsModel> tabmodel = new ArrayList<>();
////
//////                tabmodel = handler.gettabDetail(task_id, list.get(position).getTab_id());
////                Log.d("SIZE","tab size is: " + tabmodel.size());
////                if(tabmodel.size() > 0){
////                    Log.d("SIZE","tab id is: " + tabmodel.get(0).getTab_id());
////                    Log.d("SIZE","tab second id is: " + model1.getTab_id());
////
////                    if(tabmodel.get(0).getTab_id().equals(model1.getTab_id())){
////                        Log.e("ID","TAB ID exist " );
////                        model1.setJsonData(tabmodel.get(0).getJsonData());
////                        notifyDataSetChanged();
////                    }
////
////
////                }
//
//
//            }
//
//
//        });
        holder.tvFormName.setText(list.get(position).getTitle());
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
//                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                    return;
//                }
//                mLastClickTime = SystemClock.elapsedRealtime();
//                Intent i = new Intent(mContext, FormSection.class);
//                i.putExtra("id", task_id);
//                mContext.startActivity(i);
            }
        });
        holder.tvNum.setText(position + 1 + "");
        holder.tvNum.setSolidColor("#2196f3");

        AssignedIncidencesModel model = new AssignedIncidencesModel();
        model = databaseHandler.getIncidenceOnId(task_id);
        Gson gson = new Gson();
        String json = model.getJson();
        final TaskList taskList = gson.fromJson(json, TaskList.class);
        ArrayList<Field> fields = taskList.getForm().get(position).getFields();
        for (Field field : fields) {
            if (field.getRequired().equalsIgnoreCase("true")) {
                holder.ivWarning.setVisibility(View.VISIBLE);
                break;
            } else
                holder.ivWarning.setVisibility(View.GONE);
        }
        if (list.get(position).getJsonData().isEmpty()) {
            holder.tvFormName.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            holder.rootLayout.setBackground(mContext.getResources().getDrawable(R.drawable.rectangle_rounded_white));
            holder.ivCheck.setVisibility(View.GONE);
            holder.tvNum.setVisibility(View.VISIBLE);


        } else {
            holder.tvFormName.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            holder.rootLayout.setBackground(mContext.getResources().getDrawable(R.drawable.btn_background_selected));
            holder.ivCheck.setVisibility(View.VISIBLE);
            holder.tvNum.setVisibility(View.GONE);
        }

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
//                list.get(position).setJsonData("");
//                notifyDataSetChanged();
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
        //        CustomButton btnTab;
//        CustomButton plus;
        TextView tvFormName;
        CircularTextView tvNum;
        ImageView ivWarning, ivCheck;
        ConstraintLayout rootLayout;

        public ViewHolder(View view) {
            super(view);

            view.setTag(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
//            btnTab = view.findViewById(R.id.btnTab);
//            plus = view.findViewById(R.id.plus);
            tvFormName = view.findViewById(R.id.tvFormName);
            tvNum = view.findViewById(R.id.tvNum);
            ivWarning = view.findViewById(R.id.ivWarning);
            ivCheck = view.findViewById(R.id.ivCheck);
            rootLayout = view.findViewById(R.id.rootLayout);

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
            Log.d("TAB ID", "Tab id is: " + tabid);
            Log.d("TAB ID", "task id is: " + task_id);

            call = RetrofitClass.getInstance().getWebRequestsInstance().getvital(tinyDB.getString(Constants.token), tabid, task_id);

            call.enqueue(new Callback<AddVitalForm>() {
                @Override
                public void onResponse(retrofit2.Call<AddVitalForm> call, final Response<AddVitalForm> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getStatus() == 200) {

//                            Toast.makeText(mContext, "200", Toast.LENGTH_SHORT).getDialog();

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
                            model1.setPlus(response.body().getForm().isPlus());
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

                        } else if (response.code() == 401) {
                            if (mContext != null)
                                new SessionTimeoutDialog(mContext).getDialog().show();
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