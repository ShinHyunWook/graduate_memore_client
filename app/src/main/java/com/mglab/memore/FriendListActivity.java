package com.mglab.memore;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

public class FriendListActivity extends AppCompatActivity implements NetDefine {
    private String my_id;
    private AsyncHttpClient asyncHttpClient;
    final static int REQUEST = 1;
    LinearLayout search_line, list_line,search_result;
    Button btm_search;
    ImageButton btn_back,btn_close_result;
    TextView request_number;
    RelativeLayout friend_request_top;
    ImageView request_arrow;
    ListView friend_list;
    ListView search_list;
    ListView request_list;
    TextView none_message;
    boolean arrow_flag = true;

    EditText insert_name;
    ImageButton search_friend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        request_number = (TextView) findViewById(R.id.request_number);
        search_line = (LinearLayout) findViewById(R.id.search_line);
        list_line = (LinearLayout) findViewById(R.id.list_line);
        search_result = (LinearLayout) findViewById(R.id.search_result);
        friend_request_top = (RelativeLayout) findViewById(R.id.friend_request_top);
        request_arrow = (ImageView) findViewById(R.id.request_arrow);
        request_list = (ListView) findViewById(R.id.friend_request_list);
        friend_list = (ListView) findViewById(R.id.friend_list);

        search_list = (ListView) findViewById(R.id.search_list);
        none_message = (TextView) findViewById(R.id.none_message);

        insert_name = (EditText)findViewById(R.id.insert_name);
        search_friend = (ImageButton)findViewById(R.id.search_friend);

        search_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(FriendListActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(insert_name.getWindowToken(), 0);
                String name = String.valueOf(insert_name.getText());
                search_friend_list(name);
            }
        });

        search_line.setVisibility(View.GONE);
        list_line.setVisibility(View.VISIBLE);

        btm_search = (Button) findViewById(R.id.btm_search);
        btm_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendListActivity.this, FriendSearchActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_close_result = (ImageButton) findViewById(R.id.btn_close_result);
        btn_close_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_result.setVisibility(View.GONE);
                set_friend_list();
            }
        });


        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        friend_request_top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrow_flag) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        request_arrow.setImageDrawable(getDrawable(R.drawable.barrow_up));
                    }
                    ValueAnimator anim = ValueAnimator.ofInt(request_list.getMeasuredHeight(), 600);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = request_list.getLayoutParams();
                            layoutParams.height = val;
                            request_list.setLayoutParams(layoutParams);
                        }
                    });
                    anim.setDuration(1000);
                    anim.start();
                    arrow_flag = false;
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        request_arrow.setImageDrawable(getDrawable(R.drawable.barrow_down));
                    }
                    ValueAnimator anim = ValueAnimator.ofInt(request_list.getMeasuredHeight(), 0);
                    anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            int val = (Integer) valueAnimator.getAnimatedValue();
                            ViewGroup.LayoutParams layoutParams = request_list.getLayoutParams();
                            layoutParams.height = val;
                            request_list.setLayoutParams(layoutParams);
                        }
                    });
                    anim.setDuration(1000);
                    anim.start();
                    arrow_flag = true;
                }
            }
        });

        get_my_id();
    }

    @Override
    public void onBackPressed() {

    }

    public void search_friend_list(String friend_name){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("my_id", my_id);
        params.put("name", friend_name);
        asyncHttpClient.post(SERVIERIP + "get_search_friend_list", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                search_result.setVisibility(View.VISIBLE);
                String result = new String(responseBody);
                try {
                    ArrayList<FriendItem> friend_data = new ArrayList<>();
                    JSONArray array = new JSONArray(result);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject data = array.getJSONObject(i);
                        FriendItem item = new FriendItem(data.getString("profile"), data.getString("name"), data.getString("email"));
                        friend_data.add(item);
                    }
                    if (array.length() <= 0) {
                        none_message.setVisibility(View.VISIBLE);
                    } else {
                        none_message.setVisibility(View.GONE);
                        ListviewAdapter adapter = new ListviewAdapter(FriendListActivity.this, R.layout.item_friend, friend_data, my_id);
                        search_list.setAdapter(adapter);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Log.i("HUHUHU","통신에러");
            }
        });
    }

    public void set_friend_list() {

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("my_id", my_id);
        asyncHttpClient.post(SERVIERIP + "get_friend_list", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    ArrayList<FriendItem> friend_data = new ArrayList<>();
                    JSONArray array = new JSONArray(result);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject data = array.getJSONObject(i);
                        FriendItem item = new FriendItem(data.getString("profile"), data.getString("name"), data.getString("email"));
                        friend_data.add(item);
                    }
                    if (array.length() <= 0) {
                        none_message.setVisibility(View.VISIBLE);
                    } else {
                        none_message.setVisibility(View.GONE);
                        ListviewAdapter adapter = new ListviewAdapter(FriendListActivity.this, R.layout.item_friend, friend_data, my_id);
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

    public void set_request_list() {
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("my_id", my_id);
        asyncHttpClient.post(SERVIERIP + "get_friend_request_list", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    ArrayList<FriendItem> friend_data = new ArrayList<>();
                    JSONArray array = new JSONArray(result);
                    request_number.setText(array.length() + "명");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject data = array.getJSONObject(i);
                        FriendItem item = new FriendItem(data.getString("profile"), data.getString("name"), data.getString("email"));
                        friend_data.add(item);
                    }
                    if (array.length() <= 0) {
                        TextView none_message = (TextView) findViewById(R.id.none_message);
                        none_message.setVisibility(View.VISIBLE);
                    } else {
                        RequestListviewAdapter adapter = new RequestListviewAdapter(FriendListActivity.this, R.layout.item_friend_request, friend_data, my_id, REQUEST);
                        request_list.setAdapter(adapter);
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


    public void get_my_id() {
        String u_email;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(FriendListActivity.this);
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
                    set_request_list();
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
