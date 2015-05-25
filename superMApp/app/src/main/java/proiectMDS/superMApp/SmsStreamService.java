package proiectMDS.superMApp;

/**
 * Created by Mara on 11/05/2015.
 */

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;

public class SmsStreamService extends Service {


    private String numberToSend;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent,flags,startId);
        double latitude = 0;
        double longitude = 0;
        String numberToSendTo = "";

        GPSTracker gps = new GPSTracker(getApplicationContext());
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> supervisorList = new ArrayList<String>();
        superMApp.initListFromFile(this, superMApp.SUPERVISOR_FILENAME, supervisorList);
        for(int i=0;i<supervisorList.size();i++){
					Log.e("ASDFTOMATO","In SmsStreamService: " + supervisorList.get(i));
            Log.i("SmsStreamService", supervisorList.get(i));
            numberToSendTo = supervisorList.get(i).split("~")[1];
            Log.i("SmsStreamService", numberToSendTo);
            smsManager.sendTextMessage(numberToSendTo, null, SmsListener.alertToken + ","+latitude+","+longitude+",https://www.google.com/maps/@" + latitude + "," + longitude + ",18z", null, null);
        }
        Toast.makeText(SmsStreamService.this, "!", Toast.LENGTH_SHORT).show();
            Log.i("SmsStreamService", "alarma");


            return START_STICKY;
        }


    }




