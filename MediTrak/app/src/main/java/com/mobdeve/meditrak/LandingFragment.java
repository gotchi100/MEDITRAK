package com.mobdeve.meditrak;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.mobdeve.meditrak.RegisterActivity.Register;

public class LandingFragment extends Fragment {
    private MainActivity parent;

    public LandingFragment(MainActivity mainActivity) {
        this.parent = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_landing, container, false);

        Button btnRegister = fragment.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(listener -> {
            Intent intent = new Intent(this.parent, Register.class);
            startActivity(intent);
        });

        return fragment;
    }
}