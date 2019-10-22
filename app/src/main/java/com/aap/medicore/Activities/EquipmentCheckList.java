package com.aap.medicore.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Adapters.AdapterSelectImages;
import com.aap.medicore.Adapters.EqipmentCheckListSpinnerArrayAdapter;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.CheckListForms;
import com.aap.medicore.Models.ChecklistForm;
import com.aap.medicore.Models.EquipmentChecklistForm;
import com.aap.medicore.Models.EquipmentChecklistResponse;
import com.aap.medicore.Models.SelectImagesModel;
import com.aap.medicore.Models.SubmitFormResponse;
import com.aap.medicore.Models.VehicleDetail;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;

import static com.aap.medicore.Activities.Home.wayLatitude;
import static com.aap.medicore.Activities.Home.wayLongitude;

public class EquipmentCheckList extends BaseActivity {
    ImageView ivBack;
    TinyDB tinyDB;
    CustomButton btnSubmit;
    CustomTextView heading;
    Context context = EquipmentCheckList.this;
    CustomTextView subtitle;
    LinearLayout barcodelayout;
    LinearLayout barcodeviews;
    String start_time,end_time;
    HorizontalScrollView hsvLayout;
    ImageView ivSelectImages;
    int score = 0;
    RecyclerView rvSelectImages;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    DatabaseHandler handler;
    AdapterSelectImages adapterSelectImages;
    private final int REQUEST_GALLERY_PERMISSION = 5568;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String p1 = android.Manifest.permission.CAMERA, p2 = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    ArrayList<SelectImagesModel> myImages;
    LinearLayoutManager imagesManager;
    LinearLayout llViews;
    String id,form_name;
    CustomTextView timer;
    public static int chekclass = 1;
    public static String equip_barcode = "";
    List<View> allViewInstance = new ArrayList<View>();
   public static CustomTextView value;
    int vehicleId=0;
    int s = 0;
    int hours;
    RelativeLayout vehicleDetail;
    CustomTextView registration,tvColor;


    //varibles for location
    private FusedLocationProviderClient mFusedLocationClient;
    double wayLatitude = 0.0, wayLongitude = 0.0;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private android.widget.Button btnLocation;
    private android.widget.Button btnContinueLocation;
    private StringBuilder stringBuilder;

    private boolean isContinue = false;
    private boolean isGPS = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equipment_check_list);
        barcodeviews = (LinearLayout)findViewById(R.id.barcodeviews);
        getSupportActionBar().hide();
        tinyDB = new TinyDB(getApplicationContext());
        subtitle = (CustomTextView)findViewById(R.id.subtitle);
        hsvLayout = (HorizontalScrollView)findViewById(R.id.hsv);
        barcodelayout = (LinearLayout)findViewById(R.id.barcodelayout);
        ivSelectImages = findViewById(R.id.ivSelectImages);
        rvSelectImages = findViewById(R.id.rvImages);
        timer =(CustomTextView)findViewById(R.id.timer);
        handler = new DatabaseHandler(EquipmentCheckList.this);
        imagesManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        myImages = new ArrayList<>();

        vehicleId = tinyDB.getInt("vehicle_id");
        Log.d("ID","vehicle id is: " + vehicleId);
        init();


        id = getIntent().getStringExtra("formid");
        form_name = getIntent().getStringExtra("formname");
        heading.setText(form_name);
        clickListeners();
        hitEquipmentCheckList();
        if (!(tinyDB.getString(Constants.vehicle_details).isEmpty())) {

            Gson gson = new Gson();
            String json = tinyDB.getString(Constants.vehicle_details);

            final VehicleDetail obj = gson.fromJson(json, VehicleDetail.class);


            tvColor.setText(obj.getVehicleName());

            registration.setText(obj.getRegistrationno());



        } else {



        }

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




//        hitEquipmentCheckList();
    }


    @Override
    protected void onStart() {
        super.onStart();
//        hitEquipmentCheckList();
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(EquipmentCheckList.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(EquipmentCheckList.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EquipmentCheckList.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    AppConstants.LOCATION_REQUEST);

        } else {
            if (isContinue) {
                mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
            } else {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(EquipmentCheckList.this, location -> {
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

                                final Dialog dialog = new Dialog(EquipmentCheckList.this);
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

//                                        checkPermissions();
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
                    adapterSelectImages = new AdapterSelectImages(myImages, EquipmentCheckList.this, ivSelectImages);
                    rvSelectImages.setLayoutManager(imagesManager);
                    rvSelectImages.setAdapter(adapterSelectImages);
                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText( getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT ).show();
            }
        }
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



    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }





    @Override
    protected void onResume() {
        super.onResume();

        if(SettingValues.getBarcodeVal().equals("")){
            SettingValues.setBarcodeVal("Scan Barcode");

//            barcode_txt.setText(SettingValues.getBarcodeVal());
        }
        else {
            barcodeviews.removeAllViews();
//            barcode_txt.setText(SettingValues.getBarcodeVal());
            for(int i =0; i<SettingValues.getBarcodelist().size();i++) {
                CustomTextView txt = new CustomTextView(this);
                HorizontalScrollView horizontalScrollView;
                horizontalScrollView = (HorizontalScrollView)findViewById(R.id.horizontal);
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
    }


    private void init() {
        ivBack = findViewById(R.id.ivBack);
        tinyDB = new TinyDB(EquipmentCheckList.this);
        llViews = findViewById(R.id.llViews);
        btnSubmit = findViewById(R.id.btnSubmit);
        vehicleDetail = (RelativeLayout)findViewById(R.id.vehicle_detail);
        registration = (CustomTextView)findViewById(R.id.tvRegistrationNo);
        tvColor = (CustomTextView)findViewById(R.id.tvColor);
        heading = (CustomTextView)findViewById(R.id.heading);
        heading.setText(form_name);
    }

    private void clickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        barcodelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(EquipmentCheckList.this,ScanActivity.class);
                startActivity(i);
            }
        });

        ivSelectImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

    private void permissionAccess() {
        if (!checkPermission(p1)) {
            Log.e("TAG", p1);
            requestPermission(p1);
        } else if (!checkPermission(p2)) {
            Log.e("TAG", p2);
            requestPermission(p2);
        } else {
            Pix.start(EquipmentCheckList.this,                    //Activity or Fragment Instance
                    REQUEST_GALLERY_PERMISSION,                //Request code for activity results
                    30 - myImages.size());
//            Toast.makeText(CreateJob.this, "All permission granted", Toast.LENGTH_LONG).show();
        }

    }


    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(EquipmentCheckList.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {

        if (ContextCompat.checkSelfPermission(EquipmentCheckList.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(EquipmentCheckList.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            //Do the stuff that requires permission...
            Log.e("TAG", "Not say request");
        }
    }

    private void startClock(){
        Timer t = new Timer();
//Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

                                  @Override
                                  public void run() {
                                      //Called each time when 1000 milliseconds (1 second) (the period parameter)


                                      timer.setText(String.valueOf(score));
                                      score += 1;
                                  }

                              },
//Set how long before to start calling the TimerTask (in milliseconds)
                0,
//Set the amount of time between each execution (in milliseconds)
                1000);
    }


    public void logoutConfirmDialogBox() {
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        final CustomButton btnNo, btnYes;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.timer);
        btnYes = (CustomButton) dialog.findViewById(R.id.btnYes);
        btnNo = (CustomButton) dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                dialog.dismiss();
            }
        });


        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //your stuff....
                    dialog.dismiss();
                    finish();
                }
                return true;
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                btnYes.setEnabled(false);



                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss aa");
                String datetime = dateformat.format(c.getTime());
//                Toast.makeText(context, ""+datetime, Toast.LENGTH_SHORT).show();
                Log.e("curenttime","scccccccccc"+datetime);
                start_time = datetime;


                final Handler handler = new Handler();
                Timer timerr = new Timer(false);
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                score++;
//                                if(score<60){
//                                    timer.setText(score+" Sec");
//                                }

                                if(score==60){
                                    score = 0;
                                    hours++;

                                }
                                timer.setText(hours+" min"+""+ score+" sec");
                            }

                        });
                    }
                };

                timerr.scheduleAtFixedRate(timerTask, 1000, 1000);
                dialog.dismiss();

//                new CountDownTimer(-30000, 1000) {
//
//                    public void onTick(long millisUntilFinished) {
//                        timer.setText("seconds remaining: " + millisUntilFinished / 1000);
//                        //here you can have your logic to set text to edittext
//                    }
//
//                    public void onFinish() {
//                        timer.setText("done!");
//                    }
//
//                }.start();

                }
        });
        if(!((Activity) context).isFinishing())
        {
            //show dialog
            dialog.show();

        }

    }

    @Override

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void hitEquipmentCheckList() {
        retrofit2.Call<CheckListForms> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().getEquipmentChecklist(tinyDB.getString(Constants.token),id);

        call.enqueue(new Callback<CheckListForms>() {
            @Override
            public void onResponse(retrofit2.Call<CheckListForms> call, final Response<CheckListForms> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
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


    @SuppressLint("NewApi")
    public void getFormDetail(final Response<CheckListForms> response) {

        final ChecklistForm obj = response.body().getChecklistForm();
        subtitle.setText(obj.getSubTitle());
        if(obj.getBarcodeEnabled().equals("yes")){
            logoutConfirmDialogBox();
        }
        else{
            timer.setText("N/A");
        }

        Log.e("size", obj.toString());
        for (int position = 0; position < obj.getFields().size(); position++) {

            vehicleDetail.setVisibility(View.VISIBLE);

            if (obj.getFields().get(position).getType().equalsIgnoreCase("file")) {

                findViewById(R.id.hsv).setVisibility(View.VISIBLE);


                allViewInstance.add(hsvLayout);
                Log.d("SIZE", "Image size is: " + myImages.size());
                if (myImages.size() > 0) {
                    if (adapterSelectImages != null) {
                        adapterSelectImages.setItems(myImages);
                        adapterSelectImages.notifyDataSetChanged();
                    } else {
                        adapterSelectImages = new AdapterSelectImages(myImages, EquipmentCheckList.this, ivSelectImages);
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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar c = Calendar.getInstance();
                SimpleDateFormat dateformat = new SimpleDateFormat("hh:mm:ss aa");
                String datetime = dateformat.format(c.getTime());
//                Toast.makeText(context, ""+datetime, Toast.LENGTH_SHORT).show();
                Log.e("curenttime","scccccccccc"+datetime);
                end_time = datetime;
                getDataFromDynamicViews(getWindow().getDecorView().findViewById(android.R.id.content), obj);
//                btnSubmit.setEnabled(false);
                SettingValues.setBarcodeVal("");
                SettingValues.getBarcodelist().clear();

            }
        });
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
            File photo = new File(getAlbumStorageDir("SignaturePad"), String.format("Signature_%d.jpeg", System.currentTimeMillis()));
            path = photo.getPath();
            Log.e("path", path);
            saveBitmapToJPG(signature, photo);
            scanMediaFile(photo);
            result = true;


        } catch (IOException e) {
            e.printStackTrace();
        }
        if (result) {
            Toast.makeText(context, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
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

    public void getDataFromDynamicViews(View v, final ChecklistForm response) {
        try {


            JSONArray jsonArray = new JSONArray();
//            JSONObject parentObj = new JSONObject();
////          parentObj.put("patient_id", "" + response.body().getForm().getFormId());
//            parentObj.put("related_data_id", task_id);

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
                            btnSubmit.setEnabled(true);
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
            obj.put("vehicle_id", vehicleId);
            obj.put("checklist_type", "equipment_checklist");
            obj.put("form", allFields);
            obj.put("lat", wayLatitude);
            obj.put("lng", wayLongitude);
//            Toast.makeText(context, ""+val, Toast.LENGTH_SHORT).show();
            obj.put("timeCount", "Start Time : "+start_time+"  "+"End Time :"+"  "+end_time);
            submitData(obj.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void submitData(String data) {
        btnSubmit.setEnabled(false);
        retrofit2.Call<SubmitFormResponse> call;
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), data.toString());
        RequestBody iid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(vehicleId));
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "equipment_checklist");

        ArrayList<MultipartBody.Part> images = new ArrayList<>();

        for (int i = 0; i < myImages.size(); i++) {
            File file1 = new File(String.valueOf(myImages.get(i).getTempUri()));
            images.add(MultipartBody.Part.createFormData("images", file1.getName(), RequestBody.create(MediaType.parse("image/*"), myImages.get(i).getFinalImage())));
        }


        call = RetrofitClass.getInstance().getWebRequestsInstance().submitChecklistData(tinyDB.getString(Constants.token), bodyRequest,images,iid, type, Integer.parseInt(id));
        call.enqueue(new Callback<SubmitFormResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SubmitFormResponse> call, Response<SubmitFormResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        findViewById(R.id.progress).setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
btnSubmit.setEnabled(false);
                        Toast.makeText(EquipmentCheckList.this, "Successfully Incidence saved to server !", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EquipmentCheckList.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progress).setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.VISIBLE);
                    }
                } else {
                    findViewById(R.id.progress).setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    Toast.makeText(EquipmentCheckList.this, "Internal Server Error !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SubmitFormResponse> call, Throwable t) {
                Toast.makeText(EquipmentCheckList.this, "Can't connect to server !", Toast.LENGTH_SHORT).show();
                findViewById(R.id.progress).setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
                t.printStackTrace();
            }
        });
    }


}
