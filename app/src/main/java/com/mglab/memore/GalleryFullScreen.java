package com.mglab.memore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;

/**
 * Created by 신현욱 on 2017-05-01.
 */

public class GalleryFullScreen extends AppCompatActivity implements NetDefine{
    DisplayImageOptions options;
    ImageView full_image_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_full_screen);

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("imagePath");
        String flag = intent.getStringExtra("flag");

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(GalleryFullScreen.this)
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

        full_image_view = (ImageView)findViewById(R.id.full_image_view);

        if(flag.equals("cut")){
            String imgUrl = "http://" + IP + "/memore/uploads/"+imagePath;
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(imgUrl, full_image_view, options);
        }else{
            String decodedImgUri = Uri.fromFile(new File(imagePath)).toString();
            ImageLoader.getInstance().displayImage(decodedImgUri, full_image_view,options);
        }
    }
}