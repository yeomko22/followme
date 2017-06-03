package com.example.junny.followme_realbeta.activity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.junny.followme_realbeta.item.DBHelper;
import com.example.junny.followme_realbeta.adapter.MyRecyclerAdapter;
import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.item.search_item;
import com.example.junny.followme_realbeta.staticValues;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

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

import static android.R.attr.version;

public class search_endpoint_activity extends FragmentActivity implements GoogleApiClient.OnConnectionFailedListener {
    private RecyclerView recyclerView;
    private EditText search_window;
    private HttpURLConnection conn;
    private String cur_text;
    private MyRecyclerAdapter rAdapter;
    private ArrayList<search_item> search_items;
    private android.os.Handler mHandler;
    private DBHelper dbHelper;
    private LinearLayout delete_record;
    private TextView top_bar;
    private SharedPreferences pref;
    private GoogleApiClient mGoogleApiClient;
    private LatLng northeast;
    private LatLng southwest;
    private LatLngBounds korea_bounds;
    private AutocompleteFilter filter;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_endpoint);


        //디비 헬퍼 객체 생성, 이를 통해 로컬 디비 관리, 스태틱 메모리에 할당 액티비티 간 공유
        if(staticValues.dbHelper==null){
            staticValues.dbHelper=new DBHelper(getApplicationContext(), "history", null, version);
        }
        this.dbHelper=staticValues.dbHelper;

        //셰어드 프리퍼런스 사용, 로컬 디비 생성되었는지 확인, 생성 안되어있으면 생성하고, 셰어드 값 변경
        pref=getSharedPreferences("pref", MODE_PRIVATE);

        if(pref.getString("localdb_ini","").equals("")){
            dbHelper.createTable();
            SharedPreferences.Editor editor =pref.edit();
            editor.putString("localdb_ini","initialized");
            editor.commit();
        }
        else{
            Log.e("셰어드 인식","11");
        }

        delete_record=(LinearLayout)findViewById(R.id.delete_record);
        top_bar=(TextView)findViewById(R.id.top_bar);

        mHandler=new android.os.Handler();
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        rAdapter=new MyRecyclerAdapter(search_endpoint_activity.this);

        setHistory();

        recyclerView.setAdapter(rAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        northeast=new LatLng(39.5562517,131.6064755);
        southwest=new LatLng(32.5668292,125.2783505);
        korea_bounds=new LatLngBounds(southwest, northeast);
        filter=new AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_NONE).build();

        initData();
    }

    @Override
    protected void onResume() {
        if(mGoogleApiClient==null){
            mGoogleApiClient=new GoogleApiClient
                    .Builder(this)
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(this,this)
                    .build();
        }
        super.onResume();
    }

    private void setHistory(){
        ArrayList<search_item> sample=new ArrayList<search_item>();
        Cursor cursor=dbHelper.select_reverse();

        while(cursor.moveToNext()){
            sample.add(new search_item("item",cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4)));
        }

        if(sample.size()==0){
            sample.add(new search_item("norecord","최근 검색 기록이 없습니다","","",""));
        }
        delete_record.setClickable(true);
        delete_record.setVisibility(View.VISIBLE);
        top_bar.setText("최근 도착지 검색 기록");
        rAdapter.setData_list(sample);
        rAdapter.notifyDataSetChanged();
    }

    public void delete_history(View v){
        dbHelper.delete_history();
        setHistory();
    }

    private void initData(){{

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        search_window=(EditText)findViewById(R.id.search_window);
        search_window.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String new_text=search_window.getText().toString().toLowerCase();

                if(new_text.equals("")){
                    Log.e("비어있는거 인식","11");
                    setHistory();
                    return;
                }
                else if(new_text.length()<2){
                    cur_text=new_text;
                    Log.e("필터","텍스트 길이 작음");
                    return;
                }
                else if((cur_text!=null)&&(cur_text.equals(new_text))){
                    cur_text=new_text;
                    Log.e("필터","동일 텍스트 넘어옴");
                    return;
                }

                else{
                    cur_text=new_text;
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try{
//                                구글 자동완성 요청, 웹과 마찬가지로 다섯개까지 밖에는 제공하지 않는다
//                                Log.e("1", "1");
//                                PendingResult<AutocompletePredictionBuffer> result
//                                        =Places.GeoDataApi.getAutocompletePredictions(mGoogleApiClient, cur_text, korea_bounds, filter);
//                                result.then(new ResultTransform<AutocompletePredictionBuffer, Result>() {
//                                    @Nullable
//                                    @Override
//                                    public PendingResult<Result> onSuccess(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
//                                        Log.e("결과 개수", Integer.toString(autocompletePredictions.getCount()));
//                                        for(int i=0;i<autocompletePredictions.getCount();i++){
//                                            String a=autocompletePredictions.get(i).getPrimaryText(new CharacterStyle() {
//                                                @Override
//                                                public void updateDrawState(TextPaint tp) {
//
//                                                }
//                                            }).toString();
//                                            Log.e("검색 결과 값", a);
//                                        }
//                                        return null;
//                                    }
//                                });


                                //다음 로컬 api를 사용https://apis.daum.net/local/v1/search/keyword.json?apikey=5a3b393c51ad7571d6a92599bd57a77e&query=%ED%99%8D%EB%8C%80
                                String str_url="https://apis.daum.net/local/v1/search/keyword.json?apikey=00b029ef729c6020abe2c0fe859eb77f&sort=1&query="+ URLEncoder.encode(cur_text,"UTF-8");

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
                                String jsonString=sb.toString();
                                JSONObject totalObject=new JSONObject(jsonString);
                                JSONObject itemObject=new JSONObject(totalObject.getString("channel"));
                                JSONArray jsonArray=new JSONArray(itemObject.getString("item"));

                                search_items=new ArrayList<search_item>();
                                for(int i=0;i<jsonArray.length();i++){
                                    JSONObject cur_obj=jsonArray.getJSONObject(i);
                                    String added_address;
                                    if(cur_obj.getString("newAddress").equals("")){
                                        added_address=cur_obj.getString("address");
                                    }
                                    else{
                                        added_address=cur_obj.getString("newAddress");
                                    }
                                    search_items.add(new search_item("item",cur_obj.getString("title"),added_address,"",""));
                                }
                                rAdapter.setData_list(search_items);
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        rAdapter.notifyDataSetChanged();
                                        delete_record.setClickable(false);
                                        delete_record.setVisibility(View.INVISIBLE);
                                        top_bar.setText(cur_text+" 검색 결과");
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
        });
    }
    }
}
