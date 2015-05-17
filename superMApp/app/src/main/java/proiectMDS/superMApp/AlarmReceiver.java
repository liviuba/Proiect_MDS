package proiectMDS.superMApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        String SOMEACTION = "proiectMDS.superMApp.ACTION";
        Log.i("AlarmReceiver", "in AlarmReceiver");
        if (SOMEACTION.equals(intent.getAction())) {
            Intent localIntent = new Intent(context, SmsStreamService.class);
           // intent.getStringArrayExtra("contacts");
//                intent.putExtra("number", "lat");
//            intent.putExtra("lng", "lng");
            context.startService(localIntent);

        }
    }
}