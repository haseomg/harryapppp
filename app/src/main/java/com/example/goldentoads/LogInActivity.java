package com.example.goldentoads;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class LogInActivity extends AppCompatActivity {

    Button buttonLogin, buttonJoin;
    EditText editTextID, editTextPassword;

    String makeID, makePassword, userID, userPassword;

    private Context LogInContext;

    ArrayList<User> users = new ArrayList<>();

    String TAG;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        LogInContext = this;
        TAG = "로그인액티비티";


        if (getUserSize() == 0) {
            addUser("0000","0000");
        }



        buttonLogin = findViewById(R.id.buttonLogin);
        buttonJoin  = findViewById(R.id.buttonJoin);
        editTextID = findViewById(R.id.editTextID);
        editTextPassword = findViewById(R.id.editTextPasword);

        buttonJoin.setOnClickListener(new View.OnClickListener() {



            @Override
            public void onClick(View v) {



                openDialog();

            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userID = editTextID.getText().toString();
                userPassword = editTextPassword.getText().toString();
                boolean foundUser = false;
                

                if (userID.equals("")) {
                    Toast.makeText(LogInContext, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
                } else if (userPassword.equals("")) {
                    Toast.makeText(LogInContext, "비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else{
                    for (int i = 0; i < getUserSize(); i++) {

                            if (userID.equals(getUser(i).userID) && userPassword.equals(getUser(i).userPassword)) {
                               foundUser = true;

                                Intent goMainAcitivyIntent = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    goMainAcitivyIntent = new Intent(LogInActivity.this , MainActivity.class);
                                }

                                goMainAcitivyIntent.putExtra("userID",userID);
                                goMainAcitivyIntent.putExtra("userPW",userPassword);

                                startActivity(goMainAcitivyIntent);

                                Toast.makeText(LogInContext, userID+"님 로그인 하셨습니다.", Toast.LENGTH_SHORT).show();


                                break;
                            }

                    }
                    
                    if(!foundUser){
                        Toast.makeText(LogInContext, "아이디 또는 비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }




    private void openDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LogInActivity.this);
        View view = LayoutInflater.from(LogInActivity.this).inflate(R.layout.dialog_join, null, false);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        EditText editTextMakeID = view.findViewById(R.id.editTextMakeID);
        EditText editTextMakePassword = view.findViewById(R.id.editTextMakePassword);
        Button buttonJoinFirst = view.findViewById(R.id.buttonJoinFirst);
        Button buttonCancel = view.findViewById(R.id.buttonCancel);





        buttonJoinFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean idExists = false; // 아이디 중복 여부 변수 추가

                for (int i = 0; i < getUserSize(); i++) {
                    if (editTextMakeID.getText().toString().equals(getUser(i).userID)) {
                        idExists = true; // 아이디 중복 시 변수 값을 true로 설정
                        break; // 중복된 아이디가 발견되면 반복문 종료
                    }
                }

                if (idExists) {
                    Toast.makeText(LogInContext, "존재하는 아이디입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    makeID = editTextMakeID.getText().toString();
                    makePassword = editTextMakePassword.getText().toString();
                    addUser(makeID, makePassword);
                    Toast.makeText(LogInContext, "ID: " + makeID + "\nPassword: " + makePassword + "로 생성되었습니다.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //


    void addUser(String id, String password){

            String key = String.format(Locale.KOREA, "%s%03d", "USER", getUserSize()); // key = USER001;
            String value = id+"-"+password;
            PreferenceManager.setString(LogInContext,key,value);

    }

    int getUserSize() { // 저장된 멤버 아이템의 개수
        int i = 0;

        while (getUser(i) != null) {
            i++;
        }
        return i;
    }

    User getUser(int index) { // 특정 index 의 멤버 정보 가져오기
        String key = String.format(Locale.KOREA, "%s%03d", "USER", index); // arraylist나 for문을 돌려 가지고 올 때의 index는 0부터 n-1까지이므로 key를 설정할 때에는 index+1해줌
        String value = PreferenceManager.getString(LogInContext,key); // 해당 키의 데이터 가져오기
        if (value == null){
            return null; // 키에 대한 데이터가 null 이면 null 리턴
        }else {

            String[] saveData = value.split("-");
            if(saveData.length>=2) {
                String userID = saveData[0];
                String userPassword = saveData[1];
                return new User(key, userID, userPassword);
            }else {
                return null;
            }
        }
    }

    void getUserList(ArrayList<User> users) { // 저장된 모든 멤버 추가하기
         users.clear();
        for (int i = 0; i < getUserSize(); i++) {
            users.add(getUser(i));
        }


    }

//    void deleteMember(User user) {
////        선택한 User 객체의 키부터 마지막 키까지 하나씩 데이터를 당기는 작업. 마지막 키는 삭제.
//        int index = Integer.parseInt(user.getKey().replace("USER", "")); // if user.key == USER001 -> index == 1
//        while (true) {
//            String key = String.format(Locale.KOREA, "%s%03d", "USER", index); // 현재 키
//            String nextKey = String.format(Locale.KOREA, "%s%03d", "USER", index + 1); // 다음 키
//            String value = PreferenceManager.getString(LogInContext, nextKey); // 다음 키의 데이터
//
//            if (value == null) { // 마지막 키인 경우 삭제하기
//                PreferenceManager.removeKey(LogInContext,key);
//                break;
//            } else // 다음 키가 있을 경우 현재 키에 다음 키의 값을 넣어주기
//                PreferenceManager.setString(LogInContext,key,value);
//        }
//
//    }
}