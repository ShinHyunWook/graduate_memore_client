//package com.mglab.memore;
//
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.loopj.android.http.AsyncHttpClient;
//import com.loopj.android.http.AsyncHttpResponseHandler;
//import com.loopj.android.http.RequestParams;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
//import cz.msebera.android.httpclient.Header;
//
///**
// * Created by 신현욱 on 2017-03-30.
// */
//
//public class FriendRequestList extends AppCompatActivity implements NetDefine{
//    private String my_id;
//    private AsyncHttpClient asyncHttpClient;
//    final static int REQUEST = 1;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_friend_request);
//
//        get_my_id();
//    }
//
//    public void set_request_list(){
//        asyncHttpClient = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        params.put("my_id",my_id);
//        asyncHttpClient.post(SERVIERIP + "get_friend_request_list", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String result = new String(responseBody);
//                try {
//                    ListView friend_list=(ListView)findViewById(R.id.friend_list);
//                    ArrayList<FriendItem> friend_data = new ArrayList<>();
//                    JSONArray array = new JSONArray(result);
//                    for(int i = 0 ;i<array.length();i++){
//                        JSONObject data = array.getJSONObject(i);
//                        FriendItem item = new FriendItem(data.getString("profile"),data.getString("name"),data.getString("email"));
//                        friend_data.add(item);
//                    }
//                    if(array.length()<=0){
//                        TextView none_message = (TextView)findViewById(R.id.none_message);
//                        none_message.setVisibility(View.VISIBLE);
//                    }else{
//                        ListviewAdapter adapter = new ListviewAdapter(FriendRequestList.this,R.layout.item_friend_request,friend_data,my_id,REQUEST);
//                        friend_list.setAdapter(adapter);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Log.w("Join Error", error);
//            }
//        });
//    }
//
//    public void get_my_id() {
//        String u_email;
//
//        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(FriendRequestList.this);
//        u_email = sharedPreferencesManager.getLogin();
//
//        asyncHttpClient = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        params.put("email", u_email);
//        asyncHttpClient.post(SERVIERIP + "search_person", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                String result = new String(responseBody);
//                JSONObject obj = null;
//                try {
//                    obj = new JSONObject(result);
//                    my_id = obj.getString("id");
//                    set_request_list();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                Log.w("Join Error", error);
//            }
//        });
//    }
//}
