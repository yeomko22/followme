package com.example.junny.followme_realbeta.response;

/**
 * Created by junny on 2017. 6. 10..
 */

public class SearchRes {
    public Channel channel;

    public SearchRes(Channel channel) {
        this.channel = channel;
    }
    public int getCount(){
        return channel.item.length;
    }
    public String getAddress(int i){
        Item cur_item=channel.item[i];
        if(cur_item.newAddress!=""){
            return cur_item.newAddress;
        }
        else if(cur_item.address!=""){
            return cur_item.address;
        }
        else{return "";}
    }
    public String getTitle(int i){
        return channel.item[i].title;
    }

    public String getPhone(int i){
        return channel.item[i].phone;
    }
    public String getLatitude(int i){
        return channel.item[i].latitude;
    }
    public String getLongitude(int i){
        return channel.item[i].longitude;
    }
    public String getCategory(int i){
        return channel.item[i].category;
    }

    class Channel{
        public Item[] item;
        public Information info;

        public Channel(Item[] item, Information info) {
            this.item = item;
            this.info = info;
        }
    }

    class Item{
        String address;
        String addressBCode;
        String category;
        String categoryCode;
        String direction;
        String distance;
        String id;
        String imageUrl;
        String latitude;
        String longitude;
        String newAddress;
        String phone;
        String placeUrl;
        String related_place;
        String related_place_count;
        String title;
        String zipcode;

        public Item(String address, String addressBCode, String category, String categoryCode, String direction, String distance, String id, String imageUrl, String latitude, String longitude, String newAddress, String phone, String placeUrl, String related_place, String related_place_count, String title, String zipcode) {
            this.address = address;
            this.addressBCode = addressBCode;
            this.category = category;
            this.categoryCode = categoryCode;
            this.direction = direction;
            this.distance = distance;
            this.id = id;
            this.imageUrl = imageUrl;
            this.latitude = latitude;
            this.longitude = longitude;
            this.newAddress = newAddress;
            this.phone = phone;
            this.placeUrl = placeUrl;
            this.related_place = related_place;
            this.related_place_count = related_place_count;
            this.title = title;
            this.zipcode = zipcode;
        }
    }
    class Information{
        Samename samename;
        String count;
        String page;
        String totalCount;

        public Information(Samename samename, String count, String page, String totalCount) {
            this.samename = samename;
            this.count = count;
            this.page = page;
            this.totalCount = totalCount;
        }
    }
    class Samename{
        String keyword;
        String selected_region;

        public Samename(String keyword, String selected_region) {
            this.keyword = keyword;
            this.selected_region = selected_region;
        }
    }
}

