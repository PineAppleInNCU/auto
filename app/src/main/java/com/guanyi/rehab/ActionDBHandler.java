package com.guanyi.rehab;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;


public class ActionDBHandler extends SQLiteOpenHelper {

	private static final int DATEBASE_VERSION = 1;
	private static final String DATABASE_NAME = "actions.db";
	public static final String TABLE_ACTIONS = "actions";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "actionname";
	public static final String COLUMN_ACTIONDATA = "actiondata";

	public ActionDBHandler(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATEBASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String query = "CREATE TABLE " + TABLE_ACTIONS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + COLUMN_NAME
				+ " TEXT ," + COLUMN_ACTIONDATA + " TEXT " + ");";
		db.execSQL(query);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_ACTIONS);
		onCreate(db);
	}

	//Add a new action to the database
	public void addaction(ActionProducts actionproducts){
		ContentValues values = new ContentValues();
		values.put(COLUMN_NAME, actionproducts.get_aciotnname());
		values.put(COLUMN_ACTIONDATA, actionproducts.getActionData());
		SQLiteDatabase db = getWritableDatabase();		
		db.insert(TABLE_ACTIONS, null, values);
		db.close();		
	}
	
	//Delete an action from database
	public void deleteaction(String actionName){
		SQLiteDatabase db = getWritableDatabase();
		db.execSQL("DELETE FROM "+TABLE_ACTIONS+" WHERE "+COLUMN_NAME+"=\""+actionName+"\";");
	}
	
	//Print out the database as a string
	public String databaseToString(){
		String dbString = "";
		SQLiteDatabase db = getWritableDatabase();
		String query = "SELECT * FROM "+TABLE_ACTIONS+" WHERE 1";
		//Cursor point to a location in results
		Cursor c = db.rawQuery(query, null);
		//Move to the first row in results
		c.moveToFirst();
		while(!c.isAfterLast()){
			if(c.getString(c.getColumnIndex(COLUMN_NAME))!=null){
				dbString += c.getString(c.getColumnIndex(COLUMN_NAME));
				dbString += "\n";
				Log.i("ActionData", c.getString(c.getColumnIndex(COLUMN_ACTIONDATA)));
				c.moveToNext();
			}
		}
		c.close();
		db.close();
		return dbString;
	}
	public String getactiondata(String name){
		String dbString = "";
		SQLiteDatabase db = getWritableDatabase();
		String query = "SELECT * FROM "+TABLE_ACTIONS+" WHERE 1";
		//Cursor point to a location in results
		Cursor c = db.rawQuery(query, null);
		//Move to the first row in results
		c.moveToFirst();
		while(!c.isAfterLast()){
			if(c.getString(c.getColumnIndex(COLUMN_NAME)).equals(name)){
				dbString = c.getString(c.getColumnIndex(COLUMN_ACTIONDATA));
				c.close();
				db.close();
				return dbString;				
			}
			c.moveToNext();
		}
		return dbString;
	}
	
}
