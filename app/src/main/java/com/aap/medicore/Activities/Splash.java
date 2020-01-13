package com.aap.medicore.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aap.medicore.Models.Version;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.TinyDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {

    int SPLASH_TIME_OUT = 2000;
    Handler handler = new Handler();
    TinyDB tinyDB;
    public static String version = "0.3";
    public static final int PERMISSION_REQUEST_CODE = 1;
    //    String p1 = android.Manifest.permission.CAMERA, p2 = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
    String[] requiredPermissions = new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private FirebaseAnalytics mFirebaseAnalytics;
    String tokenFCM;
    private static final String TAG = "Splash";
    private boolean flag;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        tinyDB = new TinyDB(Splash.this);
        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }

                    // Get new Instance ID token
                    tokenFCM = task.getResult().getToken();
                    tinyDB.putString(Constants.tokenFCM, tokenFCM);

                    // Log and toast
//                        String msg = getString(R.string.msg_token_fmt, token);
                    Log.d(TAG, tokenFCM);
//                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).getDialog();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
//        versioncheck();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (ContextCompat.checkSelfPermission(this, requiredPermissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, requiredPermissions[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, requiredPermissions[2]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, requiredPermissions[3]) == PackageManager.PERMISSION_GRANTED) {

            versioncheck();
            flag = true;

        } else
            ActivityCompat.requestPermissions(this, requiredPermissions, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (ContextCompat.checkSelfPermission(this, requiredPermissions[0]) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, requiredPermissions[1]) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, requiredPermissions[2]) == PackageManager.PERMISSION_GRANTED &&
//                ContextCompat.checkSelfPermission(this, requiredPermissions[3]) == PackageManager.PERMISSION_GRANTED)
//            if (!flag)
//                versioncheck();

    }

    public void versioncheck() {
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        retrofit2.Call<Version> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().versioncheck(version + "");

        call.enqueue(new Callback<Version>() {
            @Override
            public void onResponse(retrofit2.Call<Version> call, Response<Version> response) {
                if (response.isSuccessful()) {
//                    if (response.body().getStatus() == 200) {
//                        permissionAccess();
//                    }
//                    else{
//                        logoutConfirmDialogBox();
//                    }
//                    permissionAccess();
                    findViewById(R.id.progress).setVisibility(View.GONE);

                    if (tinyDB.getBoolean(Constants.LoggedIn)) {
                        Intent intent = new Intent(Splash.this, Home.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(Splash.this, Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    findViewById(R.id.progress).setVisibility(View.GONE);
                    Toast.makeText(Splash.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Version> call, Throwable t) {
                findViewById(R.id.progress).setVisibility(View.GONE);
                Toast.makeText(Splash.this, "Please try again after some time", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }


    public void logoutConfirmDialogBox() {
        final Dialog dialog = new Dialog(Splash.this);
        final CustomButton btnNo, btnYes;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.version);
        btnNo = (CustomButton) dialog.findViewById(R.id.btnNo);
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }


    private void permissionAccess() {
//        if (!checkPermission(p1)) {
//            Log.e("TAG", p1);
//            requestPermission(p1);
//        } else if (!checkPermission(p2)) {
//            Log.e("TAG", p2);
//            requestPermission(p2);
//        } else {
//            findViewById(R.id.progress).setVisibility(View.VISIBLE);
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
////                findViewById(R.id.progress).setVisibility(View.GONE);
//                    if (tinyDB.getBoolean(Constants.LoggedIn)) {
//                        Intent intent = new Intent(Splash.this, Home.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Intent intent = new Intent(Splash.this, Login.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        startActivity(intent);
//                        finish();
//                    }
//                }
//            }, SPLASH_TIME_OUT);
//        }


    }

    private boolean checkPermission(String permission) {
        int result = ContextCompat.checkSelfPermission(Splash.this, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(String permission) {

        if (ContextCompat.checkSelfPermission(Splash.this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Splash.this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        } else {
            //Do the stuff that requires permission...
            Log.e("TAG", "Not say request");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
//                Log.e("TAG", "val " + grantResults[0]);
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    permissionAccess();
//                } else {
//                    Toast.makeText(Splash.this, "The app was not allowed permission. Hence, it cannot function properly. Please consider granting it this permission.", Toast.LENGTH_LONG).show();
//                }
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                    versioncheck();
                } else {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED) i = 0;
                    if (grantResults[1] != PackageManager.PERMISSION_GRANTED) i = 1;
                    if (grantResults[2] != PackageManager.PERMISSION_GRANTED) i = 2;
                    if (grantResults[3] != PackageManager.PERMISSION_GRANTED) i = 3;
                    Snackbar.make(findViewById(R.id.progress), "This app needs these permissions to function properly.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Allow", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Log.d(TAG, "Snackbar onClick: starts");
                                            if (ActivityCompat.shouldShowRequestPermissionRationale(Splash.this, permissions[i])) {
                                                Log.d(TAG, "Snackbar onClick: calling requestPermissions");
                                                ActivityCompat.requestPermissions(Splash.this, permissions, PERMISSION_REQUEST_CODE);
                                            } else {
                                                // The user has permanently denied the permission, so take them to the settings
                                                Log.d(TAG, "Snackbar onClick: launching settings");
                                                Intent intent = new Intent();
                                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", Splash.this.getPackageName(), null);
                                                Log.d(TAG, "Snackar onClick: Intent Uri is " + uri.toString());
                                                intent.setData(uri);
                                                Splash.this.startActivity(intent);
                                            }
                                            Log.d(TAG, "Snackbar onClick: ends");
                                        }
                                    }

                            ).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}

