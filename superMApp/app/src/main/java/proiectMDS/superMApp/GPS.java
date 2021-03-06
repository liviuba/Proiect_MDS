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
    public static double latitude, longitude;

    GPSTracker gps;
    TextView gpsCoordinates;
    ArrayList<String> contactsArray;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        Intent contactListIntent = getIntent();
        Log.i("GPS", "in GPS");
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
                if(!gps.canGetLocation()) {
                    gps.showSettingsAlert();}
                else {
                    if(!contactsArray.isEmpty()){
                        Log.i("GPS","porneste alarma");
                        SetAlarm(GPS.this);
                    }
                        for (String x : contactsArray) {
                            gpsCoordinates.setText("\n" + x);

                            String number = x.split(" ~ ", 2)[1];
                            try {
                                Toast.makeText(GPS.this, "Started service", Toast.LENGTH_SHORT).show();
                                gpsCoordinates.setText("Service started");
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                            Log.e("GPS", "sent a message to " + number);
                        }
                    }
                }

        });

        Button stopStreamButton = (Button) findViewById(R.id.stopStreamButton);
        stopStreamButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlarmManager am = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(GPS.this, AlarmReceiver.class);
                i.setAction("proiectMDS.superMApp.ACTION");
                PendingIntent pi = PendingIntent.getBroadcast(GPS.this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
                try{am.cancel(pi);Log.i("GPSCancel","cancelling");}
                catch(Exception e){e.printStackTrace();}
                Toast.makeText(GPS.this,"Stopped service",Toast.LENGTH_SHORT).show();
                gpsCoordinates.setText("Service stopped");
            }
        });

    }
    //sets up an alarm listener which waits for the timer to run out to send a new sms
    public void SetAlarm(Context context)
    {

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        i.setAction("proiectMDS.superMApp.ACTION");
        PendingIntent pi = PendingIntent.getBroadcast(context,0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());

        am.cancel(pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 10000, pi);
    }
	}



