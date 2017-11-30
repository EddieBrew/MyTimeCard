package com.apps.robertbrewer.stanfordtimecard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

/**
 * Created by Robert Brewer on 11/21/2017.
 */

public class HoursDatabase extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyPayrolldb.db";
    private static final String EMPLOYEE_PAYROLL_TABLE = "employeesdaily";

    // Contacts table name
    public static final String CONTACTS_TABLE_NAME = "Daily Payroll Information";
    // Contacts Table Columns names
    public static final String _COLUMN_ID = "id";
    public static final String _COLUMN_DAY = "Day";
    public static final String _COLUMN_DATE = "Date";
    public static final String _COLUMN_EVENTNUM  = "EventNumber";
    public static final String _COLUMN_EVENTNAME = "EventName";
    public static final String _COLUMN_TIME = "WorkingHours";
    public static final String _COLUMN_RHOURS = "RHours";
    public static final String _COLUMN_OTHOURS = "OTHours";

    public HoursDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_EMPLOYEE_PAYROLL_TABLE  = "CREATE TABLE " + EMPLOYEE_PAYROLL_TABLE  + "("
                + _COLUMN_ID  + " INTEGER PRIMARY KEY,"
                +  _COLUMN_DAY + " TEXT,"
                +  _COLUMN_DATE + " TEXT,"
                +  _COLUMN_EVENTNUM + " TEXT,"
                + _COLUMN_EVENTNAME  + " TEXT,"
                +  _COLUMN_TIME + " TEXT,"
                + _COLUMN_RHOURS + " TEXT,"
                + _COLUMN_OTHOURS + " TEXT " + ")";

        db.execSQL(CREATE_EMPLOYEE_PAYROLL_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " +EMPLOYEE_PAYROLL_TABLE );
        // Create tables again
        onCreate(db);

    }
   public  void addNewEntry(Context context, DailyInfoModel payrollEntry ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(_COLUMN_DAY, payrollEntry.getDay() );
        values.put(_COLUMN_DATE, payrollEntry.getDate() );
        values.put(_COLUMN_EVENTNUM,  payrollEntry.getEventNumber() );
        values.put(_COLUMN_EVENTNAME, payrollEntry.getEventName() );
        values.put(_COLUMN_TIME, payrollEntry.getTime());
        values.put(_COLUMN_RHOURS, payrollEntry.getRhours());
        values.put(_COLUMN_OTHOURS, payrollEntry.getOhours() );


        // Inserting Row
        db.insert(EMPLOYEE_PAYROLL_TABLE,_COLUMN_ID  ,  values);
        db.close(); // Closing database connection
    }

    // Getting All WorkedHours
    public List<DailyInfoModel> getAllInfo(Context context) {


        List<DailyInfoModel> workersHoursList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + EMPLOYEE_PAYROLL_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int index = 1 ;

        // looping through all rows and adding to list
        final String COMMA = ", ";
        if (cursor.moveToFirst()) {
            do {
                String info = cursor.getString(index) + ", " ;
                for(index = 2; index< 8; index++  ){
                    info = info  + cursor.getString(index) + ", ";
                }

                DailyInfoModel dailyHours = new DailyInfoModel(info);
                workersHoursList.add(dailyHours);//adds daily employees record to list
                index = 1;
            } while (cursor.moveToNext());
        }
        db.close();
        return workersHoursList;
    }

    public void DeleteAll(){

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + EMPLOYEE_PAYROLL_TABLE); //delete all rows in a table
        //db.delete("EMPLOYEE_PAYROLL_TABLE", null, null);
        db.close();
    }

}
