package my.burger.now.app.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 8029 on 08/07/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MyDBurger.db";
    public static final String SESSION_TABLE_NAME = "sessions";
    public static final String SESSION_COLUMN_ID = "id";
    public static final String SESSION_COLUMN_TIMER = "timer";
    public static final String SESSION_COLUMN_DISTANCE = "distance";
    public static final String SESSION_COLUMN_STATUS = "status";
    public static final String SESSION_COLUMN_ID_OPERATION = "status";
    public static final String SESSION_COLUMN_ID_CLIENT = "idclient";
    public static final String SESSION_COLUMN_LAT_CLIENT = "ltc";
    public static final String SESSION_COLUMN_LNG_CLIENT = "lnc";
    public static final String SESSION_COLUMN_LAT_RESTO = "ltr";
    public static final String SESSION_COLUMN_CODE = "code";
    public static final String SESSION_COLUMN_LNG_RESTO = "lnr";
    public static final String SESSION_COLUMN_BUNDLE = "bundle";
    public static final String SESSION_COLUMN_CATALOGUE = "catalogue";
    public static final String SESSION_COLUMN_PRIX = "prix";
    public static final String SESSION_COLUMN_P_ID = "pid";
    public static final String SESSION_COLUMN_NC = "nc";
    public static final String SESSION_COLUMN_ID_RESTO = "idrestorant";
    public static final String SESSION_COLUMN_NOM_ENSEIGNE = "nomenseigne";
    public static final String SESSION_COLUMN_CURRENT_PAGE = "currentpage";
    public static final String SESSION_COLUMN_CURRENT_MESSAGE = "currentmessage";

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table sessions " +
                        "(id integer primary key," +
                        "timer text," +
                        "distance text," +
                        "currentmessage text," +
                        "status text," +
                        "idclient text," +
                        "ltc text," +
                        "lnc text," +
                        "ltr text," +
                        "lnr text," +
                        "bundle text," +
                        "catalogue text," +
                        "prix text," +
                        "pid text," +
                        "nc text," +
                        "idrestorant text," +
                        "nomenseigne text," +
                        "currentpage text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS sessions");
        onCreate(db);
    }

    public boolean insertSession(){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", 1);
        contentValues.put("timer", "2");
        contentValues.put("distance", "");
        contentValues.put("currentmessage", "");
        contentValues.put("status", "");
        contentValues.put("idclient", "");
        contentValues.put("ltc", "");
        contentValues.put("lnc", "");
        contentValues.put("ltr", "");
        contentValues.put("lnr", "");
        contentValues.put("bundle", "");
        contentValues.put("catalogue", "");
        contentValues.put("prix", "");
        contentValues.put("pid", "");
        contentValues.put("nc", "");
        contentValues.put("idrestorant", "");
        contentValues.put("nomenseigne", "");
        contentValues.put("currentpage", "");
        db.insert("sessions", null, contentValues);
        return true;
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from sessions where id=1", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, SESSION_TABLE_NAME);
        return numRows;
    }

    public boolean updateSession (String nameCol, String valCol)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(""+nameCol, valCol);
        db.update("sessions", contentValues, "id = ? ", new String[] { Integer.toString(1) } );
        return true;
    }
}
