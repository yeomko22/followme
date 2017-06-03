package com.example.junny.followme_realbeta.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junny.followme_realbeta.item.DBHelper;
import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.activity.endpoint_detail_activity;
import com.example.junny.followme_realbeta.item.search_item;

import java.util.ArrayList;

import static android.R.attr.version;

/**
 * Created by junny on 2017. 5. 23..
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>{
    private ArrayList<search_item> data_list;
    private Context mContext;
    private DBHelper dbHelper;

    public MyRecyclerAdapter(Context context) {
        mContext=context;
        dbHelper=new DBHelper(mContext, "history", null, version);
    }

    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_history,viewGroup,false);
        return new ViewHolder(view);
    }
    public void onBindViewHolder(final ViewHolder viewHolder, int position){
        final search_item search_item=data_list.get(position);

        viewHolder.data_title.setText(search_item.getTitle());
        viewHolder.data_newAddress.setText(search_item.getNewAddress());
        if((search_item.getType().equals("item"))){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, endpoint_detail_activity.class);
                    intent.putExtra("keyword", search_item.getTitle());
                    mContext.startActivity(intent);
                }
            });
        }
        else if((search_item.getType().equals("history"))){
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext,"hi",Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public int getItemCount(){
        return data_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView data_newAddress;
        public TextView data_title;
        public ViewHolder(View itemView) {
            super(itemView);
            data_newAddress=(TextView)itemView.findViewById(R.id.data_newAddress);
            data_title=(TextView)itemView.findViewById(R.id.data_title);
        }
    }
    public void setData_list(ArrayList<search_item> list){
        this.data_list=list;
    }
}