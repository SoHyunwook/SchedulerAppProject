package com.example.schedulerproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by 현욱 on 2016-02-15.
 */
public class DoSpecific extends AppCompatActivity {
    LogManager logManager;
    TextView tv3, tv4, tv5, tv8, tv6; //날짜, 할일, 메모, 알림, 디데이
    MyHelper helper;
    SQLiteDatabase db;
    ListsAdapter adapter;
    Cursor c;
    int position;

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

    View.OnClickListener handler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.button :
                    deleteButton();
                    break;
                case R.id.button2 :
                    editButton();
                    break;
            }
        }
    };

    void deleteButton() {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(DoSpecific.this);
        alert_confirm.setMessage("일정을 삭제하시겠습니까?").setCancelable(false).setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'YES'
                        String toDo = tv4.getText().toString();
                        String theDay = tv3.getText().toString();
                        helper.onOpen(db);
                        helper.delete("delete from lists where toDo = '" + toDo + "' and theDay = '" + theDay + "';");
                        Toast.makeText(getApplicationContext(), "선택하신 일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'No'
                        return;
                    }
                });
        AlertDialog alert = alert_confirm.create();
        alert.show();
    }

    void editButton() {
        Intent intent = new Intent(DoSpecific.this, EditDay.class);
        intent.putExtra("toDo", tv4.getText().toString());
        intent.putExtra("memo", tv5.getText().toString());
        intent.putExtra("theDay", tv3.getText().toString());
        intent.putExtra("position", position);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.specific_do);

        helper = new MyHelper(this, "myDB.db", null, 1);
        db = helper.getReadableDatabase();
        helper.onOpen(db);

        Intent intent = getIntent();
        String toDo = intent.getStringExtra("toDo");
        String memo = intent.getStringExtra("memo");
        String theDay = intent.getStringExtra("theDay");
        position = intent.getIntExtra("position", 100);
        findViewById(R.id.button).setOnClickListener(handler);
        findViewById(R.id.button2).setOnClickListener(handler);

        db = helper.getReadableDatabase();
        c = db.query("lists", null, null, null, null, null, null);

        tv3 = (TextView)findViewById(R.id.textView3);
        tv4 = (TextView)findViewById(R.id.textView4);
        tv5 = (TextView)findViewById(R.id.textView5);
        tv6 = (TextView)findViewById(R.id.textView6);
        tv8 = (TextView)findViewById(R.id.textView8);
        tv3.setText(theDay);
        tv4.setText(toDo);
        tv5.setText(memo);
        c.close();
        db.close();
        helper.close();
    }
}
