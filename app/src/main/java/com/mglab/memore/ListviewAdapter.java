package com.mglab.memore;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import java.util.List;

import javax.crypto.spec.OAEPParameterSpec;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-04-06.
 */

public class ListviewAdapter extends BaseAdapter implements NetDefine {
    private LayoutInflater inflater;
    private ArrayList<FriendItem> data;
    private int layout;
    private AsyncHttpClient asyncHttpClient;
    String my_id;
    DisplayImageOptions options;
    final HashMap<Integer,String> delete_flag = new HashMap<Integer, String>();
    Context context;

    public ListviewAdapter(Context context, int layout, ArrayList<FriendItem> data, String my_id){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data=data;
        this.layout=layout;
        this.my_id = my_id;
        this.context = context;

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
        ImageButton btn_delete_friend;
        TextView delete_message;
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
            viewHolder.btn_delete_friend = (ImageButton)convertView.findViewById(R.id.btn_delete_friend);
            viewHolder.delete_message = (TextView)convertView.findViewById(R.id.delete_message);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FriendItem listviewitem=data.get(position);

        viewHolder.btn_delete_friend.setTag(position);

        if(delete_flag.containsKey(position)&&delete_flag.get(position).equals("true")){
            viewHolder.delete_message.setVisibility(View.VISIBLE);
            viewHolder.btn_delete_friend.setVisibility(View.GONE);
        }else{
            viewHolder.delete_message.setVisibility(View.GONE);
            viewHolder.btn_delete_friend.setVisibility(View.VISIBLE);
        }

        String imgUrl = "http://" + IP + "/memore/uploads/" + listviewitem.getProfile();

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(imgUrl, viewHolder.profile, options);


        viewHolder.name.setText(listviewitem.getName());
        viewHolder.email.setText(listviewitem.getEmail());

        final String f_email = listviewitem.getEmail();

        // 친구 삭제 기능
        viewHolder.btn_delete_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettingsAlert(viewHolder,f_email);
            }
        });

        return convertView;
    }

    public void set_friend_id(final String friend_email){
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

                    list_delete_friend(my_id,friend_id);

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

    public void list_delete_friend(String my_id, String friend_id){
        asyncHttpClient = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("friend_one", my_id);
        params.put("friend_two", friend_id);
        asyncHttpClient.post(SERVIERIP + "delete_friend", params, new AsyncHttpResponseHandler() {
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

        alertDialog.setTitle("친구 삭제");
        alertDialog.setMessage("친구를 삭제하시겠습니까?");
        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int pos = (Integer) viewHolder.btn_delete_friend.getTag();
                delete_flag.put(pos,"true");
                viewHolder.btn_delete_friend.setVisibility(View.GONE);
                viewHolder.delete_message.setVisibility(View.VISIBLE);
                set_friend_id(f_email);
            }
        });

        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }
}
