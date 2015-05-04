package proiectMDS.superMApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.database.Cursor;

public class superMApp extends Activity
{
		final int PICK_CONTACT_REQUEST = 1;
		final int AUTHENTICATE_REQUEST = 2;
		String password="tomato";//FIXME: For the sake of baby pandas, store this hashed

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
					startActivityForResult( pickContactIntent, PICK_CONTACT_REQUEST);
				}
			});
    }

		@Override
		public void onActivityResult( int requestCode, int resultCode, Intent data){
			super.onActivityResult(requestCode, resultCode, data);

			switch(requestCode){
				case AUTHENTICATE_REQUEST:
					if(resultCode == Activity.RESULT_CANCELED){
						//don't let it through
					}
					else{
						//TODO: get stuff out of return intent and add to whatever list it belongs to
						Log.e("ASDFTEST",data.getExtras().getString("SUPERVISOR_ID"));
						Log.e("ASDFTEST",data.getExtras().getString("SUPERVISOR_PHONE_NUM"));
						Log.e("ASDFTEST",data.getExtras().getString("SUPERVISOR_TOKEN"));
					}
					break;
				case PICK_CONTACT_REQUEST:
					if(resultCode == Activity.RESULT_OK){
						Uri contactData = data.getData();
						Cursor c = managedQuery( contactData, null, null, null, null );

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

						/** Start another activity to get master password.
						 *  Sending a confirmation e-mail won't work since you're logged into gmail on your phone anyway
						 *  */
						Intent goToPasswd = new Intent(superMApp.this, InputPassword.class);
						goToPasswd.putExtra("PASSWORD",password);
						goToPasswd.putExtra("SUPERVISOR_ID",contactName);
						goToPasswd.putExtra("SUPERVISOR_PHONE_NUM",contactPhoneNum);

						startActivityForResult( goToPasswd, AUTHENTICATE_REQUEST );

						}
						else{
							Log.e("superMApp:onActivityResult","No first entry for contact DB cursor");
						}
						
				}
				break;
		}
	}
}
