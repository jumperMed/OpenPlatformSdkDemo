package com.jumper.openplatformdemo.fetalheart.bean;

import com.jumper.chart.detail.FetalRecords;
import com.jumper.openplatformdemo.util.L;


public class FetalRecord implements FetalRecords {


    public String time;


    public FetalRecord() {
    }


    public FetalRecord(String time) {
        super();
        this.time = time;
    }


    public int getTimeCounts(String time) {
        int[] number = getTimes(time);
        return number[0] * 60 + number[1];
    }


    public int[] getTimes(String time) {
        int[] numbers = new int[2];
        try {
            String[] times = time.split(":");
            numbers[0] = Integer.parseInt(times[0]);
            numbers[1] = Integer.parseInt(times[1]);
        } catch (Exception e) {
            L.d("类型转换异常");
            return numbers;
        }
        return numbers;
    }

    public int[] getTimes() {
        return getTimes(this.time);
    }


    public boolean isLargerFifteenSecond(String time) {
        return getTimeCounts(time) - getTimeCounts(this.time) >= 15;
    }

    @Override
    public int getMinutes() {
        int numbers = 0;
        try {
            String[] times = time.split(":");
            numbers = Integer.parseInt(times[0]);
        } catch (Exception e) {
            L.d("类型转换异常");
            return numbers;
        }
        return numbers;
    }

    @Override
    public int getSeconds() {
        int numbers = 0;
        try {
            String[] times = time.split(":");
            numbers = Integer.parseInt(times[1]);
        } catch (Exception e) {
            L.d("类型转换异常");
            return numbers;
        }
        return numbers;
    }
}
