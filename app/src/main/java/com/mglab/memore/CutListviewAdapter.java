package com.mglab.memore;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.mglab.memore.R.id.galleryRecycler;

/**
 * Created by 신현욱 on 2017-04-27.
 */

public class CutListviewAdapter extends BaseAdapter implements NetDefine {
    private LayoutInflater inflater;
    ArrayList<FeedCutModel> data;
    private int layout;
    DisplayImageOptions options;
    AsyncHttpClient asyncHttpClient;
    TextView cut_caption;
    private FeedContentCutAdapter feedContentCutAdapter;
    private RecyclerView cutRecycler;
    ImageButton btn_tag_list;

    public CutListviewAdapter(Context context, int layout, ArrayList<FeedCutModel> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
        this.layout = layout;

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
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(layout, parent, false);
        }

        cut_caption = (TextView) convertView.findViewById(R.id.cut_caption);

        cut_caption.setText(data.get(position).getCaption());

        btn_tag_list = (ImageButton)convertView.findViewById(R.id.btn_tag_list);

        final View finalConvertView = convertView;
        if(data.get(position).getTag_list().isEmpty()){
            btn_tag_list.setVisibility(View.GONE);
        }


        btn_tag_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(finalConvertView.getContext(),FeedCutTagList.class);
                intent.putExtra("tag_list",data.get(position).getTag_list());
                finalConvertView.getContext().startActivity(intent);
            }
        });

        cutRecycler = (RecyclerView) convertView.findViewById(R.id.feedCutRecycler);
        feedContentCutAdapter = new FeedContentCutAdapter(data.get(position).getImage_name(),convertView.getContext());
        cutRecycler.setLayoutManager(new LinearLayoutManager(convertView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        cutRecycler.setAdapter(feedContentCutAdapter);

        return convertView;
    }
}
