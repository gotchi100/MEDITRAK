package com.mobdeve.meditrak.data;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobdeve.meditrak.R;

import java.util.ArrayList;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleHolder> {
    private ArrayList<Schedule> schedules;
    private Activity activity;

    public ScheduleAdapter(Activity activity) {
        this.activity = activity;
    }

    public ScheduleAdapter(ArrayList<Schedule> schedules) {
        this.setSchedules(schedules);
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.schedule_row, parent, false);

        return new ScheduleHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleHolder holder, int position) {
        holder.setSchedule(this.schedules.get(position));
    }

    @Override
    public int getItemCount() {
        return this.getSchedules().size();
    }

    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
        this.notifyDataSetChanged();
    }

    public class ScheduleHolder extends RecyclerView.ViewHolder{
        private Activity activity;
        private View view;
        private Schedule schedule;

        protected TextView tvSchedTime, tvSchedMed, tvStatus;
        protected ToggleButton tbOn;

        public ScheduleHolder(@NonNull View itemView, Activity activity) {
            super(itemView);

            this.view = itemView;
            this.view.setOnClickListener(listener -> {
                Intent intent = new Intent(this.view.getContext(), ScheduleActivity.class);
                intent.putExtra("schedule", this.schedule.getId());

                this.activity.startActivity(intent);
            });

            this.tvSchedTime = this.view.findViewById(R.id.tvSchedTime);
            this.tvSchedMed = this.view.findViewById(R.id.tvSchedMed);
            this.tvStatus = this.view.findViewById(R.id.tvStatus);
            this.tbOn = this.view.findViewById(R.id.tbOn);
            this.activity = activity;
        }

        public Activity getActivity() {
            return activity;
        }

        public void setActivity(Activity activity) {
            this.activity = activity;
        }

        public Schedule getSchedule() {
            return schedule;
        }

        public void setSchedule(Schedule schedule) {
            this.schedule = schedule;
            this.schedule.setDb(new DatabaseHandler(this.activity));

            this.tvSchedTime.setText(this.schedule.getTimeDisplay());
            this.tvSchedMed.setText(this.schedule.getMedicine() + " - "
                    + this.schedule.getDosage() + this.schedule.getMeasurement());

            this.tbOn.setChecked(this.schedule.isOn());

            if (this.schedule.isMissed()) {
                this.tvStatus.setVisibility(View.VISIBLE);
                this.tvStatus.setText("- Missed");
                this.tvStatus.setTextColor(this.activity.getColor(R.color.danger));
            } else if (this.schedule.isLate()) {
                this.tvStatus.setVisibility(View.VISIBLE);
                this.tvStatus.setText("- Late");
                this.tvStatus.setTextColor(this.activity.getColor(R.color.warning));
            }

            this.tbOn.setOnClickListener(listener -> {
                this.schedule.setOn(!this.schedule.isOn());
                this.schedule.update();
            });
        }
    }
}
