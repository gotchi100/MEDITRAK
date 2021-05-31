package com.mobdeve.meditrak;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mobdeve.meditrak.data.DatabaseHandler;
import com.mobdeve.meditrak.data.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.locks.ReentrantLock;

public class BackgroundService extends IntentService {

    private DatabaseHandler db;
    private ReentrantLock runMutex = new ReentrantLock();
    private boolean running;

    public BackgroundService() {
        super("BackgroundService");
    }
// +1-555-521-5554
    @Override
    protected void onHandleIntent(Intent intent) {
        int notifID = 0;
        SharedPreferences pref = getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE);
        String number;
        String name;
        boolean remind5MinBefore, remind10MinBefore, remind15MinBefore;

        SmsManager smsManager = SmsManager.getDefault();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notificationManager.getNotificationChannel("com.mobdeve.meditrak");

            if (mChannel == null) {
                mChannel = new NotificationChannel("com.mobdeve.meditrak", "intake", importance);
                mChannel.setDescription("intake");
                mChannel.enableVibration(true);
                mChannel.setLightColor(getColor(R.color.btn_primary));
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(mChannel);
            }
        }

        if (intent != null) {
            this.db=new DatabaseHandler(this.getApplicationContext());
            ArrayList<Schedule> schedules;
            ArrayList<Integer> notifiedIds = new ArrayList<>();
            ArrayList<Integer> notified5Ids = new ArrayList<>();
            ArrayList<Integer> notified10Ids = new ArrayList<>();
            ArrayList<Integer> notified15Ids = new ArrayList<>();

            int day = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            int day0;
            Calendar today;
            Calendar sched;

            runMutex.lock();
            running = true;
            while (running) {
                runMutex.unlock();
                try {
                    Log.i("BackgroundService", "service running");

                    number = pref.getString("phone", "");
                    name = pref.getString("name", "");

                    remind5MinBefore = pref.getBoolean("remind5MinBefore", false);
                    remind10MinBefore = pref.getBoolean("remind10MinBefore", false);
                    remind15MinBefore = pref.getBoolean("remind15MinBefore", false);

                    today = Calendar.getInstance();
                    today.set(Calendar.SECOND, 0);

                    sched = Calendar.getInstance();

                    day0 = today.get(Calendar.DAY_OF_YEAR);
                    schedules = this.db.getOnSchedules(0);

                    if (day != day0) {
                        notifiedIds.clear();
                        notified5Ids.clear();
                        notified10Ids.clear();
                        notified15Ids.clear();
                    }

                    for (Schedule s : schedules) {
                        Log.i("BackgroundService", s.getMedicine() + " - " + s.getDosage() + s.getMeasurement());
                        Log.i("BackgroundService", remind5MinBefore + " - " + remind10MinBefore + " - " + remind15MinBefore);
                        s.setDb(this.db);

                        sched.set(Calendar.HOUR_OF_DAY, s.getHour());
                        sched.set(Calendar.MINUTE, s.getMinute());
                        sched.set(Calendar.SECOND, 0);

                        if (s.intakeToday()) {
                            Log.i("BackgroundService", "Intake: " + s.getMedicine() + " - " + s.getDosage() + s.getMeasurement());
                            if (!notifiedIds.contains(s.getId())) {
                                if (remind15MinBefore && !notified15Ids.contains(s.getId())
                                        && sched.getTimeInMillis() - today.getTimeInMillis() <= 1000 * 60 * 15) {
                                    notifID = this.notifyIntake(notifID,s, notificationManager);

                                    notified15Ids.add(s.getId());
                                } else if (remind10MinBefore && !notified10Ids.contains(s.getId())
                                        && sched.getTimeInMillis() - today.getTimeInMillis() <= 1000 * 60 * 10) {
                                    notifID = this.notifyIntake(notifID,s, notificationManager);

                                    notified10Ids.add(s.getId());
                                } else if (remind5MinBefore && !notified5Ids.contains(s.getId())
                                        && sched.getTimeInMillis() - today.getTimeInMillis() <= 1000 * 60 * 5) {
                                    notifID = this.notifyIntake(notifID,s, notificationManager);

                                    notified5Ids.add(s.getId());
                                } else if (today.getTimeInMillis() - sched.getTimeInMillis() >= 0) {
                                    notifID = this.notifyIntake(notifID,s, notificationManager);

                                    notifiedIds.add(s.getId());
                                }
                            } else if (!s.isLate() && today.getTimeInMillis() - sched.getTimeInMillis() >= 1000 * 60 * 15) {
                                s.setLate(true);
                                s.update();
                                // send sms
                                smsManager.sendTextMessage(number, null,
                                        "Hey there! Looks like your friend " + name
                                                + " forgot to take their medicine:" + s.getMedicine()
                                                + ". Why don't you hit them up and let them know!", null, null);
                            } else if (!s.isMissed() && today.getTimeInMillis() - sched.getTimeInMillis() >= 1000 * 60 * 60) {
                                s.setMissed(true);
                                s.update();
                                // send sms
                                smsManager.sendTextMessage(number, null,
                                        "Hey! Looks like your friend, " + name
                                                + ", missed their chance to take their medicine:" + s.getMedicine()
                                                + ". Why don't you hit them up and let them know!", null, null);
                            }
                        }
                    }

                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runMutex.lock();
            }
            runMutex.unlock();
            Log.i("BackgroundService", "stopped");
        }
    }

    // +1-555-521-5554
    private int notifyIntake(int notifID, Schedule s, NotificationManagerCompat notificationManager) {
        // notify the user
        Intent intake = new Intent(this, IntakeBroadcastReceiver.class);
        intake.setAction("com.mobdeve.meditrak.intake." + notifID);
        intake.putExtra("schedule_intake", s.getId());
        NotificationCompat.Action action =
        new NotificationCompat.Action.Builder(
                0, "Intake", PendingIntent.getBroadcast(this, 0, intake,0)
        ).build();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "com.mobdeve.meditrak");
            builder
                .setContentTitle("Time to take " + s.getMedicine())
                .setContentText("Hey! Looks like its about time to take "
                        + s.getDosage() + s.getMeasurement()
                        + " ammount of " + s.getMedicine())
                .setSmallIcon(R.drawable.ic_notification)
                .addAction(action)
                .setAutoCancel(true)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

            notificationManager.notify(notifID++, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "com.mobdeve.meditrak")
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getColor(R.color.btn_primary))
                .setContentTitle("Time to take " + s.getMedicine())
                .setContentText("Hey! Looks like its about time to take "
                        + s.getDosage() + s.getMeasurement()
                        + " ammount of " + s.getMedicine())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .addAction(action)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(Notification.DEFAULT_ALL);

            notificationManager.notify(notifID++, builder.build());
        }

        return notifID;
    }

    @Override
    public void onDestroy() {
        runMutex.lock();
        running = false;
        runMutex.unlock();
        Log.i("BackgroundService", "stopping");
        super.onDestroy();
    }
}