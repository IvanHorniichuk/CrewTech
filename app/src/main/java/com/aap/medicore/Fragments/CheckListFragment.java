package com.aap.medicore.Fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aap.medicore.Activities.AppConstants;
import com.aap.medicore.Activities.GpsUtils;
import com.aap.medicore.Adapters.NewAdapterAdmin;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.BaseClasses.BaseFragment;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.AdminFormModel;
import com.aap.medicore.Models.DBAdminFormModel;
import com.aap.medicore.Models.NewAdminChecklist;
import com.aap.medicore.Models.SaveField2;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.SessionTimeoutDialog;
import com.aap.medicore.Utils.TinyDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckListFragment extends BaseFragment {
    //    CustomTextView tvVehicleCheckList, tvEquipmentCheckList;
    private SwipeRefreshLayout pullToRefresh;
    View v;
    RecyclerView rv_admin;
    NewAdapterAdmin adapter;
    private TinyDB tinyDB;
    ArrayList<String> animalNames = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private CheckListFragment.OnFragmentInteractionListener mListener;

    //varibles for location
    private FusedLocationProviderClient mFusedLocationClient;
    private double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private android.widget.Button btnLocation;
    private android.widget.Button btnContinueLocation;
    private StringBuilder stringBuilder;
    //    private ProgressBar progressBar;
    private boolean isContinue = false;
    private boolean isGPS = false;
    private View ltNoNetwork;
    boolean networkAvailable;

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            networkAvailable = intent.getBooleanExtra("networkAvailable", false);
        }
    };

//    Handler h = new Handler();
//    int delay = 5 * 1000; //1 second=1000 milisecond, 15*1000=15seconds
//    Runnable runnable;


    public CheckListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHandler = new DatabaseHandler(getActivity());

    }

    @Override
    public void onResume() {

//        h.postDelayed(runnable = new Runnable() {
//            public void run() {
//                if (getContext()!=null){
//                if (isConnected(getActivity())) {
//                    hitTasksList();
//                } else {
////                    Toast.makeText(getActivity(), "Please check your internet conection", Toast.LENGTH_SHORT).getDialog();
//                }
//                h.postDelayed(runnable, delay);
//            }}
//        }, delay);

        super.onResume();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_check_list, container, false);
        init(v);
        clickListeners();
        hitTasksList();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hitTasksList();
//                pullToRefresh.setRefreshing(false);
            }
        });


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(getActivity()).turnGPSOn(new GpsUtils.onGpsListener() {
            @Override
            public void gpsStatus(boolean isGPSEnable) {
                // turn on GPS
                isGPS = isGPSEnable;
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
//                        Toast.makeText(getActivity(), ""+wayLatitude+" ,"+wayLongitude, Toast.LENGTH_SHORT).getDialog();
                        Log.e("location", "loccccccccccc" + wayLongitude + " ," + wayLatitude);
                        if (!isContinue) {

                        } else {
                            stringBuilder.append(wayLatitude);
                            stringBuilder.append("-");
                            stringBuilder.append(wayLongitude);
                            stringBuilder.append("\n\n");

                        }
                        if (!isContinue && mFusedLocationClient != null) {
                            mFusedLocationClient.removeLocationUpdates(locationCallback);
                        }
                    }
                }
            }
        };


        getLocation();


        return v;
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
//                        txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
//                        Toast.makeText(this, ""+wayLongitude, Toast.LENGTH_SHORT).getDialog();
                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
        }
    }

    private void init(View view) {
//        tvEquipmentCheckList = view.findViewById(R.id.tvEquipmentCheckList);
//        tvVehicleCheckList = view.findViewById(R.id.tvVehicleCheckList);
        rv_admin = view.findViewById(R.id.rv_admin);
        tinyDB = new TinyDB(getActivity());
        pullToRefresh = v.findViewById(R.id.pullToRefresh);
//        progressBar = v.findViewById(R.id.buffering_icon);
        ltNoNetwork = v.findViewById(R.id.ltNoNetwork);
//        animalNames.add("Amin Form 1");
//        animalNames.add("Amin Form 2");
        // set up the RecyclerView


    }

    private void clickListeners() {
//        tvVehicleCheckList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                    return;
//                }
//                mLastClickTime = SystemClock.elapsedRealtime();
//                if (isConnected(getActivity())) {
//                    Intent i = new Intent(getActivity(), VehicleCheckList.class);
//                    startActivity(i);
//                } else {
//                    Toast.makeText(getActivity(), "No Internet connection!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        tvEquipmentCheckList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                    return;
//                }
//                mLastClickTime = SystemClock.elapsedRealtime();
//                if (isConnected(getActivity())) {
//                    Intent i = new Intent(getActivity(), EquipmentCheckList.class);
//                    startActivity(i);
//                } else {
//                    Toast.makeText(getActivity(), "No Internet connection!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }


//    public void hitTasksList() {
//        retrofit2.Call<AdminForms> call;
//
//        call = RetrofitClass.getInstance().getWebRequestsInstance().hitAdminFormsList(tinyDB.getString(Constants.token), tinyDB.getString(Constants.user_id));
////        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.user_id));
//
//        call.enqueue(new Callback<AdminForms>() {
//            @Override
//            public void onResponse(retrofit2.Call<AdminForms> call, final Response<AdminForms> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus() == 200) {
//                        animalNames.clear();
//                        for (int i = 0; i < response.body().getVehicleChecklistForm().getForms().size(); i++) {
//                            progressBar.setVisibility(View.GONE);
//                            animalNames.add(response.body().getVehicleChecklistForm().getForms().get(i).getTitle());
//
//                            rv_admin.setLayoutManager(new LinearLayoutManager(getActivity()));
//                            if (getActivity()!=null)
//                            adapter = new AdapterAdmin(getActivity(), animalNames, response);
//                            rv_admin.setAdapter(adapter);
//                        }
//                        Log.e("size", "sizeeee" + animalNames.size());
//                    } else if (response.body().getStatus() == 404) {
//
//                        animalNames.clear();
//                        rv_admin.setLayoutManager(new LinearLayoutManager(getActivity()));
//                        if (getActivity() != null)
//                            adapter = new AdapterAdmin(getActivity(), animalNames, response);
//                        rv_admin.setAdapter(adapter);
//                    }
//                }else if (response.code()==401){
//
//                    if (getActivity()!= null)
//                        new SessionTimeoutDialog((BaseActivity) getActivity()).getDialog().show();
//                }
//            }
//
//            @Override
//            public void onFailure(retrofit2.Call<AdminForms> call, Throwable t) {
//                t.printStackTrace();
//            }
//        });
//    }

    private void hitTasksList() {
        retrofit2.Call<NewAdminChecklist> call = RetrofitClass.getInstance().getWebRequestsInstance().getAdminForms(tinyDB.getString(Constants.token), tinyDB.getString(Constants.user_id));
        try {
            call.enqueue(new Callback<NewAdminChecklist>() {
                @Override
                public void onResponse(Call<NewAdminChecklist> call, Response<NewAdminChecklist> response) {
                    if (response.isSuccessful()) {
//                        databaseHandler.deleteAdminFormsTable();
                        if (response.body().getStatus() == 400) {
                            rv_admin.setVisibility(View.GONE);
                            v.findViewById(R.id.tvNoVehicle).setVisibility(View.VISIBLE);
                            return;
                        } else {
                            rv_admin.setVisibility(View.VISIBLE);
                            v.findViewById(R.id.tvNoVehicle).setVisibility(View.GONE);
                            List<AdminFormModel> list = response.body().getForms();
                            for (int i = 0; i < list.size(); i++) {
                                DBAdminFormModel model1 = new DBAdminFormModel();
                                model1.setForm_id(list.get(i).getId());
                                model1.setTitle(list.get(i).getTitle());
                                model1.setSub_title(list.get(i).getSubtitle());
                                model1.setTimeout(list.get(i).getTimeout());
                                Gson gson = new Gson();
                                model1.setFieldsJson(gson.toJson(list.get(i).getFieldsJson()));
                                model1.setImages("");
                                //if new form is added
                                if (databaseHandler.getAdminForm(model1.getForm_id()).getForm_id() == 0)
                                    databaseHandler.addAdminForm(model1);
                                else {
                                    //if form is updated
                                    if (list.get(i).getFieldsJson().size() > 0) {
                                        SaveField2 saveField1 = list.get(i).getFieldsJson().get(0);
                                        Gson gson1 = new Gson();
                                        List<SaveField2> fields = new ArrayList<>();
                                        try {
                                            fields = gson1.fromJson(databaseHandler.getAdminForm(list.get(i).getId()).getFieldsJson(), new TypeToken<List<SaveField2>>() {
                                            }.getType());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (fields.size() > 0) {
                                            SaveField2 saveField2 = fields.get(0);
                                            if (!saveField1.getFieldId().equals(saveField2.getFieldId())) {
                                                databaseHandler.removeAdminForm(list.get(i).getId());
                                                databaseHandler.addAdminForm(model1);
                                            }
                                        }
                                    }
                                }
                            }
                            rv_admin.setLayoutManager(new LinearLayoutManager(getActivity()));
                            if (getActivity() != null)
                                adapter = new NewAdapterAdmin(getContext(), list);
                            rv_admin.setAdapter(adapter);
                            rv_admin.getViewTreeObserver()
                                    .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                        @Override
                                        public void onGlobalLayout() {
                                            //At this point the layout is complete and the
                                            //dimensions of recyclerView and any child views are known.
                                            //Remove listener after changed RecyclerView's height to prevent infinite
                                            pullToRefresh.setRefreshing(false);
                                            rv_admin.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                        }
                                    });
                        }
                    } else if (response.code() == 401) {
                        if (getActivity() != null)
                            new SessionTimeoutDialog((BaseActivity) getActivity()).getDialog().show();
                        pullToRefresh.setRefreshing(false);

                    }
                }

                @Override
                public void onFailure(Call<NewAdminChecklist> call, Throwable t) {
                    t.printStackTrace();
                    pullToRefresh.setRefreshing(false);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            pullToRefresh.setRefreshing(false);

        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
//        h.removeCallbacks(runnable); //stop handler when activity not visible
        mListener = null;
        super.onDetach();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
