package com.test.taskcurrent.helpers;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.test.taskcurrent.R;

public class Config extends AppCompatActivity {


    private Cursor c;
    private String date = null;
    private int dayID = -1;
    private int colorBackground = -1;
    private int colorText = -1;
    private LinearLayout colorCellBackground = null;
    private LinearLayout colorCellText = null;

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
        editTitleOfActionBar();
        initSpinner();

        findViewById(R.id.buttonConfig).setOnClickListener(v -> {
            if(date == null) Toast.makeText(this,this.getResources().getString(R.string.toastError_must_choose_day),Toast.LENGTH_SHORT).show();
            else if(!checkIfColorsChoosen()) Toast.makeText(this,this.getResources().getString(R.string.toastError_must_choose_color),Toast.LENGTH_SHORT).show();
            else {
                SharedPreferences sp = getSharedPreferences(getResources().getString(R.string.widget_pref), Context.MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.putString(getResources().getString(R.string.widget_text) + widget_id, date);
                ed.putInt(getResources().getString(R.string.widget_dayID)+widget_id,dayID);
                ed.putInt(getResources().getString(R.string.widget_colorID_background) + widget_id, colorBackground);
                ed.putInt(getResources().getString(R.string.widget_colorID_text) + widget_id, colorText);
                ed.apply();
                Widget.updateAppWidget(this, sp, AppWidgetManager.getInstance(this), widget_id);
                setResult(RESULT_OK, result_intent);
                finish();
            }
        });
    }

    @SuppressLint("WrongConstant")
    private  void editTitleOfActionBar(){
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setCustomView(R.layout.action_bar_custom_for_task);
        actionBar.setDisplayOptions(android.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.getCustomView().findViewById(R.id.actionBarIconBack).setVisibility(View.GONE);
        ((TextView) actionBar.getCustomView().findViewById(R.id.actionBarTitle)).setText(this.getResources().getString(R.string.config_actionbar_title));
        ((TextView) actionBar.getCustomView().findViewById(R.id.actionBarTitle)).setTextSize(this.getResources().getDimension(R.dimen.configActionbarTitleTextSize));
    }


    private void initSpinner(){
        DBHelper dbHelper = new DBHelper(this);
        Context context = this;
        c = dbHelper.getDatesWithOrder();
        if(c.getCount() == 0) {
            Toast.makeText(this,this.getResources().getString(R.string.toastError_no_days),Toast.LENGTH_SHORT).show();
        }else {
            String title = this.getResources().getString(R.string.config_spinner_title);
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.config_spinner_item, Converters.getStringsArrayFromCursor(c,"date"));
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            Spinner spinner = findViewById(R.id.spinnerConfig);
            spinner.setAdapter(arrayAdapter);
            spinner.setPrompt(title);
//            spinner.setSelection(0);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    c.moveToPosition(position);
                    date = c.getString(c.getColumnIndex(context.getResources().getString(R.string.databaseColumnDate)));
                    dayID = c.getInt(c.getColumnIndex(context.getResources().getString(R.string.databaseColumnId)));
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


    public void onClickOnCellForBackgroundColor(View view) {
        if(colorCellBackground != null) colorCellBackground.setBackground(this.getResources().getDrawable(R.drawable.config_color_cell));
        colorCellBackground = findViewById(view.getId());
        colorCellBackground.setBackground(this.getResources().getDrawable(R.drawable.config_color_cell_choose));
        switch (view.getId()){
            case R.id.configColorBackgroundWhite:
                colorBackground = R.color.configColorBackgroundWhite;
                break;
            case R.id.configColorBackgroundLightGray:
                colorBackground = R.color.configColorBackgroundLightGray;
                break;
            case R.id.configColorBackgroundGray:
                colorBackground = R.color.configColorBackgroundGray;
                break;
            case R.id.configColorBackgroundDarkGray:
                colorBackground = R.color.configColorBackgroundDarkGray;
                break;
            case R.id.configColorBackgroundBlack:
                colorBackground = R.color.configColorBackgroundBlack;
                break;
        }
    }

    public void onClickOnCellForTextColor(View view) {
        if(colorCellText != null) colorCellText.setBackground(this.getResources().getDrawable(R.drawable.config_color_cell));
        colorCellText = findViewById(view.getId());
        colorCellText.setBackground(this.getResources().getDrawable(R.drawable.config_color_cell_choose));
        switch (view.getId()){
            case R.id.configColorTextWhite:
                colorText = R.color.configColorTextWhite;
                break;
            case R.id.configColorTextBlack:
                colorText = R.color.configColorTextBlack;
                break;
        }
    }

    private boolean checkIfColorsChoosen(){
        return ((colorBackground!=-1)&&(colorText!=-1));
    }
}
