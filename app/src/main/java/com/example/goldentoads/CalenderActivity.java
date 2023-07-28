package com.example.goldentoads;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CalenderActivity extends AppCompatActivity{

    TextView textViewMonth;
    ImageButton imageButtonBefore, imageButtonNext;

    LocalDate selectedDate;
    String TAG;
    RecyclerView dayRecyclerView;

    CalendarAdapter calendarAdapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        TAG = "캘린더 액티비티";

        textViewMonth = findViewById(R.id.textViewMonth);
        imageButtonBefore = findViewById(R.id.imageButtonBefore);
        imageButtonNext = findViewById(R.id.imageButtonNext);
        dayRecyclerView = findViewById(R.id.dayRecyclerView);

        selectedDate = LocalDate.now();

        setMonthView();




        imageButtonBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               //현재 월-1 변수
                selectedDate =  selectedDate.minusMonths(1);
                setMonthView();

            }
        });

        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedDate = selectedDate.plusMonths(1);
                setMonthView();

            }
        });



    }

    private void setMonthView(){

        textViewMonth.setText(monthYearFromDate(selectedDate));

        ArrayList<LocalDate> dayList = dayInMonthArray(selectedDate);

        calendarAdapter = new CalendarAdapter(dayList);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);

        dayRecyclerView.setLayoutManager(layoutManager);

        dayRecyclerView.setAdapter(calendarAdapter);
    }


    private String monthYearFromDate(LocalDate date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월");

        return date.format(formatter);
    }


    private ArrayList<LocalDate> dayInMonthArray(LocalDate date){

        ArrayList<LocalDate> dayList = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(date);

        //해당 월의 마지막 날짜 가져오기 (예 : 28, 30, 31)
        int lastDay = yearMonth.lengthOfMonth();

        //해당 월의 첫번째 날짜 가져오기
        LocalDate firstDay = selectedDate.withDayOfMonth(1);

        //첫번째 날짜의 요일 가져오기
        int dayOfWeek = firstDay.getDayOfWeek().getValue();

        for (int i = 1; i < 42 ; i++) {

            if(i<=dayOfWeek || i > lastDay+dayOfWeek){
                dayList.add(null);
            }else {
                dayList.add(LocalDate.of(selectedDate.getYear(),selectedDate.getMonth(),i-dayOfWeek));
            }
        }

        return dayList;
    }



}