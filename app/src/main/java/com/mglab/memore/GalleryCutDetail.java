package com.mglab.memore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-05-03.
 */

public class GalleryCutDetail extends AppCompatActivity implements NetDefine {
    String cut_caption,cut_id,cut_image_list,cut_tag_id,share_flag,feed_id = null,first_image = null,my_id = null;
    TextView txt_cut_caption, txt_cut_date;
    CutDetailAdapter galleryCutGridAdapter;
    ImageButton btn_tag_list;
    GridView gridview;
    Button btn_share;
    AsyncHttpClient asyncHttpClient;

    ShareCut shareCut = (ShareCut)ShareCut.sharecut;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_cut_detail);

        Intent intent = getIntent();
        cut_caption = intent.getStringExtra("cut_caption");
        cut_id = intent.getStringExtra("cut_id");
        cut_image_list = intent.getStringExtra("cut_image_list");
        cut_tag_id = intent.getStringExtra("cut_tag_id");
        share_flag = intent.getStringExtra("share_flag");
        btn_share = (Button)findViewById(R.id.btn_share);

        if(share_flag.equals("t")){
            btn_share.setVisibility(View.VISIBLE);
            feed_id = intent.getStringExtra("feed_id");
            my_id = intent.getStringExtra("my_id");
            first_image = intent.getStringExtra("first_image");
        }

        txt_cut_caption = (TextView)findViewById(R.id.cut_caption);
        txt_cut_date = (TextView)findViewById(R.id.cut_date);
        gridview = (GridView) findViewById(R.id.gridview);
        btn_tag_list = (ImageButton)findViewById(R.id.btn_tag_list);

        if(cut_tag_id.isEmpty()){
            btn_tag_list.setVisibility(View.GONE);
        }

        txt_cut_caption.setText(cut_caption);

        DisplayMetrics dm = new DisplayMetrics();
        GalleryCutDetail.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int size = dm.widthPixels / 4;

        galleryCutGridAdapter = new CutDetailAdapter(GalleryCutDetail.this,R.layout.gridview_item,cut_image_list,size);
        gridview.setAdapter(galleryCutGridAdapter);

        gridview.setOnItemClickListener(myOnItemClickListener);

        btn_tag_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryCutDetail.this,FeedCutTagList.class);
                intent.putExtra("tag_list",cut_tag_id);
                startActivity(intent);
            }
        });

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_cut();
            }
        });

        get_cut_date(cut_id);
    }

    public void get_cut_date(String cut_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("cut_id", cut_id);
        asyncHttpClient.post(SERVIERIP + "get_cut_date", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                String[] data = result.split("-");
                txt_cut_date.setText(data[0]+"년 "+data[1]+"월 "+data[2]+"일");

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Log.i("yangmyun","왜");
            }
        });
    }

    public void share_cut(){

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("feed_id", feed_id);
        params.put("cut_id", cut_id);
        params.put("image_name", first_image);
        params.put("my_id", my_id);
        asyncHttpClient.post(SERVIERIP + "add_thumb", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                shareCut.finish();
                finish();

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Log.i("yangmyun","왜");
            }
        });
    }

    AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String prompt = (String) parent.getItemAtPosition(position);
            Intent intent = new Intent(GalleryCutDetail.this,GalleryFullScreen.class);
            intent.putExtra("imagePath", prompt);
            intent.putExtra("flag", "cut");
            startActivity(intent);
        }
    };
}
