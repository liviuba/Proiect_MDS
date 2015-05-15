package proiectMDS.superMApp;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by andrei on 03.05.2015.
 */
public class MusicService extends Service {

    MediaPlayer mp = null;
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mp.setLooping(true);
        mp.start();
        return Service.START_NOT_STICKY;
    }
    @Override
    public void onCreate(){
        mp = MediaPlayer.create(this,R.raw.alarm);
        Log.i("MusicService.onCreate()", ":Created the Media Player");
    }

    @Override
    public void onDestroy() {
        mp.stop();
        stopSelf();
        super.onDestroy();
    }
}
