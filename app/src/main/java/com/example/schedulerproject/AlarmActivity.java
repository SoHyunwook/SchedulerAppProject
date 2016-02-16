package com.example.schedulerproject;

/**
 * Created by 현욱 on 2016-02-16.
 */

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

public class AlarmActivity extends Activity implements OnDateChangedListener, OnTimeChangedListener, OnClickListener {

    LogManager logManager;

    private AlarmManager mManager; // 알람 메니저
    private GregorianCalendar mCalendar; // 설정 일시
    private DatePicker mDate; // 일자 설정 클래스
    private TimePicker mTime; // 시작 설정 클래스
    private NotificationManager mNotification; // 통지 관련 멤버 변수
    Button set;
    Button reset;

    static long result;
    static String setting;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNotification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE); // 통지 매니저를 취득
        mManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE); // 알람 매니저를 취득
        mCalendar = new GregorianCalendar(); // 현재 시각을 취득
        Log.i("HelloAlarmActivity", mCalendar.getTime().toString());

        //셋 버튼, 리셋버튼의 리스너를 등록
        setContentView(R.layout.activity_alarm);
        set = (Button) findViewById(R.id.set);
        set.setOnClickListener(this);
        reset = (Button) findViewById(R.id.reset);
        reset.setOnClickListener(this);
        //일시 설정 클래스로 현재 시각을 설정
        mDate = (DatePicker) findViewById(R.id.date_picker);
        mDate.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
        mTime = (TimePicker) findViewById(R.id.time_picker);
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTime.setOnTimeChangedListener(this);
    }

    //알람의 설정
    //mCalendar에서 지정된 시간(우리가 지정한 시간)을 가져와 알람매니저에 넘겨 지정된 시간에 실행을 시키는 것이고 pendingIntent()는 실행될 코드.
    private long setAlarm() {
        setting = mCalendar.getTime().toString();
        return mCalendar.getTimeInMillis();
    }

    //알람의 해제
    public void resetAlarm() {
        finish();
        Intent intent = new Intent(AlarmActivity.this, AlarmActivity.class);
        startActivity(intent);
        //mManager.cancel(pendingIntent());
    }

    //일자 설정 클래스의 상태변화 리스너
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set(year, monthOfYear, dayOfMonth);
        logManager.logPrint("year : " + year + "month : " + monthOfYear + "day : " + dayOfMonth);
        Date date = mCalendar.getTime();
        System.out.println("date = " + date);
    }

    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        mCalendar.set(mDate.getYear(), mDate.getMonth(), mDate.getDayOfMonth(), hourOfDay, minute);
        logManager.logPrint("hourOfDay : " + hourOfDay + "minute : " +minute);
        Log.i("timechange", mCalendar.getTime().toString());
        Date time = mCalendar.getTime();
        System.out.println("time = " + time);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v == set) {
            result = setAlarm();
            logManager.logPrint("setting : " + setting);

            finish();
        }
        if (v == reset) {
            resetAlarm();
        }
    }
}