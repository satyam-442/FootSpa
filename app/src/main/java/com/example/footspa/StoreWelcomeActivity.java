package com.example.footspa;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class StoreWelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_welcome);
    }

    public void goToStoreRegister(View view) {
        Intent intent = new Intent(this,StoreRegisterActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
    }

    public void goToStoreLogin(View view) {
        Intent intent = new Intent(this,StoreLoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_enter,R.anim.zoom_exit);
    }
}