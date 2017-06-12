package com.example.junny.followme_realbeta.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.adapter.DetailAdapter;
import com.example.junny.followme_realbeta.interfaces.Search;
import com.example.junny.followme_realbeta.item.detail_item;
import com.example.junny.followme_realbeta.response.SearchRes;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.junny.followme_realbeta.staticValues.daumMapKey;

public class endpoint_detail_activity extends Activity {
    //통신 요소들
    private Retrofit retrofit;

    //리사이클러뷰 요소들
    private RecyclerView recyclerView;
    private DetailAdapter rAdapter;
    private ArrayList<detail_item> detail_items;

    //핸들러
    private android.os.Handler mHandler;

    //액티비티 뷰 요소들
    private TextView top_bar;
    private TextView search_keyword;
    private String keyword;
    private ImageView go_back_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enddetail);

        Intent intent=getIntent();
        keyword=intent.getExtras().getString("keyword");

        mHandler=new Handler();

        search_keyword=(TextView)findViewById(R.id.search_keyword);
        search_keyword.setText(keyword);

        top_bar=(TextView)findViewById(R.id.top_bar);
        top_bar.setText(keyword+" 검색 결과");

        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        rAdapter=new DetailAdapter(endpoint_detail_activity.this);
        rAdapter.setData_list(new ArrayList<detail_item>());

        retrofit=new Retrofit.Builder()
                .baseUrl("https://apis.daum.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        initData();

        recyclerView.setAdapter(rAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        go_back_btn=(ImageView)findViewById(R.id.go_back);
        go_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endpoint_detail_activity.this.onBackPressed();
            }
        });
    }
    @Override
    protected  void onPause(){
        super.onPause();
        finish();
    }

    private void setData(){

    }

    private void keyword_click(View v){
        Intent intent=new Intent(getApplicationContext(),search_endpoint_activity.class);
        intent.putExtra("keyword", keyword);
        startActivity(intent);
    }

    private void initData(){

        Search search_retro=retrofit.create(Search.class);
        Call<SearchRes> call = search_retro.search(daumMapKey, "1",keyword);
        call.enqueue(new Callback<SearchRes>() {
            @Override
            public void onResponse(Call<SearchRes> call, Response<SearchRes> response) {
                if(response.isSuccessful()){
                    SearchRes res=response.body();
                    Log.e("요청 보기",response.toString());
                    try{
                        detail_items=new ArrayList<detail_item>();
                        Log.e("아이템 개수", Integer.toString(res.getCount()));
                        for(int i=0;i<res.getCount();i++){
                            String item_address=res.getAddress(i);
                            String item_title=res.getTitle(i);
                            String phone=res.getPhone(i);
                            String longitude=res.getLongitude(i);
                            String latitude=res.getLatitude(i);
                            String[] temp_cate=res.getCategory(i).split(">");
                            String last_parse=temp_cate[temp_cate.length-1];
                            if(last_parse.length()>7){
                                last_parse=last_parse.substring(0,7)+"...";
                            }
                            String category=last_parse;
                            detail_items.add(new detail_item(item_title,item_address,latitude,longitude,phone,category));
                        }
                        rAdapter.setData_list(detail_items);
                        rAdapter.notifyDataSetChanged();
                    }
                    catch(Exception e){
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String exceptionAsStrting = sw.toString();
                        Log.e("예외발생", exceptionAsStrting);
                    }

                }
                else{
                    Log.e("에러 메세지", response.toString());
                }
            }

            @Override
            public void onFailure(Call<SearchRes> call, Throwable t) {

            }
        });
    }
}
