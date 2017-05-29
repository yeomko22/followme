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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
    private String latitude;
    private String longitude;
    private GoogleMap show_Map;
    private Handler mHandler;
    private LatLng cur_location;
    private LatLng destination;
    private LatLng middle_point;

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
        String title=intent.getStringExtra("title");

        mHandler=new Handler();
        cur_location=new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        destination=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        middle_point=new LatLng(((mLastLocation.getLatitude()+Double.parseDouble(latitude))/(double)2),((mLastLocation.getLongitude()+Double.parseDouble(longitude))/(double)2));

        SupportMapFragment showmapFragment=(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.show_map);
        showmapFragment.getMapAsync(this);


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
                                Polyline polyline = show_Map.addPolyline(rectOptions);
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





    public void getWalk(){
        bottom_type.setText("도보");
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{

                    //다음 로컬 api를 사용https://apis.daum.net/local/v1/search/keyword.json?apikey=5a3b393c51ad7571d6a92599bd57a77e&query=%ED%99%8D%EB%8C%80
                    String str_url="https://apis.skplanetx.com/tmap/routes/pedestrian?version=1&callback=walk_back&startX="+
                            staticValues.mLastLat+"&startY="+staticValues.mLastLong+"&endX="+latitude+"&endY="+longitude+"startName=start&endName=end";

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
                    Log.e("넘어온 제이썬",sb.toString());
//                    String jsonString=sb.toString();
//                    JSONObject totalObject=new JSONObject(jsonString);
//                    JSONArray routeArray=new JSONArray(totalObject.getString("routes"));
//                    Log.e("경로 개수", Integer.toString(routeArray.length()));
//                    JSONObject routeObject=routeArray.getJSONObject(0);
//                    JSONArray legs=new JSONArray(routeObject.getString("legs"));
//                    final JSONObject legsObject=legs.getJSONObject(0);
//                    JSONArray steps=new JSONArray(legsObject.getString("steps"));
//                    for(int i=0;i<steps.length();i++){
//                        Log.e("스텝", steps.getJSONObject(i).getString("html_instructions"));
//                    }

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            try{
//                                JSONObject distance_obj=new JSONObject(legsObject.getString("distance"));
//                                bottom_distance.setText(distance_obj.getString("text"));
//                                JSONObject time_obj=new JSONObject(legsObject.getString("duration"));
//                                bottom_time.setText(time_obj.getString("text"));
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

    public void set_camera(View v){
        Toast.makeText(show_route_activity.this,"hi",Toast.LENGTH_LONG).show();
        LatLng cur_location=new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        LatLng destination=new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

        show_Map.addMarker(new MarkerOptions().position(cur_location).title("내 위치"));
        show_Map.addMarker(new MarkerOptions().position(destination).title("목표지점"));

        show_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(cur_location,18));
    }
}
