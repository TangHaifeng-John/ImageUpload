package com.haifeng.example.iamgeupload.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.haifeng.example.iamgeupload.R;
import com.haifeng.example.iamgeupload.common.BaseAdapter;
import com.haifeng.example.iamgeupload.db.UploadEntity;
import com.haifeng.example.iamgeupload.tool.LogTool;

/**
 * 图片适配器
 */
public class PictureListAdapter extends BaseAdapter<UploadEntity, PictureListAdapter.ViewHolder> {


    public PictureListAdapter(Context context) {
        super(context);
    }


    @Override
    protected int getItemViewLayout() {
        return R.layout.item_picture;
    }

    @Override
    public void convert(int position, ViewHolder holder, UploadEntity uploadEntity) {
        UploadEntity entity = getItem(position);
        if (TextUtils.isEmpty(entity.getUrl())) {
            holder.loadPic(entity.getOldPath());

            LogTool.i("使用本地Path");
        } else {
            LogTool.i("使用服务器地址:" + entity.getUrl());

            holder.loadPic(entity.getUrl());
        }

    }


    @Override
    protected Class getHolderClass() {
        return ViewHolder.class;
    }


    static class ViewHolder extends BaseAdapter.ViewHolder {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }

        public void loadPic(String url) {
            Glide.with(imageView.getContext()).load(url).into(imageView);
        }
    }
}
