package com.test.taskcurrent.helpers;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.test.taskcurrent.R;

public class Config extends AppCompatActivity {

    public final static String WIDGET_PREF = "widget_pref";
    public final static String WIDGET_TEXT = "widget_text_";
    public final static String WIDGET_COLOR = "widget_color_";
    public final static String WIDGET_DAY_ID = "widget_dayID_";

    private Cursor c;
    private String date = null;
    private int dayID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int widget_id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
        if(widget_id == AppWidgetManager.INVALID_APPWIDGET_ID) finish();
        Intent result_intent = new Intent();
        result_intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,widget_id);
        setResult(RESULT_CANCELED,result_intent);
        setContentView(R.layout.config_widget);
        initSpinner();

        findViewById(R.id.buttonConfig).setOnClickListener(v -> {
            if(date == null) Toast.makeText(this,"Выберите день",Toast.LENGTH_SHORT).show();
            else {
                SharedPreferences sp = getSharedPreferences(WIDGET_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString(WIDGET_TEXT + widget_id, date);
                ed.putInt(WIDGET_DAY_ID+widget_id,dayID);
                ed.apply();
                Widget.updateAppWidget(this, sp, AppWidgetManager.getInstance(this), widget_id);
                setResult(RESULT_OK, result_intent);
                finish();
            }
        });
    }


    private void initSpinner(){
        DBHelper dbHelper = new DBHelper(this);
        c = dbHelper.getDatesWithOrder();
        if(c.getCount() == 0) {
//            title = "У Вас нет созданных дней для отображения(";
            Toast.makeText(this,"У Вас нет созданных дней для отображения(",Toast.LENGTH_SHORT).show();
        }else {
            String title = "Выберите день";
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Converters.getStringsArrayFromCursor(c,"date"));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            Spinner spinner = findViewById(R.id.spinnerConfig);
            spinner.setAdapter(arrayAdapter);
            spinner.setPrompt(title);
            spinner.setSelection(0);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    c.moveToPosition(position);
                    date = c.getString(c.getColumnIndex("date"));
                    dayID = c.getInt(c.getColumnIndex("id"));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) { }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        c.close();
    }
}
