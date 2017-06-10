package com.example.junny.followme_realbeta.response;

import android.util.Log;

/**
 * Created by junny on 2017. 6. 10..
 */

public class SearchRes {
    public Channel channel;

    public SearchRes(Channel channel) {
        Log.e("생성이 되긴함","!!");
        this.channel = channel;
    }

    public int getCount(){
        return channel.items.length;
    }

    public String getAddress(int i){
        Item cur_item=channel.items[i];
        if(cur_item.newAddress!=""){
            return cur_item.newAddress;
        }
        else if(cur_item.address!=""){
            return cur_item.address;
        }
        else{return "";}
    }
    public String getTitle(int i){
        return channel.items[i].title;
    }

    class Channel{
        public Item[] items;
        Information info;

        public Channel(Item[] items, Information info) {
            this.items = items;
            this.info = info;
        }
        public String[] getInformation(){
            String[] returnArray=new String[3];
            return returnArray;
        }
    }
    public class Item{
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

        public Item(String address, String addressBCode, String category, String categoryCode, String direction, String distance,
                    String id, String imageUrl, String latitude, String longitude, String newAddress, String phone, String placeUrl,
                    String related_place, String related_place_count, String title, String zipcode) {
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
