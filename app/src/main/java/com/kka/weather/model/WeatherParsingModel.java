package com.kka.weather.model;

import java.util.ArrayList;

public class WeatherParsingModel {

    private Object main;
    private ArrayList<WeatherModel> weather;

    public WeatherParsingModel(Object main, ArrayList<WeatherModel> weather) {
        this.main = main;
        this.weather = weather;
    }

//    public Object getMain() {
//        return main;
//    }

    public String getTemp() {
        String temp = String.valueOf(main).substring(6,String.valueOf(main).indexOf(","));
        return String.valueOf((Double.parseDouble(temp) -273.15)).substring(0,4);
    }

    public ArrayList<WeatherModel> getWeather() {
        return weather;
    }

}