package com.mobdeve.meditrak;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mobdeve.meditrak.data.DatabaseHandler;
import com.mobdeve.meditrak.data.Schedule;

import java.util.regex.Pattern;

public class IntakeBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("IntakeBroadcastReceiver", "here");
        if(Pattern.matches("^com\\.mobdeve\\.meditrak\\.intake\\.\\d+$", intent.getAction())) {
            int nId = intent.getIntExtra("schedule_intake", 0);
            Schedule s = new Schedule(new DatabaseHandler(context));
            s.read(nId);

            s.intake();
        }
    }
}
