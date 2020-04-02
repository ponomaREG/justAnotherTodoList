package com.test.taskcurrent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.test.taskcurrent.helpers.Converters;
import com.test.taskcurrent.helpers.DBHelper;
import com.test.taskcurrent.helpers.Task;
import com.test.taskcurrent.helpers.ViewHolderRV;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class taskOfCurrentDay extends AppCompatActivity {

    private DBHelper dbhelper;
    private int id;
    private ViewHolderRV adapter;
    private ActionMode mActionMode;
    public static boolean isActionMode = false;
    private List<Task> checked_tasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_of_current_days);
        hideTittleOfActionBar();
        init();


    }

    private void init(){
        initVariables();
        initRecyclerView();
        initOclForButtonAdd();
    }

    private void initVariables(){
        dbhelper = new DBHelper(this);
        id = getIntent().getIntExtra("id",-1);
        if(id == -1) finish();
        adapter = new ViewHolderRV(this,getListOfTasks());
        checked_tasks = new ArrayList<>();
    }

    private void initRecyclerView(){
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }


    private void initOclForButtonAdd(){
        findViewById(R.id.mainButtonAddNewDay).setOnClickListener(v -> {
            addNewTask();
        });
    }

    private void hideTittleOfActionBar(){
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }


    private List<Task> getListOfTasks(){
        List<Task> tasks = new ArrayList<>();
        Cursor c = getTasksByID(id);
        c.moveToFirst();
        for(int i = 0;i<c.getCount();i++){
            Log.d("COUNT",i+" ");
            tasks.add(new Task(
                    c.getInt(c.getColumnIndex(getResources().getString(R.string.databaseColumnId))),
                    c.getString(c.getColumnIndex(getResources().getString(R.string.databaseColumnTask))),
                    c.getInt(c.getColumnIndex(getResources().getString(R.string.databaseColumnIsDone))) == 1
            ));
            c.moveToNext();
        }
        c.close();
        return tasks;
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

    private void addNewTask(){
        createDialogBuilder();
    }

    public void createDialogBuilder(){
        AlertDialog.Builder ab = new AlertDialog.Builder(this,R.style.AlertDialog);
        View view = this.getLayoutInflater().inflate(R.layout.dialog_edittextview,null);
        final EditText new_task = view.findViewById(R.id.dialog_newTask);
        ab.setCustomTitle(this.getLayoutInflater().inflate(R.layout.dialog_custom_title,null))
                .setView(view)
                .setPositiveButton("Создать", (dialog, which) -> {
                    Log.d("MAKE",new_task.getText().toString());
                    addIntoDatabaseNewTask(new_task.getText().toString(),id);
                    dialog.dismiss();
                })
                .setNegativeButton("Отмена",((dialog, which) -> {
                    dialog.dismiss();
                }));
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

    private boolean addIntoDatabaseNewTask(String task,int id_day){
        ContentValues cv = new ContentValues();
        cv.put(getResources().getString(R.string.databaseColumnTask),task);
        cv.put(getResources().getString(R.string.databaseColumnIdDay),id_day);
        try {
            dbhelper.getReadableDatabase().insertOrThrow(
                    getResources().getString(R.string.databaseTableTasks),
                    null,
                    cv
                    );
        }catch(SQLiteConstraintException exc){
            Toast.makeText(this,"Произошла ошибка(",Toast.LENGTH_SHORT).show();
            return false;
        }
        addTaskInListOfTasksAtLast(task,id_day);
        return true;
    }

    private void addTaskInListOfTasksAtLast(String task,int id_day){
        adapter.getDataSet().add(new Task(
                dbhelper.getIDFromTableTwoCondition(
                        getResources().getString(R.string.databaseQueryGetDataFromTableWhereEqualsTwoCondition),
                        getResources().getString(R.string.databaseTableTasks),
                        getResources().getString(R.string.databaseColumnTask),
                        "'"+task+"'",
                        getResources().getString(R.string.databaseColumnIdDay),
                        String.valueOf(id_day),
                        getResources().getString(R.string.databaseColumnId)
                ),
                task,
                false));
        notifyAdapterBecauseDataSetChanged();
    }

//    private void removeTaskFromDatabaseByID(int id) {
//        ContentValues cv = new ContentValues();
//        cv.put(getResources().getString(R.string.databaseColumnId), id);
//        dbhelper.getReadableDatabase().delete(getResources().getString(R.string.databaseTableTasks), String.format(getResources().getString(R.string.database_condition_delete), String.valueOf(id)), null);
//    }
//    private void removeTaskInListOfTasks(int position_of_task){
//        adapter.getDataSet().remove(position_of_task);
//
//        notifyAdapterBecauseDataSetChanged();
//    }

    private void notifyAdapterBecauseDataSetChanged(){
        adapter.notifyDataSetChanged();
    }


    public void startActionModeForTask(){
        mActionMode = this.startActionMode(getActionMode());
    }

    public void addInCheckedTasks(int position){
        checked_tasks.add(adapter.getDataSet().get(position));
    }

    public boolean checkIfTaskAlreadyInCheckedTasks(Task t){
        return checked_tasks.contains(t);
    }

    public void removeTaskFromCheckedTasks(Task t){
       checked_tasks.remove(t);
       checkedTasksForZeroSizeAndIfZeroThenFinishActionMode();
    }

    public void checkedTasksForZeroSizeAndIfZeroThenFinishActionMode(){
        if(checked_tasks.size()==0) mActionMode.finish();
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
                        for(Task v:checked_tasks) {
                            adapter.getDataSet().remove(v);
                            deleteTaskLineInDatabase(v.getID());
                        }
                        mode.finish();
                        notifyAdapterBecauseDataSetChanged();
                        break;

                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.d("DESTROYED","1");
                mActionMode = null;
                isActionMode = false;
                checked_tasks.clear();
                adapter.notifyDataSetChanged();
            }
        };
    }

    private void deleteTaskLineInDatabase(int id){
        Log.d("ID",id+"");
        dbhelper.getReadableDatabase().delete(getResources().getString(R.string.databaseTableTasks),String.format(getResources().getString(R.string.database_condition_delete),String.valueOf(id)),null);
    }

}
