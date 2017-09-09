package com.mglab.memore;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-03-30.
 */

public class FriendActivity extends AppCompatActivity implements NetDefine{
    Button f_search, f_list,f_request_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        f_search = (Button)findViewById(R.id.f_search);
        f_list = (Button)findViewById(R.id.f_list);
        f_request_list = (Button)findViewById(R.id.f_request_list);


        f_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendActivity.this,FriendSearchActivity.class);
                startActivity(intent);
            }
        });

        f_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendActivity.this,FriendListActivity.class);
                startActivity(intent);
            }
        });

//        f_request_list.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(FriendActivity.this,FriendRequestList.class);
//                startActivity(intent);
//            }
//        });
    }
}
