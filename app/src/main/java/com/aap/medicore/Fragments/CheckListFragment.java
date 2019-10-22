package com.aap.medicore.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aap.medicore.Activities.AppConstants;
import com.aap.medicore.Activities.EquipmentCheckList;
import com.aap.medicore.Activities.GpsUtils;
import com.aap.medicore.Activities.TaskDetails;
import com.aap.medicore.Activities.VehicleCheckList;
import com.aap.medicore.Activities.VehicleDetails;
import com.aap.medicore.Adapters.AdapterAdmin;
import com.aap.medicore.Adapters.AdapterTasksList;
import com.aap.medicore.Adapters.SpinnerArrayAdapter;
import com.aap.medicore.BaseClasses.BaseFragment;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.AdminForms;
import com.aap.medicore.Models.AssignedIncidencesModel;
import com.aap.medicore.Models.EquipmentChecklistForm;
import com.aap.medicore.Models.EquipmentChecklistResponse;
import com.aap.medicore.Models.TasksListResponse;
import com.aap.medicore.Models.VehicleDetail;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomEditText;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.SettingValues;
import com.aap.medicore.Utils.TinyDB;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Callback;
import retrofit2.Response;

public class CheckListFragment extends BaseFragment {
    CustomTextView tvVehicleCheckList, tvEquipmentCheckList;
    private SwipeRefreshLayout pullToRefresh;
    View v;
    RecyclerView rv_admin;
    AdapterAdmin adapter;
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

    private boolean isContinue = false;
    private boolean isGPS = false;

    Handler h = new Handler();
    int delay = 5 * 1000; //1 second=1000 milisecond, 15*1000=15seconds
    Runnable runnable;



    public CheckListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {



        h.postDelayed(runnable = new Runnable() {
            public void run() {

                if (isConnected(getActivity())) {
                    hitTasksList();
                }
                else {
//                    Toast.makeText(getActivity(), "Please check your internet conection", Toast.LENGTH_SHORT).show();
                }
                h.postDelayed(runnable, delay);
            }
        }, delay);

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
                pullToRefresh.setRefreshing(false);
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
//                        Toast.makeText(getActivity(), ""+wayLatitude+" ,"+wayLongitude, Toast.LENGTH_SHORT).show();
                        Log.e("location","loccccccccccc"+wayLongitude+" ,"+wayLatitude);
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
//                        Toast.makeText(this, ""+wayLongitude, Toast.LENGTH_SHORT).show();
                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
        }
    }
    private void init(View view) {
        tvEquipmentCheckList = view.findViewById(R.id.tvEquipmentCheckList);
        tvVehicleCheckList = view.findViewById(R.id.tvVehicleCheckList);
        rv_admin = view.findViewById(R.id.rv_admin);
        tinyDB = new TinyDB(getActivity());
        pullToRefresh = v.findViewById(R.id.pullToRefresh);
//        animalNames.add("Amin Form 1");
//        animalNames.add("Amin Form 2");
        // set up the RecyclerView



    }

    private void clickListeners() {
        tvVehicleCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isConnected(getActivity())) {
                    Intent i = new Intent(getActivity(), VehicleCheckList.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity(), "No Internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvEquipmentCheckList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (isConnected(getActivity())) {
                    Intent i = new Intent(getActivity(), EquipmentCheckList.class);
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity(), "No Internet connection!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void hitTasksList() {
        retrofit2.Call<AdminForms> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitAdminFormsList(tinyDB.getString(Constants.token),tinyDB.getString(Constants.user_id));
//        call = RetrofitClass.getInstance().getWebRequestsInstance().hitTasksList(tinyDB.getString(Constants.user_id));

        call.enqueue(new Callback<AdminForms>() {
            @Override
            public void onResponse(retrofit2.Call<AdminForms> call, final Response<AdminForms> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        animalNames.clear();
                        for (int i = 0; i < response.body().getVehicleChecklistForm().getForms().size(); i++) {

                         animalNames.add(response.body().getVehicleChecklistForm().getForms().get(i).getTitle());

                            rv_admin.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new AdapterAdmin (getActivity(), animalNames,response);

                            rv_admin.setAdapter(adapter);
                        }

                        Log.e("size","sizeeee"+animalNames.size());



                    } else if (response.body().getStatus() == 404) {

                        animalNames.clear();
                        rv_admin.setLayoutManager(new LinearLayoutManager(getActivity()));
                            adapter = new AdapterAdmin (getActivity(), animalNames,response);
                            rv_admin.setAdapter(adapter);

                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<AdminForms> call, Throwable t) {
                t.printStackTrace();
            }
        });
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
        h.removeCallbacks(runnable); //stop handler when activity not visible
        mListener = null;
        super.onDetach();
    }



    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
