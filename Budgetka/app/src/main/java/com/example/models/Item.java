package com.example.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Item {

    private double price;
    private String type;
    private boolean isCosts;
    private String date;

    @Override
    public String toString() {
        return Double.toString(price) + "\n" + this.type + "\n" + this.isCosts + "\n" + this.date;
    }

    public Item() {
    }

    public Item(double price, String type, boolean isCosts) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
        this.date = sdf.format(new Date());
        this.price = price;
        this.type = type;
        this.isCosts = isCosts;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("price", price);
        result.put("type", type);
        result.put("isCosts", isCosts);
        result.put("date", date);
        return result;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCosts() {
        return isCosts;
    }

    public void setCosts(boolean costs) {
        isCosts = costs;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
