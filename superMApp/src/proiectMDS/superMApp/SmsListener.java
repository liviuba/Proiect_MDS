package proiectMDS.superMApp;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by andrei on 03.05.2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by andrei on 03.05.2015.
 */
public class SmsListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final SmsManager sms = SmsManager.getDefault();
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);


                    // Show alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, "senderNum: "+ senderNum + ", MESSAGE: " + message, duration);
                    toast.show();

//                    MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
//                    //player.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
//                    player.setLooping(true);
//                    player.start();
//                    Intent int1 = new Intent(context,SmsReceived.class);
//                    context.startActivity(int1);
//                    Intent serviceIntent = new Intent(context,MyService.class);
//                    context.startService(serviceIntent);
                    Intent serv = new Intent(context,MusicService.class);
                    context.startService(serv);


                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);

        }
    }
}