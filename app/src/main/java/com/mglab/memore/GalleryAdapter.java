package com.mglab.memore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.target.SquaringDrawable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 신현욱 on 2017-03-11.
 */

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private ArrayList<Drawable> gallerys;
    private Context mContext;

    public GalleryAdapter(ArrayList<Drawable> gallerys, Context context) {
        this.gallerys = gallerys;
        this. mContext = context;
    }

    @Override
    public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(GalleryAdapter.ViewHolder holder, int position) {
        holder.setImageView(gallerys.get(position));
    }

    @Override
    public int getItemCount() {
        return gallerys.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(View parent) {
            super(parent);

            imageView = (ImageView) parent.findViewById(R.id.galleryId);
        }

        public void setImageView(Drawable drawable) {
            imageView.setImageDrawable(drawable);

//            Bitmap bitmap = null;
//            if (drawable instanceof GlideBitmapDrawable) {
//                bitmap = ((GlideBitmapDrawable) drawable).getBitmap();
//            } else if (drawable instanceof TransitionDrawable) {
//                TransitionDrawable transitionDrawable = (TransitionDrawable) drawable;
//                int length = transitionDrawable.getNumberOfLayers();
//                for (int i = 0; i < length; ++i) {
//                    Drawable child = transitionDrawable.getDrawable(i);
//                    if (child instanceof GlideBitmapDrawable) {
//                        bitmap = ((GlideBitmapDrawable) child).getBitmap();
//                        break;
//                    } else if (child instanceof SquaringDrawable
//                            && child.getCurrent() instanceof GlideBitmapDrawable) {
//                        bitmap = ((GlideBitmapDrawable) child.getCurrent()).getBitmap();
//                        break;
//                    }
//                }
//            } else if (drawable instanceof SquaringDrawable) {
//                bitmap = ((GlideBitmapDrawable) drawable.getCurrent()).getBitmap();
//            }
//
//            Glide.with(mContext).load(bitmap).into(imageView);
        }
    }
}
