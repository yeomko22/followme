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
        return results[0].formatted_address;
    }

    class AddressComponent{
        public String formatted_address;

        public AddressComponent(String formatted_address) {
            this.formatted_address = formatted_address;
        }
    }
}
