package com.example.junny.followme_realbeta.item;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by junny on 2017. 6. 15..
 */

public class TourAttraction {
    private LatLng tour_latlng;
    private Location tour_location;
    private String title;
    private String category;
    private String description;
    private String radius;
    private boolean visible;

    public TourAttraction(LatLng tour_latlng, Location tour_location, String[] extra_info,String radius) {
        this.tour_latlng = tour_latlng;
        this.tour_location = tour_location;
        this.title = extra_info[0];
        this.description = extra_info[1];
        this.category = extra_info[2];
        this.radius=radius;
        this.visible=false;
    }

    public LatLng getPoint(){
        return tour_latlng;
    }
    public Location getLocation(){
        return tour_location;
    }
    public String getTitle(){
        return title;
    }
    public String[] getExtra(){
        String[] res = new String[3];
        res[0]=title;
        res[1]=category;
        res[2]=description;
        return res;
    }
    public boolean isVisible(){
        return visible;
    }
    public void setVisible(boolean visible){
        this.visible=visible;
    }
    public float getRadius(){
        return Float.parseFloat(radius);
    }
}
