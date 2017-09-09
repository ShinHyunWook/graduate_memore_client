package com.mglab.memore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-05-04.
 */

public class ShareCut extends AppCompatActivity implements NetDefine{

    public static Activity sharecut;

    String my_id, feed_id;
    AsyncHttpClient asyncHttpClient;
    GalleryCutModel galleryCutModel,cutModel_item;
    ArrayList<GalleryCutModel> galleryCutModelList = new ArrayList<GalleryCutModel>();
    GalleryCutGridAdapter galleryCutGridAdapter;
    GridView gridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_cut);

        sharecut = ShareCut.this;

        gridview = (GridView) findViewById(R.id.gridview);

        Intent intent = getIntent();
        my_id = intent.getStringExtra("my_id");
        feed_id = intent.getStringExtra("feed_id");

        get_cut_list(my_id);

        gridview.setOnItemClickListener(myOnItemClickListener);
    }

    AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            cutModel_item = new GalleryCutModel();
            cutModel_item = (GalleryCutModel)parent.getItemAtPosition(position);

            Intent intent = new Intent(ShareCut.this,GalleryCutDetail.class);

            intent.putExtra("cut_caption", cutModel_item.getCaption());
            intent.putExtra("cut_id", cutModel_item.getId());
            intent.putExtra("cut_image_list", cutModel_item.getImage_list());
            intent.putExtra("cut_tag_id", cutModel_item.getTag_id_list());
            intent.putExtra("first_image", cutModel_item.getFirst_image());
            intent.putExtra("feed_id", feed_id);
            intent.putExtra("my_id", my_id);
            intent.putExtra("share_flag", "t");

            startActivity(intent);
        }
    };

    public void get_cut_list(String my_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("my_id", my_id);
        asyncHttpClient.post(SERVIERIP + "get_cut_list", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONArray arr = new JSONArray(result);
                    for(int i = 0;i<arr.length();i++){
                        galleryCutModel = new GalleryCutModel();
                        JSONObject obj = new JSONObject(String.valueOf(arr.get(i)));
                        galleryCutModel.setId(obj.getString("id"));
                        galleryCutModel.setImage_list(obj.getString("image_name"));
                        galleryCutModel.setCaption(obj.getString("caption"));
                        galleryCutModel.setTag_id_list(obj.getString("tag_id_list"));
                        JSONArray tmp = new JSONArray(obj.getString("image_name"));
                        galleryCutModel.setFirst_image(String.valueOf(tmp.get(0)));
                        galleryCutModelList.add(galleryCutModel);
                    }
                    DisplayMetrics dm = new DisplayMetrics();
                    ShareCut.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int size = dm.widthPixels / 4;

                    Log.i("ZORO",galleryCutModelList.get(0).getFirst_image());

                    galleryCutGridAdapter = new GalleryCutGridAdapter(ShareCut.this,R.layout.gridview_item,galleryCutModelList,size,"share");
                    gridview.setAdapter(galleryCutGridAdapter);

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
