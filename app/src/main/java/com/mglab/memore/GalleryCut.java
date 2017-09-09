package com.mglab.memore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-05-02.
 */

public class GalleryCut extends AppCompatActivity implements NetDefine{
    AsyncHttpClient asyncHttpClient;
    GalleryCutGridAdapter galleryCutGridAdapter;
    Button btn_folder,btn_cut;
    GalleryCutModel galleryCutModel;
    ArrayList<GalleryCutModel> galleryCutModelList = new ArrayList<GalleryCutModel>();
    GridView gridview;
    LinearLayout gallery_line,cut_line;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_cuts);

        btn_folder = (Button)findViewById(R.id.btn_folder);
        btn_cut = (Button)findViewById(R.id.btn_cut);
        gridview = (GridView) findViewById(R.id.gridview);
        gallery_line = (LinearLayout)findViewById(R.id.gallery_line);
        cut_line = (LinearLayout)findViewById(R.id.cut_line);

        gallery_line.setVisibility(View.GONE);
        cut_line.setVisibility(View.VISIBLE);

        btn_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryCut.this,GalleryImages.class);
                startActivity(intent);
                finish();
            }
        });

        gridview.setOnItemClickListener(myOnItemClickListener);

        get_my_id();
    }

    AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GalleryCutModel prompt = (GalleryCutModel) parent.getItemAtPosition(position);
            Intent intent = new Intent(GalleryCut.this,GalleryCutDetail.class);

            intent.putExtra("cut_caption", prompt.getCaption());
            intent.putExtra("cut_id", prompt.getId());
            intent.putExtra("cut_image_list", prompt.getImage_list());
            intent.putExtra("cut_tag_id", prompt.getTag_id_list());
            intent.putExtra("share_flag", "f");

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
                    GalleryCut.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int size = dm.widthPixels / 4;

                    Log.i("ZORO",galleryCutModelList.get(0).getFirst_image());

                    galleryCutGridAdapter = new GalleryCutGridAdapter(GalleryCut.this,R.layout.gridview_item,galleryCutModelList,size,"gallery");
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

    public void get_my_id() {
        String u_email;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(GalleryCut.this);
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
                    get_cut_list(obj.getString("id"));
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




