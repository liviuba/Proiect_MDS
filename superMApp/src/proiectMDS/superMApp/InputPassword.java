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
	String supervisorID;
	String supervisorPhoneNum;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.inputpassword);

		if(savedInstanceState == null){	//freshly called, never put in background/killed
			Bundle extras = getIntent().getExtras();
			if(extras == null){
				Log.e("superMApp:InputPassword:onCreate","Oops! Somehow nothing got put in the intent");
				password = supervisorID = supervisorPhoneNum = "garbage";
			}
			else{
				password = extras.getString("PASSWORD");
				supervisorID = extras.getString("SUPERVISOR_ID");
				supervisorPhoneNum = extras.getString("SUPERVISOR_PHONE_NUM");
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
				EditText supervisorToken_et = (EditText) findViewById(R.id.supervisor_token_et);
				if( (userPasswd_et.getText().toString()!=null && !userPasswd_et.getText().toString().isEmpty()) && 
						( supervisorToken_et.getText().toString()!=null && !supervisorToken_et.getText().toString().isEmpty())){
					if( password.equals( userPasswd_et.getText().toString()) ){
						Intent returnIntent = new Intent();
						returnIntent.putExtra("SUPERVISOR_ID",supervisorID);
						returnIntent.putExtra("SUPERVISOR_PHONE_NUM",supervisorPhoneNum);
						returnIntent.putExtra("SUPERVISOR_TOKEN", supervisorToken_et.getText().toString());
						setResult(RESULT_OK, returnIntent);
						finish();
					}
					else
						Toast.makeText(getApplicationContext(), "Wrong password!", Toast.LENGTH_SHORT).show();
				}
				else{
						if(supervisorToken_et.getText().toString().isEmpty() || supervisorToken_et.getText() == null)
							Toast.makeText(getApplicationContext(), "Enter supervisor token!", Toast.LENGTH_SHORT).show();
						else
							Toast.makeText(getApplicationContext(), "Enter a password!", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
}
