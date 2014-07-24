package com.joshdonlan.apidemo;

import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

/**
 * Created by jdonlan on 7/24/14.
 */
public class Stock {

    final String TAG = "STOCK CLASS";

    private String mSymbol;
    private Double mPrice;
    private String mDate;
    private Double mHigh;
    private Double mLow;
    private Double mChange;
    private Double mOpen;
    private Integer mVolume;
    private String mPercent;
    
    public Stock(){}
    
    public Stock(String symbol, Double price, String date, Double high, Double low, Double change, Double open, Integer volume, String percent){
        mSymbol = symbol;
        mPrice = price;
        mDate = date;
        mHigh = high;
        mLow = low;
        mChange = change;
        mOpen = open;
        mVolume = volume;
        mPercent = percent;
    }
    
    public Stock(JSONObject stockData){
        try {
            mSymbol = stockData.getString("symbol");
            mPrice = stockData.getDouble("price");
            mDate = stockData.getString("date") + " " + stockData.getString("time");
            mHigh = stockData.getDouble("high");
            mLow = stockData.getDouble("low");
            mChange = stockData.getDouble("change");
            mOpen = stockData.getDouble("open");
            mVolume = stockData.getInt("volume");
            mPercent = stockData.getString("chgpct");
        } catch (Exception e) {
            Log.e(TAG, "Error updating display");
        }       
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String mSymbol) {
        this.mSymbol = mSymbol;
    }

    public Double getPrice() {
        return mPrice;
    }

    public void setPrice(Double mPrice) {
        this.mPrice = mPrice;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public Double getHigh() {
        return mHigh;
    }

    public void setHigh(Double mHigh) {
        this.mHigh = mHigh;
    }

    public Double getLow() {
        return mLow;
    }

    public void setLow(Double mLow) {
        this.mLow = mLow;
    }

    public Double getChange() {
        return mChange;
    }

    public void setChange(Double mChange) {
        this.mChange = mChange;
    }

    public Double getOpen() {
        return mOpen;
    }

    public void setOpen(Double mOpen) {
        this.mOpen = mOpen;
    }

    public Integer getVolume() {
        return mVolume;
    }

    public void setVolume(Integer mVolume) {
        this.mVolume = mVolume;
    }

    public String getPercent() {
        return mPercent;
    }

    public void setPercent(String mPercent) {
        this.mPercent = mPercent;
    }
}
