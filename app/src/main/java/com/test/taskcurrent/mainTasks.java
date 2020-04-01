package com.test.taskcurrent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.taskcurrent.helpers.Converters;
import com.test.taskcurrent.helpers.DBHelper;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class mainTasks extends AppCompatActivity {

    private DBHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tasks);

        init();
        checkDatabaseAndIfDoesntExistThenUpload();
        showDaysToUserAndInitOclToThem();

    }

    private void init(){
        initVariables();
        initOclForButtonAdd();
    }

    private void initVariables(){
        dbhelper = new DBHelper(this);
    }

    private void initOclForButtonAdd(){
        findViewById(R.id.mainButtonAddNewDay).setOnClickListener(v -> {
            addNewDay();
        });
    }


    private void checkDatabaseAndIfDoesntExistThenUpload(){
        if(!dbhelper.checkDataBase()){
            dbhelper.copyDataBase();
        }
    }

    private void showDaysToUserAndInitOclToThem(){
        LinearLayout taskLL = findViewById(R.id.tasksLL);
        LinearLayout baseLL;
        TextView baseTextView;
        View.OnClickListener ocl = getOclForCellDays();
        Cursor c = getDaysFromDB();

        searching:
        for(int i = 0;i<c.getCount();i++){
            baseLL = (LinearLayout) this.getLayoutInflater().inflate(R.layout.main_task_base_ll, taskLL, false);
            for(int j = 0;j<2;j++) {
                baseTextView = (TextView) this.getLayoutInflater().inflate(R.layout.main_task_base_textview, baseLL, false);
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) baseTextView.getLayoutParams();
                if ((j%2 == 0)) {
                    if(!c.isLast())
                    lp.setMargins(
                        0,
                        0,
                        Converters.getPixelFromDP(getResources().getDimension(R.dimen.mainMarginTaskLeft), this),
                        0);}
                else  lp.setMargins(
                        Converters.getPixelFromDP(getResources().getDimension(R.dimen.mainMarginTaskLeft),this),
                        0,
                        0,
                        0);
                baseTextView.setLayoutParams(lp);
                baseTextView.setText(c.getString(c.getColumnIndex(getResources().getString(R.string.databaseColumnDate))));
                baseTextView.setTag(c.getInt(c.getColumnIndex(getResources().getString(R.string.databaseColumnId))));
                baseTextView.setOnClickListener(ocl);
                baseLL.addView(baseTextView);
                if(c.isLast()) {
                    taskLL.addView(baseLL);
                    break searching;
                }
                c.move(1);
            }
            taskLL.addView(baseLL);
        }
        c.close();
    }

    private View.OnClickListener getOclForCellDays(){
        return v->{
            int idDays = (int) v.getTag();
            Log.d("DAYS",idDays+" ");
        };
    }

    private void addNewDay(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,dayOfMonth);
            addIntoDatabaseNewDate(new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime()));
            recreate();
        }, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


    private void addIntoDatabaseNewDate(String date){
        ContentValues cv = new ContentValues();
        cv.put(getResources().getString(R.string.databaseColumnDate),date);
        try {
            dbhelper.getReadableDatabase().insertOrThrow(getResources().getString(R.string.databaseTableDays), null, cv);
        }catch (SQLiteConstraintException exc){
            Toast.makeText(this,getResources().getString(R.string.toastError),Toast.LENGTH_SHORT).show();
        }
    }




    private Cursor getDaysFromDB(){
        Cursor c = dbhelper.getReadableDatabase().rawQuery(
                String.format(
                        getResources().getString(R.string.databaseQueryGetAllDataFromTable),
                        getResources().getString(R.string.databaseTableDays)
                ),
                null
        );
        c.moveToFirst();
        return c;
    }

    private Cursor getTasksByID(int id){
        return dbhelper.getReadableDatabase().rawQuery(
          String.format(
                  getResources().getString(R.string.databaseQueryGetDataFromTableWhereEquals),
                  getResources().getString(R.string.databaseTableTasks),
                  getResources().getString(R.string.databaseColumnIdDay),
                  String.valueOf(id)
          ),
          null
        );
    }





}
