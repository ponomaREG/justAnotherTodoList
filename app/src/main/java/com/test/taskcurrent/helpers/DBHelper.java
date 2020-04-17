package com.test.taskcurrent.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.test.taskcurrent.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "everydaytasks.db";
    private static String DB_PATH = "";
    private SQLiteDatabase mDataBase;
    private boolean mNeedUpdate = false;
    private Context context;



    public DBHelper(Context context){
        super(context, DB_NAME, null, 1);
        DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        this.context = context;
    }


    public void updateDataBase(){
        if (mNeedUpdate) {
            File dbFile = new File(DB_PATH + DB_NAME);
            if (dbFile.exists())
                dbFile.delete();
            copyDataBase();

            mNeedUpdate = false;
        }
    }
    public boolean checkDataBase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }
    public void copyDataBase() {
        if (!checkDataBase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDBFile();
            } catch (IOException mIOException) {
                throw new Error("ErrorCopyingDataBase");
            }
        }
    }
    private void copyDBFile() throws IOException {
        InputStream mInput = context.getAssets().open(DB_NAME);
        OutputStream mOutput = new FileOutputStream(DB_PATH + DB_NAME);
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0)
            mOutput.write(mBuffer, 0, mLength);
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }
    @Deprecated
    public boolean openDataBase() throws SQLException {
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;
    }

    @Override
    public synchronized void close() {
        if (mDataBase != null)
            mDataBase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            mNeedUpdate = true;
    }
//
//    public void setOpenTaskNumberIs(int task, float time){
//        SQLiteDatabase db = this.getWritableDatabase();
//        boolean isLast = false;
//        if (task == 31) {
//            isLast = true;
//            task = 30;
//        }
//        Cursor c = db.rawQuery(String.format("select * from tasks where id=%s;", task), null);
//        c.moveToFirst();
//        if (c.getInt(c.getColumnIndex("is_open")) == 0) {
//            c.close();
//            c = db.rawQuery(String.format("update tasks set is_open=1 where id=%s;", task), null);
//            c.moveToFirst();
//            c.close();
//            c = db.rawQuery(String.format("select * from tasks where id=%s;", task - 1), null);
//            c.moveToFirst();
//            float time_in_db = c.getFloat(c.getColumnIndex("time"));
//            if (time_in_db > time)
//                db.rawQuery(String.format("update tasks update time=%.1f where id=$s;", time, task - 1), null);
//
//        }else{
//            if(isLast){
//                c.close();
//                c = db.rawQuery(String.format("select * from tasks where id=%s;", task), null);
//                c.moveToFirst();
//                float time_in_db = c.getFloat(c.getColumnIndex("time"));
//                if (time_in_db > time)
//                    db.rawQuery(String.format("update tasks update time=%.1f where id=$s;", time, task), null);
//            }
//        }
//        c.close();
//    }


//    public int getIDFromTable(String query,String table, String condition, String equal, String column){
//        Cursor c = this.getReadableDatabase().rawQuery(
//           String.format(
//                   query,
//                   table,
//                   condition,
//                   equal
//           ) ,null
//        );
//        c.moveToFirst();
//        int id = c.getColumnIndex(column);
//        c.close();
//        return id;
//    }

//    public Cursor getInfoFromTableByCondition(String query,String table, String condition, String equal, String column){
//        Cursor c = this.getReadableDatabase().rawQuery(
//                String.format(
//                        query,
//                        table,
//                        condition,
//                        equal
//                ) ,null
//        );
//        c.moveToFirst();
//        return c;
//    }


//    public int getIDFromTableTwoCondition(String query,String table, String condition_1, String equal_1, String condition_2, String equal_2, String column){
//        Cursor c = this.getReadableDatabase().rawQuery(
//                String.format(
//                        query,
//                        table,
//                        condition_1,
//                        equal_1,
//                        condition_2,
//                        equal_2
//                ),null
//        );
//        c.moveToFirst();
//        int id = c.getColumnIndex(column);
//        c.close();
//        return id;
//    }

    public Cursor getAllLinesFromDB(String query, String table){
        Cursor c = this.getReadableDatabase().rawQuery(
                String.format(
                        query,
                        table
                ),
                null
        );
        c.moveToFirst();
        return c;
    }

    public long insertData(String table, String nullColumnHack, ContentValues cv){
        SQLiteDatabase s = this.getWritableDatabase();
        long ret_id = s.insertOrThrow(table,nullColumnHack,cv);
        s.close();
        return ret_id;
    }

    public void updateData(String table, ContentValues cv, String condition, String[] whereArgs){
        SQLiteDatabase s = this.getWritableDatabase();
        s.update(
                table,
                cv,
                condition,
                whereArgs
        );
        s.close();
    }

    public void deleteData(String table, String condition, String[] args){
        SQLiteDatabase s = this.getWritableDatabase();
        s.delete(table,condition,args);
        s.close();
    }


    Cursor getDatesWithOrder(){
        Cursor c = this.getReadableDatabase().rawQuery(
                String.format(
                        context.getResources().getString(R.string.databaseQueryGetAllDataFromTableWithOrderDesc),
                        context.getResources().getString(R.string.databaseTableDays),
                        context.getResources().getString(R.string.databaseColumnDate)
                                )
                ,null);
        c.moveToFirst();
        return c;
    }

    public Cursor getTasksByID(int id){
        Cursor c = this.getReadableDatabase().rawQuery(
                String.format(
                        context.getResources().getString(R.string.databaseQueryGetDataFromTableWhereEqualsWithOrderByDesc),
                        context.getResources().getString(R.string.databaseTableTasks),
                        context.getResources().getString(R.string.databaseColumnIdDay),
                        String.valueOf(id),
                        context.getResources().getString(R.string.databaseColumnIsStar)
                ),
                null
        );
        c.moveToFirst();
        return c;
    }


    public boolean checkIfDayOfIdIsExist(int id){ //"select * from days where id ="+id+";"
        Cursor c = this.getReadableDatabase().rawQuery(
                String.format(
                        context.getResources().getString(R.string.databaseQueryGetDataFromTableWhereEquals),
                        context.getResources().getString(R.string.databaseTableDays),
                        context.getResources().getString(R.string.databaseColumnId),
                        String.valueOf(id)
                )
                ,null);
        boolean check = c.getCount() >= 1;
        c.close();
        return check;
    }



}