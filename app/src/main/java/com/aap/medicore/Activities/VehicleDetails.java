package com.aap.medicore.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aap.medicore.Models.VehicleDetail;
import com.aap.medicore.Models.VehicleResponse;
import com.aap.medicore.NetworkCalls.RetrofitClass;
import com.aap.medicore.R;
import com.aap.medicore.Utils.Constants;
import com.aap.medicore.Utils.CustomTextView;
import com.aap.medicore.Utils.TinyDB;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VehicleDetails extends AppCompatActivity {
    TinyDB tinyDB;
    ImageView ivBack;
    String user_id = "";
    CustomTextView tvCallsCount, tvBedsCount, tvEngineHorsepower, tvColor, tvRegistrationNo, tvRegistrationYear, tvManufacturingYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_details);

        getSupportActionBar().hide();


        init();
        clickListeners();
        fetchVehicleData();
    }

    private void init() {
        tinyDB = new TinyDB(VehicleDetails.this);
        ivBack = findViewById(R.id.ivBack);

        tvBedsCount = findViewById(R.id.tvBedsCount);
        tvEngineHorsepower = findViewById(R.id.tvEngineHorsepower);
        tvColor = findViewById(R.id.tvColor);
        tvRegistrationNo = findViewById(R.id.tvRegistrationNo);
        tvRegistrationYear = findViewById(R.id.tvRegistrationYear);

        tvManufacturingYear = findViewById(R.id.tvManufacturingYear);
    }

    private void clickListeners() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void fetchVehicleData() {


        retrofit2.Call<VehicleResponse> call;
        call = RetrofitClass.getInstance().getWebRequestsInstance().getVehicleDetail(tinyDB.getString(Constants.token),tinyDB.getString(Constants.user_id));

        call.enqueue(new Callback<VehicleResponse>() {

            @Override
            public void onResponse(Call<VehicleResponse> call, final Response<VehicleResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        tvBedsCount.setText(response.body().getVehicleDetail().getBedSpace() + "");
                        tvColor.setText(response.body().getVehicleDetail().getVehicleName() + "");
                        tvEngineHorsepower.setText(response.body().getVehicleDetail().getEnginHouspower() + "");
                        tvRegistrationNo.setText(response.body().getVehicleDetail().getRegistrationno() + "");
                        tvRegistrationYear.setText(response.body().getVehicleDetail().getRegistrationYear() + "");
                        tvManufacturingYear.setText(response.body().getVehicleDetail().getManufacturingYear() + "");
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<VehicleResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }
}