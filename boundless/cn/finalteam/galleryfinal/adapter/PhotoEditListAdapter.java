package cn.finalteam.galleryfinal.adapter;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.PhotoEditActivity;
import cn.finalteam.galleryfinal.R;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.GFImageView;
import cn.finalteam.toolsfinal.adapter.ViewHolderAdapter;
import java.util.List;

public class PhotoEditListAdapter extends ViewHolderAdapter<ViewHolder, PhotoInfo> {
    private PhotoEditActivity mActivity;
    private int mRowWidth;

    private class OnDeletePhotoClickListener implements OnClickListener {
        private int position;

        public OnDeletePhotoClickListener(int position) {
            this.position = position;
        }

        public void onClick(View view) {
            PhotoInfo photoInfo = null;
            try {
                photoInfo = (PhotoInfo) PhotoEditListAdapter.this.getDatas().remove(this.position);
            } catch (Exception e) {
                e.printStackTrace();
            }
            PhotoEditListAdapter.this.notifyDataSetChanged();
            PhotoEditListAdapter.this.mActivity.deleteIndex(this.position, photoInfo);
        }
    }

    public class ViewHolder extends cn.finalteam.toolsfinal.adapter.ViewHolderAdapter.ViewHolder {
        ImageView mIvDelete;
        GFImageView mIvPhoto;

        public ViewHolder(View view) {
            super(view);
            this.mIvPhoto = (GFImageView) view.findViewById(R.id.iv_photo);
            this.mIvDelete = (ImageView) view.findViewById(R.id.iv_delete);
        }
    }

    public PhotoEditListAdapter(PhotoEditActivity activity, List<PhotoInfo> list, int screenWidth) {
        super(activity, list);
        this.mActivity = activity;
        this.mRowWidth = screenWidth / 5;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new ViewHolder(inflate(R.layout.gf_adapter_edit_list, parent));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        String path = "";
        PhotoInfo photoInfo = (PhotoInfo) getDatas().get(position);
        if (photoInfo != null) {
            path = photoInfo.getPhotoPath();
        }
        holder.mIvPhoto.setImageResource(R.drawable.ic_gf_default_photo);
        holder.mIvDelete.setImageResource(GalleryFinal.getGalleryTheme().getIconDelete());
        GalleryFinal.getCoreConfig().getImageLoader().displayImage(this.mActivity, path, holder.mIvPhoto, this.mActivity.getResources().getDrawable(R.drawable.ic_gf_default_photo), 100, 100);
        if (GalleryFinal.getFunctionConfig().isMutiSelect()) {
            holder.mIvDelete.setVisibility(0);
        } else {
            holder.mIvDelete.setVisibility(8);
        }
        holder.mIvDelete.setOnClickListener(new OnDeletePhotoClickListener(position));
    }
}
