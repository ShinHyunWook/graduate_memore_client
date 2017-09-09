package com.mglab.memore;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-05-06.
 */

public class RequestListviewAdapter extends BaseAdapter implements NetDefine {
    private LayoutInflater inflater;
    private ArrayList<FriendItem> data;
    private int layout;
    private AsyncHttpClient asyncHttpClient;
    String my_id;
    int request;
    static int ACCEPT = 3, REJECT = 4;
    DisplayImageOptions options;
    final HashMap<Integer,String> result_flag = new HashMap<Integer, String>();
    Context context;


    public RequestListviewAdapter(Context context, int layout, ArrayList<FriendItem> data, String my_id, int request){
        this.context = context;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.layout=layout;
        this.my_id = my_id;
        this.request = request;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
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
    }
    @Override
    public int getCount(){return data.size();}
    @Override
    public String getItem(int position){return data.get(position).getName();}
    @Override
    public long getItemId(int position){return position;}

    public class ViewHolder{
        ImageView profile;
        TextView email;
        TextView name;
        ImageButton btn_accept_reject;
        TextView accept_message;
        TextView reject_message;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        final ViewHolder viewHolder;

        if(convertView==null){
            convertView=inflater.inflate(layout,parent,false);

            viewHolder = new ViewHolder();
            viewHolder.profile = (ImageView)convertView.findViewById(R.id.item_friend_profile);
            viewHolder.name = (TextView)convertView.findViewById(R.id.item_friend_name);
            viewHolder.email = (TextView)convertView.findViewById(R.id.item_friend_email);
            viewHolder.btn_accept_reject = (ImageButton)convertView.findViewById(R.id.btn_accept_reject);
            viewHolder.accept_message = (TextView)convertView.findViewById(R.id.accept_message);
            viewHolder.reject_message = (TextView)convertView.findViewById(R.id.reject_message);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FriendItem listviewitem=data.get(position);

        viewHolder.btn_accept_reject.setTag(position);

        if(result_flag.containsKey(position)&&result_flag.get(position).equals("accept")){
            viewHolder.accept_message.setVisibility(View.VISIBLE);
            viewHolder.btn_accept_reject.setVisibility(View.GONE);
        }else if(result_flag.containsKey(position)&&result_flag.get(position).equals("reject")){
            viewHolder.reject_message.setVisibility(View.VISIBLE);
            viewHolder.btn_accept_reject.setVisibility(View.GONE);
        }else {
            viewHolder.accept_message.setVisibility(View.GONE);
            viewHolder.reject_message.setVisibility(View.GONE);
            viewHolder.btn_accept_reject.setVisibility(View.VISIBLE);
        }

        String imgUrl = "http://" + IP + "/memore/uploads/" + listviewitem.getProfile();

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(imgUrl, viewHolder.profile, options);

        viewHolder.name.setText(listviewitem.getName());
        viewHolder.email.setText(listviewitem.getEmail());

        final String f_email = listviewitem.getEmail();

        viewHolder.btn_accept_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsAlert(viewHolder,f_email);
            }
        });


        return convertView;
    }

    public void set_friend_id(final String friend_email, final int request){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", friend_email);
        asyncHttpClient.post(SERVIERIP + "search_person", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                try {
                    JSONObject obj = new JSONObject(result);
                    String friend_id = obj.getString("id");
                    if(request == ACCEPT){
                        accept_friend(my_id,friend_id);
                    }else if(request == REJECT){
                        reject_friend(my_id,friend_id);
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

    public void accept_friend(String my_id,String friend_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("friend_one", friend_id);
        params.put("friend_two", my_id);
        asyncHttpClient.post(SERVIERIP + "accept_friend", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
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

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.w("Join Error", error);
            }
        });
    }

    public void showSettingsAlert(final ViewHolder viewHolder, final String f_email) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        alertDialog.setTitle("친구 요청");
        alertDialog.setMessage("친구 요청을 수락 하시겠습니까?");
        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("수락", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int pos = (Integer)viewHolder.btn_accept_reject.getTag();
                result_flag.put(pos,"accept");
                viewHolder.accept_message.setVisibility(View.VISIBLE);
                viewHolder.btn_accept_reject.setVisibility(View.GONE);
                set_friend_id(f_email,ACCEPT);
            }
        });

        alertDialog.setNegativeButton("거절", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int pos = (Integer)viewHolder.btn_accept_reject.getTag();
                result_flag.put(pos,"reject");
                viewHolder.reject_message.setVisibility(View.VISIBLE);
                viewHolder.btn_accept_reject.setVisibility(View.GONE);
                set_friend_id(f_email,REJECT);
            }
        });
        // Cancle 하면 종료 합니다.
        alertDialog.setNeutralButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }
}
