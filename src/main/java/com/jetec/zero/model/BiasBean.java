package com.jetec.zero.model;

public class BiasBean {

    private Double bias;
    private String day;


    public BiasBean() {
    }

    public BiasBean( String day,Double bias) {
        this.bias = bias;
        this.day = day;
    }

    public Double getBias() {
        return bias;
    }

    public void setBias(Double bias) {
        this.bias = bias;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "BiasBean{" +
                "bias=" + bias +
                ", day='" + day + '\'' +
                '}';
    }
}
