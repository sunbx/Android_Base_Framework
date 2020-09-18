package com.pep.core.uibase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.pep.core.libimage.PEPImageManager;
import com.pep.core.uibase.decode.DecodeFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


/**
 * The type Photo page view fragment.
 *
 * @author sunbaixin
 */
public class PhotoPageViewFragment extends Fragment {
    /**
     * Sets on click listener. 图片点击回调监听
     *
     * @param onClickListener the on click listener
     */
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    private OnClickListener onClickListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inPhotoPageViewFragmentflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View      root  = inPhotoPageViewFragmentflater.inflate(R.layout.view_image, container, false);
        PhotoView image = root.findViewById(R.id.image);
        String    url   = (String) getArguments().get("url");
        if (url.contains("http://") || url.contains("https://")) {
            PEPImageManager.getInstance().load(this, image, url);
        } else {
            File file = new File(url);
            if (file.exists()) {

                byte[] data = DecodeFile.getFileBuffer(url);
                //使用使用Glide进行加载图片进行加载图片
//                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//                image.setImageBitmap(bitmap);

                Glide.with(getActivity()).load(data).into(image);
            }

        }

        image.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                if (onClickListener != null) {
                    onClickListener.onClick(view);
                }
            }
        });
        return root;
    }

    /**
     * 5.采样率压缩（设置图片的采样率，降低图片像素）
     *
     * @param filePath
     * @param file
     */
    public static void samplingRateCompress(String filePath, File file) {
        // 数值越高，图片像素越低
        int                   inSampleSize = 8;
        BitmapFactory.Options options      = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
//          options.inJustDecodeBounds = true;//为true的时候不会真正加载图片，而是得到图片的宽高信息。
        //采样率
        options.inSampleSize = inSampleSize;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 把压缩后的数据存放到baos中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        try {
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * The interface On click listener.图片点击回调
     */
    interface OnClickListener {
        /**
         * On click.回调事件
         *
         * @param view the view
         */
        void onClick(View view);
    }
}