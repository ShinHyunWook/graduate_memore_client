package com.mglab.memore;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormat;
import android.icu.util.Calendar;
import android.location.Location;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.IntegerRes;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.R.attr.bitmap;
import static android.R.attr.id;

/**
 * Created by 신현욱 on 2017-03-11.
 */

public class Fragment1 extends Fragment implements OnMapReadyCallback, NetDefine {

    private ArrayList<Drawable> gallerys = new ArrayList<Drawable>();
    private MapView mapView;
    private GoogleMap googleMap;
    private PolylineOptions polylineOptions;
    private List<LatLng> arrayPoints, result;

    private RecyclerView galleryRecycler;
    private RecyclerView tagRecycler;
    private GalleryAdapter galleryAdapter;
    private CutGalleryAdapter cutGalleryAdapter;
    private tagListAdapter tagListAdapter;

    private final static int DATE_START = 100;
    private final static int DATE_END = 200;

    private AsyncHttpClient asyncHttpClient;

    private String select_date_start,select_date_end;

    private final ArrayList<MarkerOptions> markerOptions = new ArrayList<MarkerOptions>();
    private Marker myLocation;
//    private Double lat, lon;

    ImageButton btn_setDate;
    LinearLayout distance_box;

    ScrollView view_scroll;

    TextView txtDistance,txtDate;

    Button view_total;

    String my_id;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = (View) inflater.inflate(R.layout.fragment1, container, false);

        get_my_id();

        for (int i = 1; i <= 30; i++) {
            if (i % 3 == 1) {
                gallerys.add(ContextCompat.getDrawable(getActivity(), R.drawable.test1));
            } else if (i % 3 == 2) {
                gallerys.add(ContextCompat.getDrawable(getActivity(), R.drawable.test2));
            } else {
                gallerys.add(ContextCompat.getDrawable(getActivity(), R.drawable.test3));
            }
        }

        arrayPoints = new ArrayList<LatLng>();
        arrayPoints.add(new LatLng(-33.866, 151.195));
        arrayPoints.add(new LatLng(-18.142, 178.431));
        arrayPoints.add(new LatLng(21.291, -157.821));
        arrayPoints.add(new LatLng(37.423, -122.091));

        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        galleryRecycler = (RecyclerView) view.findViewById(R.id.galleryRecycler);
        view_scroll = (ScrollView)view.findViewById(R.id.view_scroll);
        ImageView transparent = (ImageView)view.findViewById(R.id.imagetrans) ;
        transparent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        view_scroll.requestDisallowInterceptTouchEvent(true);
                        // Disable touch on transparent view
                        return false;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        view_scroll.requestDisallowInterceptTouchEvent(false);
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        view_scroll.requestDisallowInterceptTouchEvent(true);
                        return false;

                    default:
                        return true;
                }
            }
        });
        tagRecycler = (RecyclerView) view.findViewById(R.id.friendRecycler);
        txtDate = (TextView)view.findViewById(R.id.txtDate);
        txtDistance = (TextView)view.findViewById(R.id.txtDistance);
        btn_setDate = (ImageButton)view.findViewById(R.id.btn_setDate);
        distance_box = (LinearLayout)view.findViewById(R.id.distance_box);
        view_total = (Button)view.findViewById(R.id.view_total);

        view_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MyPageTotalCut.class);
                intent.putExtra("select_date_start",select_date_start);
                intent.putExtra("select_date_end",select_date_end);
                intent.putExtra("my_id",my_id);
                startActivity(intent);
            }
        });

        btn_setDate.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
            public void onClick(View v) {
                DialogDatePicker(DATE_START);
            }
        });

        return view;
    }

    public void get_my_id() {
        String u_email;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
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
                    my_id = obj.getString("id");
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

    public void set_latest_route(){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        String u_id;
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        u_id = sharedPreferencesManager.getLogin();

        params.put("email", u_id);

        asyncHttpClient.post(SERVIERIP + "get_latest_date", params, new AsyncHttpResponseHandler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                if(!result.equals("0000-00-00")){
                    select_date_start = result;
                    select_date_end = result;
                    DrawLine(galleryRecycler);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(getActivity(), "데이터 가져오기 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

public double getDistance(LatLng StartP, LatLng EndP) {
    int Radius = 6371;// radius of earth in Km
    double lat1 = StartP.latitude;
    double lat2 = EndP.latitude;
    double lon1 = StartP.longitude;
    double lon2 = EndP.longitude;
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(lat1))
            * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
            * Math.sin(dLon / 2);
    double c = 2 * Math.asin(Math.sqrt(a));
    double valueResult = Radius * c;
    double km = valueResult / 1;
    DecimalFormat newFormat = null;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        newFormat = new DecimalFormat("####");
    }
    int kmInDec = 0;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        kmInDec = Integer.valueOf(newFormat.format(km));
    }
    double meter = valueResult % 1000;
    int meterInDec = 0;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        meterInDec = Integer.valueOf(newFormat.format(meter));
    }
    Log.i("Distance", "" + valueResult + "   KM  " + kmInDec
            + " Meter   " + meterInDec);

    return Radius * c;
}

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void DialogDatePicker(final int STATUS) {
        if(STATUS == 100){
            Toast.makeText(getActivity(),"시작 날짜를 선택하세요",Toast.LENGTH_LONG).show();
        }else if(STATUS == 200){
            Toast.makeText(getActivity(),"종료 날짜를 선택하세요",Toast.LENGTH_LONG).show();
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
                    DrawLine(galleryRecycler);
                }
            }
        };
        DatePickerDialog alert = new DatePickerDialog(getActivity(), mDateSetListener, cyear, cmonth, cday);
//        if(STATUS == DATE_START)
//            alert.setTitle("시작 날짜");
//        else
//            alert.setTitle("종료날짜");
        alert.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.496375, 126.957448)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

        set_latest_route();
    }

    public void DrawLine(final RecyclerView galleryRecycler) {
        googleMap.clear();

        String u_id;
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        u_id = sharedPreferencesManager.getLogin();

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("email", u_id);
        params.put("start_date", select_date_start);
        params.put("end_date", select_date_end);
        asyncHttpClient.post(SERVIERIP + "get_location_between", params, new AsyncHttpResponseHandler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                JSONObject temp = null;
                try {
                    JSONArray total_tmp = new JSONArray(result);
                    if(total_tmp.length()==0){
                        Toast.makeText(getContext(),"해당 기간에 저장된 기억이 없습니다.",Toast.LENGTH_LONG).show();
                        set_latest_route();
                        return;
                    }
                    ArrayList<String> loc_data = new ArrayList<String>();
                    List<LatLng> loc_json = new ArrayList<LatLng>();
                    polylineOptions = new PolylineOptions();
                    Double distance = 0.0;
                    for(int i = 0;i<total_tmp.length();i++){
                        JSONObject obj = new JSONObject(String.valueOf(total_tmp.get(i)));
                        loc_data.add(obj.getString("id"));
                        JSONManager json = new JSONManager();
                        loc_json = json.parseJSON(obj.getString("loc_data"));
                        polylineOptions.addAll(loc_json);
                        for(int j = 0;j<loc_json.size()-1;j++){
                            distance += getDistance(loc_json.get(j),loc_json.get(j+1));
                        }
                        String txt_distance =  String.format("%.2f", distance);
                        txtDistance.setText(txt_distance+"km");
                    }
                    String[] s_data = select_date_start.split("-");
                    String[] e_data = select_date_end.split("-");
                    txtDate.setText(s_data[0]+"년 "+s_data[1]+"월 "+s_data[2]+"일 ~ "+e_data[0]+"년 "+e_data[1]+"월 "+e_data[2]+"일");

                    polylineOptions.color(Color.parseColor("#9164a8"));
                    polylineOptions.width(10);
                    googleMap.addPolyline(polylineOptions);

                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc_json.get(2)));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                    TranslateAnimation ani = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, -1.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f,
                            Animation.RELATIVE_TO_SELF, 0.0f);
                    ani.setDuration(4000);
                    ani.setInterpolator(AnimationUtils.loadInterpolator(getActivity(), android.R.anim.bounce_interpolator));
                    distance_box.startAnimation(ani);

                    get_cut_list(galleryRecycler, loc_data);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(getActivity(), "데이터 가져오기 실패", Toast.LENGTH_LONG).show();
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
                Log.i("USOF",result);
                JSONArray jsonArray = null;
                JSONObject obj = null;
                try {
                    jsonArray = new JSONArray(result);
                    ArrayList<String> cut_first_imageName = new ArrayList<String>();
                    ArrayList<String> cut_id = new ArrayList<String>();
                    ArrayList<String> cut_caption = new ArrayList<String>();
                    ArrayList<Integer> tag_id= new ArrayList<Integer>();
                    JSONArray tag = null;
                    for(int i = 0; i<jsonArray.length();i++){
                        obj = new JSONObject(String.valueOf(jsonArray.get(i)));
                        JSONArray json = new JSONArray(obj.getString("image_name"));
                        cut_first_imageName.add(String.valueOf(json.get(0)));
                        cut_id.add(obj.getString("id"));
                        cut_caption.add(obj.getString("caption"));
                        draw_cut(json.getString(0), obj.getString("location_data"));
                        if (obj.getString("tag_id_list").isEmpty()) {
                            tag_id.add(-1);
                        } else {
                            tag = new JSONArray(obj.getString("tag_id_list"));
                            for(int j = 0 ;j<tag.length();j++){
                                tag_id.add((Integer) tag.get(j));
                            }
                        }
                    }
                    Log.i("USOF",tag_id.toString());
                    HashSet hs = new HashSet(tag_id);
                    ArrayList<String> tag_id_list = new ArrayList<String>(hs);
                    set_tag_list(tag_id_list);

                    Log.i("asfwefw", String.valueOf(cut_first_imageName.size()));

                    cutGalleryAdapter = new CutGalleryAdapter(cut_first_imageName, cut_id, cut_caption, getActivity());
                    galleryRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                    galleryRecycler.setAdapter(cutGalleryAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(getActivity(), "데이터 가져오기 실패", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void set_tag_list(ArrayList<String> tag_id_list){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("tag_id_list", tag_id_list);
        if(tag_id_list.size()==0){
            Toast.makeText(getActivity(),"값 없음",Toast.LENGTH_LONG).show();
            tagRecycler.setVisibility(View.INVISIBLE);
        }else{
            tagRecycler.setVisibility(View.VISIBLE);
            asyncHttpClient.post(SERVIERIP + "get_tag_friend_list", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String result = new String(responseBody);
                    ArrayList<String> tag_name = new ArrayList<String>();
                    ArrayList<String> tag_profile = new ArrayList<String>();
                    try {
                        JSONArray arr = new JSONArray(result);
                        JSONObject obj;
                        for(int i=0;i<arr.length();i++){
                            obj = new JSONObject(String.valueOf(arr.get(i)));
                            tag_name.add(obj.getString("name"));
                            tag_profile.add(obj.getString("profile"));
                        }
                        tagListAdapter = new tagListAdapter(tag_name, tag_profile, getActivity());
                        tagRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                        tagRecycler.setAdapter(tagListAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.w("Join Error", error);
                    Toast.makeText(getActivity(), "데이터", Toast.LENGTH_LONG).show();
                }
            });
        }
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

        class GetImage extends AsyncTask<String,Void,Bitmap>{
            ProgressDialog loading;
            Double lat, lon;

            public GetImage(Double lat, Double lon){
                this.lat = lat;
                this.lon = lon;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Setting...", null,true,true);
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

                Paint color3 = new Paint();
                color2.setTextSize(35);
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
                canvas1.drawBitmap(b, (bmp.getWidth()-b.getWidth())/4+20,(bmp.getHeight()-b.getHeight())/4 , color3);


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
