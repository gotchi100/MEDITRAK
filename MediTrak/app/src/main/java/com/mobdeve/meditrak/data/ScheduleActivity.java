package com.mobdeve.meditrak.data;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.mobdeve.meditrak.HomeFragment.HomeMedicine.HomeMedicineAddActivity;
import com.mobdeve.meditrak.HomeFragment.HomeSchedule.HomeScheduleAddActivity;
import com.mobdeve.meditrak.R;

public class ScheduleActivity extends AppCompatActivity {

    private int nID;
    private Schedule s;
    private Medicine m;

    private TextView tvSchedTime, tvSchedMed, tvDays, tvMedName, tvMedStatus, tvMedQty, tvMedInitial, tvLast;
    private Button btnIntake, btnSchedDelete, btnMedRefill, btnUpdate;
    private ToggleButton tbOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        this.tvSchedTime = findViewById(R.id.tvSchedTime);
        this.tvSchedMed = findViewById(R.id.tvSchedMed);
        this.tvDays = findViewById(R.id.tvDays);
        this.tvMedName = findViewById(R.id.tvMedName);
        this.tvMedStatus = findViewById(R.id.tvMedStatus);
        this.tvLast = findViewById(R.id.tvLast);
        this.tvMedQty = findViewById(R.id.tvMedQty);
        this.tvMedInitial = findViewById(R.id.tvMedInitial);
        this.btnIntake = findViewById(R.id.btnIntake);
        this.btnSchedDelete = findViewById(R.id.btnSchedDelete);
        this.btnMedRefill = findViewById(R.id.btnMedRefill);
        this.tbOn = findViewById(R.id.tbOn);
        this.btnUpdate = findViewById(R.id.btnUpdate);

        this.nID = this.getIntent().getIntExtra("schedule", 0);
        this.s = new Schedule(new DatabaseHandler(this));

        this.btnIntake.setOnClickListener(listener -> {
            if (this.s.intake())
                this.btnIntake.setEnabled(false);
        });

        this.btnMedRefill.setOnClickListener(listener -> {
            Intent intent = new Intent(this, HomeMedicineAddActivity.class);
            intent.putExtra("medicine", this.m);

            startActivity(intent);
        });

        this.btnUpdate.setOnClickListener(listener -> {
            Intent intent = new Intent(this, HomeScheduleAddActivity.class);
            intent.putExtra("schedule", this.s);

            startActivity(intent);
        });

        this.btnSchedDelete.setOnClickListener(listener -> {
            new AlertDialog.Builder(this)
                .setTitle("Deleting " + this.s.getTimeDisplay() +  " schedule for " + this.m.getName())
                .setMessage("Do you really want to delete this schedule?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    this.s.delete();
                    this.finish();
                })
                 .setNegativeButton(android.R.string.no, null).show();
        });

        this.tbOn.setOnClickListener(listener -> {
            this.s.setOn(!this.s.isOn());
            this.s.update();
        });
    }

    @Override
    protected void onResume() {
        this.s.read(this.nID);
        this.m = this.s.getMedicineObject();

        this.tvSchedTime.setText(this.s.getTimeDisplay());
        this.tvSchedMed.setText(this.s.getMedicine() + " - " + this.s.getDosage() + this.s.getMeasurement());

        this.tvMedName.setText(this.s.getMedicine());
        this.tvMedQty.setText(this.m.getQuantity() + this.m.getMeasurement().toString());
        this.tvMedInitial.setText("/ " +  this.m.getInitial() + this.m.getMeasurement().toString());

        this.tvLast.setVisibility(View.VISIBLE);
        if (this.s.getLast_intake() != null)
            this.tvLast.setText("Last Intake: " + Schedule.SDF_TIMESTAMP.format(this.s.getLast_intake().getTime()));
        else
            this.tvLast.setVisibility(View.GONE);

        this.tbOn.setChecked(this.s.isOn());
        if (this.m.getQuantity() == 0) {
            this.tvMedStatus.setText("EMPTY");
            this.tvMedStatus.setTextColor(getColor(R.color.danger));
        } else if (this.m.getQuantity() <= this.m.getInitial() * 0.15){
            this.tvMedStatus.setText("LOW");
            this.tvMedStatus.setTextColor(getColor(R.color.warning));
        } else {
            this.tvMedStatus.setText("NORMAL");
            this.tvMedStatus.setTextColor(getColor(R.color.fg_hint));
        }

        if (this.s.intakeToday() || this.s.isLate() || this.s.isMissed()) {
            this.btnIntake.setEnabled(true);
        } else {
            this.btnIntake.setEnabled(false);
        }

        this.tvDays.setText("");
        for (String s :
                this.s.getDays().split(" ")) {
            if (s.equals("M")) {
                this.tvDays.setText(this.tvDays.getText() + "Monday\n");
            } else if (s.equals("T")) {
                this.tvDays.setText(this.tvDays.getText() + "Tuesday\n");
            } else if (s.equals("W")) {
                this.tvDays.setText(this.tvDays.getText() + "Wednesday\n");
            } else if (s.equals("Th")) {
                this.tvDays.setText(this.tvDays.getText() + "Thursday\n");
            } else if (s.equals("F")) {
                this.tvDays.setText(this.tvDays.getText() + "Friday\n");
            } else if (s.equals("S")) {
                this.tvDays.setText(this.tvDays.getText() + "Saturday\n");
            } else if (s.equals("Su")) {
                this.tvDays.setText(this.tvDays.getText() + "Sunday\n");
            }
        }
        hideStatusBar();
        super.onResume();
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