package com.b18060412.superdiary.db;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Objects;

public class DiaryItem implements Serializable {
    private String id; //主键，唯一索引
    private String mood;//心情
    private String content;//内容
    private long date;//日期
    private String weather;//天气
    private String location;//地点

    public DiaryItem(String id,String mood, String content, long date, String weather, String location) {
        this.id = id;
        this.mood = mood;
        this.content = content;
        this.date = date;
        this.weather = weather;
        this.location = location;
    }

    public DiaryItem() {
    }

    public DiaryItem(long date) {
        this.date = date;
        this.mood = "😶";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDay() {
        return new SimpleDateFormat("dd", Locale.CHINA).format(this.date);
    }

    public String getDateInfo() {
        return new SimpleDateFormat("yyyy年MM月\nHH:mm·EEEE", Locale.CHINA).format(this.date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiaryItem diaryItem = (DiaryItem) o;
        return date == diaryItem.date &&
                Objects.equals(id, diaryItem.id) &&
                Objects.equals(mood, diaryItem.mood) &&
                Objects.equals(content, diaryItem.content) &&
                Objects.equals(weather, diaryItem.weather) &&
                Objects.equals(location, diaryItem.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mood, content, date, weather, location);
    }
}
