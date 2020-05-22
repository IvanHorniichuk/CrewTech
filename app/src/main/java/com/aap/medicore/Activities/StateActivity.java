package com.aap.medicore.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aap.medicore.Adapters.SpinnerAdapter;
import com.aap.medicore.Adapters.SpinnerArrayAdapter;
import com.aap.medicore.BaseClasses.BaseActivity;
import com.aap.medicore.Models.StateModel;
import com.aap.medicore.Models.StatusResponse;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomButton;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.SessionTimeoutDialog;
import com.aap.medicore.Utils.TinyDB;

import java.util.ArrayList;

import retrofit2.Callback;
import retrofit2.Response;

public class StateActivity extends BaseActivity {
    ImageView ivBack;
    TinyDB tinyDB;
   TextView tvTitle, tvDescription;
    Button btnUpdateState;
    ImageView ivImage;
    AppCompatSpinner spinner;
    ArrayList<StateModel> list;

    String title = "", desc = "";
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);
        getSupportActionBar().hide();

        init();
        clickListeners();
    }

    private void init() {
        ivBack = findViewById(R.id.ivBack);
        tinyDB = new TinyDB(StateActivity.this);

        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        btnUpdateState = findViewById(R.id.btnUpdateState);
        ivImage = findViewById(R.id.ivImage);

        spinner = findViewById(R.id.spinner);

        list = new ArrayList<>();

        StateModel model = new StateModel();
        model.setTitle("En-Route to Pickup");
        model.setDescription("We are en route to pickup the patient.");
        model.setPosition(0);
        list.add(model);

        StateModel model1 = new StateModel();
        model1.setTitle("Waiting Return");
        model1.setDescription("Waiting for the patient to finish procedure so, We can return.");
        model1.setPosition(1);
        list.add(model1);

        StateModel model2 = new StateModel();
        model2.setTitle("En-Route to Drop Off");
        model2.setDescription("We are en route to drop off the patient.");
        model2.setPosition(2);
        list.add(model2);

        StateModel model3 = new StateModel();
        model3.setTitle("On Break");
        model3.setDescription("We are on brake.");
        model3.setPosition(3);
        list.add(model3);

        StateModel model4 = new StateModel();
        model4.setTitle("Available");
        model4.setDescription("Currently we are available and done with assigned incidents.");
        model4.setPosition(4);
        list.add(model4);

        StateModel model5 = new StateModel();
        model5.setTitle("Charlie Tango");
        model5.setDescription("Can't talk - Patient is listening and We cannot speak freely.");
        model5.setPosition(5);
        list.add(model5);

        StateModel model6 = new StateModel();
        model6.setTitle("Break Down");
        model6.setPosition(6);
        model6.setDescription("We are facing a problem with our vehicle.");
        list.add(model6);

        SpinnerAdapter  adapter = new SpinnerAdapter(StateActivity.this,
                R.layout.custom_spinner_item, list);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                title = list.get(pos).getTitle();
                desc = list.get(pos).getDescription();
                position = pos;

                tvTitle.setText(title);
                tvDescription.setText(desc);

                if (pos == 0)
                    ivImage.setBackground(getDrawable(R.drawable.ic_pickup_copy));
//                    ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_pickup_copy));

                if (pos == 1)
//                    ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_waiting));
                    ivImage.setBackground(getDrawable(R.drawable.ic_waiting));
                if (pos == 2)
//                    ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_off));
                    ivImage.setBackground(getDrawable(R.drawable.ic_drop_off));
                if (pos == 3)
//                    ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_break));
                    ivImage.setBackground(getDrawable(R.drawable.ic_break));
                if (pos == 4)
//                    ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_ready));
                    ivImage.setBackground(getDrawable(R.drawable.ic_ready));
                if (pos == 5)
//                    ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_not_allowed));
                    ivImage.setBackground(getDrawable(R.drawable.ic_not_allowed));

                if (pos == 6)
//                    ivImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_breakdown));
                    ivImage.setBackground(getDrawable(R.drawable.brake));
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (!(tinyDB.getString(Constants.StateTitle).isEmpty())) {
            spinner.setSelection(tinyDB.getInt(Constants.StatePosition));
        }
    }

    private void clickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUpdateState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tinyDB.putString(Constants.StateTitle, title);
                tinyDB.putString(Constants.StateDescription, desc);
                tinyDB.putInt(Constants.StatePosition, position);
                hitUserStatus();

            }
        });
    }

    private void hitUserStatus(){

        retrofit2.Call<StatusResponse> call;

        call = RetrofitClass.getInstance().getWebRequestsInstance().hitUserStatus(tinyDB.getString(Constants.token), tinyDB.getString(Constants.user_id), tinyDB.getString(Constants.StateTitle),"online");

        call.enqueue(new Callback<StatusResponse>() {
            private Intent newintent;
            private boolean status;

            @Override
            public void onResponse(retrofit2.Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful()) {
                    Log.e("code", response.body().getStstus() + "");
                    if (response.body().getStstus() == 200) {
                        status = true;
                        Toast.makeText(StateActivity.this, "State Updated Successfully!", Toast.LENGTH_SHORT).show();
                        finish();
                    }else
                        Toast.makeText(StateActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();

                } else if (response.code() == 401) {
                    if (getApplicationContext() != null)
                        new SessionTimeoutDialog(StateActivity.this).getDialog().show();
                }else{
                    Toast.makeText(StateActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();

                }
                newintent = new Intent(Constants.BROADCAST_ACTION);
                newintent.putExtra(Constants.Status, status);
                sendBroadcast(newintent);
            }

            @Override
            public void onFailure(retrofit2.Call<StatusResponse> call, Throwable t) {
                Toast.makeText(StateActivity.this, "Please try again later", Toast.LENGTH_SHORT).show();
                newintent = new Intent(Constants.BROADCAST_ACTION);
                newintent.putExtra(Constants.Status, status);
                sendBroadcast(newintent);
            }
        });
    }
}
