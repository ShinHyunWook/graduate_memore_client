package com.mglab.memore;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-04-26.
 */

public class FeedContent extends AppCompatActivity implements OnMapReadyCallback, NetDefine {
    AsyncHttpClient asyncHttpClient;
    DisplayImageOptions options;
    ImageView feed_profile,arrow_up,arrow_down;
    TextView feed_user_name, feed_post_date;
    private MapView mapView;
    private GoogleMap googleMap;
    private PolylineOptions polylineOptions;
    private List<LatLng> arrayPoints;
    String select_date_start,select_date_end,user_id,my_id;
    private final ArrayList<MarkerOptions> markerOptions = new ArrayList<MarkerOptions>();
    private Marker myLocation;
    ListView cut_list;
    FeedCutModel feedCutModel;
    TextView actionbar_titme;
    ScrollView view_scroll;
    RelativeLayout thumb_bar;
    ArrayList<FeedCutModel> feedCutModelList = new ArrayList<FeedCutModel>();
    TextView route_date,thumb_bar_info;
    ImageButton add_thumb,delete_thumb;

    String feed_id;
    boolean arrow_flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        setContentView(R.layout.feed_content);

        get_my_id();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(FeedContent.this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.unknown) // 로딩중에 나타나는 이미지
                .showImageForEmptyUri(R.drawable.unknown) // 값이 없을때
                .showImageOnFail(R.drawable.unknown) // 에러 났을때 나타나는 이미지
                .cacheInMemory(true)
                .considerExifParams(true)
                .build();

        Intent intent = getIntent();
        feed_id = intent.getStringExtra("feed_id");

        feed_profile = (ImageView) findViewById(R.id.feed_profile);
        feed_user_name = (TextView) findViewById(R.id.feed_user_name);
        feed_post_date = (TextView) findViewById(R.id.feed_post_date);
        cut_list = (ListView)findViewById(R.id.cut_list);
        actionbar_titme = (TextView)findViewById(R.id.actionbar_titme);
        route_date = (TextView)findViewById(R.id.route_date);
        arrow_up = (ImageView)findViewById(R.id.arrow_up);
        arrow_down = (ImageView)findViewById(R.id.arrow_down);

        add_thumb = (ImageButton)findViewById(R.id.add_thumb);
        delete_thumb = (ImageButton)findViewById(R.id.delete_thumb);

        thumb_bar_info = (TextView)findViewById(R.id.thumb_bar_info);

        add_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsAlert(feed_id,add_thumb,delete_thumb);
            }
        });

        delete_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete_thumb(feed_id,my_id);
            }
        });

        view_scroll = (ScrollView)findViewById(R.id.view_scroll);
        ImageView transparent = (ImageView)findViewById(R.id.imagetrans);

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
        thumb_bar = (RelativeLayout)findViewById(R.id.thumb_bar);
        thumb_bar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                Intent intent = new Intent(FeedContent.this,ShareContent.class);
                intent.putExtra("feed_id",feed_id);
                startActivity(intent);

            }
        });


        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

    }

    public void showSettingsAlert(final String feed_id, final ImageButton add_thumb, final ImageButton delete_thumb) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FeedContent.this);

        alertDialog.setTitle("컷 공유 확인");
        alertDialog.setMessage("'가봤어요'와 함께 당신의 기억을 공유 하시겠습니까?");
        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("컷 공유", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(FeedContent.this,ShareCut.class);
                intent.putExtra("my_id",my_id);
                intent.putExtra("feed_id",feed_id);
                FeedContent.this.startActivity(intent);
            }
        });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("가봤어요만 표시", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                add_thumb(feed_id,my_id);
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void set_feed_content(final String feed_id) {
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("feed_id", feed_id);
        asyncHttpClient.post(SERVIERIP + "set_feed_content", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONArray arr = new JSONArray(result);
                    JSONObject obj = new JSONObject(String.valueOf(arr.get(0)));
                    String imgUrl = "http://" + IP + "/memore/uploads/" + obj.getString("profile");
                    ImageLoader imageLoader = ImageLoader.getInstance();

                    select_date_start = obj.getString("start_loc_date");
                    select_date_end = obj.getString("end_loc_date");
                    user_id = obj.getString("user_id");

                    imageLoader.displayImage(imgUrl, feed_profile, options);
                    feed_post_date.setText(obj.getString("post_time"));
                    feed_user_name.setText(obj.getString("name"));
                    actionbar_titme.setText(obj.getString("name")+"님의 기억");

                    String[] s_data = select_date_start.split("-");
                    String[] e_data = select_date_end.split("-");
                    route_date.setText(s_data[0]+"년 "+s_data[1]+"월 "+s_data[2]+"일 ~ "+e_data[0]+"년 "+e_data[1]+"월 "+e_data[2]+"일");

                    DrawLine();
                    get_cut_image(obj.getString("cut_id"));
                    set_cut_list_view(obj.getString("cut_id"));
                    set_btn_visible(feed_id,my_id);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("THun", String.valueOf(e));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Log.i("THun", String.valueOf(error));
            }
        });
    }

    public void get_my_id() {
        String u_email;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(FeedContent.this);
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
                    set_feed_content(feed_id);
                    set_thumb_friend_list(feed_id);
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

    public void set_btn_visible(String feed_id, String my_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("feed_id", feed_id);
        params.put("user_id", my_id);
        asyncHttpClient.post(SERVIERIP + "set_thumb_status", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                if(result.equals("100")){
                    delete_thumb.setVisibility(View.VISIBLE);
                    add_thumb.setVisibility(View.GONE);
                }else if(result.equals("200")){
                    add_thumb.setVisibility(View.VISIBLE);
                    delete_thumb.setVisibility(View.GONE);

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Log.i("THun", String.valueOf(error));
            }
        });
    }

    public void delete_thumb(final String feed_id, String my_id){
        add_thumb.setVisibility(View.VISIBLE);
        delete_thumb.setVisibility(View.GONE);

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("feed_id", feed_id);
        params.put("user_id", my_id);
        asyncHttpClient.post(SERVIERIP + "delete_thumb", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                set_thumb_friend_list(feed_id);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Log.i("THun", String.valueOf(error));
            }
        });
    }

    public void add_thumb(final String feed_id, String my_id){
        add_thumb.setVisibility(View.GONE);
        delete_thumb.setVisibility(View.VISIBLE);

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("feed_id", feed_id);
        params.put("my_id", my_id);
        asyncHttpClient.post(SERVIERIP + "add_thumb", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                set_thumb_friend_list(feed_id);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Log.i("THun", String.valueOf(error));
            }
        });
    }

    public void set_cut_list_view(String cut_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("cut_id", cut_id);
        asyncHttpClient.post(SERVIERIP + "get_cut_images", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONArray arr = new JSONArray(result);
                    for(int i = 0;i<arr.length();i++){
                        feedCutModel = new FeedCutModel();
                        JSONObject obj = new JSONObject(String.valueOf(arr.get(i)));
                        feedCutModel.setImage_name(obj.getString("image_name"));
                        feedCutModel.setCaption(obj.getString("caption"));
                        feedCutModel.setTag_list(obj.getString("tag_id_list"));
                        feedCutModelList.add(feedCutModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                CutListviewAdapter adapter = new CutListviewAdapter(FeedContent.this,R.layout.item_feed_content_cut,feedCutModelList);
                cut_list.setAdapter(adapter);
                setListViewHeightBasedOnChildren(cut_list);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
            }
        });
    }
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(37.496375, 126.957448)));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.getMapAsync(this);
        set_thumb_friend_list(feed_id);
        set_btn_visible(feed_id,my_id);
    }

    public void DrawLine() {
        googleMap.clear();

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("my_id", user_id);
        params.put("start_loc_date", select_date_start);
        params.put("end_loc_date", select_date_end);
        asyncHttpClient.post(SERVIERIP + "get_location_between_id", params, new AsyncHttpResponseHandler() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                Log.i("LUFY",result);
                JSONObject temp = null;
                try {
                    ArrayList<String> loc_data = new ArrayList<String>();
                    List<LatLng> loc_json = new ArrayList<LatLng>();
                    polylineOptions = new PolylineOptions();
                    JSONArray total_tmp = new JSONArray(result);

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
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(18));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(FeedContent.this, "데이터 가져오기 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void set_thumb_friend_list(String feed_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("feed_id", feed_id);
        asyncHttpClient.post(SERVIERIP + "get_share_info", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONArray arr = new JSONArray(result);
                    String last_name = "";
                    Log.i("SHIT!!", String.valueOf(arr.length()));

                    if(arr.length()==1){
                        JSONObject obj = new JSONObject(String.valueOf(arr.get(arr.length()-1)));
                        last_name = obj.getString("name");
                        thumb_bar.setVisibility(View.VISIBLE);
                        thumb_bar_info.setText(last_name+"님이 이 경로에서의 기억을 공유하였습니다." );

                    }else if(arr.length()>1){
                        JSONObject obj = new JSONObject(String.valueOf(arr.get(arr.length()-1)));
                        last_name = obj.getString("name");
                        thumb_bar.setVisibility(View.VISIBLE);
                        thumb_bar_info.setText(last_name+"님 외 "+String.valueOf(arr.length()-1)+"명이 이 경로에서의 기억을 공유하였습니다." );
                    }else{
                        thumb_bar.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Log.i("THun", String.valueOf(error));
            }
        });
    }

    public void get_cut_image(String cut_list){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("cut_list", cut_list);
        asyncHttpClient.post(SERVIERIP + "get_cut_image_cut_id", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONArray arr = new JSONArray(result);
                    for(int i = 0;i<arr.length();i++){
                        JSONObject obj = new JSONObject(String.valueOf(arr.get(i)));
                        draw_cut(obj.getString("image_name"), obj.getString("location_data"));
                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.i("SANTA", String.valueOf(e));
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Log.i("THun", String.valueOf(error));
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
            loading = ProgressDialog.show(FeedContent.this, "Setting...", null,true,true);
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



//        @Override
//        protected void onPostExecute(Bitmap b) {
//            super.onPostExecute(b);
//            loading.dismiss();
//            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//            Bitmap bmp = Bitmap.createBitmap(200, 200, conf);
//            Canvas canvas1 = new Canvas(bmp);
//
//            Paint color = new Paint();
//            color.setTextSize(35);
//            color.setColor(Color.BLACK);
//
//            canvas1.drawBitmap(b, 0, 0, color);
//            Log.i("LUFY", String.valueOf(lat));
//            markerOptions.add(new MarkerOptions().position(new LatLng(lat, lon))
//                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
//                    // Specifies the anchor to be at a particular point in the marker image.
//                    .anchor(0.5f, 1));
//
//            myLocation = googleMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
//                    .icon(BitmapDescriptorFactory.fromBitmap(bmp))
//                    // Specifies the anchor to be at a particular point in the marker image.
//                    .anchor(0.5f, 1));
//        }

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
