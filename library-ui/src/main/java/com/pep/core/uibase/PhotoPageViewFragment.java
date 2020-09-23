package com.pep.core.uibase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.pep.core.libimage.PEPImageManager;
import com.pep.core.uibase.decode.DecodeFile;

import java.io.ByteArrayInputStream;
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
        View            root  = inPhotoPageViewFragmentflater.inflate(R.layout.view_image, container, false);
        final PhotoView image = root.findViewById(R.id.image);
        final View progress = root.findViewById(R.id.progress);
        String          url   = (String) getArguments().get("url");
        if (url.contains("http://") || url.contains("https://")) {
            PEPImageManager.getInstance().load(this, image, url);
        } else {
            final File file = new File(url);
            if (file.exists()) {
                progress.setVisibility(View.VISIBLE);
                new Thread() {
                    @Override
                    public void run() {
                        long startTime = System.currentTimeMillis();
                        Log.d("PEP", "" + startTime);
                        final String tempPath = getActivity().getCacheDir() + "/temp_"+startTime+".png";
                        DecodeFile.decodeFile(file, new File(tempPath));

                        //使用使用Glide进行加载图片进行加载图片
//                final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        long endTime = System.currentTimeMillis();
                        Log.d("PEP", "" + (endTime - startTime));
//                image.setImageBitmap(compressScale(bitmap));
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Glide.with(getActivity()).load(tempPath).listener(new RequestListener<Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                                    Target<Drawable> target, boolean isFirstResource) {
                                            progress.setVisibility(View.GONE);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(Drawable resource, Object model,
                                                                       Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                            progress.setVisibility(View.GONE);
                                            return false;
                                        }
                                    }).into(image);

                                }
                            });
                        }
                    }
                }.start();
//                byte[] data = DecodeFile.getFileBuffer(url);

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