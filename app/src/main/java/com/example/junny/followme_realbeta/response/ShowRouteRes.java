package com.example.junny.followme_realbeta.response;

/**
 * Created by junny on 2017. 6. 10..
 */

public class ShowRouteRes {
    WayPoint[] geocoded_waypointes;
    Route[] routes;
    String status;

    public ShowRouteRes(WayPoint[] geocoded_waypointes, Route[] routes, String status) {
        this.geocoded_waypointes = geocoded_waypointes;
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

    class WayPoint{
        String geocoder_status;
        String place_id;
        String types;

        public WayPoint(String geocoder_status, String place_id, String types) {
            this.geocoder_status = geocoder_status;
            this.place_id = place_id;
            this.types = types;
        }
    }
    class Route{
        Bounds bounds;
        String copyrights;
        Legs[] legs;
        Overview_polyline overview_polyline;
        String summary;
        String[] warnings;
        String[] waypoint_order;

        public Route(Bounds bounds, String copyrights, Legs[] legs, Overview_polyline overview_polyline, String summary, String[] warnings, String[] waypoint_order) {
            this.bounds = bounds;
            this.copyrights = copyrights;
            this.legs = legs;
            this.overview_polyline = overview_polyline;
            this.summary = summary;
            this.warnings = warnings;
            this.waypoint_order = waypoint_order;
        }
    }
    class Overview_polyline{
        String points;

        public Overview_polyline(String points) {
            this.points = points;
        }
    }
    class Bounds{
        Point northeast;
        Point southwest;

        public Bounds(Point northeast, Point southwest) {
            this.northeast = northeast;
            this.southwest = southwest;
        }
    }
    class Legs{
        Time arrival_time;
        Time departure_time;
        Text_Value distance;
        Text_Value duration;
        String end_address;
        Point end_location;
        String start_address;
        Point start_location;
        Steps[] steps;
        String[] traffic_speed_entry;
        String[] via_waypoint;

        public Legs(Time arrival_time, Time departure_time, Text_Value distance, Text_Value duration, String end_address, Point end_location, String start_address, Point start_location, Steps[] steps, String[] traffic_speed_entry, String[] via_waypoint) {
            this.arrival_time = arrival_time;
            this.departure_time = departure_time;
            this.distance = distance;
            this.duration = duration;
            this.end_address = end_address;
            this.end_location = end_location;
            this.start_address = start_address;
            this.start_location = start_location;
            this.steps = steps;
            this.traffic_speed_entry = traffic_speed_entry;
            this.via_waypoint = via_waypoint;
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
        Point end_location;
        String html_instructions;
        Polyline polyline;
        Point start_location;
        Little_Steps[] steps;
        Transit_Detail transit_details;
        String travel_mode;

        public Steps(Text_Value distance, Text_Value duration, Point end_location, String html_instructions, Polyline polyline, Point start_location, Little_Steps[] steps, Transit_Detail transit_details, String travel_mode) {
            this.distance = distance;
            this.duration = duration;
            this.end_location = end_location;
            this.html_instructions = html_instructions;
            this.polyline = polyline;
            this.start_location = start_location;
            this.steps = steps;
            this.transit_details = transit_details;
            this.travel_mode = travel_mode;
        }
    }
    class Little_Steps{
        Text_Value distance;
        Text_Value duration;
        Point end_location;
        Polyline polyline;
        Point start_location;
        String travel_mode;

        public Little_Steps(Text_Value distance, Text_Value duration, Point end_location, Polyline polyline, Point start_location, String travel_mode) {
            this.distance = distance;
            this.duration = duration;
            this.end_location = end_location;
            this.polyline = polyline;
            this.start_location = start_location;
            this.travel_mode = travel_mode;
        }
    }

    class Transit_Detail{
        Stop arrival_stop;
        Time arrival_time;
        Stop departure_stop;
        Time departure_time;
        String headsign;
        Line line;
        String num_stops;

        public Transit_Detail(Stop arrival_stop, Time arrival_time, Stop departure_stop, Time departure_time, String headsign, Line line, String num_stops) {
            this.arrival_stop = arrival_stop;
            this.arrival_time = arrival_time;
            this.departure_stop = departure_stop;
            this.departure_time = departure_time;
            this.headsign = headsign;
            this.line = line;
            this.num_stops = num_stops;
        }
    }

    class Polyline{
        String polyline;

        public Polyline(String polyline) {
            this.polyline = polyline;
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
        Agencies[] agencies;
        String color;
        String name;
        String short_name;
        String text_color;
        Vehicle vehicle;

        public Line(Agencies[] agencies, String color, String name, String short_name, String text_color, Vehicle vehicle) {
            this.agencies = agencies;
            this.color = color;
            this.name = name;
            this.short_name = short_name;
            this.text_color = text_color;
            this.vehicle = vehicle;
        }
    }
    class Agencies{
        String name;
        String url;

        public Agencies(String name, String url) {
            this.name = name;
            this.url = url;
        }
    }
    class Vehicle{
        String icon;
        String name;
        String type;

        public Vehicle(String icon, String name, String type) {
            this.icon = icon;
            this.name = name;
            this.type = type;
        }
    }
}

