package com.mglab.memore;

import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
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

import java.util.ArrayList;

/**
 * Created by 신현욱 on 2017-04-20.
 */

public class tagListAdapter extends RecyclerView.Adapter<tagListAdapter.ViewHolder> implements  NetDefine {
    private ArrayList<String> tag_name;
    private ArrayList<String> tag_profile;
    private Context mContext;
    DisplayImageOptions options;

    public tagListAdapter(ArrayList<String> tag_name, ArrayList<String> tag_profile,Context context) {
        this.tag_name = tag_name;
        this.mContext = context;
        this.tag_profile = tag_profile;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
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
    public tagListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_with_friend, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(tagListAdapter.ViewHolder holder, int position) {
        String imgUrl = "http://" + IP + "/memore/uploads/"+tag_profile.get(position);
        holder.setImageView(imgUrl,position);
    }

    @Override
    public int getItemCount() {
        return tag_name.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView tag_profile_view;
        private TextView tag_name_view;

        public ViewHolder(View parent) {
            super(parent);

            tag_profile_view = (ImageView) parent.findViewById(R.id.tag_profile);
            tag_name_view = (TextView)parent.findViewById(R.id.tag_name);
        }

        public void setImageView(String imgUrl,int position) {
            ImageLoader imageLoader = ImageLoader.getInstance();

            imageLoader.displayImage(imgUrl, tag_profile_view, options);
            tag_name_view.setText(tag_name.get(position));
        }
    }
}
