package com.mobdeve.meditrak.data;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.meditrak.MainActivity;
import com.mobdeve.meditrak.R;

import java.util.ArrayList;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineHolder> {
    ArrayList<Medicine> medicines;
    private MainActivity activity;

    public MedicineAdapter(MainActivity activity) {
        this.activity = activity;
    }

    public MedicineAdapter(ArrayList<Medicine> medicines) {
        this.setMedicines(medicines);
    }

    @NonNull
    @Override
    public MedicineHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.medicine_row, parent, false);

        return new MedicineHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineHolder holder, int position) {
        holder.setMedicine(this.medicines.get(position));
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    public ArrayList<Medicine> getMedicines() {
        return medicines;
    }

    public void setMedicines(ArrayList<Medicine> medicines) {
        this.medicines = medicines;
        this.notifyDataSetChanged();
    }

    public class MedicineHolder extends RecyclerView.ViewHolder {
        private Medicine medicine;
        private View view;
        private MainActivity activity;

        protected TextView tvName, tvQty, tvStatus, tvInitial;

        public MedicineHolder(View view, @NonNull MainActivity activity) {
            super(view);

            this.view = itemView;
            this.tvName = this.view.findViewById(R.id.txtMedName);
            this.tvQty = this.view.findViewById(R.id.txtMedQuantity);
            this.tvStatus = this.view.findViewById(R.id.txtMedStatus);
            this.tvInitial = this.view.findViewById(R.id.txtMedInitial);
            this.activity = activity;
        }

        public Medicine getMedicine() {
            return medicine;
        }

        public void setMedicine(Medicine medicine) {
            this.medicine = medicine;

            this.tvName.setText(String.valueOf(this.medicine.getName()));
            this.tvInitial.setText("/ " + this.medicine.getInitial() + this.medicine.getMeasurement());
            this.tvQty.setText(String.valueOf(this.medicine.getQuantity()) + this.medicine.getMeasurement());

            if (this.medicine.getQuantity() == 0) {
                this.tvStatus.setText("EMPTY");
                this.tvStatus.setTextColor(this.view.getContext().getColor(R.color.danger));
            } else if (this.medicine.getQuantity() <= this.medicine.getInitial() * 0.15){
                this.tvStatus.setText("LOW");
                this.tvStatus.setTextColor(this.view.getContext().getColor(R.color.warning));
            } else {
                this.tvStatus.setText("NORMAL");
                this.tvStatus.setTextColor(this.view.getContext().getColor(R.color.fg_hint));
            }

            this.view.setOnClickListener(listener -> {
                Intent intent = new Intent(this.activity, MedicineActivity.class);
                intent.putExtra("medicine", this.medicine.getName());

                this.activity.startActivity(intent);
            });
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }
    }
}
