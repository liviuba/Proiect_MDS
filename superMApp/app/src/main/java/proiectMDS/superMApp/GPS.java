package proiectMDS.superMApp;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class GPS extends Activity {
    GPSTracker gps;
    TextView gpsCoordinates;
    ArrayList<String> contactsArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        Intent contactListIntent = getIntent();
        Log.i("GPS","in GPS");
        Bundle contactsBundle = contactListIntent.getExtras();
        if (!contactsBundle.isEmpty()) {
            boolean hasStringArrayList = contactsBundle.containsKey("supervisorList");
            if (hasStringArrayList) {
                contactsArray = contactsBundle.getStringArrayList("supervisorList");

            }

        }
        gpsCoordinates = (TextView) findViewById(R.id.showGPS);
        Button gpsButton = (Button) findViewById(R.id.gpsButton);
        gpsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                gps = new GPSTracker(GPS.this);
                if (gps.canGetLocation()) {
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    if(!contactsArray.isEmpty()) {
                        SetAlarm(GPS.this);
                    }

//                    gpsCoordinates.append("\nYour location is \nLat: " + latitude + "\nLong: " + longitude);
                    for (String x : contactsArray) {
                        gpsCoordinates.append("\n" + x);

                        String number = x.split(" ~ ", 2)[1];
//                        SmsManager smsManager = SmsManager.getDefault();
                        try {

//                            smsManager.sendTextMessage("0754319586", null, latitude + "," + longitude, null, null);
                            Toast.makeText(GPS.this, "Sent a message to " + number, Toast.LENGTH_SHORT).show();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                        Log.e("GPS", "sent a message to " + number);
                    }
                } else {
                    gps.showSettingsAlert();
                }
            }
        });

        Button stopStreamButton = (Button) findViewById(R.id.stopStreamButton);
        stopStreamButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlarmManager am = (AlarmManager) GPS.this.getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(GPS.this, AlarmReceiver.class);
                i.setAction("proiectMDS.superMApp.ACTION");
                PendingIntent pi = PendingIntent.getBroadcast(GPS.this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
                am.cancel(pi);
//                stopService(new Intent(GPS.this,SmsStreamService.class));
                Toast.makeText(GPS.this,"Stopped service",Toast.LENGTH_SHORT).show();
            }
        });

    }
    //sets up an alarm listener which waits for the timer to run out to send a new sms
    public void SetAlarm(Context context)
    {

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        i.setAction("proiectMDS.superMApp.ACTION");
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());

//        calendar.add(Calendar.MINUTE, 1);
        am.cancel(pi);
//        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), calendar.getTimeInMillis(), pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10000, pi);
    }
    }



