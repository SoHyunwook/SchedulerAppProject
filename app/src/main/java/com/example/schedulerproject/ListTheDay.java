package com.example.schedulerproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by 현욱 on 2016-02-03.
 */

public class ListTheDay extends Activity {
    LogManager logManager;
    ListView listView;
    Cursor c;
    ListsAdapter adapter;
    MyHelper helper;
    SQLiteDatabase db;
    ArrayList<Lists> data = null;
    private TextView selectedDay;
    private MainActivity using = new MainActivity();

    int tmp = 0;
    int tmp2;
    boolean flag = false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.theday_list);

        helper = new MyHelper(this, "myDB.db", null, 1);

        listView = (ListView)findViewById(R.id.listView);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListTheDay.this, EditDay.class);
                startActivity(intent);
            }
        });

        selectedDay = (TextView)this.findViewById(R.id.selectedday);
        selectedDay.setText(using.imsi);

//=============실제데이터를 읽어옴==================================================================
        db = helper.getReadableDatabase();
        c = db.query("lists", null, null, null, null, null, null);
        data = new ArrayList<Lists>();
        Lists lists;
        while(c.moveToNext()) {
            lists = new Lists();
            lists._id = c.getInt(0);
            lists.toDo = c.getString(1);
            lists.dDay = c.getString(3);
            lists.theDay = c.getString(5);
            tmp++;
            if(selectedDay.getText().toString().equals(c.getString(5))) {
                if(flag == false) {
                    flag = true;
                    tmp2 = tmp - 1;
                }
                data.add(lists);
            }
        }
//=================================================================================================
        adapter = new ListsAdapter(this, data, R.layout.item);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(listener);
        registerForContextMenu(listView);
    }

    AdapterView.OnItemClickListener listener= new AdapterView.OnItemClickListener() {
       @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
           Intent intent = new Intent(ListTheDay.this, DoSpecific.class);
           c.moveToPosition(position + tmp2);
           intent.putExtra("toDo", c.getString(1));
           intent.putExtra("memo", c.getString(2));
           intent.putExtra("dDay", c.getString(3));
           intent.putExtra("settingAlarm", c.getString(4));
           intent.putExtra("theDay", c.getString(5));
           intent.putExtra("position", c.getPosition());
           c.close();
           db.close();
           helper.close();
           startActivity(intent);
           finish();
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_main1, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int index = info.position;
        switch( item.getItemId() ){
            case R.id.action_settings1:
                clickEdit(index);
                break;
            case R.id.action_settings2:
                clickDel(index);
                break;
        }
        return true;
    }

    void clickDel(final int index) {
        AlertDialog.Builder alert_confirm = new AlertDialog.Builder(ListTheDay.this);
        alert_confirm.setMessage("일정을 삭제하시겠습니까?").setCancelable(false).setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 'YES'
                        c.moveToPosition(index + tmp2);
                        String toDo = c.getString(1).toString();
                        String theDay = c.getString(5).toString();
                        helper.onOpen(db);
                        helper.delete("delete from lists where toDo = '" + toDo + "' and theDay = '" + theDay + "';");
                        Toast.makeText(getApplicationContext(), "선택하신 일정이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        c.close();
                        db.close();
                        helper.close();
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

    void clickEdit(final int index) {
        Intent intent = new Intent(ListTheDay.this, EditDay.class);
        c.moveToPosition(index + tmp2);
        intent.putExtra("toDo", c.getString(1));
        intent.putExtra("memo", c.getString(2));
        intent.putExtra("dDay", c.getString(3));
        intent.putExtra("settingAlarm", c.getString(4));
        intent.putExtra("theDay", c.getString(5));
        intent.putExtra("position", c.getPosition());
        startActivity(intent);
        finish();
    }
}

