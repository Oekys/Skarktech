package com.example.myproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);
    }


    public void btnhome(View view){
        startActivity(new Intent(DashboardActivity.this,DashboardActivity.class));
    }

    public void btnbudget(View view){
        startActivity(new Intent(DashboardActivity.this,BudgetActivity.class));
    }

    public void btnticket(View view){
        startActivity(new Intent(DashboardActivity.this,TicketsActivity.class));

    }
}


