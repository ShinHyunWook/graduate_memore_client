package com.mglab.memore;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.List;

/**
 * Created by 신현욱 on 2017-04-02.
 */

public class NewGalleryAdapter extends RecyclerView.Adapter<NewGalleryAdapter.ViewHolder> {

    private List<CutImageModel> gallerys;
    private Context mContext;

    public NewGalleryAdapter(List<CutImageModel> gallerys, Context context) {
        this.gallerys = gallerys;
        this. mContext = context;
    }

    @Override
    public NewGalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_new_gallery, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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

        public void setImageView(CutImageModel cutImageModel) {

            Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(), cutImageModel.getImageId(), MediaStore.Images.Thumbnails.MINI_KIND, null);
            int degree = GetExifOrientation(cutImageModel.getImagePath());
            Bitmap bit = GetRotatedBitmap(bitmap,degree);
            imageView.setImageBitmap(bit);
            Log.i("sfnasfnsdf", cutImageModel.getImageId() + " : " + cutImageModel.getImagePath());
        }
    }

    public synchronized static int GetExifOrientation(String filepath)
    {
        int degree = 0;
        ExifInterface exif = null;

        try
        {
            exif = new ExifInterface(filepath);
        }
        catch (IOException e)
        {
            Log.e("LUFY", "cannot read exif");
            e.printStackTrace();
        }

        if (exif != null)
        {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1)
            {
                // We only recognize a subset of orientation tag values.
                switch(orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }

        return degree;
    }

    public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees)
    {
        if ( degrees != 0 && bitmap != null )
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
            try
            {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2)
                {
                    bitmap.recycle();
                    bitmap = b2;
                }
            }
            catch (OutOfMemoryError ex)
            {
                // We have no memory to rotate. Return the original bitmap.
            }
        }

        return bitmap;
    }
}