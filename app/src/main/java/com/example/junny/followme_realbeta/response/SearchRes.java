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

        public Channel(Item[] item) {
            this.item = item;
        }
    }

    class Item{
        String address;
        String category;
        String latitude;
        String longitude;
        String newAddress;
        String phone;
        String title;

        public Item(String address, String category, String latitude, String longitude, String newAddress, String phone, String title) {
            this.address = address;
            this.category = category;
            this.latitude = latitude;
            this.longitude = longitude;
            this.newAddress = newAddress;
            this.phone = phone;
            this.title = title;
        }
    }
}

