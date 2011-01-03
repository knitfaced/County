package com.bobbinsmag;

import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class RowCounter extends RowCountDbActivity {

	private TextView mRowCountText;
	private TextView mCompleteRepeatsText;
	private TextView mRowsPerRepeatText;
	private TextView mTotalRowsText;
	
	private static final int RESET_ID = Menu.FIRST;
	private static final int SETTINGS_ID = Menu.FIRST + 1;
	private static final int INCREMENT_ID = Menu.FIRST + 2;
	private static final int DECREMENT_ID = Menu.FIRST + 3;
	
	private static final int ACTIVITY_SETTINGS = 0;
	
	private static final List<Integer> TEXT_COLOURS = Arrays.asList(new Integer[]{R.color.red, R.color.yellow, R.color.green, R.color.blue, R.color.violet});	
	
	private PowerManager.WakeLock mWakeLock;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);        

    	retrieveProjectId(savedInstanceState);
        
        mCompleteRepeatsText = (TextView) findViewById(R.id.completerepeats);
        mRowCountText = (TextView) findViewById(R.id.rowcount);        
        mRowsPerRepeatText = (TextView) findViewById(R.id.rowsperrepeat);
        mTotalRowsText = (TextView) findViewById(R.id.totalrows);
        
        retrieveState();
        populateView();
        
        mRowCountText.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mProject.isIncrement()) {
					mProject.incrementRowCount();	
				} else {
					mProject.decrementRowCount();
				}								
				saveState();
				populateView();
				return false;
			}
		});        
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Counting Rows");
    }      
    
    @Override
    protected void populateView() {
    	//should always have a project before calling
    	if (mProject != null) {
    		final int rowCount = mProject.getRowCount();
    		final int repeat = mProject.getRepeat();
    		final int completeRepeats = mProject.getCompleteRepeats();
    		final int totalRows = (completeRepeats * repeat) + rowCount;
    		final int currentColourIndex = completeRepeats % 5;
    		int newColour = getResources().getColor(TEXT_COLOURS.get(currentColourIndex));
    		mRowCountText.setTextColor(newColour);    		
	        mRowCountText.setText(rowCount+"");
	        
	        if (completeRepeats > 0) {        		        	
	        	mCompleteRepeatsText.setText(getResources().getText(R.string.completedRepeats).toString() + " " + completeRepeats);
	        	mTotalRowsText.setText(getResources().getText(R.string.totalRows).toString() + " " + totalRows);
	        } else {
	        	mCompleteRepeatsText.setText("");
	        	mTotalRowsText.setText("");
	        }
    		
    		if (repeat > 0) {
    			mRowsPerRepeatText.setText(getResources().getText(R.string.rowsPerRepeat).toString() + " " + repeat);
    		} else {
    			mRowsPerRepeatText.setText("");
    		}
    	}
    }    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, RESET_ID, 0, R.string.reset);
        menu.add(0, SETTINGS_ID, 0, R.string.settings);
        menu.add(0, DECREMENT_ID, 0, R.string.decrement);
        menu.add(0, INCREMENT_ID, 0, R.string.increment);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case RESET_ID:
            	mProject.resetCount();
            	saveState();
            	populateView();
                return true;
            case SETTINGS_ID:
                Intent i = new Intent(this, SettingsPage.class);
                i.putExtra(RowCountDbAdapter.ID, mProjectId);
                startActivityForResult(i, ACTIVITY_SETTINGS);
            	return true;
            case DECREMENT_ID:
            	mProject.decrementRowCount();
            	saveState();
            	populateView();
                return true;
            case INCREMENT_ID:
            	mProject.incrementRowCount();
            	saveState();
            	populateView();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }    
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if (mWakeLock != null) {
    		mWakeLock.acquire();
    	}
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	if (mWakeLock != null) {
    		mWakeLock.release();
    	}
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode) {
            case ACTIVITY_SETTINGS:
            	retrieveState();
            	populateView();
            	break;
        }
    }
}