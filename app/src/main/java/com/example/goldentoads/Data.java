package com.example.goldentoads;


import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

//리사이클러뷰에 담을 Data 클래스
public class Data  {

    private String key, date, item, price, userID, userPW;

    private int typePosition, categoryPosition;

    private Uri uri;


    public Data(String date){
        this.date = date;
    }
    public  Data(String userID, String date){
        this.userID = userID;
        this.date = date;
    }



    public Data(String item, String price, int typePosition, int categoryPosition, Uri uri){
        this.item = item;
        this.price = price;
        this.typePosition = typePosition;
        this.categoryPosition = categoryPosition;
        this.uri = uri;
    }

    public Data(String userID, String date ,String item, String price, int typePosition, int categoryPosition, Uri uri){

        this.userID = userID;
        this.date = date;
        this.item = item;
        this.price = price;
        this.typePosition = typePosition;
        this.categoryPosition = categoryPosition;
        this.uri = uri;

    }

    public Data(String key,String userID, String date ,String item, String price, int typePosition, int categoryPosition, Uri uri){

        this.key = key;
        this.userID = userID;
        this.date = date;
        this.item = item;
        this.price = price;
        this.typePosition = typePosition;
        this.categoryPosition = categoryPosition;
        this.uri = uri;
    }
    public Data(String key,String userID, String date ,String item, String price, int typePosition, int categoryPosition){

        this.key = key;
        this.userID = userID;
        this.date = date;
        this.item = item;
        this.price = price;
        this.typePosition = typePosition;
        this.categoryPosition = categoryPosition;
    }


    public String getItem(){
        return item;
    }

    public void setItem(String item){
        this.item =item;
    }

    public String getPrice(){
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getTypePosition(){return typePosition;}

    public void setTypePosition(int typePosition){this.typePosition=typePosition;}

    public int getCategoryPosition(){return categoryPosition;}

    public void setCategoryPosition(int categoryPosition){this.categoryPosition=categoryPosition;}

    public Uri getUri(){return uri;}

    public void setUri(Uri uri){this.uri=uri;}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserPW() {
        return userPW;
    }

    public void setUserPW(String userPW) {
        this.userPW = userPW;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }


}
