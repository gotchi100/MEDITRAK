package com.mobdeve.meditrak.RegisterActivity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RegisterPagerAdapter extends FragmentStateAdapter {
    private Register registerActivity;

    public RegisterPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.registerActivity = (Register) fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return new RegisterFragment(position, this.registerActivity);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
