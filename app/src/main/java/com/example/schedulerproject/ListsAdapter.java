package com.example.schedulerproject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by 현욱 on 2016-02-03.
 */
public class ListsAdapter extends BaseAdapter {
    Context context;
    int layout;
//    실데이터 관리
    ArrayList<Lists> list;

    public ListsAdapter(Context context, ArrayList<Lists> list, int layout) {
        this.context = context;
        this.layout = layout;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = View.inflate(context, layout, null);
        }
        Lists lists = list.get(position);
        TextView tv = (TextView)convertView.findViewById(R.id.textView2);
        TextView tv1 = (TextView)convertView.findViewById(R.id.textView7);
        tv.setText(lists.toDo);
        tv1.setText(lists.theDay);
        return convertView;
    }
}
