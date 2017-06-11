package com.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by yutinglin on 2017/5/19.
 */

@ConfigurationProperties(prefix = "ems")
public class Constant {
    private String getBillNum_addr;
    private String updatePrintDatas_addr;
    private String query_addr;
    private String sysAccount;
    private String password;
    private String appKey;
    private String authenticate;
    private String version;
    private String barcode_path;


    public String getGetBillNum_addr() {
        return getBillNum_addr;
    }

    public void setGetBillNum_addr(String getBillNum_addr) {
        this.getBillNum_addr = getBillNum_addr;
    }

    public String getUpdatePrintDatas_addr() {
        return updatePrintDatas_addr;
    }

    public void setUpdatePrintDatas_addr(String updatePrintDatas_addr) {
        this.updatePrintDatas_addr = updatePrintDatas_addr;
    }

    public String getQuery_addr() {
        return query_addr;
    }

    public void setQuery_addr(String query_addr) {
        this.query_addr = query_addr;
    }

    public String getSysAccount() {
        return sysAccount;
    }

    public void setSysAccount(String sysAccount) {
        this.sysAccount = sysAccount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAuthenticate() {
        return authenticate;
    }

    public void setAuthenticate(String authenticate) {
        this.authenticate = authenticate;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBarcode_path() {
        return barcode_path;
    }

    public void setBarcode_path(String barcode_path) {
        this.barcode_path = barcode_path;
    }


}
