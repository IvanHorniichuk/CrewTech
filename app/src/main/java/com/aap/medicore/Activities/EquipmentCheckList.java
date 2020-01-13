package com.aap.medicore.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aap.medicore.Adapters.AdapterSelectImages;
import com.aap.medicore.Adapters.EqipmentCheckListSpinnerArrayAdapter;
import com.aap.medicore.Adapters.EquipmentAccessoriesAdapter;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.DatabaseHandler.DatabaseHandler;
import com.aap.medicore.Models.CheckListForms;
import com.aap.medicore.Models.ChecklistForm;
import com.aap.medicore.Models.ChecklistFormField;
import com.aap.medicore.Models.ChecklistFormOption;
import com.aap.medicore.Models.SelectImagesModel;
import com.aap.medicore.Models.SubmitFormResponse;
import com.aap.medicore.Models.VehicleDetail;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomEditText;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.GPSTracker;
import com.aap.medicore.Utils.SessionTimeoutDialog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;

import static android.util.TypedValue.COMPLEX_UNIT_SP;
import static com.aap.medicore.Utils.UIUtils.convertToDp;

public class EquipmentCheckList extends BaseActivity implements EquipmentAccessoriesAdapter.OnAdminItemClickedListener {
    ImageView ivBack;
    TinyDB tinyDB;
    Button btnSubmit;
    TextView heading;
    Context context = EquipmentCheckList.this;
    TextView subtitle;
    LinearLayout barcodelayout;
    LinearLayout barcodeviews;
    HorizontalScrollView hsvLayout;
    ProgressBar bufferingIcon;
    ImageView ivSelectImages;
    int score = 0;
    RecyclerView rvSelectImages;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    DatabaseHandler handler;
    AdapterSelectImages adapterSelectImages;
    private final int REQUEST_GALLERY_PERMISSION = 5568;
    private static final int PERMISSION_REQUEST_CODE = 1;
    public static final int BARCODE_VALUE_REQUEST = 1232;
    private static final int PIN_REQUEST = 1234;
    String p1 = android.Manifest.permission.CAMERA, p2 = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    ArrayList<SelectImagesModel> myImages;
    LinearLayoutManager imagesManager;
    LinearLayout llViews;
    String id, form_name;
    TextView timer;
    public static int chekclass = 1;
    public static String equip_barcode = "";
    List<View> allViewInstance = new ArrayList<View>();
    public static CustomTextView value;
    int vehicleId = 0;
    int s = 0;
    int hours;
    ConstraintLayout vehicleDetail;
    TextView registration, tvColor;
    RecyclerView rvCarousel;
    ImageView leftArrow, rightArrow;
    private int numSelected = 0;
    List<ChecklistFormOption> list;
    private boolean networkAvailable;
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
    private HashMap<Integer, Integer> hashmap, hashmap3, hashMap5;
    private HashMap<Integer, Integer> hashmap2;
    ProgressBar progress;
    private HashMap<Integer, Arrows> hashmap4;
    private HashMap<Integer, Integer> rv2tvMap;
    private HashMap<Integer, List<ChecklistFormOption>> field2checkList;
    private HashMap<Integer, List<ChecklistFormOption>> toggleHash;
    private HashMap<Integer, List<Integer>> toggleHash2;
    TextView selectedTV;
    private View ltNoNetwork;
    private int currentImageViewId;
    private GPSTracker gps;
    BroadcastReceiver broadcastReceiver;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_equipment_check_list);
        hideHeyboard();

//        barcodeviews = (LinearLayout)findViewById(R.id.barcodeviews);
        getSupportActionBar().hide();
        gps = new GPSTracker(this);
        tinyDB = new TinyDB(getApplicationContext());
        subtitle = findViewById(R.id.subtitle);
//        hsvLayout = (HorizontalScrollView) findViewById(R.id.hsv);
//        barcodelayout = (LinearLayout)findViewById(R.id.barcodelayout);
//        ivSelectImages = findViewById(R.id.ivSelectImages);
//        rvSelectImages = findViewById(R.id.rvImages);
        bufferingIcon = findViewById(R.id.buffering_icon);
        timer = findViewById(R.id.timer);
        handler = new DatabaseHandler(EquipmentCheckList.this);
        myImages = new ArrayList<>();
        hashmap = new HashMap<>();
        hashmap2 = new HashMap<>();
        hashmap3 = new HashMap<>();
        hashmap4 = new HashMap<>();
        rv2tvMap = new HashMap<>();
        hashMap5 = new HashMap<>();
        toggleHash = new HashMap<>();
        field2checkList = new HashMap<>();
        toggleHash2 = new HashMap<>();
        vehicleId = tinyDB.getInt("vehicle_id");
        Log.d("ID", "vehicle id is: " + vehicleId);
        init();
//        broadcastReceiver = new NetworkReciever(ltNoNetwork);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!isConnected(context)) {
                    ltNoNetwork.setVisibility(View.VISIBLE);
                    bufferingIcon.setVisibility(View.GONE);
                } else {
                    ltNoNetwork.setVisibility(View.GONE);
                    if (vehicleDetail.getVisibility() != View.VISIBLE) {
                        bufferingIcon.setVisibility(View.VISIBLE);
                        hitEquipmentCheckList();
                    }
                }

            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        id = getIntent().getStringExtra("formid");
        form_name = getIntent().getStringExtra("formname");
        heading.setText(form_name);
        clickListeners();
//        hitEquipmentCheckList();
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
        checkPermission();
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


//        getLocation();
//        if (rvCarousel.computeHorizontalScrollRange() < rvCarousel.getWidth()){
//            rightArrow.setVisibility(View.GONE);
//        }

//        hitEquipmentCheckList();
    }


    public void hideHeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
//                        Toast.makeText(this, ""+wayLatitude, Toast.LENGTH_SHORT).getDialog();
//                        txtLocation.setText(String.format(Locale.US, "%s - %s", wayLatitude, wayLongitude));
//                        Toast.makeText(this, ""+wayLongitude, Toast.LENGTH_SHORT).getDialog();
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

            case PIN_REQUEST:
                if (data != null) {
                    CustomEditText editText = findViewById(data.getIntExtra("ViewId", 0));
                    editText.setText(data.getStringExtra("PinValue"));
                }
        }
        if (requestCode == BARCODE_VALUE_REQUEST) {
            if (data != null) {
                int id = data.getIntExtra("ImageViewId", 0);
                Log.d("ImageViewId", "onActivityResult: " + id);
                CustomEditText textView = findViewById(hashmap.get(id));
                textView.setText(data.getStringExtra("Value"));
            }
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

//                if (adapterSelectImages != null) {
//                    adapterSelectImages.setItems(myImages);
//                    adapterSelectImages.notifyDataSetChanged();
//                } else {
                adapterSelectImages = new AdapterSelectImages(myImages, EquipmentCheckList.this, ivSelectImages);
                rvSelectImages = findViewById(hashmap.get(currentImageViewId));
//                    rvSelectImages.setLayoutManager(imagesManager);
                rvSelectImages.setAdapter(adapterSelectImages);
//                }

            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText( getApplicationContext(), "Cancelled", Toast.LENGTH_SHORT ).getDialog();
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
        if (!isConnected(this)) {
            bufferingIcon.setVisibility(View.GONE);
        } else {
            if (vehicleDetail.getVisibility() != View.VISIBLE)
                bufferingIcon.setVisibility(View.VISIBLE);
        }
        if (SettingValues.getBarcodeVal().equals("")) {
            SettingValues.setBarcodeVal("Scan Barcode");

//            barcode_txt.setText(SettingValues.getBarcodeVal());
        } else {
//            barcodeviews.removeAllViews();
//            barcode_txt.setText(SettingValues.getBarcodeVal());
            for (int i = 0; i < SettingValues.getBarcodelist().size(); i++) {
                CustomTextView txt = new CustomTextView(this);
                HorizontalScrollView horizontalScrollView;
                horizontalScrollView = (HorizontalScrollView) findViewById(R.id.horizontal);
                horizontalScrollView.fullScroll(HorizontalScrollView.FOCUS_RIGHT);
                LinearLayout.LayoutParams txtperams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                txtperams.setMargins(10, 0, 10, 0);
                txt.setLayoutParams(txtperams);
                int val = i + 1;
                txt.setText("Value" + val + ": " + SettingValues.getBarcodelist().get(i) + ",");
                txt.setTextSize(14);
                txt.setGravity(Gravity.CENTER_VERTICAL);
//                barcodeviews.addView(txt);
            }
        }
    }


    private void init() {
        ivBack = findViewById(R.id.ivBack);
        tinyDB = new TinyDB(EquipmentCheckList.this);
        llViews = findViewById(R.id.llViews);
        btnSubmit = findViewById(R.id.btnLogin);
        vehicleDetail = findViewById(R.id.vehicle_detail);
        registration = findViewById(R.id.tvRegistrationNo);
        tvColor = findViewById(R.id.tvColor);
        heading = findViewById(R.id.heading);
        heading.setText(form_name);
        progress = findViewById(R.id.progress);
        ltNoNetwork = findViewById(R.id.ltNoNetwork);
    }

    private void clickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
//        barcodelayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(EquipmentCheckList.this,ScanActivity.class);
//                startActivity(i);
//            }
//        });


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
//            Toast.makeText(CreateJob.this, "All permission granted", Toast.LENGTH_LONG).getDialog();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            if (gps.canGetLocation()) {
                if (gps.getLatitude() != 0.0) {

                    wayLatitude = gps.getLatitude();
                    wayLongitude = gps.getLongitude();
                }
            }
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

    private void startClock() {
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

                                if (score == 60) {
                                    score = 0;
                                    hours++;

                                }
                                timer.setText(hours + " min" + "" + score + " sec");
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
        if (!((Activity) context).isFinishing()) {
            //getDialog dialog
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

        call = RetrofitClass.getInstance().getWebRequestsInstance().getEquipmentChecklist(tinyDB.getString(Constants.token), id);

        call.enqueue(new Callback<CheckListForms>() {
            @Override
            public void onResponse(retrofit2.Call<CheckListForms> call, final Response<CheckListForms> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        bufferingIcon.setVisibility(View.GONE);
                        Log.e("equip response", "Equp responseeeee" + response.body());

                        getFormDetail(response);
                    } else if (response.body().getStatus() == 404) {
                        Log.e("equip response", "fail Equp responseeeee" + response.body());
                    }
                } else if (response.code() == 401) {
                    bufferingIcon.setVisibility(View.GONE);
                    if (getApplicationContext() != null)
                        new SessionTimeoutDialog(EquipmentCheckList.this).getDialog().show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<CheckListForms> call, Throwable t) {
                Log.e("equip response", "fail Equp responseeeee" + t);
                t.printStackTrace();
            }
        });
    }


    @SuppressLint("NewApi")
    public void getFormDetail(final Response<CheckListForms> response) {

        final ChecklistForm obj = response.body().getChecklistForm();
        subtitle.setText(obj.getSubTitle());
        if (obj.getBarcodeEnabled().equals("yes")) {
            logoutConfirmDialogBox();
        } else {
            timer.setText("N/A");
        }
        btnSubmit.setVisibility(View.VISIBLE);
        Log.e("size", obj.toString());
        for (int position = 0; position < obj.getFields().size(); position++) {

            vehicleDetail.setVisibility(View.VISIBLE);

            if (obj.getFields().get(position).getType().equalsIgnoreCase("file")) {

                HorizontalScrollView hsvLayout = new HorizontalScrollView(this);
                hsvLayout.setFillViewport(true);
                hsvLayout.setVerticalScrollBarEnabled(false);
                hsvLayout.setHorizontalScrollBarEnabled(false);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(convertToDp(16, this), convertToDp(24, this), convertToDp(16, this), convertToDp(24, this));
//                params.gravity = Gravity.CENTER;
                hsvLayout.setLayoutParams(params);

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setGravity(Gravity.CENTER);
                HorizontalScrollView.LayoutParams params1 = new HorizontalScrollView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.setMarginEnd(convertToDp(10, this));
                params1.setMarginStart(convertToDp(10, this));
//                params1.gravity = Gravity.CENTER;
                relativeLayout.setLayoutParams(params1);

                ivSelectImages = new ImageView(this);
                ivSelectImages.setId(View.generateViewId());
                ivSelectImages.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.ic_add_pictures));
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(convertToDp(90, this), convertToDp(90, this));
                params2.setMargins(convertToDp(16, this), convertToDp(16, this), convertToDp(16, this), convertToDp(16, this));
                params2.addRule(RelativeLayout.CENTER_VERTICAL);
                ivSelectImages.setLayoutParams(params2);
                ivSelectImages.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        currentImageViewId = view.getId();
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
                rvSelectImages = new RecyclerView(this);
                rvSelectImages.setId(View.generateViewId());
                rvSelectImages.setNestedScrollingEnabled(false);
                params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.setMarginEnd(convertToDp(16, this));
                params2.addRule(RelativeLayout.RIGHT_OF, ivSelectImages.getId());
                rvSelectImages.setLayoutParams(params2);
                hashmap.put(ivSelectImages.getId(), rvSelectImages.getId());
                imagesManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
                rvSelectImages.setLayoutManager(imagesManager);
                relativeLayout.addView(ivSelectImages);
                relativeLayout.addView(rvSelectImages);
                hsvLayout.addView(relativeLayout);
                llViews.addView(hsvLayout);


                relativeLayout = new RelativeLayout(this);
//                relativeLayout.setGravity(RelativeLayout.CENTER_IN_PARENT);
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                relativeLayout.setLayoutParams(params);

                TextView textView = new TextView(this);
                textView.setId(View.generateViewId());
                textView.setText("Upload Image");
//                textView.setGravity(RelativeLayout.CENTER_HORIZONTAL);

                textView.setVisibility(View.GONE);
                params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(params2);
                params2.addRule(RelativeLayout.CENTER_IN_PARENT);
                relativeLayout.addView(textView);

                ImageView imageView = new ImageView(this);
                imageView.setId(View.generateViewId());
                imageView.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.red_astrik));
                imageView.setVisibility(View.GONE);
                params2 = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                params2.setMarginStart(convertToDp(16, this));
                params2.addRule(RelativeLayout.RIGHT_OF, textView.getId());
                imageView.setLayoutParams(params2);

                relativeLayout.addView(imageView);
                llViews.addView(relativeLayout);

                hsvLayout.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    imageView.setVisibility(View.VISIBLE);
                }

                allViewInstance.add(hsvLayout);
                hideHeyboard();

                Log.d("SIZE", "Image size is: " + myImages.size());
//                if (myImages.size() > 0) {
//                    if (adapterSelectImages != null) {
//                        adapterSelectImages.setItems(myImages);
//                        adapterSelectImages.notifyDataSetChanged();
//                    } else {
//                        adapterSelectImages = new AdapterSelectImages(myImages, EquipmentCheckList.this, ivSelectImages);
//                        rvSelectImages.setLayoutManager(imagesManager);
//                        rvSelectImages.setAdapter(adapterSelectImages);
//                    }
//                }
            }


            if (obj.getFields().get(position).getType().equalsIgnoreCase("text") && !(obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("Time")) && !(obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("signature"))) {
//                RelativeLayout ettxt_start = new RelativeLayout(this);
//                ettxt_start.setId(View.generateViewId());
//                CustomEditText etText = new CustomEditText(context);
//                etText.setId(View.generateViewId());
//                RelativeLayout.LayoutParams perams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                perams.setMargins(convertToDp(16), 4, convertToDp(16), 4);
////                etText.setLayoutParams(perams);
//                etText.setTextSize(12);
//                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
//                etText.setHint(obj.getFields().get(position).getPlaceholder());
////                RelativeLayout.LayoutParams etparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
////                etparams.setMargins(0, 0, 0, 0);
//                etText.setPadding(20, 0, 20, 0);
//                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
//                etText.setMinHeight(60);
//                etText.setMinimumHeight(60);
//                etText.setSingleLine();
//
//
//
//                RelativeLayout.LayoutParams labelparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                labelparams.setMargins(convertToDp(16), 0, convertToDp(16), 4);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                CustomTextView labeltext = new CustomTextView(this);
//                labeltext.setText(obj.getFields().get(position).getLabel());
//                labeltext.setId(View.generateViewId());
//                labeltext.setTextColor(Color.parseColor("#616060"));
//                labeltext.setTextSize(14);
//                labeltext.setLayoutParams(labelparams);
//
//                perams.addRule(RelativeLayout.BELOW,labeltext.getId());
//                etText.setLayoutParams(perams);
////                llViews.addView(labeltext);
//
//
//                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                startparams.setMargins(10, 10, 4, 4);
//                ImageView star = new ImageView(this);
//                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
//                star.setLayoutParams(startparams);
//                star.setVisibility(View.GONE);
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//
//                ettxt_start.addView(labeltext);
//                ettxt_start.addView(etText);
////                ettxt_start.addView(star, layoutParams);
//                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
////                    star.setVisibility(View.VISIBLE);
//                    ettxt_start.setBackground(getDrawable(R.drawable.required));
//                }
//
//
//
//                allViewInstance.add(etText);
//
//                View view = new View(context);
//                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
//                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                p.setMargins(0, 16, 0, 16);
//                p.addRule(RelativeLayout.BELOW,etText.getId());
//                view.setLayoutParams(p);
//
////                llViews.addView(view);
//                ettxt_start.addView(view);
//                llViews.addView(ettxt_start);
//                hashMap5.put(obj.getFields().get(position).getFieldId(),ettxt_start.getId());
////                if (obj.getFields().get(position).getFieldId()==8692){
////                    ettxt_start.setVisibility(View.GONE);
////                }
                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setId(View.generateViewId());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(convertToDp(16, this), convertToDp(8, this), convertToDp(16, this), convertToDp(8, this));
                relativeLayout.setLayoutParams(params);

                EditText textView = new CustomEditText(this);
                if (obj.getFields().get(position).getLabel().contains("PIN")) {
                    textView.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(EquipmentCheckList.this, PINVerificationActivity.class);
                            intent.putExtra("ViewId", textView.getId());
                            startActivityForResult(intent, PIN_REQUEST);
                        }
                    });
//                    textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                        @Override
//                        public void onFocusChange(View v, boolean hasFocus) {
//                            if (hasFocus) {
//
//                                Intent intent = new Intent(EquipmentCheckList.this, PINVerificationActivity.class);
//                                intent.putExtra("ViewId", textView.getId());
//                                startActivityForResult(intent, PIN_REQUEST);
//                            }
//                        }
//                    });
                }
                textView.setBackground(null);
                textView.setId(View.generateViewId());
                textView.setTextSize(COMPLEX_UNIT_SP, 14);
                textView.setHint(obj.getFields().get(position).getLabel());
                textView.setPadding(convertToDp(8, this), convertToDp(4, this), convertToDp(8, this), convertToDp(4, this));
                RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                ImageView ivBarcode = new ImageView(this);
                ivBarcode.setVisibility(View.GONE);
                ivBarcode.setId(View.generateViewId());
                ivBarcode.setImageDrawable(getDrawable(R.drawable.ic_bars_code));
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(convertToDp(30,this),convertToDp(30,this));
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params2.addRule(RelativeLayout.CENTER_IN_PARENT);
                params2.setMargins(0, convertToDp(4, this), 0, convertToDp(4, this));
                ivBarcode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EquipmentCheckList.this, ScanActivity.class);
                        intent.putExtra("ImageViewId", v.getId());
                        Log.d("ImageViewId", "onClick: " + v.getId());
                        startActivityForResult(intent, BARCODE_VALUE_REQUEST);
                    }
                });
                removeView(ivBarcode);
                if (obj.getFields().get(position).isBarcode())
                    ivBarcode.setVisibility(View.VISIBLE);

                tvParams.addRule(RelativeLayout.LEFT_OF, ivBarcode.getId());
                relativeLayout.addView(textView, tvParams);
                relativeLayout.addView(ivBarcode, params2);

                ImageView imageView = new ImageView(this);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                imageView.setVisibility(View.GONE);
                tvParams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                tvParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                tvParams.setMargins(0, 0, 0, convertToDp(24, this));
                relativeLayout.addView(imageView, tvParams);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    imageView.setVisibility(View.VISIBLE);
                }

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.addRule(RelativeLayout.BELOW, textView.getId());
                relativeLayout.addView(view, p);

                allViewInstance.add(textView);
                llViews.addView(relativeLayout);
                hashMap5.put(obj.getFields().get(position).getFieldId(), relativeLayout.getId());
                hashmap.put(ivBarcode.getId(), textView.getId());
                hideHeyboard();


            } else if (obj.getFields().get(position).getType().equalsIgnoreCase("number")) {

                ChecklistFormField field = obj.getFields().get(position);

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setId(View.generateViewId());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(convertToDp(16, this), convertToDp(8, this), convertToDp(16, this), convertToDp(8, this));
                relativeLayout.setLayoutParams(params);

                EditText editText = new CustomEditText(this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setId(View.generateViewId());
                editText.setPadding(convertToDp(8, this), convertToDp(4, this), convertToDp(8, this), convertToDp(4, this));
                editText.setBackground(null);
                editText.setHint(field.getLabel());
                editText.setTextSize(COMPLEX_UNIT_SP, 14);
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                ImageView ivBarcode = new ImageView(this);
                ivBarcode.setVisibility(View.GONE);
                ivBarcode.setId(View.generateViewId());
                ivBarcode.setImageDrawable(getDrawable(R.drawable.ic_bars_code));
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(convertToDp(30,this),convertToDp(30,this));
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params2.addRule(RelativeLayout.CENTER_IN_PARENT);
                params2.setMargins(0, convertToDp(4, this), 0, 0);
                ivBarcode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EquipmentCheckList.this, ScanActivity.class);
                        intent.putExtra("ImageViewId", v.getId());
                        Log.d("ImageViewId", "onClick: " + v.getId());
                        startActivityForResult(intent, BARCODE_VALUE_REQUEST);
                    }
                });
                removeView(ivBarcode);
                if (field.isBarcode())
                    ivBarcode.setVisibility(View.VISIBLE);
                if (obj.getFields().get(position).getLabel().contains("PIN")) {
                    editText.setInputType(InputType.TYPE_NULL);
                    editText.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(EquipmentCheckList.this, PINVerificationActivity.class);
                            intent.putExtra("ViewId", editText.getId());
                            startActivityForResult(intent, PIN_REQUEST);
                        }
                    });
//                    editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                        @Override
//                        public void onFocusChange(View v, boolean hasFocus) {
//                            if (hasFocus) {
//
//                                Intent intent = new Intent(EquipmentCheckList.this, PINVerificationActivity.class);
//                                intent.putExtra("ViewId", editText.getId());
//                                startActivityForResult(intent, PIN_REQUEST);
//                            }
//                        }
//                    });
                }
                ImageView ivStar = new ImageView(this);
                ivStar.setId(View.generateViewId());
                ivStar.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                ivStar.setVisibility(View.GONE);
                RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                tvParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                tvParams.setMargins(0, 0, 0, convertToDp(16, this));
//                tvParams.addRule(RelativeLayout.ABOVE, ;ivBarcode.getId());
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    ivStar.setVisibility(View.VISIBLE);
                }
                params1.addRule(RelativeLayout.LEFT_OF, ivBarcode.getId());

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.addRule(RelativeLayout.BELOW, editText.getId());


                relativeLayout.addView(editText, params1);
                relativeLayout.addView(ivBarcode, params2);
                relativeLayout.addView(ivStar, tvParams);
                relativeLayout.addView(view, p);
                llViews.addView(relativeLayout);

                allViewInstance.add(editText);
                hashMap5.put(obj.getFields().get(position).getFieldId(), relativeLayout.getId());
                hashmap.put(ivBarcode.getId(), editText.getId());
                hideHeyboard();


//                CustomEditText barcodeValue = new CustomEditText(context);
//                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                perams.setMargins(convertToDp(16), convertToDp(4), convertToDp(16), convertToDp(4));
//                relativeLayout.setLayoutParams(perams);
//                RelativeLayout.LayoutParams etparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                etparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
////                etparams.setMargins(convertToDp(16),convertToDp(4), convertToDp(16), convertToDp(4));
//
//                barcodeValue.setId(View.generateViewId());
//
//
//                RelativeLayout.LayoutParams labelparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
////                labelparams.setMargins(convertToDp(16), convertToDp(4), convertToDp(16), convertToDp(4));// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                CustomTextView labeltext = new CustomTextView(this);
//                labeltext.setId(View.generateViewId());
//                labeltext.setText(obj.getFields().get(position).getLabel());
//                labeltext.setTextColor(Color.parseColor("#616060"));
//                labeltext.setTextSize(14);
//                labeltext.setLayoutParams(labelparams);
////                llViews.addView(labeltext);
//                relativeLayout.addView(labeltext);
//                etparams.addRule(RelativeLayout.BELOW,labeltext.getId());
//                barcodeValue.setTextSize(12);
//                barcodeValue.setTextColor(getResources().getColor(R.color.colorPrimary));
//                barcodeValue.setHint(obj.getFields().get(position).getPlaceholder());
//                barcodeValue.setId(View.generateViewId());
//                barcodeValue.setPadding(20, 8, 20, 8);
//                barcodeValue.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
//                barcodeValue.setMinHeight(60);
//                barcodeValue.setMinimumHeight(60);
//                barcodeValue.setSingleLine();
//                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                startparams.setMargins(10, 10, 4, 4);
//                ImageView star = new ImageView(this);
//                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
//                star.setLayoutParams(startparams);
//                star.setVisibility(View.VISIBLE);
//                star.setVisibility(View.GONE);
//                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
////                    star.setVisibility(View.VISIBLE);
//                    relativeLayout.setBackground(getDrawable(R.drawable.required));
//                }
//
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//
//                relativeLayout.addView(barcodeValue);
////                relativeLayout.addView(star, layoutParams);
//
//
//                View view = new View(context);
//                view.setId(View.generateViewId());
//                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
//                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                p.setMargins(0, 24, 0, 24);
//                p.addRule(RelativeLayout.BELOW,barcodeValue.getId());
//                view.setLayoutParams(p);
//                relativeLayout.addView(view);
//
//                if (obj.getTitle().equalsIgnoreCase("Equipment Checklist")) {
//                    ImageView imageView = new ImageView(this);
//                    imageView.setId(View.generateViewId());
//                    imageView.setImageDrawable(getDrawable(R.drawable.ic_bars_code));
//                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(60, 60);
//                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                    params.addRule(RelativeLayout.BELOW,labeltext.getId());
//                    etparams.addRule(RelativeLayout.LEFT_OF,imageView.getId());
////                    params.addRule(RelativeLayout.CENTER_IN_PARENT);
////                    params.rightMargin = convertToDp(16);
//                    imageView.setLayoutParams(params);
//                    hashmap.put(imageView.getId(), barcodeValue.getId());
//                    imageView.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Intent intent = new Intent(EquipmentCheckList.this, ScanActivity.class);
//                            intent.putExtra("ImageViewId", v.getId());
//                            Log.d("ImageViewId", "onClick: " + v.getId());
//                            startActivityForResult(intent, 2);
//                        }
//                    });
//                    if (imageView.getParent() != null) {
//                        ((ViewGroup) imageView.getParent()).removeView(imageView); // <- fix
//                    }
//
//                    relativeLayout.addView(imageView);
//                }
//                barcodeValue.setLayoutParams(etparams);
//                llViews.addView(relativeLayout);
//
//
//                allViewInstance.add(barcodeValue);
//                allViewInstance.add(relativeLayout);
//                allViewInstance.add(view);
//
////                if (obj.getFields().get(position).getFieldId()==8286||obj.getFields().get(position).getFieldId()==8288||obj.getFields().get(position).getFieldId()==8289||obj.getFields().get(position).getFieldId()==8290||obj.getFields().get(position).getFieldId()==8292||obj.getFields().get(position).getFieldId()==8294){
////                    relativeLayout.setVisibility(View.GONE);
////                }
////                if (obj.getFields().get(position - 1).getLabel().equalsIgnoreCase("Meds bag sealed")) {
////                    SwitchViews switchViews = new SwitchViews();
////                    switchViews.setLabelId(barcodeValue.getId());
////                    switchViews.setRelativeLayoutId(relativeLayout.getId());
////                    switchViews.setViewId(view.getId());
////                    toggleHash.put("Meds bag sealed", switchViews);
////                }
////                llViews.addView(view);
//                hashMap5.put(obj.getFields().get(position).getFieldId(),relativeLayout.getId());

            } else if (obj.getFields().get(position).getType().equals("checkbox-group")) {
//                setUpEquipmentList();
                list = obj.getFields().get(position).getOptions();
                field2checkList.put(obj.getFields().get(position).getFieldId(), list);
                RelativeLayout relativeLayout = new RelativeLayout(this);
                TextView tvCheckBoxTitle = new CustomTextView(context);
                tvCheckBoxTitle.setText(obj.getFields().get(position).getLabel());
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                relativeLayout.setLayoutParams(perams);
                RelativeLayout.LayoutParams perams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                perams.setMargins(20, 20, 20, 20);
                perams1.setMargins(convertToDp(16, this), convertToDp(8, this), convertToDp(8, this), convertToDp(8, this));
                tvCheckBoxTitle.setId(View.generateViewId());
                if ((obj.getFields().get(position - 1).getType().equalsIgnoreCase("header") && obj.getFields().get(position + 1).getType().equalsIgnoreCase("header")) || (obj.getFields().get(position - 1).getType().equalsIgnoreCase("header") && obj.getFields().get(position + 1).getType().equalsIgnoreCase("textarea"))) {
                    if (obj.getTitle().equalsIgnoreCase("Equipment checkList")) {
//                        relativeLayout.setGravity(Gravity.CENTER);
                        tvCheckBoxTitle.setTextColor(getResources().getColor(R.color.colorPrimary));
                        tvCheckBoxTitle.setTextSize(COMPLEX_UNIT_SP, 16);
                    }

                } else {
                    tvCheckBoxTitle.setTextColor(Color.parseColor("#616060"));
                    tvCheckBoxTitle.setTextSize(COMPLEX_UNIT_SP, 14);

                    tvCheckBoxTitle.setText(obj.getFields().get(position).getLabel());
                }
//                llViews.addView(tvCheckBoxTitle);
                RelativeLayout.LayoutParams startparams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                    perams.setMargins(4, 10, 4, 4);
                startparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    star.setVisibility(View.VISIBLE);
//                        tvCheckBoxTitle.setTextColor(Color.RED);
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                tvCheckBoxTitle.setLayoutParams(perams1);
                relativeLayout.addView(tvCheckBoxTitle);
                relativeLayout.addView(star);
                selectedTV = new TextView(this);
                selectedTV.setId(View.generateViewId());
                selectedTV.setTextSize(COMPLEX_UNIT_SP, 14);
                if ((obj.getFields().get(position - 1).getType().equalsIgnoreCase("header") && obj.getFields().get(position + 1).getType().equalsIgnoreCase("header")) || (obj.getFields().get(position - 1).getType().equalsIgnoreCase("header") && obj.getFields().get(position + 1).getType().equalsIgnoreCase("textarea"))) {
                    if (obj.getTitle().equalsIgnoreCase("Equipment checkList")) {
                        selectedTV.setTextSize(COMPLEX_UNIT_SP, 16);
                    }

                }
                RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //or MATCH_PARENT
                tvParams.addRule(RelativeLayout.RIGHT_OF, tvCheckBoxTitle.getId());
//                tvParams.setMargins(16, 0, 0, 0);
//                tvParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                tvParams.setMargins(0, convertToDp(8, this), 0, convertToDp(8, this));

                selectedTV.setLayoutParams(tvParams);
                selectedTV.setTextColor(Color.parseColor("#79C730"));
                selectedTV.setText(numSelected + "/" + list.size()); /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                relativeLayout.addView(selectedTV);
//                relativeLayout.addView(star, layoutParams);
                if ((obj.getFields().get(position - 1).getType().equalsIgnoreCase("header") && obj.getFields().get(position + 1).getType().equalsIgnoreCase("header")) || (obj.getFields().get(position - 1).getType().equalsIgnoreCase("header") && obj.getFields().get(position + 1).getType().equalsIgnoreCase("textarea"))) {
                    if (obj.getTitle().equalsIgnoreCase("Equipment checkList")) {
                        relativeLayout.setGravity(Gravity.CENTER);
                        perams.setMargins(convertToDp(24, this), 0, convertToDp(16, this), 0);
                        relativeLayout.setLayoutParams(perams);
                    }

                }
                llViews.addView(relativeLayout);

                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.VERTICAL);

                RelativeLayout relativeLayout1 = new RelativeLayout(this);
                relativeLayout1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//
//
                leftArrow = new ImageView(this);
                leftArrow.setId(View.generateViewId());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                params.setMargins(16, 0, 16, 0);
                leftArrow.setLayoutParams(params);
                leftArrow.setPadding(convertToDp(16, this), 0, 0, 0);
                leftArrow.setImageDrawable(getDrawable(R.drawable.ic_arrow_l));
                leftArrow.setVisibility(View.INVISIBLE);


                rightArrow = new ImageView(this);
                rightArrow.setId(View.generateViewId());
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                params.setMargins(16, 0, 16, 0);
                rightArrow.setLayoutParams(params);
                rightArrow.setImageDrawable(getDrawable(R.drawable.ic_slide_r));
                rightArrow.setPadding(0, 0, convertToDp(16, this), 0);

                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.RIGHT_OF, leftArrow.getId());
                params.addRule(RelativeLayout.LEFT_OF, rightArrow.getId());
                params.setMargins(8, convertToDp(8, this), 8, convertToDp(8, this));
                linearLayout.setLayoutParams(params);

                rvCarousel = new RecyclerView(this);
                rvCarousel.setId(View.generateViewId());
                LinearLayout.LayoutParams rvParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                rvParams.gravity = Gravity.CENTER_HORIZONTAL;
                rvCarousel.setLayoutParams(rvParams);
                linearLayout.addView(rvCarousel);

                relativeLayout1.addView(leftArrow);
                relativeLayout1.addView(linearLayout);
                relativeLayout1.addView(rightArrow);
                hashmap2.put(leftArrow.getId(), rvCarousel.getId());
                hashmap3.put(rightArrow.getId(), rvCarousel.getId());
                hashMap5.put(rightArrow.getId(), list.size());
                Arrows arrows = new Arrows();
                arrows.setLeftArrow(leftArrow.getId());
                arrows.setRightArrow(rightArrow.getId());
                arrows.setListSize(list.size());
                hashmap4.put(rvCarousel.getId(), arrows);
                setUpCarousel();
                rv2tvMap.put(rvCarousel.getId(), selectedTV.getId());
                hashMap5.put(rvCarousel.getId(), obj.getFields().get(position).getFieldId());
//                for (int i = 0; i < obj.getFields().get(position).getOptions().size(); i++) {
//                    AppCompatCheckBox checkBox = new AppCompatCheckBox(context);
//                    LinearLayout.LayoutParams cbPerams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                    cbPerams.setMargins(8, 8, 8, 8);
//                    checkBox.setText(obj.getFields().get(position).getOptions().get(i).getLabel());
//                    checkBox.setLayoutParams(cbPerams);
//                    Typeface font = Typeface.createFromAsset(getAssets(), "ProductSans-Medium.ttf");
//                    checkBox.setTypeface(font);
//                    checkBox.setTextColor(Color.parseColor("#616060"));
//                    checkBox.setTextSize(14);
//
//                    checkBox.setTag(obj.getFields().get(position).getOptions().get(i).getId() + "," + obj.getFields().get(position).getOptions().get(i).getLabel());
//
//                    ll.addView(checkBox);
//                }
                removeView(relativeLayout1);
                llViews.addView(relativeLayout1);


                allViewInstance.add(ll);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(convertToDp(16, this), 16, convertToDp(16, this), 16);
                view.setLayoutParams(p);
//                if (obj.getFields().get(position).getFieldId() == 8303 || obj.getFields().get(position).getFieldId() == 8299)
                llViews.addView(view);
                hideHeyboard();

            } else if (obj.getFields().get(position).getType().equals("radio-group")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                TextView tvRadioButtonTitle = new CustomTextView(context);
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());

                LinearLayout.LayoutParams perams1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                perams1.setMargins(convertToDp(16, this), convertToDp(16, this), convertToDp(16, this), convertToDp(16, this));

                RelativeLayout.LayoutParams perams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                tvRadioButtonTitle.setTextSize(12);

//                perams.setMargins(convertToDp(16),convertToDp(4), convertToDp(16), convertToDp(4));
                tvRadioButtonTitle.setLayoutParams(perams);
                relativeLayout.setLayoutParams(perams1);
                tvRadioButtonTitle.setLayoutParams(perams);
//                tvRadioButtonTitle.setTextSize(14);
//                tvRadioButtonTitle.setTypeface(tvRadioButtonTitle.getTypeface(), Typeface.NORMAL);
                tvRadioButtonTitle.setTextColor(Color.parseColor("#616060"));
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
//                llViews.addView(tvRadioButtonTitle);

                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                perams.setMargins(4, 10, 4, 4);

                ImageView star = new ImageView(this);
                star.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
//                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    star.setVisibility(View.VISIBLE);
//                    tvRadioButtonTitle.setTextColor(Color.RED);

                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                Switch toggle = new Switch(this);
                toggle.setId(View.generateViewId());
                int[][] states = new int[][]{
                        new int[]{-android.R.attr.state_checked},
                        new int[]{android.R.attr.state_checked},
                };

                int[] thumbColors = new int[]{
                        Color.GRAY,
                        Color.parseColor("#79C730"),
                };

                int[] trackColors = new int[]{
                        Color.GRAY,
                        Color.parseColor("#79C730"),
                };


                DrawableCompat.setTintList(DrawableCompat.wrap(toggle.getThumbDrawable()), new ColorStateList(states, thumbColors));
                DrawableCompat.setTintList(DrawableCompat.wrap(toggle.getTrackDrawable()), new ColorStateList(states, trackColors));
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); //or MATCH_PARENT
                layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layoutParams1.addRule(RelativeLayout.CENTER_IN_PARENT);
//                layoutParams1.setMarginEnd(convertToDp(4));
                relativeLayout.addView(tvRadioButtonTitle);
                relativeLayout.addView(toggle, layoutParams1);
//                relativeLayout.addView(star, layoutParams);
                llViews.addView(relativeLayout);


//                RadioGroup rg = new RadioGroup(context);
//                rg.setTag(obj.getFields().get(position).getFieldId() + "");
//
//                for (int i = 0; i < obj.getFields().get(position).getOptions().size(); i++) {
//                    RadioButton rb = new RadioButton(context);
//                    rb.setText(obj.getFields().get(position).getOptions().get(i).getLabel());
//                    Typeface font = Typeface.createFromAsset(getAssets(), "ProductSans-Medium.ttf");
//                    rb.setTypeface(font);
//                    rb.setTextSize(14);
//                    rb.setTextColor(Color.parseColor("#616060"));
//                    rg.addView(rb);
//
//                    rb.setTag(obj.getFields().get(position).getFieldId() + "," + obj.getFields().get(position).getOptions().get(i).getId() + "," + obj.getFields().get(position).getOptions().get(i).getLabel());
//                }
//
//                rg.setLayoutParams(perams);
//
//                llViews.addView(rg);
//                allViewInstance.add(rg);
                List<ChecklistFormOption> options = obj.getFields().get(position).getOptions();
                toggleHash.put(toggle.getId(), options);
                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);
                List<Integer> list = new ArrayList<>();
                for (int i = position; i < obj.getFields().size() - 1; i++) {
                    if (obj.getFields().get(i + 1).getType().equalsIgnoreCase("number")) {
                        list.add(obj.getFields().get(i + 1).getFieldId());
                    } else if (obj.getTitle().equalsIgnoreCase("Adverse Incident / Near Miss Form") && obj.getFields().get(i + 1).getType().equalsIgnoreCase("text")) {
                        list.add(obj.getFields().get(i + 1).getFieldId());
                        break;
                    } else
                        break;

                }

                toggleHash2.put(toggle.getId(), list);
//                llViews.addView(view);

                int finalPosition = position;
                toggle.setChecked(true);
                hashMap5.put(toggle.getId(), obj.getFields().get(finalPosition).getFieldId());
                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            for (int i : toggleHash2.get(buttonView.getId())) {
                                findViewById(hashMap5.get(i)).setVisibility(View.VISIBLE);
                            }

                        } else {
                            for (int i : toggleHash2.get(buttonView.getId())) {
                                findViewById(hashMap5.get(i)).setVisibility(View.GONE);
                            }
                        }
                    }

                });
                allViewInstance.add(toggle);
                hideHeyboard();

            } else if (obj.getFields().get(position).getType().equals("textarea")) {
//                RelativeLayout relativeLayout = new RelativeLayout(this);
//                CustomEditText etTextArea = new CustomEditText(context);
//                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//
////                perams.setMargins(4, 0, 8, 0);
//                perams.setMargins(4, 4, 4, 4);
//                relativeLayout.setLayoutParams(perams);
//                etTextArea.setPadding(20, 10, 20, 10);
//                etTextArea.setHint(obj.getFields().get(position).getPlaceholder());
//
//
//                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                labelparams.setMargins(convertToDp(16), 0, convertToDp(16), 0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                CustomTextView labeltext = new CustomTextView(this);
//                labeltext.setId(View.generateViewId());
//                labeltext.setText(obj.getFields().get(position).getLabel());
//                labeltext.setTextColor(Color.parseColor("#616060"));
//                labeltext.setTextSize(14);
//                labeltext.setLayoutParams(labelparams);
//                llViews.addView(labeltext);
//
//                etTextArea.setGravity(Gravity.TOP);
//                etTextArea.setTextColor(getResources().getColor(R.color.colorPrimary));
//                etTextArea.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
//                etTextArea.setMinHeight(200);
//                etTextArea.setMinimumHeight(200);
//                etTextArea.setTextSize(12);
////                etTextArea.setLayoutParams(perams);
////                llViews.addView(etTextArea);
//
//                RelativeLayout.LayoutParams prams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                prams.setMargins(convertToDp(16), 0, convertToDp(16), 0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                etTextArea.setLayoutParams(prams);
//
//                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(20, 20); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                startparams.setMargins(4, 10, 4, 4);
//                ImageView star = new ImageView(this);
//                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
//                star.setLayoutParams(startparams);
//                star.setVisibility(View.VISIBLE);
//                star.setVisibility(View.GONE);
//                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
////                    star.setVisibility(View.VISIBLE);
//                    etTextArea.setBackground(getDrawable(R.drawable.required));
//                }
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//
//
//                relativeLayout.addView(etTextArea);
//                relativeLayout.addView(star, layoutParams);
//                llViews.addView(relativeLayout);
//
//
//                View view = new View(context);
//                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
//                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                p.setMargins(0, 16, 0, 16);
//                view.setLayoutParams(p);
//                allViewInstance.add(etTextArea);
//
//                llViews.addView(view);

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setId(View.generateViewId());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(convertToDp(16, this), convertToDp(8, this), convertToDp(16, this), convertToDp(8, this));
                relativeLayout.setLayoutParams(params);

                EditText textView = new CustomEditText(this);
                textView.setBackground(null);
                textView.setId(View.generateViewId());
                textView.setTextSize(COMPLEX_UNIT_SP, 14);
                textView.setHint(obj.getFields().get(position).getLabel());
                textView.setPadding(convertToDp(8, this), convertToDp(4, this), convertToDp(8, this), convertToDp(4, this));
                RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                relativeLayout.addView(textView, tvParams);

                ImageView ivBarcode = new ImageView(this);
                ivBarcode.setVisibility(View.GONE);
                ivBarcode.setId(View.generateViewId());
                ivBarcode.setImageDrawable(getDrawable(R.drawable.ic_bars_code));
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(60, 60);
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params2.addRule(RelativeLayout.CENTER_IN_PARENT);
                params2.setMargins(0, convertToDp(4, this), 0, 0);
                ivBarcode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(EquipmentCheckList.this, ScanActivity.class);
                        intent.putExtra("ImageViewId", v.getId());
                        Log.d("ImageViewId", "onClick: " + v.getId());
                        startActivityForResult(intent, BARCODE_VALUE_REQUEST);
                    }
                });
                removeView(ivBarcode);
                if (obj.getFields().get(position).isBarcode())
                    ivBarcode.setVisibility(View.VISIBLE);

                tvParams.addRule(RelativeLayout.LEFT_OF, ivBarcode.getId());
                removeView(textView);
                relativeLayout.addView(textView, tvParams);
                relativeLayout.addView(ivBarcode, params2);

                ImageView imageView = new ImageView(this);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                imageView.setVisibility(View.GONE);
                tvParams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                tvParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                tvParams.setMargins(0, 0, 0, convertToDp(16, this));
//                tvParams.addRule(RelativeLayout.ABOVE, ;ivBarcode.getId());//                tvParams.addRule(RelativeLayout.ABOVE, textView.getId());
                relativeLayout.addView(imageView, tvParams);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    imageView.setVisibility(View.VISIBLE);
                }

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.addRule(RelativeLayout.BELOW, textView.getId());
                relativeLayout.addView(view, p);

                allViewInstance.add(textView);
                llViews.addView(relativeLayout);
                hideHeyboard();

            } else if (obj.getFields().get(position).getType().equals("select")) {
                RelativeLayout relativeLayout = new RelativeLayout(this);
                CustomTextView tvRadioButtonTitle = new CustomTextView(context);
                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                tvRadioButtonTitle.setLayoutParams(perams);
                perams.setMargins(convertToDp(16, this), convertToDp(4, this), convertToDp(16, this), convertToDp(4, this));
                tvRadioButtonTitle.setTextSize(12);

//                perams.setMargins(20, 20, 20, 20);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                relativeLayout.setLayoutParams(perams);
                tvRadioButtonTitle.setLayoutParams(params);
                tvRadioButtonTitle.setTextSize(14);

                tvRadioButtonTitle.setText(obj.getFields().get(position).getLabel());
//                llViews.addView(tvRadioButtonTitle);
                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this)); // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                perams.setMargins(4, 10, 4, 4);

                ImageView star = new ImageView(this);
                startparams.setMarginEnd(convertToDp(16, this));
                startparams.gravity = Gravity.END;
                star.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                llViews.addView(star);
                star.setVisibility(View.VISIBLE);
//                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    star.setVisibility(View.VISIBLE);
                    tvRadioButtonTitle.setTextColor(Color.RED);
                }

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);


//                relativeLayout.addView(tvRadioButtonTitle);
//                relativeLayout.addView(star, layoutParams);
//                llViews.addView(relativeLayout);

                AppCompatSpinner spinner = new AppCompatSpinner(context);
//                spinner.setBackgroundColor(getColor(R.color.colorSlightGray));
                List<ChecklistFormOption> list = obj.getFields().get(position).getOptions();
                ChecklistFormOption option = new ChecklistFormOption();
                option.setLabel(obj.getFields().get(position).getLabel());
                list.add(option);
                EqipmentCheckListSpinnerArrayAdapter adapter = new EqipmentCheckListSpinnerArrayAdapter(context,
                        R.layout.custom_spinner_item, list);
                spinner.setAdapter(adapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                    }

                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                star.setLayoutParams(startparams);

                spinner.setSelection(adapter.getCount());
                spinner.setPadding(convertToDp(2, this), 0, 0, 0);
                RelativeLayout.LayoutParams apinnerParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                apinnerParams.setMargins(0, 0, 0, convertToDp(16, this));
                apinnerParams.addRule(RelativeLayout.LEFT_OF, star.getId());
                spinner.setLayoutParams(apinnerParams);

                removeView(relativeLayout);
//                relativeLayout.addView(spinner);
                llViews.addView(spinner);

                allViewInstance.add(spinner);

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.setMargins(0, 16, 0, 16);
                view.setLayoutParams(p);
                hideHeyboard();

//                llViews.addView(view);


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
                labelparams.setMargins(10, 0, 0, 0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
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
                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                startparams.setMargins(20, 10, 20, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
//                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    star.setVisibility(View.VISIBLE);
                    relativeLayout.setBackground(getDrawable(R.drawable.required));
                }
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(20, 20); //or MATCH_PARENT
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

                                if (selectedHour < 10 && selectedMinute < 10) {
                                    etText.setText("  " + "0" + selectedHour + " : " + "0" + selectedMinute);
                                } else if (selectedHour < 10) {
                                    etText.setText("  " + "0" + selectedHour + " : " + selectedMinute);
                                } else if (selectedMinute < 10) {
                                    etText.setText("  " + selectedHour + " : " + "0" + selectedMinute);
                                } else {
                                    etText.setText("  " + selectedHour + " : " + selectedMinute);
                                }
                            }
                        }, hour, minute, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();

                    }
                });
                hideHeyboard();

            } else if (obj.getFields().get(position).getType().equals("date")) {
//                RelativeLayout relativeLayout = new RelativeLayout(this);
//                final String lable = obj.getFields().get(position).getLabel();
//                final CustomTextView etText = new CustomTextView(context);
//                LinearLayout.LayoutParams perams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                perams.setMargins(convertToDp(16, this), 4, convertToDp(16, this), 4);
//                relativeLayout.setLayoutParams(perams);
//                etText.setTextSize(12);
//                etText.setTextColor(getResources().getColor(R.color.colorPrimary));
//                etText.setHint(obj.getFields().get(position).getPlaceholder());
//
//
//                LinearLayout.LayoutParams labelparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                labelparams.setMargins(convertToDp(16, this), 0, convertToDp(16, this), 0);// Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                CustomTextView labeltext = new CustomTextView(this);
//                labeltext.setText(obj.getFields().get(position).getLabel());
//                labeltext.setTextColor(Color.parseColor("#616060"));
//                labeltext.setTextSize(14);
//                labeltext.setLayoutParams(labelparams);
//                llViews.addView(labeltext);
//                RelativeLayout.LayoutParams startparams = new RelativeLayout.LayoutParams(20, 20);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                startparams.setMargins(4, 10, 4, 4);
//                startparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                ImageView star = new ImageView(this);
//                star.setImageDrawable(getDrawable(R.drawable.red_astrik));
//                star.setLayoutParams(startparams);
//                star.setVisibility(View.GONE);
////                star.setVisibility(View.GONE);
//                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
//                    star.setVisibility(View.VISIBLE);
////                    relativeLayout.setBackground(getDrawable(R.drawable.required));
//                }
//                RelativeLayout.LayoutParams prams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                etText.setLayoutParams(prams);
//                prams.addRule(RelativeLayout.RIGHT_OF, star.getId());
//                etText.setPadding(20, 0, 20, 0);
//                etText.setBackground(getResources().getDrawable(R.drawable.btn_background_gray_color));
//                etText.setMinHeight(60);
//                etText.setMinimumHeight(60);
//                etText.setGravity(Gravity.CENTER_VERTICAL);
//                etText.setSingleLine();
//
//
//                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(30, 30); //or MATCH_PARENT
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//
//
//                relativeLayout.addView(etText);
//                relativeLayout.addView(star, layoutParams);
//                llViews.addView(relativeLayout);
//                allViewInstance.add(etText);
//
//                View view = new View(context);
//                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
//                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                p.setMargins(0, 16, 0, 16);
//                view.setLayoutParams(p);
//
//                llViews.addView(view);
//
////                llViews.addView(etText);
////
////                allViewInstance.add(etText);
////
////                View view = new View(context);
////                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
////                LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
////                p.setMargins(0, 24, 0, 24);
////                view.setLayoutParams(p);
////
////                llViews.addView(view);
//
//
//                final Calendar myCalendar = Calendar.getInstance();
//
//                final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
//
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int monthOfYear,
//                                          int dayOfMonth) {
//                        // TODO Auto-generated method stub
//                        myCalendar.set(Calendar.YEAR, year);
//                        myCalendar.set(Calendar.MONTH, monthOfYear);
//                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//
//                        String myFormat = " MM - dd - yyyy"; //In which you need put here
//                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
//
//                        etText.setText(" " + sdf.format(myCalendar.getTime()));
//                    }
//
//                };
//
//                etText.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // TODO Auto-generated method stub
//                        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
//                            return;
//                        }
//                        mLastClickTime = SystemClock.elapsedRealtime();
//                        new DatePickerDialog(context, date, myCalendar
//                                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//                    }
//                });
//                hideHeyboard();

                RelativeLayout relativeLayout = new RelativeLayout(this);
                relativeLayout.setId(View.generateViewId());

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(convertToDp(16, this), convertToDp(8, this), convertToDp(16, this), convertToDp(8, this));
                relativeLayout.setLayoutParams(params);

                CustomTextView textView = new CustomTextView(this);

                textView.setBackground(null);
                textView.setId(View.generateViewId());
                textView.setTextSize(COMPLEX_UNIT_SP, 14);
                textView.setHint(obj.getFields().get(position).getLabel());
                textView.setPadding(convertToDp(8, this), convertToDp(4, this), convertToDp(8, this), convertToDp(4, this));
                RelativeLayout.LayoutParams tvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                relativeLayout.addView(textView, tvParams);


                removeView(textView);
                relativeLayout.addView(textView, tvParams);

                ImageView imageView = new ImageView(this);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                imageView.setVisibility(View.GONE);
                tvParams = new RelativeLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));
                tvParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                tvParams.setMargins(0, 0, 0, convertToDp(16, this));
//                tvParams.addRule(RelativeLayout.ABOVE, ;ivBarcode.getId());//                tvParams.addRule(RelativeLayout.ABOVE, textView.getId());
                relativeLayout.addView(imageView, tvParams);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    imageView.setVisibility(View.VISIBLE);
                }

                View view = new View(context);
                view.setBackgroundColor(getResources().getColor(R.color.colorSlightGray));
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                p.addRule(RelativeLayout.BELOW, textView.getId());
                relativeLayout.addView(view, p);

                allViewInstance.add(textView);
                llViews.addView(relativeLayout);

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

                        textView.setText(" " + sdf.format(myCalendar.getTime()));
                    }

                };

                textView.setOnClickListener(new View.OnClickListener() {

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

                RelativeLayout relativeLayout = new RelativeLayout(this);
//                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
//                    params.setMargins(8, 8, 8, 8);

                final TextView textView = new CustomTextView(context);
                RelativeLayout.LayoutParams perams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(convertToDp(16, this), convertToDp(16, this), convertToDp(16, this), convertToDp(8, this));
                perams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                textView.setLayoutParams(perams);
                if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h1")) {
                    textView.setTextSize(COMPLEX_UNIT_SP, 20);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h2")) {
                    textView.setTextSize(COMPLEX_UNIT_SP, 18);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h3")) {
                    textView.setTextSize(COMPLEX_UNIT_SP, 16);
                } else if (obj.getFields().get(position).getPlaceholder().equalsIgnoreCase("h4")) {
                    textView.setTextSize(COMPLEX_UNIT_SP, 4);
                }
                textView.setTextColor(getResources().getColor(R.color.colorPrimary));
                textView.setText(obj.getFields().get(position).getLabel());
                textView.setId(View.generateViewId());
//                    textView.setPadding(28, 28, 28, 28);
                textView.setMinHeight(10);
                textView.setMinimumHeight(60);
                textView.setGravity(Gravity.START);
                textView.setSingleLine();

                textView.setTag(obj.getFields().get(position).getPlaceholder());
                Log.e("Tag", obj.getFields().get(position).getPlaceholder());

                int nxt2nxtPosition = position + 2;
                if (nxt2nxtPosition < obj.getFields().size()) {
                    if (obj.getFields().get(nxt2nxtPosition).getType().equalsIgnoreCase("header") || obj.getFields().get(nxt2nxtPosition).getType().equalsIgnoreCase("textarea")) {

                    } else {
                        removeView(textView);
                        relativeLayout.addView(textView);
                        removeView(relativeLayout);
                        llViews.addView(relativeLayout);
                    }
                }
                if (position == 0) {
                    removeView(textView);
                    relativeLayout.addView(textView);
                    removeView(relativeLayout);
                    llViews.addView(relativeLayout);
                }
                allViewInstance.add(textView);
                hideHeyboard();

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
                LinearLayout.LayoutParams startparams = new LinearLayout.LayoutParams(convertToDp(10, this), convertToDp(10, this));    // Pass two args; must be LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, or an integer pixel value.
                perams.setMargins(4, 10, 4, 4);
                ImageView star = new ImageView(this);
                star.setImageDrawable(getResources().getDrawable(R.drawable.red_astrik));
                star.setLayoutParams(startparams);
//                star.setVisibility(View.VISIBLE);
                star.setVisibility(View.GONE);
                if (obj.getFields().get(position).getRequired().equalsIgnoreCase("true")) {
                    star.setVisibility(View.VISIBLE);
//                    etText.setTextColor(Color.RED);
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
                hideHeyboard();

            }

        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableLoader();
                getDataFromDynamicViews(getWindow().getDecorView().findViewById(android.R.id.content), obj);
//                btnSubmit.setEnabled(false);
                SettingValues.setBarcodeVal("");
                SettingValues.getBarcodelist().clear();

            }
        });

        hideHeyboard();
    }

    private void setUpCarousel() {
        List<Drawable> images = new ArrayList<>();
        images.add(getResources().getDrawable(R.drawable.ic_electric_shaver));
        images.add(getResources().getDrawable(R.drawable.ic_file));
        images.add(getResources().getDrawable(R.drawable.ic_ecg));
        images.add(getResources().getDrawable(R.drawable.ic_cable));

        EquipmentAccessoriesAdapter adapter = new EquipmentAccessoriesAdapter(list, images, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        rvCarousel.setAdapter(adapter);
        rvCarousel.setLayoutManager(manager);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;
//        if (screenWidth / manager.getChildAt(0).getWidth()>list.size()){
//            leftArrow.setVisibility(View.GONE);
//            rightArrow.setVisibility(View.GONE);
//        }

        final int finalScreenWidth = screenWidth;
        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView rvCarousel = findViewById(hashmap2.get(v.getId()));
                LinearLayoutManager manager = (LinearLayoutManager) rvCarousel.getLayoutManager();
                if (manager.findFirstVisibleItemPosition() > finalScreenWidth / manager.getChildAt(0).getWidth()) {
                    rvCarousel.smoothScrollToPosition(manager.findFirstCompletelyVisibleItemPosition() - finalScreenWidth / manager.getChildAt(0).getWidth() + 1);
                } else {
                    rvCarousel.smoothScrollToPosition(0);
                }

            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecyclerView rvCarousel = findViewById(hashmap3.get(v.getId()));
                int size = hashMap5.get(v.getId());
                LinearLayoutManager manager = (LinearLayoutManager) rvCarousel.getLayoutManager();
                if (size - manager.findLastCompletelyVisibleItemPosition() > finalScreenWidth / manager.getChildAt(0).getWidth() - 1)
                    rvCarousel.smoothScrollToPosition(manager.findLastCompletelyVisibleItemPosition() + finalScreenWidth / manager.getChildAt(0).getWidth() - 1);
                else rvCarousel.smoothScrollToPosition(size - 1);

            }
        });

        rvCarousel.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                ImageView leftArrow = findViewById(hashmap4.get(recyclerView.getId()).getLeftArrow());
                ImageView rightArrow = findViewById(hashmap4.get(recyclerView.getId()).getRightArrow());
                int size = hashmap4.get(recyclerView.getId()).getListSize();
                if (manager.findFirstCompletelyVisibleItemPosition() == 0) {
                    leftArrow.setVisibility(View.INVISIBLE);
                } else {
                    leftArrow.setVisibility(View.VISIBLE);
                }

                if (manager.findLastCompletelyVisibleItemPosition() == size - 1) {

                    rightArrow.setVisibility(View.INVISIBLE);
                } else
                    rightArrow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                ImageView leftArrow = findViewById(hashmap4.get(recyclerView.getId()).getLeftArrow());
                ImageView rightArrow = findViewById(hashmap4.get(recyclerView.getId()).getRightArrow());
                int size = hashmap4.get(recyclerView.getId()).getListSize();

                if (manager.findFirstCompletelyVisibleItemPosition() == 0) {
                    leftArrow.setVisibility(View.INVISIBLE);
                } else {
                    leftArrow.setVisibility(View.VISIBLE);
                }

                if (manager.findLastCompletelyVisibleItemPosition() == size - 1) {

                    rightArrow.setVisibility(View.INVISIBLE);
                } else
                    rightArrow.setVisibility(View.VISIBLE);
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
//                Toast.makeText(SignatureActivity.this, "OnStartSigning", Toast.LENGTH_SHORT).getDialog();
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
//                    Toast.makeText(SignatureActivity.this, "SVG Signature saved into the Gallery", Toast.LENGTH_SHORT).getDialog();
//                } else {
//                    Toast.makeText(SignatureActivity.this, "Unable to store the SVG signature", Toast.LENGTH_SHORT).getDialog();
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


                if (noOfViews == 0) {
                    fieldsObj.put("equip_barcode", SettingValues.getBarcodeVal());
                    SettingValues.setBarcodeVal("");
                }

                if (noOfViews == 0) {
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
                    if (spinner.getSelectedItemPosition() == spinner.getAdapter().getCount()) {
                        Toast.makeText(this, "Please fill in all the mandatory fields carefully", Toast.LENGTH_SHORT).show();
                        disableLoader();
                        return;
                    }
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


                    try {
                        Switch toggle = (Switch) allViewInstance.get(noOfViews);
                        if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                            if (toggle.isChecked()) {
                                List<ChecklistFormOption> list = toggleHash.get(toggle.getId());
                                for (ChecklistFormOption option : list) {

                                }
                            }
                        }
//                        RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
//                        RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
//                        Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");

//                        String a[] = selectedRadioBtn.getTag().toString().split(",");
//                            fieldsObj.put("field_id", "" + a[0]);
                        fieldsObj.put("field_id", "" + toggle.getId());
                        fieldsObj.put("value", "");
//                        Log.e("f", a[0]);
//                        Log.e("s", a[1]);
//                        Log.e("s", a[2]);

                        JSONArray optionsArray = new JSONArray();
//                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
                        JSONObject optionsObj = new JSONObject();
                        ChecklistFormOption optionYes = new ChecklistFormOption();
                        ChecklistFormOption optionNo = new ChecklistFormOption();
                        for (ChecklistFormOption option : toggleHash.get(toggle.getId())) {
                            if (option.getLabel().equalsIgnoreCase("yes")) {
                                optionYes = option;
                            } else if (option.getLabel().equalsIgnoreCase("no")) {
                                optionNo = option;
                            }

                        }

                        if (toggle.isChecked()) {
                            optionsObj.put("option_id", "" + optionYes.getId());
                            fieldsObj.put("type", "radio-group");
                            optionsObj.put("value", "" + optionYes.getLabel());
                        } else {
                            optionsObj.put("option_id", "" + optionNo.getId());
                            fieldsObj.put("type", "radio-group");
                            optionsObj.put("value", "" + optionNo.getLabel());
                        }
                        optionsArray.put(optionsObj);
//                    }
                        fieldsObj.put("option", optionsArray);
                        jsonArray.put(fieldsObj);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).show();
                        disableLoader();
                        btnSubmit.setEnabled(true);
                        return;
                    }
//                    } else {
//                        try {
//
//                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
//                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
//
//                            Log.e("selected_radio_button", selectedRadioBtn.getTag().toString() + "");
//
//                            String a[] = selectedRadioBtn.getTag().toString().split(",");
//                            fieldsObj.put("field_id",
//                                    "" + a[0]);
//                            fieldsObj.put("type", "radio-group");
//                            fieldsObj.put("value", "");
//                            Log.e("f", a[0]);
//                            Log.e("s", a[1]);
//                            Log.e("s", a[2]);
//
//                            JSONArray optionsArray = new JSONArray();
////                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
//                            JSONObject optionsObj = new JSONObject();
//                            optionsObj.put("option_id", "" + a[1]);
//                            optionsObj.put("value", "" + a[2]);
//                            optionsArray.put(optionsObj);
////                    }
//                            fieldsObj.put("option", optionsArray);
//                            jsonArray.put(fieldsObj);
//
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                            RadioGroup radioGroup = (RadioGroup) allViewInstance.get(noOfViews);
////                            RadioButton selectedRadioBtn = (RadioButton) findViewById(radioGroup.getId());
//
//                            Log.e("rg_id", radioGroup.getTag().toString() + "");
//
//                            fieldsObj.put("field_id",
//                                    radioGroup.getTag().toString());
//                            fieldsObj.put("type", "radio-group");
//                            fieldsObj.put("value", "");
////                            Log.e("f", a[0]);
////                            Log.e("s", a[1]);
////                            Log.e("s", a[2]);
//
//                            JSONArray optionsArray = new JSONArray();
////                    for (int k = 0; k < response.body().getForm().getFields().get(spinner.getSelectedItemPosition()).getOptions().size(); k++) {
//                            JSONObject optionsObj = new JSONObject();
//                            optionsObj.put("option_id", "");
//                            optionsObj.put("value", "");
//                            optionsArray.put(optionsObj);
////                        }
//                            fieldsObj.put("option", optionsArray);
//                            jsonArray.put(fieldsObj);
////
////                            return;
//                        }
//                    }
                }
                if (response.getFields().get(noOfViews).getType().equals("checkbox-group")) {

//                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
//                    Boolean empty = true;
//                    LinearLayout ll = (LinearLayout) allViewInstance.get(noOfViews);
                    JSONArray optionsArray = new JSONArray();
                    String field_id = response.getFields().get(noOfViews).getFieldId() + "";
                    List<ChecklistFormOption> checklistFormOptionList = field2checkList.get(Integer.parseInt(field_id));
                    fieldsObj.put("field_id", field_id);
                    fieldsObj.put("type", "checkbox-group");
                    fieldsObj.put("value", "");

                    for (ChecklistFormOption model : checklistFormOptionList) {
                        if (model.isSelected()) {
                            JSONObject optionsObj = new JSONObject();
                            optionsObj.put("option_id", "" + model.getId());
                            optionsObj.put("value", "" + model.getLabel());
                            optionsArray.put(optionsObj);
                        }
                    }
                    fieldsObj.put("option", optionsArray);
                    jsonArray.put(fieldsObj);

//                    for (int i = 0; i < ll.getChildCount(); i++) {
//                        CheckBox tempChkBox = (CheckBox) ll.getChildAt(i);
//                        if (tempChkBox.isChecked()) {
//                            empty = false;
//                            String a[] = tempChkBox.getTag().toString().split(",");
//
//                            Log.e("f", a[0]);
//                            Log.e("s", a[1]);

//                            JSONObject optionsObj = new JSONObject();
//                            optionsObj.put("option_id", "" + a[0]);
//                            optionsObj.put("value", "" + a[1]);
//                            optionsArray.put(optionsObj);
//                            Log.e("Selected checkbox", tempChkBox.getTag().toString() + "");
//                        }
//                    }
//                        if (empty) {
//                            Toast.makeText(this, "Please fill mandatory fields carefully!", Toast.LENGTH_SHORT).getDialog();
//                            return;
//                        } else {
//                    fieldsObj.put("option", optionsArray);
//                    jsonArray.put(fieldsObj);
//                        }
//                    } else {
//                        String field_id = response.getFields().get(noOfViews).getFieldId() + "";
//                        fieldsObj.put("field_id", field_id);
//                        fieldsObj.put("type", "checkbox-group");
//                        fieldsObj.put("value", "");
//                        LinearLayout ll = (LinearLayout) allViewInstance.get(noOfViews);
//                        JSONArray optionsArray = new JSONArray();
//                        for (int i = 0; i < ll.getChildCount(); i++) {
//                            CheckBox tempChkBox = (CheckBox) ll.getChildAt(i);
//                            if (tempChkBox.isChecked()) {
//                                String a[] = tempChkBox.getTag().toString().split(",");
//
//                                Log.e("f", a[0]);
//                                Log.e("s", a[1]);
//
//                                JSONObject optionsObj = new JSONObject();
//                                optionsObj.put("option_id", "" + a[0]);
//                                optionsObj.put("value", "" + a[1]);
//                                optionsArray.put(optionsObj);
//                                Log.e("Selected checkbox", tempChkBox.getTag().toString() + "");
//                            }
//                        }
//
//                        fieldsObj.put("option", optionsArray);
//                        jsonArray.put(fieldsObj);
//                    }

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
                            disableLoader();
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
                            disableLoader();
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
                            disableLoader();
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
                            disableLoader();
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
                            disableLoader();
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
                            disableLoader();
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
                                disableLoader();
                                return;
                            }

                            JSONArray optionsArray = new JSONArray();
                            fieldsObj.put("option", optionsArray);
                            jsonArray.put(fieldsObj);

                            Log.d("myImages", "ID: " + field_id + " Data: " + encodedImage);
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Signature is Mandatory. Please take signature", Toast.LENGTH_SHORT).show();
                            disableLoader();
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

                if (response.getFields().get(noOfViews).getType().equalsIgnoreCase("file")) {
                    if (response.getFields().get(noOfViews).getRequired().equalsIgnoreCase("true")) {
                        if (myImages.size() == 0) {
                            Toast.makeText(this, "Please upload the mandatory images", Toast.LENGTH_SHORT).show();
                            disableLoader();
                            return;
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
//            Toast.makeText(context, ""+val, Toast.LENGTH_SHORT).getDialog();
            obj.put("timeCount", timer.getText());
            submitData(obj.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeView(View view) {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view); // <- fix
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void submitData(String data) {
//        btnSubmit.setEnabled(false);
        retrofit2.Call<SubmitFormResponse> call;
        RequestBody bodyRequest = RequestBody.create(MediaType.parse("application/json"), data.toString());
        RequestBody iid = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(vehicleId));
        RequestBody type = RequestBody.create(MediaType.parse("text/plain"), "equipment_checklist");

        ArrayList<MultipartBody.Part> images = new ArrayList<>();

        for (int i = 0; i < myImages.size(); i++) {
            File file1 = new File(String.valueOf(myImages.get(i).getTempUri()));
            images.add(MultipartBody.Part.createFormData("images", file1.getName(), RequestBody.create(MediaType.parse("image/*"), myImages.get(i).getFinalImage())));
        }


        call = RetrofitClass.getInstance().getWebRequestsInstance().submitChecklistData(tinyDB.getString(Constants.token), bodyRequest, images, iid, type, Integer.parseInt(id));
        call.enqueue(new Callback<SubmitFormResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SubmitFormResponse> call, Response<SubmitFormResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        disableLoader();
                        Toast.makeText(EquipmentCheckList.this, "Successfully Incidence saved to server !", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EquipmentCheckList.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        disableLoader();

                    }
                } else if (response.code() == 401) {
                    disableLoader();
                    if (getApplicationContext() != null)
                        new SessionTimeoutDialog(EquipmentCheckList.this).getDialog().show();
                } else {
                    disableLoader();
                    Toast.makeText(EquipmentCheckList.this, "Internal Server Error !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SubmitFormResponse> call, Throwable t) {
                Toast.makeText(EquipmentCheckList.this, "Can't connect to server !", Toast.LENGTH_SHORT).show();
                disableLoader();

                t.printStackTrace();
            }
        });
    }

    public void enableLoader() {
        progress.setVisibility(View.VISIBLE);
        btnSubmit.setVisibility(View.GONE);
        ivBack.setEnabled(false);
    }

    public void disableLoader() {
        progress.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.VISIBLE);
        ivBack.setEnabled(true);
    }


    @Override
    public void onAdminItemClicked(int recyclerViewId, List<ChecklistFormOption> list) {
        TextView textView = findViewById(rv2tvMap.get(recyclerViewId));
        numSelected = 0;
        for (ChecklistFormOption model : list) {
            if (model.isSelected())
                numSelected++;
        }
        String s = numSelected + "/" + list.size();
        textView.setText(s);
        field2checkList.put(hashMap5.get(recyclerViewId), list);
    }

    public static class Arrows {

        int leftArrow;
        int rightArrow;
        int listSize;

        public int getListSize() {
            return listSize;
        }

        public void setListSize(int listSize) {
            this.listSize = listSize;
        }

        public int getLeftArrow() {
            return leftArrow;
        }

        public void setLeftArrow(int leftArrow) {
            this.leftArrow = leftArrow;
        }

        public int getRightArrow() {
            return rightArrow;
        }

        public void setRightArrow(int rightArrow) {
            this.rightArrow = rightArrow;
        }
    }
}
