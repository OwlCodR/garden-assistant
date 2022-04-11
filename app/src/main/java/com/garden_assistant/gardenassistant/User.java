package com.garden_assistant.gardenassistant;

import android.util.Log;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String username;
    private String email;
    private Map<String, Map<String, Object>> MapGreenhouses;
    private int countGreenhouse;

    public User() {
        MapGreenhouses = new HashMap<>();
    }

    public Map<String, Map<String, Object>> getMapGreenhouses() {
        return MapGreenhouses;
    }

    public void setMapGreenhouses(Map<String, Map<String, Object>> mapGreenhouses) {
        MapGreenhouses = mapGreenhouses;
    }
    public int getCountGreenhouse() {
        return countGreenhouse;
    }

    public void setCountGreenhouse(int countGreenhouse) {
        this.countGreenhouse = countGreenhouse;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGreenhouseTemperature(String greenhouse_id) {
        return (String) MapGreenhouses.get(greenhouse_id).get("Temperature");
    }

    public void setGreenhouseTemperature(String greenhouse_id, String temperature) {
        Map<String, Object> map = new HashMap<>();
        map.put("Temperature", temperature);
        MapGreenhouses.put(greenhouse_id, map);
    }

    public String getGreenhouseHumidity(String greenhouse_id) {
        return (String) MapGreenhouses.get(greenhouse_id).get("Humidity");
    }

    public void setGreenhouseHumidity(String greenhouse_id, String humidity) {
        Map<String, Object> map = new HashMap<>();
        map.put("Humidity", humidity);
        MapGreenhouses.put(greenhouse_id, map);
    }

    public String getGreenhouseName(String greenhouse_id) {
        return (String) MapGreenhouses.get(greenhouse_id).get("Name");
    }

    public void setGreenhouseName(String greenhouse_id, String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("Name", name);
        MapGreenhouses.put(greenhouse_id, map);
        Log.d("fLog", getGreenhouseName(greenhouse_id));
    }

}