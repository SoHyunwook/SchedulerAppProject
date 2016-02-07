package com.example.schedulerproject;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class EntireList extends AppCompatActivity {
    LogManager logManager;
    ListView listView;
    Cursor c;
    ListsAdapter adapter;
    MyHelper helper;
    SQLiteDatabase db;
    ArrayList<Lists> data = null;
    private TextView selectedDay;
    private MainActivity using = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entire_list);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        helper = new MyHelper(this, "myDB.db", null, 1);

        listView = (ListView)findViewById(R.id.listView2);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.view2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EntireList.this, EditDay.class);
                startActivity(intent);
            }
        });

        selectedDay = (TextView)this.findViewById(R.id.textView);
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
            lists.theDay = c.getString(5);
            data.add(lists);
        }
        c.close();
        db.close();
        helper.close();
//=================================================================================================
        adapter = new ListsAdapter(this, data, R.layout.item);
        listView.setAdapter(adapter);
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
