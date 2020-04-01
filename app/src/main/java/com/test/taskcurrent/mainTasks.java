package com.test.taskcurrent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class mainTasks extends AppCompatActivity {

    private DBHelper dbhelper;
    private ActionMode mActionMode;
    private ArrayList<View> list_of_checked_cells;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tasks);
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

    private void showDaysToUserAndSetOclToThem(){
        Log.d("SHOW","1");
        LinearLayout taskLL = findViewById(R.id.tasksLL);
        LinearLayout baseLL;
        TextView baseTextView;
        View.OnClickListener ocl = getOclForCellDays();
        View.OnLongClickListener long_ocl = getLongOclForCellDays();
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

    private View.OnClickListener getOclForCellDays(){
        return v->{
            int idDays = (int) v.getTag();
//
//            if (v.isSelected()) v.setSelected(false);
//            else v.setSelected(true);

            Log.d("DAYS",idDays+" ");
            Log.d("SELECTED",v.isSelected()+" ");
        };
    }

    private View.OnLongClickListener getLongOclForCellDays(){
        return v -> {
//            if(mActionMode != null){
//                return false;
//            }
            mActionMode = this.startActionMode(getActionMode());
            setAnotherOCLToDaysCellsWhenActionModeLaunch();
            v.setSelected(true);
            list_of_checked_cells.add(v);
            return true;
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
            }
            else {
                v.setSelected(true);
                list_of_checked_cells.add(v);
            }
        };
    }

    private void addNewDay(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,dayOfMonth);
            addIntoDatabaseNewDate(new SimpleDateFormat("dd.MM.yyyy").format(calendar.getTime()));
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
            dbhelper.getReadableDatabase().insertOrThrow(getResources().getString(R.string.databaseTableDays), null, cv);
        }catch (SQLiteConstraintException exc){
            Toast.makeText(this,getResources().getString(R.string.toastError),Toast.LENGTH_SHORT).show();
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

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.buttonDelete:
                        Log.d("PRESSED DELETE", "1");
                        for(View v:list_of_checked_cells) deleteDayLineInDatabase((int) v.getTag());
                        mode.finish();
                        break;

                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.d("DESTROYED","1");
                mActionMode = null;
                updateTaskLL();
            }
        };
    }

    private void deleteDayLineInDatabase(int id){
        Log.d("ID",id+"");
        dbhelper.getReadableDatabase().delete(getResources().getString(R.string.databaseTableDays),String.format(getResources().getString(R.string.database_condition_delete),String.valueOf(id)),null);
    }




}
