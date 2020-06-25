package com.aap.medicore.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aap.medicore.Models.LoginData;
import com.aap.medicore.Models.Version;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.TinyDB;
import com.crashlytics.android.Crashlytics;
import com.fxn.pix.Pix;

import io.fabric.sdk.android.Fabric;
import retrofit2.Callback;
import retrofit2.Response;

public class Splash extends AppCompatActivity {

    int SPLASH_TIME_OUT = 2000;
    Handler handler = new Handler();
    TinyDB tinyDB;
    public static double version = 1.12;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String p1 = android.Manifest.permission.CAMERA, p2 = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
tinyDB = new TinyDB(Splash.this);
        versioncheck();

    }

    public void versioncheck() {

        retrofit2.Call<Version> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().versioncheck(version+"");

        call.enqueue(new Callback<Version>() {
            @Override
            public void onResponse(retrofit2.Call<Version> call, Response<Version> response) {
                if (response.isSuccessful()) {
                    //uncoment to enable version check
                  /*  if (response.body().getStatus() == 200) {
                        permissionAccess();
                    }
                    else{
                        logoutConfirmDialogBox();
                    }*/
                    permissionAccess();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Version> call, Throwable t) {
                //todo what to do if no internet connection is available
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
        if (!checkPermission(p1)) {
            Log.e("TAG", p1);
            requestPermission(p1);
        } else if (!checkPermission(p2)) {
            Log.e("TAG", p2);
            requestPermission(p2);
        } else {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                findViewById(R.id.progress).setVisibility(View.GONE);
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
                }
            }, SPLASH_TIME_OUT);
        }
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                Log.e("TAG", "val " + grantResults[0]);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionAccess();
                } else {
                    Toast.makeText(Splash.this, "The app was not allowed permission. Hence, it cannot function properly. Please consider granting it this permission.", Toast.LENGTH_LONG).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
