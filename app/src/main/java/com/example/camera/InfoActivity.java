package com.example.camera;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        String model = getIntent().getStringExtra("MODEL");
        TextView tv1 = findViewById(R.id.model);
        tv1.setText(model);
        String make = getIntent().getStringExtra("MAKE");
        TextView tv2 = findViewById(R.id.make);
        tv2.setText(make);
        String date = getIntent().getStringExtra("DATE");
        TextView tv3 = findViewById(R.id.date);
        tv3.setText(date);
        String size = getIntent().getStringExtra("SIZE");
        TextView tv4 = findViewById(R.id.size);
        tv4.setText(size);
        String local = getIntent().getStringExtra("LOCAL");
        TextView tv5 = findViewById(R.id.local);
        tv5.setText(local);
    }
    public void startmap(View view) {
        Intent intent = new Intent();
        intent.setClass(InfoActivity.this,MapActivity.class);
        startActivity(intent);
    }

}
