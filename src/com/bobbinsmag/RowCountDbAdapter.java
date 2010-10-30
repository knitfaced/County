package com.bobbinsmag;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RowCountDbAdapter {

	public static final String ID = "_id";
	public static final String NAME = "name";
	public static final String ROWCOUNT = "rowCount";
	public static final String REPEAT = "repeat";
	public static final String COMPLETEREPEATS = "completeRepeats";
	public static final String INCREMENT = "increment";
	public static final String NUMERIC = "numeric";
	public static final String NOTES = "notes";
	
	//old name = rowcountsettings
    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "project";
    private static final int DATABASE_VERSION = 1;

    private static final String TAG = RowCountDbAdapter.class.getSimpleName();
    
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    
    private final Context mCtx;
    
    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table "+DATABASE_TABLE+
        " (_id integer primary key autoincrement," +
        " name text not null," +
        " rowCount integer not null," +
        " repeat integer," +
        " completeRepeats integer," +
        " increment short not null," +
        " numeric short not null," +
        " notes text);";
    
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param ctx the Context within which to work
     */
    public RowCountDbAdapter(Context ctx) {
        this.mCtx = ctx;
    }
    
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
            Project project = new Project();
            ContentValues contentValues = project.toContentValues();
            db.insert(DATABASE_TABLE, null, contentValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
            onCreate(db);
        }
    }

    /**
     * Open the rowcount database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public RowCountDbAdapter open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * for testing
     */
    public void upgrade() {
        mDbHelper.onUpgrade(mDb, 0, 1);
    }    

    /**
     * Return a cursor that points to the given projectId
     * 
     * @param rowId id of project to retrieve
     * @return matching project, if found
     * @throws SQLException if project could not be found/retrieved
     */
    public Cursor fetchProject(long projectId) throws SQLException {
        Cursor cursor = mDb.query(true, DATABASE_TABLE, 
	    		getColumnList(),
	    		ID + "=" + projectId, 
	    		null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    
    /**
     * Return a Cursor over the list of all projects in the database
     * 
     * @return Cursor over all projects
     */
    public Cursor fetchAllProjects() {
    	Cursor mCursor = mDb.query(DATABASE_TABLE, getColumnList(), null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;    	
    }

    public boolean updateProject(Project project) {
        ContentValues args = project.toContentValues();
        return mDb.update(DATABASE_TABLE, args, ID + "=" + project.getId(), null) > 0;
    }
    
    private String[] getColumnList() {
    	return new String[] {ID, NAME, ROWCOUNT, REPEAT, COMPLETEREPEATS, INCREMENT, NUMERIC, NOTES};
    }

    /**
     * insert a project and return its id
     * @return
     */
	public Project createProject() {
		Project project = new Project();		
		long projectId = mDb.insert(DATABASE_TABLE, null, project.toContentValues());
		project.setId(projectId);
		return project;
	}
}