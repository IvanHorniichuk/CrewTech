package com.aap.medicore.Fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aap.medicore.Activities.Login;
import com.aap.medicore.Activities.StateActivity;
import com.aap.medicore.Activities.VehicleDetails;
import com.aap.medicore.Adapters.AdapterTasksList;
import com.aap.medicore.BaseClasses.BaseFragment;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.AssignedIncidencesModel;
import com.aap.medicore.Models.TasksListResponse;
import com.aap.medicore.Models.VehicleDetail;
import com.aap.medicore.Models.VehicleResponse;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.TinyDB;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashbordFragment extends BaseFragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView rvVisits;
    private View view;
    private LinearLayoutManager manager;
    private AdapterTasksList adapter;
    private CustomTextView tvCallsCount, tvBedsCount, tvEngineHorsepower, tvColor, tvRegistrationNo, tvStatus, tvState, tvVehicleErrorMessage;
    private LinearLayout llNoVehicleData, llVRegistration, llVName;
    private TinyDB tinyDB;
    private LinearLayout llLogout, llVehicleDetails, llState;
    private SwipeRefreshLayout pullToRefresh;
    private ImageView ivStatus, ivState;
    private CircleImageView profileImage;
    private DatabaseHandler databaseHandler;
    Response<TasksListResponse> responselist;
    public static ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();

    Handler h = new Handler();
    int delay = 5 * 1000; //1 second=1000 milisecond, 15*1000=15seconds
    Runnable runnable;


    public DashbordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashbord, container, false);

        init(view);
        clickListeners();
        if (isConnected(getActivity())) {
            fetchVehicleData();
            hitTasksList();
        } else {
            adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(),bitmapArray,responselist);
            tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
            rvVisits.setLayoutManager(manager);
            rvVisits.setAdapter(adapter);
            if (!(tinyDB.getString(Constants.vehicle_details).isEmpty())) {
                llNoVehicleData.setVisibility(View.GONE);
                llVName.setVisibility(View.VISIBLE);
                llVRegistration.setVisibility(View.VISIBLE);

                Gson gson = new Gson();
                String json = tinyDB.getString(Constants.vehicle_details);

                final VehicleDetail obj = gson.fromJson(json, VehicleDetail.class);

                tvBedsCount.setText(obj.getBedSpace() + "");
                tvColor.setText(obj.getVehicleName());
                tvEngineHorsepower.setText(obj.getEnginHouspower() + "");
                tvRegistrationNo.setText(obj.getRegistrationno());

                llVehicleDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), VehicleDetails.class);
                        i.putExtra(Constants.user_id, obj.getUserId());
                        startActivity(i);
                    }
                });


            } else {

                llNoVehicleData.setVisibility(View.VISIBLE);
                llVName.setVisibility(View.INVISIBLE);
                llVRegistration.setVisibility(View.INVISIBLE);

            }
        }

        return view;
    }

    private void init(View v) {
        tinyDB = new TinyDB(getActivity());
        databaseHandler = new DatabaseHandler(getActivity());

        profileImage = v.findViewById(R.id.profileImage);

        if (isConnected(getActivity()))
            if (!(tinyDB.getString(Constants.profile_image).isEmpty()))
                Glide.with(getActivity()).load(tinyDB.getString(Constants.profile_image)).into(profileImage);

        rvVisits = v.findViewById(R.id.rvVisits);
        manager = new LinearLayoutManager(getActivity());
        tvCallsCount = v.findViewById(R.id.tvCallsCount);
        llLogout = v.findViewById(R.id.llLogout);

        tvBedsCount = v.findViewById(R.id.tvBedsCount);
        tvEngineHorsepower = v.findViewById(R.id.tvEngineHorsepower);
        tvColor = v.findViewById(R.id.tvColor);
        tvRegistrationNo = v.findViewById(R.id.tvRegistrationNo);

        llVehicleDetails = v.findViewById(R.id.llVehicleDetails);
        pullToRefresh = v.findViewById(R.id.pullToRefresh);

        ivStatus = v.findViewById(R.id.ivStatus);
        tvStatus = v.findViewById(R.id.tvStatus);

        ivState = v.findViewById(R.id.ivState);
        tvState = v.findViewById(R.id.tvState);

        if (!(tinyDB.getString(Constants.StateTitle).isEmpty())) {
            tvState.setText(tinyDB.getString(Constants.StateTitle));
        } else {
            tinyDB.putString(Constants.StateTitle, "Available");
            tinyDB.putString(Constants.StateDescription, "Currently we are available and done with assigned incidents.");
            tinyDB.putInt(Constants.StatePosition, 4);
            tvState.setText(tinyDB.getString(Constants.StateTitle));
        }
        llState = v.findViewById(R.id.llState);

        llNoVehicleData = v.findViewById(R.id.llNoVehicleData);
        llVRegistration = v.findViewById(R.id.llVRegistration);
        llVName = v.findViewById(R.id.llVName);
        tvVehicleErrorMessage = v.findViewById(R.id.tvVehicleErrorMessage);

    }

    private void clickListeners() {

        llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutConfirmDialogBox();
            }
        });

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hitPullToRefreshTasksList(pullToRefresh);
                fetchVehicleData();
                pullToRefresh.setRefreshing(false);
            }
        });

        ivState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), StateActivity.class);
                startActivity(i);
            }
        });

        llState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), StateActivity.class);
                startActivity(i);
            }
        });


    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        mListener = null;
        super.onDetach();

    }

    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }


    public void hitTasksList() {
        retrofit2.Call<TasksListResponse> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.token),tinyDB.getString(Constants.user_id));
//        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.user_id));

        call.enqueue(new Callback<TasksListResponse>() {
            @Override
            public void onResponse(retrofit2.Call<TasksListResponse> call, final Response<TasksListResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {

                        databaseHandler.deleteAssignedIncidencesTable();
//                        databaseHandler.getAllIncidences().clear();
                        responselist = response;
//                        bitmapArray.clear();

                        for (int i = 0; i < response.body().getTaskList().size(); i++) {

                            AssignedIncidencesModel model = new AssignedIncidencesModel();
                            model.setId(response.body().getTaskList().get(i).getIncidencId());
                            Gson gson = new Gson();
                            String json = gson.toJson(response.body().getTaskList().get(i));
                            model.setJson(json);
                            databaseHandler.addIncidences(model);
                            Log.d("SIZE ARRAY", "Array size is: " + databaseHandler.getAllIncidences());
                            Log.d("SIZE ARRAY", "server Array size is: " + response.body().getTaskList().size());
                            adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(),bitmapArray,response);
                        }





                        tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
                        rvVisits.setLayoutManager(manager);
                        rvVisits.setAdapter(adapter);


//                        if (adapter != null) {
//                            adapter.setItems(response.body().getTaskList());
//                            adapter.notifyDataSetChanged();
//                        } else {
//                            adapter = new AdapterTasksList(response.body().getTaskList(), getActivity());
//                            rvVisits.setLayoutManager(manager);
//                            rvVisits.setAdapter(adapter);
////                        tvCallsCount.setVisibility(View.VISIBLE);
//                            tvCallsCount.setText(response.body().getTaskList().size() + "");
//
//                        }
                    } else if (response.body().getStatus() == 404) {

                        databaseHandler.deleteAssignedIncidencesTable();
                        adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(),bitmapArray,response);
                        tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
                        rvVisits.setLayoutManager(manager);
                        rvVisits.setAdapter(adapter);

                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<TasksListResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        hitTasksList();
    }
//    private class ConvertUrlToBitmap extends AsyncTask<String, Long, Bitmap> {
//        @Override
//        protected Bitmap doInBackground(String... params) {
//
//            try {
//                URL url = new URL(params[0]);
//                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//                bitmapArray.add(bitmap);
//                Log.e("biTMAPARRAY","BITMAP ARRAY SIZE "+bitmapArray.size());
//                return bitmap;
//            } catch (Exception e) {
//                Log.e("", e.toString());
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(final Bitmap image) {
//            super.onPostExecute(image);
//
//
//
//
//
//        }
//    }

    public void hitPullToRefreshTasksList(final SwipeRefreshLayout pullToRefresh) {
        retrofit2.Call<TasksListResponse> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.token),tinyDB.getString(Constants.user_id));
//        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.user_id));
        call.enqueue(new Callback<TasksListResponse>() {
            @Override
            public void onResponse(retrofit2.Call<TasksListResponse> call, final Response<TasksListResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        databaseHandler.deleteAssignedIncidencesTable();

                        for (int i = 0; i < response.body().getTaskList().size(); i++) {

                            AssignedIncidencesModel model = new AssignedIncidencesModel();
                            model.setId(response.body().getTaskList().get(i).getIncidencId());
                            Gson gson = new Gson();
                            String json = gson.toJson(response.body().getTaskList().get(i));
                            model.setJson(json);

                            databaseHandler.addIncidences(model);

                        }

                        adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(),bitmapArray,response);
                        tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
                        rvVisits.setLayoutManager(manager);
                        rvVisits.setAdapter(adapter);

                        pullToRefresh.setRefreshing(false);

                    } else if (response.body().getStatus() == 404) {
                        databaseHandler.deleteAssignedIncidencesTable();
                        adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(),bitmapArray,response);
                        tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
                        rvVisits.setLayoutManager(manager);
                        rvVisits.setAdapter(adapter);
                        pullToRefresh.setRefreshing(false);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<TasksListResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void hitRefreshTasksList() {
        retrofit2.Call<TasksListResponse> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.token),tinyDB.getString(Constants.user_id));

//        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.user_id));
        call.enqueue(new Callback<TasksListResponse>() {
            @Override
            public void onResponse(retrofit2.Call<TasksListResponse> call, final Response<TasksListResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {

                        databaseHandler.deleteAssignedIncidencesTable();

                        for (int i = 0; i < response.body().getTaskList().size(); i++) {

                            AssignedIncidencesModel model = new AssignedIncidencesModel();
                            model.setId(response.body().getTaskList().get(i).getIncidencId());
                            Gson gson = new Gson();
                            String json = gson.toJson(response.body().getTaskList().get(i));
                            model.setJson(json);

                            databaseHandler.addIncidences(model);

                        }

                        adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(),bitmapArray,response);
                        rvVisits.setLayoutManager(manager);
                        rvVisits.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<TasksListResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void logoutConfirmDialogBox() {
        final Dialog dialog = new Dialog(getActivity());
        final CustomButton btnNo, btnYes;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_logout);
        btnYes = (CustomButton) dialog.findViewById(R.id.btnYes);
        btnNo = (CustomButton) dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                tinyDB.putBoolean(Constants.LoggedIn, false);
                tinyDB.remove(Constants.user_id);
                tinyDB.remove(Constants.email);
                tinyDB.remove(Constants.first_name);
                tinyDB.remove(Constants.last_name);
                tinyDB.remove(Constants.username);

                Intent i = new Intent(getActivity(), Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                dialog.hide();
                getActivity().finishAffinity();
            }
        });
        dialog.show();
    }

    public void fetchVehicleData() {

        retrofit2.Call<VehicleResponse> call;
        call = RetrofitClass.getInstance().getWebRequestsInstance().getVehicleDetail(tinyDB.getString(Constants.token),tinyDB.getString(Constants.user_id));

        call.enqueue(new Callback<VehicleResponse>() {

            @Override
            public void onResponse(Call<VehicleResponse> call, final Response<VehicleResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {


                        Gson gson = new Gson();
                        String json = gson.toJson(response.body().getVehicleDetail());
                        tinyDB.putString(Constants.vehicle_details, json);

                        llNoVehicleData.setVisibility(View.GONE);
                        llVName.setVisibility(View.VISIBLE);
                        llVRegistration.setVisibility(View.VISIBLE);

                        tvBedsCount.setText(response.body().getVehicleDetail().getBedSpace() + "");
                        tvColor.setText(response.body().getVehicleDetail().getVehicleName());
                        tvEngineHorsepower.setText(response.body().getVehicleDetail().getEnginHouspower() + "");
                        tvRegistrationNo.setText(response.body().getVehicleDetail().getRegistrationno());

                        tinyDB.putInt("vehicle_id", response.body().getVehicleDetail().getVehicleId());

//                        llVehicleDetails.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent i = new Intent(getActivity(), VehicleDetails.class);
//                                i.putExtra(Constants.user_id, response.body().getVehicleDetail().getUserId());
//                                startActivity(i);
//                            }
//                        });

                    } else {
//                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        llNoVehicleData.setVisibility(View.VISIBLE);
                        llVName.setVisibility(View.INVISIBLE);
                        llVRegistration.setVisibility(View.INVISIBLE);
                        tvVehicleErrorMessage.setText("Vehicle is not assigned, Please contact Admin to assign Vehicle!");
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<VehicleResponse> call, Throwable t) {
//                Toast.makeText(getActivity(), "Can't connect to Server.", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onResume() {

        hitTasksList();
        h.postDelayed(runnable = new Runnable() {
            public void run() {

                if (isConnected(getActivity())) {
                    fetchVehicleData();
                    hitTasksList();
                } else {
                    adapter = new AdapterTasksList(databaseHandler.getAllIncidences(), getActivity(),bitmapArray,responselist);
                    tvCallsCount.setText(databaseHandler.getAllIncidences().size() + "");
                    rvVisits.setLayoutManager(manager);
                    rvVisits.setAdapter(adapter);
                    if (!(tinyDB.getString(Constants.vehicle_details).isEmpty())) {
                        llNoVehicleData.setVisibility(View.GONE);
                        llVName.setVisibility(View.VISIBLE);
                        llVRegistration.setVisibility(View.VISIBLE);

                        Gson gson = new Gson();
                        String json = tinyDB.getString(Constants.vehicle_details);

                        final VehicleDetail obj = gson.fromJson(json, VehicleDetail.class);

                        tvBedsCount.setText(obj.getBedSpace() + "");
                        tvColor.setText(obj.getVehicleName());
                        tvEngineHorsepower.setText(obj.getEnginHouspower() + "");
                        tvRegistrationNo.setText(obj.getRegistrationno());

                        llVehicleDetails.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                    return;
                                }
                                mLastClickTime = SystemClock.elapsedRealtime();
                                Intent i = new Intent(getActivity(), VehicleDetails.class);
                                i.putExtra(Constants.user_id, obj.getUserId());
                                startActivity(i);
                            }
                        });

                    } else {

                        llNoVehicleData.setVisibility(View.VISIBLE);
                        llVName.setVisibility(View.INVISIBLE);
                        llVRegistration.setVisibility(View.INVISIBLE);

                    }
                }
                h.postDelayed(runnable, delay);
            }
        }, delay);


        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(
                Constants.BROADCAST_ACTION));

        if (!(tinyDB.getString(Constants.StateTitle).isEmpty())) {
            tvState.setText(tinyDB.getString(Constants.StateTitle));
        } else {
            tinyDB.putString(Constants.StateTitle, "Available");
            tinyDB.putString(Constants.StateDescription, "Currently we are available and done with assigned incidents.");
            tinyDB.putInt(Constants.StatePosition, 4);
            tvState.setText(tinyDB.getString(Constants.StateTitle));
        }

        super.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        Boolean status = false;

        @Override
        public void onReceive(Context context, Intent intent) {

            status = intent.getBooleanExtra(Constants.Status, false);

            if (status) {
                ivStatus.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_status_online_new));
                tvStatus.setText("Online");
                tvStatus.setTextColor(getActivity().getResources().getColor(R.color.green));
            } else {
                ivStatus.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_status_offline_new));
                tvStatus.setText("Offline");
                tvStatus.setTextColor(getActivity().getResources().getColor(R.color.colorGray));
            }
            Log.e("Status", intent.getBooleanExtra(Constants.Status, false) + "");

        }
    };
}
