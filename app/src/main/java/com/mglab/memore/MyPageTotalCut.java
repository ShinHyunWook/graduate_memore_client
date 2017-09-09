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
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-05-05.
 */

public class MyPageTotalCut extends AppCompatActivity implements NetDefine {

    AsyncHttpClient asyncHttpClient;
    GalleryCutGridAdapter galleryCutGridAdapter;
    GalleryCutModel galleryCutModel;
    ArrayList<GalleryCutModel> galleryCutModelList = new ArrayList<GalleryCutModel>();
    GridView gridview;
    String select_date_start,select_date_end,my_id;
    TextView txt_data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mypage_total_cut);

        gridview = (GridView) findViewById(R.id.gridview);
        txt_data = (TextView)findViewById(R.id.txt_data);

        Intent intent = getIntent();
        select_date_start = intent.getStringExtra("select_date_start");
        select_date_end = intent.getStringExtra("select_date_end");
        my_id = intent.getStringExtra("my_id");

        String[] s_data = select_date_start.split("-");
        String[] e_data = select_date_end.split("-");
        txt_data.setText(s_data[0]+"년 "+s_data[1]+"월 "+s_data[2]+"일 ~ "+e_data[0]+"년 "+e_data[1]+"월 "+e_data[2]+"일");

        get_cut_list(select_date_start,select_date_end,my_id);

        gridview.setOnItemClickListener(myOnItemClickListener);
    }

    AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            GalleryCutModel prompt = (GalleryCutModel) parent.getItemAtPosition(position);
            Intent intent = new Intent(MyPageTotalCut.this,GalleryCutDetail.class);

            intent.putExtra("cut_caption", prompt.getCaption());
            intent.putExtra("cut_id", prompt.getId());
            intent.putExtra("cut_image_list", prompt.getImage_list());
            intent.putExtra("cut_tag_id", prompt.getTag_id_list());
            intent.putExtra("share_flag", "f");

            startActivity(intent);
        }
    };


    public void get_cut_list(String select_date_start,String select_date_end,String my_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("my_id", my_id);
        params.put("select_date_start", select_date_start);
        params.put("select_date_end", select_date_end);

        asyncHttpClient.post(SERVIERIP + "mypage_cut_list", params, new AsyncHttpResponseHandler() {
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
                    MyPageTotalCut.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int size = dm.widthPixels / 4;

                    galleryCutGridAdapter = new GalleryCutGridAdapter(MyPageTotalCut.this,R.layout.gridview_item,galleryCutModelList,size,"gallery");
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
