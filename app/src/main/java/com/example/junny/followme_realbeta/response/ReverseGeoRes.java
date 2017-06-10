package com.example.junny.followme_realbeta.response;

/**
 * Created by junny on 2017. 6. 10..
 */

public class ReverseGeoRes {
    public AddressComponent[] results;
    public String status;

    public ReverseGeoRes(AddressComponent[] results, String status) {
        this.results = results;
        this.status=status;
    }

    public String getAddress(){
        return results[0].getFormatted_address();
    }

    class AddressComponent{
        public Detail[] address_component;
        public String formatted_address;
        public Geometry geometry;
        public String place_id;
        public String[] types;

        public AddressComponent(Detail[] address_component, String formatted_address, Geometry geometry, String place_id, String[] types) {
            this.address_component = address_component;
            this.formatted_address = formatted_address;
            this.geometry = geometry;
            this.place_id = place_id;
//            this.types = types;
        }
        public String getFormatted_address(){
            return formatted_address;
        }
    }
    class Detail{
        public String long_name;
        public String short_name;
        public  String[] types;

        public Detail(String long_name, String short_name, String[] types) {
            this.long_name = long_name;
            this.short_name = short_name;
            this.types = types;
        }
    }
    class Geometry{
        Location location;
        String location_type;
        Viewport viewport;

        public Geometry(Location location, String location_type, Viewport viewport) {
            this.location = location;
            this.location_type = location_type;
            this.viewport = viewport;
        }
    }
    class Location{
        public String lat;
        public String lng;

        public Location(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }
    class Viewport{
        public Location northeast;
        public Location southwest;

        public Viewport(Location northeast, Location southwest) {
            this.northeast = northeast;
            this.southwest = southwest;
        }
    }
}
