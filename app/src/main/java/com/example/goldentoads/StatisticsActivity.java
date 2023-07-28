package com.example.goldentoads;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    PieChart pieChart;
    BarChart barChart;

    String TAG = "통계 액티비티";
    String selectedDate;
    ArrayList<Data> statisticsArrayList, filteredListByMonth, filteredListByYear;
    int[] incomeSumByCategory, spendSumByCategory, incomeSumByType, spendSumByType;

    ImageButton imageButtonBack;
    Button buttonYearIncome, buttonYearSpend, buttonMonthIncome, buttonMonthSpend;
    TextView textViewYear, textViewMonth, textViewNoData1, textViewNoData2;

    Boolean buttonYearIsChecked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);

        imageButtonBack = findViewById(R.id.imageButtonBack);
        textViewYear = findViewById(R.id.textViewYear);
        textViewMonth = findViewById(R.id.textViewMonth);
        buttonYearIncome = findViewById(R.id.buttonYearIncome);
        buttonYearSpend = findViewById(R.id.buttonYearSpend);
        buttonMonthIncome = findViewById(R.id.buttonMonthIncome);
        buttonMonthSpend = findViewById(R.id.buttonMonthSpend);
        textViewNoData1 = findViewById(R.id.textViewNoData);
        textViewNoData2 = findViewById(R.id.textViewNoData2);

        buttonYearIsChecked = false;
        buttonYearIncome.setBackgroundResource(R.drawable.left_button_round);
        buttonYearSpend.setBackgroundResource(R.drawable.right_button_round_press);

        statisticsArrayList = new ArrayList<>();
        getDataList(statisticsArrayList);

        Intent intent = getIntent();
        selectedDate = intent.getStringExtra("selectedDate");
        Log.d(TAG, "selectedDate : " + selectedDate);

        textViewYear.setText(selectedDate.substring(0,5));
        textViewMonth.setText(selectedDate.substring(0,9));


        filteredListByYear = filterYearByData(selectedDate);
        typeSum(filteredListByYear);
        filteredListByMonth = filterMonthByData(selectedDate);
        categorySum(filteredListByMonth);

        setBarChart(1);
        setPieChart(1);

        buttonMonthIncome.setBackgroundResource(R.drawable.left_button_round);
        buttonMonthSpend.setBackgroundResource(R.drawable.right_button_round_press);



        buttonYearIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonYearIsChecked = true;
                setBarChart(0);
                buttonYearIncome.setBackgroundResource(R.drawable.left_button_round_press);
                buttonYearSpend.setBackgroundResource(R.drawable.right_button_round);
                int sum =0;
                for (int i = 0; i <incomeSumByType.length; i++) {
                    sum = sum + incomeSumByType[i];
                }
                Log.d(TAG, "sum : "+sum);
                if (sum == 0) {
                    textViewNoData1.setVisibility(View.VISIBLE);
                }else{
                    textViewNoData1.setVisibility(View.INVISIBLE);
                }

            }
        });
        buttonYearSpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonYearIsChecked = false;
                setBarChart(1);
                buttonYearIncome.setBackgroundResource(R.drawable.left_button_round);
                buttonYearSpend.setBackgroundResource(R.drawable.right_button_round_press);

                int sum =0;
                for (int i = 0; i <spendSumByType.length; i++) {
                    sum = sum + spendSumByType[i];
                }
                if (sum == 0) {
                    textViewNoData1.setVisibility(View.VISIBLE);
                }else{
                    textViewNoData1.setVisibility(View.INVISIBLE);
                }
            }
        });

        buttonMonthIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPieChart(0);
                buttonMonthIncome.setBackgroundResource(R.drawable.left_button_round_press);
                buttonMonthSpend.setBackgroundResource(R.drawable.right_button_round);

                int sum =0;
                for (int i = 0; i <incomeSumByCategory.length; i++) {
                    sum = sum + incomeSumByCategory[i];
                }
                if (sum == 0) {
                    textViewNoData2.setVisibility(View.VISIBLE);
                }else{
                    textViewNoData2.setVisibility(View.INVISIBLE);
                }

            }
        });
        buttonMonthSpend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPieChart(1);
                buttonMonthIncome.setBackgroundResource(R.drawable.left_button_round);
                buttonMonthSpend.setBackgroundResource(R.drawable.right_button_round_press);

                int sum =0;
                for (int i = 0; i <spendSumByCategory.length; i++) {
                    sum = sum + spendSumByCategory[i];
                }
                if (sum == 0) {
                    textViewNoData2.setVisibility(View.VISIBLE);
                }else{
                    textViewNoData2.setVisibility(View.INVISIBLE);
                }
            }
        });





        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

                int selectedMonth = (int) e.getX()+1;
                Log.d(TAG, "눌른 값 : "+selectedMonth);
                String replaceMonth = String.format("%02d",selectedMonth);

               selectedDate = selectedDate.replace(selectedDate.substring(6,8),replaceMonth);
                textViewMonth.setText(selectedDate.substring(0,9));

                Log.d(TAG, "selectedDate : "+selectedDate);
                filteredListByMonth = filterMonthByData(selectedDate);
                categorySum(filteredListByMonth);
                if (buttonYearIsChecked) {
                    setPieChart(0);
                    buttonMonthIncome.setBackgroundResource(R.drawable.left_button_round_press);
                    buttonMonthSpend.setBackgroundResource(R.drawable.right_button_round);
                    int sum =0;
                    for (int i = 0; i <incomeSumByCategory.length; i++) {
                        sum = sum + incomeSumByCategory[i];
                    }
                    if (sum == 0) {
                        textViewNoData2.setVisibility(View.VISIBLE);
                    }else{
                        textViewNoData2.setVisibility(View.INVISIBLE);
                    }

                }else{
                    setPieChart(1);
                    buttonMonthIncome.setBackgroundResource(R.drawable.left_button_round);
                    buttonMonthSpend.setBackgroundResource(R.drawable.right_button_round_press);
                    int sum =0;
                    for (int i = 0; i <spendSumByCategory.length; i++) {
                        sum = sum + spendSumByCategory[i];
                    }
                    if (sum == 0) {
                        textViewNoData2.setVisibility(View.VISIBLE);
                    }else{
                        textViewNoData2.setVisibility(View.INVISIBLE);
                    }
                }


            }

            @Override
            public void onNothingSelected() {


            }
        });


        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                startActivity(intent);
            }
        });


    }

    int getDataSize() { // 저장된 멤버 아이템의 개수
        int i = 0;

        while (getData(i) != null) {
            i++;
        }
        return i;
    }

    //이부분 고쳐야댐!!
    Data getData(int index) { // 특정 index 의 멤버 정보 가져오기

        String key = String.format(Locale.KOREA, "%s%02d", "DATA", index);
        String value = PreferenceManager.getString(getApplicationContext(), key); // 해당 키의 데이터 가져오기
        if (value == null) {
            return null; // 키에 대한 데이터가 null 이면 null 리턴
        } else {

            String[] saveData = value.split("-");
            if (saveData.length == 7) {

                String userID = saveData[0];
                String Date = saveData[1];
                String Item = saveData[2];
                String Price = saveData[3];
                int typePosition = Integer.parseInt(saveData[4]);
                int categoryPosition = Integer.parseInt(saveData[5]);
                Uri uri = Uri.parse(saveData[6]);


                return new Data(key, userID, Date, Item, Price, typePosition, categoryPosition, uri);
            } else if (saveData.length >= 6) {

                String userID = saveData[0];
                String Date = saveData[1];
                String Item = saveData[2];
                String Price = saveData[3];
                int typePosition = Integer.parseInt(saveData[4]);
                int categoryPosition = Integer.parseInt(saveData[5]);

                return new Data(key, userID, Date, Item, Price, typePosition, categoryPosition);

            } else {
                return null;
            }
        }
    }

    void getDataList(ArrayList<Data> arrayList) { // 저장된 모든 멤버 추가하기


        for (int i = 0; i < getDataSize(); i++) {

            arrayList.add(getData(i));
        }

    }

    private ArrayList<Data> filterMonthByData(String selectedDate) {

        ArrayList<Data> filteredList = new ArrayList<>();

        for (Data data : statisticsArrayList) {

            if (isSameDate(data.getDate().substring(0, 9), selectedDate.substring(0, 9))) {

                filteredList.add(data);

            }


        }
        return filteredList;
    }

    private ArrayList<Data> filterYearByData(String selectedDate){
        ArrayList<Data> filteredList = new ArrayList<>();

        for (Data data : statisticsArrayList) {

            if (isSameDate(data.getDate().substring(0, 5), selectedDate.substring(0, 5))) {

                filteredList.add(data);
            }


        }
        return filteredList;
    }

    private boolean isSameDate(String date1, String date2) {

        Log.d(TAG, "date1 값 : " + date1);
        Log.d(TAG, "date2 값 : " + date2);

        if (date1 == null || date2 == null) {
            return false;
        }

        if (date1.equals(date2)) {
            return true;
        }

        return false;
    }

    private void categorySum(ArrayList<Data> arrayList) { // 월별 >> 항목별 >> 카테고리별 합계

        incomeSumByCategory = new int[5];
        spendSumByCategory = new int[8];

        for (int i = 0; i < arrayList.size(); i++) {

            if (arrayList.get(i).getTypePosition() == 0) {
                for (int j = 0; j < 5; j++) {
                    if (arrayList.get(i).getCategoryPosition() == j) {
                       incomeSumByCategory[j] = incomeSumByCategory[j] + Integer.parseInt(arrayList.get(i).getPrice());
                    }
                }
            } else if (arrayList.get(i).getTypePosition() == 1) {
                for (int j = 0; j < 8; j++) {
                    if (arrayList.get(i).getCategoryPosition() == j) {
                        spendSumByCategory[j] = spendSumByCategory[j] + Integer.parseInt(arrayList.get(i).getPrice());
                    }
                }
            }

        }
        Log.d(TAG, "incomeSumByCategory : "+ Arrays.toString(incomeSumByCategory));
        Log.d(TAG, "spendSumByCategory : "+ Arrays.toString(spendSumByCategory));
    }

    private void typeSum(ArrayList<Data> arrayList) { // 연별 >> 타입별 합계



        incomeSumByType = new int[12];
        spendSumByType = new int[12];


        for (int i = 0; i < arrayList.size(); i++) {

            if (arrayList.get(i).getTypePosition() == 0) {
                for (int j = 0; j < 12 ; j++) {
                    if (Integer.parseInt(arrayList.get(i).getDate().substring(6,8))-1 == j) {
                            incomeSumByType[j] = incomeSumByType[j] + Integer.parseInt(arrayList.get(i).getPrice());
                    }
            }
            } else if (arrayList.get(i).getTypePosition() == 1) {
                for (int j = 0; j < 12 ; j++) {
                    if (Integer.parseInt(arrayList.get(i).getDate().substring(6,8))-1 == j) {
                        spendSumByType[j] = spendSumByType[j] + Integer.parseInt(arrayList.get(i).getPrice());
                    }
                }
            }

        }
        Log.d(TAG, "incomeSumByType : "+ Arrays.toString(incomeSumByType));
        Log.d(TAG, "spendSumByType : "+ Arrays.toString(spendSumByType));
    }

    private void setPieChart(int typePosition){


        //파이차트 백분율로 나타내기
        pieChart.setUsePercentValues(true);
        //파이차트에 대한 설명 비활성화
        pieChart.getDescription().setEnabled(false);
        //여백설정
        pieChart.setExtraOffsets(5, 10, 5, 5);
        //드래그 감속 마찰계수 설정
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        //가운데에 구멍이 그려지지 않도록 설정
        pieChart.setDrawHoleEnabled(false);
        //구멍부분 색상설정
        pieChart.setHoleColor(Color.WHITE);
        //투명한 원의 반지름 설정
        pieChart.setTransparentCircleRadius(61f);



        if (typePosition == 0) { // 체크된상태 수입



            ArrayList<PieEntry> incomeCategoryEntry = new ArrayList<>();

            incomeCategoryEntry.add(new PieEntry(incomeSumByCategory[0], "월급"));
            incomeCategoryEntry.add(new PieEntry(incomeSumByCategory[1], "용돈"));
            incomeCategoryEntry.add(new PieEntry(incomeSumByCategory[2], "이자"));
            incomeCategoryEntry.add(new PieEntry(incomeSumByCategory[3], "보너스"));
            incomeCategoryEntry.add(new PieEntry(incomeSumByCategory[4], "기타"));

            for (int i = 0; i <incomeCategoryEntry.size(); i++) {
                if (incomeCategoryEntry.get(i).getValue() == 0 ) {
                    incomeCategoryEntry.remove(i);
                }
            }

            PieDataSet dataSet = new PieDataSet(incomeCategoryEntry,"");

            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            dataSet.setValueFormatter(new CustomValueFormatter());

            PieData data = new PieData(dataSet);

            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);

            //회전 및 터치 효과 사라짐
            pieChart.invalidate();
            pieChart.setTouchEnabled(false);
            Legend legend = pieChart.getLegend();

            legend.setEnabled(false);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setData(data);
            pieChart.animateY(2000);
            if (incomeSumByCategory == null) {

            }


        } else if (typePosition == 1) { // 체크안된상태 지출



            ArrayList<PieEntry> spendCategoryEntry = new ArrayList<>();


            spendCategoryEntry.add(new PieEntry(spendSumByCategory[0], "식비"));
            spendCategoryEntry.add(new PieEntry(spendSumByCategory[1], "의류미용비"));
            spendCategoryEntry.add(new PieEntry(spendSumByCategory[2], "주거비"));
            spendCategoryEntry.add(new PieEntry(spendSumByCategory[3], "생활용품비"));
            spendCategoryEntry.add(new PieEntry(spendSumByCategory[4], "병원의료비"));
            spendCategoryEntry.add(new PieEntry(spendSumByCategory[5], "교통비"));
            spendCategoryEntry.add(new PieEntry(spendSumByCategory[6], "저축"));
            spendCategoryEntry.add(new PieEntry(spendSumByCategory[7], "기타"));

            for (int i = 0; i <spendCategoryEntry.size(); i++) {
                if (spendCategoryEntry.get(i).getValue() == 0 ) {
                    spendCategoryEntry.remove(i);
                }
            }

            PieDataSet dataSet = new PieDataSet(spendCategoryEntry,"");

            dataSet.setSliceSpace(3f);
            dataSet.setSelectionShift(5f);
            dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            dataSet.setValueFormatter(new CustomValueFormatter());

            PieData data = new PieData(dataSet);

            data.setValueTextSize(10f);
            data.setValueTextColor(Color.BLACK);

            //회전 및 터치 효과 사라짐
            pieChart.invalidate();
            pieChart.setTouchEnabled(false);
            Legend legend = pieChart.getLegend();

            legend.setEnabled(false);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setData(data);
            pieChart.animateY(2000);
        }


    }

    private void setBarChart(int typePosition){



        if (typePosition == 0) {


            ArrayList<BarEntry> incomeSumForYear = new ArrayList<>();

            for (int i = 0; i < 12; i++) {
                incomeSumForYear.add(new BarEntry(i,incomeSumByType[i]));
            }

            BarDataSet barDataSet = new BarDataSet(incomeSumForYear,"수입");
            barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(10f);

            XAxis xAxis = barChart.getXAxis();

            ArrayList<String> xAxisValues = new ArrayList<>();

            for (int i = 0; i < 12; i++) {
                xAxisValues.add(i+1+"월");
            }

            xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
            xAxis.setLabelCount(12);



            BarData barData = new BarData(barDataSet);

            barChart.setFitBars(true);
            barChart.setData(barData);
            barChart.getAxisLeft().setEnabled(false);
            barChart.getAxisRight().setEnabled(false);
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.animateY(2000);

        } else if (typePosition == 1) {


            ArrayList<BarEntry> spendSumForYear = new ArrayList<>();

            for (int i = 0; i < 12; i++) {
                spendSumForYear.add(new BarEntry(i,spendSumByType[i]));
            }

            BarDataSet barDataSet = new BarDataSet(spendSumForYear,"수입");
            barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            barDataSet.setValueTextColor(Color.BLACK);
            barDataSet.setValueTextSize(10f);

            XAxis xAxis = barChart.getXAxis();

            ArrayList<String> xAxisValues = new ArrayList<>();

            for (int i = 0; i < 12; i++) {
                xAxisValues.add(i+1+"월");
            }

            xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));
            xAxis.setLabelCount(12);



            BarData barData = new BarData(barDataSet);

            barChart.setFitBars(true);
            barChart.setData(barData);
            barChart.getAxisLeft().setEnabled(false);
            barChart.getAxisRight().setEnabled(false);
            barChart.getXAxis().setDrawGridLines(false);
            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getLegend().setEnabled(false);
            barChart.animateY(2000);
        }


    }
}