package com.mglab.memore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;

import static com.mglab.memore.R.id.inputId;

/**
 * Created by 신현욱 on 2017-03-30.
 */

public class FriendSearchActivity extends AppCompatActivity implements NetDefine{
    Button friend_request, friend_cancel,friend_yes,friend_no,friend_delete,btn_list;
    ImageButton search_friend,btn_back;
    EditText insert_email;
    LinearLayout yes_no_btn,search_line,list_line;
    TextView friend_name,friend_email;
    TextView search_bar;
    static int SEARCH = 1, REQUEST = 2, CANCEL = 3, ACCEPT=4, REJECT=5, DELETE=6;
    private String my_id;
    private AsyncHttpClient asyncHttpClient;
    DisplayImageOptions options;
    ImageView imView;
    RelativeLayout search_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);
        get_my_id();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.unknown) // 로딩중에 나타나는 이미지
                .showImageForEmptyUri(R.drawable.unknown) // 값이 없을때
                .showImageOnFail(R.drawable.unknown) // 에러 났을때 나타나는 이미지
                .cacheInMemory(true)
                .considerExifParams(true)
                .build();

        friend_name = (TextView) findViewById(R.id.friend_name);
        friend_email = (TextView) findViewById(R.id.friend_email);
        search_result = (RelativeLayout) findViewById(R.id.search_result);

        search_friend = (ImageButton) findViewById(R.id.search_friend);
        friend_request = (Button) findViewById(R.id.friend_request);
        friend_cancel = (Button) findViewById(R.id.friend_cancel);
        friend_yes = (Button) findViewById(R.id.btn_yes);
        friend_no = (Button) findViewById(R.id.btn_no);
        friend_delete = (Button) findViewById(R.id.friend_delete);

        btn_back = (ImageButton) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_list = (Button) findViewById(R.id.btn_list);
        btn_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FriendSearchActivity.this,FriendListActivity.class);
                startActivity(intent);
                finish();
            }
        });


        yes_no_btn = (LinearLayout) findViewById(R.id.yes_no_btn);

        insert_email = (EditText) findViewById(R.id.insert_email);
        search_bar = (TextView)findViewById(R.id.search_bar);

        search_line = (LinearLayout) findViewById(R.id.search_line);
        list_line = (LinearLayout) findViewById(R.id.list_line);

        search_line.setVisibility(View.VISIBLE);
        list_line.setVisibility(View.GONE);

        friend_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView friend_email = (TextView) findViewById(R.id.friend_email);
                String f_email = String.valueOf(friend_email.getText());
                set_friend_id(f_email,CANCEL);
            }
        });
        friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView friend_email = (TextView) findViewById(R.id.friend_email);
                String f_email = String.valueOf(friend_email.getText());
                set_friend_id(f_email,REQUEST);
            }
        });

        search_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(FriendListActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(insert_email.getWindowToken(), 0);
                String insert_data = String.valueOf(insert_email.getText());
                set_friend_id(insert_data, SEARCH);
            }
        });

        friend_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView friend_email = (TextView) findViewById(R.id.friend_email);
                String f_email = String.valueOf(friend_email.getText());
                set_friend_id(f_email,REJECT);
            }
        });
        friend_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView friend_email = (TextView) findViewById(R.id.friend_email);
                String f_email = String.valueOf(friend_email.getText());
                set_friend_id(f_email,ACCEPT);
            }
        });
        friend_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView friend_email = (TextView) findViewById(R.id.friend_email);
                String f_email = String.valueOf(friend_email.getText());
                set_friend_id(f_email,DELETE);
            }
        });
    }

    @Override public void onBackPressed() {

    }

    public void get_my_id() {
        String u_email;

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(FriendSearchActivity.this);
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

    public void set_friend_id(String friend_email, final int request) {
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", friend_email);
        asyncHttpClient.post(SERVIERIP + "search_person", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                if(result.equals("empty")){
                    Toast.makeText(FriendSearchActivity.this,"존재하지 않는 아이디입니다.",Toast.LENGTH_SHORT).show();
                    search_result.setVisibility(View.GONE);
                }else{
                    try {
                        JSONObject obj = new JSONObject(result);
                        String friend_id = obj.getString("id");
                        if (request == SEARCH) {
                            get_friend_info(my_id, friend_id);
                        } else if (request == REQUEST) {
                            add_request_friend(my_id, friend_id);
                        }else if(request == CANCEL){
                            cancel_request_friend(my_id,friend_id);
                        }else if(request == ACCEPT){
                            accept_friend(my_id,friend_id);
                        }else if(request == REJECT){
                            reject_friend(my_id,friend_id);
                        }else if(request == DELETE){
                            delete_friend(my_id,friend_id);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(FriendSearchActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void accept_friend(String my_id,String friend_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("friend_one", friend_id);
        params.put("friend_two", my_id);
        asyncHttpClient.post(SERVIERIP + "accept_friend", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                yes_no_btn.setVisibility(View.GONE);
                friend_delete.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(FriendSearchActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void reject_friend(String my_id, String friend_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("friend_one", friend_id);
        params.put("friend_two", my_id);
        asyncHttpClient.post(SERVIERIP + "reject_friend", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                friend_request.setVisibility(View.VISIBLE);
                friend_cancel.setVisibility(View.GONE);
                yes_no_btn.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(FriendSearchActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void delete_friend(String my_id, String friend_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("friend_one", my_id);
        params.put("friend_two", friend_id);
        asyncHttpClient.post(SERVIERIP + "delete_friend", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                friend_request.setVisibility(View.VISIBLE);
                friend_delete.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(FriendSearchActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void get_friend_info(final String m_id, String friend_id) {

        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("my_id", m_id);
        params.put("friend_id", friend_id);
        asyncHttpClient.post(SERVIERIP + "search_friend", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject obj = new JSONObject(result);

                    search_result.setVisibility(View.VISIBLE);
                    friend_name.setText(obj.getString("name"));
                    friend_email.setText(insert_email.getText());

                    String imgUrl = "http://" + IP + "/memore/uploads/" + obj.getString("profile");

                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imView = (ImageView) findViewById(R.id.friend_profile);

                    imageLoader.displayImage(imgUrl, imView, options);


                    //friend => 0  친구 신청도 안되어 있는 상태 , friend => 1 친구 신청 대기중이거나 친구인 상태
                    if (obj.getString("friend").equals("0")) {
                        friend_request.setVisibility(View.VISIBLE);
                        friend_cancel.setVisibility(View.GONE);
                        yes_no_btn.setVisibility(View.GONE);
                        friend_delete.setVisibility(View.GONE);
                    } else if(obj.getString("friend").equals("1")&&obj.getString("status").equals("0")&&obj.getString("role").equals("sender")){
                        friend_request.setVisibility(View.GONE);
                        friend_cancel.setVisibility(View.GONE);
                        yes_no_btn.setVisibility(View.VISIBLE);
                        friend_delete.setVisibility(View.GONE);
                    } else if(obj.getString("friend").equals("1")&&obj.getString("status").equals("0")&&obj.getString("role").equals("receiver")) {
                        friend_request.setVisibility(View.GONE);
                        friend_cancel.setVisibility(View.VISIBLE);
                        yes_no_btn.setVisibility(View.GONE);
                        friend_delete.setVisibility(View.GONE);
                    }else if(obj.getString("friend").equals("1")&&obj.getString("status").equals("1")){
                        friend_request.setVisibility(View.GONE);
                        friend_cancel.setVisibility(View.GONE);
                        yes_no_btn.setVisibility(View.GONE);
                        friend_delete.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(FriendSearchActivity.this, "통신 에러 밑밑밑", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void add_request_friend(String my_id, String friend_id) {
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("friend_one", my_id);
        params.put("friend_two", friend_id);
        asyncHttpClient.post(SERVIERIP + "friend_request", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                friend_request.setVisibility(View.GONE);
                friend_cancel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(FriendSearchActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void cancel_request_friend(String my_id,String friend_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("friend_one", my_id);
        params.put("friend_two", friend_id);
        asyncHttpClient.post(SERVIERIP + "cancel_friend_request", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                friend_request.setVisibility(View.VISIBLE);
                friend_cancel.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
                Toast.makeText(FriendSearchActivity.this, "통신 에러", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
