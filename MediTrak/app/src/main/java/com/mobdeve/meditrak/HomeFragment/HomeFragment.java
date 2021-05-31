package com.mobdeve.meditrak.HomeFragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobdeve.meditrak.HomeFragment.HomeMedicine.HomeMedicineFragment;
import com.mobdeve.meditrak.HomeFragment.HomeSchedule.HomeScheduleFragment;
import com.mobdeve.meditrak.MainActivity;
import com.mobdeve.meditrak.R;

public class HomeFragment extends Fragment {

    public int pos;
    public int prevPos;

    private MainActivity parent;

    public BottomNavigationView navigationView;

    public HomeFragment() { this.parent = (MainActivity) this.getActivity(); }

    public HomeFragment(MainActivity mainActivity) {
        this.parent = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        this.navigationView = v.findViewById(R.id.nav);

        this.navigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selected = null;
            prevPos = pos;

            int nextPos = pos;

            switch (item.getItemId()) {
                case R.id.nav_home:
                    selected = new HomeMainFragment(this);
                    nextPos = 0;
                    break;
                case R.id.nav_schedule:
                    selected = new HomeScheduleFragment(this);
                    nextPos = 1;
                    break;
                case R.id.nav_pill:
                    selected = new HomeMedicineFragment(this);
                    nextPos = 2;
                    break;
                case R.id.nav_settings:
                    selected = new HomeSettingsFragment();
                    nextPos = 3;
                    break;
            }

            if (pos == nextPos)
                getActivity().getSupportFragmentManager()
                        .beginTransaction().replace(R.id.fcvHomeContainer, selected ).commitAllowingStateLoss();
            else if (pos > nextPos)
                getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right)
                        .replace(R.id.fcvHomeContainer, selected ).commitAllowingStateLoss();
            else
                getActivity().getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fcvHomeContainer, selected ).commitAllowingStateLoss();

            pos = nextPos;
            return true;
        });

        pos = 0;
        getActivity().getSupportFragmentManager()
                .beginTransaction().replace(R.id.fcvHomeContainer, new HomeMainFragment(this) ).commitAllowingStateLoss();
        return v;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) this.parent.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getView().getWindowToken(), 0);
    }
}