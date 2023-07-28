package com.example.goldentoads;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
    String TAG = "캘린더 어댑터";
    ArrayList<LocalDate> dayList;
    LocalDate day;
    ArrayList<HolidayDate> holidayArrayList;
    View selectedView;

    int incomeSum, spendSum;
    String selectedDate, formattedIncomeSum, formattedSpendSum;
    private final static String secretKey = "1SNyHpr5m1VDGsbD7OtePlj5Dxdmo9cG4C%2FLqL3V39j7YVQoh5YfzGJMVH0pwTh4pZW6bGPZQ0POdJuI%2B38LIQ%3D%3D";
    public  CalendarAdapter(){

    }
    public  CalendarAdapter(ArrayList<LocalDate> dayList){
        this.dayList = dayList;
    }

    public void setArrayList(ArrayList<LocalDate> dayList) {
        this.dayList = dayList;
    }

    public ArrayList<HolidayDate> getHolidayArrayList() {
        return holidayArrayList;
    }

    public void setHolidayArrayList(ArrayList<HolidayDate> holidayArrayList) {
        this.holidayArrayList = holidayArrayList;
    }

    //리스트의 각 항목을 이루는 디자인(xml)을 적용.
    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calender_cell, parent, false);

        return new CalendarViewHolder(view);

    }



    //리스트에 각 항목에 들어갈 데이터를 지정
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {


        day = dayList.get(position);
        Log.d(TAG, "day : "+day);

        int selectedYear = 0;
        int selectedMonth = 0;
        int selectedDay = 0;

        if (day != null) {

            selectedYear = day.getYear();
            selectedMonth = day.getMonthValue();
            selectedDay = day.getDayOfMonth();
        }


        selectedDate = String.format("%d년 %02d월 %02d일",selectedYear,selectedMonth,selectedDay);
        Log.d(TAG, "selectedDate : "+selectedDate);
        incomeSum = PreferenceManager.getInt(selectedView.getContext(), selectedDate+""+"incomeSum");
        Log.d(TAG, "키값 : "+selectedDate+""+"incomeSum");
        spendSum = PreferenceManager.getInt(selectedView.getContext(), selectedDate+""+"spendSum");
        Log.d(TAG, "키값 : "+selectedDate+""+"spendSum");




        DecimalFormat decimalFormat = new DecimalFormat("#,###원");
        formattedIncomeSum = decimalFormat.format(incomeSum);
        formattedSpendSum = decimalFormat.format(spendSum);


        holder.textViewIncome.setText("+"+formattedIncomeSum);
        holder.textViewSpend.setText("-"+formattedSpendSum);

        //날짜 색 지정
        if ((position+1) % 7 == 0 ) { // 토요일 파랑
            holder.textViewDay.setTextColor(Color.BLUE);
        } else if (position == 0 || position % 7 == 0) { // 일요일 빨강

            holder.textViewDay.setTextColor(Color.RED);

        }

        if(day == null){
            holder.textViewDay.setText("");
            holder.textViewSpend.setText("");
            holder.textViewIncome.setText("");
        }else {
            //해당일자를 넣는다.
            holder.textViewDay.setText(String.valueOf(day.getDayOfMonth()));

            if (holidayArrayList != null) {

                for (int i = 0; i < holidayArrayList.size(); i++) {

                    if (holidayArrayList.get(i).getLocdate() != null) {

                        String formattedLocDate = locdateFormat(holidayArrayList.get(i).getLocdate());


                        Log.d(TAG, "formattedLocDate : "+formattedLocDate);
                        Log.d(TAG, "selectedDate : "+selectedDate);


                        if (formattedLocDate.equals(selectedDate)) {

                            holder.textViewDay.setText((day.getDayOfMonth())+"\n"+holidayArrayList.get(i).getDateName());
                            holder.textViewDay.setTextSize(12f);
                            holder.textViewDay.setTextColor(Color.RED);

                        }


                    }


                }
            }
            //오늘 날짜 색상 칠하기
            if(day.equals(LocalDate.now())){
                holder.textViewDay.setTextColor(Color.parseColor("#8ACC3E"));
            }

            //2023년 공휴일 빨간색
            holidaySet(selectedYear,selectedMonth,selectedDay,holder.textViewDay,holder.textViewHoliday);

        }


    }


    //화면에 보여줄 데이터의 갯수를 반환.
    @Override
    public int getItemCount() {

        return dayList.size();
    }

    //아이템 클릭 리스너 인터페이스
    interface OnItemClickListener{
        void onItemClick(View v, int position); //뷰와 포지션값

    }
    private OnItemClickListener listener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;


    }
    //뷰홀더 객체에 저장되어 화면에 표시되고, 필요에 따라 생성 또는 재활용 된다.
    public class CalendarViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDay, textViewIncome, textViewSpend, textViewHoliday;
        View parentView;

        public CalendarViewHolder(@NonNull View itemView){
            super(itemView);

            this.textViewDay = itemView.findViewById(R.id.textViewDay);
            this.textViewHoliday = itemView.findViewById(R.id.textViewHoliday);
            this.parentView = itemView.findViewById(R.id.parentView);
            this.textViewIncome = itemView.findViewById(R.id.textViewIncome);
            this.textViewSpend = itemView.findViewById(R.id.textViewSpend);
            selectedView = itemView.findViewById(R.id.parentView);



              itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                      Log.d(TAG, "아이템뷰 클릭");
                          int position = getAdapterPosition();
                      Log.d(TAG, "아이템뷰 position : "+position);
                          if(position!=RecyclerView.NO_POSITION){
                              if(listener!=null){
                                  Log.d(TAG, "listner : "+listener);
                                  Log.d(TAG, "v : "+v);
                                  listener.onItemClick(v,position);

                              }
                          }


                  }
              });

        }
    }

//    public void holidayInfoAPI(String year, String month) throws IOException, JSONException {
//
//        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"); /*URL*/
//        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + secretKey); /*Service Key*/
//        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
//        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
//        urlBuilder.append("&" + URLEncoder.encode("solYear", "UTF-8") + "=" + URLEncoder.encode(year, "UTF-8")); /*연 */
//        urlBuilder.append("&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + URLEncoder.encode(month, "UTF-8")); /*월*/
//        urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /* json으로 요청 */
//
//        URL url = new URL(urlBuilder.toString());
//        System.out.println("요청URL = " + urlBuilder);
//
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//        conn.setRequestMethod("GET");
//        conn.setRequestProperty("Content-type", "application/json");
//
//        BufferedReader rd;
//        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
//            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//        } else {
//            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
//        }
//        StringBuilder sb = new StringBuilder();
//        String line;
//        while ((line = rd.readLine()) != null) {
//            sb.append(line);
//        }
//        rd.close();
//        conn.disconnect();
//
//
//        JSONObject jObject = new JSONObject(sb.toString());
//        JSONObject parseResponse = (JSONObject) jObject.get("response");
//        JSONObject parseBody = (JSONObject) parseResponse.get("body");
//        int totalCount = (int) parseBody.get("totalCount");
//
//
//
//        if (totalCount == 0) {
//
//            Log.d(TAG, "해당월에 공휴일이 없습니다.");
//
//        } else if (totalCount == 1) {
//            JSONObject parseItemsObject = (JSONObject) parseBody.get("items");
//            JSONObject parseItemObject = (JSONObject) parseItemsObject.get("item");
//
//            String dateName = parseItemObject.getString("dateName");
//            Log.d(TAG, "dateName : "+dateName);
//            String locdate= parseItemObject.getString("locdate");
//            Log.d(TAG, "locdate : "+locdate);
//
//            holidayDate = new HolidayDate(dateName, locdate);
//            holidayArrayList = new ArrayList<>();
//            holidayArrayList.add(holidayDate);
//
//
//        } else if (totalCount >= 2) {
//            JSONObject parseItemsObject = (JSONObject) parseBody.get("items");
//            JSONArray parseItem = (JSONArray) parseItemsObject.get("item");
//
//            holidayArrayList = new ArrayList<>();
//
//            for (int i = 0; i < parseItem.length(); i++) {
//
//                JSONObject object = parseItem.getJSONObject(i);
//                String dateName = object.getString("dateName");
//                String locdate= object.getString("locdate");
//
//                holidayDate = new HolidayDate(dateName, locdate);
//                holidayArrayList.add(holidayDate);
//            }
//        }
//
//    }

    public String locdateFormat (String locdate){

        String year = locdate.substring(0,4);
        String month = locdate.substring(4,6);
        String day = locdate.substring(6,8);


        return year+"년 "+month+"월 "+day+"일";

    }

    public void holidaySet (int selectedYear,int selectedMonth, int selectedDay, TextView textViewDay,TextView textViewHoliday){
        if (selectedYear == 2022) {
            if (selectedMonth == 12) {
                if (selectedDay == 25) {
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("성탄절");
                }
            }
        }


        if(selectedYear==2023){
            if(selectedMonth==1){
                if(selectedDay==1){
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("신정");
                } else if (selectedDay==21||selectedDay==22||selectedDay==23) {
                    textViewDay.setTextColor(Color.RED);
                   textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("설날");
                } else if (selectedDay == 24) {
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("대체공휴일");
                }
            } else if (selectedMonth==3) {
                if (selectedDay == 1) {
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("삼일절");
                }
                
            } else if (selectedMonth==5) {
                if (selectedDay == 5) {
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("어린이날");
                }else if (selectedDay == 27) {
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("석가탄신일");
                }
            } else if(selectedMonth==6){
                if(selectedDay==6){
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("현충일");
                }
            } else if (selectedMonth==8) {
                if(selectedDay==15){
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("광복절");
                }

            }else if (selectedMonth==9) {
                if(selectedDay==28||selectedDay==29||selectedDay==30){
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("추석");
                }

            }else if (selectedMonth==10) {
                if(selectedDay==3){
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("개천절");
                }else if(selectedDay==9){
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("한글날");
                }

            }else if (selectedMonth==12) {
                if(selectedDay==25){
                    textViewDay.setTextColor(Color.RED);
                    textViewHoliday.setVisibility(View.VISIBLE);
                    textViewHoliday.setText("성탄절");
                }

            }

        }

    }


}
