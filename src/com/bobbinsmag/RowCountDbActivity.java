package com.bobbinsmag;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;

public abstract class RowCountDbActivity extends Activity {

	protected RowCountDbAdapter mDbHelper;
	protected Long mProjectId;
	protected Project mProject;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mDbHelper = new RowCountDbAdapter(this);
        mDbHelper.open();
	}
	
	protected void retrieveState() {
		// should always have a project id before calling
		if (mProjectId != null) {
			Cursor cursor = mDbHelper.fetchProject(mProjectId);
			int rowCount = cursor.getInt(cursor
					.getColumnIndexOrThrow(RowCountDbAdapter.ROWCOUNT));
			int completeRepeats = cursor.getInt(cursor
					.getColumnIndexOrThrow(RowCountDbAdapter.COMPLETEREPEATS));
			int repeat = cursor.getInt(cursor
					.getColumnIndexOrThrow(RowCountDbAdapter.REPEAT));
			String name = cursor.getString(cursor
					.getColumnIndexOrThrow(RowCountDbAdapter.NAME));
			String notes = cursor.getString(cursor
					.getColumnIndexOrThrow(RowCountDbAdapter.NOTES));
			boolean numeric = cursor.getShort(cursor
					.getColumnIndexOrThrow(RowCountDbAdapter.NUMERIC)) > 0;
			boolean increment = cursor.getShort(cursor
					.getColumnIndexOrThrow(RowCountDbAdapter.INCREMENT)) > 0;
			if (mProject == null) {
				mProject = new Project();
			}
			mProject.setId(mProjectId);
			mProject.setIncrement(increment);
			mProject.setNumeric(numeric);
			mProject.setName(name);
			mProject.setNotes(notes);
			mProject.setRepeat(repeat);
			mProject.setRowCount(rowCount);
			mProject.setCompleteRepeats(completeRepeats);
			mProject.setUserPreferences(new UserPreferences(this));
			cursor.close();
		}
	}

	protected void saveState() {
		// should always have a project before calling
		if (mProject != null) {
			mDbHelper.updateProject(mProject);
		}
	}
	
	protected void retrieveProjectId(Bundle savedInstanceState) {
		Long projectId = null;
		//look in saved instance state first
		if (savedInstanceState != null) {
    		projectId = (Long) savedInstanceState.getSerializable(RowCountDbAdapter.ID);
		}
		//then check the extras bundle
        if (projectId == null) {
            Bundle extras = getIntent().getExtras();
            projectId = extras != null ? extras.getLong(RowCountDbAdapter.ID) : null;
        }
        //if we've not found a project id yet, create a new one
        if (projectId == null) {	        	
        	mProject = mDbHelper.createProject();
        	mProjectId = mProject.getId();
        }
        mProjectId = projectId;	        
    }	   
	
	protected abstract void populateView();
	
    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        populateView();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(RowCountDbAdapter.ID, mProjectId);
    }
}
