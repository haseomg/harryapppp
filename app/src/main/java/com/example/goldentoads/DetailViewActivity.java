package com.example.goldentoads;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import org.jetbrains.annotations.NotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

public class DetailViewActivity extends AppCompatActivity {


    TextView textViewDataDate, textViewDataType, textViewDataCategory, textViewDataItem, textViewDataPrice;
    ImageView imageViewDataImage;

    ImageButton imageButtonBack;

    Data data;

    Context detailContext;
    String TAG;
    String[] type, category;


    private static final int REQUEST_PERMISSION_CODE = 123;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_MEDIA_IMAGES,
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            android.Manifest.permission.CAMERA
    };

    public void checkPermission(Context context){

        int WRITE_PERMISSION = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int READ_PERMISSION = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES);
        int CAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (READ_PERMISSION != PackageManager.PERMISSION_GRANTED) {

            Log.d(TAG, "READ_PERMISSION : "+READ_PERMISSION);

            if(shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)){

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("권한 필요")
                        .setMessage("외부 저장소의 이미지에 접근하기 위해 권한이 필요합니다.")
                        .setPositiveButton("확인", (dialog, which) -> {
                            ActivityCompat.requestPermissions(this, new String[]{ android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
                        })
                        .setNegativeButton("취소", (dialog, which) -> dialog.dismiss())
                        .show();
            }

            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE);

        }else {
            Log.d(TAG, "권한이 이미 허용되어있습니다.");
        }


    }

//    @Override
//    protected void onStart(){
//        super.onStart();
//        checkPermission(this);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        checkPermission(this);

        detailContext = getApplicationContext();
        TAG = "디테일뷰액티비티";

        textViewDataDate = findViewById(R.id.textViewDataDate);
        textViewDataType = findViewById(R.id.textViewDataType);
        textViewDataCategory = findViewById(R.id.textViewDataCategory);
        textViewDataItem = findViewById(R.id.textViewDataItem);
        textViewDataPrice = findViewById(R.id.textViewDataPrice);
        imageViewDataImage = findViewById(R.id.imageViewDataImage);
        imageButtonBack = findViewById(R.id.imageButtonBack);

        Intent intent = getIntent();
        int position = intent.getExtras().getInt("position");

        data = getData(position);
        Log.d(TAG, "data uri값 : "+data.getUri());
        type = getResources().getStringArray(R.array.type);

        if (data.getTypePosition() == 0) {
            category = getResources().getStringArray(R.array.income);
        } else if (data.getTypePosition() == 1) {
            category = getResources().getStringArray(R.array.spend);
        }





        textViewDataDate.setText(data.getDate());
        textViewDataType.setText(type[data.getTypePosition()]);
        textViewDataCategory.setText(category[data.getCategoryPosition()]);
        textViewDataItem.setText(data.getItem());

        DecimalFormat decimalFormat = new DecimalFormat("#,###원");
        String price = data.getPrice();
        String formattedPrice = "";

        if(price != null){
            try {
                formattedPrice = decimalFormat.format(Long.parseLong(price));
            }catch (NumberFormatException e){
                Log.e(TAG, "NumberFormatException : "+e.getMessage() );
            }
        }
        textViewDataPrice.setText(formattedPrice);

        if (data.getUri() == null) {
            imageViewDataImage.setVisibility(View.INVISIBLE);
        } else if (data.getUri() != null) {
           imageViewDataImage.setImageURI(data.getUri());
            imageViewDataImage.setVisibility(View.VISIBLE);
        }



        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                Intent goMainIntent = new Intent(detailContext, MainActivity.class);
                startActivity(goMainIntent);
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
        String value = PreferenceManager.getString(detailContext,key); // 해당 키의 데이터 가져오기
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





}