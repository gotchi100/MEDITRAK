package com.mobdeve.meditrak.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DB_VER = 9;
    private static final String DATABASE_NAME = "contactsManager";
    public static final String TABLE_MEDICINES = "medicines";
    public static final String MED_KEY_NAME = "name";
    public static final String MED_KEY_QTY = "quantity";
    public static final String MED_KEY_INITIAL_QTY = "initial_quantity";
    public static final String MED_KEY_MES = "measurement";
    public static final String MED_KEY_ADD = "added_at";

    public static final String TABLE_SCHED = "schedules";
    public static final String SCHED_KEY_ID = "id";
    public static final String SCHED_KEY_MED = "medicine";
    public static final String SCHED_KEY_TIME = "time";
    public static final String SCHED_KEY_DOSAGE = "dosage";
    public static final String SCHED_KEY_DAYS = "days";
    public static final String SCHED_KEY_LAST = "last_intake";
    public static final String SCHED_KEY_MISSED = "has_missed";
    public static final String SCHED_KEY_LATE = "is_late";
    public static final String SCHED_KEY_MEAS = "measurement";
    public static final String SCHED_KEY_ON = "is_on";

    public DatabaseHandler(Context ctx) {
        super(ctx, DATABASE_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEDCINE_TABLE = "CREATE TABLE " + TABLE_MEDICINES + "("
                + MED_KEY_NAME + " TEXT PRIMARY KEY,"
                + MED_KEY_QTY + " REAL, " + MED_KEY_MES + " TEXT, " + MED_KEY_INITIAL_QTY + " REAL, "
                + MED_KEY_ADD + " TIMESTAMP DEFAULT current_timestamp"+ ")";
        db.execSQL(CREATE_MEDCINE_TABLE);

        String CREATE_SCHEDULE_TABLE = "CREATE TABLE " + TABLE_SCHED + "("
                + SCHED_KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + SCHED_KEY_MED + " TEXT,"
                + SCHED_KEY_TIME + " TEXT, " + SCHED_KEY_DOSAGE + " REAL, "
                + SCHED_KEY_MEAS + " TEXT, " + SCHED_KEY_LAST + " TEXT, "
                + SCHED_KEY_ON + " INTEGER, " + SCHED_KEY_DAYS + " TEXT, "
                + SCHED_KEY_MISSED + " INTEGER, " + SCHED_KEY_LATE + " INTEGER " + ")";
        db.execSQL(CREATE_SCHEDULE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHED);

        onCreate(db);
    }

    public void clear() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_MEDICINES + " WHERE 1=1");
        db.execSQL("DELETE FROM " + TABLE_SCHED + " WHERE 1=1");

        db.close();
    }
    public Medicine getMedicine(String name) {
        Medicine m = new Medicine();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =  db.query(TABLE_MEDICINES, new String[]{"*"}, MED_KEY_NAME + "=?",
                new String[]{name}, null, null, null, null);

        if (cursor != null) cursor.moveToFirst();

        m.setName(cursor.getString(cursor.getColumnIndex(MED_KEY_NAME)));
        m.setQuantity(cursor.getFloat(cursor.getColumnIndex(MED_KEY_QTY)));
        m.setMeasurement(MeasurementEnum.getEnum(cursor.getString(cursor.getColumnIndex(MED_KEY_MES))));
        m.setInitial(cursor.getFloat(cursor.getColumnIndex(MED_KEY_INITIAL_QTY)));

        cursor.close();
        db.close();
        return m;
    }

    public ArrayList<Medicine> getMedicines(String filter, int limit) {
        ArrayList<Medicine> medicines = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDICINES, new String[]{"*"}, MED_KEY_NAME + " LIKE ?",
                new String[]{"%"+filter+"%"}, null, null, MED_KEY_ADD + " DESC", (limit == 0) ? null : String.valueOf(limit));

        if (cursor.moveToFirst()) {
            do {
                Medicine m = new Medicine();

                m.setName(cursor.getString(cursor.getColumnIndex(MED_KEY_NAME)));
                m.setQuantity(cursor.getFloat(cursor.getColumnIndex(MED_KEY_QTY)));
                m.setMeasurement(MeasurementEnum.getEnum(cursor.getString(cursor.getColumnIndex(MED_KEY_MES))));
                m.setInitial(cursor.getFloat(cursor.getColumnIndex(MED_KEY_INITIAL_QTY)));

                medicines.add(m);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return medicines;
    }

    public ArrayList<Medicine> getMedicines(int limit) {
        ArrayList<Medicine> medicines = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "
                + TABLE_MEDICINES + " ORDER BY " + MED_KEY_ADD + " DESC "
                + ((limit == 0) ? "" : " LIMIT " + limit), null);

        if (cursor.moveToFirst()) {
            do {
                Medicine m = new Medicine();

                m.setName(cursor.getString(cursor.getColumnIndex(MED_KEY_NAME)));
                m.setQuantity(cursor.getFloat(cursor.getColumnIndex(MED_KEY_QTY)));
                m.setMeasurement(MeasurementEnum.getEnum(cursor.getString(cursor.getColumnIndex(MED_KEY_MES))));
                m.setInitial(cursor.getFloat(cursor.getColumnIndex(MED_KEY_INITIAL_QTY)));

                medicines.add(m);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return medicines;
    }

    public ArrayList<Medicine> getLowMedicines(int limit) {
        ArrayList<Medicine> medicines = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEDICINES, new String[]{"*"},
                MED_KEY_QTY + " <= 0.15 * " + MED_KEY_INITIAL_QTY, null,
                null, null, MED_KEY_ADD + " DESC ",
                (limit == 0) ? null : String.valueOf(limit));

        if (cursor.moveToFirst()) {
            do {
                Medicine m = new Medicine();

                m.setName(cursor.getString(cursor.getColumnIndex(MED_KEY_NAME)));
                m.setQuantity(cursor.getFloat(cursor.getColumnIndex(MED_KEY_QTY)));
                m.setMeasurement(MeasurementEnum.getEnum(cursor.getString(cursor.getColumnIndex(MED_KEY_MES))));
                m.setInitial(cursor.getFloat(cursor.getColumnIndex(MED_KEY_INITIAL_QTY)));

                medicines.add(m);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return medicines;
    }

    public Schedule getSchedule(int id) {
        Schedule s = new Schedule();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor =  db.query(TABLE_SCHED, new String[]{"*"}, SCHED_KEY_ID + "=" + id,
                null, null, null, null, null);

        if (cursor != null) cursor.moveToFirst();

        if (cursor.getCount() > 0) {
            s.setId(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ID)));
            s.setMedicine(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MED)));
            s.setTime(cursor.getString(cursor.getColumnIndex(SCHED_KEY_TIME)));
            s.setDosage(cursor.getFloat(cursor.getColumnIndex(SCHED_KEY_DOSAGE)));
            s.setDays(cursor.getString(cursor.getColumnIndex(SCHED_KEY_DAYS)));
            s.setMeasurement(MeasurementEnum.getEnum(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MEAS))));

            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Schedule.SDF_TIMESTAMP.parse(cursor.getString(cursor.getColumnIndex(SCHED_KEY_LAST))));

                s.setLast_intake(calendar);
            } catch (Exception e) {
                s.setLast_intake(null);
            }
            s.setOn(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ON)) == 1);
            s.setMissed(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_MISSED)) == 1);
            s.setLate(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_LATE)) == 1);

            cursor.close();
            db.close();
            return s;
        } else {
            return null;
        }
    }

    public ArrayList<Schedule> getSchedules(int limit) {
        ArrayList<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Schedules ORDER BY time DESC, has_missed DESC, is_late DESC "
                + ((limit != 0) ? " LIMIT " + limit : ""), null);

        if (cursor.moveToFirst()) {
            do {
                Schedule s = new Schedule();

                s.setId(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ID)));
                s.setMedicine(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MED)));
                s.setTime(cursor.getString(cursor.getColumnIndex(SCHED_KEY_TIME)));
                s.setDosage(cursor.getFloat(cursor.getColumnIndex(SCHED_KEY_DOSAGE)));
                s.setDays(cursor.getString(cursor.getColumnIndex(SCHED_KEY_DAYS)));
                s.setMeasurement(MeasurementEnum.getEnum(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MEAS))));
                try {
                    Calendar calendar0 = Calendar.getInstance();
                    calendar0.setTime(Schedule.SDF_TIMESTAMP.parse(cursor.getString(cursor.getColumnIndex(SCHED_KEY_LAST))));

                    s.setLast_intake(calendar0);
                } catch (Exception e) {
                    s.setLast_intake(null);
                }
                s.setOn(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ON)) == 1);
                s.setMissed(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_MISSED)) == 1);
                s.setLate(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_LATE)) == 1);

                schedules.add(s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    public ArrayList<Schedule> getOnSchedules(int limit) {
        ArrayList<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Schedules WHERE " + SCHED_KEY_ON + " = 1"
                + ((limit != 0) ? " LIMIT " + limit : ""), null);

        if (cursor.moveToFirst()) {
            do {
                Schedule s = new Schedule();

                s.setId(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ID)));
                s.setMedicine(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MED)));
                s.setTime(cursor.getString(cursor.getColumnIndex(SCHED_KEY_TIME)));
                s.setDosage(cursor.getFloat(cursor.getColumnIndex(SCHED_KEY_DOSAGE)));
                s.setDays(cursor.getString(cursor.getColumnIndex(SCHED_KEY_DAYS)));
                s.setMeasurement(MeasurementEnum.getEnum(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MEAS))));
                try {
                    Calendar calendar0 = Calendar.getInstance();
                    calendar0.setTime(Schedule.SDF_TIMESTAMP.parse(cursor.getString(cursor.getColumnIndex(SCHED_KEY_LAST))));

                    s.setLast_intake(calendar0);
                } catch (Exception e) {
                    s.setLast_intake(null);
                }
                s.setOn(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ON)) == 1);
                s.setMissed(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_MISSED)) == 1);
                s.setLate(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_LATE)) == 1);

                schedules.add(s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    public ArrayList<Schedule> getSchedules(boolean on, String time, int limit) {
        ArrayList<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCHED, new String[]{"*"}, SCHED_KEY_ON + "=? AND " + SCHED_KEY_TIME + "=?",
                new String[]{String.valueOf(on ? 1 : 0), time}, null, null, null, (limit == 0) ? null : String.valueOf(limit));

        if (cursor.moveToFirst()) {
            do {
                Schedule s = new Schedule();

                s.setId(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ID)));
                s.setMedicine(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MED)));
                s.setTime(cursor.getString(cursor.getColumnIndex(SCHED_KEY_TIME)));
                s.setDosage(cursor.getFloat(cursor.getColumnIndex(SCHED_KEY_DOSAGE)));
                s.setDays(cursor.getString(cursor.getColumnIndex(SCHED_KEY_DAYS)));
                s.setMeasurement(MeasurementEnum.getEnum(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MEAS))));
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Schedule.SDF_TIMESTAMP.parse(cursor.getString(cursor.getColumnIndex(SCHED_KEY_LAST))));

                    s.setLast_intake(calendar);
                } catch (Exception e) {
                    s.setLast_intake(null);
                }
                s.setOn(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ON)) == 1);
                s.setMissed(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_MISSED)) == 1);
                s.setLate(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_LATE)) == 1);

                schedules.add(s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    public ArrayList<Schedule> getSchedules(boolean on, int limit) {
        ArrayList<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCHED, new String[]{"*"}, SCHED_KEY_ON + "=?",
                new String[]{String.valueOf(on ? 1 : 0)}, null, null, null, (limit == 0) ? null : String.valueOf(limit));

        if (cursor.moveToFirst()) {
            do {
                Schedule s = new Schedule();

                s.setId(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ID)));
                s.setMedicine(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MED)));
                s.setTime(cursor.getString(cursor.getColumnIndex(SCHED_KEY_TIME)));
                s.setDosage(cursor.getFloat(cursor.getColumnIndex(SCHED_KEY_DOSAGE)));
                s.setDays(cursor.getString(cursor.getColumnIndex(SCHED_KEY_DAYS)));
                s.setMeasurement(MeasurementEnum.getEnum(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MEAS))));
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Schedule.SDF_TIMESTAMP.parse(cursor.getString(cursor.getColumnIndex(SCHED_KEY_LAST))));

                    s.setLast_intake(calendar);
                } catch (Exception e) {
                    s.setLast_intake(null);
                }
                s.setOn(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ON)) == 1);
                s.setMissed(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_MISSED)) == 1);
                s.setLate(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_LATE)) == 1);

                schedules.add(s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    public ArrayList<Schedule> getSchedules(String medicine, int limit) {
        ArrayList<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCHED, new String[]{"*"}, SCHED_KEY_MED + "=?",
                new String[]{medicine}, null, null,
                SCHED_KEY_TIME + " DESC, " + SCHED_KEY_MISSED + " DESC, "
                        + SCHED_KEY_LATE + " DESC", (limit == 0) ? null : String.valueOf(limit));

        if (cursor.moveToFirst()) {
            do {
                Schedule s = new Schedule();

                s.setId(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ID)));
                s.setMedicine(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MED)));
                s.setTime(cursor.getString(cursor.getColumnIndex(SCHED_KEY_TIME)));
                s.setDosage(cursor.getFloat(cursor.getColumnIndex(SCHED_KEY_DOSAGE)));
                s.setDays(cursor.getString(cursor.getColumnIndex(SCHED_KEY_DAYS)));
                s.setMeasurement(MeasurementEnum.getEnum(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MEAS))));
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Schedule.SDF_TIMESTAMP.parse(cursor.getString(cursor.getColumnIndex(SCHED_KEY_LAST))));

                    s.setLast_intake(calendar);
                } catch (Exception e) {
                    s.setLast_intake(null);
                }
                s.setOn(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ON)) == 1);
                s.setMissed(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_MISSED)) == 1);
                s.setLate(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_LATE)) == 1);

                schedules.add(s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }

    public ArrayList<Schedule> getSchedules(Medicine medicine, int limit) {
        return this.getSchedules(medicine.getName(), limit);
    }

    public ArrayList<Schedule> getSchedulesMissedFirst(int limit) {
        ArrayList<Schedule> schedules = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String dayToday = Schedule.getDayToday();

        Cursor cursor = db.rawQuery("SELECT * FROM Schedules ORDER BY has_missed DESC, is_late DESC "
                + ((limit != 0) ? " LIMIT " + limit : ""), null);

        if (cursor.moveToFirst()) {
            do {
                Schedule s = new Schedule();

                s.setId(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ID)));
                s.setMedicine(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MED)));
                s.setTime(cursor.getString(cursor.getColumnIndex(SCHED_KEY_TIME)));
                s.setDosage(cursor.getFloat(cursor.getColumnIndex(SCHED_KEY_DOSAGE)));
                s.setDays(cursor.getString(cursor.getColumnIndex(SCHED_KEY_DAYS)));
                s.setMeasurement(MeasurementEnum.getEnum(cursor.getString(cursor.getColumnIndex(SCHED_KEY_MEAS))));
                try {
                    Calendar calendar0 = Calendar.getInstance();
                    calendar0.setTime(Schedule.SDF_TIMESTAMP.parse(cursor.getString(cursor.getColumnIndex(SCHED_KEY_LAST))));

                    s.setLast_intake(calendar0);
                } catch (Exception e) {
                    s.setLast_intake(null);
                }
                s.setOn(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_ON)) == 1);
                s.setMissed(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_MISSED)) == 1);
                s.setLate(cursor.getInt(cursor.getColumnIndex(SCHED_KEY_LATE)) == 1);

                schedules.add(s);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return schedules;
    }
}
