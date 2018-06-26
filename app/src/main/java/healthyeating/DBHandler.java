package healthyeating;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by darren on 4/6/17.
 */

public class DBHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "DailyLog";

    // Table Name
    private static final String TABLE_LOG_DETAILS = "DailyLog";

    // Table Column Names
    private static final String KEY_DATE = "date";
    private static final String KEY_HEALTHY = "healthy";
    private static final String KEY_REASON = "reason";
    private static final String KEY_DAYS = "consecutive_healthy_days";

    public DBHandler(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOG_DETAILS_TABLE = "CREATE TABLE " + TABLE_LOG_DETAILS + "("
                + KEY_DATE + " STRING PRIMARY KEY, "
                + KEY_HEALTHY + " INTEGER ,"
                + KEY_REASON + " TEXT,"
                + KEY_DAYS + " INTEGER " + ")";

        db.execSQL(CREATE_LOG_DETAILS_TABLE);
    }


    public void reset(){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db,1,1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOG_DETAILS);
        //Create updated table
        onCreate(db);
    }

    /*Update database based on button clicked*/
    long updateDB(DailyLog tlog){
        long newRowId;

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("date", String.valueOf(tlog.logDate));
        values.put("healthy", tlog.isHealthDay);
        values.put("reason",tlog.reasonNonHealth);
        values.put("consecutive_healthy_days",tlog.consecHealthDays);

        //Check if log exists for t date
        Cursor exists = getLog(String.valueOf(tlog.logDate));
        if (exists.getCount() == 0){
            newRowId = db.insert("DailyLog",null,values);
        }else{  //Log exists, replace data
            newRowId = db.replace("DailyLog",null,values);
        }
        exists.close();
        return newRowId;
    }

    /*get the date*/
    public Cursor getLog(String id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM DailyLog WHERE date = ?", new String[] {id});
        if (cursor != null) {
            cursor.moveToFirst();
        };
        return cursor;
    }

    /*get the most recent entry*/
    public Cursor getRecent(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM DailyLog ORDER BY date DESC LIMIT 2", new String[] {});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /*get the most recent entry*/
    public Cursor getEarliest(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM DailyLog ORDER BY date ASC LIMIT 1", new String[] {});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /*get max consec days*/
    public Cursor getMax(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(consecutive_healthy_days) FROM DailyLog", new String[] {});
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    /*get max consec days*/
    public long getHealthDays(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM DailyLog WHERE healthy LIKE 1", new String[] {});
        long returnValue = cursor.getCount();
        cursor.close();
        return returnValue;
    }

    /*get log count*/
    public long getCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM DailyLog", new String[] {});
        long returnValue = cursor.getCount();
        cursor.close();
        return returnValue;
    }

    /*get all non healthy day logs*/
    public Cursor getNonHealthLogs(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM DailyLog WHERE TRIM(reason) > ''", new String[] {});
        return cursor;
    }



/*    *//*Fetches all the notes from the database.*//*
    public List getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM DailyLog", null);

        while(cursor.moveToNext()) {
            String whoknows = cursor.getString(0);
            System.out.println(whoknows);
        }
        cursor.close();


        List test = new ArrayList();

        cursor.close();
        return test;
    }*/

}
