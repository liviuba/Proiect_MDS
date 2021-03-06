package proiectMDS.superMApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by andrei on 03.05.2015.
 */

    //Right now, in this stage, the GoogleMaps won't start again if
    // stopped because 'trackedList' will be lost.

public class SmsListener extends BroadcastReceiver {

    public static boolean soundStarted = false;
    public static final String alertToken = "trei";
    private static String message = null;
    private static String phoneNumber = null;
    private static double lat;
    private static double lon;
    final String TRACKED_FILENAME = "amyTracked";
    public static ArrayList<String> trackedList = new ArrayList<String>();

    @Override
    public void onReceive(Context context, Intent intent) {
			Log.e("ASDFTOMATO", "Listener started!");
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

								Log.e("ASDFTOMATO", "Just before checks");
                if (someoneInDanger() && notAJoke(context)) {
									if (true) {
											//Extract the coordinates and send them
											// to the Maps Activity
											LatLng position = new LatLng(lat, lon);
											Bundle arg = new Bundle();
											arg.putParcelable("position", position);
											Intent acIntent = new Intent(context,MapsActivity.class);
											acIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
											acIntent.putExtra("bundle", arg);

											context.startActivity(acIntent);

											//Let the music play if it was never started
											if (!soundStarted) {

												if( true ){
														Intent serv = new Intent(context, MusicService.class);
														context.startService(serv);

														soundStarted = true;
												}
											}
									}
								}
						}
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver");
        }
    }

    private boolean notAJoke(Context context){
			Log.e("ASDFTOMATO", "Started running notAJoke");
        superMApp.initListFromFile(context,TRACKED_FILENAME, trackedList);
				Log.e("ASDFTOMATO","PhoneNumber : " + phoneNumber);

        for (String str : trackedList){
            str = str.replace("(","");
            str = str.replace(")","");
            str = str.replace(" ","");
            str = str.replace("-","");
						str = str.replace("-","");
						phoneNumber = phoneNumber.replace("+4","");

						Log.e("ASDFTOMATO", "Checked String :" + str);
            if (str.endsWith(phoneNumber)){
							Log.e("ASDFTOMATO","VICTORY!!");
                return true;
						}
        }

        return false;	//false
    }
    

    private boolean someoneInDanger(){
        //if message starts with the token, then
        //delete the token

        String[] parts = message.split(",");
        for (String str : parts)
            Log.i("someoneInDanger():", str);
        if (parts.length < 4)
            return false;

        if (!parts[0].equals(alertToken))
            return false;


        try {
            lat = Double.parseDouble(parts[1]);
            lon = Double.parseDouble(parts[2]);
        }
        catch (Exception e){
            return false;
        }

        if (!parts[3].startsWith("https://www.google.com/maps/@"))
            return false;

        return true;

    }

    public ArrayList initListFromFile(Context context,String filename, ArrayList<String> list){
        SharedPreferences myFile = context.getSharedPreferences(filename, 0);


        //initialize tracked/supervisor list, if any
        int num = myFile.getInt("NUM", -100);
        if(num != -100)// -100 is the default value, in case no such key is found
            for(int i=0; i<num; i++)
                list.add( myFile.getString( Integer.toString(i), "Mara Predescu ~ 0754319586" ) );

        return list;
    }
}
