package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2017/4/6.
 */

public class BlackDB extends SQLiteOpenHelper {

    public BlackDB(Context context) {
        super(context, "black.db", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacktb" +
                "(_id integer primary key autoincrement," +
                "phone text," +
                "mode integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists blacktb");
        onCreate(db);
    }
}
