package com.mobdeve.meditrak.HomeFragment.HomeSchedule;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.mobdeve.meditrak.R;
import com.mobdeve.meditrak.TimePickerFragment;
import com.mobdeve.meditrak.data.DatabaseHandler;
import com.mobdeve.meditrak.data.Medicine;
import com.mobdeve.meditrak.data.Schedule;

import java.util.concurrent.atomic.AtomicBoolean;

public class HomeScheduleAddActivity extends AppCompatActivity {

    private Medicine medicine;
    private Schedule schedule;

    private Button btnGo, btnBack, btnTime;
    private TextView tvGreet, tvContent, tvDosage, tvTimeVal, tvTime;
    private EditText etDosage;

    private CheckBox cbM, cbT, cbW, cbTh, cbF, cbS, cbSu;
    private AtomicBoolean timeWasSet = new AtomicBoolean(false);

    @Override
    protected void onResume() {
        hideStatusBar();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_schedule_add);

        Schedule sched_to_create = new Schedule(new DatabaseHandler(this));

        this.etDosage = findViewById(R.id.etDosage);
        this.tvGreet = findViewById(R.id.tvGreet);
        this.tvContent = findViewById(R.id.tvContent);
        this.tvDosage = findViewById(R.id.tvDosage);
        this.tvTime = findViewById(R.id.tvTime);
        this.btnGo = findViewById(R.id.btnGo);
        this.btnBack = findViewById(R.id.btnBack);
        this.tvTimeVal = this.findViewById(R.id.tvTimeVal);
        this.btnTime = this.findViewById(R.id.btnTime);
        this.cbM = findViewById(R.id.cbM);
        this.cbT = findViewById(R.id.cbT);
        this.cbW = findViewById(R.id.cbW);
        this.cbTh = findViewById(R.id.cbTh);
        this.cbF = findViewById(R.id.cbF);
        this.cbS = findViewById(R.id.cbS);
        this.cbSu = findViewById(R.id.cbSu);

        this.schedule = getIntent().getParcelableExtra("schedule");

        if (this.schedule != null) {
            this.schedule.setDb(new DatabaseHandler(this));
            this.medicine = this.schedule.getMedicineObject();

            this.tvContent.setText(R.string.sched_update_content);
            this.tvGreet.setText(R.string.sched_update_header);

            this.etDosage.setText(String.valueOf(this.schedule.getDosage()));
            this.tvTimeVal.setText(this.schedule.getTimeDisplay());
            for (String s : this.schedule.getDays().split(" ")) {
                switch (s) {
                    case "M":
                        this.cbM.setChecked(true);
                        break;
                    case "T":
                        this.cbT.setChecked(true);
                        break;
                    case "W":
                        this.cbW.setChecked(true);
                        break;
                    case "Th":
                        this.cbTh.setChecked(true);
                        break;
                    case "S":
                        this.cbS.setChecked(true);
                        break;
                    case "Su":
                        this.cbSu.setChecked(true);
                        break;
                }
            }
        } else {
            this.medicine = getIntent().getParcelableExtra("medicine");
        }

        this.tvContent.setText(this.tvContent.getText().toString().replaceAll("\\$meds", this.medicine.getName()));

        this.tvDosage.setText(this.tvDosage.getText().toString().replaceAll("\\$meds", this.medicine.getName()));
        this.tvTime.setText(this.tvTime.getText().toString().replaceAll("\\$meds", this.medicine.getName()));

        this.tvDosage.setText(this.tvDosage.getText().toString().replaceAll("\\$meas", this.medicine.getMeasurement().toString()));
        this.tvTime.setText(this.tvTime.getText().toString().replaceAll("\\$meas", this.medicine.getMeasurement().toString()));

        btnTime.setOnClickListener(listener -> {
            DialogFragment newFragment = new TimePickerFragment(sched_to_create, tvTimeVal, timeWasSet);
            newFragment.show(getSupportFragmentManager(), "timePicker");
        });

        btnBack.setOnClickListener(listener -> finish());
        btnGo.setOnClickListener(listener -> {
            this.cbM.setError(null);
            this.tvTimeVal.setError(null);
            this.etDosage.setError(null);

            String day = this.getDays();
            if (this.schedule == null) {
                sched_to_create.setMedicine(this.medicine.getName());

                if (day.length() > 0) {
                    if (this.tvTimeVal.getText().length() != 0) {
                        try {
                            float dosage = Float.parseFloat(this.etDosage.getText().toString());

                            sched_to_create.setDosage(dosage);
                            sched_to_create.setOn(true);
                            sched_to_create.setLast_intake(null);
                            sched_to_create.setMeasurement(this.medicine.getMeasurement());
                            sched_to_create.setDays(day);
                            sched_to_create.setMissed(false);

                            sched_to_create.create();
                            finish();
                        } catch (Exception e) {
                            Log.e("er", Log.getStackTraceString(e));
                            this.etDosage.setError("Hey! You need a number here!");
                        }
                    } else {
                        this.tvTimeVal.setError("Uh oh! You need to set a time first!");
                    }
                } else {
                    this.cbM.setError("Oops! You need to choose the days for the reminder!");
                }
            } else {
                sched_to_create.setMedicine(this.medicine.getName());

                if (day.length() > 0) {
                    if (this.tvTimeVal.getText().length() != 0) {
                        try {
                            float dosage = Float.parseFloat(this.etDosage.getText().toString());

                            if (timeWasSet.get())
                                this.schedule.setTime(sched_to_create.getTime());

                            this.schedule.setDays(this.getDays());
                            this.schedule.setDosage(dosage);

                            this.schedule.update();
                            finish();
                        } catch (Exception e) {
                            Log.e("HomeScheduleAddActivity", Log.getStackTraceString(e));
                            this.etDosage.setError("Hey! You need a number here!");
                        }
                    } else {
                        this.tvTimeVal.setError("Uh oh! You need to set a time first!");
                    }
                } else {
                    this.cbM.setError("Oops! You need to choose the days for the reminder!");
                }
            }
        });
    }

    public String getDays() {
        String day = "";

        if (this.cbM.isChecked())
            day = "M";

        if (this.cbT.isChecked())
            day += (day.length() == 0) ? "T" : " T";

        if (this.cbW.isChecked())
            day += (day.length() == 0) ? "W" : " W";

        if (this.cbTh.isChecked())
            day += (day.length() == 0) ? "Th" : " Th";

        if (this.cbF.isChecked())
            day += (day.length() == 0) ? "F" : " F";

        if (this.cbS.isChecked())
            day += (day.length() == 0) ? "S" : " S";

        if (this.cbSu.isChecked())
            day += (day.length() == 0) ? "Su" : " Su";

        return day;
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