package com.example.junny.followme_realbeta.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.junny.followme_realbeta.item.detail_item;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class endpoint_detail_activity extends Activity {
    //통신 요소들
    private HttpURLConnection conn;

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

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{

                    //다음 로컬 api를 사용https://apis.daum.net/local/v1/search/keyword.json?apikey=5a3b393c51ad7571d6a92599bd57a77e&query=%ED%99%8D%EB%8C%80
                    String str_url="https://apis.daum.net/local/v1/search/keyword.json?apikey=00b029ef729c6020abe2c0fe859eb77f&sort=1&query="+ URLEncoder.encode(keyword,"UTF-8");

                    URL url=new URL(str_url);

                    conn=(HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(1000);
                    conn.connect();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    StringBuilder sb=new StringBuilder();
                    String line=null;
                    while((line=br.readLine())!=null){
                        sb.append(line);
                    }
                    Log.e("넘어온 JSON", sb.toString());
                    String jsonString=sb.toString();
                    JSONObject totalObject=new JSONObject(jsonString);
                    JSONObject itemObject=new JSONObject(totalObject.getString("channel"));
                    JSONArray jsonArray=new JSONArray(itemObject.getString("item"));

                    detail_items=new ArrayList<detail_item>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject cur_obj=jsonArray.getJSONObject(i);
                        String added_address;
                        String temp_title=cur_obj.getString("title");
                        if(temp_title.length()>14){
                            temp_title=temp_title.substring(0,14)+"...";
                        }
                        String title=temp_title;
                        String phone=cur_obj.getString("phone");
                        String longitude=cur_obj.getString("longitude");
                        String latitude=cur_obj.getString("latitude");
                        String[] temp_cate=cur_obj.getString("category").split(">");
                        String last_parse=temp_cate[temp_cate.length-1];
                        if(last_parse.length()>7){
                            last_parse=last_parse.substring(0,7)+"...";
                        }
                        String category=last_parse;

                        if(cur_obj.getString("newAddress").equals("")){
                            added_address=cur_obj.getString("address");
                        }
                        else{
                            added_address=cur_obj.getString("newAddress");
                        }
                        detail_items.add(new detail_item(title,added_address,latitude,longitude,phone,category));
                    }
                    rAdapter.setData_list(detail_items);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            rAdapter.notifyDataSetChanged();
                            return;
                        }
                    });
                }
                catch(Exception e){
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exceptionAsStrting = sw.toString();
                    Log.e("예외발생", exceptionAsStrting);
                }
                finally {
                    if(conn!=null){
                        conn.disconnect();
                    }
                    return;
                }
            }
        });
    }
}
