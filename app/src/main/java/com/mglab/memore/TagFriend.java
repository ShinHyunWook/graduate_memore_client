package com.mglab.memore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
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
 * Created by 신현욱 on 2017-04-16.
 */

public class TagFriend extends Activity implements NetDefine{
    AsyncHttpClient asyncHttpClient;
    Button tag_complete;
    ListView friend_list;
    String my_id;
    TagListviewAdapter adapter;
    ArrayList<String> check_name;
    ArrayList<String> check_id;
    ImageButton btn_quit;

    final static int FRIEND = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_friend);

        tag_complete = (Button)findViewById(R.id.tag_complete);
        friend_list = (ListView)findViewById(R.id.friend_list);
        btn_quit = (ImageButton)findViewById(R.id.btn_quit);

        btn_quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        check_id = new ArrayList<String>();
        check_name = new ArrayList<String>();
        tag_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_id.clear();
                check_name.clear();
                boolean ischeck[] = adapter.getCheck();
                for(int i = 0;i<adapter.getCount();i++){
                    if(ischeck[i]==true){
                        check_name.add(adapter.getItem(i));
                        check_id.add(adapter.getID(i));
                    }
                }

                Intent tmp = new Intent();
                tmp.putExtra("TagName",check_name);
                tmp.putExtra("TagID",check_id);
                setResult(400,tmp);
                finish();
            }
        });

        get_my_id();
    }

    public void get_my_id() {
        String u_email;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(TagFriend.this);
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
                    set_friend_list();
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

    public void set_friend_list(){

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("my_id", my_id);
        asyncHttpClient.post(SERVIERIP + "get_friend_list", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    ListView friend_list=(ListView)findViewById(R.id.friend_list);
                    friend_list.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
                    ArrayList<TagFriendItem> friend_data = new ArrayList<>();
                    JSONArray array = new JSONArray(result);
                    for(int i = 0 ;i<array.length();i++){
                        JSONObject data = array.getJSONObject(i);
                        TagFriendItem item = new TagFriendItem(data.getString("profile"),data.getString("name"),data.getString("email"),data.getString("id"));
                        friend_data.add(item);
                    }
                    if(array.length()<=0){
                        TextView none_message = (TextView)findViewById(R.id.none_message);
                        none_message.setVisibility(View.VISIBLE);
                    }else{
                        adapter = new TagListviewAdapter(TagFriend.this,R.layout.item_tag_friend,friend_data,my_id,FRIEND);
                        friend_list.setAdapter(adapter);
                    }


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
