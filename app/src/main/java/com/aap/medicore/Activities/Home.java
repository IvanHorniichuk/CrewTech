package com.aap.medicore.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import com.aap.medicore.Adapters.MainPagerAdapter;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.Fragments.CheckListFragment;
import com.aap.medicore.Fragments.Staticpages;
import com.aap.medicore.Fragments.DashbordFragment;
import com.aap.medicore.Fragments.PendingUploadsFragment;
import com.aap.medicore.Models.StatusResponse;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.TinyDB;
import com.aap.medicore.Utils.UploaderService;
import com.aap.medicore.Utils.myPager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.novoda.merlin.Connectable;
import com.novoda.merlin.Merlin;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import retrofit2.Callback;
import retrofit2.Response;


public class Home extends BaseActivity implements CheckListFragment.OnFragmentInteractionListener, DashbordFragment.OnFragmentInteractionListener, PendingUploadsFragment.OnFragmentInteractionListener, Staticpages.OnFragmentInteractionListener {
    myPager pager;

    Intent newintent;
    TinyDB tinyDB;
    String state = "";
    private Boolean status = false;
    private Merlin merlin;
    RecyclerView rvForms;
    LinearLayoutManager manager;
    MainPagerAdapter pagerAdapter;
    BottomNavigationView bottomNavigationView;

    //varibles for location
    private FusedLocationProviderClient mFusedLocationClient;
    public static double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private android.widget.Button btnLocation;
    private android.widget.Button btnContinueLocation;
    private StringBuilder stringBuilder;

    private boolean isContinue = false;
    private boolean isGPS = false;


    Handler h = new Handler();
    int delay = 2 * 1000; //1 second=1000 milisecond, 15*1000=15seconds
    Runnable runnable;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_check_list:
                    pager.setCurrentItem(0, false);
                    return true;

                case R.id.navigation_dashboard:
                    pager.setCurrentItem(1, false);
                    return true;

                case R.id.navigation_patient_information:
                    pager.setCurrentItem(2, false);
                    return true;
                    case R.id.Staticpages:
                    pager.setCurrentItem(3, false);
                    return true;

            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        init();
        clickListeners();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10 * 1000); // 10 seconds
        locationRequest.setFastestInterval(5 * 1000); // 5 seconds

        new GpsUtils(this).turnGPSOn(new GpsUtils.onGpsListener() {
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



        if (!(tinyDB.getString(Constants.StateTitle).isEmpty())) {
            state = tinyDB.getString(Constants.StateTitle);
        }

        retrofit2.Call<StatusResponse> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitUserStatus(tinyDB.getString(Constants.token), tinyDB.getString(Constants.user_id), state);

        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(retrofit2.Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful()) {
                    Log.e("code", response.body().getStstus() + "");
                    if (response.body().getStstus() == 200) {
                        status = true;
                    } else {
                        status = false;
                    }
                } else {
                    status = false;
                }
            }

            @Override
            public void onFailure(retrofit2.Call<StatusResponse> call, Throwable t) {
                status = false;
            }
        });
        newintent = new Intent(Constants.BROADCAST_ACTION);
        newintent.putExtra(Constants.Status, status);
        sendBroadcast(newintent);

        if (isConnected(Home.this)) {

        }

        merlin.registerConnectable(new Connectable() {
            @Override
            public void onConnect() {
                Intent intent = new Intent(Home.this, UploaderService.class);
                startService(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Intent i = new Intent(Home.this,Home.class);
//        startActivity(i);
    }

    private BroadcastReceiver mConnReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);

            NetworkInfo currentNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

            if (currentNetworkInfo.isConnected()) {
//                Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                Log.d("SERVICE", "about to call service");
                Intent newIntent = new Intent(Home.this, UploaderService.class);
                startService(newIntent);
            } else {
//                Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Home.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(Home.this, location -> {
                    if (location != null) {
                        wayLatitude = location.getLatitude();
                        wayLongitude = location.getLongitude();
//                        Toast.makeText(this, ""+wayLatitude, Toast.LENGTH_SHORT).show();
//                        txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
//                        Toast.makeText(this, ""+wayLongitude, Toast.LENGTH_SHORT).show();
                    } else {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        //start handler as activity become visible
        merlin.bind();
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        if (!(tinyDB.getString(Constants.StateTitle).isEmpty())) {
            state = tinyDB.getString(Constants.StateTitle);
        }
        h.postDelayed(runnable = new Runnable() {
            public void run() {

                retrofit2.Call<StatusResponse> call;

                call = RetrofitClass.getInstance().getWebRequestsInstance().hitUserStatus(tinyDB.getString(Constants.token),tinyDB.getString(Constants.user_id), state);

                call.enqueue(new Callback<StatusResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<StatusResponse> call, Response<StatusResponse> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getStstus() == 200) {
                                status = true;
                            } else {
                                status = false;
                            }
                        } else {
                            status = false;
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<StatusResponse> call, Throwable t) {
                        status = false;
                    }
                });
                newintent = new Intent(Constants.BROADCAST_ACTION);
                newintent.putExtra(Constants.Status, status);
                sendBroadcast(newintent);

                h.postDelayed(runnable, delay);
            }
        }, delay);

        super.onResume();
    }

    @Override
    protected void onPause() {
        h.removeCallbacks(runnable); //stop handler when activity not visible
        merlin.unbind();
        this.unregisterReceiver(this.mConnReceiver);
        super.onPause();
    }

    private void init() {
        tinyDB = new TinyDB(Home.this);

        merlin = new Merlin.Builder().withConnectableCallbacks().build(Home.this);
        this.registerReceiver(this.mConnReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        pager = findViewById(R.id.pager);
        setUpPager();
        pager.setCurrentItem(1, false);
        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);
    }

    private void clickListeners() {
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_check_list);
                }
                if (position == 1) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);

                }
                if (position == 2) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_patient_information);

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setUpPager() {
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);
//        pager.setPagingEnabled(false);
        pagerAdapter = new MainPagerAdapter(getSupportFragmentManager());

        pagerAdapter.addFragment(new CheckListFragment(), "CheckListFragment");
        pagerAdapter.addFragment(new DashbordFragment(), "DashbordFragment");
        pagerAdapter.addFragment(new PendingUploadsFragment(), "PendingUploadsFragment");
        pagerAdapter.addFragment(new Staticpages(), "Static Pages");
        pager.setAdapter(pagerAdapter);
        pager.setOffscreenPageLimit(4);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }


}
