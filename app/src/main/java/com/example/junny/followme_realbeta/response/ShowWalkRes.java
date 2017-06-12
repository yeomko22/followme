package com.example.junny.followme_realbeta.response;

import java.util.ArrayList;

/**
 * Created by junny on 2017. 6. 10..
 */

public class ShowWalkRes {
    String type;
    Features[] features;

    public ShowWalkRes(String type, Features[] features) {
        this.type = type;
        this.features = features;
    }

    public int get_count(){
        return features.length;
    }
    public int get_coor_count(int i){return features[i].geometry.coordinates.length;}
    public String[] get_coor_lat(int i, int j){
        //지금 코디네이츠에는 어레이리스트가 들어가있다
        //ArrayList<String[]>  이 형태
        String[] a=new String[2];
        a[0]=((ArrayList)(features[i].geometry.coordinates[j])).get(1).toString();
        a[1]=((ArrayList)(features[i].geometry.coordinates[j])).get(0).toString();
        return a;
    }
    public String get_type(int i){
        return features[i].geometry.type;
    }
    public String get_point_lat(int i){
        return features[i].geometry.coordinates[1].toString();
    }
    public String get_point_lng(int i){
        return features[i].geometry.coordinates[0].toString();
    }
    public String get_description(int i){
        return features[i].properties.description;
    }
    public String get_total_time(){
        return features[0].properties.totalTime;
    }
    public String get_total_distance(){
        return features[0].properties.totalDistance;
    }

    class Features{
        Geometry geometry;
        Properties properties;
        String type;

        public Features(Geometry geometry, Properties properties, String type) {
            this.geometry = geometry;
            this.properties = properties;
            this.type = type;
        }
    }
    class Geometry{
        String type;
        Object[] coordinates;

        public Geometry(String type, String[] coordinates) {
            this.type = type;
            this.coordinates = coordinates;
        }
        public Geometry(String type, Point[] coordinates) {
            this.type = type;
            this.coordinates = coordinates;
        }
    }

    class Point{
        String[] latlng;
        public Point(String[] latlng) {
            this.latlng = latlng;
        }
    }
    class Properties{
        String totalDistance;
        String totalTime;
        String description;

        public Properties(String totalDistance, String totalTime, String description) {
            this.totalDistance = totalDistance;
            this.totalTime = totalTime;
            this.description = description;
        }
    }
}

