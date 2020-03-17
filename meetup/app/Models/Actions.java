package com.rubenmimoun.meetup.app.Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Actions {

    private String kind ;
    private String name ;
    private String place ;
    private String time ;
    private String end_time ;
    private String img_url ;
    private String creator ;
    private String adress ;
    private String description ;
    private String date ;
    private Map<String,Object> participating ;


    public Actions(){}

    public Actions(String kind, String name, String place, String time, String end_time, String creator) {
        this.kind = kind ;
        this.name = name;
        this.place = place;
        this.time = time;
        this.end_time = end_time;
        this.img_url = img_url;
        this.creator =creator ;
        this.adress = "" ;
        this.description = "";
        this.date = "";

    }

    public String getDate() {
        return date;
    }

    public Map<String,Object> getParticipating() {
        return participating;
    }

    public void setParticipating(Map<String,Object>participating) {
        this.participating = participating;
    }

    public String getAdress() {
        return adress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    @Override
    public String toString() {
        return "Actions{" +
                "kind='" + kind + '\'' +
                ", name='" + name + '\'' +
                ", place='" + place + '\'' +
                ", time='" + time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", img_url='" + img_url + '\'' +
                ", creator='" + creator + '\'' +
                '}';
    }
}
