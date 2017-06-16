package com.example.junny.followme_realbeta.response;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by junny on 2017. 6. 10..
 */

public class NearTourRes {

    Attraction[] attractions;

    public NearTourRes(Attraction[] attractions) {
        this.attractions = attractions;
    }

    public int getCount(){
        return attractions.length;
    }
    public String[] getExtra(int i){
        String[] res=new String[3];
        res[0]=attractions[i].title;
        res[1]=attractions[i].description;
        res[2]=attractions[i].category;
        return res;
    }
    public String getTitle(int i){
        return attractions[i].title;
    }

    public LatLng getPoint(int i){
        return new LatLng(Double.parseDouble(attractions[i].lati), Double.parseDouble(attractions[i].lngi));
    }
    public Location getLocation(int i){
        Location return_location= new Location("res");
        return_location.setLatitude(Double.parseDouble(attractions[i].lati));
        return_location.setLongitude(Double.parseDouble(attractions[i].lngi));
        return return_location;
    }

    class Attraction{
        String title;
        String description;
        String lati;
        String lngi;
        String category;
        String Radius;

        public Attraction(String title, String description, String lati, String lngi, String category, String radius) {
            this.title = title;
            this.description = description;
            this.lati = lati;
            this.lngi = lngi;
            this.category = category;
            Radius = radius;
        }
    }
}


