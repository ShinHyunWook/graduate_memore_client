package com.mglab.memore;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by 신현욱 on 2017-04-27.
 */

public class ThumTagListviewAdapter extends BaseAdapter implements NetDefine{
    private LayoutInflater inflater;
    private int layout;
    ArrayList<ThumbTagModel> data;
    DisplayImageOptions options;
    ImageView item_friend_profile;
    TextView item_friend_name,item_friend_email;


    public ThumTagListviewAdapter(Context context, int layout, ArrayList<ThumbTagModel>data){
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.layout=layout;
        this.data = data;

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
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        item_friend_profile = (ImageView)convertView.findViewById(R.id.item_friend_profile);
        item_friend_name = (TextView)convertView.findViewById(R.id.item_friend_name);
        item_friend_email = (TextView)convertView.findViewById(R.id.item_friend_email);

        item_friend_email.setText(data.get(position).getEmail());
        item_friend_name.setText(data.get(position).getName());

        String imgUrl = "http://" + IP + "/memore/uploads/" + data.get(position).getProfile();

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(imgUrl, item_friend_profile, options);

        return convertView;
    }
}
