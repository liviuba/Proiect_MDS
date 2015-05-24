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
    public int onStartCommand(Intent intent,int flags, int startId) {
        double latitude=0;
        double longitude=0;
        String numberToSendTo="";

        GPSTracker gps = new GPSTracker(getApplicationContext());
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
        }
        SmsManager smsManager = SmsManager.getDefault();
        SharedPreferences supervisorsFile = this.getSharedPreferences(superMApp.SUPERVISOR_FILENAME, 0);
        int supervisorsNum = supervisorsFile.getInt("SUPERVISORS_NUM", -100);
        Log.i("SmsNumber",Integer.toString(supervisorsNum));
        if( supervisorsNum != -100)
            for(int i=0; i<supervisorsNum; i++) {
                numberToSend = supervisorsFile.getString(Integer.toString(i), "Couldn't retrieve supervisor");
                if(!numberToSendTo.equals("Couldn't retrieve supervisor"))
                {
                    Log.i("SmsNumber", numberToSendTo);
                    //smsManager.sendTextMessage(numberToSend, null, "https://www.google.com/maps/@" + latitude + "," + longitude + ",18z", null, null);

                }
            }
        Toast.makeText(SmsStreamService.this, "!", Toast.LENGTH_SHORT).show();
        Log.i("SmsStreamService", "alarma");



        return START_STICKY;
    }


}



