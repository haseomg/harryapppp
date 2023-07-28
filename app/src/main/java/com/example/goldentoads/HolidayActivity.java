package com.example.goldentoads;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HolidayActivity extends AppCompatActivity {


    private final static String secretKey = "1SNyHpr5m1VDGsbD7OtePlj5Dxdmo9cG4C%2FLqL3V39j7YVQoh5YfzGJMVH0pwTh4pZW6bGPZQ0POdJuI%2B38LIQ%3D%3D";
    TextView textView;
    List<String> holidayDates;
    String holiday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textView = findViewById(R.id.textViewHoliday);

        new Thread(()->{


            try {
               holidayInfoAPI("2023","01"); // network 동작, 인터넷에서 xml을 받아오는 코드
            } catch (IOException | JSONException e) {
                throw new RuntimeException(e);
            }
        }).start();




    }

    public static void holidayInfoAPI(String year, String month) throws IOException, JSONException {

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getHoliDeInfo"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + secretKey); /*Service Key*/
//        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
//        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("solYear", "UTF-8") + "=" + URLEncoder.encode(year, "UTF-8")); /*연 */
        urlBuilder.append("&" + URLEncoder.encode("solMonth", "UTF-8") + "=" + URLEncoder.encode(month, "UTF-8")); /*월*/
        urlBuilder.append("&" + URLEncoder.encode("_type", "UTF-8") + "=" + URLEncoder.encode("json", "UTF-8")); /* json으로 요청 */

        URL url = new URL(urlBuilder.toString());
        System.out.println("요청URL = " + urlBuilder);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        JSONObject jObject = new JSONObject(sb.toString());
        JSONObject parseResponse = (JSONObject) jObject.get("response");
        JSONObject parseBody = (JSONObject) parseResponse.get("body");
        JSONObject parseItems = (JSONObject) parseBody.get("items");
        JSONArray parseItem = (JSONArray) parseItems.get("item");


        for (int i = 0; i < parseItem.length(); i++) {
            JSONObject object = parseItem.getJSONObject(i);
            String dateName = object.getString("dateName");
            String locdate= object.getString("locdate");
            System.out.println("dateName(" + i + "): " + dateName);
            System.out.println("locdate(" + i + "): " + locdate);
            System.out.println();
        }
    }


}