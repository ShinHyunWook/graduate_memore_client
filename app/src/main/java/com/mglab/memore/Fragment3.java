package com.mglab.memore;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-03-11.
 */

public class Fragment3 extends Fragment implements NetDefine {
    ImageButton write_feed;
    Button test;
    RecyclerView feedRecyclerView;
    FeedViewModel feedViewModel;
    ArrayList<FeedViewModel> feedViewModelsList = new ArrayList<FeedViewModel>();
    FeedAdapter feedAdapter;
    Bundle bundle;
    AsyncHttpClient asyncHttpClient;
    ScrollView view_scroll;
    String my_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(R.layout.fragment3, container, false);
        bundle = savedInstanceState;
        write_feed = (ImageButton) view.findViewById(R.id.write_feed);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            write_feed.setBackground(new ShapeDrawable(new OvalShape()));
            write_feed.setBackgroundColor(Color.parseColor("#00000000"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            write_feed.setClipToOutline(true);
        }
        test = (Button) view.findViewById(R.id.test);
        feedRecyclerView = (RecyclerView)view.findViewById(R.id.feedRecycler);

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_my_id();
            }
        });

        write_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),WriteFeed.class);
                startActivity(intent);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        get_my_id();
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
                    load_feed(obj.getString("id"));
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

    public void load_feed(final String my_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("my_id", my_id);
        asyncHttpClient.post(SERVIERIP + "get_feed_new", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                Log.i("THun",result);
                feedViewModelsList.clear();
                try {

                    JSONArray arr = new JSONArray(result);
                    Log.i("SANJI",arr.toString());
                    for(int i = 0;i<arr.length();i++){
                        JSONObject obj = new JSONObject(String.valueOf(arr.get(i)));
                        feedViewModel = new FeedViewModel();
                        feedViewModel.setUser_id(obj.getString("user_id"));
                        feedViewModel.setUser_name(obj.getString("name"));
                        feedViewModel.setUser_profile(obj.getString("profile"));
                        feedViewModel.setPost_time(obj.getString("post_time"));
                        feedViewModel.setCut_id_list(obj.getString("cut_id"));
                        feedViewModel.setStart_loc_date(obj.getString("start_loc_date"));
                        feedViewModel.setEnd_loc_date(obj.getString("end_loc_date"));
                        feedViewModel.setFeed_id(obj.getString("id"));
                        feedViewModelsList.add(feedViewModel);
                    }

                    feedAdapter = new FeedAdapter(feedViewModelsList,my_id,getActivity(),bundle);
                    feedRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
                    feedRecyclerView.setAdapter(feedAdapter);

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
