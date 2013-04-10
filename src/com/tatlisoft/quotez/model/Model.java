package com.tatlisoft.quotez.model;

import android.content.Context;

import com.tatlisoft.quotez.db.DatabaseHelper;

public class Model {

	protected DatabaseHelper _databaseHelper;
	
	protected Context _context;
	
	public Model(Context context) {
		_context = context;
		_databaseHelper = DatabaseHelper.getInstance(context);
	}

	public DatabaseHelper getDatabaseHelper() {
		return _databaseHelper;
	}

	public void setDatabaseHelper(DatabaseHelper _databaseHelper) {
		this._databaseHelper = _databaseHelper;
	}
	
}
