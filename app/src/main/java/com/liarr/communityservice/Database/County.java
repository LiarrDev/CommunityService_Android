package com.liarr.communityservice.Database;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {

    private int id;                 // 县 ID

    private String countyName;      // 县名

    private int cityId;             // 当前县所属市的 ID

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}