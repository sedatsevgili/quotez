package com.tatlisoft.quotez.model;

import java.util.ArrayList;

import com.tatlisoft.quotez.db.DatabaseHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Person extends Model {
	
	public Person(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	private int _id;
	private String _name;
	private String _info;
	
	public int getId() {
		return _id;
	}
	public void setId(int _id) {
		this._id = _id;
	}
	public String getName() {
		return _name;
	}
	public void setName(String _name) {
		this._name = _name;
	}
	public String getInfo() {
		return _info;
	}
	public void setInfo(String _info) {
		this._info = _info;
	}
	
	public void load(int _id) {
		this._id = _id;
		SQLiteDatabase database = _databaseHelper.getDatabase();
		String[] columns = {DatabaseHelper.COLUMN_PERSON_ID, DatabaseHelper.COLUMN_PERSON_INFO, DatabaseHelper.COLUMN_PERSON_NAME};
		Cursor cursor = database.query(DatabaseHelper.TABLE_PERSON, columns, DatabaseHelper.COLUMN_PERSON_ID + " = " + String.valueOf(this._id), null, null, null, null);
		if(cursor.getCount() == 0) {
			_databaseHelper.closeDatabase();
			throw new Error("Person not found: " + String.valueOf(this._id));
		}
		cursor.moveToFirst();
		_info = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PERSON_INFO));
		_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PERSON_NAME));
		_databaseHelper.closeDatabase();
	}
	
	public void loadByName(String _name) {
		this._name = _name.replace("'", "");
		SQLiteDatabase database = _databaseHelper.getDatabase();
		String[] columns = {DatabaseHelper.COLUMN_PERSON_ID, DatabaseHelper.COLUMN_PERSON_INFO, DatabaseHelper.COLUMN_PERSON_NAME};
		Cursor cursor = database.query(DatabaseHelper.TABLE_PERSON, columns, DatabaseHelper.COLUMN_PERSON_NAME + " = '" + this._name + "'", null, null, null, null);
		if(cursor.getCount() == 0) {
			_databaseHelper.closeDatabase();
			throw new Error("Person not found: " + this._name);
		}
		cursor.moveToFirst();
		_info = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PERSON_INFO));
		_name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PERSON_NAME));
		_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PERSON_ID));
		_databaseHelper.closeDatabase();
	}
	
	public static ArrayList<String> loadAll(Context context) {
		return Person.loadAll(context, "");
	}
	
	public static ArrayList<String> loadAll(Context context, String condition) {
		ArrayList resultList = new ArrayList<String>();
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance(context);
		SQLiteDatabase database = databaseHelper.getDatabase();
		String[] columns = {DatabaseHelper.COLUMN_PERSON_ID, DatabaseHelper.COLUMN_PERSON_INFO, DatabaseHelper.COLUMN_PERSON_NAME};
		Cursor cursor = database.query(DatabaseHelper.TABLE_PERSON, columns, condition, null, null, null, "name ASC");
		if(cursor.getCount() == 0) {
			databaseHelper.closeDatabase();
			return resultList;
		}
		cursor.moveToFirst();
		while(cursor.isAfterLast() == false) {
			resultList.add(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PERSON_NAME)));
			cursor.moveToNext();
		}
		databaseHelper.closeDatabase();
		return resultList;
	}
}
