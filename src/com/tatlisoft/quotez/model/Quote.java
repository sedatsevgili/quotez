package com.tatlisoft.quotez.model;

import com.tatlisoft.quotez.db.DatabaseHelper;

import android.R;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

public class Quote extends Model{

	public static int LIKED = 1;
	
	public static int NOT_LIKED = 0;
	
	public Quote(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	private int _id;
	private int _personId;
	private String _quote;
	private Person _person;
	private int _liked;
	
	public int getId() {
		return _id;
	}
	public void setId(int _id) {
		this._id = _id;
	}
	public int getPersonId() {
		return _personId;
	}
	public void setPersonId(int _personId) {
		this._personId = _personId;
	}
	public String getQuote() {
		return _quote;
	}
	public void setQuote(String _quote) {
		this._quote = _quote;
	}
	public void setPerson(Person _person) {
		this._person = _person;
	}
	public void setLiked(int _liked) {
		this._liked = _liked;
	}
	public int getLiked() {
		return this._liked;
	}
	public Person getPerson() {
		if(_person == null) {
			// load person
			_person = new Person(this._context);
			_person.load(_personId);
		}
		return _person;
	}
	
	public void load(int _id) {
		this._id = _id;
		SQLiteDatabase database = _databaseHelper.getDatabase();
		String[] columns = {DatabaseHelper.COLUMN_QUOTE_ID, DatabaseHelper.COLUMN_QUOTE_PERSON_ID, DatabaseHelper.COLUMN_QUOTE_QUOTE, DatabaseHelper.COLUMN_QUOTE_LIKED};
		Cursor cursor = database.query(DatabaseHelper.TABLE_QUOTE, columns, DatabaseHelper.COLUMN_QUOTE_ID + " = " + String.valueOf(this._id), null, null, null, null);
		if(cursor.getCount() == 0) {
			_databaseHelper.closeDatabase();
			throw new Error("Quote not found: " + String.valueOf(_id));
		}
		cursor.moveToFirst();
		_personId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUOTE_PERSON_ID));
		_quote = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUOTE_QUOTE));
		_liked = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUOTE_LIKED));
		_databaseHelper.closeDatabase();
		_person = new Person(_context);
		_person.load(_personId);
	}
	
	public static Quote getRandom(Context context) throws Exception {
		return Quote.getRandom(context, null);
	}
	
	public static Quote getRandom(Context context, String condition) throws Exception {
		Quote quote = new Quote(context);
		SQLiteDatabase database = quote.getDatabaseHelper().getDatabase();
		String[] columns = new String[1];
		columns[0] = DatabaseHelper.COLUMN_QUOTE_ID;
		Cursor cursor = database.query(DatabaseHelper.TABLE_QUOTE, columns, condition, null, null, null, "RANDOM()", "1");
		if(cursor.getCount() == 0) {
			quote.getDatabaseHelper().closeDatabase();
			throw new Exception(context.getResources().getString(com.tatlisoft.quotez.R.string.not_found));
		}
		cursor.moveToFirst();
		int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUOTE_ID));
		quote.getDatabaseHelper().closeDatabase();
		quote.load(id);
		return quote;
	}
	
	public void update() {
		SQLiteDatabase database = _databaseHelper.getDatabase();
		String strFilter = "id=" + _id;
		ContentValues args = new ContentValues();
		args.put("quote", _quote);
		args.put("liked", _liked);
		database.update(DatabaseHelper.TABLE_QUOTE, args, strFilter, null);
		_databaseHelper.closeDatabase();
	}
	
	public String toShareString() {
		String q = _quote.trim();
		if(q.startsWith("\"")) {
			q = q.substring(1);
		}
		if(q.endsWith("\"")) {
			q = q.substring(0, q.length() - 1);
		}
		return "\"" + q + "\" " + getPerson().getName().trim();
	}
}
