package com.kka.weather.model;

public class WeatherModel {
    private String main;
    private String description;

    public WeatherModel(String main, String description) {
        this.main = main;
        this.description = description;
    }

    public String getMain() {
        return main;
    }

    public String getDescription() {
        return description;
    }
}
