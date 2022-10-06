package com.jetec.zero.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "stock")
public class StockBean {

    @Id
    @Column(columnDefinition="CHAR(32)",name="stock_id")
    private String stockid;
    private String name;
    @Column(name = "stock_day")
    private String stockday;
    @Column(name = "transaction_date" )
    private LocalDate transactiondate;//日期
    @Column(name = "transactions_number")
    private Integer transactionsnumber;//成交量
    @Column(name = "open_price")
    private Double openprice;//開
    @Column(name = "end_price")
    private Double endprice;//結束
    @Column(name = "hight_price")
    private Double hightprice;//最高
    @Column(name = "lowest_price")
    private Double lowestprice;//最低

    public String getStockid() {
        return stockid;
    }

    public void setStockid(String stockid) {
        this.stockid = stockid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStockday() {
        return stockday;
    }

    public void setStockday(String stockday) {
        this.stockday = stockday;
    }


    public Integer getTransactionsnumber() {
        return transactionsnumber;
    }

    public void setTransactionsnumber(Integer transactionsnumber) {
        this.transactionsnumber = transactionsnumber;
    }

    public Double getOpenprice() {
        return openprice;
    }

    public void setOpenprice(Double openprice) {
        this.openprice = openprice;
    }

    public Double getEndprice() {
        return endprice;
    }

    public void setEndprice(Double endprice) {
        this.endprice = endprice;
    }

    public Double getHightprice() {
        return hightprice;
    }

    public void setHightprice(Double hightprice) {
        this.hightprice = hightprice;
    }

    public Double getLowestprice() {
        return lowestprice;
    }

    public void setLowestprice(Double lowestprice) {
        this.lowestprice = lowestprice;
    }

    public LocalDate getTransactiondate() {
        return transactiondate;
    }

    public void setTransactiondate(LocalDate transactiondate) {
        this.transactiondate = transactiondate;
    }

    @Override
    public String toString() {
        return "StockBean{" +
                "stockid='" + stockid + '\'' +
                ", name='" + name + '\'' +
                ", stockday='" + stockday + '\'' +
                ", transactionsnumber=" + transactionsnumber +
                ", openprice=" + openprice +
                ", endprice=" + endprice +
                ", hightprice=" + hightprice +
                ", lowestprice=" + lowestprice +
                '}';
    }
}
