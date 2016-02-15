package com.example.schedulerproject;

import java.text.*;
import java.util.*;

import android.annotation.*;
import android.app.*;
import android.app.DatePickerDialog.OnDateSetListener;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
//import android.widget.Toolbar;
import android.content.*;
import android.os.*;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

@TargetApi(3)
public class MainActivity extends AppCompatActivity implements OnClickListener {

    LogManager mlog;

    Date now = new Date();
    static int Year, Month, Day;//dday설정을 위해서 넘겨줘야하는 변수
    Button currentMonth;
    private GridView calendarView;
    static String imsi;//variables(currently clicked days)
    GridCellAdapter adapter;
    private Calendar _calendar;
    private DatePickerDialog mDatePickerDialog = null;
    @SuppressLint("NewApi")
    private int month, year;
    @SuppressWarnings("unused")
    @SuppressLint({ "NewApi", "NewApi", "NewApi", "NewpApi" })
    SimpleDateFormat format1 = new SimpleDateFormat("yyyy/MM/dd");
    SimpleDateFormat format2 = new SimpleDateFormat("yyyy년 MM월");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditDay.class);
                startActivity(intent);
            }
        });

        _calendar = Calendar.getInstance(Locale.getDefault());
        month = _calendar.get(Calendar.MONTH) + 1;
        year = _calendar.get(Calendar.YEAR);


        currentMonth = (Button) this.findViewById(R.id.currentMonth);
        currentMonth.setText(format2.format(_calendar.getTime()));
        currentMonth.setOnClickListener(this);

        calendarView = (GridView) this.findViewById(R.id.gridView);

        // Initialized
        adapter = new GridCellAdapter(getApplicationContext(),
                R.id.calendar_day_gridcell, month, year);
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
//바로 이렇게 해줘도 된다.
        MenuItem item1 = menu.findItem(R.id.all_List);

        return super.onCreateOptionsMenu(menu);
    }
    //이쪽에서 실제 실행이된다.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all_List :
                Intent intent = new Intent(MainActivity.this, EntireList.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setGridCellAdapterToDate(int month, int year) {
        adapter = new GridCellAdapter(getApplicationContext(),
                R.id.calendar_day_gridcell, month, year);
        _calendar.set(year, month - 1, _calendar.get(Calendar.DAY_OF_MONTH));
        currentMonth.setText(format2.format(_calendar.getTime()));
        adapter.notifyDataSetChanged();
        calendarView.setAdapter(adapter);
    }

    public void update() {
        currentMonth.setText(format2.format(_calendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        //clicked currentMonth button
        if(v == currentMonth) {
            OnDateSetListener callBack = new OnDateSetListener()
            {
                @Override
                public void onDateSet( DatePicker View, int year, int monthOfYear, int dayOfMonth )
                {
                    _calendar.set(year, monthOfYear, 1);
                    update();
                    adapter = new GridCellAdapter(getApplicationContext(),
                            R.id.calendar_day_gridcell, monthOfYear + 1, year);
                    _calendar.set(year, monthOfYear, _calendar.get(Calendar.DAY_OF_MONTH));
                    adapter.notifyDataSetChanged();
                    calendarView.setAdapter(adapter);

                }
            };

            mDatePickerDialog = new DatePickerDialog(this, callBack, this.year, this.month - 1, 1);
            mDatePickerDialog.show();

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    // Inner Class
    public class GridCellAdapter extends BaseAdapter implements OnClickListener {
        private static final String tag = "GridCellAdapter";
        private final Context _context;

        private final List<String> list;
        private static final int DAY_OFFSET = 1;
        private final String[] weekdays = new String[] { "일", "월", "화",
                "수", "목", "금", "토" };
        private final String[] months = { "1", "2", "3",
                "4", "5", "6", "7", "8", "9",
                "10", "11", "12" };
        private final int[] daysOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30,
                31, 30, 31 };
        private int daysInMonth;
        private int currentDayOfMonth;
        private int currentWeekDay;
        Button gridcell;
        TextView num_events_per_day;
        private final HashMap<String, Integer> eventsPerMonthMap;
        private final SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");


        // Days in Current Month
        public GridCellAdapter(Context context, int textViewResourceId, int month, int year) {
            super();
            this._context = context;
            this.list = new ArrayList<String>();
            Calendar calendar = Calendar.getInstance();
            setCurrentDayOfMonth(calendar.get(Calendar.DAY_OF_MONTH));
            setCurrentWeekDay(calendar.get(Calendar.DAY_OF_WEEK));

            // Print Month
            printMonth(month, year);

            // Find Number of Events
            eventsPerMonthMap = findNumberOfEventsPerMonth(year, month);
        }

        private String getMonthAsString(int i) {
            return months[i];
        }

        private String getWeekDayAsString(int i) {
            return weekdays[i];
        }

        private int getNumberOfDaysOfMonth(int i) {
            return daysOfMonth[i];
        }

        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * Prints Month
         *
         * @param mm
         * @param yy
         */
        //private
        public void printMonth(int mm, int yy) {
            int trailingSpaces = 0;
            int daysInPrevMonth = 0;
            int prevMonth = 0;
            int prevYear = 0;
            int nextMonth = 0;
            int nextYear = 0;

            int currentMonth = mm - 1;
            String currentMonthName = getMonthAsString(currentMonth);
            daysInMonth = getNumberOfDaysOfMonth(currentMonth);

            GregorianCalendar cal = new GregorianCalendar(yy, currentMonth, 1);
            if (currentMonth == 11) {
                prevMonth = currentMonth - 1;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 0;
                prevYear = yy;
                nextYear = yy + 1;
            } else if (currentMonth == 0) {
                prevMonth = 11;
                prevYear = yy - 1;
                nextYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
                nextMonth = 1;
            } else {
                prevMonth = currentMonth - 1;
                nextMonth = currentMonth + 1;
                nextYear = yy;
                prevYear = yy;
                daysInPrevMonth = getNumberOfDaysOfMonth(prevMonth);
            }

            int currentWeekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            trailingSpaces = currentWeekDay;

            if (cal.isLeapYear(cal.get(Calendar.YEAR)))
                if (mm == 2)
                    ++daysInMonth;
                else if (mm == 3)
                    ++daysInPrevMonth;

            // Trailing Month days
            for (int i = 0; i < trailingSpaces; i++) {
                list.add(String
                        .valueOf((daysInPrevMonth - trailingSpaces + DAY_OFFSET)
                                + i)
                        + "/"
                        + getMonthAsString(prevMonth)
                        + "/"
                        + prevYear);
            }
            // Current Month Days
            for (int i = 1; i <= daysInMonth; i++) {
                list.add(String.valueOf(i)
                        + "/"
                        + getMonthAsString(currentMonth) + "/" + yy);
            }

            // Leading Month days
            for (int i = 0; i < list.size() % 7; i++) {
                list.add(String.valueOf(i + 1) +
                        "/"
                        + getMonthAsString(nextMonth) + "/" + nextYear);
            }


        }

        /**
         * NOTE: YOU NEED TO IMPLEMENT THIS PART Given the YEAR, MONTH, retrieve
         * ALL entries from a SQLite database for that month. Iterate over the
         * List of All entries, and get the dateCreated, which is converted into
         * day.
         *
         * @param year
         * @param month
         * @return
         */
        private HashMap<String, Integer> findNumberOfEventsPerMonth(int year, int month) {
            HashMap<String, Integer> map = new HashMap<String, Integer>();

            return map;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            if (row == null) {
                LayoutInflater inflater = (LayoutInflater) _context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = inflater.inflate(R.layout.calendar_cell, parent, false);
            }

            // Get a reference to the Day gridcell
            gridcell = (Button) row.findViewById(R.id.calendar_day_gridcell);
            gridcell.setOnClickListener(this);

            // ACCOUNT FOR SPACING

            String[] day_color = list.get(position).split("/");
            String theday = day_color[0];
            String themonth = day_color[1];
            String theyear = day_color[2];
            Year = Integer.parseInt(theyear);
            Month = Integer.parseInt(themonth);
            themonth = String.format("%02d", Month);
            Day = Integer.parseInt(theday);
            theday = String.format("%02d", Day);
            if ((!eventsPerMonthMap.isEmpty()) && (eventsPerMonthMap != null)) {
                if (eventsPerMonthMap.containsKey(theday)) {
                    num_events_per_day = (TextView) row
                            .findViewById(R.id.num_events_per_day);
                    Integer numEvents = (Integer) eventsPerMonthMap.get(theday);
                    num_events_per_day.setText(numEvents.toString());
                }
            }

            // Set the Day GridCell
            gridcell.setText(theday);
//            gridcell.setTag(theday + "/" + themonth + "/" + theyear);
            gridcell.setTag(theyear + "/" + themonth + "/" + theday);
            return row;
        }

        @Override
        public void onClick(View view) {
            String date_month_year = (String) view.getTag();
            mlog.logPrint("view.getTag() : " + view.getTag().toString());
            imsi = date_month_year;
            String[] _sendday = date_month_year.split("/");
            Year = Integer.parseInt(_sendday[2]);
            Month = Integer.parseInt(_sendday[1]);
            Day = Integer.parseInt(_sendday[0]);
//            Year = Integer.parseInt(_sendday[0]);
//            Month = Integer.parseInt(_sendday[1]);
//            Day = Integer.parseInt(_sendday[2]);
            try {
                Date parsedDate = format2.parse(date_month_year);
                imsi = date_month_year;
                Intent intent = new Intent(MainActivity.this, ListTheDay.class);
                startActivity(intent);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        public int getCurrentDayOfMonth() {
            return currentDayOfMonth;
        }

        private void setCurrentDayOfMonth(int currentDayOfMonth) {
            this.currentDayOfMonth = currentDayOfMonth;
        }

        public void setCurrentWeekDay(int currentWeekDay) {
            this.currentWeekDay = currentWeekDay;
        }

        public int getCurrentWeekDay() {
            return currentWeekDay;
        }
    }
}

