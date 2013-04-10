package com.tatlisoft.quotez.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static String DB_PATH = "/data/data/com.tatlisoft.quotez/databases/";
	
	private static String DB_NAME = "db";
	
	private static SQLiteDatabase _myDataBase;
	
	private final Context myContext;
	
	private static DatabaseHelper _instance;
	
	public static int DATABASE_VERSION = 2;
	
	public static String TABLE_PERSON = "person";
	public static String TABLE_QUOTE = "quote";
	public static String COLUMN_PERSON_ID = "id";
	public static String COLUMN_PERSON_NAME = "name";
	public static String COLUMN_PERSON_INFO = "info";
	public static String COLUMN_QUOTE_ID = "id";
	public static String COLUMN_QUOTE_PERSON_ID = "person_id";
	public static String COLUMN_QUOTE_QUOTE = "quote";
	public static String COLUMN_QUOTE_LIKED = "liked";
	
	public static DatabaseHelper getInstance(Context context) {
		if(_instance != null) {
			return _instance;
		}
		_instance = new DatabaseHelper(context, DatabaseHelper.DB_NAME, null, DatabaseHelper.DATABASE_VERSION);
		try {
			_instance.createDataBase();
			//_instance.openDatabase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		} catch (SQLException se) {
			throw new Error("Unable to open database");
		} catch (Exception e2) {
			throw new Error("Unknown error: " + e2.getMessage());
		}
		return _instance;
	}
	
	public DatabaseHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.myContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		
	}
	
	private int fetchDbVersion() {
		openDatabase();
		String[] columns = {"count(id) as total"};
		Cursor cursor = _myDataBase.query(DatabaseHelper.TABLE_PERSON, columns, null, null,null,null,null);
		if(cursor.getCount() == 0) {
			closeDatabase();
			throw new Error("kisi sayisi bulunamadi");
		}
		cursor.moveToFirst();
		int count = cursor.getInt(cursor.getColumnIndex("total"));
		closeDatabase();
		if(count < 989) {
			return 1;
		}
		return 2;
	}
	
	private void _updateDb() {
		int oldVersion = fetchDbVersion();
		int newVersion = DatabaseHelper.DATABASE_VERSION;
		if(newVersion <= oldVersion) {	// nolur nolmaz
			return;
		}
		SQLiteDatabase db = getDatabase();
		if(!db.isOpen() || db.isReadOnly()) {
			try {
				db = this.getWritableDatabase();
			} catch (SQLiteException ex) {
				throw new Error("Unable to open writable database to update");
			}
		}
		db.beginTransaction();
		BufferedReader bufferedReader = null;
		Log.d("debug","oldversion: " + oldVersion + ", newversion: " + newVersion);
		for(int i = oldVersion+1; i <= newVersion; i++) {
			try {
				 Log.d("debug","opening dbchangelog/" + i + ".sqlite");
				 bufferedReader = new BufferedReader(new InputStreamReader(myContext.getAssets().open("dbchangelog/" + i + ".sqlite")));
				 String line = "";
				 while((line = bufferedReader.readLine()) != null) {
					 line = line.trim();
					 if(line.length() == 0) {
						 continue;
					 }
					 try {
						 //Log.d("debug", "query runs: " + line);
						 db.execSQL(line);
					 } catch (SQLiteException sex) {
						 throw new Error("Unable to compile query: " + line);
					 } catch (SQLException sqex) {
						 throw new Error("Unable to run query: " + line);
					 } catch (Exception exx) {
						 //Log.d("error","ERROR: " + exx.getMessage());
						 throw new Error("Unknown error: " + exx.getMessage());
					 }
				 }
				 bufferedReader.close();
			} catch (IOException ex) {
				throw new Error("Unable to open update database " + i + ".sqlite");
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//_updateDb(oldVersion, newVersion);
	}
	
	public void createDataBase() throws IOException {
		boolean dbExists = checkDatabaseExists();
		if(dbExists) {
			openDatabase();
			closeDatabase();
			_updateDb();
			return;
		}
		try {
			copyDatabase();
			openDatabase();
			closeDatabase();
		} catch (IOException e) {
			throw new Error(e.toString());
		}
	}

	private Boolean checkDatabaseExists() {
		SQLiteDatabase checkDB = null;
		try {
			File dbDir = new File(DB_PATH);
			if(!dbDir.exists()) {
				dbDir.mkdir();
			}
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE );
		} catch (SQLiteException e) {
			
		}
		if(checkDB != null) {
			checkDB.close();
			return true;
		}
		return false;
	}
	
	private void copyDatabase() throws IOException {
		GZIPInputStream myInput = new GZIPInputStream(myContext.getAssets().open(DB_NAME));
    	String outFileName = DB_PATH + DB_NAME;
    	OutputStream myOutput = new FileOutputStream(outFileName);
    	byte[] buffer = new byte[1024];
    	int length;
    	while((length = myInput.read(buffer)) > 0) {
    		myOutput.write(buffer, 0, length);
    	}
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
	}

	public void openDatabase() throws SQLException {
		if(_myDataBase != null && _myDataBase.isOpen()) {
			return;
		}
		String myPath = DB_PATH + DB_NAME;
		_myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
	}
	
	public void closeDatabase() {
		_myDataBase.close();
	}
	
	@Override
	public synchronized void close() {
		if(_myDataBase != null) {
			_myDataBase.close();
		}
		super.close();
	}
	
	public SQLiteDatabase getDatabase() {
		if(_myDataBase == null || !_myDataBase.isOpen()) {
			openDatabase();
		}
		if(_myDataBase.isReadOnly()) {
			closeDatabase();
			openDatabase();
		}
		return _myDataBase;
	}
}
