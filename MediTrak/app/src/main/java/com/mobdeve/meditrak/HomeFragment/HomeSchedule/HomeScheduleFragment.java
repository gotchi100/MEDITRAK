package com.mobdeve.meditrak.HomeFragment.HomeSchedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.meditrak.HomeFragment.HomeFragment;
import com.mobdeve.meditrak.R;
import com.mobdeve.meditrak.data.DatabaseHandler;
import com.mobdeve.meditrak.data.ScheduleAdapter;

public class HomeScheduleFragment extends Fragment {
    private HomeFragment homeFragment;
    private View view;

    private RecyclerView rvScheds;
    private ScheduleAdapter adapter;

    private DatabaseHandler db;
    public HomeScheduleFragment() {
        this.homeFragment = (HomeFragment) this.getParentFragment();
    }
    public HomeScheduleFragment(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
        this.db = new DatabaseHandler(this.homeFragment.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_home_schedule, container, false);
        this.rvScheds = this.view.findViewById(R.id.rvScheds);

        LinearLayoutManager llm = new LinearLayoutManager(this.homeFragment.getContext());
        this.adapter = new ScheduleAdapter(this.getActivity());
        this.rvScheds.setLayoutManager(llm);
        this.rvScheds.setAdapter(this.adapter);

        return view;
    }

    @Override
    public void onResume() {
        this.adapter.setSchedules(this.db.getSchedules(0));

        super.onResume();
    }
}