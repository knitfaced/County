package com.bobbinsmag;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class SettingsPage extends RowCountDbActivity {
	
	private EditText mRepeatText;
	private ToggleButton mIncrementButton;
	private ToggleButton mCurrentRowButton;
	private UserPreferences mUserPreferences;
	
	static final int DIALOG_INVALID_NUMBER_ID = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        
        setContentView(R.layout.settings);
        setTitle(R.string.settings);

    	retrieveProjectId(savedInstanceState);
	
        mRepeatText = (EditText) findViewById(R.id.repeat);
		mIncrementButton = (ToggleButton) findViewById(R.id.increment_button);
		mCurrentRowButton = (ToggleButton) findViewById(R.id.currentRowButton);
		
		mUserPreferences = new UserPreferences(this);
		retrieveState();
        populateView();
                
        mIncrementButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (validate()) {
                	saveState();
				}
			}
		});

        mCurrentRowButton.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				mUserPreferences.setShowCurrentRow(mCurrentRowButton.isChecked());
			}
		});
        
        
        Button okButton = (Button) findViewById(R.id.ok);        
        okButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                if (validate()) {
                	saveState();
                	setResult(RESULT_OK);
                    finish();
                }
            }

        });
        ImageView imageView = (ImageView) findViewById(R.id.bobbins_link);        
        imageView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				Uri bobbinsUrl = Uri.parse(getResources().getString(R.string.bobbinsUrl));
				Intent myIntent = new Intent(Intent.ACTION_VIEW, bobbinsUrl);
				startActivity(myIntent);
				return false;
			}
		});
	}

	@Override
	protected void populateView() {
		mRepeatText.setText(mProject.getRepeat()+"");
		mIncrementButton.setChecked(mProject.isIncrement());		
		
		mCurrentRowButton.setChecked(mUserPreferences.isShowCurrentRow());
	}

	private boolean validate() {
		String value = mRepeatText.getText().toString();
		try {			
			int repeat = 0;
			if (value != null && !"".equals(value)) {
				repeat = Integer.parseInt(value);
			}
			//reset count if the repeat has changed
			if (repeat != mProject.getRepeat()) {
				mProject.resetCount();
			}

			mProject.setRepeat(repeat);
		} catch (NumberFormatException e) {
			showDialog(DIALOG_INVALID_NUMBER_ID);
			return false;
		}		
		mProject.setIncrement(mIncrementButton.isChecked());
		return true;
	}
	
	private Dialog showAlert(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
		       .setCancelable(false)
		       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   dialog.dismiss();
		           }
		       });
		return builder.create();				
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
	    Dialog dialog;
	    switch(id) {
		    case DIALOG_INVALID_NUMBER_ID:
		    	dialog = showAlert("Please enter a number for the repeat value");
		        break;
		    default:
		        dialog = null;
	    }
	    return dialog;
	}

}
