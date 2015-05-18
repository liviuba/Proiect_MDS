package proiectMDS.superMApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import com.google.android.gms.maps.model.LatLng;

/**
 * Created by andrei on 03.05.2015.
 */

    //Right now, in this stage, the GoogleMaps won't start again if
    // stopped because 'trackedList' will be lost.

public class SmsListener extends BroadcastReceiver {

    public static boolean soundStarted = false;
    private static final String alertToken = "trei";
    private static String message = null;
    private static String phoneNumber = null;

    @Override
    public void onReceive(Context context, Intent intent) {
			/***TEST **/
			
        final SmsManager sms = SmsManager.getDefault();
        final Bundle bundle = intent.getExtras();

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");


                //reading the SMS. In this stage, I am sure that the length of SMS
                //won't take more than one pdu object
                SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[0]);
                phoneNumber = currentMessage.getDisplayOriginatingAddress();
                message = currentMessage.getDisplayMessageBody();

                Log.i("SmsReceiver", "senderNum: " + phoneNumber + "; message: " + message);

                // start the alarm through a Service if the sms is a notification from
                //someone who is in danger and send the coordinates to GoogleMaps


								//FIXME: tests. Replace w/ previous when persistent storage gets done
                //if (someoneInDanger() && notAJoke()) {
								Log.e("ASDFTOMATO", "1");
								if (true) {
										Log.e("ASDFTOMATO", "2");

                    //Extract the coordinates and send them
                    // to the Maps Activity
										/* FIXME:
                    String[] coordinates = message.split(",",2);
                    LatLng position = new LatLng(Double.parseDouble(coordinates[0]), Double.parseDouble(coordinates[1]));
                    Bundle arg = new Bundle();
										*/
										/*	FIXME:
                    arg.putParcelable("position", position);
                    Intent acIntent = new Intent(context,MapsActivity.class);
                    acIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    acIntent.putExtra("bundle", arg);

                    context.startActivity(acIntent);
*/
                    //Let the music play if it was never started
//FIXME                    if (!soundStarted) {
										Log.e("ASDFTOMATO", "3");

										if( true ){
												Log.e("ASDFTOMATO", "4");

                        Intent serv = new Intent(context, MusicService.class);
												Log.e("ASDFTOMATO", "5");
                        context.startService(serv);
												Log.e("ASDFTOMATO", "6");

												soundStarted = true;
												Log.e("ASDFTOMATO", "7");
                    }
                }
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        }
    }

    private boolean notAJoke(){
        //search for the originating phone number
        // in the tracked list
        for (String str : superMApp.trackedList)
            if (str.endsWith(phoneNumber))
                return true;
        return false;

    }

    private boolean someoneInDanger(){
        //if message starts with the token, then
        //delete the token and return true;
        if (message.startsWith(alertToken)){
            message = message.substring(alertToken.length() );
            return true;
        }
        return false;
    }
}
