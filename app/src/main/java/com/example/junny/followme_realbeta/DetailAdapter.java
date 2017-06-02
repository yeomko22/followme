package com.example.junny.followme_realbeta;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.attr.version;

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
                staticValues.to_lat=detail_item.getLatitude();
                staticValues.to_long=detail_item.getLongitude();
                staticValues.to_title=detail_item.getTitle();
                if(staticValues.dbHelper==null){
                    staticValues.dbHelper=new DBHelper(mContext, "history", null, version);
                }
                staticValues.dbHelper.insert(detail_item.getTitle(), detail_item.getNewAddress(), detail_item.getLatitude(), detail_item.getLongitude());
                Log.e("나 눌렸잖아요 뱀~","11");
                mContext.startActivity(intent);
                //여기서 이루어져야 할 것 - 로컬 디비 검색결과 추가
                //다음 액티비티에 눌린 놈의 타이틀, 경도 위도 좌표값 던져주기
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