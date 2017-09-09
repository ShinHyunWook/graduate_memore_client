package com.mglab.memore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.L;

import java.io.File;

import static android.R.id.list;

/**
 * Created by 신현욱 on 2017-05-01.
 */

public class GalleryImages extends AppCompatActivity {
    GalleryGridAdapter myImageAdapter;
    Button btn_folder,btn_cut;
    LinearLayout gallery_line,cut_line;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.gallery_images);
        DisplayMetrics dm = new DisplayMetrics();
        GalleryImages.this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int size = dm.widthPixels / 4;

        GridView gridview = (GridView) findViewById(R.id.gridview);
        myImageAdapter = new GalleryGridAdapter(this,size,R.layout.gridview_item);
        gridview.setAdapter(myImageAdapter);

        String ExternalStorageDirectoryPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath();

        String targetPath = ExternalStorageDirectoryPath + "/Pictures/Memore/";

        File targetDirector = new File(targetPath);

        File[] files = targetDirector.listFiles();
        for (File file : files){
            myImageAdapter.add(file.getAbsolutePath());
        }

        gridview.setOnItemClickListener(myOnItemClickListener);

        btn_folder = (Button)findViewById(R.id.btn_folder);
        btn_cut = (Button)findViewById(R.id.btn_cut);

        btn_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GalleryImages.this,GalleryCut.class);
                startActivity(intent);
                finish();
            }
        });
        gallery_line = (LinearLayout)findViewById(R.id.gallery_line);
        cut_line = (LinearLayout)findViewById(R.id.cut_line);

        gallery_line.setVisibility(View.VISIBLE);
        cut_line.setVisibility(View.GONE);

    }

    AdapterView.OnItemClickListener myOnItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String prompt = (String) parent.getItemAtPosition(position);
            Intent intent = new Intent(GalleryImages.this,GalleryFullScreen.class);
            intent.putExtra("imagePath", prompt);
            intent.putExtra("flag", "image");
            startActivity(intent);
        }
    };
}
