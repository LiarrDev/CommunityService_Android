package com.liarr.communityservice.Database;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport {

    private int id;                     // 城市 ID

    private String cityName;            // 城市名

    private int cityCode;               // 城市代号

    private int provinceId;             // 当前城市所属省份的 ID

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}