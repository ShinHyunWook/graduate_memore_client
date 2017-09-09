package com.mglab.memore;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.w3c.dom.Text;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-04-22.
 */

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter .ViewHolder> implements NetDefine {

    public List<FeedViewModel> feedViewModelList;
    private Context mContext;
    DisplayImageOptions options;
    DisplayImageOptions options_people;
    Bundle bundle;
    String my_id;
    int current_position=0;

    public FeedAdapter(List<FeedViewModel> feedViewModelList,String my_id,Context context,Bundle bundle){
        this.mContext = context;
        this.bundle = bundle;
        this.feedViewModelList = feedViewModelList;
        this.my_id = my_id;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading) // 로딩중에 나타나는 이미지
                .showImageForEmptyUri(R.drawable.loading) // 값이 없을때
                .showImageOnFail(R.drawable.loading) // 에러 났을때 나타나는 이미지
                .cacheInMemory(true)
                .considerExifParams(true)
                .build();

        options_people = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.unknown) // 로딩중에 나타나는 이미지
                .showImageForEmptyUri(R.drawable.unknown) // 값이 없을때
                .showImageOnFail(R.drawable.unknown) // 에러 났을때 나타나는 이미지
                .cacheInMemory(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public FeedAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feed, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FeedAdapter.ViewHolder holder, int position) {
        holder.setImageView(feedViewModelList.get(position),bundle);
    }

    @Override
    public int getItemCount() {
        return feedViewModelList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback{

        ImageView feed_profile,first_profile;
        TextView feed_user_name,feed_date,share_text_name,share_text_number,txt_next_name,txt_end;
        LinearLayout share_form;
        private MapView mapView;
        private GoogleMap googleMap;
        AsyncHttpClient asyncHttpClient;
        private PolylineOptions polylineOptions;
        ImageView feed_cut_1,feed_cut_2,feed_cut_3;
        private final ArrayList<MarkerOptions> markerOptions = new ArrayList<MarkerOptions>();
        private Marker myLocation;

        public ViewHolder(View itemView) {
            super(itemView);

            feed_profile = (ImageView)itemView.findViewById(R.id.feed_profile);
            first_profile = (ImageView)itemView.findViewById(R.id.first_profile);
            feed_user_name = (TextView)itemView.findViewById(R.id.feed_user_name);
            feed_date = (TextView)itemView.findViewById(R.id.feed_date);
            share_text_name = (TextView)itemView.findViewById(R.id.share_text_name);
            share_text_number = (TextView)itemView.findViewById(R.id.share_text_number);
            txt_next_name = (TextView)itemView.findViewById(R.id.txt_next_name);
            txt_end = (TextView)itemView.findViewById(R.id.txt_end);
            share_form = (LinearLayout)itemView.findViewById(R.id.share_form);
            mapView = (MapView) itemView.findViewById(R.id.map);
            feed_cut_1 = (ImageView)itemView.findViewById(R.id.feed_cut_1);
            feed_cut_2 = (ImageView)itemView.findViewById(R.id.feed_cut_2);
            feed_cut_3 = (ImageView)itemView.findViewById(R.id.feed_cut_3);

            final ScrollView view_scroll = (ScrollView)itemView.findViewById(R.id.view_scroll);
            ImageView transparent = (ImageView)itemView.findViewById(R.id.imagetrans) ;
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

        }

        public void setImageView(final FeedViewModel feedViewModel, Bundle bundle) {
            set_share_info(feedViewModel.getFeed_id());
            get_cut_image(feedViewModel.getCut_id_list());
            String imgUrl = "http://" + IP + "/memore/uploads/"+feedViewModel.getUser_profile();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(imgUrl, feed_profile, options_people);

            feed_user_name.setText(feedViewModel.getUser_name());
            feed_date.setText(feedViewModel.getPost_time());
            mapView.onCreate(bundle);
            mapView.onResume();
            mapView.getMapAsync(this);
            RelativeLayout feed_body = (RelativeLayout)itemView.findViewById(R.id.feed_body);
            feed_body.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), FeedContent.class);
                    intent.putExtra("feed_id", feedViewModel.getFeed_id());
                    v.getContext().startActivity(intent);
                }
            });

            final ImageButton delete_thumb = (ImageButton)itemView.findViewById(R.id.delete_thumb);
            final ImageButton add_thumb = (ImageButton)itemView.findViewById(R.id.add_thumb);
            add_thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSettingsAlert(feedViewModel,add_thumb,delete_thumb);
                }
            });

            delete_thumb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_thumb(feedViewModel,add_thumb,delete_thumb);
                }
            });

            set_btn_visible(feedViewModel,add_thumb,delete_thumb);
        }

        public void set_share_info(String feed_id){
            asyncHttpClient = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            params.put("feed_id", feed_id);
            asyncHttpClient.post(SERVIERIP + "get_share_info", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String result = new String(responseBody);
                    try {
                        JSONArray arr = new JSONArray(result);
                        if(arr.length()>=1){
                            share_form.setVisibility(View.VISIBLE);
                        }else{
                            share_form.setVisibility(View.GONE);
                        }
                        for(int i = 0;i<arr.length();i++){
                            JSONObject obj = new JSONObject(String.valueOf(arr.get(i)));

                            String imgUrl = "http://" + IP + "/memore/uploads/"+obj.getString("profile");
                            ImageLoader imageLoader = ImageLoader.getInstance();
                            imageLoader.displayImage(imgUrl, first_profile, options_people);

                            share_text_name.setText(obj.getString("name"));
                            Log.i("PLATE", String.valueOf(arr.length()));
                            if(arr.length()==1){
                                txt_next_name.setText("님이 이 경로에서의 기억을 공유하였습니다.");
                                txt_end.setVisibility(View.GONE);
                                share_text_number.setVisibility(View.GONE);
                            }else{
                                txt_end.setVisibility(View.VISIBLE);
                                share_text_number.setVisibility(View.VISIBLE);
                                txt_next_name.setText("님 외 ");
                                share_text_number.setText(arr.length()-1+"명");
                                txt_end.setText("이 이 경로에서의 기억을 공유하였습니다.");
                            }
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

        public void showSettingsAlert(final FeedViewModel feedViewModel, final ImageButton add_thumb, final ImageButton delete_thumb) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            alertDialog.setTitle("컷 공유 확인");
            alertDialog.setMessage("'가봤어요'와 함께 당신의 기억을 공유 하시겠습니까?");
            // OK 를 누르게 되면 설정창으로 이동합니다.
            alertDialog.setPositiveButton("컷 공유", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(mContext,ShareCut.class);
                    intent.putExtra("my_id",my_id);
                    intent.putExtra("feed_id",feedViewModel.getFeed_id());
                    mContext.startActivity(intent);
                }
            });
            // Cancle 하면 종료 합니다.
            alertDialog.setNegativeButton("가봤어요만 표시", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    add_thumb(feedViewModel,add_thumb,delete_thumb);
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }

        public void set_btn_visible(FeedViewModel feedViewModel, final ImageButton add_thumb, final ImageButton delete_thumb){
            asyncHttpClient = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            params.put("feed_id", feedViewModel.getFeed_id());
            params.put("user_id", my_id);
            asyncHttpClient.post(SERVIERIP + "set_thumb_status", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String result = new String(responseBody);
                    if(result.equals("100")){
                        delete_thumb.setVisibility(View.VISIBLE);
                        add_thumb.setVisibility(View.INVISIBLE);
                    }else if(result.equals("200")){
                        add_thumb.setVisibility(View.VISIBLE);
                        delete_thumb.setVisibility(View.INVISIBLE);

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.w("Join Error", error);
                    Log.i("THun", String.valueOf(error));
                }
            });
        }

        public void delete_thumb(final FeedViewModel feedViewModel, ImageButton add_thumb, ImageButton delete_thumb){
            add_thumb.setVisibility(View.VISIBLE);
            delete_thumb.setVisibility(View.INVISIBLE);

            asyncHttpClient = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            params.put("feed_id", feedViewModel.getFeed_id());
            params.put("user_id", my_id);
            asyncHttpClient.post(SERVIERIP + "delete_thumb", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String result = new String(responseBody);
                    set_share_info(feedViewModel.getFeed_id());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Log.w("Join Error", error);
                    Log.i("THun", String.valueOf(error));
                }
            });
        }

        public void add_thumb(final FeedViewModel feedViewModel, ImageButton add_thumb, ImageButton delete_thumb){
            add_thumb.setVisibility(View.INVISIBLE);
            delete_thumb.setVisibility(View.VISIBLE);

            asyncHttpClient = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            params.put("feed_id", feedViewModel.getFeed_id());
            params.put("my_id", my_id);
            asyncHttpClient.post(SERVIERIP + "add_thumb", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String result = new String(responseBody);
                    set_share_info(feedViewModel.getFeed_id());
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
                            Log.i("MOCOMOCO", String.valueOf(i));
                            String imgUrl = "http://" + IP + "/memore/uploads/"+obj.getString("image_name");
                            ImageLoader imageLoader = ImageLoader.getInstance();
                            if(arr.get(i)!=""){
                                if(i==0){
                                    imageLoader.displayImage(imgUrl, feed_cut_1, options);
                                    feed_cut_1.setVisibility(View.VISIBLE);
                                }else if(i==1){
                                    imageLoader.displayImage(imgUrl, feed_cut_2, options);
                                    feed_cut_2.setVisibility(View.VISIBLE);
                                }else if(i==2){
                                    imageLoader.displayImage(imgUrl, feed_cut_3, options);
                                    feed_cut_3.setVisibility(View.VISIBLE);
                                    if(arr.length()>3){
                                        ImageView text_Layer = (ImageView)itemView.findViewById(R.id.text_Layer);
                                        TextView more_cut_txt = (TextView)itemView.findViewById(R.id.more_cut_txt);
                                        more_cut_txt.setText("+"+(arr.length()-3));
                                        more_cut_txt.setVisibility(View.VISIBLE);
                                        text_Layer.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    continue;
                                }
                            }
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
//                loading = ProgressDialog.show(getActivity(), "Setting...", null,true,true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
//                loading.dismiss();
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


        @Override
        public void onMapReady(GoogleMap googleMap) {
            this.googleMap = googleMap;
            DrawLine(feedViewModelList.get(current_position).getUser_id(),feedViewModelList.get(current_position).getStart_loc_date(),feedViewModelList.get(current_position).getEnd_loc_date());
        }

        public void DrawLine(String u_id,String start_date,String end_date) {
            googleMap.clear();
            asyncHttpClient = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            params.put("my_id", u_id);
            params.put("start_loc_date", start_date);
            params.put("end_loc_date", end_date);

            current_position++;
            if(current_position==feedViewModelList.size()){
                current_position = 0;
            }

            asyncHttpClient.post(SERVIERIP + "get_location_between_id", params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String result = new String(responseBody);
                    Log.i("DATETESST",result);
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
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));

//                        polylineOptions = new PolylineOptions();
//                        polylineOptions.color(Color.GREEN);
//                        polylineOptions.width(10);
//                        polylineOptions.addAll(loc_json);
//                        googleMap.addPolyline(polylineOptions);
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(loc_json.get(0)));
//                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
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
    }
}

