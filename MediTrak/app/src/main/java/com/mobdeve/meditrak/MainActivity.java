package com.mobdeve.meditrak;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mobdeve.meditrak.HomeFragment.HomeFragment;

// Phone number: +1-555-521-5554
public class MainActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;

    private FrameLayout fcvContainerView;
    private LandingFragment fLanding;
    private HomeFragment fHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = this.getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE);
//        sharedPreferences.edit().clear().commit();
        
        this.fcvContainerView = findViewById(R.id.fcvContainer);
        fLanding = new LandingFragment(this);
        fHome = new HomeFragment(this);
    }

    private void checkPermissions() {
        Log.i("MainActivity", "checking permissions");

        if (ContextCompat.checkSelfPermission(this,
         Manifest.permission.SEND_SMS)
         != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
               Manifest.permission.SEND_SMS)) {
                if (!isServiceRunning(BackgroundService.class)) {
                    Intent intent0 = new Intent(this, BackgroundService.class);
                    this.startService(intent0);
                }
            } else {
               ActivityCompat.requestPermissions(this,
                  new String[]{Manifest.permission.SEND_SMS},0);
            }
      } else {
            if (!isServiceRunning(BackgroundService.class)) {
                Intent intent0 = new Intent(this, BackgroundService.class);
                this.startService(intent0);
            }
      }
    }
    @Override
    protected void onResume() {
        hideStatusBar();
        if (sharedPreferences.getBoolean("registered", false)) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fcvContainer, fHome).commitAllowingStateLoss();
            checkPermissions();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fcvContainer, fLanding).commitAllowingStateLoss();
        }
        super.onResume();
    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void hideStatusBar() {
        View decorView = this.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        try {
            this.getActionBar().hide();
        } catch (Exception e) {
            this.getSupportActionBar().hide();
        }
    }
}