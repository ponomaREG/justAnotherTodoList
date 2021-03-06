package com.test.taskcurrent;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.taskcurrent.Services.ForegroundServiceForNotify;
import com.test.taskcurrent.helpers.AnotherHelpers;
import com.test.taskcurrent.helpers.Converters;
import com.test.taskcurrent.helpers.DBHelper;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class mainTasks extends AppCompatActivity {

    private DBHelper dbhelper;
    private ActionMode mActionMode;
    private ArrayList<View> list_of_checked_cells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tasks);

        hideTittleOfActionBar();
        init();
        checkDatabaseAndIfDoesntExistThenUpload();
        showDaysToUserAndSetOclToThem();
    }

    private void init(){
        initVariables();
        initOclForButtonAdd();
    }

    private void initVariables(){
        list_of_checked_cells = new ArrayList<>();
        dbhelper = new DBHelper(this);
    }

    private void hideTittleOfActionBar(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    private void initOclForButtonAdd(){
        findViewById(R.id.mainButtonAddNewDay).setOnClickListener(v -> addNewDay());
    }


    private void checkDatabaseAndIfDoesntExistThenUpload(){
        if(!dbhelper.checkDataBase()){
            dbhelper.copyDataBase();
        }
    }

    private void showDaysToUserAndSetOclToThem(){
        LinearLayout taskLL = findViewById(R.id.tasksLL);
        LinearLayout baseLL;
        TextView baseTextView;
        View.OnClickListener ocl = getOclForCellDays();
        View.OnLongClickListener long_ocl = getLongOclForCellDays();
        Cursor c = dbhelper.getAllLinesFromDB(getResources().getString(R.string.databaseQueryGetAllDataFromTable),
                getResources().getString(R.string.databaseTableDays));

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
                baseTextView.setTag(R.string.tagMainCellDate,c.getString(c.getColumnIndex(getResources().getString(R.string.databaseColumnDate))));
                baseTextView.setTag(c.getInt(c.getColumnIndex(getResources().getString(R.string.databaseColumnId))));

                baseTextView.setOnClickListener(ocl);
                baseTextView.setOnLongClickListener(long_ocl);
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

    @SuppressLint("SimpleDateFormat")
    private View.OnClickListener getOclForCellDays(){
        return v->{
            int idDays = (int) v.getTag();
            Intent intent = new Intent(mainTasks.this,taskOfCurrentDay.class);
            intent.putExtra(getResources().getString(R.string.intentExtraId),idDays);
            intent.putExtra(getResources().getString(R.string.intentExtraDate),String.valueOf(v.getTag(R.string.tagMainCellDate)));
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(Objects.requireNonNull(new SimpleDateFormat("dd.MM.yyyy").parse(String.valueOf(v.getTag(R.string.tagMainCellDate)))));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            //SET NOTIFY
            long time_set = AnotherHelpers.getTimeInMillisOfDayByMillis(calendar.getTimeInMillis(),8);
            if(System.currentTimeMillis()<time_set){
            Intent intent_for_notify = new Intent(mainTasks.this,ForegroundServiceForNotify.class);
            intent_for_notify.putExtra(this.getResources().getString(R.string.intentExtraId),idDays);
            intent_for_notify.putExtra(this.getResources().getString(R.string.intentExtraTimeForNotify), time_set);
            intent_for_notify.putExtra(this.getResources().getString(R.string.intentExtraAction),
                    this.getResources().getString(R.string.intentExtraActionAdd));
            intent_for_notify.putExtra(getResources().getString(R.string.intentExtraDate),String.valueOf(v.getTag(R.string.tagMainCellDate)));
            startService(intent_for_notify);
            }
            startActivity(intent);

        };
    }

    private View.OnLongClickListener getLongOclForCellDays(){
        return v -> {
            if(mActionMode == null) {
                AnotherHelpers.vibrateWhenClickIsLong(this);
                mActionMode = this.startActionMode(getActionMode());
                setAnotherOCLToDaysCellsWhenActionModeLaunch();
                v.setSelected(true);
                list_of_checked_cells.add(v);
                return true;
            }
            return false;
        };
    }


    private void setAnotherOCLToDaysCellsWhenActionModeLaunch(){
        View.OnClickListener ocl = getOclForDaysCellWhenActionModeLaunch();
        LinearLayout taskLY = findViewById(R.id.tasksLL);
        for(int i = 0 ;i<taskLY.getChildCount();i++){
            LinearLayout baseLL = (LinearLayout) taskLY.getChildAt(i);
            for(int j = 0;j<baseLL.getChildCount();j++){
                baseLL.getChildAt(j).setOnClickListener(ocl);
            }
        }
    }

    private View.OnClickListener getOclForDaysCellWhenActionModeLaunch(){
        return v->{
            if(v.isSelected()) {
                v.setSelected(false);
                list_of_checked_cells.remove(v);
                if(list_of_checked_cells.size() == 0) mActionMode.finish();
            }
            else {
                v.setSelected(true);
                list_of_checked_cells.add(v);
            }
        };
    }

    private void addNewDay(){
        @SuppressLint("SimpleDateFormat") DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,dayOfMonth);
            addIntoDatabaseNewDate(AnotherHelpers.convertLongToDate(calendar.getTimeInMillis()));
            updateTaskLL();
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
            dbhelper.insertData(getResources().getString(R.string.databaseTableDays), null, cv);
        }catch (SQLiteConstraintException exc){
            Toast.makeText(this,getResources().getString(R.string.toastError_dayExists),Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTaskLL(){
        clearAllViewsinTaskLL();
        showDaysToUserAndSetOclToThem();
    }

    private void clearAllViewsinTaskLL(){
        LinearLayout taskLL = findViewById(R.id.tasksLL);
        if(taskLL.getChildCount()>0) taskLL.removeAllViews();
    }






    private ActionMode.Callback getActionMode() {
         return  new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater mi = mode.getMenuInflater();
                mi.inflate(R.menu.menu_action_mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @SuppressLint("SimpleDateFormat")
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                for (View v : list_of_checked_cells) {
                    deleteDayLineInDatabase((int) v.getTag());
                    Intent intent_for_notify = new Intent(mainTasks.this, ForegroundServiceForNotify.class);
                    Calendar calendar = Calendar.getInstance();
                    try {
                        calendar.setTime(Objects.requireNonNull(new SimpleDateFormat(getResources().getString(R.string.dateFormat)).parse(String.valueOf(v.getTag(R.string.tagMainCellDate)))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    intent_for_notify.putExtra(getResources().getString(R.string.intentExtraId), (int) v.getTag());
                    intent_for_notify.putExtra(getResources().getString(R.string.intentExtraTimeForNotify), AnotherHelpers.getTimeInMillisOfYesterdayDayByMillisDay(calendar.getTimeInMillis(), Integer.parseInt(getResources().getString(R.string.timeForNotifyYD))));
                    intent_for_notify.putExtra(getResources().getString(R.string.intentExtraAction),
                            getResources().getString(R.string.intentExtraActionDelete));
                    startService(intent_for_notify);
                }
                mode.finish();
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
                list_of_checked_cells.clear();
                updateTaskLL();
            }
        };
    }

    private void deleteDayLineInDatabase(int id){
        dbhelper.deleteData(getResources().getString(R.string.databaseTableDays),String.format(getResources().getString(R.string.database_condition_delete),String.valueOf(id)),null);
    }

}
