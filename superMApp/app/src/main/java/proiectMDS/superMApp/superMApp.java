/**
 * TODO:	re-use everything from previous button
 * just put an IntExtra for which list to store the result in
 * */

package proiectMDS.superMApp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
		// Entry format :  <Contact name> | <Contact phone #>
		ArrayList<String> supervisorList = new ArrayList<String>();
		public static ArrayList<String> trackedList = new ArrayList<String>();

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
			final ArrayAdapter<String> supervisorAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, supervisorList);
			supervisorListView.setAdapter(supervisorAdapter);
			//delete contact from list
			supervisorListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> a, View v, int position, long id){
					AlertDialog.Builder adb = new AlertDialog.Builder(superMApp.this);
					adb.setTitle("Delete?");
					adb.setMessage("Are you sure you want to delete this contact?");
					adb.setNegativeButton("Cancel", null);
					final int position_final = position;
					adb.setPositiveButton("OK", new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which){
							supervisorList.remove(position_final);
							supervisorAdapter.notifyDataSetChanged();
						}
					});
					adb.show();
				}
			});

			ListView trackedListView = (ListView) findViewById( R.id.tracked_list);
			final ArrayAdapter<String> trackedAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, trackedList);
			trackedListView.setAdapter(trackedAdapter);
			//delete contact from list
			trackedListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> a, View v, int position, long id){
					AlertDialog.Builder adb = new AlertDialog.Builder(superMApp.this);
					adb.setTitle("Delete?");
					adb.setMessage("Are you sure you want to delete this contact?");
					adb.setNegativeButton("Cancel", null);
					final int position_final = position;
					adb.setPositiveButton("OK", new AlertDialog.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which){
							trackedList.remove(position_final);
							trackedAdapter.notifyDataSetChanged();
						}
					});
					adb.show();
				}
			});
		}

		@Override
		public void onActivityResult( int requestCode, int resultCode, Intent data){
			super.onActivityResult(requestCode, resultCode, data);

			switch(requestCode){
				case AUTHENTICATE_REQUEST:
					if(resultCode == Activity.RESULT_CANCELED){
						Log.e("onActRes:switch()", "Bad stuff happened\n");
						//FIXME: don't let it through
					}
					else{
						String newContact = new String();
						newContact = data.getExtras().getString("CONTACT_NAME") + " | "+ data.getExtras().getString("CONTACT_PHONE_NUM");

						switch(tempListSwitch){
							case ADD_TO_SUPERVISOR:
								supervisorList.add(newContact);
								break;
							case ADD_TO_TRACKED:
								trackedList.add(newContact);
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
						goToPasswd.putExtra("CONTACT_NAME",contactName);
						goToPasswd.putExtra("CONTACT_PHONE_NUM",contactPhoneNum);

						startActivityForResult( goToPasswd, AUTHENTICATE_REQUEST );

						}
						else{
							Log.e("onActivityResult","No first entry for contact DB cursor");
						}
						
				}
				break;
		}
	}
}
