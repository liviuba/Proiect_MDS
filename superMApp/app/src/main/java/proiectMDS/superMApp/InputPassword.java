package proiectMDS.superMApp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;

public class InputPassword extends Activity{
	String password;
	String contactName;
	String contactPhoneNum;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputpassword);

		if(savedInstanceState == null){	//freshly called, never put in background/killed
			Bundle extras = getIntent().getExtras();
			if(extras == null){
				Log.e("InputPassword:onCreate","Oops! Somehow nothing got put in the intent");
				password = contactName = contactPhoneNum = "garbage";
			}
			else{
				password = extras.getString("PASSWORD");
				contactName = extras.getString("CONTACT_NAME");
				contactPhoneNum = extras.getString("CONTACT_PHONE_NUM");
			}
		}
		else{
			//TODO: deal with this
		}

		Button submitPasswd = (Button) findViewById(R.id.input_password_b);
		
		submitPasswd.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				Log.e("superMApp:InputPassword","Submit button pressed");

				EditText userPasswd_et = (EditText) findViewById(R.id.input_password_et);
				if( (userPasswd_et.getText().toString()!=null && !userPasswd_et.getText().toString().isEmpty()) ){
					if( password.equals( userPasswd_et.getText().toString()) ){
						Intent returnIntent = new Intent();
						returnIntent.putExtra("CONTACT_NAME",contactName);
						returnIntent.putExtra("CONTACT_PHONE_NUM",contactPhoneNum);
						setResult(RESULT_OK, returnIntent);
						finish();
					}
					else
						Toast.makeText(getApplicationContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
				}
				else{
							Toast.makeText(getApplicationContext(), "Enter a password!", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
