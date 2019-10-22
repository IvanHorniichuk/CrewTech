package com.aap.medicore.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.aap.medicore.R;

public class MainActivity extends AppCompatActivity {
    RecyclerView rvForms;
    LinearLayoutManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        clickListeners();
    }

    private void init() {
        rvForms = findViewById(R.id.rvForms);
        manager = new LinearLayoutManager(MainActivity.this);
        rvForms.setLayoutManager(manager);
    }

    private void clickListeners() {

    }
}
