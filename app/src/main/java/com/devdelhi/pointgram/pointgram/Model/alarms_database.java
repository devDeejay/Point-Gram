package com.devdelhi.pointgram.pointgram.Model;

public class alarms_database {
    String name;
    String description;
    String place;
    Double lat,lng;
    int range;

    public alarms_database(String name, String description, Double lat, Double lng, int range, String nameOfPlace) {
        this.name = name;
        this.description = description;
        this.lat = lat;
        this.lng = lng;
        this.range = range;
        this.place = nameOfPlace;
    }

    public alarms_database(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameOfPlace() {
        return place;
    }

    public void setNameOfPlace(String nameOfPlace) {
        this.place = nameOfPlace;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }
}
