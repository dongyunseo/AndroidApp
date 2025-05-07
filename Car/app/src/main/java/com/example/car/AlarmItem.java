package com.example.car;

public class AlarmItem {
    private String fullModelName;
    private String price;
    private String regDate;
    private int alertCount; // 추가

    public AlarmItem(String fullModelName, String price, String regDate, int alertCount) {
        this.fullModelName = fullModelName;
        this.price = price;
        this.regDate = regDate;
        this.alertCount = alertCount;
    }


    public String getFullModelName() {
        return fullModelName;
    }

    public String getPrice() {
        return price;
    }

    public String getRegDate() {
        return regDate;
    }

    public int getAlertCount() {
        return alertCount;
    }

    @Override
    public String toString() {
        return "AlarmItem{" +
                "fullModelName='" + fullModelName + '\'' +
                ", price='" + price + '\'' +
                ", regDate='" + regDate + '\'' +
                ", alertCount='" + alertCount + '\'' +
                '}';
    }
}