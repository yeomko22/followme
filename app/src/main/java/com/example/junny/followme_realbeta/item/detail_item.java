package com.example.junny.followme_realbeta.item;

/**
 * Created by junny on 2017. 5. 25..
 */

public class detail_item {
    private String title;
    private String newAddress;
    private String latitude;
    private String longitude;
    private String phone;
    private String category;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNewAddress() {
        return newAddress;
    }

    public void setNewAddress(String newAddress) {
        this.newAddress = newAddress;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public detail_item(String title, String newAddress, String latitude, String longitude, String phone, String category) {

        this.title = title;
        this.newAddress = newAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.phone = phone;
        this.category = category;
    }
}
