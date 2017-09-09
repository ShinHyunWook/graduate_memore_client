package com.mglab.memore;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by 신현욱 on 2017-05-03.
 */

public class CutDetailAdapter extends BaseAdapter implements NetDefine {
    private Context context;
    private LayoutInflater inflater;
    private int layout;
    String imageList;
    int size;

    DisplayImageOptions options;

    public CutDetailAdapter(Context context,int layout,String imageList,int size){
        this.context = context;
        this.layout = layout;
        this.imageList = imageList;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.size = size;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();

        ImageLoader.getInstance().init(config);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading) // 로딩중에 나타나는 이미지
                .showImageForEmptyUri(R.drawable.loading) // 값이 없을때
                .showImageOnFail(R.drawable.loading) // 에러 났을때 나타나는 이미지
                .cacheInMemory(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public int getCount(){
        JSONArray arr;
        try {
            arr = new JSONArray(imageList);
            return arr.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        JSONArray arr;
        try {
            arr = new JSONArray(imageList);
            return arr.get(position);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            convertView=inflater.inflate(layout,parent,false);
        }
        ImageView imageView=(ImageView)convertView.findViewById(R.id.imageView);

        imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, size));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        try {
            JSONArray arr = new JSONArray(imageList);
            String imgPath = String.valueOf(arr.get(position));
            String imgUrl = "http://" + IP + "/memore/uploads/"+imgPath;
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(imgUrl, imageView, options);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return imageView;
    }
}
