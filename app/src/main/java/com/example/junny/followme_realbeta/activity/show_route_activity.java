package com.example.junny.followme_realbeta.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.interfaces.ShowRoute;
import com.example.junny.followme_realbeta.interfaces.ShowWalk;
import com.example.junny.followme_realbeta.response.ShowRouteRes;
import com.example.junny.followme_realbeta.response.ShowWalkRes;
import com.example.junny.followme_realbeta.staticValues;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.junny.followme_realbeta.R.id.start_point;
import static com.example.junny.followme_realbeta.staticValues.*;

public class show_route_activity extends FragmentActivity implements OnMapReadyCallback {
    //상단 뷰 요소들
    private TextView start_point_view;
    private TextView end_point_view;
    private ImageView bus_image;
    private ImageView walk_image;

    //하단 뷰 요소들
    private TextView bottom_type;
    private TextView bottom_time;
    private TextView bottom_distance;
    private LinearLayout bottom_container;
    private LinearLayout bottom_container2;

    private String cur_mode="bus";

    //구글 맵 변수들
    private GoogleMap show_Map;

    //라인 그려주는 변수들
    private Resources resources;
    private String last_stop;
    private boolean contain_transit;

    private PolylineOptions walkOptions;

    private Retrofit retro_transit;
    private Retrofit retro_walk;

    private boolean poly_start_set=true;
    private boolean poly_end_set=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_route_activity);

        resources=getResources();
        retro_transit=new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retro_walk=new Retrofit.Builder()
                .baseUrl("https://apis.skplanetx.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        start_point_view=(TextView)findViewById(start_point);
        end_point_view=(TextView)findViewById(R.id.end_point);
        bus_image=(ImageView)findViewById(R.id.transport_bus);
        walk_image=(ImageView)findViewById(R.id.transport_walk);

        bottom_container=(LinearLayout)findViewById(R.id.show_route_stepcontainer);
        bottom_container2=(LinearLayout)findViewById(R.id.show_route_stepcontainer2);

        bottom_type=(TextView)findViewById(R.id.show_route_type);
        bottom_time=(TextView)findViewById(R.id.show_route_time);
        bottom_distance=(TextView)findViewById(R.id.show_route_distance);


        SupportMapFragment showmapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.show_map);
        showmapFragment.getMapAsync(this);

        if (staticValues.cur_address.length()>8){
            start_point_view.setText(staticValues.cur_address.substring(0,8)+"...");
        }
        else{
            start_point_view.setText(staticValues.cur_address);
        }

        if(to_title.length()>8){
            end_point_view.setText(to_title.substring(0,8)+"...");
        }
        else{end_point_view.setText(to_title);}
        getTransit();
    }

    public void onMapReady(GoogleMap googleMap) {
        show_Map = googleMap;
        set_zoom();
    }

    public void set_zoom(){
        if(staticValues.distance<100){
            Log.e("직선 거리","100 미만");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,19));
        }
        else if(staticValues.distance<300){
            Log.e("직선 거리","300 미만");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,17));
        }

        else if(staticValues.distance<500){
            Log.e("직선 거리","500 미만");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,16));
        }

        else if(staticValues.distance<1000){
            Log.e("직선 거리","1000 미만");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,15));
        }

        else if(staticValues.distance<2000){
            Log.e("직선 거리","2000 미만");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,14));
        }
        else if(staticValues.distance<3500){
            Log.e("직선 거리","3500 미만");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,13));
        }
        else if(staticValues.distance<7500){
            Log.e("직선 거리","7500 미만");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,12));
        }
        else if(staticValues.distance<15000){
            Log.e("직선 거리","15000 미만");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,11));
        }
        else if(staticValues.distance<30000){
            Log.e("직선 거리","30000 미만");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,10));
        }
        else if(staticValues.distance<40000){
            Log.e("직선 거리","40000 미만");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,9));
        }
        else if(staticValues.distance<80000){
            Log.e("직선 거리","80000 미만");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,8));
        }
        else{
            Log.e("직선 거리","그 외");
            show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,7));
        }
    }
    public void replace(View v){
        switch(v.getId()){
            case R.id.transport_bus :
                if(cur_mode.equals("bus")){
                    break;
                }

                getTransit();
                break;
            case R.id.transport_walk :
                if(cur_mode.equals("walk")){
                    break;
                }
                getWalk();
                break;
        }
    }
    public void getTransit(){
        //현재 모드 바꿔주기
        bus_image.setImageResource(R.drawable.bus_active);
        walk_image.setImageResource(R.drawable.walk_passive);
        cur_mode="bus";
        bottom_type.setText("대중교통");
        if(show_Map!=null){
            show_Map.clear();
        }

        ShowRoute retro_showRoute=retro_transit.create(ShowRoute.class);
        Call<ShowRouteRes> call = retro_showRoute.showRoute(gRouteKey,"ko","transit",mLastLat+","+mLastLong,to_lat+","+to_long);
        call.enqueue(new Callback<ShowRouteRes>() {
            @Override
            public void onResponse(Call<ShowRouteRes> call, Response<ShowRouteRes> response) {
                if(response.isSuccessful()){
                    ShowRouteRes res = response.body();
                    Log.e("요청 보기",response.toString());
                    try{
                        final List<LatLng> polylines=decodePoly(res.get_poly());
                        final PolylineOptions rectOptions = new PolylineOptions();

                        transit_all_latlng=new ArrayList<LatLng>();
                        transit_guide_latlng=new ArrayList<LatLng>();
                        transit_guide_text=new ArrayList<String>();

                        for(int i=0;i<polylines.size();i++){
                            //대중교통 경로는 다 들어가 있는 것
                            transit_all_latlng.add(polylines.get(i));
                        }
                        //지시문, 주요 포인트 저장
                        for(int i=0;i<res.get_legs_count();i++){
                            transit_guide_text.add(res.get_description(i));
                            String[] str_latlng=res.get_start_location(i);
                            transit_guide_latlng.add(new LatLng(Double.parseDouble(str_latlng[0]),Double.parseDouble(str_latlng[1])));
                        }

                        //현재 지점부터 출발 지점까지의 경로 구하기
                        String[] start_end_type=res.get_start_end_type();
                        if(start_end_type[0].equals("WALKING")){
                            poly_start_set=false;
                            String[] startpoint=res.get_start_latlng();
                            ShowWalk walkRes=retro_walk.create(ShowWalk.class);
                            Call<ShowWalkRes> walk_call = walkRes.showWalk(tMapKey,startpoint[1],startpoint[0],startpoint[3],startpoint[2],
                                    "start","end","WGS84GEO","WGS84GEO");
                            walk_call.enqueue(new Callback<ShowWalkRes>() {
                                @Override
                                public void onResponse(Call<ShowWalkRes> call, Response<ShowWalkRes> response) {
                                    ShowWalkRes res=response.body();
                                    ArrayList<LatLng> tran_walk_guide_latlng=new ArrayList<LatLng>();
                                    ArrayList<LatLng> tran_walk_all_latlng=new ArrayList<LatLng>();
                                    ArrayList<String> tran_walk_guide_text=new ArrayList<String>();
                                    for(int i=0;i<res.get_count();i++){
                                        if(res.get_type(i).equals("Point")){
                                            LatLng added_point=new LatLng(Double.parseDouble(res.get_point_lat(i)),Double.parseDouble(res.get_point_lng(i)));
                                            tran_walk_guide_latlng.add(added_point);
                                            tran_walk_guide_text.add(res.get_description(i));
                                            tran_walk_all_latlng.add(added_point);
                                        }
                                        else{
                                            for(int j=0;j<res.get_coor_count(i);j++){
                                                String[] str_latlng=res.get_coor_lat(i,j);
                                                LatLng added_point=new LatLng(Double.parseDouble(str_latlng[0]),Double.parseDouble(str_latlng[1]));
                                                tran_walk_all_latlng.add(added_point);
                                            }
                                        }
                                    }
                                    // 도보 경로 모아놓은 곳 뒤쪽에 대중교통 경로를 붙여준다 그리고 이걸 다시 대중교통 전체 경로로 지정한다
                                    //먼저 전체 경로 합치기, 합치기 전에 맨 앞 포인트 제거
                                    transit_all_latlng.remove(0);
                                    tran_walk_all_latlng.addAll(transit_all_latlng);
                                    transit_all_latlng=tran_walk_all_latlng;
                                    //다음 주요 포인트 합치기
                                    transit_guide_latlng.remove(0);
                                    tran_walk_guide_latlng.addAll(transit_guide_latlng);
                                    transit_guide_latlng=tran_walk_guide_latlng;
                                    //다음 텍스트 합치기
                                    transit_guide_text.remove(0);
                                    tran_walk_guide_text.addAll(transit_guide_text);
                                    transit_guide_text=tran_walk_guide_text;

                                    //이 작업이 다 끝났으면 지도 위에 그려주기\
                                    poly_start_set=true;
                                    if(poly_start_set&&poly_end_set){
                                        for(int i=0;i<transit_all_latlng.size();i++){
                                            rectOptions.add(transit_all_latlng.get(i));
                                        }
                                        show_Map.addMarker(new MarkerOptions().position(staticValues.mLastLatLong).title("내 위치"));
                                        show_Map.addMarker(new MarkerOptions().position(staticValues.to_latlng).title("목표지점"));
                                        staticValues.cur_poly=rectOptions;
                                        show_Map.addPolyline(rectOptions);
                                    }
                                    else{
                                        Log.e("첫부분 앞 뒤 경로가 설정이 안됨","11");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ShowWalkRes> call, Throwable t) {

                                }
                            });
                        }

                        if(start_end_type[1].equals("WALKING")){
                            poly_end_set=false;
                            String[] endpoint=res.get_end_latlng();
                            ShowWalk walkRes=retro_walk.create(ShowWalk.class);
                            Call<ShowWalkRes> walk_call = walkRes.showWalk(tMapKey,endpoint[1],endpoint[0],endpoint[3],endpoint[2],
                                    "start","end","WGS84GEO","WGS84GEO");
                            walk_call.enqueue(new Callback<ShowWalkRes>() {
                                @Override
                                public void onResponse(Call<ShowWalkRes> call, Response<ShowWalkRes> response) {
                                    Log.e("요청보기",response.toString());

                                    ShowWalkRes res=response.body();
                                    ArrayList<LatLng> tran_walk_guide_latlng=new ArrayList<LatLng>();
                                    ArrayList<LatLng> tran_walk_all_latlng=new ArrayList<LatLng>();
                                    ArrayList<String> tran_walk_guide_text=new ArrayList<String>();
                                    for(int i=0;i<res.get_count();i++){
                                        if(res.get_type(i).equals("Point")){
                                            LatLng added_point=new LatLng(Double.parseDouble(res.get_point_lat(i)),Double.parseDouble(res.get_point_lng(i)));
                                            tran_walk_guide_latlng.add(added_point);
                                            tran_walk_guide_text.add(res.get_description(i));
                                            tran_walk_all_latlng.add(added_point);
                                        }
                                        else{
                                            for(int j=0;j<res.get_coor_count(i);j++){
                                                String[] str_latlng=res.get_coor_lat(i,j);
                                                LatLng added_point=new LatLng(Double.parseDouble(str_latlng[0]),Double.parseDouble(str_latlng[1]));
                                                tran_walk_all_latlng.add(added_point);
                                            }
                                        }
                                    }
                                    // 전체 경로 쌓아놓은 것에 뒤쪽에 나머지를 붙여준다
                                    //먼저 전체 경로 합치기, 합치기 전에 맨 뒷 포인트 제거
                                    transit_all_latlng.remove(transit_all_latlng.size()-1);
                                    transit_all_latlng.addAll(tran_walk_all_latlng);

                                    //다음 주요 포인트 합치기
                                    transit_guide_latlng.remove(transit_guide_latlng.size()-1);
                                    transit_guide_latlng.addAll(tran_walk_guide_latlng);

                                    //다음 텍스트 합치기
                                    transit_guide_text.remove(transit_guide_text.size()-1);
                                    transit_guide_text.addAll(tran_walk_guide_text);

                                    //이 작업이 다 끝났으면 지도 위에 그려주기
                                    poly_end_set=true;
                                    if(poly_start_set&&poly_end_set){
                                        for(int i=0;i<transit_all_latlng.size();i++){
                                            rectOptions.add(transit_all_latlng.get(i));
                                        }
                                        show_Map.addMarker(new MarkerOptions().position(staticValues.mLastLatLong).title("내 위치"));
                                        show_Map.addMarker(new MarkerOptions().position(staticValues.to_latlng).title("목표지점"));
                                        staticValues.cur_poly=rectOptions;
                                        show_Map.addPolyline(rectOptions);
                                    }
                                    else{
                                        Log.e("뒷 부분 경로가 설정이 안됨","11");
                                    }
                                }

                                @Override
                                public void onFailure(Call<ShowWalkRes> call, Throwable t) {

                                }
                            });
                        }

                        final ArrayList<String> line_num_list=new ArrayList<String>();
                        final ArrayList<String> line_name_list=new ArrayList<String>();
                        int walk_time=0;
                        for(int i=0;i<res.get_legs_count();i++) {
                            if (res.get_type(i).equals("TRANSIT")) {
                                contain_transit = true;
                                String transit_name = res.get_line(i);
                                if (transit_name.contains("호선")) {
                                    line_num_list.add(transit_name.substring(0, 1));
                                } else {
                                    line_num_list.add(transit_name);
                                }

                                line_name_list.add(res.get_start_name(i).split(" ")[0]);
                                last_stop = res.get_stop_name(i);
                            } else {
                                walk_time += Integer.parseInt(res.get_walk_time(i));
                            }
                        }

                        if(!contain_transit){
                            Toast.makeText(show_route_activity.this,"가까운 거리는 도보 경로를 이용해주세요", Toast.LENGTH_LONG).show();
                            getWalk();
                            return;
                        }

                        bottom_distance.setText(res.get_total_distance());
                        bottom_time.setText(res.get_total_time());

                        LayoutInflater inflater=(LayoutInflater)show_route_activity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        for(int i=0;i<line_num_list.size();i++){
                            LinearLayout added_item=(LinearLayout) inflater.inflate(R.layout.line_background,null);
                            ((TextView) added_item.findViewById(R.id.num)).setText(line_num_list.get(i));
                            ((TextView) added_item.findViewById(R.id.name)).setText(line_name_list.get(i));
                            switch (line_num_list.get(i)){
                                        case "1":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(resources.getDrawable(R.drawable.line_1));
                                            break;
                                        case "2":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(resources.getDrawable(R.drawable.line_2));
                                            break;
                                        case "3":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(resources.getDrawable(R.drawable.line_3));
                                            break;
                                        case "4":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(resources.getDrawable(R.drawable.line_4));
                                            break;
                                        case "5":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(resources.getDrawable(R.drawable.line_5));
                                            break;
                                        case "6":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(resources.getDrawable(R.drawable.line_6));
                                            break;
                                        case "7":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(resources.getDrawable(R.drawable.line_7));
                                            break;
                                        case "8":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(resources.getDrawable(R.drawable.line_8));
                                            break;
                                        case "9":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(resources.getDrawable(R.drawable.line_9));
                                            break;
                                        default:
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(resources.getDrawable(R.drawable.line_bus));
                                            break;
                                    }
                                    if(i<2){
                                        bottom_container.addView(added_item);
                                    }
                                    else{
                                        bottom_container2.addView(added_item);
                                    }
                                }
                                LinearLayout last_point=(LinearLayout) inflater.inflate(R.layout.line_end,null);
                                ((TextView)(last_point.findViewById(R.id.last_name))).setText(last_stop+" 하차");
                                if(line_num_list.size()>2){
                                    bottom_container2.addView(last_point);
                                }
                                else{
                                    bottom_container.addView(last_point);
                                }
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
            public void onFailure(Call<ShowRouteRes> call, Throwable t) {
                Log.e("실패원인",t.toString());
            }
        });
    }

    //구글 폴리라인 인코딩 해석기, 점들의 리스트를 리턴해준다
    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    public void getWalk() {
        bus_image.setImageResource(R.drawable.bus_passive);
        walk_image.setImageResource(R.drawable.walk_active);
        cur_mode="walk";
        bottom_type.setText("도보");
        show_Map.clear();
        bottom_container.removeAllViews();
        walkOptions=new PolylineOptions();
        walk_all_latlng=new ArrayList<LatLng>();

        ShowWalk walkRes=retro_walk.create(ShowWalk.class);
        Call<ShowWalkRes> call = walkRes.showWalk(tMapKey,Double.toString(mLastLong),Double.toString(mLastLat),Double.toString(to_long),Double.toString(to_lat),
                "start","end","WGS84GEO","WGS84GEO");
        call.enqueue(new Callback<ShowWalkRes>() {
            @Override
            public void onResponse(Call<ShowWalkRes> call, Response<ShowWalkRes> response) {
                if (response.isSuccessful()) {
                    ShowWalkRes res = response.body();
                    Log.e("요청 보기", response.toString());
                    try {
                        walk_guide_latlng=new ArrayList<LatLng>();
                        walk_guide_text=new ArrayList<String>();
                        for(int i=0;i<res.get_count();i++){
                            if(res.get_type(i).equals("Point")){
                                LatLng added_point=new LatLng(Double.parseDouble(res.get_point_lat(i)),Double.parseDouble(res.get_point_lng(i)));
                                walk_guide_latlng.add(added_point);
                                walk_guide_text.add(res.get_description(i));
                                walkOptions.add(added_point);
                                walk_all_latlng.add(added_point);
                            }
                            else{
                                for(int j=0;j<res.get_coor_count(i);j++){
                                    String[] str_latlng=res.get_coor_lat(i,j);
                                    LatLng added_point=new LatLng(Double.parseDouble(str_latlng[0]),Double.parseDouble(str_latlng[1]));
                                    walkOptions.add(added_point);
                                    staticValues.walk_all_latlng.add(added_point);
                                }
                            }
                        }

                        show_Map.addPolyline(walkOptions);
                        staticValues.cur_poly=walkOptions;
                        show_Map.addMarker(new MarkerOptions().position(staticValues.mLastLatLong).title("내 위치"));
                        show_Map.addMarker(new MarkerOptions().position(staticValues.to_latlng).title("목표지점"));

                        String walk_time=res.get_total_time();
                        int added_walk_time=Integer.parseInt(walk_time)/60;
                        distance=Float.parseFloat(res.get_total_distance());
                        bottom_distance.setText(String.format("%.2f",(staticValues.distance)/1000.0f)+"km");
                        bottom_time.setText(added_walk_time+"분");

                    } catch (Exception e) {
                        StringWriter sw = new StringWriter();
                        e.printStackTrace(new PrintWriter(sw));
                        String exceptionAsStrting = sw.toString();
                        Log.e("예외발생", exceptionAsStrting);
                    }
                } else {
                    Log.e("에러 메세지", response.toString());
                }
            }

            public void onFailure(Call<ShowWalkRes> call, Throwable t) {
                Log.e("실패원인", t.toString());
            }
        });
    }

    public void set_camera(View v){
        if((staticValues.walk_guide_latlng!=null)&&(walk_guide_text!=null)&&
                (staticValues.walk_all_latlng!=null)&&(staticValues.walk_guide_latlng.size()>3)){
            Intent intent=new Intent(show_route_activity.this,ar_activity.class);
            intent.putExtra("type","walk");
            staticValues.static_cur_mode="walk";
            startActivity(intent);
        }
        else if((staticValues.transit_guide_latlng!=null)&&(transit_guide_text!=null)&&
                (staticValues.transit_all_latlng!=null)&&(staticValues.transit_guide_latlng.size()>3)){
            Intent intent=new Intent(show_route_activity.this,ar_activity.class);
            intent.putExtra("type","transit");
            staticValues.static_cur_mode="transit";
            startActivity(intent);
        }
        else{
            Toast.makeText(show_route_activity.this,"경로 정보를 준비중입니다",Toast.LENGTH_LONG).show();
        }
    }
}
