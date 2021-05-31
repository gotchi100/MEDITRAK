package com.mobdeve.meditrak.HomeFragment.HomeMedicine;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mobdeve.meditrak.R;
import com.mobdeve.meditrak.data.DatabaseHandler;
import com.mobdeve.meditrak.data.MeasurementEnum;
import com.mobdeve.meditrak.data.Medicine;

public class HomeMedicineAddActivity extends AppCompatActivity {

    private Medicine medicine;
    private EditText etName, etQuantity;
    private Spinner spMeasurement;
    private Button btnBack, btnGo;

    private TextView tvGreet, tvContent1, tvContent2;
    public HomeMedicineAddActivity(){}

    @Override
    protected void onResume() {
        hideStatusBar();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_medicine_add);

        this.medicine = getIntent().getParcelableExtra("medicine");

        this.etName = findViewById(R.id.etName);
        this.etQuantity = findViewById(R.id.etQuantity);
        this.spMeasurement = findViewById(R.id.spMeasurement);
        this.btnBack = findViewById(R.id.btnBack);
        this.btnGo = findViewById(R.id.btnGo);
        this.tvGreet = findViewById(R.id.tvGreet);
        this.tvContent1 = findViewById(R.id.tvContent);
        this.tvContent2 = findViewById(R.id.tvContent2);

        this.etName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });
        this.etQuantity.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

        this.hideStatusBar();

        if (this.medicine == null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.measurements,
                        android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            this.spMeasurement.setAdapter(adapter);
        }

        if (this.medicine != null) {
            int pos = 0;

            tvGreet.setText(R.string.med_refill_header);
            tvContent1.setText(R.string.med_refill_content);
            tvContent2.setText(R.string.med_refill_content1);

            MeasurementEnum[] measurements = this.medicine.getMeasurement().validConversions();

            ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item);
            for(int i = 0; i < measurements.length; i++) {
                if (measurements[pos].toString().compareToIgnoreCase(this.medicine.getMeasurement().toString()) == 0)
                    pos = i;

                adapter.add(measurements[i].toString());
            }

            if (pos + 1 < measurements.length) pos = pos + 1;

            this.spMeasurement.setAdapter(adapter);

            this.etName.setText(this.medicine.getName());
            this.etName.setEnabled(false);

            this.spMeasurement.setSelection(pos, true);

            this.btnGo.setText("Refill");
        }

        this.btnBack.setOnClickListener(listener -> {
            this.finish();
        });

        this.btnGo.setOnClickListener(listener -> {
            String name = this.etName.getText().toString().trim().replaceAll("/\\s+/", " ");

            if (name.length() > 0) {
                try {
                    float num = Float.parseFloat(this.etQuantity.getText().toString());
                    MeasurementEnum meas = MeasurementEnum.getEnum(this.spMeasurement.getSelectedItem().toString());

                    if (medicine == null) {
                        Medicine meds = new Medicine(new DatabaseHandler(this));
                        meds.setName(name);
                        meds.setQuantity(num);
                        meds.setInitial(num);
                        meds.setMeasurement(meas);

                        if (meds.create()) {
                            finish();
                        } else {
                            this.etName.setError("Oops! Looks like you already have meds with the same name!");
                        }
                    } else {
                        float converted = medicine.getMeasurement().convert(this.medicine.getQuantity(), meas);

                        Log.d("converted", String.valueOf(converted));
                        medicine.setDb(new DatabaseHandler(this));
                        medicine.setQuantity(converted + num);
                        medicine.setInitial(converted + num);
                        medicine.setMeasurement(meas);

                        medicine.update();

                        finish();
                    }
                } catch (Exception e) {
                    Log.e("Err", Log.getStackTraceString(e));
                    this.etQuantity.setError("Hey! You need a number here!");
                }
            } else {
                this.etName.setError("Uh oh! Your meds needs a name!");
            }
        });
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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}