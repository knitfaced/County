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
//	private ToggleButton mNumericButton;

	//static final int DIALOG_TOO_MANY_LETTERS_ID = 0;
	static final int DIALOG_INVALID_NUMBER_ID = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setTitle(R.string.settings);

    	retrieveProjectId(savedInstanceState);
	
        mRepeatText = (EditText) findViewById(R.id.repeat);
		mIncrementButton = (ToggleButton) findViewById(R.id.increment_button);
		//mNumericButton = (ToggleButton) findViewById(R.id.numeric_button);
		
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
//		mNumericButton.setChecked(mProject.isNumeric());
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
//			if (repeat > 26 && !mProject.isNumeric()) {
//				showDialog(DIALOG_TOO_MANY_LETTERS_ID);
//				repeat = 26;
//				return false;
//			}
			mProject.setRepeat(repeat);
		} catch (NumberFormatException e) {
			showDialog(DIALOG_INVALID_NUMBER_ID);
			return false;
		}		
		mProject.setIncrement(mIncrementButton.isChecked());
		//mProject.setNumeric(mNumericButton.isChecked());
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
//		    case DIALOG_TOO_MANY_LETTERS_ID:
//		    	dialog = showAlert("If you're using letters, you can't have a repeat of greater than 26!");
//		        break;
		    default:
		        dialog = null;
	    }
	    return dialog;
	}
}
