package com.mobdeve.meditrak.HomeFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.meditrak.MainActivity;
import com.mobdeve.meditrak.R;
import com.mobdeve.meditrak.data.DatabaseHandler;
import com.mobdeve.meditrak.data.MedicineAdapter;
import com.mobdeve.meditrak.data.ScheduleAdapter;

import java.util.Calendar;

public class HomeMainFragment extends Fragment {
    private DatabaseHandler db;
    private SharedPreferences pref;
    private HomeFragment homeFragment;

    private RecyclerView rvMeds, rvScheds;
    private TextView txtGreet, txtName, txtViewMeds, txtViewSched;
    private ImageView img;

    private MedicineAdapter medAdapter;
    private ScheduleAdapter schedAdapter;

    public HomeMainFragment() {
        this.homeFragment = (HomeFragment) this.getParentFragment();
    }
    public HomeMainFragment(HomeFragment homeFragment){
        this.homeFragment = homeFragment;
        this.db = new DatabaseHandler(this.homeFragment.getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_main, container, false);

        this.rvMeds = view.findViewById(R.id.rvMeds);
        this.txtName = view.findViewById(R.id.txtName);
        this.rvScheds = view.findViewById(R.id.rvScheds);
        this.txtGreet = view.findViewById(R.id.txtGreet);
        this.img = view.findViewById(R.id.imgHomeHeader);
        this.txtViewMeds = view.findViewById(R.id.txtViewMeds);
        this.txtViewSched = view.findViewById(R.id.txtViewSched);

        this.pref = this.getActivity().getSharedPreferences(getString(R.string.shared_pref), Context.MODE_PRIVATE);
        LinearLayoutManager llm = new LinearLayoutManager(this.homeFragment.getContext());
        LinearLayoutManager llm2 = new LinearLayoutManager(this.homeFragment.getContext());

        this.medAdapter = new MedicineAdapter((MainActivity) this.getActivity());
        this.rvMeds.setLayoutManager(llm);
        this.rvMeds.setAdapter(this.medAdapter);

        this.schedAdapter = new ScheduleAdapter((MainActivity) this.getActivity());
        this.rvScheds.setLayoutManager(llm2);
        this.rvScheds.setAdapter(this.schedAdapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        String[] greets = getResources().getStringArray(R.array.home_greetings);
        int selected = (int) (Math.random() * (greets.length));

        txtGreet.setText(greets[selected] + ",");
        txtName.setText(pref.getString("name", "Name").split(" ")[0]);

        txtViewSched.setOnClickListener(listener -> this.homeFragment.navigationView.setSelectedItemId(R.id.nav_schedule));
        txtViewMeds.setOnClickListener(listener -> this.homeFragment.navigationView.setSelectedItemId(R.id.nav_pill));

        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.HOUR_OF_DAY) < 12)
            img.setImageResource(R.drawable.ic_morning);
        else if (now.get(Calendar.HOUR_OF_DAY) < 18)
            img.setImageResource(R.drawable.ic_afternoon);
        else if (now.get(Calendar.HOUR_OF_DAY) < 24)
            img.setImageResource(R.drawable.ic_evening);

        this.medAdapter.setMedicines(this.db.getMedicines(4));
        this.schedAdapter.setSchedules(this.db.getSchedulesMissedFirst(4));

        super.onResume();
    }
}