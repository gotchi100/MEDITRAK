package com.mobdeve.meditrak.HomeFragment.HomeMedicine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.meditrak.HomeFragment.HomeFragment;
import com.mobdeve.meditrak.MainActivity;
import com.mobdeve.meditrak.R;
import com.mobdeve.meditrak.data.DatabaseHandler;
import com.mobdeve.meditrak.data.MedicineAdapter;

public class HomeMedicineFragment extends Fragment {
    private HomeFragment homeFragment;
    private View view;

    private Button btnAddMed;
    private EditText etText;
    private RecyclerView rvMeds;
    private MedicineAdapter medAdapter;

    private DatabaseHandler db;
    public HomeMedicineFragment() {
        this.homeFragment = (HomeFragment) this.getParentFragment();
    }

    public HomeMedicineFragment(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;
        this.db = new DatabaseHandler(this.homeFragment.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_home_medicine, container, false);

        this.btnAddMed = this.view.findViewById(R.id.btnAdd);
        this.etText = this.view.findViewById(R.id.etMedName);

        this.rvMeds = this.view.findViewById(R.id.rvMeds);

        LinearLayoutManager llm = new LinearLayoutManager(this.homeFragment.getContext());
        this.medAdapter = new MedicineAdapter((MainActivity) this.getActivity());
        this.rvMeds.setLayoutManager(llm);
        this.rvMeds.setAdapter(this.medAdapter);

        this.etText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

        this.etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                medAdapter.setMedicines(db.getMedicines(editable.toString(), 0));
            }
        });

        this.btnAddMed.setOnClickListener(listener -> {
            Intent intent = new Intent(this.homeFragment.getContext(), HomeMedicineAddActivity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        String search = this.etText.getText().toString().trim().replaceAll("/\\s+/", " ");
        if (search.length() != 0)
            this.medAdapter.setMedicines(this.db.getMedicines(0));
        else
            this.medAdapter.setMedicines(this.db.getMedicines(search, 0));

        super.onResume();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) this.getActivity()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}