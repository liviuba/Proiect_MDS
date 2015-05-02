package proiectMDS.superMApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;


public class superMApp extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
			final int PICK_CONTACT = 1;
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);

			Button button = (Button) findViewById(R.id.asdf);
			button.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					Log.e("superMApp:OnClick","Intru in onCLick!");
					Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult( pickContactIntent, PICK_CONTACT);
				}
			});
    }

		public void test(View view){
			final int PICK_CONTACT = 1;
			Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
			startActivityForResult( pickContactIntent, PICK_CONTACT);
		}

		@Override
		public void onActivityResult( int requestCode, int resultCode, Intent data){
			super.onActivityResult(requestCode, resultCode, data);

			if(requestCode == 1 && resultCode == Activity.RESULT_OK){
				Uri contactData = data.getData();
				Log.e("superMApp:onActivityResult", contactData.toString());
			}

		}
}
