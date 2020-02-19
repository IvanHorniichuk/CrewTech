package com.aap.medicore.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aap.medicore.Models.LoginData;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomEditText;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.TinyDB;
import com.google.android.material.snackbar.Snackbar;

import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.aap.medicore.Utils.UIUtils.shakeView;
import static com.aap.medicore.Utils.UIUtils.showSnackbar;

public class Login extends AppCompatActivity implements View.OnClickListener {
    CustomEditText etUsername;
    CustomEditText etPassword;
    CustomTextView tvVersion;
    CustomButton btnLogin;
    TinyDB tinyDB;
    Unregistrar unregistrar;
    ProgressBar progressBar;
    String tokenFCM;
    private static final String TAG = "Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        getSupportActionBar().hide();
        init();
        tokenFCM = tinyDB.getString(Constants.tokenFCM);

        btnLogin.setOnClickListener(this);
        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                    btnLogin.performClick();
                return false;

            }
        });
    }

    public static void hide(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void init() {
        tinyDB = new TinyDB(Login.this);
        etUsername = (CustomEditText) findViewById(R.id.etUsername);
        etPassword = (CustomEditText) findViewById(R.id.etPass);
        btnLogin = (CustomButton) findViewById(R.id.btnLogin);
        tvVersion = (CustomTextView) findViewById(R.id.version_no);
        progressBar = findViewById(R.id.progress);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tvVersion.setText("Version " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
//        KeyboardVisibilityEvent.setEventListener(
//                Login.this,
//                new KeyboardVisibilityEventListener() {
//                    @Override
//                    public void onVisibilityChanged(boolean isOpen) {
//                        // some code depending on keyboard visiblity status
//                        Log.e("board", isOpen + "");
//
//                        if (isOpen) {
//                            findViewById(R.id.rll).animate().translationY(-250);
//                        } else {
//                            findViewById(R.id.rll).animate().translationY(0);
//                        }
//
//                    }
//                });
//
//        unregistrar = KeyboardVisibilityEvent.registerEventListener(
//                Login.this,
//                new KeyboardVisibilityEventListener() {
//                    @Override
//                    public void onVisibilityChanged(boolean isOpen) {
//                        // some code depending on keyboard visiblity status
//                    }
//                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        unregistrar.unregister();
    }

//    private void clickListeners() {
//
//        btnLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String user = etUsername.getText().toString().trim();
//                String pass = etPassword.getText().toString().trim();
//                if (user.isEmpty()) {
//                    Toast.makeText(Login.this, "Please enter valid username", Toast.LENGTH_SHORT).getDialog();
//                } else {
//                    if (pass.isEmpty()) {
//                        Toast.makeText(Login.this, "Please enter valid etPassword", Toast.LENGTH_SHORT).getDialog();
//                    } else {
//                        findViewById(R.id.progress).setVisibility(View.VISIBLE);
//                        btnLogin.setVisibility(View.GONE);
//                        login(user, pass);
//                    }
//                }
//            }
//        });
//
//    }

    public void login(String uName, String pass) {

        retrofit2.Call<LoginData> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitLogin(uName, pass);

        call.enqueue(new Callback<LoginData>() {
            @Override
            public void onResponse(retrofit2.Call<LoginData> call, Response<LoginData> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus() == 200) {
//                        findViewById(R.id.progress).setVisibility(View.GONE);
//                        btnLogin.setVisibility(View.VISIBLE);
                        tinyDB.putString(Constants.user_id, response.body().getLoginResponse().getUserId() + "");
                        Log.d(TAG, "onResponse: "+response.body().getLoginResponse().getUserId());
                        tinyDB.putString(Constants.email, response.body().getLoginResponse().getEmail() + "");
                        tinyDB.putString(Constants.username, response.body().getLoginResponse().getUsername() + "");
                        tinyDB.putString(Constants.first_name, response.body().getLoginResponse().getFirstName() + "");
                        tinyDB.putString(Constants.last_name, response.body().getLoginResponse().getLastName() + "");
                        tinyDB.putString(Constants.token, "Token " + response.body().getLoginResponse().getToken());
//                        tinyDB.putString(Constants.token,"Token "+123456789);
                        Log.d(TAG, "token val is " + tinyDB.getString(Constants.token));
                        tinyDB.putString(Constants.profile_image, Constants.IMAGE_IP + response.body().getLoginResponse().getImage() + "");
                        tinyDB.putBoolean(Constants.LoggedIn, true);
                        sendTokenToServer(tinyDB.getString(Constants.token), tokenFCM);
//                        Toast.makeText(Login.this, "successfully logged in " + response.body().getLoginResponse().getEmail(), Toast.LENGTH_SHORT).getDialog();
                        Intent i = new Intent(Login.this, Home.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(Login.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        disableLoader();
                    }
                } else {
                    disableLoader();
                    Toast.makeText(Login.this, "Internal Server Error!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<LoginData> call, Throwable t) {
                Toast.makeText(Login.this, "Can't connect to server!", Toast.LENGTH_SHORT).show();
                disableLoader();
                t.printStackTrace();
            }
        });
    }

    private void sendTokenToServer(String AuthToken, String tokenFCM) {
        try {
            Call<ResponseBody> call = RetrofitClass.getInstance().getWebRequestsInstance().sendFCMTokenToServer(AuthToken, tokenFCM, "ANDROID");
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == 200)
                        Log.d(TAG, "onResponse: " + "Token send success");
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enableLoader() {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
        etUsername.setEnabled(false);
        etPassword.setEnabled(false);
    }

    public void disableLoader() {
        progressBar.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
        etUsername.setEnabled(true);
        etPassword.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v == btnLogin) {
            hide(this);
            if (etUsername.getText() != null && etPassword.getText() != null) {

                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.isEmpty() && password.isEmpty()) {
                    shakeView(Login.this, etUsername);
                    shakeView(Login.this, findViewById(R.id.imageView2));
                    shakeView(Login.this, etPassword);
                    shakeView(Login.this, findViewById(R.id.imageView3));
                    showSnackbar(Login.this, v, "Please enter the username and the password", Snackbar.LENGTH_SHORT);

                } else if (username.isEmpty()) {
                    showSnackbar(Login.this, v, "Please enter the username", Snackbar.LENGTH_SHORT);
                    shakeView(Login.this, etUsername);
                    shakeView(Login.this, findViewById(R.id.imageView2));

                } else if (password.isEmpty()) {
                    showSnackbar(Login.this, v, "Please enter the password", Snackbar.LENGTH_SHORT);
                    shakeView(Login.this, etPassword);
                    shakeView(Login.this, findViewById(R.id.imageView3));

                } else {
                    enableLoader();
                    login(username, password);
                }
            }
        }
    }


}
