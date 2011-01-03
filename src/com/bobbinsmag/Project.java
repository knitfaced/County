package com.bobbinsmag;

import android.content.ContentValues;

public class Project {
	
	private Long id;
	private String name = "<Touch here to start counting>";
	private int rowCount = 0;
	private int repeat = 0;
	private int completeRepeats = 0;
	private boolean increment = true;
	private boolean numeric = true;
	private String notes = "";

	private UserPreferences userPreferences;
	
	public Project() {		
	}
	
	public Project(ContentValues contentValues) {
		id = contentValues.getAsLong(RowCountDbAdapter.ID);
		name = contentValues.getAsString(RowCountDbAdapter.NAME);
		rowCount = contentValues.getAsInteger(RowCountDbAdapter.ROWCOUNT);
		repeat = contentValues.getAsInteger(RowCountDbAdapter.REPEAT);
		completeRepeats = contentValues.getAsInteger(RowCountDbAdapter.COMPLETEREPEATS);
		increment = contentValues.getAsShort(RowCountDbAdapter.INCREMENT) > 0;
		numeric = contentValues.getAsShort(RowCountDbAdapter.NUMERIC) > 0;
		notes = contentValues.getAsString(RowCountDbAdapter.NOTES);
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRowCount() {
		return rowCount;
	}
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	public int getRepeat() {
		return repeat;
	}
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}
	public int getCompleteRepeats() {
		return completeRepeats;
	}
	public void setCompleteRepeats(int completeRepeats) {
		this.completeRepeats = completeRepeats;
	}
	public boolean isIncrement() {
		return increment;
	}
	public void setIncrement(boolean increment) {
		this.increment = increment;
	}
	public boolean isNumeric() {
		return numeric;
	}
	public void setNumeric(boolean numeric) {
		this.numeric = numeric;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public UserPreferences getUserPreferences() {
		return userPreferences;
	}

	public void setUserPreferences(UserPreferences userPreferences) {
		this.userPreferences = userPreferences;
	}

	public ContentValues toContentValues() {
        ContentValues contentValues = new ContentValues();
		if (id != null) {
			contentValues.put(RowCountDbAdapter.ID, id);
		}
        contentValues.put(RowCountDbAdapter.NAME, name);
        contentValues.put(RowCountDbAdapter.ROWCOUNT, rowCount);
        contentValues.put(RowCountDbAdapter.REPEAT, repeat);
        contentValues.put(RowCountDbAdapter.COMPLETEREPEATS, completeRepeats);
        contentValues.put(RowCountDbAdapter.INCREMENT, increment);
        contentValues.put(RowCountDbAdapter.NUMERIC, numeric);
        if (notes != null) {
        	contentValues.put(RowCountDbAdapter.NOTES, notes);
        }
		return contentValues;
	}
	
    public void incrementRowCount() {
    	int currentRowCount = getRowCount();
    	int repeat = getRepeat();
		
		//more than 2 digits won't fit on screen
		if (++currentRowCount == 100) {
			currentRowCount = 0;
		} 
		if (repeat != 0) {
			boolean incrementNow = currentRowCount > repeat;
			if (!userPreferences.isShowCurrentRow()) {
				incrementNow = currentRowCount >= repeat;
			}
    		if (incrementNow) {
    			setRowCount(currentRowCount - repeat);
    			int completeRepeats = getCompleteRepeats();
    			setCompleteRepeats(completeRepeats + 1);
    			return;
    		}    		
    	}
		setRowCount(currentRowCount);
    }

    public void decrementRowCount() {
    	int currentRowCount = getRowCount();
    	int repeat = getRepeat();
    	
    	if (--currentRowCount < 0) {
    		currentRowCount = 0;
    	}    	
		if (repeat != 0) {
    		if (currentRowCount == 0) {
    			setRowCount(repeat);
    			int completeRepeats = getCompleteRepeats();
    			setCompleteRepeats(completeRepeats + 1);
    			return;
    		}    		
    	}
		setRowCount(currentRowCount);    	
    }
    
    public void resetCount() {
    	setRowCount(isIncrement() ? 0 : getRepeat());
    	setCompleteRepeats(0);
    }
}