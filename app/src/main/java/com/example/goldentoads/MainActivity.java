package com.example.goldentoads;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import org.jetbrains.annotations.NotNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MainActivity extends AppCompatActivity {


    private final static String secretKey = "PujVFkIqWl4QqiMj%2BrH%2BMuTBeNfBMXnltxkvOiYnuTKWcqZfjLatceDGlbCoQUs0mwaMTJupMgX5UjoAmAoqpQ%3D%3D";

    String imageFilePath, item, price, selectedDate, userID, userPW;
    int typePosition, categoryPosition, incomeSum, spendSum;
    TextView textViewSelectedDate, textView3, textViewMonth,tv_empty;
    RecyclerView mRecyclerView, dayRecyclerView;
    Adapter mAdapter;
    CalendarAdapter calendarAdapter;
    Context mContext;
    ArrayList<Data> mArrayList, filteredArrayList;
    HolidayDate holidayDate;
    ArrayList<HolidayDate> holidayArrayList;
    ImageButton btn_add, imageButtonBefore, imageButtonNext, imageButtonPieChart;
    ImageView imageViewImage;
    Uri uri;
    Data data;
    final String TAG = "메인 액티비티";

    LocalDate selectedLocalDate;


    private static final int REQUEST_PERMISSION_CODE = 123;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_MEDIA_IMAGES,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };

    public void checkPermission(Context context){

        int WRITE_PERMISSION = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int READ_PERMISSION = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES);
        int CAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (READ_PERMISSION != PackageManager.PERMISSION_GRANTED || CAMERA != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "READ_PERMISSION : "+READ_PERMISSION);
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE);
            Log.d(TAG, "넌 동작함?");


        }else {
            Log.d(TAG, "권한이 이미 허용되어있습니다.");
        }


    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "권한 허용");
            } else {
                Log.d(TAG, "권한 거부");

//                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
//                    // 사용자에게 권한이 필요한 이유를 설명하는 다이얼로그 등을 표시하고,
//                    // 다시 권한을 요청할 수 있는 로직을 구현
//                    ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE);
//                } else {
//                    // 사용자가 "다시 묻지 않음"을 선택한 경우이므로,
//                    // 앱 설정 화면으로 이동하여 사용자가 직접 권한을 활성화할 수 있도록 안내
////                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
////                    Uri uri = Uri.fromParts("package", getPackageName(), null);
////                    Log.d(TAG, "패키지 이름 : "+getPackageName());
////                    intent.setData(uri);
////                    startActivity(intent);
//                }
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //메인액티비티를 객체화(실체화) 시켜준다.

        checkPermission(this);



        // xml과 연결
        mContext = getApplicationContext();

        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        textView3 = findViewById(R.id.textViewTitle);
        mRecyclerView = findViewById(R.id.recyclerView);
        btn_add = findViewById(R.id.btn_add);
        tv_empty = findViewById(R.id.tv_empty);
        textViewMonth = findViewById(R.id.textViewMonth);
        imageButtonBefore = findViewById(R.id.imageButtonBefore);
        imageButtonNext = findViewById(R.id.imageButtonNext);
        dayRecyclerView = findViewById(R.id.dayRecyclerView);
        imageButtonPieChart = findViewById(R.id.imageButtonPieChart);


        Intent intent = getIntent();

        userID = intent.getStringExtra("userID");
        userPW = intent.getStringExtra("userPW");

        Log.d(TAG, "userID 값 : "+ userID+"\nuserPW 값 : "+userPW);

        selectedLocalDate = LocalDate.now();
        selectedDate = dayMonthYearFromDate(selectedLocalDate);

        textViewSelectedDate.setText(selectedDate);

        //레이아웃메니저는 리사이클러뷰의 항목 배치를 어떻게 할지 정하고, 스크롤 동작도 정의한다.
        //수평/수직 리스트 LinearLayoutManager
        //그리드 리스트 GridLayoutManager

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        //새로운 어레이리스트 생성
        mArrayList = new ArrayList<>();

        //쉐어드에서 받아온 어레이리스트 연결
        getDataList(mArrayList);

        Log.d(TAG, "mArrayList 크기 : "+mArrayList.size());

        //어레이리스트 오늘날짜로 필터링
        filteredArrayList = filterDatebyData(selectedDate);

        //필터링한 필터드리스트 어댑터에 연결
        mAdapter = new Adapter(mContext, filteredArrayList);

        //어댑터 리사이클러뷰 연결
        mRecyclerView.setAdapter(mAdapter);

        if(filteredArrayList.size()==0){
            tv_empty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        }else {
            tv_empty.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        calendarAdapter = new CalendarAdapter();
        setMonthView();

        Log.d(TAG, "calendarAdapter : "+calendarAdapter);

        calendarAdapter.setOnItemClickListener(new CalendarAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                // 캘린더뷰 눌렀을때 나오는 반응
                LocalDate day = calendarAdapter.dayList.get(position);
                Log.d(TAG, "position : "+position);

                int yearOfDay = 0;
                int monthOfDay = 0;
                int dayOfDay = 0;

                if (day != null) {
                    yearOfDay = day.getYear();
                    monthOfDay = day.getMonthValue();
                    dayOfDay = day.getDayOfMonth();

                    selectedDate = String.format("%d년 %02d월 %02d일",yearOfDay,monthOfDay,dayOfDay);
                    Log.d(TAG, "selectedDate : "+selectedDate);

                }


                if (calendarAdapter.selectedView != v) {
                    calendarAdapter.selectedView.setBackgroundResource(R.color.empty);
                }
                v.setBackgroundResource(R.color.lightyellow);



                calendarAdapter.selectedView = v;
                //다이어리뷰 날짜세팅

                textViewSelectedDate.setText(selectedDate);

                //어레이리스트 내가 선택한 날짜로 필터링
                filteredArrayList = filterDatebyData(selectedDate);

                //어댑터-필터드어레이리스트 연결
                mAdapter.setArrayList(filteredArrayList);

                //리사이클러뷰-어댑터 연결
                mRecyclerView.setAdapter(mAdapter);



                //추가버튼 세팅
                btn_add.setVisibility(View.VISIBLE);

                if (filteredArrayList.size() == 0) {
                    tv_empty.setVisibility(View.VISIBLE);
                    mRecyclerView.setVisibility(View.INVISIBLE);
                } else {
                    tv_empty.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });


        imageButtonBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //현재 월-1 변수
                selectedLocalDate =  selectedLocalDate.minusMonths(1);
                setMonthView();

            }
        });

        imageButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectedLocalDate = selectedLocalDate.plusMonths(1);
                setMonthView();

            }
        });

        imageButtonPieChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent goStatisticsActivityIntent = new Intent(mContext, StatisticsActivity.class);

                goStatisticsActivityIntent.putExtra("selectedDate",selectedDate);
                startActivity(goStatisticsActivityIntent);
            }
        });


        // add 버튼 클릭
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedDate.equals("")){
                    Toast.makeText(mContext, "추가하고 싶은 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                }

                tv_empty.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                btn_add.setVisibility(View.INVISIBLE);

                addItem();


            }

        });

        
        
        mAdapter.setOnItemClickListener(new Adapter.OnItemClickListener() {

            @Override
            public void onItemClick(View v, int position) {

                //상세보기때 인텐트로 넘길 데이터들(data객체를 넒기면 편할듯?)
               Intent intent = new Intent(mContext, DetailViewActivity.class);
               intent.putExtra("position", mArrayList.indexOf(filteredArrayList.get(position)));
               startActivity(intent);

                Log.d(TAG, "클릭한놈 포지션값 :"+ position);
                Log.d(TAG, "데이터 주소값 : "+filteredArrayList.get(position));

            }

            @Override
            public void onEditClick(View v, int position) {

                item = filteredArrayList.get(position).getItem();
                price = filteredArrayList.get(position).getPrice();
                typePosition = filteredArrayList.get(position).getTypePosition();
                categoryPosition = filteredArrayList.get(position).getCategoryPosition();
                uri = filteredArrayList.get(position).getUri();

                Log.d(TAG, "수정시 데이터 주소값 : "+filteredArrayList.get(position));
                Log.d(TAG, "포지션값 : "+position);

//                checkPermission(MainActivity.this);
                editItem(filteredArrayList.get(position),position);




            }

            @Override
            public void onDeleteClick(View v, int position) {

                //지워야될 포지션
                String selectedDate = filteredArrayList.get(position).getDate();
                deleteData(mArrayList, filteredArrayList.get(position));



                Log.d(TAG, "딜리트 중 mArrayList 사이즈 : "+mArrayList.size());
                Log.d(TAG, "mArrayListPosition : "+mArrayList.indexOf(filteredArrayList.get(position)));

                mArrayList.remove(mArrayList.indexOf(filteredArrayList.get(position)));

                filteredArrayList=filterDatebyData(selectedDate);

                mAdapter.setArrayList(filteredArrayList);
                mRecyclerView.setAdapter(mAdapter);

                //메서드로 해당날짜 합계 넘기기
                incomeSum = incomeSum();
                spendSum = spendSum();

                //쉐어드에 합계 저장
                PreferenceManager.setInt(mContext,selectedDate+"incomeSum", incomeSum);
                PreferenceManager.setInt(mContext,selectedDate+"spendSum", spendSum);

                dayRecyclerView.setAdapter(calendarAdapter);

                Log.d(TAG, "딜리트 후 mArrayList 사이즈 : "+mArrayList.size());

//                Log.e(TAG, "mArrayListPosition : "+mArrayList.indexOf(filteredArrayList.get(position)));

                if (mArrayList.size() == 0) {
                    tv_empty.setVisibility(View.VISIBLE);
                }
            }

        });



    }


    void addData(ArrayList<Data> arrayList, Data data){

        int index = arrayList.size()-1;
        String key = String.format(Locale.KOREA, "%s%02d","DATA",index);

        Log.d(TAG, "key값 맨뒤 숫자 : "+index);

        data.setKey(key);
        Log.d(TAG, "추가된 키값 : "+key);

        if(data.getUri()==null) {
            String value =
                            data.getUserID()
                            + "-" + data.getDate()
                            + "-" + data.getItem()
                            + "-" + data.getPrice()
                            + "-" + data.getTypePosition()
                            + "-" + data.getCategoryPosition();
            PreferenceManager.setString(mContext,key,value);

        }else{
            String value =
                            data.getUserID()
                            + "-" + data.getDate()
                            + "-" + data.getItem()
                            + "-" + data.getPrice()
                            + "-" + data.getTypePosition()
                            + "-" + data.getCategoryPosition()
                            + "-" + data.getUri().toString();

            PreferenceManager.setString(mContext,key,value);

        }

    }
    void changeData(Data data){

        int index = Integer.parseInt(data.getKey().replace("DATA", ""));// arraylist나 for문을 돌려 가지고 올 때의 index는 0부터 n-1까지이므로 key를 설정할 때에는 index+1해줌
        String key = String.format(Locale.KOREA, "%s%02d", "DATA", index); // key(ex) = 00002023053001
        data.setKey(key);

        if(data.getUri()==null) {
            String value =
                    data.getUserID()
                            + "-" + data.getDate()
                            + "-" + data.getItem()
                            + "-" + data.getPrice()
                            + "-" + data.getTypePosition()
                            + "-" + data.getCategoryPosition() ;

            PreferenceManager.setString(mContext,key,value);

        }else{
            String value =
                    data.getUserID()
                            + "-" + data.getDate()
                            + "-" + data.getItem()
                            + "-" + data.getPrice()
                            + "-" + data.getTypePosition()
                            + "-" + data.getCategoryPosition()
                            + "-" + data.getUri().toString();

            PreferenceManager.setString(mContext,key,value);

        }  //수정된 값 집어넣기

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
        String value = PreferenceManager.getString(mContext,key); // 해당 키의 데이터 가져오기
        if (value == null){
            return null; // 키에 대한 데이터가 null 이면 null 리턴
        }else {

            String[] saveData = value.split("-");
            Log.d(TAG, "saveData.length : "+saveData.length);
            if(saveData.length == 7) {

                String userID = saveData[0];
                String Date = saveData[1];
                String Item = saveData[2];
                String Price = saveData[3];
                int typePosition = Integer.parseInt(saveData[4]) ;
                int categoryPosition = Integer.parseInt(saveData[5]);
                Uri uri = Uri.parse(saveData[6]);


                return new Data(key, userID, Date, Item, Price, typePosition, categoryPosition,uri);
            } else if (saveData.length >= 6) {

                String userID = saveData[0];
                String Date = saveData[1];
                String Item = saveData[2];
                String Price = saveData[3];
                int typePosition = Integer.parseInt(saveData[4]) ;
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
            Log.d(TAG, "getDataList 중 arrayList 사이즈 : "+arrayList.size());
        }

    }

        void deleteData(ArrayList<Data> arrayList,Data data) {
//        선택한 Data 객체의 키부터 마지막 키까지 하나씩 데이터를 당기는 작업. 마지막 키는 삭제.

            int index = arrayList.indexOf(data);

             while (true){

                String key = String.format(Locale.KOREA, "%s%02d", "DATA", index); // 현재 키
                String nextKey = String.format(Locale.KOREA, "%s%02d", "DATA",index+1); // 다음 키
                String value = PreferenceManager.getString(mContext, nextKey); // 다음 키의 데이터
                Log.d(TAG, "key 값 : "+key);
                Log.d(TAG, "nextkey : "+nextKey);
                Log.d(TAG, "value : "+value);
                if (value == null || value.equals("")) { // 마지막 키인 경우 삭제하기
                    PreferenceManager.removeKey(mContext,key);
                    Log.d(TAG, "null 일때 실행");
                    break;
                }  else {// 다음 키가 있을 경우 현재 키에 다음 키의 값을 넣어주기
                    PreferenceManager.setString(mContext, key, value);
                    Log.d(TAG, "null 아닐때 실행");
                    index++;
                }
            }


    }
    private ArrayList<Data> filterDatebyData(String selectedDate) {

        ArrayList<Data> filteredList = new ArrayList<>();

        for (Data data : mArrayList) {

                    if (isSameDate(data.getDate(), selectedDate)) {
                        Log.d(TAG, "필터링 과정 중 data 주소값 : "+data);
                       filteredList.add(data);
                        Log.d(TAG, "타입포지션 : "+data.getTypePosition());
                    }


        }
        return filteredList;
    }

private boolean isSameDate(String date1, String date2){

    Log.d(TAG, "date1 값 : "+date1);
    Log.d(TAG, "date2 값 : "+date2);

        if(date1 == null || date2 == null){
            return false;
        }

    if (date1.equals(date2)) {
        return true;
    }

    return false;
}


    private void editItem(Data data,int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog, null, false);
        builder.setView(view);



        final AlertDialog dialog = builder.create();
        final Button btn_save = view.findViewById(R.id.btn_save);
        EditText edit_item = view.findViewById(R.id.edit_item);
        EditText edit_price = view.findViewById(R.id.edit_price);
        final Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Spinner spinnerType = view.findViewById(R.id.spinner_type);
        Spinner spinnerCategory = view.findViewById(R.id.spinner_category);
        TextView textViewNoImage = view.findViewById(R.id.textViewNoImage);
        imageViewImage = view.findViewById(R.id.imageViewImage);
        ImageButton buttonGallery = view.findViewById(R.id.imageButtonGallery);
        ImageButton buttonCamera = view.findViewById(R.id.imageButtonCamera);
        TextView textViewDate = view.findViewById(R.id.textViewDate);

        int mArrayListPosition = mArrayList.indexOf(data);

        textViewDate.setText(data.getDate());
        edit_item.setText(filteredArrayList.get(position).getItem());
        edit_price.setText(filteredArrayList.get(position).getPrice());


        imageViewImage.setImageURI(filteredArrayList.get(position).getUri());
        textViewNoImage.setVisibility(View.GONE);
        imageViewImage.setVisibility(View.VISIBLE);

        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                imageViewImage.setImageResource(0);

                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                imageViewImage.setVisibility(View.VISIBLE);
                textViewNoImage.setVisibility(View.GONE);

                Log.d(TAG, "수정갤러리 누르고 갤러리 등장");

                activityResultImage.launch(intent);
                Log.d(TAG, "피커에서 받아온 uri : "+intent.getData());
            }
        });

        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                imageViewImage.setImageResource(0);

                sendTakePhotoIntent();


                imageViewImage.setVisibility(View.VISIBLE);
                textViewNoImage.setVisibility(View.GONE);
            }
        });




        //type spinner 활성화

        //어댑터 생성
        ArrayAdapter typeAdapater = ArrayAdapter.createFromResource(MainActivity.this, R.array.type, android.R.layout.simple_spinner_dropdown_item);

        //스피너 클릭시 모양설정
        typeAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //스피너에 어댑터 연결
        spinnerType.setAdapter(typeAdapater);
        spinnerType.setSelection(filteredArrayList.get(position).getTypePosition());


        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int spinnerTypePosition, long id) {

                data.setTypePosition(spinnerTypePosition);

                if (spinnerType.getSelectedItemPosition() == 0) {
                    ArrayAdapter incomeAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.income, android.R.layout.simple_spinner_dropdown_item);
                    incomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinnerCategory.setAdapter(incomeAdapter);
                    spinnerCategory.setSelection(filteredArrayList.get(position).getCategoryPosition());
                    spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                } else if (spinnerType.getSelectedItemPosition() == 1) {
                    ArrayAdapter spendAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.spend, android.R.layout.simple_spinner_dropdown_item);
                    spendAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(spendAdapter);
                    Log.d(TAG, "내가누른 포지션값 : "+position);
                    Log.d(TAG, "필터리스트 해당데이터 포지션값 : "+filteredArrayList.get(position));
                    spinnerCategory.setSelection(filteredArrayList.get(position).getCategoryPosition());
                    spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (spinnerType.getSelectedItem().toString().length() == 0) {
                    Toast.makeText(mContext, "유형을 선택하세요.", Toast.LENGTH_SHORT).show();
                } else if (spinnerCategory.getSelectedItem().toString().length() == 0) {
                    Toast.makeText(mContext, "카테고리를 선택하세요.", Toast.LENGTH_SHORT).show();
                } else {

                    data.setItem(edit_item.getText().toString());
                    data.setPrice(edit_price.getText().toString());
                    data.setTypePosition(spinnerType.getSelectedItemPosition());
                    data.setCategoryPosition(spinnerCategory.getSelectedItemPosition());
                    data.setUri(uri);

                    //전체 어레이리스트 수정
                    mArrayList.set(mArrayListPosition, data);
                    Log.d(TAG, "수정된 어레이리스트 : "+mArrayList);
//                    어레이 리스트 필터링
                    filteredArrayList = filterDatebyData(filteredArrayList.get(position).getDate());
                    Log.d(TAG, "수정된 필터드 어레이리스트 : "+filteredArrayList);

                    //어댑터 세팅
                    mAdapter.setArrayList(filteredArrayList);
                    mAdapter.notifyItemChanged(position, filteredArrayList);

                    //리사이클러뷰 세팅
                    mRecyclerView.setAdapter(mAdapter);

                    changeData(data);

                    dialog.dismiss();

                    //메서드로 해당날짜 합계 넘기기
                    incomeSum = incomeSum();
                    spendSum = spendSum();

                    //쉐어드에 합계 저장
                    PreferenceManager.setInt(mContext,selectedDate+"incomeSum", incomeSum);
                    PreferenceManager.setInt(mContext,selectedDate+"spendSum", spendSum);

                    dayRecyclerView.setAdapter(calendarAdapter);


                }

            }

        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                btn_add.setVisibility(View.VISIBLE);
            }
        });


        dialog.show();


    }

    private void addItem() {


        data = new Data(userID, selectedDate);
        Log.d(TAG, "addItem에서 객체 만들어질때 data 주소값 : "+data);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog, null, false);
        builder.setView(view);


        final AlertDialog dialog = builder.create();

        final Button btn_save = view.findViewById(R.id.btn_save);
        EditText edit_item = view.findViewById(R.id.edit_item);
        EditText edit_price = view.findViewById((R.id.edit_price));
        final Button btn_cancel = view.findViewById(R.id.btn_cancel);
        Spinner spinnerType = view.findViewById(R.id.spinner_type);
        Spinner spinnerCategory = view.findViewById(R.id.spinner_category);
        ImageButton buttonGallery = view.findViewById(R.id.imageButtonGallery);
        ImageButton buttonCamera = view.findViewById(R.id.imageButtonCamera);
        imageViewImage = view.findViewById(R.id.imageViewImage);
        TextView textViewNoImage = view.findViewById(R.id.textViewNoImage);
        TextView textViewDate    = view.findViewById(R.id.textViewDate);


        textViewDate.setText(data.getDate());
        edit_item.setText("");
        edit_price.setText("");



        buttonGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                imageViewImage.setImageResource(0);
//                checkPermission(MainActivity.this);
                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                imageViewImage.setVisibility(View.VISIBLE);
                textViewNoImage.setVisibility(View.GONE);

                activityResultImage.launch(intent);


            }
        });


        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                    sendTakePhotoIntent();


                    imageViewImage.setVisibility(View.VISIBLE);
                    textViewNoImage.setVisibility(View.GONE);

            }
        });


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (edit_item.getText().length() == 0) {
                    Toast.makeText(mContext, "항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if (edit_price.getText().length() == 0) {
                    Toast.makeText(mContext, "가격을 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {

                    Log.d(TAG, "값 넣기 전 data 주소값 : "+data);

                    data.setItem(edit_item.getText().toString());
                    data.setPrice(edit_price.getText().toString());
                    data.setTypePosition(spinnerType.getSelectedItemPosition());
                    Log.d(TAG, "타입포지션 : "+spinnerType.getSelectedItemPosition());
                    data.setCategoryPosition(spinnerCategory.getSelectedItemPosition());
                    Log.d(TAG, "카테고리포지션 : "+spinnerCategory.getSelectedItemPosition());
//                    spinnerType.setSelection(typePosition);
//                    spinnerCategory.setSelection(categoryPosition);
                    data.setUri(uri);
                    Log.d(TAG, "sav할때 data 주소값 : "+data);



                    //새로운 어레이리스트에 값  추가
                    mArrayList.add(data);


                    int arrayListPosition = mArrayList.size()-1;
                    Log.d(TAG, "어레이리스트에 들어간 데이터 타입포지션 : "+mArrayList.get(arrayListPosition).getTypePosition());

                    Log.d(TAG, "add 어레이리스트 : "+mArrayList);
                    Log.d(TAG, "data의 인덱스값 : "+arrayListPosition);
                    //해당날짜로 필터링
                    filteredArrayList = filterDatebyData(selectedDate);
                    Log.d(TAG, "add 필터드어레이리스트 : "+filteredArrayList);
                    Log.d(TAG, "필터드어레이리스트 타입포지션 : "+filteredArrayList.get(filteredArrayList.size()-1).getTypePosition());

                    //리사이클러뷰 세팅
                    mAdapter.setArrayList(filteredArrayList);
                    mRecyclerView.setAdapter(mAdapter);

                    //메서드로 해당날짜 합계 넘기기
                    incomeSum = incomeSum();
                    spendSum = spendSum();

                    //쉐어드에 합계 저장
                    PreferenceManager.setInt(mContext,selectedDate+"incomeSum", incomeSum);
                    PreferenceManager.setInt(mContext,selectedDate+"spendSum", spendSum);

                    dayRecyclerView.setAdapter(calendarAdapter);

                    Log.d(TAG, "priceIncomeSum, priceSpendSum : "+incomeSum +" , "+spendSum);



                    dialog.dismiss();
                    btn_add.setVisibility(View.VISIBLE);

                    addData(mArrayList, data); //해당 인덱스의 데이터

                }
            }

        });


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                btn_add.setVisibility(View.VISIBLE);
            }
        });

        //type spinner 활성화

        //어댑터 생성
        ArrayAdapter typeAdapater = ArrayAdapter.createFromResource(MainActivity.this, R.array.type, android.R.layout.simple_spinner_dropdown_item);

        //스피너 클릭시 모양설정
        typeAdapater.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //스피너에 어댑터 연결
        spinnerType.setAdapter(typeAdapater);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (spinnerType.getSelectedItemPosition() == 0) {

                    ArrayAdapter incomeAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.income, android.R.layout.simple_spinner_dropdown_item);
                    incomeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(incomeAdapter);

                    spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else if (spinnerType.getSelectedItemPosition() == 1) {

                    ArrayAdapter spendAdapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.spend, android.R.layout.simple_spinner_dropdown_item);
                    spendAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(spendAdapter);

                    spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }

        });


        dialog.show();
    }

    //이미지가 저장될 파일을 만드는 함수
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".jpg",         /* suffix */
                storageDir          /* directory */
        );
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void sendTakePhotoIntent() {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);


        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }

            if (photoFile != null) {

                uri = FileProvider.getUriForFile(MainActivity.this, "com.example.goldentoads.fileprovider", photoFile);

                grantUriPermission(getApplicationContext().getPackageName(),uri,Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION) ;

                Log.d(TAG, "uri : "+ uri);
                activityResultCamera.launch(uri);


            }
        }

}
    //갤러리 인텐트 런처
    ActivityResultLauncher<Intent> activityResultImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if (result.getResultCode() == RESULT_OK) {

                if (result.getData() != null) {
                    Log.d(TAG, "런처 실행됌?");
                    Intent intent = result.getData();
                    uri = intent.getData();
                    Log.d(TAG, "포토피커에서 받아온 uri 값 : "+uri.toString());


                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);


                    Log.d(TAG, "런처 안의 uri : "+uri);

                    imageViewImage.setImageURI(uri);

                }
            }

        }
    });

    //카메라 인텐트 런처
    ActivityResultLauncher<Uri> activityResultCamera = registerForActivityResult(new ActivityResultContracts.TakePicture(),
            new ActivityResultCallback<Boolean>() {
                @Override
                public void onActivityResult(Boolean isTaken) {
                    if (isTaken) {
                        Log.d(TAG, "카메라 런처 실행됌?");

                        Log.d(TAG, "uri : "+uri);

                        imageViewImage.setImageURI(uri);

                    }

                }
            });

    // 갤러리에 사진 추가
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imageFilePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        imageViewImage.setImageURI(contentUri);
    }



    private void setMonthView(){
        Log.d(TAG, "setMonthView: 돼?");
        String yearAndMonth = monthYearFromDate(selectedLocalDate);
        textViewMonth.setText(yearAndMonth);

        String apiYear = yearAndMonth.substring(0,4);
        Log.d(TAG, "년 : "+apiYear);
        String apiMonth = yearAndMonth.substring(6,8);
        Log.d(TAG, "월 : "+apiMonth);

        ArrayList<LocalDate> dayList = dayInMonthArray(selectedLocalDate);



        calendarAdapter.setArrayList(dayList);

//        new Thread(()->{
//            try {
//                holidayInfoAPI(apiYear,apiMonth);
//                calendarAdapter.setHolidayArrayList(holidayArrayList);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            } catch (JSONException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();


        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);

        dayRecyclerView.setLayoutManager(layoutManager);

        dayRecyclerView.setAdapter(calendarAdapter);
    }


    private String monthYearFromDate(LocalDate date){

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월");

        return date.format(formatter);
    }

    private  String dayMonthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

        return date.format(formatter);
    }

    private ArrayList<LocalDate> dayInMonthArray(LocalDate date){

        ArrayList<LocalDate> dayList = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(date);

        //해당 월의 마지막 날짜 가져오기 (예 : 28, 30, 31)
        int lastDay = yearMonth.lengthOfMonth();

        //해당 월의 첫번째 날짜 가져오기
        LocalDate firstDay = selectedLocalDate.withDayOfMonth(1);

        //첫번째 날짜의 요일 가져오기
        int dayOfWeek = firstDay.getDayOfWeek().getValue();

        for (int i = 1; i < 42 ; i++) {

            if(i<=dayOfWeek || i > lastDay+dayOfWeek){
                dayList.add(null);
            }else {
                dayList.add(LocalDate.of(selectedLocalDate.getYear(),selectedLocalDate.getMonth(),i-dayOfWeek));
            }
        }

        return dayList;
    }

    private int incomeSum(){

        int priceIncomeSum = 0 ;

        for (int i = 0; i < filteredArrayList.size(); i++) {

            if(filteredArrayList.get(i).getTypePosition()==0) {
                int priceIncome = Integer.parseInt(filteredArrayList.get(i).getPrice());
                priceIncomeSum = priceIncomeSum + priceIncome;
            }
        }
         return priceIncomeSum;
    }

    private int spendSum(){

        int priceSpendSum = 0;
        for (int i = 0; i < filteredArrayList.size(); i++) {

             if (filteredArrayList.get(i).getTypePosition()==1) {
                int priceSpend = Integer.parseInt(filteredArrayList.get(i).getPrice());
                priceSpendSum = priceSpendSum + priceSpend;
            }
        }
            return  priceSpendSum;
    }

    public void holidayInfoAPI(String year, String month) throws IOException, JSONException {

        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + secretKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
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
        int totalCount = (int) parseBody.get("totalCount");



        if (totalCount == 0) {

            Log.d(TAG, "해당월에 공휴일이 없습니다.");

        } else if (totalCount == 1) {
            JSONObject parseItemsObject = (JSONObject) parseBody.get("items");
            JSONObject parseItemObject = (JSONObject) parseItemsObject.get("item");

            String dateName = parseItemObject.getString("dateName");
            Log.d(TAG, "dateName : "+dateName);
            String locdate= parseItemObject.getString("locdate");
            Log.d(TAG, "locdate : "+locdate);

            holidayDate = new HolidayDate(dateName, locdate);
            holidayArrayList = new ArrayList<>();
            holidayArrayList.add(holidayDate);


        } else if (totalCount >= 2) {
            JSONObject parseItemsObject = (JSONObject) parseBody.get("items");
            JSONArray parseItem = (JSONArray) parseItemsObject.get("item");

            holidayArrayList = new ArrayList<>();

            for (int i = 0; i < parseItem.length(); i++) {

                JSONObject object = parseItem.getJSONObject(i);
                String dateName = object.getString("dateName");
                Log.d(TAG, "dateName : "+dateName);
                String locdate= object.getString("locdate");
                Log.d(TAG, "locdate : "+locdate);

                holidayDate = new HolidayDate(dateName, locdate);
                holidayArrayList.add(holidayDate);
            }
        }

    }

}