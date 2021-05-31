package com.mobdeve.meditrak.RegisterActivity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.mobdeve.meditrak.R;

public class Register extends AppCompatActivity {
    private static final int NUM_PAGES = 3;

    public ViewPager2 vp2Register;
    public FragmentStateAdapter pagerAdapter;
    public String name, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        vp2Register = findViewById(R.id.vp2Register);
        pagerAdapter = new RegisterPagerAdapter(this);
        vp2Register.setAdapter(pagerAdapter);
        vp2Register.setUserInputEnabled(false);

        init();
    }

    @Override
    public void onBackPressed() {
        if (vp2Register.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            vp2Register.setCurrentItem(vp2Register.getCurrentItem() - 1);
        }
    }

    private void init() {
        hideStatusBar();
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