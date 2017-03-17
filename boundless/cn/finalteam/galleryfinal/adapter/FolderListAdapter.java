package cn.finalteam.galleryfinal.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.R;
import cn.finalteam.galleryfinal.model.PhotoFolderInfo;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.GFImageView;
import cn.finalteam.toolsfinal.adapter.ViewHolderAdapter;
import cn.finalteam.toolsfinal.adapter.ViewHolderAdapter.ViewHolder;
import java.util.List;

public class FolderListAdapter extends ViewHolderAdapter<FolderViewHolder, PhotoFolderInfo> {
    private Activity mActivity;
    private FunctionConfig mFunctionConfig;
    private PhotoFolderInfo mSelectFolder;

    static class FolderViewHolder extends ViewHolder {
        GFImageView mIvCover;
        ImageView mIvFolderCheck;
        TextView mTvFolderName;
        TextView mTvPhotoCount;
        View mView;

        public FolderViewHolder(View view) {
            super(view);
            this.mView = view;
            this.mIvCover = (GFImageView) view.findViewById(R.id.iv_cover);
            this.mTvFolderName = (TextView) view.findViewById(R.id.tv_folder_name);
            this.mTvPhotoCount = (TextView) view.findViewById(R.id.tv_photo_count);
            this.mIvFolderCheck = (ImageView) view.findViewById(R.id.iv_folder_check);
        }
    }

    public FolderListAdapter(Activity activity, List<PhotoFolderInfo> list, FunctionConfig FunctionConfig) {
        super(activity, list);
        this.mFunctionConfig = FunctionConfig;
        this.mActivity = activity;
    }

    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        return new FolderViewHolder(inflate(R.layout.gf_adapter_folder_list_item, parent));
    }

    public void onBindViewHolder(FolderViewHolder holder, int position) {
        PhotoFolderInfo photoFolderInfo = (PhotoFolderInfo) getDatas().get(position);
        String path = "";
        PhotoInfo photoInfo = photoFolderInfo.getCoverPhoto();
        if (photoInfo != null) {
            path = photoInfo.getPhotoPath();
        }
        holder.mIvCover.setImageResource(R.drawable.ic_gf_default_photo);
        GalleryFinal.getCoreConfig().getImageLoader().displayImage(this.mActivity, path, holder.mIvCover, this.mActivity.getResources().getDrawable(R.drawable.ic_gf_default_photo), 200, 200);
        holder.mTvFolderName.setText(photoFolderInfo.getFolderName());
        int size = 0;
        if (photoFolderInfo.getPhotoList() != null) {
            size = photoFolderInfo.getPhotoList().size();
        }
        holder.mTvPhotoCount.setText(this.mActivity.getString(R.string.folder_photo_size, new Object[]{Integer.valueOf(size)}));
        if (GalleryFinal.getCoreConfig().getAnimation() > 0) {
            holder.mView.startAnimation(AnimationUtils.loadAnimation(this.mActivity, GalleryFinal.getCoreConfig().getAnimation()));
        }
        holder.mIvFolderCheck.setImageResource(GalleryFinal.getGalleryTheme().getIconCheck());
        if (this.mSelectFolder == photoFolderInfo || (this.mSelectFolder == null && position == 0)) {
            holder.mIvFolderCheck.setVisibility(0);
            holder.mIvFolderCheck.setColorFilter(GalleryFinal.getGalleryTheme().getCheckSelectedColor());
            return;
        }
        holder.mIvFolderCheck.setVisibility(8);
    }

    public void setSelectFolder(PhotoFolderInfo photoFolderInfo) {
        this.mSelectFolder = photoFolderInfo;
    }

    public PhotoFolderInfo getSelectFolder() {
        return this.mSelectFolder;
    }
}
