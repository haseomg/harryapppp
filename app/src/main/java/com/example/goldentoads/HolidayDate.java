package com.example.goldentoads;

public class HolidayDate {

    String dateName;
    String locdate;

    public HolidayDate(String dateName, String locdate){

        this.dateName = dateName;
        this.locdate = locdate;

    }

    public String getDateName() {
        return dateName;
    }

    public void setDateName(String dateName) {
        this.dateName = dateName;
    }

    public String getLocdate() {
        return locdate;
    }

    public void setLocdate(String locdate) {
        this.locdate = locdate;
    }
}
