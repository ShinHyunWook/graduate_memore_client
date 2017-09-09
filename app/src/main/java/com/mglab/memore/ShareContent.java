package com.mglab.memore;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-05-05.
 */

public class ShareContent extends AppCompatActivity implements NetDefine {
    String feed_id;
    ListView share_list;
    ShareModel shareModel;
    ArrayList<ShareModel> shareModelArrayList = new ArrayList<ShareModel>();
    AsyncHttpClient asyncHttpClient;
    ShareListviewAdapter shareListviewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_content);

        Intent intent = getIntent();
        feed_id = intent.getStringExtra("feed_id");

        share_list = (ListView) findViewById(R.id.thumb_friend_list);

        set_share_info(feed_id);
    }

    public void set_share_info(String feed_id) {
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("feed_id", feed_id);
        asyncHttpClient.post(SERVIERIP + "get_share_info", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONArray arr = new JSONArray(result);
                    for (int i = 0; i < arr.length(); i++) {
                        shareModel = new ShareModel();
                        JSONObject obj = new JSONObject(String.valueOf(arr.get(i)));

                        if (obj.getString("cut_id").isEmpty()) {
                            shareModel.setCut_id("-1");
                        } else {
                            shareModel.setCut_id(obj.getString("cut_id"));
                        }
                        shareModel.setImage_name(obj.getString("image_name"));
                        shareModel.setUser_id(obj.getString("user_id"));
                        shareModel.setEmail(obj.getString("email"));
                        shareModel.setName(obj.getString("name"));
                        shareModel.setProfile(obj.getString("profile"));
                        get_cut_info(obj.getString("cut_id"), shareModel);
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

    public void get_cut_info(String cut_id, final ShareModel shareModel) {
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("cut_id", cut_id);
        asyncHttpClient.post(SERVIERIP + "get_cut_info", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONArray arr = new JSONArray(result);
                    if(arr.length()==1) {
                        JSONObject obj = new JSONObject(String.valueOf(arr.get(0)));

                        shareModel.setCut_caption(obj.getString("caption"));
                        shareModel.setImage_list(obj.getString("image_name"));
                        shareModel.setTag_id_list(obj.getString("tag_id_list"));
                    }else{
                        shareModel.setCut_caption("");
                        shareModel.setImage_list("");
                        shareModel.setTag_id_list("");
                    }
                    shareModelArrayList.add(shareModel);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                shareListviewAdapter = new ShareListviewAdapter(ShareContent.this, R.layout.item_share, shareModelArrayList);
                share_list.setAdapter(shareListviewAdapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }
}
