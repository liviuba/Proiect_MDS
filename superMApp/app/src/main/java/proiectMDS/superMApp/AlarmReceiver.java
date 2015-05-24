package proiectMDS.superMApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String SOME_ACTION = "proiectMDS.superMApp.ACTION";
    public void onReceive(Context context, Intent intent) {
        Log.i("AlarmReceiver", "in AlarmReceiver");
        if (SOME_ACTION.equals(intent.getAction())) {
            Intent localIntent = new Intent(context, SmsStreamService.class);
           // intent.getStringArrayExtra("contacts");
//                intent.putExtra("number", "lat");
//            intent.putExtra("lng", "lng");
//            ArrayList<String> array = intent.getStringArrayListExtra("contactsArray");
//            String contacts = "";
//            for(String s: array){
//                contacts+=s+";";
//            }
//            Log.i("AlarmReceiver",contacts);
//            localIntent.putExtra("contactsArray",contacts);
            context.startService(localIntent);

        }
    }
}