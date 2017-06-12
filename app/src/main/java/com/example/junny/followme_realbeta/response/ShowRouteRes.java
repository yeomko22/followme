package com.example.junny.followme_realbeta.response;

/**
 * Created by junny on 2017. 6. 10..
 */

public class ShowRouteRes {
    Route[] routes;
    String status;

    public ShowRouteRes(Route[] routes, String status) {
        this.routes = routes;
        this.status = status;
    }

    public String get_poly(){
        return routes[0].overview_polyline.points;
    }
    public int get_legs_count(){
        return routes[0].legs[0].steps.length;
    }
    public String get_type(int i){
        return routes[0].legs[0].steps[i].travel_mode;
    }
    public String get_line(int i){
        return routes[0].legs[0].steps[i].transit_details.line.short_name;
    }

    public String get_start_name(int i){
        return routes[0].legs[0].steps[i].transit_details.departure_stop.name;
    }

    public String get_stop_name(int i){
        return routes[0].legs[0].steps[i].transit_details.arrival_stop.name;
    }
    public String get_walk_time(int i){
        return routes[0].legs[0].steps[i].duration.value;
    }
    public String get_total_distance(){
        return routes[0].legs[0].distance.text;
    }
    public String get_total_time(){
        return routes[0].legs[0].duration.text;
    }

    class Route{
        Legs[] legs;
        Overview_polyline overview_polyline;

        public Route(Legs[] legs, Overview_polyline overview_polyline) {
            this.legs = legs;
            this.overview_polyline = overview_polyline;
        }
    }
    class Overview_polyline{
        String points;

        public Overview_polyline(String points) {
            this.points = points;
        }
    }

    class Legs{
        Text_Value distance;
        Text_Value duration;
        Steps[] steps;

        public Legs(Text_Value distance, Text_Value duration, Steps[] steps) {
            this.distance = distance;
            this.duration = duration;
            this.steps = steps;
        }
    }
    class Point{
        String lat;
        String lng;

        public Point(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }
    class Time{
        String text;
        String time_zone;
        String value;

        public Time(String text, String time_zone, String value) {
            this.text = text;
            this.time_zone = time_zone;
            this.value = value;
        }
    }
    class Text_Value{
        String text;
        String value;

        public Text_Value(String text, String value) {
            this.text = text;
            this.value = value;
        }
    }
    class Steps{
        Text_Value distance;
        Text_Value duration;
        String html_instructions;
        Transit_Detail transit_details;
        String travel_mode;

        public Steps(Text_Value distance, Text_Value duration, String html_instructions, Transit_Detail transit_details, String travel_mode) {
            this.distance = distance;
            this.duration = duration;
            this.html_instructions = html_instructions;
            this.transit_details = transit_details;
            this.travel_mode = travel_mode;
        }
    }

    class Transit_Detail{
        Stop arrival_stop;
        Time arrival_time;
        Stop departure_stop;
        Time departure_time;
        Line line;
        String num_stops;

        public Transit_Detail(Stop arrival_stop, Time arrival_time, Stop departure_stop, Time departure_time, String headsign, Line line, String num_stops) {
            this.arrival_stop = arrival_stop;
            this.arrival_time = arrival_time;
            this.departure_stop = departure_stop;
            this.departure_time = departure_time;
            this.line = line;
            this.num_stops = num_stops;
        }
    }

    class Stop{
        Point locaiton;
        String name;

        public Stop(Point locaiton, String name) {
            this.locaiton = locaiton;
            this.name = name;
        }
    }
    class Line{
        String color;
        String name;
        String short_name;

        public Line(String color, String name, String short_name) {
            this.color = color;
            this.name = name;
            this.short_name = short_name;
        }
    }
}

