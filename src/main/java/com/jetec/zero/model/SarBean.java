package com.jetec.zero.model;

import java.time.LocalDate;

public class SarBean {

    private String name;
    private Double sar;
    private Double endprice;
    private String stockday;
    private LocalDate transactiondate;
    private boolean isUP ;

    public boolean isUP() {
        return isUP;
    }

    public void setUP(boolean UP) {
        isUP = UP;
    }

    public Double getEndprice() {
        return endprice;
    }

    public void setEndprice(Double endprice) {
        this.endprice = endprice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getSar() {
        return sar;
    }

    public void setSar(Double sar) {
        this.sar = sar;
    }

    public String getStockday() {
        return stockday;
    }

    public void setStockday(String stockday) {
        this.stockday = stockday;
    }

    public LocalDate getTransactiondate() {
        return transactiondate;
    }

    public void setTransactiondate(LocalDate transactiondate) {
        this.transactiondate = transactiondate;
    }

    @Override
    public String toString() {
        return "SarBean{" +
                "name='" + name + '\'' +
                ", sar=" + sar +
                ", endprice=" + endprice +
                ", stockday='" + stockday + '\'' +
                ", transactiondate=" + transactiondate +
                ", isUP=" + isUP +
                '}';
    }
}
