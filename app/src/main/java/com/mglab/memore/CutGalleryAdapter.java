package com.mglab.memore;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
 * Created by 신현욱 on 2017-04-13.
 */

public class CutGalleryAdapter extends RecyclerView.Adapter<CutGalleryAdapter.ViewHolder> implements  NetDefine {
    private ArrayList<String> gallerys;
    private ArrayList<String> cut_id;
    private ArrayList<String> cut_caption;
    private Context mContext;
    DisplayImageOptions options;

    public CutGalleryAdapter(ArrayList<String> gallerys, ArrayList<String> cut_id, ArrayList<String> cut_caption, final Context context) {
        this.gallerys = gallerys;
        this.mContext = context;
        this.cut_id = cut_id;
        this.cut_caption = cut_caption;

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
    public CutGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CutGalleryAdapter.ViewHolder holder, int position) {
        String imgUrl = "http://" + IP + "/memore/uploads/"+gallerys.get(position);
        Log.i("asfa", imgUrl);
        holder.setImageView(imgUrl,position);
    }

    @Override
    public int getItemCount() {
        return gallerys.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView_id;
        private TextView textView_caption;

        public ViewHolder(View parent) {
            super(parent);

            imageView = (ImageView) parent.findViewById(R.id.galleryId);
            textView_id = (TextView)parent.findViewById(R.id.cut_id);
            textView_caption = (TextView)parent.findViewById(R.id.cut_caption);
        }

        public void setImageView(String imgUrl,int position) {
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(imgUrl, imageView, options);
            textView_id.setText(cut_id.get(position));
            textView_caption.setText(cut_caption.get(position));
        }
    }
}

