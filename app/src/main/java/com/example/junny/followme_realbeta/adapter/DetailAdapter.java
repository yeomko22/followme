package com.example.junny.followme_realbeta.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.activity.show_route_activity;
import com.example.junny.followme_realbeta.item.DBHelper;
import com.example.junny.followme_realbeta.item.detail_item;
import com.example.junny.followme_realbeta.staticValues;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static android.R.attr.version;
import static com.example.junny.followme_realbeta.staticValues.mLastLat;
import static com.example.junny.followme_realbeta.staticValues.mLastLong;
import static com.example.junny.followme_realbeta.staticValues.to_lat;
import static com.example.junny.followme_realbeta.staticValues.to_location;
import static com.example.junny.followme_realbeta.staticValues.to_long;

/**
 * Created by junny on 2017. 5. 23..
 */

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder>{
    private ArrayList<detail_item> detail_items;
    private Context mContext;

    public DetailAdapter(Context context) {
        mContext=context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.detail_item,viewGroup,false);
        return new ViewHolder(view);
    }
    public void onBindViewHolder(ViewHolder viewHolder, int position){
        final detail_item detail_item=detail_items.get(position);

        viewHolder.data_title.setText(detail_item.getTitle());
        viewHolder.data_category.setText(detail_item.getCategory());
        viewHolder.data_newAddress.setText(detail_item.getNewAddress());
        viewHolder.data_phone.setText(detail_item.getPhone());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, show_route_activity.class);
                staticValues.to_lat=Double.parseDouble(detail_item.getLatitude());
                staticValues.to_long=Double.parseDouble(detail_item.getLongitude());
                staticValues.to_latlng=new LatLng(staticValues.to_lat,staticValues.to_long);
                staticValues.to_title=detail_item.getTitle();

                to_location=new Location("to");
                to_location.setLatitude(staticValues.to_lat);
                to_location.setLongitude(to_long);

                staticValues.distance=staticValues.mLastLocation.distanceTo(to_location);
                staticValues.middle_point=new LatLng(((mLastLat+to_lat)/2.0),((mLastLong+to_long)/2.0));

                if(staticValues.dbHelper==null){
                    staticValues.dbHelper=new DBHelper(mContext, "history", null, version);
                }
                staticValues.dbHelper.insert(detail_item.getTitle(), detail_item.getNewAddress(), detail_item.getLatitude(), detail_item.getLongitude());
                mContext.startActivity(intent);
            }
        });
    }
    public int getItemCount(){
        return detail_items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView data_newAddress;
        public TextView data_title;
        public TextView data_category;
        public TextView data_phone;
        public ViewHolder(View itemView) {
            super(itemView);
            data_newAddress=(TextView)itemView.findViewById(R.id.detail_address);
            data_title=(TextView)itemView.findViewById(R.id.detail_title);
            data_category=(TextView)itemView.findViewById(R.id.detail_category);
            data_phone=(TextView)itemView.findViewById(R.id.detail_phone);
        }
    }
    public void setData_list(ArrayList<detail_item> list){
        this.detail_items=list;
    }
}