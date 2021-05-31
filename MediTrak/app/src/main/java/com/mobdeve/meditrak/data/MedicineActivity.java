package com.mobdeve.meditrak.data;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.meditrak.HomeFragment.HomeMedicine.HomeMedicineAddActivity;
import com.mobdeve.meditrak.HomeFragment.HomeSchedule.HomeScheduleAddActivity;
import com.mobdeve.meditrak.R;

public class MedicineActivity extends AppCompatActivity {

    public String medName;
    public Medicine medicine;
    protected TextView tvName, tvQty, tvStatus, tvInitial;

    private Button btnAdd, btnRefill, btnDelete;
    public RecyclerView rvSchedules;
    public ScheduleAdapter schedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        medName = this.getIntent().getStringExtra("medicine");

        this.medicine = new Medicine();
        this.medicine.setDb(new DatabaseHandler(this));

        this.tvName = this.findViewById(R.id.txtMedName);
        this.tvQty = this.findViewById(R.id.txtMedQuantity);
        this.tvStatus = this.findViewById(R.id.txtMedStatus);
        this.tvInitial = this.findViewById(R.id.txtMedInitial);
        this.rvSchedules = this.findViewById(R.id.rvSchedules);
        this.btnAdd = this.findViewById(R.id.btnAddSchedInMed);
        this.btnRefill = this.findViewById(R.id.btnRefill);
        this.btnDelete = this.findViewById(R.id.btnDelete);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        this.schedAdapter = new ScheduleAdapter(this);
        this.rvSchedules.setLayoutManager(llm);
        this.rvSchedules.setAdapter(this.schedAdapter);


        this.btnRefill.setOnClickListener(listener -> {
            Intent intent = new Intent(this, HomeMedicineAddActivity.class);
            intent.putExtra("medicine", this.medicine);

            startActivity(intent);
        });

        this.btnDelete.setOnClickListener(listener -> {
            new AlertDialog.Builder(this)
                .setTitle("Deleting " + this.medicine.getName())
                .setMessage("Do you really want to delete this medicine?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    this.medicine.delete();
                    this.finish();
                })
                 .setNegativeButton(android.R.string.no, null).show();
        });

        this.btnAdd.setOnClickListener(listener -> {

            Intent intent = new Intent(this, HomeScheduleAddActivity.class);
            intent.putExtra("medicine", this.medicine);

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        this.medicine.read(this.medName);
        this.tvName.setText(String.valueOf(this.medicine.getName()));
        this.tvInitial.setText("/ " + this.medicine.getInitial() + this.medicine.getMeasurement());
        this.tvQty.setText(String.valueOf(this.medicine.getQuantity()) + this.medicine.getMeasurement());

        this.schedAdapter.setSchedules(this.medicine.getDb().getSchedules(this.medicine, 0));
        if (this.medicine.getQuantity() == 0) {
            this.tvStatus.setText("EMPTY");
            this.tvStatus.setTextColor(this.getColor(R.color.danger));
        } else if (this.medicine.getQuantity() <= this.medicine.getInitial() * 0.15){
            this.tvStatus.setText("LOW");
            this.tvStatus.setTextColor(this.getColor(R.color.warning));
        } else {
            this.tvStatus.setText("NORMAL");
            this.tvStatus.setTextColor(this.getColor(R.color.fg_hint));
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