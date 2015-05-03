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
import android.database.Cursor;


public class superMApp extends Activity
{
		final int PICK_CONTACT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);

			Button button = (Button) findViewById(R.id.pick_contact_b);
			button.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					Log.e("superMApp:OnClick","Picking a contact");
					Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult( pickContactIntent, PICK_CONTACT);
				}
			});
    }

		@Override
		public void onActivityResult( int requestCode, int resultCode, Intent data){
			super.onActivityResult(requestCode, resultCode, data);

			//TODO switch-if
			if(requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK){
				Uri contactData = data.getData();
				Cursor c = managedQuery( contactData, null, null, null, null );

				//Log.e("ASDFTEST", (String) c.moveToFirst());
				if( c.moveToFirst() ){
					String contactName = c.getString( c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME) );
					String contactPhoneNum;

					/** Get phone # (if any) **/
					String id = c.getString( c.getColumnIndexOrThrow( ContactsContract.Contacts._ID ));
					String hasPhone = c.getString( c.getColumnIndex( ContactsContract.Contacts.HAS_PHONE_NUMBER ) );
					
					if(hasPhone.equalsIgnoreCase("1")){
						Cursor phones = getContentResolver().query(
								ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
								ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "+id,
								null, null);
						phones.moveToFirst();

						contactPhoneNum = phones.getString(phones.getColumnIndex("data1"));
					}
					else{
						contactPhoneNum = "No phone number. Run, Forest, run!!";
					}
					Log.e("superMApp:ASDFTEST", contactName + "--" + contactPhoneNum);

				}
				else{
					Log.e("superMApp:onActivityResult","No first entry for contact DB cursor");
				}

			}
		}
}
