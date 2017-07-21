package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by DELL on 2017/7/14.
 */

public class MyDb extends SQLiteOpenHelper {
    private static String TABLE_NAME = "HEAD_IMG_TABLE";
    private static String DB_NAME = "HEAD_IMG_DB";
    private static String IMAGE = "_bitmap";
    private static String USERNAME = "_username";
    private static int DB_VERSION = 1;
    public MyDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE if not exists "
                + TABLE_NAME
                + " ("+ USERNAME + " TEXT PRIMARY KEY , " + IMAGE + " BLOB ) ";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public byte[] turnBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
    //将图片一字节形式存储数据库读取操作
    public long insert(String username, Bitmap _img) {
        byte[] img = turnBitmapToByte(_img);
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(USERNAME, username);
        cv.put(IMAGE, img);
        long result = db.insert(TABLE_NAME, null, cv);
        db.close();
        return result;
    }
    //获取图片，通过username
    public Bitmap getBitmap(String username){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME,
                new String[]{USERNAME, IMAGE},
                null, null, null, null, null);
        if (cursor.moveToFirst() == false) {
            //cursor empty
            db.close();
            return null;
        }
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int nameColumn = cursor.getColumnIndex(USERNAME);
            int imgColumn = cursor.getColumnIndex(IMAGE);
            String name = cursor.getString(nameColumn);
            if (name.equals(username)) {
                byte[] img = cursor.getBlob(imgColumn);
                Bitmap bmpout = BitmapFactory.decodeByteArray(img, 0, img.length);
                db.close();
                return bmpout;
            }
        }
        //找不到就返回空
        db.close();
        return null;
    }
    //sqlite update参数：TABLENAME, values, whereClause, whereArgs
    public void replaceHeadImg(String username, Bitmap _img) {
        SQLiteDatabase db = getWritableDatabase();
        byte[] img = turnBitmapToByte(_img);
        ContentValues cv = new ContentValues();
        cv.put(USERNAME, username);
        cv.put(IMAGE, img);
        String whereClause = USERNAME+"=?";
        String whereArgs[] = {username};
        db.update(TABLE_NAME, cv, whereClause, whereArgs);
        db.close();
    }
}
