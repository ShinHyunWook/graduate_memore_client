package com.mglab.memore;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 신현욱 on 2017-04-20.
 */

public class FeedCutAdapter extends RecyclerView.Adapter<FeedCutAdapter.ViewHolder> implements  NetDefine {

    private List<FeedModel> feedModelList;
    private Context mContext;
    DisplayImageOptions options;

    public static OnCutItemClickListener mOnCutItemClickListener;

    public interface OnCutItemClickListener {
        void onItemClick(int position);
    }

    public void setOnCutItemClickListener(OnCutItemClickListener onCutItemClickListener) {
        this.mOnCutItemClickListener = onCutItemClickListener;
    }

    public FeedCutAdapter(List<FeedModel> feedModelList, Context context) {

        this.mContext = context;
        this.feedModelList = feedModelList;

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
    public FeedCutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cut_select_gallery, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FeedCutAdapter.ViewHolder holder, int position) {
        holder.setImageView(feedModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return feedModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imageView;
        private TextView textView_caption;
        private RelativeLayout itemSelected;
        private LinearLayout cut_item;

        public ViewHolder(View parent) {
            super(parent);
            cut_item = (LinearLayout) parent.findViewById(R.id.cut_item);
            itemSelected = (RelativeLayout) parent.findViewById(R.id.itemSelected);
            imageView = (ImageView) parent.findViewById(R.id.galleryId);
            textView_caption = (TextView)parent.findViewById(R.id.cut_caption);
            cut_item.setOnClickListener(this);
        }

        public void setImageView(FeedModel feedModel) {
            String imgUrl = "http://" + IP + "/memore/uploads/"+feedModel.getCutName();
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(imgUrl, imageView, options);
            textView_caption.setText(feedModel.getCutDescription());
            if (feedModel.isSelected()) {
                itemSelected.setVisibility(View.VISIBLE);
            } else {
                itemSelected.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.cut_item) {
                if (mOnCutItemClickListener != null) {
                    if (feedModelList.get(getPosition()).isSelected()) {
                        itemSelected.setVisibility(View.GONE);
                    } else {
                        itemSelected.setVisibility(View.VISIBLE);
                    }
                    mOnCutItemClickListener.onItemClick(getPosition());
                }
            }
        }
    }
}