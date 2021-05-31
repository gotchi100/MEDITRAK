package com.mobdeve.meditrak.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class Schedule implements Parcelable {
    public static final SimpleDateFormat SDF_TIMESTAMP = new SimpleDateFormat("LLL dd, yyyy hh:mm:ssa", Locale.ENGLISH);
    public static final SimpleDateFormat SDF_TIME = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

    private int id;
    private String medicine;
    private String time;
    private String days;
    private float dosage;
    private Calendar last_intake;
    private MeasurementEnum measurement;
    private boolean on, missed, late;

    private DatabaseHandler db;

    public Schedule(){
        this.missed = false;
        this.on = false;
        this.late = false;
    }

    public Schedule(DatabaseHandler db) {
        this.db = db;
    }

    protected Schedule(Parcel in) {
        this.setId(in.readInt());
        this.setMedicine(in.readString());
        this.setTime(in.readString());
        this.setDosage(in.readFloat());
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(SDF_TIMESTAMP.parse(in.readString()));

            this.setLast_intake(calendar);
        } catch (ParseException e) {
            this.setLast_intake(null);
        }
        this.setMeasurement(MeasurementEnum.getEnum(in.readString()));
        this.setOn(in.readByte() != 0);
        this.setMissed(in.readByte() != 0);
        this.setLate(in.readByte() != 0);
        this.setDays(in.readString());
    }

    public ContentValues values(boolean noIndex) {
         ContentValues vals = new ContentValues(8);

         if (!noIndex)
            vals.put(DatabaseHandler.SCHED_KEY_ID, this.getId());
         vals.put(DatabaseHandler.SCHED_KEY_DOSAGE, this.getDosage());
         vals.put(DatabaseHandler.SCHED_KEY_LAST, (this.getLast_intake() == null) ? null : Schedule.SDF_TIMESTAMP.format(this.getLast_intake().getTime()));
         vals.put(DatabaseHandler.SCHED_KEY_MEAS, this.getMeasurement().toString());
         vals.put(DatabaseHandler.SCHED_KEY_MED, this.getMedicine());
         vals.put(DatabaseHandler.SCHED_KEY_ON, this.isOn() ? 1 : 0);
         vals.put(DatabaseHandler.SCHED_KEY_TIME, this.getTime());
         vals.put(DatabaseHandler.SCHED_KEY_MISSED, this.isMissed() ? 1 : 0);
         vals.put(DatabaseHandler.SCHED_KEY_LATE, this.isLate() ? 1 : 0);
         vals.put(DatabaseHandler.SCHED_KEY_DAYS, this.getDays());


         return vals;
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public boolean create() {
        boolean created;

        try (SQLiteDatabase _db = this.db.getWritableDatabase()) {
            ContentValues values = this.values(true);
            values.put(DatabaseHandler.SCHED_KEY_MISSED, 0);
            values.put(DatabaseHandler.SCHED_KEY_LATE, 0);
            _db.insertOrThrow(DatabaseHandler.TABLE_SCHED, null, values);
            created = true;
        } catch (SQLException e) {
            created = false;
        }

        return created;
    }

    public Schedule read(int id) {
        this.copy(this.db.getSchedule(id));
        return this;
    }

    public Schedule update() {
        SQLiteDatabase _db = this.db.getWritableDatabase();

        _db.update(DatabaseHandler.TABLE_SCHED, this.values(true),
                DatabaseHandler.SCHED_KEY_ID + "=?", new String[]{String.valueOf(this.getId())});

        _db.close();

        return this;
    }

    public boolean delete() {
        boolean deleted;
        SQLiteDatabase _db = this.db.getWritableDatabase();

        deleted = _db.delete(DatabaseHandler.TABLE_SCHED,DatabaseHandler.SCHED_KEY_ID + "=?",
                new String[]{String.valueOf(this.getId())}) > 0;

        _db.close();
        return deleted;
    }

    public boolean intake() {
        try {
            Medicine medicine = this.getMedicineObject();
            medicine.setDb(this.getDb());

            medicine.setQuantity(medicine.getQuantity() - this.getDosage());
            medicine.update();

            this.setLate(false);
            this.setMissed(false);
            this.setLast_intake(Calendar.getInstance());

            this.update();
            return true;
        } catch (Exception e) {
            Log.e("Schedule.update()", Log.getStackTraceString(e));
            return false;
        }
    }

    public boolean intakeToday() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        if(last_intake != null && last_intake.get(Calendar.DAY_OF_WEEK) == day) return false;

        ArrayList<String> daysList = new ArrayList<>(Arrays.asList(this.days.split(" ")));

        switch (day) {
            case Calendar.MONDAY: return daysList.contains("M");
            case Calendar.TUESDAY: return daysList.contains("T");
            case Calendar.WEDNESDAY: return daysList.contains("W");
            case Calendar.THURSDAY: return daysList.contains("Th");
            case Calendar.FRIDAY: return daysList.contains("F");
            case Calendar.SATURDAY: return daysList.contains("S");
            case Calendar.SUNDAY: return daysList.contains("Su");
        }

        return false;
    }

    public boolean isLate() {
        if (this.late) return true;
        if (this.getLast_intake() == null || !this.intakeToday()) return false;
        Calendar nowCalendar = Calendar.getInstance();
        Calendar inCalendar = Calendar.getInstance();

        inCalendar.set(Calendar.HOUR_OF_DAY, this.getHour());
        inCalendar.set(Calendar.MINUTE, this.getMinute());

        // millis/sec sec/mins
        return ((nowCalendar.getTimeInMillis() - inCalendar.getTimeInMillis()) / 1000 / 60) >= 15;
    }

    public boolean isMissed() {
        if (this.missed) return  true;
        if (this.getLast_intake() == null || !this.intakeToday()) return false;
        Calendar nowCalendar = Calendar.getInstance();
        Calendar inCalendar = Calendar.getInstance();

        inCalendar.set(Calendar.HOUR_OF_DAY, this.getHour());
        inCalendar.set(Calendar.MINUTE, this.getMinute());

        // millis/sec sec/mins mins/hours
        return ((nowCalendar.getTimeInMillis() - inCalendar.getTimeInMillis()) / 1000 / 60 / 60) >= 1;
    }

    public void copy(Schedule schedule) {
        this.setId(schedule.getId());
        this.setMedicine(schedule.getMedicine());
        this.setLast_intake(schedule.getLast_intake());
        this.setTime(schedule.getTime());
        this.setMeasurement(schedule.getMeasurement());
        this.setOn(schedule.isOn());
        this.setDosage(schedule.getDosage());
        this.setMissed(schedule.isMissed());
        this.setMissed(schedule.isLate());
        this.setDays(schedule.getDays());
    }

    @Override
    public int describeContents() {
        return Parcelable.CONTENTS_FILE_DESCRIPTOR;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.getId());
        parcel.writeString(this.getMedicine());
        parcel.writeString(this.getTime());
        parcel.writeFloat(this.getDosage());
        parcel.writeString(SDF_TIMESTAMP.format(this.getLast_intake().getTime()));
        parcel.writeString(this.getMeasurement().toString());
        parcel.writeByte((byte) (this.isOn() ? 1 : 0));
        parcel.writeByte((byte) (this.isMissed() ? 1 : 0));
        parcel.writeByte((byte) (this.isLate() ? 1 : 0));
        parcel.writeString(this.getDays());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedicine() {
        return medicine;
    }

    public Medicine getMedicineObject() {
        return (new Medicine(this.getDb())).read(this.getMedicine());
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getTime() {
        return time;
    }

    public String getTimeDisplay() {
        String hour, minute, meridian;
        hour = this.getTime().split(":")[0];
        minute = this.getTime().split(":")[1];

        if (Integer.parseInt(hour) > 12) {
            meridian = "PM";
            hour = String.valueOf(Integer.parseInt(hour) - 12);
        } else {
            meridian = "AM";
        }

        if (Integer.parseInt(hour) < 10) hour = "0" + hour;
        if (Integer.parseInt(minute) < 10) minute = "0" + minute;

        return hour + ":" + minute + meridian;
    }

    public static String getDayToday() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.MONDAY: return "M";
            case Calendar.TUESDAY: return "T";
            case Calendar.WEDNESDAY: return "W";
            case Calendar.THURSDAY: return "Th";
            case Calendar.FRIDAY: return "F";
            case Calendar.SATURDAY: return "S";
            case Calendar.SUNDAY: return "Su";
        }

        return "M";
    }

    public int getHour() {
        return Integer.parseInt(this.getTime().split(":")[0]);
    }

    public int getMinute() {
        return Integer.parseInt(this.getTime().split(":")[1]);
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getDosage() {
        return dosage;
    }

    public void setDosage(float dosage) {
        this.dosage = dosage;
    }

    public Calendar getLast_intake() {
        return last_intake;
    }

    public void setLast_intake(Calendar last_intake) {
        this.last_intake = last_intake;
    }

    public MeasurementEnum getMeasurement() {
        return measurement;
    }

    public void setMeasurement(MeasurementEnum measurement) {
        this.measurement = measurement;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public DatabaseHandler getDb() {
        return db;
    }

    public void setDb(DatabaseHandler db) {
        this.db = db;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public void setMissed(boolean missed) {
        this.missed = missed;
    }

    public void setLate(boolean late) {
        this.late = late;
    }
}
