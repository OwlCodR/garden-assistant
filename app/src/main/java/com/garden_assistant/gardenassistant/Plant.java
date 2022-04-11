package com.garden_assistant.gardenassistant;

import android.support.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Plant implements Comparable<Plant> {
    private int image_id;
    private long last_days;
    private int days;
    private String name;
    private String date;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    Plant() {}

    Plant(String name, int days, long last_days, int image_id, String date) {
        this.name = name;
        this.days = days;
        this.last_days = last_days;
        this.image_id = image_id;
        this.date = date;
    }

    public static long getDaysCount(String start, String end) {
        long daysCount = 0;
        try {
            Date startDate = simpleDateFormat.parse(start);
            Date endDate = simpleDateFormat.parse(end);
            double msInDay = 60 * 60 * 24 * 1000; // 86400000 ms в 1 дне
            daysCount = Math.round((endDate.getTime() - startDate.getTime()) / msInDay);
        } catch (Exception e) {
        }
        return daysCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getImage_id() {
        return image_id;
    }

    public void setImage_id(int image_id) {
        this.image_id = image_id;
    }

    public long getLast_days() {
        return last_days;
    }

    public void setLast_days(long last_days) {
        this.last_days = last_days;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(@NonNull Plant plant) {
        return this.name.compareTo(plant.getName());
    }
}