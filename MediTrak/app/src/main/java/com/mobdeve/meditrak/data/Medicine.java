package com.mobdeve.meditrak.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

public class Medicine implements Parcelable {
    private String name;
    private float quantity, initial;
    private MeasurementEnum measurement;

    private DatabaseHandler db;

    public Medicine() {}

    public Medicine(DatabaseHandler db) {
        this.db = db;
    }

    protected Medicine(Parcel in) {
        name = in.readString();
        quantity = in.readFloat();
        measurement = MeasurementEnum.getEnum(in.readString());
        initial = in.readFloat();
    }

    public static final Creator<Medicine> CREATOR = new Creator<Medicine>() {
        @Override
        public Medicine createFromParcel(Parcel in) {
            return new Medicine(in);
        }

        @Override
        public Medicine[] newArray(int size) {
            return new Medicine[size];
        }
    };

    public boolean create() {
        boolean created;

        try (SQLiteDatabase _db = this.db.getWritableDatabase()) {
            _db.insertOrThrow(DatabaseHandler.TABLE_MEDICINES, null, this.values(false));
            created = true;
        } catch (SQLException e) {
            created = false;
            Log.e("Medicine(): ", Log.getStackTraceString(e));
        }

        return created;
    }

    public Medicine read(String name) {
        this.copy(this.db.getMedicine(name));
        return this;
    }

    public Medicine update() {
        SQLiteDatabase _db = this.db.getWritableDatabase();

        _db.update(DatabaseHandler.TABLE_MEDICINES, this.values(true),
                DatabaseHandler.MED_KEY_NAME + "=?", new String[]{this.getName()});

        ArrayList<Schedule> schedules = this.db.getSchedules(this.getName(), 0);
        for (Schedule s :
                schedules) {
            if (s.getMeasurement() != this.getMeasurement()) {
                s.setDb(this.getDb());
                s.setDosage(s.getMeasurement().convert(s.getDosage(), this.getMeasurement()));
                s.setMeasurement(this.getMeasurement());

                s.update();
            }
        }
        _db.close();

        return this;
    }

    public boolean delete() {
        boolean deleted;
        SQLiteDatabase _db = this.db.getWritableDatabase();

        deleted = _db.delete(DatabaseHandler.TABLE_MEDICINES,DatabaseHandler.MED_KEY_NAME + "=?",
                new String[]{this.getName()}) > 0;

        if (deleted)
            deleted = _db.delete(DatabaseHandler.TABLE_SCHED, DatabaseHandler.SCHED_KEY_MED + "=?", new String[]{this.getName()}) > 0;

        _db.close();
        return deleted;
    }

    public ContentValues values(boolean noIndex) {
         ContentValues vals = new ContentValues(3);

         if (!noIndex)
            vals.put(DatabaseHandler.MED_KEY_NAME, this.getName());
         vals.put(DatabaseHandler.MED_KEY_QTY, this.getQuantity());
         vals.put(DatabaseHandler.MED_KEY_MES, this.getMeasurement().toString());
         vals.put(DatabaseHandler.MED_KEY_INITIAL_QTY, this.getInitial());

         return vals;
    }

    public void copy(Medicine medicine) {
        this.setName(medicine.getName());
        this.setQuantity(medicine.getQuantity());
        this.setMeasurement(medicine.getMeasurement());
        this.setInitial(medicine.initial);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getQuantity() {
        return quantity;
    }

    public void setQuantity(float quantity) {
        this.quantity = quantity;
    }

    public MeasurementEnum getMeasurement() {
        return measurement;
    }

    public void setMeasurement(MeasurementEnum measurement) {
        this.measurement = measurement;
    }

    @Override
    public int describeContents() {
        return Parcelable.CONTENTS_FILE_DESCRIPTOR;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getName());
        parcel.writeFloat(this.getQuantity());
        parcel.writeString(this.getMeasurement().toString());
        parcel.writeFloat(this.getInitial());
    }

    public DatabaseHandler getDb() {
        return db;
    }

    public void setDb(DatabaseHandler db) {
        this.db = db;
    }

    public float getInitial() {
        return initial;
    }

    public void setInitial(float initial) {
        this.initial = initial;
    }
}
