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

import java.util.ArrayList;

/**
 * Created by 신현욱 on 2017-05-02.
 */

public class GalleryCutGridAdapter extends BaseAdapter implements NetDefine{
    private Context context;
    private int layout;
    private LayoutInflater inflater;
    private int size;
    String flag;

    ArrayList<GalleryCutModel> galleryCutModelList;
    DisplayImageOptions options;

    public GalleryCutGridAdapter(Context context, int layout,ArrayList<GalleryCutModel>galleryCutModelList,int size,String flag){
        this.context = context;
        this.layout = layout;
        this.galleryCutModelList = galleryCutModelList;
        this.inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.size = size;
        this.flag = flag;

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
    public int getCount() {
        return galleryCutModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return galleryCutModelList.get(position);
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

        String imgPath = galleryCutModelList.get(position).getFirst_image();
        String imgUrl = "http://" + IP + "/memore/uploads/"+imgPath;
        Log.i("test",imgPath);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imgUrl, imageView, options);

        Log.i("lalalal",galleryCutModelList.get(position).getFirst_image());

        return imageView;
    }
}
