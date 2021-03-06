package com.aap.medicore.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aap.medicore.Adapters.AdapterSelectImages;
import com.aap.medicore.Adapters.EqipmentCheckListSpinnerArrayAdapter;
import com.aap.medicore.Adapters.SpinnerArrayAdapter;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.AssignedIncidencesModel;
import com.aap.medicore.Models.CheckListForms;
import com.aap.medicore.Models.ChecklistForm;
import com.aap.medicore.Models.Field;
import com.aap.medicore.Models.Form;
import com.aap.medicore.Models.Option;
import com.aap.medicore.Models.QueueModel;
import com.aap.medicore.Models.SaveField;
import com.aap.medicore.Models.SaveForm;
import com.aap.medicore.Models.SelectImagesModel;
import com.aap.medicore.Models.TaskList;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomEditText;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.SettingValues;
import com.aap.medicore.Utils.TinyDB;
import com.bumptech.glide.Glide;
import com.fxn.pix.Pix;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.raed.drawingview.BrushView;
import com.raed.drawingview.DrawingView;
import com.raed.drawingview.brushes.BrushSettings;
import com.raed.drawingview.brushes.Brushes;
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Callback;
import retrofit2.Response;

import static com.aap.medicore.Activities.Home.wayLatitude;
import static com.aap.medicore.Activities.Home.wayLongitude;
import static com.aap.medicore.Adapters.AdapterTasksList.img_bm;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class FormSection extends BaseActivity implements Serializable{
    ImageView ivBack;
    TinyDB tinyDB;
    CustomButton btnSave, saveDrawingBtn;
    Context context = FormSection.this;
    LinearLayout barcodeviews;
    DatabaseHandler handler;
    String barcode = "true";
    Boolean isSignatureSaved = false;
    HorizontalScrollView hsvLayout;
    LinearLayout llViews, llViews1;
    File photo = null;
    ChecklistForm objj;
    List<View> allViewInstance = new ArrayList<View>();
    LinearLayoutManager imagesManager;
    CustomTextView subtitle;
    ArrayList<SelectImagesModel> myImages;
    ImageView ivSelectImages;
    RecyclerView rvSelectImages;
    AdapterSelectImages adapterSelectImages;
    NestedScrollView nonDrawView;
    LinearLayout drawingLayout;
    Form obj;
    Bitmap img;
    CustomButton clear;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String p1 = android.Manifest.permission.CAMERA, p2 = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static GoogleApiClient mGoogleApiClient;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";
    private Location mLocation;
    LinearLayout barcodelayout;
    private LocationRequest mLocationRequest;
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private double lat = 0.0, lng = 0.0;
    String task_id = "", position;
    public static CustomTextView barcode_text;
    private final int REQUEST_GALLERY_PERMISSION = 5568;
    private DrawingView drawingView;
    Boolean isDrawOverImageForm = false;
    String formId = "", formTitle = "";
    CustomTextView notes;
    CustomTextView heading;
    public static String barcode_value = null;
    Bitmap signBitmap;
    boolean isMandatoryFilled = true;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_section);
        getSupportActionBar().hide();
        if (getIntent() != null) {
            task_id = getIntent().getStringExtra(Constants.task_id);
            Log.d("ID", "task id is: " + task_id);
            position = getIntent().getStringExtra("position");
            formId = getIntent().getStringExtra("tab_id");
            formTitle = getIntent().getStringExtra("title");
            barcodeviews = (LinearLayout)findViewById(R.id.barcodeviews);
            SettingValues.setFormId(formId);
        }

        init();
        clickListeners();

        if(formTitle.contains("vital sign form")){
//            hitEquipmentCheckList();
            getFormDetail();

        }
        else {
            getFormDetail();
        }
    }


    public void hitEquipmentCheckList() {
        retrofit2.Call<CheckListForms> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().getEquipmentChecklist(tinyDB.getString(Constants.token),formId);

        call.enqueue(new Callback<CheckListForms>() {
            @Override
            public void onResponse(retrofit2.Call<CheckListForms> call, final Response<CheckListForms> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        objj = response.body().getChecklistForm();
                        Log.e("equip response","Equp responseeeee"+response.body());
                        getFormDetail(response);

                    } else if (response.body().getStatus() == 404) {
                        Log.e("equip response","fail Equp responseeeee"+response.body());
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CheckListForms> call, Throwable t) {
                Log.e("equip response","fail Equp responseeeee"+t);
                t.printStackTrace();
            }
        });
    }

    private void clickListeners() {

        barcodelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FormSection.this, ScanActivity.class);
                i.putExtra("formid",formId);
//                Toast.makeText(context, ""+formId, Toast.LENGTH_SHORT).show();
                startActivity(i);
            }

        });
        if(SettingValues.getBarcodeVal().isEmpty()){
            barcode_text.setText("Scan Barcode");
        }
        else {
            barcode_text.setText(SettingValues.getBarcodeVal());
        }

//        Toast.makeText(context, ""+barcode_value, Toast.LENGTH_SHORT).show();
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });

        ivSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    permissionAccess();

                } else {
                    permissionAccess();
                }
            }
        });
    }

    private void init() {
        ivBack = findViewById(R.id.ivBack);
        tinyDB = new TinyDB(FormSection.this);
        handler = new DatabaseHandler(FormSection.this);
        nonDrawView = findViewById(R.id.nonDrawView);
        drawingLayout = findViewById(R.id.drawing_layout);
        hsvLayout = findViewById(R.id.hsv);
        barcodelayout = (LinearLayout) findViewById(R.id.barcodelayout);
        heading = findViewById(R.id.heading);
        subtitle = (CustomTextView) findViewById(R.id.tvTaskLocation);
        barcode_text = (CustomTextView) findViewById(R.id.barcode_text);
//        subtitle.setText(obj.getSub_title());
        clear = (CustomButton) findViewById(R.id.clear);
        if (formTitle != null) {
            if (!formTitle.isEmpty()) {
                heading.setText(formTitle);
            }
        }

        if (barcode.equals("true")) {
        } else {
            barcodelayout.setVisibility(View.GONE);
        }
        ivSelectImages = findViewById(R.id.ivSelectImages);
        llViews = findViewById(R.id.llViews);
        llViews1 = findViewById(R.id.llViews1);
        btnSave = findViewById(R.id.btnSave);
        saveDrawingBtn = findViewById(R.id.btnSave1);
        imagesManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        myImages = new ArrayList<>();
//        adapterSelectImages = new AdapterSelectImages(imagesList, context, ivSelectImages);
        rvSelectImages = findViewById(R.id.rvImages);
//        rvSelectImages.setLayoutManager(imagesManager);
//        rvSelectImages.setAdapter(adapterSelectImages);
        TaskDetails.sectionImagesList = new ArrayList<>();
        drawingView = findViewById(R.id.drawing_view);

//        KeyboardVisibilityEvent.setEventListener(

//                FormSection.this,
//                new KeyboardVisibilityEventListener() {
//                    @Override
//                    public void onVisibilityChanged(boolean isOpen) {
//                        // some code depending on keyboard visiblity status
//                        Log.e("board", isOpen + "");
//
//                        if (isOpen) {
//                            findViewById(R.id.llViews).animate().translationY(-200);
//                        } else {
//                            findViewById(R.id.llViews).animate().translationY(0);
//                        }
//
//                    }
//                });
    }



    public void vitaldata(View v, final ChecklistForm response) {
        try {

            JSONArray jsonArray = new JSONArray();
            JSONObject parentObj = new JSONObject();
//          parentObj.put("patient_id", "" + response.body().getForm().getFormId());
            parentObj.put("related_data_id", task_id);

            for (int noOfViews = 0; noOfViews < response.getFields().size(); noOfViews++) {
                JSONObject fieldsObj = new JSONObject();


                if(noOfViews == 0){
                    fieldsObj.put("equip_barcode", SettingValues.getBarcodeVal());
                    SettingValues.setBarcodeVal("");
                }

                if(noOfViews==0) {
                    JSONArray barcodelist = new JSONArray();
                    for (int i = 0; i < SettingValues.getBarcodelist().size(); i++) {

                        JSONObject barcodeobj = new JSONObject();

                        barcodelist.put("" + SettingValues.getBarcodelist().get(i));

                    }
                    fieldsObj.put("equip_barcode" +
                            "", "" + barcodelist);
//                    jsonArray.put(fieldsObj);
                }


                if (response.getFields().get(noOfViews).getType().equals("select")) {
                    Spinner spinner = (Spinner) allViewInstance.get(noOfViews);

                    String selected_name = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getLabel();
                    String selected_id = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getId() + "";
                    //Log.e("Selected_field_ID", response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getFieldId() + "");
                    Log.e("value", selected_name + "");
                    Log.e("field_id", selected_id + "");
                    fieldsObj.put("field_id", "" + response.getFields().get(noOfViews).getFieldId());
                    fieldsObj.put("type", "select");
                    fieldsObj.put("value", "");

                    JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                    JSONObject optionsObj = new JSONObject();
                    optionsObj.put("option_id", "" + selected_id);
                    optionsObj.put("value", "" + selected_name);
                    optionsArray.put(optionsObj);
//                    }
                    fieldsObj.put("option", optionsArray);
                    jsonArray.put(fieldsObj);
                }

                if (response.getFields().get(noOfViews).getType().equals("radio-group")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        try {
                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

                            Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");

                            String a[] = selectedRadioBtn.getTag().toString().split(",");
                            fieldsObj.put("field_id",
                                    "" + a[0]);
                            fieldsObj.put("value", "");
                            Log.e("f", a[0]);
                            Log.e("s", a[1]);
                            Log.e("s", a[2]);

                            JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                            JSONObject optionsObj = new JSONObject();
                            optionsObj.put("option_id", "" + a[1]);
                            fieldsObj.put("type", "radio-group");
                            optionsObj.put("value", "" + a[2]);
                            optionsArray.put(optionsObj);
//                    }
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        try {

                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

                            Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");

                            String a[] = selectedRadioBtn.getTag().toString().split(",");
                            fieldsObj.put("field_id",
                                    "" + a[0]);
                            fieldsObj.put("type", "radio-group");
                            fieldsObj.put("value", "");
                            Log.e("f", a[0]);
                            Log.e("s", a[1]);
                            Log.e("s", a[2]);

                            JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                            JSONObject optionsObj = new JSONObject();
                            optionsObj.put("option_id", "" + a[1]);
                            optionsObj.put("value", "" + a[2]);
                            optionsArray.put(optionsObj);
//                    }
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
//                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getId());

                            Log.e("rg_id", radioGroup.getTag().toString() + "");

                            fieldsObj.put("field_id",
                                    radioGroup.getTag().toString());
                            fieldsObj.put("type", "radio-group");
                            fieldsObj.put("value", "");
//                            Log.e("f", a[0]);
//                            Log.e("s", a[1]);
//                            Log.e("s", a[2]);

                            JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                            JSONObject optionsObj = new JSONObject();
                            optionsObj.put("option_id", "");
                            optionsObj.put("value", "");
                            optionsArray.put(optionsObj);
//                        }
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);
//
//                            return;
                        }
                    }
                }
                if (response.getFields().get(noOfViews).getType().equals("checkbox-group")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        Boolean empty = true;
                        String field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "checkbox-group");
                        fieldsObj.put("value", "");
                        LinearLayout ll = (LinearLayout) allViewInstance.get(noOfViews);
                        JSONArray optionsArray = new JSONArray();
                        for (int i = 0; i < ll.getChildCount(); i++) {
                            CheckBox tempChkBox = (CheckBox) ll.getChildAt(i);
                            if (tempChkBox.isChecked()) {
                                empty = false;
                                String a[] = tempChkBox.getTag().toString().split(",");

                                Log.e("f", a[0]);
                                Log.e("s", a[1]);

                                JSONObject optionsObj = new JSONObject();
                                optionsObj.put("option_id", "" + a[0]);
                                optionsObj.put("value", "" + a[1]);
                                optionsArray.put(optionsObj);
                                Log.e("Selected checkbox", tempChkBox.getTag().toString() + "");
                            }
                        }
                        if (empty) {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);
                        }
                    } else {
                        String field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "checkbox-group");
                        fieldsObj.put("value", "");
                        LinearLayout ll = (LinearLayout) allViewInstance.get(noOfViews);
                        JSONArray optionsArray = new JSONArray();
                        for (int i = 0; i < ll.getChildCount(); i++) {
                            CheckBox tempChkBox = (CheckBox) ll.getChildAt(i);
                            if (tempChkBox.isChecked()) {
                                String a[] = tempChkBox.getTag().toString().split(",");

                                Log.e("f", a[0]);
                                Log.e("s", a[1]);

                                JSONObject optionsObj = new JSONObject();
                                optionsObj.put("option_id", "" + a[0]);
                                optionsObj.put("value", "" + a[1]);
                                optionsArray.put(optionsObj);
                                Log.e("Selected checkbox", tempChkBox.getTag().toString() + "");
                            }
                        }

                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);
                    }

                }
                if (response.getFields().get(noOfViews).getType().equals("text") && !(response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("Time")) && !(response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("signature"))) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "text");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "text");
                        fieldsObj.put("value", text);

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }
                }
                if (response.getFields().get(noOfViews).getType().equals("number")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "number");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "number");
                        fieldsObj.put("value", text);

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }

                }
                if (response.getFields().get(noOfViews).getType().equals("textarea")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;
                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);

                        text = textView.getText().toString();
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "textarea");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");

                        Log.e("textarea", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;
                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);

                        text = textView.getText().toString();
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";


                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "textarea");
                        fieldsObj.put("value", text);


                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");

                        Log.e("textarea", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }

                }
                if (response.getFields().get(noOfViews).getType().equals("text") && (response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("Time"))) {
                    //Time
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "time");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();


                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "time");
                        fieldsObj.put("value", text);


                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }

                }
                if (response.getFields().get(noOfViews).getType().equals("date")) {
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("True")) {
                        //Date
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "date");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        //Date
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "date");
                        fieldsObj.put("value", text);

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }


                }
                if (response.getFields().get(noOfViews).getType().equals("header")) {
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getTag().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "header");
                            fieldsObj.put("value", text);
                        } else {
                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getTag().toString();


                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "header");
                        fieldsObj.put("value", text);


                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }
                }
                if (response.getFields().get(noOfViews).getType().equals("text") && (response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("signature"))) {
                    //Time
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        try {
                            String field_id, text;

                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";
                            text = textView.getTag().toString();

                            Bitmap bm = BitmapFactory.decodeFile(text);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();

                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


                            if (!text.equalsIgnoreCase("")) {
                                fieldsObj.put("field_id", field_id);
                                fieldsObj.put("type", "signature");
                                fieldsObj.put("value", encodedImage);
                            } else {
                                Toast.makeText(this, "Signature is Mandatory. Please take signature", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                            Log.d("myImages", "ID: " + field_id + " Data: " + encodedImage);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Signature is Mandatory. Please take signature", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } else {
                        try {


                            String field_id, text;

                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";
                            text = textView.getTag().toString();

                            Bitmap bm = BitmapFactory.decodeFile(text);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();

                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "signature");
                            fieldsObj.put("value", encodedImage);


                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                            Log.d("myImages", "ID: " + field_id + " Data: " + encodedImage);
                        } catch (NullPointerException e) {
                            e.printStackTrace();

                            String field_id;
                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";

                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "signature");
                            fieldsObj.put("value", "");

                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                        }
                    }
                }
            }

            JSONObject allFields = new JSONObject();
            allFields.put("fields", jsonArray);

            JSONObject formFields = new JSONObject();
            formFields.put("fields", jsonArray);


            JSONArray imagesArray = new JSONArray();
            JSONArray imagesPathArray = new JSONArray();
            for (int i = 0; i < myImages.size(); i++) {
                imagesArray.put(myImages.get(i).getName());
                imagesPathArray.put(myImages.get(i).getTempUri());
                Log.d("URI", "images uri is: " + myImages.get(i).getTempUri());
            }


            allFields.put("images", imagesArray);
            formFields.put("images", imagesPathArray);

            JSONObject obj = new JSONObject();
            obj.put("checklist_type", "equipment_checklist");
            obj.put("form", allFields);
            obj.put("lat", wayLatitude);
            obj.put("lng", wayLongitude);


            allFields.put("images", imagesArray);
            formFields.put("images", imagesPathArray);


            parentObj.put("form-section", allFields);
//            mainObj.put("barcode","1234")

            QueueModel model = new QueueModel();
            model.setId(formId + "");
            model.setJson(formFields + "");
            model.setState(Constants.StateAdded);
            model.setTitle("");
            model.setMessage("");
            model.setTaskID(task_id);
            handler.deleteDraftIncidenceOnIndidenceId(formId, task_id);
            handler.addDraftIncidenceOnIncidenceID(model);
            Log.e("taskandformid","VTASK_ID IS THISSSS"+task_id+" "+"FORM ID IS THISSSS"+formId);
            Log.d("optionsObj", parentObj + "");

            TaskDetails.sectionImagesList.addAll(myImages);
            Log.d("optionsObj", "image list size is:" + TaskDetails.sectionImagesList.size());

            if(isMandatoryFilled){
                Toast.makeText(context, "Form saved successfully.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
            }


            TaskDetails td = new TaskDetails();
            td.resetView(position, parentObj.toString(), FormSection.this, isMandatoryFilled);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public static final TypeAdapter<Number> UNRELIABLE_INTEGER = new TypeAdapter<Number>() {
        @Override
        public Number read(JsonReader in) throws IOException {
            JsonToken jsonToken = in.peek();
            switch (jsonToken) {
                case NUMBER:
                case STRING:
                    String s = in.nextString();
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException ignored) {
                    }
                    try {
                        return (int) Double.parseDouble(s);
                    } catch (NumberFormatException ignored) {
                    }
                    return null;
                case NULL:
                    in.nextNull();
                    return null;
                case BOOLEAN:
                    in.nextBoolean();
                    return null;
                default:
                    throw new JsonSyntaxException("Expecting number, got: " + jsonToken);
            }
        }

        @Override
        public void write(JsonWriter out, Number value) throws IOException {
            out.value(value);
        }
    };
    public static final TypeAdapterFactory UNRELIABLE_INTEGER_FACTORY = TypeAdapters.newFactory(int.class, Integer.class, UNRELIABLE_INTEGER);


    @SuppressLint("NewApi")
    public void getFormDetail(final Response<CheckListForms> response) {

        final ChecklistForm obj = response.body().getChecklistForm();
        subtitle.setText(obj.getSubTitle());
        if(obj.getBarcodeEnabled().equals("yes")){
            barcodelayout.setVisibility(View.VISIBLE);
        }

        Log.e("size", obj.toString());
        for (int position = 0; position < obj.getFields().size(); position++) {

            if (obj.getFields().get(position).getType().equalsIgnoreCase("file")) {

                findViewById(R.id.hsv).setVisibility(View.VISIBLE);


                allViewInstance.add(hsvLayout);
                Log.d("SIZE", "Image size is: " + myImages.size());
                if (myImages.size() > 0) {
                    if (adapterSelectImages != null) {
                        adapterSelectImages.setItems(myImages);
                        adapterSelectImages.notifyDataSetChanged();
                    } else {
                        adapterSelectImages = new AdapterSelectImages(myImages, FormSection.this, ivSelectImages);
                        rvSelectImages.setLayoutManager(imagesManager);
                        rvSelectImages.setAdapter(adapterSelectImages);
                    }
                }
            }


            if (obj.getFields().get(position).getType().equalsIgnoreCase("text") && !(obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("Time")) && !(obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("signature"))) {
                RelativeLayout ettxt_start = new RelativeLayout(this);
                CustomEditText etText = new CustomEditText(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 4, 4);
                etText.setLayoutParams(perams);
                etText.setTextSize(12);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getFields().get(position).getPlaceholder());
                LinearLayout.LayoutParams etparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                etparams.setMargins(0, 0, 0, 0);
                etText.setPadding(20, 0, 20, 0);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(60);
                etText.setMinimumHeight(60);
                etText.setSingleLine();
                etText.setLayoutParams(perams);


                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10,0,0,4);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);


                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(10, 10, 4, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                ettxt_start.addView(etText);
                ettxt_start.addView(star , layoutParams);
                if(obj.getFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    ettxt_start.setBackground(getDrawable(R.drawable.required));
                }

                llViews.addView(ettxt_start);

                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

            } else if (obj.getFields().get(position).getType().equalsIgnoreCase("number")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                CustomEditText etText = new CustomEditText(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 4, 4);
                relativeLayout.setLayoutParams(perams);
                LinearLayout.LayoutParams etparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                etparams.setMargins(0, 0, 0,0 );
                etText.setLayoutParams(etparams);


                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10,0,0,0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);

                etText.setTextSize(12);
                etText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getFields().get(position).getPlaceholder());

                etText.setPadding(20, 0, 20, 0);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(60);
                etText.setMinimumHeight(60);
                etText.setSingleLine();

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(10, 10, 4, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    relativeLayout.setBackground(getDrawable(R.drawable.required));
                }

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etText);
                relativeLayout.addView(star , layoutParams);

                llViews.addView(relativeLayout);


                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 24, 0, 24);
                view.setLayoutParams(p);

                llViews.addView(view);

            } else if (obj.getFields().get(position).getType().equals("checkbox-group")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                CustomTextView tvCheckBoxTitle = new CustomTextView(context);
                tvCheckBoxTitle.setText(obj.getFields().get(position).getLabel());
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(20, 20, 20, 20);
                relativeLayout.setLayoutParams(perams);

                tvCheckBoxTitle.setLayoutParams(perams);
                tvCheckBoxTitle.setTextColor(Color.parseColor("#616060"));
                tvCheckBoxTitle.setTextSize(14);

                tvCheckBoxTitle.setText(obj.getFields().get(position).getLabel());
//                llViews.addView(tvCheckBoxTitle);
                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    tvCheckBoxTitle.setTextColor(Color.RED);

                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(tvCheckBoxTitle);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);

                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.VERTICAL);

                for (int i = 0; i < obj.getFields().get(position).getOptions().size(); i++) {
                    AppCompatCheckBox checkBox = new AppCompatCheckBox(context);
                    LinearLayout.LayoutParams cbPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                    cbPerams.setMargins(8, 8, 8, 8);
                    checkBox.setText(obj.getFields().get(position).getOptions().get(i).getLabel());
                    checkBox.setLayoutParams(cbPerams);
                    Typeface font = Typeface.createFromAsset(getAssets(), "ProductSans-Medium.ttf");
                    checkBox.setTypeface(font);
                    checkBox.setTextColor(Color.parseColor("#616060"));
                    checkBox.setTextSize(14);

                    checkBox.setTag(obj.getFields().get(position).getOptions().get(i).getId() + "," + obj.getFields().get(position).getOptions().get(i).getLabel());

                    ll.addView(checkBox);
                }
                llViews.addView(ll);

                allViewInstance.add(ll);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

            } else if (obj.getFields().get(position).getType().equals("radio-group")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                CustomTextView tvRadioButtonTitle = new CustomTextView(context);
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(12);

                perams.setMargins(20, 20, 20, 20);
                relativeLayout.setLayoutParams(perams);
                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(14);
                tvRadioButtonTitle.setTypeface(tvRadioButtonTitle.getTypeface(), Typeface.NORMAL);
                tvRadioButtonTitle.setTextColor(Color.parseColor("#616060"));
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
//                llViews.addView(tvRadioButtonTitle);

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);

                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    tvRadioButtonTitle.setTextColor(Color.RED);

                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(tvRadioButtonTitle);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);


                RadioGroup rg = new RadioGroup(context);
                rg.setTag(obj.getFields().get(position).getFieldId() + "");

                for (int i = 0; i < obj.getFields().get(position).getOptions().size(); i++) {
                    RadioButton rb = new RadioButton(context);
                    rb.setText(obj.getFields().get(position).getOptions().get(i).getLabel());
                    Typeface font = Typeface.createFromAsset(getAssets(), "ProductSans-Medium.ttf");
                    rb.setTypeface(font);
                    rb.setTextSize(14);
                    rb.setTextColor(Color.parseColor("#616060"));
                    rg.addView(rb);

                    rb.setTag(obj.getFields().get(position).getFieldId() + "," + obj.getFields().get(position).getOptions().get(i).getId() + "," + obj.getFields().get(position).getOptions().get(i).getLabel());
                }

                rg.setLayoutParams(perams);

                llViews.addView(rg);
                allViewInstance.add(rg);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

            } else if (obj.getFields().get(position).getType().equals("textarea")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                CustomEditText etTextArea = new CustomEditText(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

//                perams.setMargins(4, 0, 8, 0);
                perams.setMargins(4, 4, 4, 4);
                relativeLayout.setLayoutParams(perams);
                etTextArea.setPadding(20, 10, 20, 10);
                etTextArea.setHint(obj.getFields().get(position).getPlaceholder());


                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10,0,0,0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);

                etTextArea.setGravity(Gravity.TOP);
                etTextArea.setTextColor(getResources().getColor(R.color.colorPrimary));
                etTextArea.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etTextArea.setMinHeight(200);
                etTextArea.setMinimumHeight(200);
                etTextArea.setTextSize(12);
//                etTextArea.setLayoutParams(perams);
//                llViews.addView(etTextArea);

                LinearLayout.LayoutParams prams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,60); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                prams.setMargins(0, 0, 0, 0);
                etTextArea.setLayoutParams(prams);

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(4, 10, 4, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    etTextArea.setBackground(getDrawable(R.drawable.required));
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etTextArea);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);


                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);
                allViewInstance.add(etTextArea);

                llViews.addView(view);

            } else if (obj.getFields().get(position).getType().equals("select")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                CustomTextView tvRadioButtonTitle = new CustomTextView(context);
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 0, 0);
                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(12);

                perams.setMargins(20, 20, 20, 20);
                relativeLayout.setLayoutParams(perams);
                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(14);

                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
//                llViews.addView(tvRadioButtonTitle);
                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    tvRadioButtonTitle.setTextColor(Color.RED);
                }

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(tvRadioButtonTitle);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);

                AppCompatSpinner spinner = new AppCompatSpinner(context);
                EqipmentCheckListSpinnerArrayAdapter adapter = new EqipmentCheckListSpinnerArrayAdapter(context,
                        R.layout.custom_spinner_item, obj.getFields().get(position).getOptions());
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                llViews.addView(spinner);

                allViewInstance.add(spinner);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);


            } else if (obj.getFields().get(position).getType().equals("text") && (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("Time"))) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                final CustomTextView etText = new CustomTextView(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 4, 4);
                etText.setLayoutParams(perams);
                etText.setTextSize(12);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getFields().get(position).getPlaceholder());


                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10,0,0,0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);


                LinearLayout.LayoutParams prams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                prams.setMargins(0, 0, 0, 0);

                etText.setPadding(20, 20, 20, 20);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(60);
                etText.setMinimumHeight(60);
                etText.setGravity(Gravity.CENTER_VERTICAL);
                etText.setSingleLine();
                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(20, 10, 20, 4);
                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    relativeLayout.setBackground(getDrawable(R.drawable.required));
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etText);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);

                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

//                llViews.addView(etText);

//                allViewInstance.add(etText);
//
//                View view = new View(context);
//                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
//                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                p.setMargins(0, 24, 0, 24);
//                view.setLayoutParams(p);
//
//                llViews.addView(view);

                etText.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                                if(selectedHour<10&&selectedMinute<10){
                                    etText.setText("  " + "0"+selectedHour + " : " + "0"+selectedMinute);
                                }
                                else if(selectedHour<10)

                                {
                                    etText.setText("  " + "0"+selectedHour + " : " +selectedMinute);
                                }

                                else if(selectedMinute<10)

                                {
                                    etText.setText("  " + selectedHour + " : " +"0"+selectedMinute);
                                }

                                else {
                                    etText.setText("  " + selectedHour + " : "+selectedMinute);
                                }
                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();

                    }
                });
            } else if (obj.getFields().get(position).getType().equals("date")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                final String lable = obj.getFields().get(position).getLabel();
                final CustomTextView etText = new CustomTextView(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 4, 4);
                relativeLayout.setLayoutParams(perams);
                etText.setTextSize(12);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getFields().get(position).getPlaceholder());



                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10,0,0,0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);

                LinearLayout.LayoutParams prams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                prams.setMargins(0, 0, 0, 0);
                etText.setLayoutParams(prams);
                etText.setPadding(20, 0, 20, 0);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(60);
                etText.setMinimumHeight(60);
                etText.setGravity(Gravity.CENTER_VERTICAL);
                etText.setSingleLine();

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(4, 10, 4, 4);
                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    relativeLayout.setBackground(getDrawable(R.drawable.required));
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30 ); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etText);
                relativeLayout.addView(star , layoutParams);
                llViews.addView(relativeLayout);
                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

//                llViews.addView(etText);
//
//                allViewInstance.add(etText);
//
//                View view = new View(context);
//                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
//                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                p.setMargins(0, 24, 0, 24);
//                view.setLayoutParams(p);
//
//                llViews.addView(view);


                final Calendar myCalendar = Calendar.getInstance();

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = " MM - dd - yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        etText.setText(" " + sdf.format(myCalendar.getTime()));
                    }

                };

                etText.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        new DatePickerDialog(context, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
            } else if (obj.getFields().get(position).getType().equals("header")) {
                final CustomTextView etText = new CustomTextView(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(8, 8, 8, 8);

                etText.setLayoutParams(perams);
                if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h1")) {
                    etText.setTextSize(20);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h2")) {
                    etText.setTextSize(18);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h3")) {
                    etText.setTextSize(16);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h4")) {
                    etText.setTextSize(14);
                }
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setText(obj.getFields().get(position).getLabel());

                etText.setPadding(28, 28, 28, 28);
                etText.setMinHeight(10);
                etText.setMinimumHeight(60);
                etText.setGravity(Gravity.CENTER);
                etText.setSingleLine();

                etText.setTag(obj.getFields().get(position).getPlaceholder());
                Log.e("Tag", obj.getFields().get(position).getPlaceholder());

                llViews.addView(etText);

                // TEXTVIEW


                allViewInstance.add(etText);


            } else if (obj.getFields().get(position).getType().equals("file")) {
//                NestedScrollView nestedScrollView = new NestedScrollView(TaskDetails.this);
//                LinearLayout.LayoutParams nsvPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                nsvPerams.setMargins(8, 8, 8, 8);
//
//                nestedScrollView.setLayoutParams(nsvPerams);
//
//                LinearLayout rl = new LinearLayout(TaskDetails.this);
//                LinearLayout.LayoutParams rlPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                rlPerams.setMargins(12, 0, 12, 0);
//                rl.setLayoutParams(nsvPerams);
//                rl.setOrientation(LinearLayout.HORIZONTAL);
//
//                ImageView iv = new ImageView(TaskDetails.this);
//                LinearLayout.LayoutParams ivPerams = new LinearLayout.LayoutParams(140, 140);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                ivPerams.setMargins(12, 20, 20, 20);
//                ivPerams.gravity = Gravity.CENTER_VERTICAL;
//
//                iv.setLayoutParams(ivPerams);
//                iv.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_pictures));
//
//                iv.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//
//                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                            return;
//                        }
//                        mLastClickTime = SystemClock.elapsedRealtime();
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                            permissionAccess();
//
//                        } else {
//                            Pix.start(TaskDetails.this,                    //Activity or Fragment Instance
//                                    REQUEST_GALLERY_PERMISSION,                //Request code for activity results
//                                    30 - imagesList.size());
//                        }
//                    }
//                });
//
//                rvSelectImages = new RecyclerView(TaskDetails.this);
//                LinearLayout.LayoutParams rvPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                rvPerams.setMargins(20, 20, 20, 20);
//                rvSelectImages.setLayoutParams(rvPerams);
//                rvSelectImages.setNestedScrollingEnabled(false);
//
//                rvSelectImages.setLayoutManager(imagesManager);
//                rvSelectImages.setAdapter(adapterSelectImages);
//
//                rl.addView(iv);
//                rl.addView(rvSelectImages);
//
//                nestedScrollView.addView(rl);
//
//                llViews.addView(nestedScrollView);

            } else if (obj.getFields().get(position).getType().equals("text") && obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("signature")) {
                LinearLayout ll = new LinearLayout(context);
                LinearLayout.LayoutParams llPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                llPerams.setMargins(8, 8, 8, 8);
                ll.setLayoutParams(llPerams);
                ll.setOrientation(LinearLayout.VERTICAL);
                final CustomTextView etText = new CustomTextView(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(16, 16, 16, 16);

                etText.setText(obj.getFields().get(position).getPlaceholder());
                etText.setTextSize(16);
                etText.setLayoutParams(perams);


//                ll.addView(etText);
                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);
                ImageView star =  new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if(obj.getFields().get(position).getRequired().equalsIgnoreCase("true")){
//                    star.setVisibility(View.VISIBLE);
                    etText.setTextColor(Color.RED);
                }

                ll.addView(etText);
                ll.addView(star);

                final ImageView iv = new ImageView(context);
                LinearLayout.LayoutParams ivPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                iv.setLayoutParams(ivPerams);
                ll.addView(iv);
                iv.setVisibility(View.GONE);
                etText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        getSignatures(etText, iv);
                    }
                });


//                iv.setVisibility(View.GONE);

                llViews.addView(ll);
                allViewInstance.add(etText);
            }

        }

    }





    public void getFormDetail() {

        AssignedIncidencesModel model = new AssignedIncidencesModel();
        model = handler.getIncidenceOnId(task_id);
        Gson gson = new Gson();
        String json = model.getJson();
        final TaskList taskList = gson.fromJson(json, TaskList.class);
        int pos = Integer.parseInt(position);
        Log.d("POS", "POSITION IS: " + pos);
        if(pos < taskList.getForm().size()){
            obj = taskList.getForm().get(Integer.parseInt(position));

        }else{
            Toast.makeText(context, "Form has been updated!", Toast.LENGTH_SHORT).show();
            Log.e("taskk","taskkkkk"+taskList.getForm().size());
            return;

        }

        subtitle.setText(taskList.getForm().get(Integer.parseInt(position)).getSub_title());

        if (taskList.getForm().get(Integer.parseInt(position)).getBarcode_enabled().equals("yes")) {

            barcodelayout.setVisibility(View.VISIBLE);
        } else if (taskList.getForm().get(Integer.parseInt(position)).getBarcode_enabled().equals("no")) {

        }

        QueueModel model1 = handler.getDraftIncidenceStateOnIncidenceID(formId, task_id);

        Gson gson1 = new GsonBuilder()
                .registerTypeAdapterFactory(UNRELIABLE_INTEGER_FACTORY)
                .create();
        String json1 = model1.getJson();
        Log.d("DATA", "result data is: " + json1);

        SaveForm taskList1 = gson1.fromJson(json1, SaveForm.class);

        if (obj.getTitle().equalsIgnoreCase("Draw over Image")) {
            isDrawOverImageForm = true;
            findViewById(R.id.nonDrawView).setVisibility(View.GONE);
            drawingLayout.setVisibility(View.VISIBLE);
            drawingView.setVisibility(View.VISIBLE);
            BrushSettings brushSettings = drawingView.getBrushSettings();
            brushSettings.setSelectedBrushSize((float) 0.01);
            drawingView.setUndoAndRedoEnable(true);
            findViewById(R.id.hsv).setVisibility(View.VISIBLE);

            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    drawingView.clear();

                    String drawImg = Constants.IMAGE_IP + taskList.getDrawOverImage();
                    //the size will be -> 50 + 0.5 * (100 - 50) = 75 pixel
                    new ConvertUrlToBitmap().execute(drawImg);

//                    drawingView.setBackgroundImage(img_bm);
                }
            });

            if (taskList1 != null) {
                Log.d("SIZE", "Image size is: " + taskList1.getImages().size());

                if (taskList1.getImages().size() > 0) {
                    for (int i = 0; i < taskList1.getImages().size(); i++) {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        final Bitmap bitmap = BitmapFactory.decodeFile(taskList1.getImages().get(0), bmOptions);
//                        FormSection.this.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
                        // update UI here
                        drawingView.setBackgroundImage(bitmap);
//                            }
//                        });

                    }


//                    drawingView.setBackgroundImage(img_bm);
                }else{
                    String drawImg = Constants.IMAGE_IP + taskList.getDrawOverImage();
                    //the size will be -> 50 + 0.5 * (100 - 50) = 75 pixel
                    new ConvertUrlToBitmap().execute(drawImg);

//                    drawingView.setBackgroundImage(img_bm);
                }

            } else {
                String drawImg = Constants.IMAGE_IP + taskList.getDrawOverImage();
                //the size will be -> 50 + 0.5 * (100 - 50) = 75 pixel
                new ConvertUrlToBitmap().execute(drawImg);

//                drawingView.setBackgroundImage(img_bm);
            }

        } else {
            findViewById(R.id.nonDrawView).setVisibility(View.VISIBLE);

            drawingLayout.setVisibility(View.GONE);
            isDrawOverImageForm = false;
            drawingView.setVisibility(View.GONE);
            findViewById(R.id.hsv).setVisibility(View.GONE);
        }
        Log.e("size", obj.toString());

        for (int position = 0; position < obj.getFields().size(); position++) {

            Log.d("BARCOADEEEEEE","bar coeeeeeeeeeeeee: " +  obj.getFields().get(position).getPlaceholder());
            Log.d("TYPE","FIELD TYPE IS: " + obj.getFields().get(position).getType());
//            if (obj.getFields().get(position).getType().equals("text") && obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("barcode")){
//                Toast.makeText(context, "mil gyaaaaa", Toast.LENGTH_SHORT).show();
//            } else
            if (obj.getFields().get(position).getType().equalsIgnoreCase("file")) {

                findViewById(R.id.hsv).setVisibility(View.VISIBLE);
                if (taskList1 != null) {
                    Log.d("SIZE", "Image size is: " + taskList1.getImages().size());


                    if (taskList1.getImages().size() > 0)
                        for (int i = 0; i < taskList1.getImages().size(); i++) {
                            String path = taskList1.getImages().get(i);
                            SelectImagesModel imageModel = new SelectImagesModel();
                            imageModel.setTempUri(Uri.parse(path));
                            File f = new File(path);
                            imageModel.setFinalImage(f);
                            imageModel.setName(f.getName());
                            myImages.add(imageModel);

//                            String drawImg = Constants.IMAGE_IP + taskList.getDrawOverImage();
//                            //the size will be -> 50 + 0.5 * (100 - 50) = 75 pixel
//                            new ConvertUrlToBitmap().execute(drawImg);

                        }
                }

                allViewInstance.add(hsvLayout);
                Log.d("SIZE", "Image size is: " + myImages.size());
                if (myImages.size() > 0) {
                    if (adapterSelectImages != null) {
                        adapterSelectImages.setItems(myImages);
                        adapterSelectImages.notifyDataSetChanged();
                    } else {
                        adapterSelectImages = new AdapterSelectImages(myImages, FormSection.this, ivSelectImages);
                        rvSelectImages.setLayoutManager(imagesManager);
                        rvSelectImages.setAdapter(adapterSelectImages);
                    }
                }
            }
//            else if (obj.getFields().get(position).getType().equalsIgnoreCase("text") && obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("barcode")){
//                Toast.makeText(context, "mil gyaaaaa", Toast.LENGTH_SHORT).show();
//            }
            else if (obj.getFields().get(position).getType().equalsIgnoreCase("text") && !(obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("Time")) && !(obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("signature"))) {
                RelativeLayout ettxt_start = new RelativeLayout(this);
                CustomEditText etText = new CustomEditText(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(0, 4, 0, 4);
                ettxt_start.setGravity(Gravity.CENTER);
                etText.setLayoutParams(perams);
                ettxt_start.setLayoutParams(perams);
                etText.setTextSize(14);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getFields().get(position).getPlaceholder());

                LinearLayout.LayoutParams etparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

                etparams.setMargins(0, 0, 0, 0);
                etText.setLayoutParams(etparams);
                etText.setPadding(20, 0, 20, 0);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(140);
                etText.setMinimumHeight(140);
                etText.setSingleLine();

                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10, 0, 0, 0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);


                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

                startparams.setMargins(10, 10, 4, 4);

                ImageView star = new ImageView(this);

                star.setImageDrawable(getDrawable(R.drawable.red_astrik));

                star.setLayoutParams(startparams);

                star.setVisibility(View.GONE);

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT

                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                ettxt_start.addView(etText);

                ettxt_start.addView(star, layoutParams);


                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {

//                    star.setVisibility(View.VISIBLE);
                    ettxt_start.setBackground(getDrawable(R.drawable.required));

                }

                llViews.addView(ettxt_start);

                allViewInstance.add(etText);

                View view = new View(context);

                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));

                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

                p.setMargins(0, 16, 0, 16);

                view.setLayoutParams(p);

                llViews.addView(view);

                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {

                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                etText.setText(f.getValue());
                            }
                        }
                }


            } else if (obj.getFields().get(position).getType().equalsIgnoreCase("number")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                CustomEditText etText = new CustomEditText(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 4, 4);
                etText.setLayoutParams(perams);


                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10, 0, 0, 0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);


                LinearLayout.LayoutParams etparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                etparams.setMargins(0, 4, 0, 4);

                etText.setLayoutParams(etparams);
                etText.setTextSize(12);
                etText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getFields().get(position).getPlaceholder());

                etText.setPadding(20, 20, 20, 20);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(140);
                etText.setMinimumHeight(140);
                etText.setSingleLine();

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(10, 10, 4, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);

                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
//                    star.setVisibility(View.VISIBLE);
                    relativeLayout.setBackground(getDrawable(R.drawable.required));
                }

                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {

                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                etText.setText(f.getValue());
                            }
                        }
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etText);
                relativeLayout.addView(star, layoutParams);

                llViews.addView(relativeLayout);

                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

            } else if (obj.getFields().get(position).getType().equals("checkbox-group")) {

                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(20, 20, 20, 20);
                relativeLayout.setLayoutParams(perams);

                CustomTextView tvCheckBoxTitle = new CustomTextView(context);
                tvCheckBoxTitle.setText(obj.getFields().get(position).getLabel());
                tvCheckBoxTitle.setTextSize(14);

                tvCheckBoxTitle.setTypeface(tvCheckBoxTitle.getTypeface(), Typeface.NORMAL);
                tvCheckBoxTitle.setText(obj.getFields().get(position).getLabel());
                tvCheckBoxTitle.setTextColor(Color.parseColor("#616060"));
                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
//                    star.setVisibility(View.VISIBLE);
//                    relativeLayout.setBackground(getDrawable(R.drawable.required));
                    tvCheckBoxTitle.setTextColor(Color.RED);
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(tvCheckBoxTitle);
                relativeLayout.addView(star, layoutParams);
                llViews.addView(relativeLayout);
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.VERTICAL);

                for (int i = 0; i < obj.getFields().get(position).getOptions().size(); i++) {
                    AppCompatCheckBox checkBox = new AppCompatCheckBox(context);
                    LinearLayout.LayoutParams cbPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                    cbPerams.setMargins(8, 8, 8, 8);
                    checkBox.setText(obj.getFields().get(position).getOptions().get(i).getLabel());
                    checkBox.setLayoutParams(cbPerams);
                    Typeface font = Typeface.createFromAsset(getAssets(), "ProductSans-Medium.ttf");
                    checkBox.setTypeface(font);
                    checkBox.setTextColor(Color.parseColor("#616060"));

                    if (taskList1 != null) {
                        if (taskList1.getFields().size() > 0)
                            for (SaveField f : taskList1.getFields()) {

                                for (Option o : f.getOptions()) {
                                    if (obj.getFields().get(position).getOptions().get(i).getOptionId().equals(o.getOptionId())) {
                                        Log.d("WOW", "Value matcheeeeeeeeeeeeeed");
                                        checkBox.setChecked(true);
                                    }
                                }
                            }
                    }

                    checkBox.setTextSize(13);

                    checkBox.setTag(obj.getFields().get(position).getOptions().get(i).getOptionId() + "," + obj.getFields().get(position).getOptions().get(i).getLabel());


                    ll.addView(checkBox);
                }
                llViews.addView(ll);

                allViewInstance.add(ll);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

            } else if (obj.getFields().get(position).getType().equals("radio-group")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(20, 20, 20, 20);
                relativeLayout.setLayoutParams(perams);

                CustomTextView tvRadioButtonTitle = new CustomTextView(context);
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(12);

                perams.setMargins(20, 20, 20, 20);
                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(14);
                tvRadioButtonTitle.setTypeface(tvRadioButtonTitle.getTypeface(), Typeface.NORMAL);
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
                tvRadioButtonTitle.setTypeface(tvRadioButtonTitle.getTypeface(), Typeface.NORMAL);
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
                tvRadioButtonTitle.setTextColor(Color.parseColor("#616060"));
                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);

                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
//                    star.setVisibility(View.VISIBLE);
                    tvRadioButtonTitle.setTextColor(Color.RED);
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(tvRadioButtonTitle);
                relativeLayout.addView(star, layoutParams);
                llViews.addView(relativeLayout);

                RadioGroup rg = new RadioGroup(context);
                rg.setTag(obj.getFields().get(position).getFieldId() + "");
//                rg.setMaxInRow(3);


                for (int i = 0; i < obj.getFields().get(position).getOptions().size(); i++) {
                    RadioButton rb = new RadioButton(context);
                    rb.setText(obj.getFields().get(position).getOptions().get(i).getLabel());
                    Typeface font = Typeface.createFromAsset(getAssets(), "ProductSans-Medium.ttf");
                    rb.setTypeface(font);
                    rb.setTextSize(14);
                    rb.setTextColor(Color.parseColor("#616060"));
                    rg.addView(rb);

                    if (taskList1 != null) {
                        if (taskList1.getFields().size() > 0)
                            for (SaveField f : taskList1.getFields()) {

                                for (Option o : f.getOptions()) {
                                    if (obj.getFields().get(position).getOptions().get(i).getOptionId().equals(o.getOptionId())) {
                                        Log.d("WOW", "Value matcheeeeeeeeeeeeeed");
                                        rb.setChecked(true);
                                    }
                                }
                            }
                    }


                    rb.setTag(obj.getFields().get(position).getOptions().get(i).getFieldId() + "," + obj.getFields().get(position).getOptions().get(i).getOptionId() + "," + obj.getFields().get(position).getOptions().get(i).getLabel());
                }

//                rg.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener() {
//                    @Override
//                    public void onCheckedChanged(ViewGroup group, RadioButton button) {
//                        Toast.makeText(FormSection.this,
//                                button.getText() + " was clicked",
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });

                rg.setLayoutParams(perams);

                llViews.addView(rg);
                allViewInstance.add(rg);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

            } else if (obj.getFields().get(position).getType().equals("textarea")) {

                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(0, 4, 0, 4);
                relativeLayout.setLayoutParams(perams);

                CustomEditText etTextArea = new CustomEditText(context);


                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10, 0, 0, 0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);


                etTextArea.setPadding(20, 10, 20, 10);
                etTextArea.setHint(obj.getFields().get(position).getPlaceholder());
                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {

                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                etTextArea.setText(f.getValue());
                            }
                        }
                }

                etTextArea.setGravity(Gravity.TOP);
                etTextArea.setTextColor(getResources().getColor(R.color.colorPrimary));
                etTextArea.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etTextArea.setMinHeight(60);
                etTextArea.setMinimumHeight(60);
                etTextArea.setTextSize(12);
                LinearLayout.LayoutParams prams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                prams.setMargins(7, 0, 10, 0);
                etTextArea.setLayoutParams(perams);

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(4, 10, 4, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
//                    star.setVisibility(View.VISIBLE);
                    etTextArea.setBackground(getDrawable(R.drawable.required));
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etTextArea);
                relativeLayout.addView(star, layoutParams);
                llViews.addView(relativeLayout);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);
                allViewInstance.add(etTextArea);

                llViews.addView(view);

            } else if (obj.getFields().get(position).getType().equals("select")) {

                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 4, 4, 4);
                relativeLayout.setLayoutParams(perams);

                CustomTextView tvRadioButtonTitle = new CustomTextView(context);
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());

                perams.setMargins(4, 4, 0, 0);
                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(12);
                tvRadioButtonTitle.setTextColor(Color.parseColor("#616060"));

                perams.setMargins(20, 20, 20, 20);

                tvRadioButtonTitle.setLayoutParams(perams);
                tvRadioButtonTitle.setTextSize(14);
                tvRadioButtonTitle.setTypeface(tvRadioButtonTitle.getTypeface(), Typeface.NORMAL);
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
                tvRadioButtonTitle.setTextColor(Color.parseColor("#616060"));

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(30, 30);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
//                    star.setVisibility(View.VISIBLE);
                    tvRadioButtonTitle.setTextColor(Color.parseColor("#ff0000"));
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(20, 20); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                relativeLayout.addView(tvRadioButtonTitle);
                relativeLayout.addView(star, layoutParams);
                llViews.addView(relativeLayout);

                AppCompatSpinner spinner = new AppCompatSpinner(context);
                SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(context,
                        R.layout.custom_spinner_item, obj.getFields().get(position).getOptions());
                spinner.setAdapter(adapter);

                for (int i = 0; i < obj.getFields().get(position).getOptions().size(); i++) {

                    if (taskList1 != null) {
                        if (taskList1.getFields().size() > 0)
                            for (SaveField f : taskList1.getFields()) {

                                for (Option o : f.getOptions()) {
                                    if (obj.getFields().get(position).getOptions().get(i).getOptionId().equals(o.getOptionId())) {
                                        Log.d("WOW", "Value matcheeeeeeeeeeeeeed");
                                        spinner.setSelection(i);
                                    }
                                }
                            }
                    }

                }

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                llViews.addView(spinner);

                allViewInstance.add(spinner);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);


            } else if (obj.getFields().get(position).getType().equals("text") && (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("Time"))) {


                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                perams.setMargins(0, 4, 0, 4);
                relativeLayout.setLayoutParams(perams);


                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10, 0, 0, 0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);


                final String lable = obj.getFields().get(position).getLabel();
                final CustomTextView etText = new CustomTextView(context);
                // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.

                etText.setTextSize(12);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getFields().get(position).getPlaceholder());
                LinearLayout.LayoutParams prams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                prams.setMargins(0, 0, 0, 0);
                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {

                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                etText.setText(f.getValue());
                            }
                        }
                }

                etText.setPadding(20, 20, 20, 20);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(60);
                etText.setMinimumHeight(60);
                etText.setLayoutParams(prams);
                etText.setGravity(Gravity.CENTER_VERTICAL);
                etText.setSingleLine();


                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(30, 30);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(4, 10, 4, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
//                    star.setVisibility(View.VISIBLE);
                    relativeLayout.setBackground(getDrawable(R.drawable.required));
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etText);
                relativeLayout.addView(star, layoutParams);
                llViews.addView(relativeLayout);

                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);

                etText.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        Calendar mcurrentTime = Calendar.getInstance();
                        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                        int minute = mcurrentTime.get(Calendar.MINUTE);
                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                                etText.setText("  " + selectedHour + " : " + selectedMinute);

                                if(selectedHour<10&&selectedMinute<10){
                                    etText.setText("  " + "0"+selectedHour + " : " + "0"+selectedMinute);
                                }
                                else if(selectedHour<10)

                                {
                                    etText.setText("  " + "0"+selectedHour + " : " +selectedMinute);
                                }

                                else if(selectedMinute<10)

                                {
                                    etText.setText("  " + selectedHour + " : " +"0"+selectedMinute);
                                }

                                else {
                                    etText.setText("  " + selectedHour + " : "+selectedMinute);
                                }


                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();

                    }
                });
            } else if (obj.getFields().get(position).getType().equals("date")) {

                RelativeLayout relativeLayout = new RelativeLayout(this);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                perams.setMargins(0, 4, 0, 4);
                relativeLayout.setLayoutParams(perams);


                final String lable = obj.getFields().get(position).getLabel();
                LinearLayout.LayoutParams prams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                prams.setMargins(0, 0, 4, 0);
                final CustomTextView etText = new CustomTextView(context);
                etText.setLayoutParams(perams);
                etText.setTextSize(12);
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setHint(obj.getFields().get(position).getPlaceholder());

                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {

                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                etText.setText(f.getValue());
                            }
                        }
                }


                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                labelparams.setMargins(10, 0, 0, 4);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                CustomTextView labeltext = new CustomTextView(this);
                labeltext.setText(obj.getFields().get(position).getLabel());
                labeltext.setTextColor(Color.parseColor("#616060"));
                labeltext.setTextSize(14);
                labeltext.setLayoutParams(labelparams);
                llViews.addView(labeltext);

                etText.setPadding(20, 20, 20, 20);
                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
                etText.setMinHeight(60);
                etText.setMinimumHeight(60);
                etText.setGravity(Gravity.CENTER_VERTICAL);
                etText.setSingleLine();

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(30, 30);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(0, 0, 0, 0);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
//                    star.setVisibility(View.VISIBLE);
                    relativeLayout.setBackground(getDrawable(R.drawable.required));
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


                relativeLayout.addView(etText);
                relativeLayout.addView(star, layoutParams);
                llViews.addView(relativeLayout);
                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);

                llViews.addView(view);


                final Calendar myCalendar = Calendar.getInstance();

                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        // TODO Auto-generated method stub
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String myFormat = " MM - dd - yyyy"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        etText.setText(" " + sdf.format(myCalendar.getTime()));
                    }

                };

                etText.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        new DatePickerDialog(context, date, myCalendar
                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    }
                });
            } else if (obj.getFields().get(position).getType().equals("header")) {
                final CustomTextView etText = new CustomTextView(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(8, 8, 8, 8);

                etText.setLayoutParams(perams);
                if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h1")) {
                    etText.setTextSize(20);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h2")) {
                    etText.setTextSize(18);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h3")) {
                    etText.setTextSize(16);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h4")) {
                    etText.setTextSize(14);
                }
                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
                etText.setText(obj.getFields().get(position).getLabel());

                etText.setPadding(28, 28, 28, 28);
                etText.setMinHeight(60);
                etText.setMinimumHeight(60);
                etText.setGravity(Gravity.CENTER);
                etText.setSingleLine();
                etText.setSingleLine();
                etText.setTypeface(null, Typeface.BOLD);
                etText.setTag(obj.getFields().get(position).getPlaceholder());
                Log.e("Tag", obj.getFields().get(position).getPlaceholder());
                if (isDrawOverImageForm) {
                    if (etText.getParent() != null) {
                        ((ViewGroup) etText.getParent()).removeView(etText); // <- fix
                    }
                    llViews1.addView(etText);
                } else {
                    llViews.addView(etText);
                }
                // TEXTVIEW


                allViewInstance.add(etText);


            } else if (obj.getFields().get(position).getType().equals("text") && obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("signature")) {
                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams llPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                llPerams.setMargins(8, 8, 8, 8);
                ll.setLayoutParams(llPerams);
                ll.setOrientation(LinearLayout.VERTICAL);
                final CustomTextView etText = new CustomTextView(context);
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(16, 0, 16, 40);

                etText.setText(obj.getFields().get(position).getLabel());
                etText.setTextSize(14);
                etText.setTextColor(Color.parseColor("#616060"));
                etText.setLayoutParams(perams);
                etText.setTypeface(etText.getTypeface(), Typeface.NORMAL);

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(30, 30);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 0, 4, 10);
                startparams.gravity = Gravity.RIGHT;
                ImageView star = new ImageView(this);
                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);

                ll.addView(star);
                ll.addView(etText);


                final ImageView iv = new ImageView(context);
                LinearLayout.LayoutParams ivPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 200);
                iv.setLayoutParams(ivPerams);
                iv.setBackground(getDrawable(R.drawable.dotted));
                iv.setVisibility(View.VISIBLE);

                ll.addView(iv);

                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
//                    star.setVisibility(View.VISIBLE);
                    etText.setTextColor(Color.RED);

                }

                if (taskList1 != null) {
                    if (taskList1.getFields().size() > 0)
                        for (SaveField f : taskList1.getFields()) {

                            if (obj.getFields().get(position).getFieldId().equals(f.getFieldId())) {
                                if (f.getValue().isEmpty()) {

                                } else {

                                    String base64Image = f.getValue();

                                    byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    String path = addJpgSignatureToGallery(decodedByte);
                                    etText.setTag(path);
//

                                    iv.setImageBitmap(decodedByte);
                                    isSignatureSaved = true;
                                }

                            }

                        }
                }
                else {

                }
                if (isSignatureSaved) {
                    iv.setVisibility(View.VISIBLE);
                } else {
                    iv.setVisibility(View.VISIBLE);
                }
                etText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        getSignatures(etText, iv);
                    }
                });

                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                            return;
                        }
                        mLastClickTime = SystemClock.elapsedRealtime();
                        getSignatures(etText, iv);
                    }
                });

//                iv.setVisibility(View.GONE);

                llViews.addView(ll);
                allViewInstance.add(etText);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 30, 0, 30);
                view.setLayoutParams(p);

                llViews.addView(view);

            }



        }

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                hideKeyboard(FormSection.this);
                if (isDrawOverImageForm) {
                    Bitmap bitmap = drawingView.exportDrawingWithoutBackground();
                    Log.d("valuie", "bitmap value is: " + bitmap);

                    if (bitmap != null) {
                        String path = saveImage(bitmap);
                        Log.d("PAth", "image path is : " + path);

                        String file = compressImage(path);
                        SelectImagesModel model1 = new SelectImagesModel();
                        model1.setTempUri(Uri.parse(file));
                        File f = new File(file);
                        model1.setFinalImage(f);
                        model1.setName(f.getName());
                        Log.d("IMAGE", "image name is: " + f.getName());
                        myImages.add(model1);
                    }
                    Log.d("SIZE", "images size is: " + myImages.size());
                }

                getDataFromDynamicViews(getWindow().getDecorView().findViewById(android.R.id.content), obj);
            }
        });
        saveDrawingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                hideKeyboard(FormSection.this);
                if (isDrawOverImageForm) {
                    Bitmap bitmap = drawingView.exportDrawing();
                    Log.d("valuie", "bitmap value is: " + bitmap);

                    if (bitmap != null) {
                        String path = saveImage(bitmap);
                        Log.d("PAth", "image path is : " + path);

                        String file = compressImage(path);
                        SelectImagesModel model1 = new SelectImagesModel();
                        model1.setTempUri(Uri.parse(file));
                        File f = new File(file);
                        model1.setFinalImage(f);
                        model1.setName(f.getName());
                        Log.d("IMAGE", "image name is: " + f.getName());
                        myImages.add(model1);
                    }
                    Log.d("SIZE", "images size is: " + myImages.size());
                }

                getDataFromDynamicViews(getWindow().getDecorView().findViewById(android.R.id.content), obj);
            }
        });


    }

    private String saveImage(Bitmap image) {
        String savedImagePath = null;

        String imageFileName = "Draw_OVER_IMAGE" + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/Draw_OVER_IMAGES");

        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the image to the system gallery
        }
        return savedImagePath;
    }


    private class ConvertUrlToBitmap extends AsyncTask<String, Long, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return bitmap;
            } catch (Exception e) {
                Log.e("", e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(final Bitmap image) {
            super.onPostExecute(image);

            Log.d("VALUE", "Bitmpa value is: " + image);
            // download was successful
            // if you want to update your UI, make sure u do it on main thread. like this:
            if (image != null) {
                img = image;
                if (isDrawOverImageForm) {
                    FormSection.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // update UI here
                            drawingView.setBackgroundImage(image);
                        }
                    });
                } else {
//                    Toast.makeText(context, "saving", Toast.LENGTH_SHORT).show();
//                    String path = saveImage(img);
//
//                    String file = compressImage(path);
//
//                    SelectImagesModel img = new SelectImagesModel();
//                    img.setTempUri(Uri.parse(file));
//                    File f = new File(file);
//                    img.setFinalImage(f);
//                    img.setName(f.getName());
//
//                    myImages.add(img);
                }

            }


        }
    }

    private void getSignatures(final CustomTextView tv, final ImageView iv) {
        final Dialog dialog = new Dialog(context);
        final SignaturePad mSignaturePad;
        final Button mClearButton;
        final Button mSaveButton;

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_signature);
        mClearButton = (Button) dialog.findViewById(R.id.clear_button);
        mSaveButton = (Button) dialog.findViewById(R.id.save_button);
        mSignaturePad = (SignaturePad) dialog.findViewById(R.id.signature_pad);

        mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {
//                Toast.makeText(SignatureActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSigned() {
                mSaveButton.setEnabled(true);
                mClearButton.setEnabled(true);
            }

            @Override
            public void onClear() {
                mSaveButton.setEnabled(false);
                mClearButton.setEnabled(false);
            }
        });


        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSignaturePad.clear();
            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
//                temp = addJpgSignatureToGallery(signatureBitmap);
                String path = addJpgSignatureToGallery(signatureBitmap);
//                Log.e("beforeReturningPath", temp);
                tv.setTag(path);
                iv.setVisibility(View.VISIBLE);
                Glide.with(context).load(path).into(iv);
                Toast.makeText(context, "Signature saved", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
//                if (addSvgSignatureToGallery(mSignaturePad.getSignatureSvg())) {
//                    Toast.makeText(SignatureActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(SignatureActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        dialog.show();


    }

    public String addJpgSignatureToGallery(Bitmap signature) {
        boolean result = false;
        String path = "";
        try {
            File photo = new File(getAlbumStorageDir(".SignaturePad"), String.format("Signature_%d.jpeg", System.currentTimeMillis()));
            path = photo.getPath();
            Log.e("path", path);
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;


        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result) {

            return path;
        } else {
            Toast.makeText(context, "Unable to store the signature", Toast.LENGTH_SHORT).show();
            return "";
        }
    }


    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("SignaturePad", "Directory not created");
        }
        return file;
    }

    public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(photo);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        stream.close();
    }


    private void scanMediaFile(File photo) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(photo);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }






    public void getDataFromDynamicViews(View v, final Form response) {
        try {

            JSONObject mainObj = new JSONObject();
            JSONArray jsonArray = new JSONArray();
//            JSONObject parentObj = new JSONObject();
////          parentObj.put("patient_id", "" + response.body().getForm().getFormId());
//            parentObj.put("related_data_id", task_id);

            for (int noOfViews = 0; noOfViews < response.getFields().size(); noOfViews++) {
                JSONObject fieldsObj = new JSONObject();

                if (SettingValues.getBarcodeVal() != null) {
                    if (noOfViews == 0) {
                        fieldsObj.put("barcodee", SettingValues.getBarcodeVal());
                    }
                } else {
                    fieldsObj.put("barcodee", "null");

                }

                if(noOfViews==0) {
                    JSONArray barcodelist = new JSONArray();
                    for (int i = 0; i < SettingValues.getBarcodelist().size(); i++) {

                        JSONObject barcodeobj = new JSONObject();

                        barcodelist.put("" + SettingValues.getBarcodelist().get(i));

                    }
                    fieldsObj.put("barcode_scan", "" + barcodelist);
//                    jsonArray.put(fieldsObj);
                }

                Log.d("BARCODDE","barcode information is: "+ response.getFields().get(noOfViews).getPlaceholder());


                if (response.getFields().get(noOfViews).getType().equals("select")) {
                    Spinner spinner = (Spinner) allViewInstance.get(noOfViews);

                    String selected_name = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getLabel();
                    String selected_id = response.getFields().get(noOfViews).getOptions().get(spinner.getSelectedItemPosition()).getOptionId() + "";
                    //Log.e("Selected_field_ID", response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getFieldId() + "");
                    Log.e("value", selected_name + "");
                    Log.e("field_id", selected_id + "");
                    fieldsObj.put("field_id", "" + response.getFields().get(noOfViews).getFieldId());
                    fieldsObj.put("type", "select");
                    fieldsObj.put("value", "");

                    JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                    JSONObject optionsObj = new JSONObject();
                    optionsObj.put("option_id", "" + selected_id);
                    optionsObj.put("value", "" + selected_name);
                    optionsArray.put(optionsObj);
//                    }
                    fieldsObj.put("option", optionsArray);
                    jsonArray.put(fieldsObj);
                }

                if (response.getFields().get(noOfViews).getType().equals("radio-group")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        try {
                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

                            Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");

                            String a[] = selectedRadioBtn.getTag().toString().split(",");
                            fieldsObj.put("field_id",
                                    "" + a[0]);
                            fieldsObj.put("value", "");
                            Log.e("f", a[0]);
                            Log.e("s", a[1]);
                            Log.e("s", a[2]);

                            JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                            JSONObject optionsObj = new JSONObject();
                            optionsObj.put("option_id", "" + a[1]);
                            fieldsObj.put("type", "radio-group");
                            optionsObj.put("value", "" + a[2]);
                            optionsArray.put(optionsObj);
//                    }
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
                            isMandatoryFilled = false;
//                            return;
                        }
                    } else {
                        try {

                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

                            Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");

                            String a[] = selectedRadioBtn.getTag().toString().split(",");
                            fieldsObj.put("field_id",
                                    "" + a[0]);
                            fieldsObj.put("type", "radio-group");
                            fieldsObj.put("value", "");
                            Log.e("f", a[0]);
                            Log.e("s", a[1]);
                            Log.e("s", a[2]);

                            JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                            JSONObject optionsObj = new JSONObject();
                            optionsObj.put("option_id", "" + a[1]);
                            optionsObj.put("value", "" + a[2]);
                            optionsArray.put(optionsObj);
//                    }
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
//                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getId());

                            Log.e("rg_id", radioGroup.getTag().toString() + "");

                            fieldsObj.put("field_id",
                                    radioGroup.getTag().toString());
                            fieldsObj.put("type", "radio-group");
                            fieldsObj.put("value", "");
//                            Log.e("f", a[0]);
//                            Log.e("s", a[1]);
//                            Log.e("s", a[2]);

                            JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                            JSONObject optionsObj = new JSONObject();
                            optionsObj.put("option_id", "");
                            optionsObj.put("value", "");
                            optionsArray.put(optionsObj);
//                        }
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);
//
//                            return;
                        }
                    }
                }
                if (response.getFields().get(noOfViews).getType().equals("checkbox-group")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        Boolean empty = true;
                        String field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "checkbox-group");
                        fieldsObj.put("value", "");
                        LinearLayout ll = (LinearLayout) allViewInstance.get(noOfViews);
                        JSONArray optionsArray = new JSONArray();
                        for (int i = 0; i < ll.getChildCount(); i++) {
                            CheckBox tempChkBox = (CheckBox) ll.getChildAt(i);
                            if (tempChkBox.isChecked()) {
                                empty = false;
                                String a[] = tempChkBox.getTag().toString().split(",");

                                Log.e("f", a[0]);
                                Log.e("s", a[1]);

                                JSONObject optionsObj = new JSONObject();
                                optionsObj.put("option_id", "" + a[0]);
                                optionsObj.put("value", "" + a[1]);
                                optionsArray.put(optionsObj);
                                Log.e("Selected checkbox", tempChkBox.getTag().toString() + "");
                            }
                        }
                        if (empty) {
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
                            isMandatoryFilled = false;
//                            return;
                        } else {
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);
                        }
                    } else {
                        String field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "checkbox-group");
                        fieldsObj.put("value", "");
                        LinearLayout ll = (LinearLayout) allViewInstance.get(noOfViews);
                        JSONArray optionsArray = new JSONArray();
                        for (int i = 0; i < ll.getChildCount(); i++) {
                            CheckBox tempChkBox = (CheckBox) ll.getChildAt(i);
                            if (tempChkBox.isChecked()) {
                                String a[] = tempChkBox.getTag().toString().split(",");

                                Log.e("f", a[0]);
                                Log.e("s", a[1]);

                                JSONObject optionsObj = new JSONObject();
                                optionsObj.put("option_id", "" + a[0]);
                                optionsObj.put("value", "" + a[1]);
                                optionsArray.put(optionsObj);
                                Log.e("Selected checkbox", tempChkBox.getTag().toString() + "");
                            }
                        }

                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);
                    }

                }
                if (response.getFields().get(noOfViews).getType().equals("text") && !(response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("Time")) && !(response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("signature"))) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "text");
                            fieldsObj.put("value", text);
                        } else {
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "text");
                        fieldsObj.put("value", text);

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }
                }


                if (response.getFields().get(noOfViews).getType().equals("number")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "number");
                            fieldsObj.put("value", text);
                        } else {
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "number");
                        fieldsObj.put("value", text);

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }

                }
                if (response.getFields().get(noOfViews).getType().equals("textarea")) {

                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;
                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);

                        text = textView.getText().toString();
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "textarea");
                            fieldsObj.put("value", text);
                        } else {
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");

                        Log.e("textarea", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;
                        CustomEditText textView = (CustomEditText) allViewInstance.get(noOfViews);

                        text = textView.getText().toString();
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";


                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "textarea");
                        fieldsObj.put("value", text);


                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");

                        Log.e("textarea", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }

                }
                if (response.getFields().get(noOfViews).getType().equals("text") && (response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("Time"))) {
                    //Time
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "time");
                            fieldsObj.put("value", text);
                        } else {
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();


                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "time");
                        fieldsObj.put("value", text);


                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }

                }
                if (response.getFields().get(noOfViews).getType().equals("date")) {
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("True")) {
                        //Date
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "date");
                            fieldsObj.put("value", text);
                        } else {
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        //Date
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getText().toString();

                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "date");
                        fieldsObj.put("value", text);

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }


                }
                if (response.getFields().get(noOfViews).getType().equals("header")) {
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getTag().toString();

                        if (!text.equalsIgnoreCase("")) {
                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "header");
                            fieldsObj.put("value", text);
                        } else {
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
                            isMandatoryFilled = false;
//                            return;
                        }

                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    } else {
                        String field_id, text;

                        CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                        field_id = response.getFields().get(noOfViews).getFieldId() + "";
                        text = textView.getTag().toString();


                        fieldsObj.put("field_id", field_id);
                        fieldsObj.put("type", "header");
                        fieldsObj.put("value", text);


                        JSONArray optionsArray = new JSONArray();
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);

                        Log.e("text", "ID: " + field_id + " Text: " + textView.getText().toString() + "");
                    }
                }

//                if (response.getFields().get(noOfViews).getType().equals("text") && (response.getFields().get(noOfViews).getPlaceholder().equals("barcode"))) {
//
//                    Toast.makeText(context, "you enter barcode in place holder ", Toast.LENGTH_SHORT).show();
//
//                }



                if (obj.getFields().get(noOfViews).getType().equalsIgnoreCase("text") && obj.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("barcode")){
//                    Toast.makeText(context, "barcode json", Toast.LENGTH_SHORT).show();
                }

                if (response.getFields().get(noOfViews).getType().equals("text") && (response.getFields().get(noOfViews).getPlaceholder().equalsIgnoreCase("signature"))) {
                    //Time
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        try {
                            String field_id, text;
                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";
                            text = textView.getTag().toString();


                            Bitmap bm = BitmapFactory.decodeFile(text);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();

                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);


                            if (!text.equalsIgnoreCase("")) {
                                fieldsObj.put("field_id", field_id);
                                fieldsObj.put("type", "signature");
                                fieldsObj.put("value", encodedImage);
                            } else {
                                Toast.makeText(this, "Signature is Mandatory. Please take signature", Toast.LENGTH_SHORT).show();
//                                return;
                                isMandatoryFilled = false;
                            }

                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                            Log.d("myImages", "ID: " + field_id + " Data: " + encodedImage);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Signature is Mandatory. Please take signature", Toast.LENGTH_SHORT).show();
                            isMandatoryFilled = false;
//                            return;
                        }
                    } else {
                        try {

                            String field_id, text = "";

                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";
                            if(textView.getTag() != null)
                                text = textView.getTag().toString();



                            Log.d("SIGNATURE","Signature data is: " + text);

                            Bitmap bm = BitmapFactory.decodeFile(text);
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                            byte[] b = baos.toByteArray();

                            String path = addJpgSignatureToGallery(bm);

                            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "signature");
                            fieldsObj.put("value", encodedImage);


                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                            Log.d("myImages", "ID: " + field_id + " Data: " + encodedImage);
                        } catch (NullPointerException e) {
                            e.printStackTrace();

                            String field_id;
                            CustomTextView textView = (CustomTextView) allViewInstance.get(noOfViews);
                            field_id = response.getFields().get(noOfViews).getFieldId() + "";

                            fieldsObj.put("field_id", field_id);
                            fieldsObj.put("type", "signature");
                            fieldsObj.put("value", "");

                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                        }
                    }
                }
            }
            Log.d("COUNT", "images json count: " + jsonArray.length());
            Log.d("COUNT", "images json count: " + myImages.size());


            JSONObject allFields = new JSONObject();
            allFields.put("fields", jsonArray);

            JSONObject formFields = new JSONObject();
            formFields.put("fields", jsonArray);


            JSONArray imagesArray = new JSONArray();
            JSONArray imagesPathArray = new JSONArray();
            for (int i = 0; i < myImages.size(); i++) {
                imagesArray.put(myImages.get(i).getName());
                imagesPathArray.put(myImages.get(i).getTempUri());
                Log.d("URI", "images uri is: " + myImages.get(i).getTempUri());
            }


            allFields.put("images", imagesArray);
            formFields.put("images", imagesPathArray);


            mainObj.put("form-section", allFields);
//            mainObj.put("barcode","1234")

            QueueModel model = new QueueModel();
            model.setId(formId + "");
            model.setJson(formFields + "");
            model.setState(Constants.StateAdded);
            model.setTitle("");
            model.setMessage("");
            model.setTaskID(task_id);
            handler.deleteDraftIncidenceOnIndidenceId(formId, task_id);
            handler.addDraftIncidenceOnIncidenceID(model);

            Log.e("taskandformid","TASK_ID IS THISSSS"+task_id+" "+"FORM ID IS THISSSS"+formId);

            Log.d("optionsObj", mainObj + "");

            TaskDetails.sectionImagesList.addAll(myImages);
            Log.d("optionsObj", "image list size is:" + TaskDetails.sectionImagesList.size());

            if(isMandatoryFilled){
                Toast.makeText(context, "Form saved successfully.", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_LONG).show();
            }


            TaskDetails td = new TaskDetails();
            td.resetView(position, mainObj.toString(), FormSection.this, isMandatoryFilled);

//            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 1280.0f;
        float maxWidth = 1024.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }


    /* Initiate Google API Client  */

    /* Check Location Permission for Marshmallow Devices */
//    private void checkPermissions() {
//        if (Build.VERSION.SDK_INT >= 23) {
//            if (ContextCompat.checkSelfPermission(context,
//                    android.Manifest.permission.ACCESS_FINE_LOCATION)
//                    != PackageManager.PERMISSION_GRANTED)
//                requestLocationPermission();
//            else
//                showSettingDialog();
//        } else
//            showSettingDialog();
//
//    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(FormSection.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(FormSection.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        } else {
            ActivityCompat.requestPermissions(FormSection.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Show Location Access Dialog */
//    private void showSettingDialog() {
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
//        locationRequest.setInterval(30 * 1000);
//        locationRequest.setFastestInterval(5 * 1000);//5 sec Time interval for location update
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off
//
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final Status status = result.getStatus();
//                final LocationSettingsStates state = result.getLocationSettingsStates();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        // All location settings are satisfied. The client can initialize location
//                        // requests here.
//                        if (mGoogleApiClient == null) {
//                            initGoogleAPIClient();
//                        }
//
//                        //updateGPSStatus("GPS is Enabled in your device");
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        // Location settings are not satisfied. But could be fixed by showing the user
//                        // a dialog.
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            status.startResolutionForResult(FormSection.this, REQUEST_CHECK_SETTINGS);
//                        } catch (IntentSender.SendIntentException e) {
//                            e.printStackTrace();
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // Location settings are not satisfied. However, we have no way to fix the
//                        // settings so we won't show the dialog.
//                        break;
//                }
//            }
//        });
//    }


    @Override
    protected void onResume() {
        HorizontalScrollView horizontalScrollView;
        horizontalScrollView = (HorizontalScrollView)findViewById(R.id.horizontal);
        super.onResume();
        if(SettingValues.getBarcodeVal().equals("")){
            SettingValues.setBarcodeVal("Scan Barcode");

            barcode_text.setText(SettingValues.getBarcodeVal());
        }
        else {
            barcodeviews.removeAllViews();
            barcode_text.setText(SettingValues.getBarcodeVal());
            for(int i =0; i<SettingValues.getBarcodelist().size();i++) {
                CustomTextView txt = new CustomTextView(this);

                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                LinearLayout.LayoutParams txtperams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                txtperams.setMargins(10, 0, 10, 0);
                txt.setLayoutParams(txtperams);
                int val = i+1;
                txt.setText("Value"+val+": "+SettingValues.getBarcodelist().get(i)+",");
                txt.setTextSize(14);
                txt.setGravity(Gravity.CENTER_VERTICAL);
                barcodeviews.addView(txt);
            }
        }
//        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS
    }




    //Method to update GPS status text
//    private void updateGPSStatus(String status) {
//        gps_status.setText(status);
//    }


    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */


    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Splash", "onpause");
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        hideKeyboard(FormSection.this);
        if (isDrawOverImageForm) {
            Bitmap bitmap = drawingView.exportDrawing();
            Log.d("valuie", "bitmap value is: " + bitmap);

            if (bitmap != null) {
                String path = saveImage(bitmap);
                Log.d("PAth", "image path is : " + path);

                String file = compressImage(path);
                SelectImagesModel model1 = new SelectImagesModel();
                model1.setTempUri(Uri.parse(file));
                File f = new File(file);
                model1.setFinalImage(f);
                model1.setName(f.getName());
                Log.d("IMAGE", "image name is: " + f.getName());
                myImages.add(model1);
            }
            Log.d("SIZE", "images size is: " + myImages.size());
        }

        getDataFromDynamicViews(getWindow().getDecorView().findViewById(android.R.id.content), obj);
        SettingValues.getBarcodelist().clear();
        vitaldata(getWindow().getDecorView().findViewById(android.R.id.content), objj);

//        SettingValues.setBarcodeVal("");
//        if (photo != null) {
//
//            File file = new File(photo.getPath());
//            boolean deleted = file.delete();
//            if (deleted) {
//                Uri contentUri = Uri.fromFile(file);
//                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, contentUri);
//                sendBroadcast(mediaScanIntent);
//            }
//        }

    }


    @Override
    public void onBackPressed() {
        Log.e("Splash", "back");
        super.onBackPressed();

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        Log.e("Settings", "Result OK");


                        break;
                    case RESULT_CANCELED:
                        Log.e("Settings", "Result Cancel");

                        new Handler().post(new Runnable() {
                            public void run() {

                                final Dialog dialog = new Dialog(FormSection.this);
                                final CustomButton btnClose, btnYes;
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setContentView(R.layout.dialog_alert);
                                dialog.setCancelable(false);
                                btnYes = (CustomButton) dialog.findViewById(R.id.btnYes);
                                btnClose = (CustomButton) dialog.findViewById(R.id.btnNo);
                                btnClose.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });

                                btnYes.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                                            return;
                                        }
                                        mLastClickTime = SystemClock.elapsedRealtime();

                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();

                            }
                        });
                        break;
                }
                break;
        }


        if (requestCode == REQUEST_GALLERY_PERMISSION) {
            if (resultCode == Activity.RESULT_OK) {

                ArrayList<String> img = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                for (int i = 0; i < img.size(); i++) {
                    String file = compressImage(img.get(i));

                    SelectImagesModel model = new SelectImagesModel();
                    model.setTempUri(Uri.parse(file));
                    File f = new File(file);
                    model.setFinalImage(f);
                    model.setName(f.getName());

                    myImages.add(model);
                    Log.e("myImages", img.get(i));
                }

                if (adapterSelectImages != null) {
                    adapterSelectImages.setItems(myImages);
                    adapterSelectImages.notifyDataSetChanged();
                } else {
                    adapterSelectImages = new AdapterSelectImages(myImages, FormSection.this, ivSelectImages);
                    rvSelectImages.setLayoutManager(imagesManager);
                    rvSelectImages.setAdapter(adapterSelectImages);
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText( getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT ).show();
            }
        }
    }

    private void permissionAccess() {
        if (!checkPermission(p1)) {
            Log.e("TAG", p1);
            requestPermission(p1);
        } else if (!checkPermission(p2)) {
            Log.e("TAG", p2);
            requestPermission(p2);
        } else {
            Pix.start(FormSection.this,                    //Activity or Fragment Instance
                    REQUEST_GALLERY_PERMISSION,                //Request code for activity results
                    30 - myImages.size());
//            Toast.makeText(CreateJob.this, "All permission granted", Toast.LENGTH_LONG).show();
        }

    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(FormSection.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {

        if (ContextCompat.checkSelfPermission(FormSection.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FormSection.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            //Do the stuff that requires permission...
            Log.e("TAG", "Not say request");
        }
    }

}