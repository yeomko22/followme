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

    public TourAttraction(LatLng tour_latlng, Location tour_location, String[] extra_info) {
        this.tour_latlng = tour_latlng;
        this.tour_location = tour_location;
        this.title = extra_info[0];
        this.description = extra_info[1];
        this.category = extra_info[2];
    }
}
