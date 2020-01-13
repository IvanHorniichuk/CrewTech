package com.aap.medicore.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.aap.medicore.Utils.CustomEditText;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.TinyDB;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;




public class ScanActivity extends Activity implements ZXingScannerView.ResultHandler{

    private ZXingScannerView mScannerView;
    TinyDB tinyDB;
    String formId = "";
    String pos;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);
        tinyDB = new TinyDB(ScanActivity.this);// Set the scanner view as the content view
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void handleResult(Result rawResult) {

        Intent intent = new Intent();
        int id = getIntent().getIntExtra("ImageViewId",0);
        Log.d("imageViewId", "handleResult: "+id);
        intent.putExtra("ImageViewId", id);
        intent.putExtra("Value",rawResult.toString());
        Log.d("BarcodeValue", "handleResult: "+rawResult.toString());
        setResult(2,intent);
        finish();
//        SettingValues.setBarcodeVal(rawResult.getText());
//        CustomTextView textView = (CustomTextView) allViewInstance.get(Integer.parseInt(position));
//        textView.setText(rawResult.getText());
//        finish();




//        Toast.makeText(this, ""+SettingValues.getBarcodeVal(), Toast.LENGTH_SHORT).getDialog();
        // Do something with the result here
        // Log.v("tag", rawResult.getText()); // Prints scan results
        // Log.v("tag", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)



//        TaskDetails.bar.setText(rawResult.getText());

        // If you would like to resume scanning, call this method below:
        //mScannerView.resumeCameraPreview(this);
    }
}