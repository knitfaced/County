package com.bobbinsmag;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class SelectProject extends ListActivity {

	private RowCountDbAdapter mDbHelper;
	private Cursor mProjectsCursor;
	private static final int ACTIVITY_COUNT=0;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_project);
        mDbHelper = new RowCountDbAdapter(this);
        mDbHelper.open();
        //mDbHelper.upgrade();
        fillData();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Cursor c = mProjectsCursor;
        c.moveToPosition(position);
        Intent i = new Intent(this, RowCounter.class);
        i.putExtra(RowCountDbAdapter.ID, id);
        startActivityForResult(i, ACTIVITY_COUNT);
    }
    
	private void fillData() {
        // Get all of the rows from the database and create the item list
        mProjectsCursor = mDbHelper.fetchAllProjects();
        startManagingCursor(mProjectsCursor);

        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[]{RowCountDbAdapter.NAME};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter projects = 
            new SimpleCursorAdapter(this, R.layout.project_row, mProjectsCursor, from, to);
        setListAdapter(projects);
	}
}
