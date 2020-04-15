package com.test.taskcurrent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.test.taskcurrent.helpers.Converters;
import com.test.taskcurrent.helpers.DBHelper;
import com.test.taskcurrent.helpers.Task;
import com.test.taskcurrent.helpers.ViewHolderRV;
import com.test.taskcurrent.helpers.Widget;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class taskOfCurrentDay extends AppCompatActivity {

    private DBHelper dbhelper;
    private int id;
    private ViewHolderRV adapter;
    private ActionMode mActionMode;
    public static boolean isActionMode = false;
    private RecyclerView rv;
    private List<Task> checked_tasks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_of_current_days);
        prepareActionBar();
        init();


    }

    private void init(){
        initVariables();
        initRecyclerView();
        initOclForButtonAdd();
    }

    private void initVariables(){
        dbhelper = new DBHelper(this);
        id = getIntent().getIntExtra(this.getResources().getString(R.string.intentExtraId),-1);
        if(id == -1) finish();
        if(!dbhelper.checkIfDayOfIdIsExist(id)) finish();
        adapter = new ViewHolderRV(this,getListOfTasks());
        checked_tasks = new ArrayList<>();
    }

    private void initRecyclerView(){
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }


    private void initOclForButtonAdd(){
        findViewById(R.id.mainButtonAddNewDay).setOnClickListener(v -> addNewTask());
    }




    private List<Task> getListOfTasks(){
        List<Task> tasks = new ArrayList<>();
        Cursor c = dbhelper.getTasksByID(id);
        for(int i = 0;i<c.getCount();i++){
            tasks.add(new Task(
                    c.getInt(c.getColumnIndex(getResources().getString(R.string.databaseColumnId))),
                    c.getString(c.getColumnIndex(getResources().getString(R.string.databaseColumnTask))),
                    c.getInt(c.getColumnIndex(getResources().getString(R.string.databaseColumnIsDone))) == 1,
                    c.getInt(c.getColumnIndex(getResources().getString(R.string.databaseColumnIsStar))) == 1
            ));
            c.moveToNext();
        }
        c.close();
        return tasks;
    }

    private void addNewTask(){
        createDialogBuilder(false,null,-1);
    }

    private void editExistsTask(){
        createDialogBuilder(true,checked_tasks.get(0).getTask(),checked_tasks.get(0).getID());
    }

    @SuppressLint("InflateParams")
    public void createDialogBuilder(boolean isEdit,String text_begin,int id_task){
        AlertDialog.Builder ab = new AlertDialog.Builder(this,R.style.AlertDialog);
        View view = this.getLayoutInflater().inflate(R.layout.dialog_edittextview,null);
        final EditText new_task = view.findViewById(R.id.dialog_newTask);
        if(isEdit) new_task.setText(text_begin);
        String positive_button_title;
        if(isEdit) positive_button_title  = getResources().getString(R.string.dialog_button_edit);
        else positive_button_title  = getResources().getString(R.string.dialog_button_make);
        ab.setCustomTitle(this.getLayoutInflater().inflate(R.layout.dialog_custom_title,null))
                .setView(view)
                .setPositiveButton(positive_button_title, (dialog, which) -> {
                    addOrEditIntoDatabaseNewTask(new_task.getText().toString(),id,isEdit,id_task);
                    dialog.dismiss();
                })
                .setNegativeButton(getResources().getString(R.string.dialog_button_cancel),((dialog, which) -> dialog.dismiss()));
        AlertDialog ad_cr = ab.create();
        ad_cr.setOnShowListener(dialog -> {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT,100.0f);
            lp.gravity= Gravity.CENTER;
            Button positiveButton = ad_cr.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton = ad_cr.getButton(DialogInterface.BUTTON_NEGATIVE);
            int pixelsFromDp = Converters.getPixelFromDP(2.0f,this);
            lp.setMargins(pixelsFromDp,pixelsFromDp,pixelsFromDp,pixelsFromDp);
            positiveButton.setLayoutParams(lp);
            positiveButton.setBackground(getResources().getDrawable(R.drawable.dialog_button_style));
            positiveButton.setTextColor(getResources().getColor(R.color.colorFullBlack));
            negativeButton.setLayoutParams(lp);
            negativeButton.setBackground(getResources().getDrawable(R.drawable.dialog_button_style));
            negativeButton.setTextColor(getResources().getColor(R.color.colorFullBlack));
        });
        ad_cr.show();
    }

    private void addOrEditIntoDatabaseNewTask(String task, int id_day, boolean is_edit, int task_id_for_edit){
        ContentValues cv = new ContentValues();
        cv.put(getResources().getString(R.string.databaseColumnTask),task);
        if(!is_edit) {
            try {
                cv.put(getResources().getString(R.string.databaseColumnIdDay),id_day);
                long id_task = dbhelper.getWritableDatabase().insertOrThrow(
                        getResources().getString(R.string.databaseTableTasks),
                        null,
                        cv
                );
                addTaskInListOfTasksAtLast((int) id_task, task);
            } catch (SQLiteConstraintException exc) {
                Toast.makeText(this, this.getResources().getString(R.string.toastError_some_error), Toast.LENGTH_SHORT).show();
            }
        }else {
            try {
                dbhelper.getWritableDatabase().update(
                        getResources().getString(R.string.databaseTableTasks),
                        cv,
                        String.format(getResources().getString(R.string.database_condition_update_uni), getResources().getString(R.string.databaseColumnId),task_id_for_edit),
                        null
                );
            } catch (SQLiteConstraintException exc) {
                Toast.makeText(this, this.getResources().getString(R.string.toastError_some_error), Toast.LENGTH_SHORT).show();
            }
            editInDataSet(task,task_id_for_edit);
            notifyAdapterBecauseDataSetChanged();
        }
    }

    private void editInDataSet(String new_task,int id_task){
        for(int i = 0;i<adapter.getDataSet().size();i++){
            if(adapter.getDataSet().get(i).getID()==id_task) {
                adapter.getDataSet().get(i).setTask(new_task);
                break;
            }
        }
    }

    private void addTaskInListOfTasksAtLast(int id_task,String task){
        adapter.getDataSet().add(new Task(
                id_task,
                task,
                false,
                false));
        notifyAdapterBecauseDataSetChanged();
    }


    public void setDoneOrUndoneTaskByID(int id_task,boolean is_done){
        ContentValues cv = new ContentValues();
        cv.put(getResources().getString(R.string.databaseColumnIsDone),((is_done) ? 1:0));
        dbhelper.getWritableDatabase().
                update(getResources().getString(R.string.databaseTableTasks),
                        cv,
                        String.format(getResources().getString(R.string.database_condition_delete),String.valueOf(id_task)),
                        null
                        );
    }

    public void setStarOrUnstarTaskByID(int id_task,boolean is_star){
        ContentValues cv = new ContentValues();
        cv.put(getResources().getString(R.string.databaseColumnIsStar),((is_star) ? 1:0));
        dbhelper.getWritableDatabase().
                update(getResources().getString(R.string.databaseTableTasks),
                        cv,
                        String.format(getResources().getString(R.string.database_condition_delete),String.valueOf(id_task)),
                        null
                );
    }

    private void notifyAdapterBecauseDataSetChanged(){
        adapter.notifyDataSetChanged();
    }


    public void startActionModeForTask(){
        mActionMode = this.startActionMode(getActionMode());
    }

    public void addInCheckedTasks(int position){
        checked_tasks.add(adapter.getDataSet().get(position));
        checkIfCheckedTaskIsOneAndLaunchOrUnlaucnhEditButton();
        Objects.requireNonNull(rv.findViewHolderForAdapterPosition(position)).itemView.findViewById(R.id.line_textview_ll).setBackground(getResources().getDrawable(R.drawable.task_pressed));
    }

    private void checkIfCheckedTaskIsOneAndLaunchOrUnlaucnhEditButton(){
        if(checked_tasks.size()==1){
            mActionMode.getMenu().findItem(R.id.menuButtonEdit).setVisible(true);
        }else{
            mActionMode.getMenu().findItem(R.id.menuButtonEdit).setVisible(false);
        }
    }

    public boolean checkIfTaskAlreadyInCheckedTasks(Task t){
        return checked_tasks.contains(t);
    }

    public void removeTaskFromCheckedTasks(int position){
       checked_tasks.remove(adapter.getDataSet().get(position));
       Objects.requireNonNull(rv.findViewHolderForAdapterPosition(position)).itemView.findViewById(R.id.line_textview_ll).setBackground(getResources().getDrawable(R.drawable.task));
       if(!checkedTasksForZeroSizeAndIfZeroThenFinishActionMode())
       checkIfCheckedTaskIsOneAndLaunchOrUnlaucnhEditButton();
    }

    public boolean checkedTasksForZeroSizeAndIfZeroThenFinishActionMode(){
        if(checked_tasks.size()==0) {
            mActionMode.finish();
            return true;
        }
        return false;
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
                if(item.getItemId() == R.id.menuButtonDelete){
                for (Task v : checked_tasks) {
                       adapter.getDataSet().remove(v);
                       deleteTaskLineInDatabase(v.getID());
                    }
                }else{
                    editExistsTask();
                }
                mode.finish();
                notifyAdapterBecauseDataSetChanged();
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
                isActionMode = false;
                checked_tasks.clear();
                adapter.setVisibilityOfIcon(true);
                adapter.notifyDataSetChanged();
            }
        };
    }

    @SuppressLint("WrongConstant")
    private void prepareActionBar(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_custom_for_task);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actionBarTitle)).setText(getIntent().getStringExtra(getResources().getString(R.string.intentExtraDate)));
        getSupportActionBar().getCustomView().findViewById(R.id.actionBarIconBack).setOnClickListener(v -> finish());
    }

    private void deleteTaskLineInDatabase(int id){
        dbhelper.getReadableDatabase().delete(getResources().getString(R.string.databaseTableTasks),String.format(getResources().getString(R.string.database_condition_delete),String.valueOf(id)),null);
    }


    @Override
    protected void onPause() {
        updateListViewOfWidget();
        super.onPause();
    }

    private void updateListViewOfWidget(){
        AppWidgetManager awm = AppWidgetManager.getInstance(this);
        SharedPreferences sp = getSharedPreferences(this.getResources().getString(R.string.widget_pref), Context.MODE_PRIVATE);
        for(int idWidget:awm.getAppWidgetIds(new ComponentName(this, Widget.class))){
            if(sp.getInt(this.getResources().getString(R.string.widget_dayID)+idWidget,-1) == id){
                awm.notifyAppWidgetViewDataChanged(idWidget,R.id.widget_listview);
            }
        }
    }
}
