package com.kka.weather.model;

public class ResultsModel {
    private String formatted_address;
    private Object geometry;

    public ResultsModel(String formatted_address, Object geometry) {
        this.formatted_address = formatted_address;
        this.geometry = geometry;
    }

    public double getLat() {
        int start = geometry.toString().indexOf("lat=")+4;
        int end = geometry.toString().indexOf(",");
        return Double.parseDouble(geometry.toString().substring(start,end));
    }

    public double getLon() {
        int start = geometry.toString().indexOf("lng=")+4;
        int end = geometry.toString().indexOf("}");
        return Double.parseDouble(geometry.toString().substring(start,end));
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public Object getGeometry() {
        return geometry;
    }

}
