package com.example.schedulerproject;

/**
 * Created by 현욱 on 2016-02-03.
 */
import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.example.schedulerproject.Lists;

public class EditDay extends Activity implements OnClickListener{
    LogManager logManager;
    private Calendar _calendar;
    private DatePickerDialog mDatePickerDialog = null;

    private static final String PROVIDER_URI = "content://com.example.scheduler.ListsProvider";
    private Calendar calendar, dcalendar;//d-day를 위한 캘린더
    private long d, t, r;
    private int dYear=1, dMonth=1, dDay=1;
    private int resultNumber = 0;
    static final int DATE_DIALOG_ID = 0;

    static String alarm2;
    String toDo1, memo1;
    String alarm1;
    TextView ddayText, alarmText;
    CheckBox dday, alarm;
    TextView num_events_per_day;
    Button cancel, ok;
    TextView title, memo = null;
    int month, year, day, caldday; //dday계산을 위한 변수
    private MainActivity using = new MainActivity();
    Cursor c;
    ListsAdapter adapter;
    MyHelper helper;
    SQLiteDatabase db;
    ArrayList<Lists> data = null;
    private AlarmActivity setvalue = new AlarmActivity();
    private AlarmActivity imsi;
    AlarmManager m2Manager;
    int position;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_edit);
        calendar = Calendar.getInstance(Locale.getDefault());
        dcalendar = Calendar.getInstance();
        month = calendar.get(Calendar.MONTH) + 1;
        year = calendar.get(Calendar.YEAR);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        dcalendar.set(using.Day, using.Month, using.Year);
        calendar.set(year, month, day);
        t = calendar.getTimeInMillis(); //오늘날짜
        d = dcalendar.getTimeInMillis(); //디데이날짜
        logManager.logPrint(d+"");
        r = (d-t)/(24*60*60*1000);
        resultNumber = (int)r;

        logManager.logPrint("onCreate alarm1 : " + alarm1);

        logManager.logPrint(resultNumber+"");
        caldday = (int)r + 1;

        Intent _intent = getIntent();

        //dday체크박스 버튼
        dday = (CheckBox)findViewById(R.id.dday);
        dday.setOnClickListener(this);
        ddayText = (TextView) findViewById(R.id.show_dday);
        if(_intent.getStringExtra("dDay") != null) {
            dday.setChecked(true);
            ddayText.setText(_intent.getStringExtra("dDay"));
        }
        if(_intent.getStringExtra("dDay") == null) {
            dday.setChecked(false);
            ddayText.setText("");
        }

        //알람체크박스버튼
        alarm = (CheckBox)findViewById(R.id.alarm);
        alarm.setOnClickListener(this);
        alarmText = (TextView) findViewById(R.id.textView10);
        if(_intent.getStringExtra("settingAlarm") != null) {
            alarm.setChecked(true);
            logManager.logPrint("setvalue.setting : " + setvalue.setting);
            alarmText.setText(alarm1);
        }
        if(_intent.getStringExtra("settingAlarm") == null) {
            alarm.setChecked(false);
            alarmText.setText("");
        }
        m2Manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE); // 알람 매니저를 취득

        //취소버튼
        cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        //저장버튼
        ok = (Button)findViewById(R.id.ok);
        ok.setOnClickListener(this);
        //달력에서 클릭한 날짜
        num_events_per_day = (TextView)findViewById(R.id.num_events_per_day);
        if(using.imsi == null) {
            String themonth = String.format("%02d", month);
            String theday = String.format("%02d", day);
            num_events_per_day.setText(year + "/" + themonth + "/" + theday);
        } else {
            num_events_per_day.setText(using.imsi);
            if(_intent.getStringExtra("theDay") != null) {
                flag = true;
                position = _intent.getIntExtra("position", 100);
                logManager.logPrint("_intent,getIntExtra : " + position);
                num_events_per_day.setText(_intent.getStringExtra("theDay"));
            }
        }
        num_events_per_day.setOnClickListener(this);
        //일정할일(제목)
        title = (TextView)findViewById(R.id.title);
        if(_intent.getStringExtra("toDo") != null) {
            title.setText(_intent.getStringExtra("toDo"));
            toDo1 = _intent.getStringExtra("toDo");
        }

        //세부일정(메모)
        memo = (TextView)findViewById(R.id.memo);
        if(_intent.getStringExtra("memo") != null) {
            memo.setText(_intent.getStringExtra("memo"));
            memo1 = _intent.getStringExtra("memo");
        }

    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.num_events_per_day :
                DatePickerDialog.OnDateSetListener callBack = new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet( DatePicker View, int year, int monthOfYear, int dayOfMonth )
                    {
                        String theday = String.format("%02d", dayOfMonth);
                        String themonth = String.format("%02d", (monthOfYear + 1));
                        num_events_per_day.setText(year + "/" + themonth + "/" + theday);
                    }
                };
                mDatePickerDialog = new DatePickerDialog(this, callBack, this.year, this.month - 1, 1);
                mDatePickerDialog.show();
                break;
            case R.id.alarm :
                if(alarm.isChecked()) {
                    Intent intent = new Intent(EditDay.this, AlarmActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.dday :
                if(dday.isChecked()) {
                    if(resultNumber >= 0) {
                        ddayText.setText(String.format("D-%d", resultNumber));
                    }
                    else {
                        int absR = Math.abs(resultNumber);
                        ddayText.setText(String.format("D+%d", absR));
                    }
                }
                else{
                    ddayText.setText("");
                }
                break;
            case R.id.cancel :
                Intent intent = new Intent(EditDay.this, EntireList.class);
                startActivity(intent);
                break;
            case R.id.ok :
                if(flag == false) {
                    m2Manager.set(AlarmManager.RTC_WAKEUP, setvalue.result, pendingIntent());
                    alarm2 = imsi.setting;
                    alarmText.setText(alarm2);
                    inserting();
                }
                if(flag == true) {
                    m2Manager.set(AlarmManager.RTC_WAKEUP, setvalue.result, pendingIntent());
                    alarm2 = imsi.setting;
                    alarmText.setText(alarm2);
                    updating(position);
                }
                break;
        }
    }

    private DatePickerDialog.OnDateSetListener dDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dYear = year;
            dMonth = monthOfYear;
            dDay = dayOfMonth;
            final Calendar dCalendar = Calendar.getInstance();
            dCalendar.set(dYear, dMonth, dDay);

            d = dCalendar.getTimeInMillis();
            r = (d-t)/(24*60*60*1000);

            resultNumber = (int)r;
        }
    };

    protected Dialog onCreateDialog(int id){
        if(id == DATE_DIALOG_ID){
            return new DatePickerDialog(this, dDateSetListener, year, month, day);
        }
        return null;
    }

    //알람의 설정 시각에 발생하는 인텐트 작성
    public PendingIntent pendingIntent() {
        Intent i = new Intent(EditDay.this, AlarmEnd.class);
        PendingIntent pi = PendingIntent.getActivity(EditDay.this, 0, i, 0);
        return pi;
    }

    void inserting() {
        doDBOpen();
        ContentValues values = new ContentValues();
        values.put("toDo", title.getText().toString());
        values.put("memo", memo.getText().toString());
        values.put("dDay", ddayText.getText().toString());
        values.put("settingAlarm", alarm1);
        logManager.logPrint("alarm1 : " + alarm1);
        values.put("theDay", num_events_per_day.getText().toString());
        try {
            long id = db.insert("lists", null, values);
            logManager.logPrint(id > 0 ? "insert success" : "insert fail");
        } catch (SQLException e) {
            logManager.logPrint("insert error : " + e);
        }
        doDBClose();
        Intent intent = new Intent(EditDay.this, ListTheDay.class);
        startActivity(intent);
        finish();
    }

    void updating(int position) {
        doDBOpen();
        ContentValues values = new ContentValues();
        Intent _intent = getIntent();
        values.put("toDo", title.getText().toString());
        values.put("memo", memo.getText().toString());
        values.put("dDay", ddayText.getText().toString());
        values.put("settingAlarm", alarm1);
        if(dday.isChecked()) {
            values.put("dDay", ddayText.getText().toString());
        }
        if(!dday.isChecked()) {
            values.put("dDay", "");
        }
        if(alarm.isChecked()) {
            values.put("settingAlarm", _intent.getStringExtra("settingAlarm"));
        }
        if(!alarm.isChecked()) {
            values.put("alarm", "");
        }
        try {
            long id = db.update("lists", values, "toDo = '" + toDo1 + "' and memo = '" + memo1 + "';" , null);
            logManager.logPrint("position : " + position);
            logManager.logPrint(id > 0 ? "insert success" : "insert fail");
        } catch (SQLException e) {
            logManager.logPrint("insert error : " + e);
        }
        doDBClose();
        Intent intent = new Intent(EditDay.this, ListTheDay.class);
        startActivity(intent);
        finish();
    }

    void doDBOpen() {
        if(helper == null) {
            helper = new MyHelper(this, "myDB.db", null, 1);
        }
        db = helper.getWritableDatabase();
    }

    void doDBClose() {
        if(db != null) {
            if(db.isOpen()) {
                db.close();
            }
        }
    }

}
