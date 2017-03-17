package cn.finalteam.galleryfinal;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PointerIconCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.finalteam.galleryfinal.adapter.FolderListAdapter;
import cn.finalteam.galleryfinal.adapter.PhotoListAdapter;
import cn.finalteam.galleryfinal.adapter.PhotoListAdapter.PhotoViewHolder;
import cn.finalteam.galleryfinal.model.PhotoFolderInfo;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.permission.AfterPermissionGranted;
import cn.finalteam.galleryfinal.permission.EasyPermissions;
import cn.finalteam.galleryfinal.utils.PhotoTools;
import cn.finalteam.galleryfinal.widget.FloatingActionButton;
import cn.finalteam.toolsfinal.DeviceUtils;
import cn.finalteam.toolsfinal.StringUtils;
import cn.finalteam.toolsfinal.io.FilenameUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PhotoSelectActivity extends PhotoBaseActivity implements OnClickListener, OnItemClickListener {
    private final int HANDLER_REFRESH_LIST_EVENT = PointerIconCompat.TYPE_HAND;
    private final int HANLDER_TAKE_PHOTO_EVENT = 1000;
    private List<PhotoFolderInfo> mAllPhotoFolderList;
    private List<PhotoInfo> mCurPhotoList;
    private FloatingActionButton mFabOk;
    private FolderListAdapter mFolderListAdapter;
    private GridView mGvPhotoList;
    private Handler mHanlder = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1000) {
                PhotoSelectActivity.this.takeRefreshGallery(msg.obj);
                PhotoSelectActivity.this.refreshSelectCount();
            } else if (msg.what == PointerIconCompat.TYPE_HAND) {
                PhotoSelectActivity.this.refreshSelectCount();
                PhotoSelectActivity.this.mPhotoListAdapter.notifyDataSetChanged();
                PhotoSelectActivity.this.mFolderListAdapter.notifyDataSetChanged();
                if (((PhotoFolderInfo) PhotoSelectActivity.this.mAllPhotoFolderList.get(0)).getPhotoList() == null || ((PhotoFolderInfo) PhotoSelectActivity.this.mAllPhotoFolderList.get(0)).getPhotoList().size() == 0) {
                    PhotoSelectActivity.this.mTvEmptyView.setText(R.string.no_photo);
                }
                PhotoSelectActivity.this.mGvPhotoList.setEnabled(true);
                PhotoSelectActivity.this.mLlTitle.setEnabled(true);
                PhotoSelectActivity.this.mIvTakePhoto.setEnabled(true);
            }
        }
    };
    private boolean mHasRefreshGallery = false;
    private ImageView mIvBack;
    private ImageView mIvClear;
    private ImageView mIvFolderArrow;
    private ImageView mIvPreView;
    private ImageView mIvTakePhoto;
    private LinearLayout mLlFolderPanel;
    private LinearLayout mLlTitle;
    private ListView mLvFolderList;
    private PhotoListAdapter mPhotoListAdapter;
    private ArrayList<PhotoInfo> mSelectPhotoList = new ArrayList();
    private RelativeLayout mTitlebar;
    private TextView mTvChooseCount;
    private TextView mTvEmptyView;
    private TextView mTvSubTitle;
    private TextView mTvTitle;

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectPhotoMap", this.mSelectPhotoList);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mSelectPhotoList = (ArrayList) getIntent().getSerializableExtra("selectPhotoMap");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GalleryFinal.getFunctionConfig() == null || GalleryFinal.getGalleryTheme() == null) {
            resultFailureDelayed(getString(R.string.please_reopen_gf), true);
        } else {
            setContentView(R.layout.gf_activity_photo_select);
            mPhotoTargetFolder = null;
            findViews();
            setListener();
            this.mAllPhotoFolderList = new ArrayList();
            this.mFolderListAdapter = new FolderListAdapter(this, this.mAllPhotoFolderList, GalleryFinal.getFunctionConfig());
            this.mLvFolderList.setAdapter(this.mFolderListAdapter);
            this.mCurPhotoList = new ArrayList();
            this.mPhotoListAdapter = new PhotoListAdapter(this, this.mCurPhotoList, this.mSelectPhotoList, this.mScreenWidth);
            this.mGvPhotoList.setAdapter(this.mPhotoListAdapter);
            if (GalleryFinal.getFunctionConfig().isMutiSelect()) {
                this.mTvChooseCount.setVisibility(0);
                this.mFabOk.setVisibility(0);
            }
            setTheme();
            this.mGvPhotoList.setEmptyView(this.mTvEmptyView);
            if (GalleryFinal.getFunctionConfig().isCamera()) {
                this.mIvTakePhoto.setVisibility(0);
            } else {
                this.mIvTakePhoto.setVisibility(8);
            }
            refreshSelectCount();
            requestGalleryPermission();
            this.mGvPhotoList.setOnScrollListener(GalleryFinal.getCoreConfig().getPauseOnScrollListener());
        }
        Global.mPhotoSelectActivity = this;
    }

    private void setTheme() {
        this.mIvBack.setImageResource(GalleryFinal.getGalleryTheme().getIconBack());
        if (GalleryFinal.getGalleryTheme().getIconBack() == R.drawable.ic_gf_back) {
            this.mIvBack.setColorFilter(GalleryFinal.getGalleryTheme().getTitleBarIconColor());
        }
        this.mIvFolderArrow.setImageResource(GalleryFinal.getGalleryTheme().getIconFolderArrow());
        if (GalleryFinal.getGalleryTheme().getIconFolderArrow() == R.drawable.ic_gf_triangle_arrow) {
            this.mIvFolderArrow.setColorFilter(GalleryFinal.getGalleryTheme().getTitleBarIconColor());
        }
        this.mIvClear.setImageResource(GalleryFinal.getGalleryTheme().getIconClear());
        if (GalleryFinal.getGalleryTheme().getIconClear() == R.drawable.ic_gf_clear) {
            this.mIvClear.setColorFilter(GalleryFinal.getGalleryTheme().getTitleBarIconColor());
        }
        this.mIvPreView.setImageResource(GalleryFinal.getGalleryTheme().getIconPreview());
        if (GalleryFinal.getGalleryTheme().getIconPreview() == R.drawable.ic_gf_preview) {
            this.mIvPreView.setColorFilter(GalleryFinal.getGalleryTheme().getTitleBarIconColor());
        }
        this.mIvTakePhoto.setImageResource(GalleryFinal.getGalleryTheme().getIconCamera());
        if (GalleryFinal.getGalleryTheme().getIconCamera() == R.drawable.ic_gf_camera) {
            this.mIvTakePhoto.setColorFilter(GalleryFinal.getGalleryTheme().getTitleBarIconColor());
        }
        this.mFabOk.setIcon(GalleryFinal.getGalleryTheme().getIconFab());
        this.mTitlebar.setBackgroundResource(R.drawable.act_home_title_bj);
        this.mTvSubTitle.setTextColor(GalleryFinal.getGalleryTheme().getTitleBarTextColor());
        this.mTvTitle.setTextColor(GalleryFinal.getGalleryTheme().getTitleBarTextColor());
        this.mTvChooseCount.setTextColor(GalleryFinal.getGalleryTheme().getTitleBarTextColor());
        this.mFabOk.setColorPressed(GalleryFinal.getGalleryTheme().getFabPressedColor());
        this.mFabOk.setColorNormal(GalleryFinal.getGalleryTheme().getFabNornalColor());
    }

    private void findViews() {
        this.mGvPhotoList = (GridView) findViewById(R.id.gv_photo_list);
        this.mLvFolderList = (ListView) findViewById(R.id.lv_folder_list);
        this.mTvSubTitle = (TextView) findViewById(R.id.tv_sub_title);
        this.mLlFolderPanel = (LinearLayout) findViewById(R.id.ll_folder_panel);
        this.mIvTakePhoto = (ImageView) findViewById(R.id.iv_take_photo);
        this.mTvChooseCount = (TextView) findViewById(R.id.tv_choose_count);
        this.mIvBack = (ImageView) findViewById(R.id.iv_back);
        this.mFabOk = (FloatingActionButton) findViewById(R.id.fab_ok);
        this.mTvEmptyView = (TextView) findViewById(R.id.tv_empty_view);
        this.mLlTitle = (LinearLayout) findViewById(R.id.ll_title);
        this.mIvClear = (ImageView) findViewById(R.id.iv_clear);
        this.mTitlebar = (RelativeLayout) findViewById(R.id.titlebar);
        this.mTvTitle = (TextView) findViewById(R.id.tv_title);
        this.mIvFolderArrow = (ImageView) findViewById(R.id.iv_folder_arrow);
        this.mIvPreView = (ImageView) findViewById(R.id.iv_preview);
    }

    private void setListener() {
        this.mLlTitle.setOnClickListener(this);
        this.mIvTakePhoto.setOnClickListener(this);
        this.mIvBack.setOnClickListener(this);
        this.mIvFolderArrow.setOnClickListener(this);
        this.mLvFolderList.setOnItemClickListener(this);
        this.mGvPhotoList.setOnItemClickListener(this);
        this.mFabOk.setOnClickListener(this);
        this.mIvClear.setOnClickListener(this);
        this.mIvPreView.setOnClickListener(this);
    }

    protected void deleteSelect(int photoId) {
        try {
            Iterator<PhotoInfo> iterator = this.mSelectPhotoList.iterator();
            while (iterator.hasNext()) {
                PhotoInfo info = (PhotoInfo) iterator.next();
                if (info != null && info.getPhotoId() == photoId) {
                    iterator.remove();
                    break;
                }
            }
        } catch (Exception e) {
        }
        refreshAdapter();
    }

    private void refreshAdapter() {
        this.mHanlder.sendEmptyMessageDelayed(PointerIconCompat.TYPE_HAND, 100);
    }

    protected void takeRefreshGallery(PhotoInfo photoInfo, boolean selected) {
        if (!isFinishing() && photoInfo != null) {
            Message message = this.mHanlder.obtainMessage();
            message.obj = photoInfo;
            message.what = 1000;
            this.mSelectPhotoList.add(photoInfo);
            this.mHanlder.sendMessageDelayed(message, 100);
        }
    }

    private void takeRefreshGallery(PhotoInfo photoInfo) {
        this.mCurPhotoList.add(0, photoInfo);
        this.mPhotoListAdapter.notifyDataSetChanged();
        List<PhotoInfo> photoInfoList = ((PhotoFolderInfo) this.mAllPhotoFolderList.get(0)).getPhotoList();
        if (photoInfoList == null) {
            photoInfoList = new ArrayList();
        }
        photoInfoList.add(0, photoInfo);
        ((PhotoFolderInfo) this.mAllPhotoFolderList.get(0)).setPhotoList(photoInfoList);
        List<PhotoInfo> list;
        if (this.mFolderListAdapter.getSelectFolder() != null) {
            PhotoFolderInfo photoFolderInfo = this.mFolderListAdapter.getSelectFolder();
            list = photoFolderInfo.getPhotoList();
            if (list == null) {
                list = new ArrayList();
            }
            list.add(0, photoInfo);
            if (list.size() == 1) {
                photoFolderInfo.setCoverPhoto(photoInfo);
            }
            this.mFolderListAdapter.getSelectFolder().setPhotoList(list);
        } else {
            String folderA = new File(photoInfo.getPhotoPath()).getParent();
            for (int i = 1; i < this.mAllPhotoFolderList.size(); i++) {
                PhotoFolderInfo folderInfo = (PhotoFolderInfo) this.mAllPhotoFolderList.get(i);
                String folderB = null;
                if (!StringUtils.isEmpty(photoInfo.getPhotoPath())) {
                    folderB = new File(photoInfo.getPhotoPath()).getParent();
                }
                if (TextUtils.equals(folderA, folderB)) {
                    list = folderInfo.getPhotoList();
                    if (list == null) {
                        list = new ArrayList();
                    }
                    list.add(0, photoInfo);
                    folderInfo.setPhotoList(list);
                    if (list.size() == 1) {
                        folderInfo.setCoverPhoto(photoInfo);
                    }
                }
            }
        }
        this.mFolderListAdapter.notifyDataSetChanged();
    }

    protected void takeResult(PhotoInfo photoInfo) {
        Message message = this.mHanlder.obtainMessage();
        message.obj = photoInfo;
        message.what = 1000;
        if (GalleryFinal.getFunctionConfig().isMutiSelect()) {
            this.mSelectPhotoList.add(photoInfo);
            this.mHanlder.sendMessageDelayed(message, 100);
            return;
        }
        this.mSelectPhotoList.clear();
        this.mSelectPhotoList.add(photoInfo);
        if (GalleryFinal.getFunctionConfig().isEditPhoto()) {
            this.mHasRefreshGallery = true;
            toPhotoEdit();
        } else {
            ArrayList<PhotoInfo> list = new ArrayList();
            list.add(photoInfo);
            resultData(list);
        }
        this.mHanlder.sendMessageDelayed(message, 100);
    }

    protected void toPhotoEdit() {
        Intent intent = new Intent(this, PhotoEditActivity.class);
        intent.putExtra("select_map", this.mSelectPhotoList);
        startActivity(intent);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ll_title || id == R.id.iv_folder_arrow) {
            if (this.mLlFolderPanel.getVisibility() == 0) {
                this.mLlFolderPanel.setVisibility(8);
                this.mLlFolderPanel.setAnimation(AnimationUtils.loadAnimation(this, R.anim.gf_flip_horizontal_out));
                return;
            }
            this.mLlFolderPanel.setAnimation(AnimationUtils.loadAnimation(this, R.anim.gf_flip_horizontal_in));
            this.mLlFolderPanel.setVisibility(0);
        } else if (id == R.id.iv_take_photo) {
            if (GalleryFinal.getFunctionConfig().isMutiSelect() && this.mSelectPhotoList.size() == GalleryFinal.getFunctionConfig().getMaxSize()) {
                toast(getString(R.string.select_max_tips));
            } else if (DeviceUtils.existSDCard()) {
                takePhotoAction();
            } else {
                toast(getString(R.string.empty_sdcard));
            }
        } else if (id == R.id.iv_back) {
            if (this.mLlFolderPanel.getVisibility() == 0) {
                this.mLlTitle.performClick();
            } else {
                finish();
            }
        } else if (id == R.id.fab_ok) {
            if (this.mSelectPhotoList.size() <= 0) {
                return;
            }
            if (GalleryFinal.getFunctionConfig().isEditPhoto()) {
                toPhotoEdit();
            } else {
                resultData(this.mSelectPhotoList);
            }
        } else if (id == R.id.iv_clear) {
            this.mSelectPhotoList.clear();
            this.mPhotoListAdapter.notifyDataSetChanged();
            refreshSelectCount();
        } else if (id == R.id.iv_preview) {
            Intent intent = new Intent(this, PhotoPreviewActivity.class);
            intent.putExtra("photo_list", this.mSelectPhotoList);
            startActivity(intent);
        }
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.lv_folder_list) {
            folderItemClick(position);
        } else {
            photoItemClick(view, position);
        }
    }

    private void folderItemClick(int position) {
        this.mLlFolderPanel.setVisibility(8);
        this.mCurPhotoList.clear();
        PhotoFolderInfo photoFolderInfo = (PhotoFolderInfo) this.mAllPhotoFolderList.get(position);
        if (photoFolderInfo.getPhotoList() != null) {
            this.mCurPhotoList.addAll(photoFolderInfo.getPhotoList());
        }
        this.mPhotoListAdapter.notifyDataSetChanged();
        if (position == 0) {
            mPhotoTargetFolder = null;
        } else {
            PhotoInfo photoInfo = photoFolderInfo.getCoverPhoto();
            if (photoInfo == null || StringUtils.isEmpty(photoInfo.getPhotoPath())) {
                mPhotoTargetFolder = null;
            } else {
                mPhotoTargetFolder = new File(photoInfo.getPhotoPath()).getParent();
            }
        }
        this.mTvSubTitle.setText(photoFolderInfo.getFolderName());
        this.mFolderListAdapter.setSelectFolder(photoFolderInfo);
        this.mFolderListAdapter.notifyDataSetChanged();
        if (this.mCurPhotoList.size() == 0) {
            this.mTvEmptyView.setText(R.string.no_photo);
        }
    }

    private void photoItemClick(View view, int position) {
        PhotoInfo info = (PhotoInfo) this.mCurPhotoList.get(position);
        if (GalleryFinal.getFunctionConfig().isMutiSelect()) {
            boolean checked;
            if (this.mSelectPhotoList.contains(info)) {
                try {
                    Iterator<PhotoInfo> iterator = this.mSelectPhotoList.iterator();
                    while (iterator.hasNext()) {
                        PhotoInfo pi = (PhotoInfo) iterator.next();
                        if (pi != null && TextUtils.equals(pi.getPhotoPath(), info.getPhotoPath())) {
                            iterator.remove();
                            break;
                        }
                    }
                } catch (Exception e) {
                }
                checked = false;
            } else if (GalleryFinal.getFunctionConfig().isMutiSelect() && this.mSelectPhotoList.size() == GalleryFinal.getFunctionConfig().getMaxSize()) {
                toast(getString(R.string.select_max_tips));
                return;
            } else {
                this.mSelectPhotoList.add(info);
                checked = true;
            }
            refreshSelectCount();
            PhotoViewHolder holder = (PhotoViewHolder) view.getTag();
            if (holder == null) {
                this.mPhotoListAdapter.notifyDataSetChanged();
                return;
            } else if (checked) {
                holder.mIvCheck.setBackgroundColor(GalleryFinal.getGalleryTheme().getCheckSelectedColor());
                return;
            } else {
                holder.mIvCheck.setBackgroundColor(GalleryFinal.getGalleryTheme().getCheckNornalColor());
                return;
            }
        }
        this.mSelectPhotoList.clear();
        this.mSelectPhotoList.add(info);
        String ext = FilenameUtils.getExtension(info.getPhotoPath());
        if (GalleryFinal.getFunctionConfig().isEditPhoto() && (ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg"))) {
            toPhotoEdit();
            return;
        }
        ArrayList<PhotoInfo> list = new ArrayList();
        list.add(info);
        resultData(list);
    }

    @SuppressLint({"StringFormatMatches"})
    public void refreshSelectCount() {
        this.mTvChooseCount.setText(getString(R.string.selected, new Object[]{Integer.valueOf(this.mSelectPhotoList.size()), Integer.valueOf(GalleryFinal.getFunctionConfig().getMaxSize())}));
        if (this.mSelectPhotoList.size() <= 0 || !GalleryFinal.getFunctionConfig().isMutiSelect()) {
            this.mIvClear.setVisibility(8);
        } else {
            this.mIvClear.setVisibility(0);
        }
        if (GalleryFinal.getFunctionConfig().isEnablePreview()) {
            this.mIvPreView.setVisibility(0);
        } else {
            this.mIvPreView.setVisibility(8);
        }
    }

    public void onPermissionsGranted(List<String> list) {
        getPhotos();
    }

    public void onPermissionsDenied(List<String> list) {
        this.mTvEmptyView.setText(R.string.permissions_denied_tips);
        this.mIvTakePhoto.setVisibility(8);
    }

    @AfterPermissionGranted(2001)
    private void requestGalleryPermission() {
        if (EasyPermissions.hasPermissions(this, "android.permission.READ_EXTERNAL_STORAGE")) {
            getPhotos();
            return;
        }
        EasyPermissions.requestPermissions(this, getString(R.string.permissions_tips_gallery), 2001, "android.permission.READ_EXTERNAL_STORAGE");
    }

    private void getPhotos() {
        this.mTvEmptyView.setText(R.string.waiting);
        this.mGvPhotoList.setEnabled(false);
        this.mLlTitle.setEnabled(false);
        this.mIvTakePhoto.setEnabled(false);
        new Thread() {
            public void run() {
                super.run();
                PhotoSelectActivity.this.mAllPhotoFolderList.clear();
                List<PhotoFolderInfo> allFolderList = PhotoTools.getAllPhotoFolder(PhotoSelectActivity.this, PhotoSelectActivity.this.mSelectPhotoList);
                PhotoSelectActivity.this.mAllPhotoFolderList.addAll(allFolderList);
                PhotoSelectActivity.this.mCurPhotoList.clear();
                if (allFolderList.size() > 0 && ((PhotoFolderInfo) allFolderList.get(0)).getPhotoList() != null) {
                    PhotoSelectActivity.this.mCurPhotoList.addAll(((PhotoFolderInfo) allFolderList.get(0)).getPhotoList());
                }
                PhotoSelectActivity.this.refreshAdapter();
            }
        }.start();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 2001:
                getPhotos();
                return;
            default:
                return;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || this.mLlFolderPanel.getVisibility() != 0) {
            return super.onKeyDown(keyCode, event);
        }
        this.mLlTitle.performClick();
        return true;
    }

    protected void onResume() {
        super.onResume();
        if (this.mHasRefreshGallery) {
            this.mHasRefreshGallery = false;
            requestGalleryPermission();
        }
    }

    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (GalleryFinal.getCoreConfig() != null && GalleryFinal.getCoreConfig().getImageLoader() != null) {
            GalleryFinal.getCoreConfig().getImageLoader().clearMemoryCache();
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        mPhotoTargetFolder = null;
        this.mSelectPhotoList.clear();
        System.gc();
    }
}
