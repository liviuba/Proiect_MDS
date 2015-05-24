package proiectMDS.superMApp;

/**
 * Created by Mara on 11/05/2015.
 */
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class SmsStreamService extends Service {

    String numberToSend="";
    @Override
    public void onCreate() {

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

//        String lat = intent.getStringExtra("lat");
//        String lng = intent.getStringExtra("lng");
           // numberToSend = intent.getStringExtra("number");

//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage("0754319586", null, "trei24.44,24.44", null, null);

                 Toast.makeText(SmsStreamService.this, "!", Toast.LENGTH_SHORT).show();
                 Log.i("SmsStreamService", "alarma");



        return START_STICKY;
    }


}



