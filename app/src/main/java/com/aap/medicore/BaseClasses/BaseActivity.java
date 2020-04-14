package com.aap.medicore.BaseClasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.aap.medicore.R;
import com.aap.medicore.Utils.CustomTextView;


public class BaseActivity extends AppCompatActivity {
    public long mLastClickTime = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void addFragmentWithBackstack(int containerId, Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction().
                add(containerId, fragment, tag)
                .addToBackStack(tag).commit();
    }

    public void addFragment(int containerId, Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .add(containerId, fragment, tag).commit();
    }

    public void replaceFragmentWithBackstack(int containerId, Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction().
                replace(containerId, fragment, tag)
                .addToBackStack(tag).commit();
    }

    public void replaceFragment(int containerId, Fragment fragment, String tag) {
        getSupportFragmentManager().beginTransaction()
                .replace(containerId, fragment, tag).commit();
    }

    //    public Boolean isConnected() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
//            //we are connected to a network
//            return true;
//        } else {
//            return false;
//        }
//    }
    public boolean isConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }

    public static ProgressDialog createProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.getWindow()
                .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progress_dialog);
        // dialog.setMessage(Message);
        return dialog;
    }

    public static void hideKeyboard(AppCompatActivity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        boolean b = imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        Log.d("log", "hideKeyboard: "+b);
    }
}
