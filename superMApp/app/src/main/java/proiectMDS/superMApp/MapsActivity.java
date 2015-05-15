package proiectMDS.superMApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends Activity {

    private GoogleMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("MapsActivity:onCreate()", "sunt aici");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        Bundle bundle = getIntent().getParcelableExtra("bundle");
        LatLng fromPosition = bundle.getParcelable("position");
        map.addMarker(new MarkerOptions().position(fromPosition)
                .title("Person In Danger").snippet("Person In Danger"));


        // Move the camera instantly to the position with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(fromPosition, 15));

        //This button allow the user to stop the alarm while the GoogleMaps will remain.
        Button stopTheMusic = (Button) findViewById(R.id.stop_the_sound);
        stopTheMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("onClick():", " The music should stop now.");
                stopService(new Intent(MapsActivity.this, MusicService.class));
            }
        });

    }



    @Override
    protected void onNewIntent(Intent intent) {
        Log.i("onNewIntent():", "Everything is fine. ");
        Bundle bundle = intent.getParcelableExtra("bundle");
        LatLng fromPosition = bundle.getParcelable("position");
        map.addMarker(new MarkerOptions().position(fromPosition)
                .title("Person In Danger").snippet("Person In Danger"));


        // Move the camera instantly to the position with a zoom of 15.
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(fromPosition, 15));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        return true;
    }


    @Override
    protected void onDestroy() {
        SmsListener.soundStarted = false;
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        SmsListener.soundStarted = false;
        super.onStop();
    }



}
