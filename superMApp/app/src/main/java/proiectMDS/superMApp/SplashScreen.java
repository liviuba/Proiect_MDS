package proiectMDS.superMApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        Log.i("MainMenu", "am intrat in menu ");
        Thread splash = new Thread(){
        public void run() {
//            try {
//                sleep(2000);
                Intent intent = new Intent(SplashScreen.this,superMApp.class);
                finish();
                startActivity(intent);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }


        };
        splash.start();
    }


}
