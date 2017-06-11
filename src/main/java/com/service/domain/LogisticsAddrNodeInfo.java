package com.service.domain;

import org.hibernate.annotations.Table;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;
import java.time.LocalDateTime;

/**
 * Created by yutinglin on 2017/5/24.
 * 包裹路由节点信息
 */
@Entity
@javax.persistence.Table(name="logistics_addr_node_info")
public class LogisticsAddrNodeInfo {

    public LogisticsAddrNodeInfo(){

    }


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //发生时间
    private Date occurTime;
    //订单号
    private String tradeSn;
    //运单号
    private String mailNo;
    //物流公司
    private String company;
    //包裹所在地
    private String station;
    //事件
    private String action;
    //备注
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getOccurTime() {
        return occurTime;
    }

    public void setOccurTime(Date occurTime) {
        this.occurTime = occurTime;
    }

    public String getTradeSn() {
        return tradeSn;
    }

    public void setTradeSn(String tradeSn) {
        this.tradeSn = tradeSn;
    }

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
