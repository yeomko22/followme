package com.example.junny.followme_realbeta.activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.junny.followme_realbeta.R;
import com.example.junny.followme_realbeta.staticValues;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.junny.followme_realbeta.staticValues.middle_point;
import static com.example.junny.followme_realbeta.staticValues.to_lat;
import static com.example.junny.followme_realbeta.staticValues.to_long;
import static com.example.junny.followme_realbeta.staticValues.to_title;
import static com.example.junny.followme_realbeta.staticValues.walk_google_poly;
import static com.example.junny.followme_realbeta.staticValues.walk_guide;
import static com.example.junny.followme_realbeta.staticValues.walk_guide_poly;

public class show_route_activity extends FragmentActivity implements OnMapReadyCallback {
    private TextView start_point_view;
    private TextView end_point_view;
    private ImageView bus_image;
    private ImageView walk_image;

    private TextView bottom_type;
    private TextView bottom_time;
    private TextView bottom_distance;
    private LinearLayout bottom_container;

    private String cur_mode="bus";
    private FragmentManager fm;
    private HttpURLConnection conn;

    private GoogleMap show_Map;
    private Handler mHandler;

    private TMapData tMapData;
    private TMapPoint tMap_start;
    private TMapPoint tMap_end;

    private PolylineOptions walkOptions;
    private Resources res;
    private String last_stop;
    private JSONArray features;
    private boolean contain_transit;

    private PolylineOptions walk_poly_options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_route_activity);

        res=getResources();

        start_point_view=(TextView)findViewById(R.id.start_point);
        end_point_view=(TextView)findViewById(R.id.end_point);
        bus_image=(ImageView)findViewById(R.id.transport_bus);
        walk_image=(ImageView)findViewById(R.id.transport_walk);

        bottom_container=(LinearLayout)findViewById(R.id.show_route_stepcontainer);
        bottom_type=(TextView)findViewById(R.id.show_route_type);
        bottom_time=(TextView)findViewById(R.id.show_route_time);
        bottom_distance=(TextView)findViewById(R.id.show_route_distance);

        Intent intent=getIntent();

        mHandler=new Handler();
        tMap_start=new TMapPoint(staticValues.mLastLat, staticValues.mLastLong);
        tMap_end=new TMapPoint(staticValues.to_lat, to_long);

        SupportMapFragment showmapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.show_map);
        showmapFragment.getMapAsync(this);
        tMapData=new TMapData();

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
        bus_image.setImageResource(R.drawable.bus_active);
        walk_image.setImageResource(R.drawable.walk_passive);
        cur_mode="bus";
        bottom_type.setText("대중교통");
        if(show_Map!=null){
            show_Map.clear();
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{

                    //다음 로컬 api를 사용https://apis.daum.net/local/v1/search/keyword.json?apikey=5a3b393c51ad7571d6a92599bd57a77e&query=%ED%99%8D%EB%8C%80
                    String str_url="https://maps.googleapis.com/maps/api/directions/json?origin="+
                            staticValues.mLastLat+","+staticValues.mLastLong+"&destination="+to_lat+","+to_long+
                            "&mode=transit&language=ko&key=AIzaSyBAy4VmTHyOCI1e_XvXwRlNn7pqwKU0t7o";
                    Log.e("url",str_url);
                    URL url=new URL(str_url);

                    conn=(HttpURLConnection)url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(2000);
                    conn.connect();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
                    StringBuilder sb=new StringBuilder();
                    String line=null;
                    while((line=br.readLine())!=null){
                        sb.append(line);
                    }
                    String jsonString=sb.toString();
                    JSONObject totalObject=new JSONObject(jsonString);
                    JSONArray routeArray=new JSONArray(totalObject.getString("routes"));
                    JSONObject routeObject=routeArray.getJSONObject(0);
                    JSONArray legs=new JSONArray(routeObject.getString("legs"));
                    JSONObject overview_poly=new JSONObject(routeObject.getString("overview_polyline"));

                    final List<LatLng> polylines=decodePoly(overview_poly.getString("points"));

                    final JSONObject legsObject=legs.getJSONObject(0);
                    JSONArray steps=new JSONArray(legsObject.getString("steps"));
                    int walk_time=0;
                    final ArrayList<String> line_num_list=new ArrayList<String>();
                    final ArrayList<String> line_name_list=new ArrayList<String>();
                    contain_transit=false;
                    for(int i=0;i<steps.length();i++){
                        JSONObject step_detail=steps.getJSONObject(i);
                        if(step_detail.getString("travel_mode").equals("TRANSIT")){
                            contain_transit=true;
                            JSONObject transit_detail=step_detail.getJSONObject("transit_details");
                            String org_name=new JSONObject(transit_detail.getString("line")).getString("short_name");
                            Log.e("타입",org_name);
                            line_num_list.add(org_name.substring(0,1));

                            String org_dep=new JSONObject(transit_detail.getString("departure_stop")).getString("name");
                            line_name_list.add(org_dep.split(" ")[0]);

                            last_stop=new JSONObject(transit_detail.getString("arrival_stop")).getString("name");
                        }
                        else{
                            walk_time+=Integer.parseInt(new JSONObject(step_detail.getString("duration")).getString("value"));
                        }
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                if(!contain_transit){
                                    Toast.makeText(show_route_activity.this,"가까운 거리는 도보 경로를 이용해주세요", Toast.LENGTH_LONG).show();
                                    getWalk();
                                    return;
                                }
                                JSONObject distance_obj=new JSONObject(legsObject.getString("distance"));
                                bottom_distance.setText(distance_obj.getString("text"));
                                JSONObject time_obj=new JSONObject(legsObject.getString("duration"));
                                bottom_time.setText(time_obj.getString("text"));

                                PolylineOptions rectOptions = new PolylineOptions();
                                for(int i=0;i<polylines.size();i++){
                                    rectOptions.add(polylines.get(i));
                                }
                                LayoutInflater inflater=(LayoutInflater)show_route_activity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                for(int i=0;i<line_num_list.size();i++){
                                    LinearLayout added_item=(LinearLayout) inflater.inflate(R.layout.line_background,null);
                                    ((TextView) added_item.findViewById(R.id.num)).setText(line_num_list.get(i));
                                    ((TextView) added_item.findViewById(R.id.name)).setText(line_name_list.get(i));
                                    switch (line_num_list.get(i)){
                                        case "1":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(res.getDrawable(R.drawable.line_1));
                                            break;
                                        case "2":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(res.getDrawable(R.drawable.line_2));
                                            break;
                                        case "3":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(res.getDrawable(R.drawable.line_3));
                                            break;
                                        case "4":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(res.getDrawable(R.drawable.line_4));
                                            break;
                                        case "5":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(res.getDrawable(R.drawable.line_5));
                                            break;
                                        case "6":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(res.getDrawable(R.drawable.line_6));
                                            break;
                                        case "7":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(res.getDrawable(R.drawable.line_7));
                                            break;
                                        case "8":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(res.getDrawable(R.drawable.line_8));
                                            break;
                                        case "9":
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(res.getDrawable(R.drawable.line_9));
                                            break;
                                        default:
                                            ((TextView) added_item.findViewById(R.id.num)).setBackground(res.getDrawable(R.drawable.line_9));
                                            break;
                                    }
                                    bottom_container.addView(added_item);
                                }
                                LinearLayout last_point=(LinearLayout) inflater.inflate(R.layout.line_end,null);
                                ((TextView)(last_point.findViewById(R.id.last_name))).setText(last_stop+" 하차");
                                bottom_container.addView(last_point);

                                show_Map.addMarker(new MarkerOptions().position(staticValues.mLastLatLong).title("내 위치"));
                                show_Map.addMarker(new MarkerOptions().position(staticValues.to_latlng).title("목표지점"));
                                show_Map.addPolyline(rectOptions);
                            }
                            catch (Exception e){
                                StringWriter sw = new StringWriter();
                                e.printStackTrace(new PrintWriter(sw));
                                String exceptionAsStrting = sw.toString();
                                Log.e("예외발생", exceptionAsStrting);
                            }

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
//        티맵 데이터 객체로 경로를 요청, 날라온 폴리라인을 분해해서 이걸로 다시 구글 폴리라인을 생성, 상당히 번거롭다
//        아래는 지오 제이썬 객체로 도보 경로를 받아온 것, 여기의 좌표로 폴리라인을 그릴 수 있다면 충분함 그걸로 대체하는 방향으로 가자
        tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMap_start, tMap_end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {

                ArrayList<TMapPoint> tmap_poly = tMapPolyLine.getLinePoint();
                staticValues.walk_google_poly=new ArrayList<LatLng>();
                walkOptions = new PolylineOptions();
                for (int i = 0; i < tmap_poly.size(); i++) {
                    TMapPoint cur_tpoint = tmap_poly.get(i);
                    walkOptions.add(new LatLng(cur_tpoint.getLatitude(), cur_tpoint.getLongitude()));
                    staticValues.walk_google_poly.add(new LatLng(cur_tpoint.getLatitude(), cur_tpoint.getLongitude()));
                }
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                show_Map.addMarker(new MarkerOptions().position(staticValues.mLastLatLong).title("내 위치"));
                                show_Map.addMarker(new MarkerOptions().position(staticValues.to_latlng).title("목표지점"));
                                show_Map.addPolyline(walkOptions);
                                staticValues.cur_poly=walkOptions;
                            }
                        });
                    }
                });
            }
        });
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.e("워크 어싱크야 돌아라","11");
                    //도보 경로 티맵 geoJSON 받아오는 유알엘, 리퀘스트 코드 타입과 리스본스 코드 타입을 조심할 것, 경도 위도가 일반적인 순서와 다르다
                    String tes_url="https://apis.skplanetx.com/tmap/routes/pedestrian?version=1&startX="+staticValues.mLastLong+"&startY="+staticValues.mLastLat+"&endX="+to_long+"&endY="+to_lat+"&startName=start&endName=end&reqCoordType=WGS84GEO&resCoordType=WGS84GEO&appKey=4004a4c7-8e67-3c17-88d9-9799c613ecc7";
                    Log.e("도보 url", tes_url);
                    URL url = new URL(tes_url);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(2000);
                    conn.connect();

                    if(conn.getResponseCode()==200){
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        String line = null;

                        while ((line=br.readLine()) != null) {
                            sb.append(line);
                        }
                        JSONObject org_obj=new JSONObject(sb.toString());
                        features=org_obj.getJSONArray("features");

                        walk_poly_options = new PolylineOptions();
                        staticValues.walk_guide_poly=new ArrayList<LatLng>();
                        staticValues.walk_guide=new ArrayList<String>();

                        for(int i=0;i<features.length();i++){
                            JSONObject cur_obj=features.getJSONObject(i);
                            JSONObject cur_geo=new JSONObject(cur_obj.getString("geometry"));
                            JSONObject cur_prop=new JSONObject(cur_obj.getString("properties"));

                            if(cur_geo.getString("type").equals("Point")){
                                String point_array=cur_geo.getString("coordinates");
                                if(point_array.charAt(1)!='['){
                                    String trim_string=point_array.substring(1,point_array.length()-1);
                                    String[] res=trim_string.split(",");
                                    LatLng cur_point=new LatLng(Double.parseDouble(res[1]),Double.parseDouble(res[0]));

                                    walk_poly_options.add(cur_point);
                                    staticValues.walk_guide_poly.add(cur_point);
                                    staticValues.walk_guide.add(cur_prop.getString("description"));
                                }
                            }
                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("워크 핸들러야 돌아라","11");
                                try {

                                    String walk_time=(new JSONObject(features.getJSONObject(0).getString("properties"))).getString("totalTime");
                                    int added_walk_time=Integer.parseInt(walk_time)/60;

                                    String walk_distance=(new JSONObject(features.getJSONObject(0).getString("properties"))).getString("totalDistance");
                                    float added_walk_distance=(Float.parseFloat(walk_distance)/(float)1000);
                                    bottom_distance.setText(String.format("%.2f",added_walk_distance)+"km");
                                    bottom_time.setText(added_walk_time+"분");

                                } catch (Exception e) {
                                    StringWriter sw = new StringWriter();
                                    e.printStackTrace(new PrintWriter(sw));
                                    String exceptionAsStrting = sw.toString();
                                    Log.e("예외발생", exceptionAsStrting);
                                }
                                return;
                            }
                        });
                    }
                    else{
                        Log.e("유알엘",tes_url);
                        Log.e("실패코드", Integer.toString(conn.getResponseCode()));
                        Log.e("실패코드", conn.getResponseMessage());
                    }
                } catch (Exception e) {
                    StringWriter sw = new StringWriter();
                    e.printStackTrace(new PrintWriter(sw));
                    String exceptionAsStrting = sw.toString();
                    Log.e("예외발생", exceptionAsStrting);
                }
            }
        });
    }

    public void set_camera(View v){
        if((staticValues.walk_guide_poly!=null)&&(walk_guide!=null)&&(walk_google_poly!=null)&&(staticValues.walk_guide_poly.size()>3)){
            Intent intent=new Intent(show_route_activity.this,ar_activity.class);
            startActivity(intent);
        }
        else{
            Log.e("가이드 폴리",Integer.toString(walk_google_poly.size()));
            Log.e("워크 가이드 폴리", Integer.toString(walk_guide_poly.size()));
            Log.e("설명 개수",Integer.toString(walk_guide.size()));
            Toast.makeText(show_route_activity.this,"경로 정보를 준비중입니다",Toast.LENGTH_LONG).show();
        }

    }
}
