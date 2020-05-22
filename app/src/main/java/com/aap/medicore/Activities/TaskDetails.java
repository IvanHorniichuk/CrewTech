package com.aap.medicore.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Adapters.AdapterTabs;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.AssignedIncidencesModel;
import com.aap.medicore.Models.DBImagesModel;
import com.aap.medicore.Models.QueueModel;
import com.aap.medicore.Models.SelectImagesModel;
import com.aap.medicore.Models.SubmitFormResponse;
import com.aap.medicore.Models.TabsModel;
import com.aap.medicore.Models.TaskList;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.GPSTracker;
import com.aap.medicore.Utils.NetworkReciever;
import com.aap.medicore.Utils.SessionTimeoutDialog;
import com.aap.medicore.Utils.SettingValues;
import com.aap.medicore.Utils.TinyDB;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetails extends BaseActivity {
    ImageView ivBack;
    public static TabsModel model1 = new TabsModel();
    String task_id = "", task_location = "", reported_agency = "";
    TextView heading, tvTaskLocation;
   TextView tvName, tvDate, tvMRN, tvMobility, tvInfectiousStatus, tvMedicalRequirements, tvAccount, tvOrderNo, tvFromFacility, tvToFacility, return_facility, dob, ref;
    CustomButton btnSubmit, btnSave;

    public ArrayList<SelectImagesModel> imagesList = new ArrayList<>();

    public static ArrayList<SelectImagesModel> sectionImagesList;

    //varibles for location
    private FusedLocationProviderClient mFusedLocationClient;
    static double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private android.widget.Button btnLocation;
    private android.widget.Button btnContinueLocation;
    private StringBuilder stringBuilder;

    private boolean isContinue = false;
    private boolean isGPS = false;


    ImageView ivReport;
    TinyDB tinyDB;
    public String temp = "";
    public static DatabaseHandler handler;
    MaskedEditText etDOB;
    String note;

    private long mLastClickTime = 0;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private Location mLocation;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private double lat = 0.0, lng = 0.0;
    TextView notes;

    LinearLayout ll;
    RecyclerView rvTabs;
    TaskList taskList;
    RelativeLayout savebtnlayout;
    public static ArrayList<TabsModel> tabsList;
    AdapterTabs adapterTabs;
    private GPSTracker gps;
    private View ltNoNetwork;
    private BroadcastReceiver broadcastReceiver;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_task_details);
        getSupportActionBar().hide();

        if (getIntent() != null) {
            task_id = getIntent().getStringExtra(Constants.task_id);
            SettingValues.setTaskId(task_id);
            task_location = getIntent().getStringExtra(Constants.task_location);
            reported_agency = getIntent().getStringExtra(Constants.task_witness);
        }
        BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//        Toast.makeText(this, ""+batLevel+"%", Toast.LENGTH_SHORT).getDialog();
        gps = new GPSTracker(this);
        init();
        clickListeners();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }
        broadcastReceiver = new NetworkReciever(ltNoNetwork);
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
//                        wayLatitude = location.getLatitude();
//                        wayLongitude = location.getLongitude();
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


//        getLocation();

    }


    private void init() {
        ivBack = findViewById(R.id.ivBack);
        tinyDB = new TinyDB(TaskDetails.this);
        savebtnlayout = (RelativeLayout) findViewById(R.id.savebtnlayout);
        handler = new DatabaseHandler(TaskDetails.this);


        heading = findViewById(R.id.heading);
        notes =  findViewById(R.id.note);
//        heading.setText(task_location);
        tvTaskLocation = findViewById(R.id.tvTaskLocation);
//        tvTaskLocation.setText(reported_agency);
        tvName = findViewById(R.id.tvName);
        tvDate = findViewById(R.id.tvDate);
        tvMRN = findViewById(R.id.tvMRN);
        tvMobility = findViewById(R.id.tvMobility);
        tvInfectiousStatus = findViewById(R.id.tvInfectiousStatus);
        tvMedicalRequirements = findViewById(R.id.tvMedicalRequirements);
        dob = findViewById(R.id.dob);
        ref = findViewById(R.id.ref);
        return_facility = findViewById(R.id.return_facility);
        tvAccount = findViewById(R.id.tvAccount);
        tvOrderNo = findViewById(R.id.tvOrderNo);
        tvFromFacility = findViewById(R.id.tvFromFacility);
        tvToFacility = findViewById(R.id.tvToFacility);

        btnSubmit = findViewById(R.id.btnLogin);
        ltNoNetwork = findViewById(R.id.ltNoNetwork);

        ll = findViewById(R.id.ll);
        ivReport = findViewById(R.id.ivReport);

        rvTabs = findViewById(R.id.rvTabs);

        AssignedIncidencesModel model = new AssignedIncidencesModel();

        model = handler.getIncidenceOnId(task_id);

        GridLayoutManager manager = new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false);


        Gson gson = new Gson();
        String json = model.getJson();
        taskList = gson.fromJson(json, TaskList.class);
        SettingValues.setTaskList(taskList);
        tabsList = new ArrayList<>();

        if (taskList != null) {
            for (int tabCount = 0; tabCount < taskList.getForm().size(); tabCount++) {
//                TabsModel model1 = new TabsModel();
                model1 = new TabsModel();
                model1.setTitle(taskList.getForm().get(tabCount).getTitle());
                model1.setTab_id(taskList.getForm().get(tabCount).getFormId().toString());
                model1.setJsonData("");
                model1.setPlus(taskList.getForm().get(tabCount).isPlus());

                ArrayList<TabsModel> tabmodel = new ArrayList<>();

                tabmodel = handler.gettabDetail(task_id, model1.getTab_id());
                Log.d("SIZE", "tab size is: " + tabmodel.size());
                if (tabmodel.size() > 0) {
                    Log.d("SIZE", "tab id is: " + tabmodel.get(0).getTab_id());
                    Log.d("SIZE", "tab second id is: " + model1.getTab_id());

                    if (tabmodel.get(0).getTab_id().equals(model1.getTab_id())) {
                        Log.e("ID", "TAB ID exist ");
                        model1.setJsonData(tabmodel.get(0).getJsonData());
                    }
                }

                ArrayList<SelectImagesModel> imgList = new ArrayList<>();

                imgList = handler.getAllTAbImagesOnImageId(model1.getTab_id(), task_id);
                Log.d("SIZE", "image size is :" + imgList.size());
                if (imgList.size() > 0)
                    model1.setSelectedImages(imgList);
                tabsList.add(model1);

            }
            SettingValues.setTabList(tabsList);


            if (taskList != null) {
                if (taskList.getName() != null && !taskList.getName().isEmpty())
                    tvName.setText(taskList.getName());
                if (taskList.getJobDateTime() != null && !taskList.getJobDateTime().isEmpty())
                    tvDate.setText(taskList.getJobDateTime());
                if (taskList.getMrn() != null && !taskList.getMrn().isEmpty())
                    tvMRN.setText(taskList.getMrn());
                if (taskList.getMobility() != null && !taskList.getMobility().isEmpty())
                    tvMobility.setText(taskList.getMobility());
                if (taskList.getInfectiousStatus() != null && !taskList.getInfectiousStatus().isEmpty())
                    tvInfectiousStatus.setText(taskList.getInfectiousStatus());
                if (taskList.getMedicalRequirments() != null && !taskList.getMedicalRequirments().isEmpty())
                    tvMedicalRequirements.setText(taskList.getMedicalRequirments());
                if (taskList.getAccount() != null && !taskList.getAccount().isEmpty())
                    tvAccount.setText(taskList.getAccount());
                if (taskList.getOrderNo() != null && !taskList.getOrderNo().isEmpty())
                    tvOrderNo.setText(taskList.getOrderNo());
                if (taskList.getFromFacitity() != null && !taskList.getFromFacitity().isEmpty())
                    tvFromFacility.setText(taskList.getFromFacitity());
                if (taskList.getToFacility() != null && !taskList.getToFacility().isEmpty())
                    tvToFacility.setText(taskList.getToFacility());
                if (taskList.getRef() != null && !taskList.getRef().isEmpty())
                    ref.setText(taskList.getRef());
                if (taskList.getDob() != null && !taskList.getDob().isEmpty())
                    dob.setText(taskList.getDob());
                if (taskList.getReturn_facility() != null && !taskList.getReturn_facility().isEmpty())
                    return_facility.setText(taskList.getReturn_facility());
                if (taskList.getNotes() != null && !taskList.getNotes().isEmpty())
                    notes.setText(taskList.getNotes());
                btnSave = findViewById(R.id.btnSave);
            }
            adapterTabs = new AdapterTabs(SettingValues.getTabList(), TaskDetails.this, task_id);
            rvTabs.setAdapter(adapterTabs);
            SettingValues.setAdapterTabs(adapterTabs);
            rvTabs.setLayoutManager(manager);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void clickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(TaskDetails.this, IncidentReport.class);
                i.putExtra("id", task_id + "");
                startActivity(i);
            }
        });
        ivReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(TaskDetails.this, IncidentReport.class);
                i.putExtra("id", task_id + "");
                startActivity(i);
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
// Log.e("Sizes1", "ImagesList: " + imagesList.size() + " TabsList: " + tabsList.get(0).getSelectedImages().size());
                imagesList.clear();
// Log.e("Sizes2", "ImagesList: " + imagesList.size() + " TabsList: " + tabsList.get(0).getSelectedImages().size());
                Boolean empty = false;
                for (int i = 0; i < tabsList.size(); i++) {
                    if (tabsList.get(i).getJsonData().isEmpty()) {
                        empty = true;
                        break;
                    }
                }
                if (empty) {
                    Toast.makeText(TaskDetails.this, "Please fill all the forms!", Toast.LENGTH_SHORT).show();
                } else {
                    logoutConfirmDialogBox();
                }
            }
        });
//                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                    return;
//                }
//                mLastClickTime = SystemClock.elapsedRealtime();
////                Log.e("Sizes1", "ImagesList: " + imagesList.size() + " TabsList: " + tabsList.get(0).getSelectedImages().size());
//                imagesList.clear();
////                Log.e("Sizes2", "ImagesList: " + imagesList.size() + " TabsList: " + tabsList.get(0).getSelectedImages().size());
//                Boolean empty = false;
//                for (int i = 0; i < tabsList.size(); i++) {
//                    if (tabsList.get(i).getJsonData().isEmpty()) {
//                        empty = true;
//                        break;
//                    }
//                }
//                if (empty) {
//                    Toast.makeText(TaskDetails.this, "Please fill forms!", Toast.LENGTH_SHORT).getDialog();
//                } else {
//                    try {
//
//                        JSONObject obj = new JSONObject();
//                        obj.put("job_id", task_id);
//                        obj.put("lat", lat);
//                        obj.put("lng", lng);
//                        JSONArray array = new JSONArray();
//                        for (int i = 0; i < tabsList.size(); i++) {
//                            if(tabsList.get(i).getSelectedImages() != null)
//                            if(tabsList.get(i).getSelectedImages().size() >0)
//                            imagesList.addAll(tabsList.get(i).getSelectedImages());
//                            JSONObject jsonObject = new JSONObject(tabsList.get(i).getJsonData());
//                            array.put(jsonObject);
//                        }
//                        obj.put("forms", array);
//                        Log.d("finalJson", obj + "");
//                        if (isConnected(TaskDetails.this)) {
//                            submitData(obj + "");
//                        } else {
//                            Toast.makeText(TaskDetails.this, "No network connection! your form is queued to be upload.", Toast.LENGTH_SHORT).getDialog();
//                            findViewById(R.id.progress).setVisibility(View.GONE);
//                            btnSubmit.setVisibility(View.VISIBLE);
//                            if (imagesList.size() == 0) {
//                                QueueModel model = new QueueModel();
//                                model.setId(task_id + "");
//                                model.setJson(obj + "");
//                                model.setState(Constants.StateAdded);
//                                model.setTitle(task_location);
//                                model.setMessage("Pending to upload");
//                                handler.addQueueIncidenceOnIncidenceID(model);
//                                handler.deleteAssignedIncidenceOnIndidenceId(model.getId());
//                            } else {
//                                QueueModel queueModel = new QueueModel();
//                                queueModel.setId(task_id + "");
//                                queueModel.setJson(obj + "");
//                                queueModel.setState(Constants.StateAdded);
//                                queueModel.setTitle(task_location);
//                                queueModel.setMessage("Pending upload");
//                                handler.addQueueIncidenceOnIncidenceID(queueModel);
//
//                                for (int i = 0; i < imagesList.size(); i++) {
//                                    DBImagesModel model = new DBImagesModel();
//                                    model.setIncidenceId(task_id);
//                                    model.setTempUri(imagesList.get(i).getTempUri());
//                                    handler.addImagesOnIncidenceID(model);
//                                }
//                                handler.deleteAssignedIncidenceOnIndidenceId(queueModel.getId());
//                            }
//                            finish();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
    }


//    private void deleteTempFolder(String dir) {
//        File myDir = new File(Environment.getExternalStorageDirectory() + "/"+dir);
//        if (myDir.isDirectory()) {
//            String[] children = myDir.list();
//            for (int i = 0; i < children.length; i++) {
//                new File(myDir, children[i]).delete();
//            }
//        }
//    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(TaskDetails.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(TaskDetails.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(TaskDetails.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(TaskDetails.this, location -> {
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, Splash.PERMISSION_REQUEST_CODE);
        } else {
            if (gps.canGetLocation()) {
                if (gps.getLatitude() != 0.0) {

                    wayLatitude = gps.getLatitude();
                    wayLongitude = gps.getLongitude();
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1000: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (isContinue) {
                        mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                    } else {
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(TaskDetails.this, location -> {
                            if (location != null) {
//                                wayLatitude = location.getLatitude();
//                                wayLongitude = location.getLongitude();
//                                txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
                            } else {
                                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
                            }
                        });
                    }
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    public void prepareData(QueueModel model, List<SelectImagesModel> imagesModels) {
        try {
            String json = model.getJson();
            JSONObject mainObject = new JSONObject(json);
            JSONArray array1 = mainObject.getJSONArray("fields");
            for (int i=0; i<array1.length();i++){
                JSONObject object = array1.getJSONObject(i);
                if (object.get("type").equals("file"))
                    array1.remove(i);
            }
            JSONArray imagesArray = new JSONArray();
            for (int i = 0; i < imagesModels.size(); i++) {
                imagesArray.put(imagesModels.get(i).getName());
            }
            mainObject.put("images",imagesArray);
            JSONObject object = new JSONObject();
            object.put("form-section", mainObject);
            JSONObject obj = new JSONObject();

            obj.put("job_id", task_id);
            obj.put("form_id", model.getId());
            JSONArray array = new JSONArray();
            array.put(object);
            obj.put("forms", array);
            RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), obj.toString());
            submitForms(bodyRequest, imagesModels);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void logoutConfirmDialogBox() {
        final Dialog dialog = new Dialog(this);
        final Button btnNo, btnYes;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.save_alert);
        btnYes = (Button) dialog.findViewById(R.id.btnYes);
        btnNo = (Button) dialog.findViewById(R.id.btnNo);

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnYes.setEnabled(true);
                dialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                findViewById(R.id.progress).setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.GONE);
                btnNo.setEnabled(false);
//                savebtnlayout.setVisibility(View.GONE);
                try {
                    btnYes.setEnabled(false);

                    JSONObject obj = new JSONObject();
                    obj.put("job_id", task_id);
                    obj.put("lat", wayLatitude);
                    obj.put("lng", wayLongitude);
                    obj.put("notes", notes.getText());
                    obj.put("dob", dob.getText());
                    obj.put("ref", ref.getText());
                    obj.put("return_facility", return_facility.getText());


                    JSONArray array = new JSONArray();
                    for (int i = 0; i < tabsList.size(); i++) {

                        ArrayList<SelectImagesModel> imgList = new ArrayList<>();

                        imgList = handler.getAllTAbImagesOnImageId(tabsList.get(i).getTab_id(), task_id);
                        Log.d("SIZE", "image size is :" + imgList.size());
                        if (imgList.size() > 0)
                            imagesList.addAll(imgList);
//                        if(tabsList.get(i).getSelectedImages() != null)
//                     if(tabsList.get(i).getSelectedImages().size() >0){
//                         Log.d("IMAGES","LISt size is: " + tabsList.get(i).getSelectedImages().size());
//                         imagesList.addAll(tabsList.get(i).getSelectedImages());
//
//                     }
                        JSONObject jsonObject = new JSONObject(tabsList.get(i).getJsonData());
                        jsonObject.put("form_id", tabsList.get(i).getTab_id());
                        array.put(jsonObject);
                    }
                    obj.put("forms", array);
                    Log.d("finalJson", obj + "");
                    if (isConnected(TaskDetails.this)) {
                        obj.remove("forms");
                        submitData(obj + "", dialog);

                    } else {
                        tinyDB.putBoolean(Constants.pendingStatus, true);
                        Toast.makeText(TaskDetails.this, "No network connection! your form is queued to be upload.", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progress).setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                        if (imagesList.size() == 0) {
                            QueueModel model = new QueueModel();
                            model.setId(task_id + "");
                            model.setJson(obj + "");
                            model.setState(Constants.StateAdded);
                            model.setTitle(task_location);
                            model.setMessage("Pending to upload");
                            model.setNumSubmitted("0");
                            handler.addQueueIncidenceOnIncidenceID(model);
                            handler.deleteAssignedIncidenceOnIndidenceId(model.getId());
                        } else {
                            QueueModel queueModel = new QueueModel();
                            queueModel.setId(task_id + "");
                            queueModel.setJson(obj + "");
                            queueModel.setState(Constants.StateAdded);
                            queueModel.setTitle(task_location);
                            queueModel.setMessage("Pending upload");
                            queueModel.setNumSubmitted("0");
                            handler.addQueueIncidenceOnIncidenceID(queueModel);

                            for (int i = 0; i < imagesList.size(); i++) {
                                DBImagesModel model = new DBImagesModel();
                                model.setIncidenceId(task_id);
                                model.setTempUri(imagesList.get(i).getTempUri());
                                model.setName(imagesList.get(i).getName());
                                handler.addImagesOnIncidenceID(model);
                            }
                            handler.deleteAssignedIncidenceOnIndidenceId(queueModel.getId());
                        }


                        finish();
                        findViewById(R.id.progress).setVisibility(View.GONE);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.show();
    }

    public void resetView(String position, String json, Context ctx,
                          boolean isAllMandatoryDataFilled, boolean isSubmitted) {
        Log.e("mandatory", "mandatory fields are " + isAllMandatoryDataFilled);
        Log.d("CALLEEEEEEEEd", "func calllled" + json);
        if (tabsList == null) {
            tabsList = SettingValues.getTabList();
        }
        if (taskList == null) {

            taskList = SettingValues.getTaskList();
        }
        TabsModel t = new TabsModel();
        t.setTitle(tabsList.get(Integer.parseInt(position)).getTitle());
        if (isAllMandatoryDataFilled) {
            t.setJsonData(json);
        } else {
            t.setJsonData("");
        }
        t.setSubmitted(isSubmitted);
        t.setTab_id(tabsList.get(Integer.parseInt(position)).getTab_id());
        t.setPlus(tabsList.get(Integer.parseInt(position)).isPlus());


        handler = new DatabaseHandler(ctx);

        handler.addTabs(t, SettingValues.getTaskId());

        Log.d("SIZE", "IMage list size is : " + sectionImagesList.size());


        List<SelectImagesModel> selectImagesModels = new ArrayList<>();
        for (int i = 0; i < sectionImagesList.size(); i++) {
            SelectImagesModel model = new SelectImagesModel();
            model.setTaskId(SettingValues.getTaskId());
            model.setTempUri(sectionImagesList.get(i).getTempUri());
            model.setFormId(SettingValues.getFormId());
            model.setFinalImage(sectionImagesList.get(i).getFinalImage());
            model.setName(sectionImagesList.get(i).getName());
            Log.d("VALUE", "IMG VALUE IS: " + model.getFormId());
            selectImagesModels.add(model);
            handler.deleteTabsImagesIncidenceOnIndidenceId(String.valueOf(model.getFormId()), SettingValues.getTaskId());
        }

        handler.addImagesOnTabIncidenceID(selectImagesModels);

        SettingValues.setTabList(tabsList);

        tabsList.set(Integer.parseInt(position), t);

        if (adapterTabs == null) {
            adapterTabs = SettingValues.getAdapterTabs();
        }
        adapterTabs.setItems(tabsList);
        adapterTabs.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        Log.e("code", requestCode + "");


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GPS_REQUEST) {
                isGPS = true; // flag maintain before get location
            }
        }

        if (requestCode == 1100) {
            if (resultCode == Activity.RESULT_OK) {
//                Log.e("Sizes6", "ImagesList: " + imagesList.size() + " TabsList: ");

                String position = data.getStringExtra("position");
                String json = data.getStringExtra("json");
//                Log.e("Sizes7", "ImagesList: " + imagesList.size() + " TabsList: ");


                TabsModel t = new TabsModel();
                t.setTitle(tabsList.get(Integer.parseInt(position)).getTitle());
                t.setJsonData(json);
                t.setPlus(tabsList.get(Integer.parseInt(position)).isPlus());
                t.setTab_id(tabsList.get(Integer.parseInt(position)).getTab_id());
                t.setSelectedImages(sectionImagesList);

//                Log.e("Sizes8", "ImagesList: " + imagesList.size() + " TabsList: ");

                tabsList.set(Integer.parseInt(position), t);
//                Log.e("Sizes9", "ImagesList: " + imagesList.size() + " TabsList: " + tabsList.get(0).getSelectedImages().size());

//                tabsList.get(Integer.parseInt(position)).setJsonData(json);
//                tabsList.get(Integer.parseInt(position)).setSelectedImages(imagesList);
//                Log.e("Sizes10", "ImagesList: " + imagesList.size() + " TabsList: " + tabsList.get(0).getSelectedImages().size());

                adapterTabs.setItems(tabsList);
                adapterTabs.notifyDataSetChanged();
//                Log.e("Sizes11", "ImagesList: " + imagesList.size() + " TabsList: " + tabsList.get(0).getSelectedImages().size());
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }


    }


    public void submitData(String data, Dialog dialog) {
        Log.e("Images", "FInal image list size : " + imagesList.size());

        ArrayList<MultipartBody.Part> images = new ArrayList<>();

        for (int i = 0; i < imagesList.size(); i++) {
            File file1 = new File(String.valueOf(imagesList.get(i).getTempUri()));
            images.add(MultipartBody.Part.createFormData("images", file1.getName(), RequestBody.create(MediaType.parse("image/*"), imagesList.get(i).getFinalImage())));
        }

        retrofit2.Call<SubmitFormResponse> call;
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), data.toString());
        call = RetrofitClass.getInstance().getWebRequestsInstance().completeCall(tinyDB.getString(Constants.token), bodyRequest);

//        if (imagesList.size() == 0) {
//            call = RetrofitClass.getInstance().getWebRequestsInstance().formSubmitWitoutImages(tinyDB.getString(Constants.token), bodyRequest);
//            Log.e("Token", "value of token is " + tinyDB.getString(Constants.token));
//        } else {
//            call = RetrofitClass.getInstance().getWebRequestsInstance().formSubmit(tinyDB.getString(Constants.token), bodyRequest, images);
//            Log.e("Token", "value of token is " + tinyDB.getString(Constants.token));
//        }
        call.enqueue(new Callback<SubmitFormResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SubmitFormResponse> call, Response<SubmitFormResponse> response) {
                if (response.isSuccessful()) {
//                    if (response.body().getStatus() == 200) {
                    findViewById(R.id.progress).setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    handler.deleteTabsData();
                    handler.deleteAssignedIncidenceOnIndidenceId(task_id);
                    for (TabsModel model : tabsList) {
                        List<SelectImagesModel> formImages =  handler.getAllTAbImagesOnImageId(model.getTab_id(),task_id);
                        QueueModel model2 = handler.getDraftIncidenceStateOnIncidenceID(model.getTab_id(), task_id);
                        prepareData(model2, formImages);
                        handler.deleteDraftIncidenceOnIndidenceId(model.getTab_id(), task_id);
                    }
                    handler.deleteDraftIncidencesTable();
                    handler.deleteTabsImagesData();

                    Toast.makeText(TaskDetails.this, "Successfully Completed the Call!", Toast.LENGTH_SHORT).show();
                    finish();
//                    } else {

//                        Toast.makeText(TaskDetails.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
//                        findViewById(R.id.progress).setVisibility(View.GONE);
//                        btnSubmit.setVisibility(View.VISIBLE);
//                        finish();
//                    }
                } else if (response.code() == 401) {
                    if (getApplicationContext() != null)
                        new SessionTimeoutDialog(TaskDetails.this).getDialog().show();
                    dialog.cancel();
                } else {
                    findViewById(R.id.progress).setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    Toast.makeText(TaskDetails.this, "Internal Server Error !", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SubmitFormResponse> call, Throwable t) {
                Toast.makeText(TaskDetails.this, "Can't connect to server !", Toast.LENGTH_SHORT).show();
                findViewById(R.id.progress).setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
                t.printStackTrace();
            }
        });
    }

    public void submitForms(RequestBody body, List<SelectImagesModel> myImages) {
        ArrayList<MultipartBody.Part> images = new ArrayList<>();
        retrofit2.Call<SubmitFormResponse> call;

        for (int i = 0; i < myImages.size(); i++) {
            File file1 = new File(String.valueOf(myImages.get(i).getTempUri()));
            images.add(MultipartBody.Part.createFormData("images", file1.getName(), RequestBody.create(MediaType.parse("image/*"), myImages.get(i).getFinalImage())));
        }
        if (myImages.size() == 0) {
            call = RetrofitClass.getInstance().getWebRequestsInstance().formSubmitWitoutImages(tinyDB.getString(Constants.token), body);
            Log.e("Token", "value of token is " + tinyDB.getString(Constants.token));
        } else {
            call = RetrofitClass.getInstance().getWebRequestsInstance().formSubmit(tinyDB.getString(Constants.token), body, images);
            Log.e("Token", "value of token is " + tinyDB.getString(Constants.token));
        }
        call.enqueue(new Callback<SubmitFormResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SubmitFormResponse> call, Response<SubmitFormResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {

                        for (SelectImagesModel selectImagesModel:myImages){
                            File dir = getFilesDir();
                            File file = new File("/storage/emulated/0/MyFolder/Images", selectImagesModel.getName());
                            boolean deleted = file.delete();
                            Log.d("file", "onResponse: "+deleted);
                        }
//                        Toast.makeText(TaskDetails.this, "Successfully Incidence saved to Server !", Toast.LENGTH_SHORT).show();
//                        finish();
//                        handler.deleteAssignedIncidenceOnIndidenceId(task_id);
                    } else {
//                        Toast.makeText(TaskDetails.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SubmitFormResponse> call, Throwable t) {
                Toast.makeText(TaskDetails.this, "Can't connect to server !", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(TaskDetails.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(TaskDetails.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(TaskDetails.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Show Location Access Dialog */


    @Override
    protected void onResume() {
        super.onResume();
        if (!isConnected(this))
            ltNoNetwork.setVisibility(View.VISIBLE);
        else
            ltNoNetwork.setVisibility(View.GONE);
//        SettingValues.getBarcodelist().clear();
//        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS
    }


}