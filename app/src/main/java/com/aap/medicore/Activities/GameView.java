package com.aap.medicore.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.aap.medicore.R;;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.TinyDB;


public class GameView extends AppCompatActivity {
WebView webView;
TinyDB tinyDB;
CustomTextView heading;
ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        tinyDB = new TinyDB(GameView.this);
        webView = (WebView)findViewById(R.id.game_wv);
        getSupportActionBar().hide();
        heading = (CustomTextView)findViewById(R.id.heading);
        back = (ImageView) findViewById(R.id.ivBack);
        webView.setWebViewClient(new CustomWebViewClient());
        WebSettings webSetting = webView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setDisplayZoomControls(true);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

       String form_name = getIntent().getStringExtra("formname");
       heading.setText(form_name);
       String id = getIntent().getStringExtra("formname");
       String url = getIntent().getStringExtra("url");
        webView.loadUrl(url);

//        try {
//            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//            startActivity(myIntent);
//        } catch (ActivityNotFoundException e) {
//            Toast.makeText(this, "No application can handle this request."
//                    + " Please install a webbrowser",  Toast.LENGTH_LONG).show();
//            e.printStackTrace();
//        }

    }

    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


    }}

