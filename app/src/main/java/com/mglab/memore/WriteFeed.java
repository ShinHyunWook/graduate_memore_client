package com.mglab.memore;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-04-20.
 */

public class WriteFeed extends AppCompatActivity implements OnMapReadyCallback, NetDefine{
    ImageButton btn_setDate;
    private MapView mapView;
    private GoogleMap googleMap;
    AsyncHttpClient asyncHttpClient;
    private PolylineOptions polylineOptions;
    private List<LatLng> arrayPoints, result;
    private RecyclerView galleryRecycler;
    private FeedCutAdapter feedCutAdapter;
    private FeedModel feedModel;
    private List<FeedModel> feedModelList = new ArrayList<FeedModel>();
    private HashMap<Integer, FeedModel> selectFeedList = new HashMap<Integer, FeedModel>();
    private String select_date_start,select_date_end;

    private final ArrayList<MarkerOptions> markerOptions = new ArrayList<MarkerOptions>();
    private Marker myLocation;

    private final static int DATE_START = 100;
    private final static int DATE_END = 200;

    Button btn_complete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_feed);

        galleryRecycler = (RecyclerView)findViewById(R.id.galleryRecycler);
        btn_setDate = (ImageButton) findViewById(R.id.btn_setDate);
        btn_complete = (Button) findViewById(R.id.btn_complete);

        btn_setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogDatePicker(DATE_START);
            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_my_id();
            }
        });

        mapView = (MapView)findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.getMapAsync(this);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void DialogDatePicker(final int STATUS) {
        if(STATUS == 100){
            Toast.makeText(WriteFeed.this,"시작 날짜를 선택하세요",Toast.LENGTH_LONG).show();
        }else if(STATUS == 200){
            Toast.makeText(WriteFeed.this,"종료 날짜를 선택하세요",Toast.LENGTH_LONG).show();
        }
        Calendar c = Calendar.getInstance();
        int cyear = c.get(Calendar.YEAR);
        int cmonth = c.get(Calendar.MONTH);
        int cday = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            // onDateSet method
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (STATUS == 100) {
                    String month, day;
                    if (monthOfYear + 1 < 10) {
                        month = '0' + String.valueOf(monthOfYear + 1);
                    } else {
                        month = String.valueOf(monthOfYear + 1);
                    }
                    if (dayOfMonth < 10) {
                        day = '0' + String.valueOf(dayOfMonth);
                    } else {
                        day = String.valueOf(dayOfMonth);
                    }
                    select_date_start = String.valueOf(year) + '-' + month + '-' + day;
                    DialogDatePicker(DATE_END);
                } else if (STATUS == 200) {
                    String month, day;
                    if (monthOfYear + 1 < 10) {
                        month = '0' + String.valueOf(monthOfYear + 1);
                    } else {
                        month = String.valueOf(monthOfYear + 1);
                    }
                    if (dayOfMonth < 10) {
                        day = '0' + String.valueOf(dayOfMonth);
                    } else {
                        day = String.valueOf(dayOfMonth);
                    }
                    select_date_end = String.valueOf(year) + '-' + month + '-' + day;
                    TextView cut_select_txt = (TextView)findViewById(R.id.cut_select_txt);
                    cut_select_txt.setVisibility(View.GONE);
                    galleryRecycler.setVisibility(View.VISIBLE);
                    DrawLine(galleryRecycler);
                }
            }
        };
        DatePickerDialog alert = new DatePickerDialog(WriteFeed.this, mDateSetListener, cyear, cmonth, cday);
//        if(STATUS == DATE_START)
//            alert.setTitle("시작 날짜");
//        else
//            alert.setTitle("종료날짜");
        alert.show();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.496375, 126.957448)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    public void DrawLine(final RecyclerView galleryRecycler) {
        googleMap.clear();

        String u_id;
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(WriteFeed.this);
        u_id = sharedPreferencesManager.getLogin();

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("email", u_id);
        params.put("start_date", select_date_start);
        params.put("end_date", select_date_end);
        asyncHttpClient.post(SERVIERIP + "get_location_between", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONArray total_tmp = new JSONArray(result);
                    if(total_tmp.length()==0){
                        Toast.makeText(WriteFeed.this,"해당 기간에 저장된 기억이 없습니다.",Toast.LENGTH_LONG).show();
                        finish();
                        startActivity(getIntent());
                        return;
                    }
                    ArrayList<String> loc_data = new ArrayList<String>();
                    List<LatLng> loc_json = new ArrayList<LatLng>();
                    polylineOptions = new PolylineOptions();
                    for(int i = 0;i<total_tmp.length();i++){
                        JSONObject obj = new JSONObject(String.valueOf(total_tmp.get(i)));
                        loc_data.add(obj.getString("id"));
                        JSONManager json = new JSONManager();
                        loc_json = json.parseJSON(obj.getString("loc_data"));
                        polylineOptions.addAll(loc_json);
                    }

                    polylineOptions.color(Color.parseColor("#FF9164a8"));
                    polylineOptions.width(10);
                    googleMap.addPolyline(polylineOptions);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc_json.get(2)));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));

                    get_cut_list(galleryRecycler, loc_data);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(WriteFeed.this, "데이터 가져오기 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void get_cut_list(final RecyclerView galleryRecycler, ArrayList<String> loc_id) {
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("loc_id", loc_id);

        asyncHttpClient.post(SERVIERIP + "get_cut_image_locID", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    feedModelList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = new JSONObject(String.valueOf(jsonArray.get(i)));
                        JSONArray image_name_list = new JSONArray(obj.getString("image_name"));

                        feedModel = new FeedModel();
                        feedModel.setCutId(obj.getString("id"));
                        feedModel.setCutName(image_name_list.getString(0));
                        feedModel.setCutDescription(obj.getString("caption"));
                        feedModel.setSelected(false);
                        feedModelList.add(feedModel);

                        draw_cut(image_name_list.getString(0), obj.getString("location_data"));
                    }

                    feedCutAdapter = new FeedCutAdapter(feedModelList, WriteFeed.this);
                    galleryRecycler.setLayoutManager(new LinearLayoutManager(WriteFeed.this, LinearLayoutManager.VERTICAL, false));
                    galleryRecycler.setAdapter(feedCutAdapter);

                    feedCutAdapter.setOnCutItemClickListener(new FeedCutAdapter.OnCutItemClickListener() {
                        @Override
                        public void onItemClick(int position) {
                            if (feedModelList.get(position).isSelected()) {
                                feedModelList.get(position).setSelected(false);
                                selectFeedList.remove(position);
                           } else {
                                feedModelList.get(position).setSelected(true);
                                selectFeedList.put(position, feedModelList.get(position));}
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(WriteFeed.this, "데이터 가져오기 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void draw_cut(String imageName, String location_data) {
        JSONObject json;
        try {
            json = new JSONObject(location_data);
            Double lat = Double.valueOf(json.getString("lat").toString());
            Double lon = Double.valueOf(json.getString("lng").toString());
            GetImage gi = new GetImage(lat,lon);
            gi.execute(imageName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//        Bitmap bmp = Bitmap.createBitmap(200, 200, conf);
//        Canvas canvas1 = new Canvas(bmp);
//
//        Paint color = new Paint();
//        color.setTextSize(35);
//        color.setColor(Color.BLACK);
//
//        String folder_path = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES))+"/Memore/";
//        String imagePath = folder_path+imageName;
//
//        File file = new File(imagePath);
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        options.inSampleSize = 8;
//        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
//
//        canvas1.drawBitmap(bitmap, 0, 0, color);
////                canvas1.drawText("사진", 30, 40, color);
//
//        markerOptions.add(new MarkerOptions().position(new LatLng(lat, lon))
//                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
//                // Specifies the anchor to be at a particular point in the marker image.
//                .anchor(0.5f, 1));
//
//        myLocation = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
//                .icon(BitmapDescriptorFactory.fromBitmap(bmp))
//                // Specifies the anchor to be at a particular point in the marker image.
//                .anchor(0.5f, 1));
    }


    public void post_feed(String my_id){
        ArrayList<String> cut_id_list = new ArrayList<String>();
        for (int i = 0; i < feedModelList.size(); i++) {
            if (feedModelList.get(i).isSelected()) {
                cut_id_list.add(feedModelList.get(i).getCutId());
            }
        }

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("my_id", my_id);
        params.put("start_date", select_date_start);
        params.put("end_date", select_date_end);
        for (String element : cut_id_list) {
            params.add("elements[]", element);
        }

        asyncHttpClient.post(SERVIERIP + "post_feed", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(WriteFeed.this, "데이터 가져오기 실패", Toast.LENGTH_LONG).show();
            }
        });


    }

    public void get_my_id() {
        String u_email;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(WriteFeed.this);
        u_email = sharedPreferencesManager.getLogin();

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", u_email);
        asyncHttpClient.post(SERVIERIP + "search_person", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                JSONObject obj = null;
                try {
                    obj = new JSONObject(result);
                    post_feed(obj.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
            }
        });
    }

    class GetImage extends AsyncTask<String,Void,Bitmap> {
        ProgressDialog loading;
        Double lat, lon;

        public GetImage(Double lat, Double lon){
            this.lat = lat;
            this.lon = lon;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = ProgressDialog.show(WriteFeed.this, "Setting...", null,true,true);
        }

        @Override
        protected void onPostExecute(Bitmap b) {
            super.onPostExecute(b);
            loading.dismiss();
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmp = Bitmap.createBitmap(b.getWidth()+100, b.getHeight()+120, conf);
            Canvas canvas1 = new Canvas(bmp);

            Paint color = new Paint();
            color.setTextSize(35);
            color.setColor(Color.parseColor("#00000000"));
            color.setStyle(Paint.Style.FILL);
            color.setStrokeWidth(10); // set stroke width

            Paint color2 = new Paint();
            color2.setTextSize(35);
            color2.setColor(Color.parseColor("#ffc200"));
            color2.setStyle(Paint.Style.FILL);
            color2.setStrokeWidth(10); // set stroke width

            canvas1.drawRect(0,0,bmp.getWidth(),bmp.getHeight(), color);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                canvas1.drawRoundRect(20,0,b.getWidth()+(bmp.getWidth()-b.getWidth())/2+20,b.getHeight()+(bmp.getHeight()-b.getHeight())/2,30,30, color2);
            }

            Point point1_draw = new Point((b.getWidth()+(bmp.getWidth()-b.getWidth())/2)/2+20, b.getHeight()+(bmp.getHeight()-b.getHeight())/2+50);
            Point point2_draw = new Point((b.getWidth()+(bmp.getWidth()-b.getWidth())/2)/4+20, b.getHeight()+(bmp.getHeight()-b.getHeight())/2);
            Point point3_draw = new Point((b.getWidth()+(bmp.getWidth()-b.getWidth())/2)/4*3+20, b.getHeight()+(bmp.getHeight()-b.getHeight())/2);

            Path path = new Path();
            path.moveTo(point1_draw.x, point1_draw.y);
            path.lineTo(point2_draw.x, point2_draw.y);
            path.lineTo(point3_draw.x, point3_draw.y);
            path.lineTo(point1_draw.x, point1_draw.y);
            path.close();

            canvas1.drawPath(path,color2);
            canvas1.drawBitmap(b, (bmp.getWidth()-b.getWidth())/4+20,(bmp.getHeight()-b.getHeight())/4 , color2);

            markerOptions.add(new MarkerOptions().position(new LatLng(lat, lon))
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                    // Specifies the anchor to be at a particular point in the marker image.
                    .anchor(0.5f, 1));

            myLocation = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                    // Specifies the anchor to be at a particular point in the marker image.
                    .anchor(0.5f, 1));
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imageName = params[0];
            String add = "http://" + IP + "/memore/uploads/"+imageName;
            URL url = null;
            Bitmap image = null;
            try {
                url = new URL(add);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                options.inSampleSize = 4;
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream(),null,options);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return image;
        }
    }

}