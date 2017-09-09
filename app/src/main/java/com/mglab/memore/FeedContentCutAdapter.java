package com.mglab.memore;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by 신현욱 on 2017-04-27.
 */

public class FeedContentCutAdapter extends RecyclerView.Adapter<FeedContentCutAdapter.ViewHolder> implements  NetDefine {
    private String images;
    private Context mContext;
    DisplayImageOptions options;
    int size;

    public FeedContentCutAdapter(String images, Context context) {
        this.images = images;
        this.mContext = context;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
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
    public FeedContentCutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);

        FeedContentCutAdapter.ViewHolder viewHolder = new FeedContentCutAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FeedContentCutAdapter.ViewHolder holder, int position) {
        try {
            JSONArray arr = new JSONArray(images);
            String imgUrl = "http://" + IP + "/memore/uploads/"+arr.get(position);
            holder.setImageView(imgUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        try {
            JSONArray arr = new JSONArray(images);
            size = arr.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(View parent) {
            super(parent);

            imageView = (ImageView) parent.findViewById(R.id.galleryId);
        }

        public void setImageView(String imgUrl) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(imgUrl, imageView, options);
        }
    }
}
