package com.aap.medicore.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aap.medicore.Models.LoginData;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomEditText;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.TinyDB;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Callback;
import retrofit2.Response;

import static com.aap.medicore.Activities.Splash.version;

public class Login extends AppCompatActivity {
    CustomEditText user_name;
    CustomEditText password;
    CustomTextView version_no;
    CustomButton login_btn;
    TinyDB tinyDB;
    Unregistrar unregistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_new);
        getSupportActionBar().hide();

        init();
        clickListeners();

    }

    private void init() {
        tinyDB = new TinyDB(Login.this);
        user_name = (CustomEditText) findViewById(R.id.etUsername);
        password = (CustomEditText) findViewById(R.id.etPass);
        login_btn = (CustomButton) findViewById(R.id.btnLogin);
        version_no = (CustomTextView)findViewById(R.id.version_no);
        version_no.setText("Version "+version);
        KeyboardVisibilityEvent.setEventListener(
                Login.this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        Log.e("board", isOpen + "");

                        if (isOpen) {
                            findViewById(R.id.rll).animate().translationY(-250);
                        } else {
                            findViewById(R.id.rll).animate().translationY(0);
                        }

                    }
                });

        unregistrar = KeyboardVisibilityEvent.registerEventListener(
                Login.this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregistrar.unregister();
    }

    private void clickListeners() {

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = user_name.getText().toString().trim();
                String pass = password.getText().toString().trim();
                if (user.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter valid username", Toast.LENGTH_SHORT).show();
                } else {
                    if (pass.isEmpty()) {
                        Toast.makeText(Login.this, "Please enter valid password", Toast.LENGTH_SHORT).show();
                    } else {
                        findViewById(R.id.progress).setVisibility(View.VISIBLE);
                        login_btn.setVisibility(View.GONE);
                        login(user, pass);
                    }
                }
            }
        });

    }

    public void login(String uName, String pass) {

        retrofit2.Call<LoginData> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitLogin(uName, pass);

        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(retrofit2.Call<LoginData> call, Response<LoginData> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
                        findViewById(R.id.progress).setVisibility(View.GONE);
                        login_btn.setVisibility(View.VISIBLE);
                        tinyDB.putString(Constants.user_id, response.body().getLoginResponse().getUserId() + "");
                        tinyDB.putString(Constants.email, response.body().getLoginResponse().getEmail() + "");
                        tinyDB.putString(Constants.username, response.body().getLoginResponse().getUsername() + "");
                        tinyDB.putString(Constants.first_name, response.body().getLoginResponse().getFirstName() + "");
                        tinyDB.putString(Constants.last_name, response.body().getLoginResponse().getLastName() + "");
                        tinyDB.putString(Constants.token,"Token "+response.body().getLoginResponse().getToken());
//                        tinyDB.putString(Constants.token,"Token "+123456789);
                        Log.e("token val","token val is "+tinyDB.getString(Constants.token));
                        tinyDB.putString(Constants.profile_image, Constants.IMAGE_IP + response.body().getLoginResponse().getImage() + "");
                        tinyDB.putBoolean(Constants.LoggedIn, true);

//                        Toast.makeText(Login.this, "successfully logged in " + response.body().getLoginResponse().getEmail(), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Login.this, Home.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(Login.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        findViewById(R.id.progress).setVisibility(View.GONE);
                        login_btn.setVisibility(View.VISIBLE);
                    }
                } else {
                    findViewById(R.id.progress).setVisibility(View.GONE);
                    login_btn.setVisibility(View.VISIBLE);
                    Toast.makeText(Login.this, "Internal Server Error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<LoginData> call, Throwable t) {
                Toast.makeText(Login.this, "Can't connect to server!", Toast.LENGTH_SHORT).show();
                findViewById(R.id.progress).setVisibility(View.GONE);
                login_btn.setVisibility(View.VISIBLE);
                t.printStackTrace();
            }
        });
    }


}
