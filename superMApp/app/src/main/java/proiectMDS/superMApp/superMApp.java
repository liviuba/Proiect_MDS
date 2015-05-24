/**
 * TODO:	re-use everything from previous button
 * just put an IntExtra for which list to store the result in
 * */

/*FIXME
 * Persistent storage format:
 * ("NUM", # of elements in list)
 * ("#", list element with that index)
 */

package proiectMDS.superMApp;

import android.content.Context;
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
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences;

import java.util.ArrayList;

public class superMApp extends Activity
{
		final int PICK_CONTACT_REQUEST = 1;
		final int AUTHENTICATE_REQUEST = 2;
		final int ADD_TO_SUPERVISOR = 10;
		final int ADD_TO_TRACKED = 11;

		static final String SUPERVISOR_FILENAME = "mySupervisors";
		final String TRACKED_FILENAME = "myTracked";
		
		//specifies in whcih list to put the contact, after login; Can't propagate through from onClick because of PICK_CONTACT_REQUEST
		int tempListSwitch = 0;	
		String password="tomato";//FIXME: For the sake of baby pandas, store this hashed
		// Entry format :  <Contact name> | <Contact phone #>
		public static ArrayList<String> supervisorList = new ArrayList<String>();
		public static ArrayList<String> trackedList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_super_mapp);

			trackedList.clear();
			supervisorList.clear();

			initListFromFile(this,TRACKED_FILENAME, trackedList);
			initListFromFile(this,SUPERVISOR_FILENAME, supervisorList);


			Button addSupervisorButton = (Button) findViewById(R.id.add_supervisor);
			addSupervisorButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.e("superMApp:OnClick", "Picking a contact");
					tempListSwitch = ADD_TO_SUPERVISOR;
					Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
					startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
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
			Button startServiceButton = (Button)findViewById(R.id.startServiceButton);
			startServiceButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle contactsArrayBundle = new Bundle();
					contactsArrayBundle.putStringArrayList("supervisorList",supervisorList);
					Intent gpsIntent = new Intent(superMApp.this, GPS.class);
					gpsIntent.putExtras(contactsArrayBundle);
					startActivity(gpsIntent);
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
							mirrorListModificationInFile(SUPERVISOR_FILENAME, supervisorList);
							//update screen
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
							mirrorListModificationInFile(TRACKED_FILENAME, trackedList);
							//show on screen
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
						newContact = data.getExtras().getString("CONTACT_NAME") + " ~ "+ data.getExtras().getString("CONTACT_PHONE_NUM");

						switch(tempListSwitch){
							case ADD_TO_SUPERVISOR:
								supervisorList.add(newContact);
								mirrorListModificationInFile(SUPERVISOR_FILENAME, supervisorList);	
								break;
							case ADD_TO_TRACKED:
								trackedList.add(newContact);
								mirrorListModificationInFile(TRACKED_FILENAME, trackedList);
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

	public static ArrayList initListFromFile(Context context,String filename, ArrayList<String> list){
		SharedPreferences myFile = context.getSharedPreferences(filename, 0);


		//initialize tracked/supervisor list, if any
		int num = myFile.getInt("NUM", -100);
		if(num != -100)// -100 is the default value, in case no such key is found
			for(int i=0; i<num; i++)
				list.add( myFile.getString( Integer.toString(i), "Couldn't retrieve data from file : "+filename ) );

		return list;
	}

	public void mirrorListModificationInFile(String filename, ArrayList<String> list){
		Editor fileEditor = this.getSharedPreferences(filename, 0).edit();

		fileEditor.clear();
		fileEditor.putInt("NUM", list.size() );
		for(int i=0; i<supervisorList.size(); i++)
			fileEditor.putString( Integer.toString(i), list.get(i) );
		fileEditor.commit();
	}
}
