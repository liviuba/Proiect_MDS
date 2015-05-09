/**
 * TODO:	re-use everything from previous button
 * just put an IntExtra for which list to store the result in
 * */

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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

public class superMApp extends Activity
{
		final int PICK_CONTACT_REQUEST = 1;
		final int AUTHENTICATE_REQUEST = 2;
		final int ADD_TO_SUPERVISOR = 10;
		final int ADD_TO_TRACKED = 11;
		
			//specifies in whcih list to put the contact, after login; Can't propagate through from onClick because of PICK_CONTACT_REQUEST
		int tempListSwitch = 0;	
		String password="tomato";//FIXME: For the sake of baby pandas, store this hashed
		ArrayList<String> supervisorList = new ArrayList<String>();
		ArrayList<String> trackedList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);

			Button addSupervisorButton = (Button) findViewById(R.id.add_supervisor);
			addSupervisorButton.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					Log.e("superMApp:OnClick","Picking a contact");
					tempListSwitch = ADD_TO_SUPERVISOR;
					Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult( pickContactIntent, PICK_CONTACT_REQUEST);
				}
			});

			Button addTrackedButton = (Button) findViewById(R.id.add_tracked);
			addTrackedButton.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v){
					tempListSwitch = ADD_TO_TRACKED;
					Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult( pickContactIntent, PICK_CONTACT_REQUEST);
				}
			});
    }

		@Override
		public void onResume(){
			super.onResume();

			ListView supervisorListView = (ListView) findViewById( R.id.supervisor_list );
			ArrayAdapter<String> supervisorAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, supervisorList);
			supervisorListView.setAdapter(supervisorAdapter);

			ListView trackedListView = (ListView) findViewById( R.id.tracked_list);
			ArrayAdapter<String> trackedAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, trackedList);
			trackedListView.setAdapter(trackedAdapter);
		}

		@Override
		public void onActivityResult( int requestCode, int resultCode, Intent data){
			super.onActivityResult(requestCode, resultCode, data);

			switch(requestCode){
				case AUTHENTICATE_REQUEST:
					if(resultCode == Activity.RESULT_CANCELED){
						Log.e("superMApp:onActivityResult:switch(requestCode)", "Bad stuff happened\n");
						//FIXME: don't let it through
					}
					else{
						String newSupervisor = new String();
						newSupervisor = data.getExtras().getString("CONTACT_ID") + " | "+ data.getExtras().getString("CONTACT_PHONE_NUM");

						switch(tempListSwitch){
							case ADD_TO_SUPERVISOR:
								supervisorList.add(newSupervisor);
								break;
							case ADD_TO_TRACKED:
								trackedList.add(newSupervisor);
								break;
						}
						tempListSwitch = 0;
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
						// These two get propagated through login screen back to the main activity
						// TODO: does it make more sense to keep them in some temp variables in the activity?
						goToPasswd.putExtra("CONTACT_ID",contactName);
						goToPasswd.putExtra("CONTACT_PHONE_NUM",contactPhoneNum);

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
