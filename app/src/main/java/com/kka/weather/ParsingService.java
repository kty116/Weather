package com.kka.weather;

import com.kka.weather.model.GeoParsingModel;
import com.kka.weather.model.WeatherParsingModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ParsingService {
    String weatherUrl = "http://api.openweathermap.org/data/2.5/";
    String geocodingUrl = "https://maps.googleapis.com/maps/api/geocode/";

    @GET("weather")
    Call<WeatherParsingModel> getWeather(@Query("lat") String lat, @Query("lon") String lon, @Query("APPID") String APPID);

    @GET("json")
    Call<GeoParsingModel> getGeo(@Query("address") String address, @Query("language") String language ,@Query("key") String key);




}
