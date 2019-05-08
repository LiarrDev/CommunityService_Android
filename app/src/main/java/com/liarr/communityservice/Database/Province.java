package com.liarr.communityservice.Database;

import org.litepal.crud.LitePalSupport;

public class Province extends LitePalSupport {

    private int id;                     // 省份 ID

    private String provinceName;        // 省份名

    private int provinceCode;           // 省份代号

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}