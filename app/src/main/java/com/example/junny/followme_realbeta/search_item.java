package com.example.junny.followme_realbeta;

/**
 * Created by junny on 2017. 5. 25..
 */

public class search_item {
    private String type;
    private String title;
    private String latitude;
    private String longitude;
    private String newAddress;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
    }

    public search_item(String type, String title, String newAddress, String latitude, String longitude) {

        this.type = type;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.newAddress = newAddress;
    }
}
