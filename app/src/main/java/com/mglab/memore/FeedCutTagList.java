package com.mglab.memore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-04-27.
 */

public class FeedCutTagList extends Activity implements NetDefine {
    Button btn_close;
    AsyncHttpClient asyncHttpClient;
    ThumbTagModel thumbTagModel;
    ArrayList<ThumbTagModel> thumbTagModelList = new ArrayList<ThumbTagModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.feed_cut_tag_list);

        //데이터 가져오기
        Intent intent = getIntent();
        String data = intent.getStringExtra("tag_list");

        btn_close = (Button)findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)v.getContext()).finish();
            }
        });

        set_tag_list(data);
    }

    public void set_tag_list(String tag_list){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("tag_list",tag_list);

        asyncHttpClient.post(SERVIERIP + "get_tag_info", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    ListView tag_friend_list = (ListView)findViewById(R.id.tag_friend_list);
                    JSONArray arr = new JSONArray(result);
                    for(int i =0;i<arr.length();i++){
                        thumbTagModel = new ThumbTagModel();
                        JSONObject obj = new JSONObject(String.valueOf(arr.get(i)));
                        thumbTagModel.setUser_id(obj.getString("id"));
                        thumbTagModel.setProfile(obj.getString("profile"));
                        thumbTagModel.setName(obj.getString("name"));
                        thumbTagModel.setEmail(obj.getString("email"));
                        thumbTagModelList.add(thumbTagModel);
                    }
                    ThumTagListviewAdapter adapter = new ThumTagListviewAdapter(FeedCutTagList.this,R.layout.item_thumb_tag,thumbTagModelList);
                    tag_friend_list.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(FeedCutTagList.this, "데이터 가져오기 실패", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
