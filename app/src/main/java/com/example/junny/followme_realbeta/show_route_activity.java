package com.example.junny.followme_realbeta;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.example.junny.followme_realbeta.staticValues.mLastLocation;

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
    private String title;
    private String latitude;
    private String longitude;
    private GoogleMap show_Map;
    private Handler mHandler;
    private LatLng cur_location;
    private LatLng destination;
    private LatLng middle_point;
    private TMapData tMapData;
    private TMapPoint tMap_start;
    private TMapPoint tMap_end;
    private PolylineOptions walkOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_route_activity);

        start_point_view=(TextView)findViewById(R.id.start_point);
        end_point_view=(TextView)findViewById(R.id.end_point);
        bus_image=(ImageView)findViewById(R.id.transport_bus);
        walk_image=(ImageView)findViewById(R.id.transport_walk);

        bottom_container=(LinearLayout)findViewById(R.id.show_route_stepcontainer);
        bottom_type=(TextView)findViewById(R.id.show_route_type);
        bottom_time=(TextView)findViewById(R.id.show_route_time);
        bottom_distance=(TextView)findViewById(R.id.show_route_distance);

        Intent intent=getIntent();
        latitude=intent.getStringExtra("latitude");
        longitude=intent.getStringExtra("longitude");
        title=intent.getStringExtra("title");

        mHandler=new Handler();
        cur_location=new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        tMap_start=new TMapPoint(mLastLocation.getLatitude(), mLastLocation.getLongitude());

        destination=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        tMap_end=new TMapPoint(Double.parseDouble(latitude),Double.parseDouble(longitude));

        middle_point=new LatLng(((mLastLocation.getLatitude()+Double.parseDouble(latitude))/(double)2),((mLastLocation.getLongitude()+Double.parseDouble(longitude))/(double)2));

        SupportMapFragment showmapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.show_map);
        showmapFragment.getMapAsync(this);
        tMapData=new TMapData();


        if (staticValues.cur_address.length()>8){
            start_point_view.setText(staticValues.cur_address.substring(0,8)+"...");
        }
        else{
            start_point_view.setText(staticValues.cur_address);
        }

        if(title.length()>8){
            end_point_view.setText(title.substring(0,8)+"...");
        }
        else{end_point_view.setText(title);}
        getTransit();
    }

    public void onMapReady(GoogleMap googleMap) {
        Log.e("지도 준비됬다","11");
        show_Map = googleMap;

        show_Map.addMarker(new MarkerOptions().position(cur_location).title("내 위치"));
        show_Map.addMarker(new MarkerOptions().position(destination).title("목표지점"));

        show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(middle_point,12));
        // Add a marker in Sydney and move the camera
    }
    public void replace(View v){
        switch(v.getId()){
            case R.id.transport_bus :
                if(cur_mode.equals("bus")){
                    break;
                }
                bus_image.setImageResource(R.drawable.bus_active);
                walk_image.setImageResource(R.drawable.walk_passive);
                cur_mode="bus";
                getTransit();
                break;
            case R.id.transport_walk :
                if(cur_mode.equals("walk")){
                    break;
                }
                bus_image.setImageResource(R.drawable.bus_passive);
                walk_image.setImageResource(R.drawable.walk_active);
                cur_mode="walk";
                getWalk();
                break;
        }

    }
    public void getTransit(){
        bottom_type.setText("대중교통");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{

                    //다음 로컬 api를 사용https://apis.daum.net/local/v1/search/keyword.json?apikey=5a3b393c51ad7571d6a92599bd57a77e&query=%ED%99%8D%EB%8C%80
                    String str_url="https://maps.googleapis.com/maps/api/directions/json?origin="+
                            staticValues.mLastLat+","+staticValues.mLastLong+"&destination="+latitude+","+longitude+
                            "&mode=transit&language=ko&key=AIzaSyBAy4VmTHyOCI1e_XvXwRlNn7pqwKU0t7o";

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
                    JSONArray routeArray=new JSONArray(totalObject.getString("routes"));
                    Log.e("경로 개수", Integer.toString(routeArray.length()));
                    JSONObject routeObject=routeArray.getJSONObject(0);
                    JSONArray legs=new JSONArray(routeObject.getString("legs"));
                    JSONObject overview_poly=new JSONObject(routeObject.getString("overview_polyline"));

                    final List<LatLng> polylines=decodePoly(overview_poly.getString("points"));

                    final JSONObject legsObject=legs.getJSONObject(0);
                    JSONArray steps=new JSONArray(legsObject.getString("steps"));
                    for(int i=0;i<steps.length();i++){
                        Log.e("스텝", steps.getJSONObject(i).getString("html_instructions"));
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                JSONObject distance_obj=new JSONObject(legsObject.getString("distance"));
                                bottom_distance.setText(distance_obj.getString("text"));
                                JSONObject time_obj=new JSONObject(legsObject.getString("duration"));
                                bottom_time.setText(time_obj.getString("text"));

                                PolylineOptions rectOptions = new PolylineOptions();
                                for(int i=0;i<polylines.size();i++){
                                    rectOptions.add(polylines.get(i));
                                }
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
        bottom_type.setText("도보");
        //티맵 데이터 객체로 경로를 요청, 날라온 폴리라인을 분해해서 이걸로 다시 구글 폴리라인을 생성, 상당히 번거롭다
        //아래는 지오 제이썬 객체로 도보 경로를 받아온 것, 여기의 좌표로 폴리라인을 그릴 수 있다면 충분함 그걸로 대체하는 방향으로 가자
        tMapData.findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMap_start, tMap_end, new TMapData.FindPathDataListenerCallback() {
            @Override
            public void onFindPathData(TMapPolyLine tMapPolyLine) {
                ArrayList<TMapPoint> tmap_poly = tMapPolyLine.getLinePoint();
                ArrayList<LatLng> google_poly = new ArrayList<LatLng>();
                walkOptions = new PolylineOptions();
                for (int i = 0; i < tmap_poly.size(); i++) {
                    TMapPoint cur_tpoint = tmap_poly.get(i);
                    walkOptions.add(new LatLng(cur_tpoint.getLatitude(), cur_tpoint.getLongitude()));
                }
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                show_Map.clear();
                                show_Map.addPolyline(walkOptions);
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
                    //도보 경로 티맵 geoJSON 받아오는 유알엘, 리퀘스트 코드 타입과 리스본스 코드 타입을 조심할 것, 경도 위도가 일반적인 순서와 다르다
                    String tes_url="https://apis.skplanetx.com/tmap/routes/pedestrian?version=1&startX="+staticValues.mLastLong+"&startY="+staticValues.mLastLat+"&endX="+longitude+"&endY="+latitude+"&startName=start&endName=end&reqCoordType=WGS84GEO&resCoordType=WGS84GEO&appKey=4004a4c7-8e67-3c17-88d9-9799c613ecc7";
                    URL url = new URL(tes_url);

                    conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setConnectTimeout(1000);
                    conn.connect();

                    if(conn.getResponseCode()==200){
                        Log.e("성공",tes_url);
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        StringBuilder sb = new StringBuilder();
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line);
                        }
                    }
                    else{
                        Log.e("유알엘",tes_url);
                        Log.e("실패코드", Integer.toString(conn.getResponseCode()));
                        Log.e("실패코드", conn.getResponseMessage());
                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try {

                            } catch (Exception e) {
                                StringWriter sw = new StringWriter();
                                e.printStackTrace(new PrintWriter(sw));
                                String exceptionAsStrting = sw.toString();
                                Log.e("예외발생", exceptionAsStrting);
                            }
                            return;
                        }
                    });
                } catch (Exception e) {

                }
            }
        });
    }

    public void set_camera(View v){
        Toast.makeText(show_route_activity.this,"hi",Toast.LENGTH_LONG).show();
        LatLng cur_location=new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        LatLng destination=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

        show_Map.addMarker(new MarkerOptions().position(cur_location).title("내 위치"));
        show_Map.addMarker(new MarkerOptions().position(destination).title("목표지점"));

        show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_location,18));
    }
}
