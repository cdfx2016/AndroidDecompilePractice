package cn.finalteam.galleryfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.finalteam.galleryfinal.adapter.PhotoEditListAdapter;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.model.PhotoTempModel;
import cn.finalteam.galleryfinal.utils.ILogger;
import cn.finalteam.galleryfinal.utils.RecycleViewBitmapUtils;
import cn.finalteam.galleryfinal.utils.Utils;
import cn.finalteam.galleryfinal.widget.FloatingActionButton;
import cn.finalteam.galleryfinal.widget.HorizontalListView;
import cn.finalteam.galleryfinal.widget.crop.CropImageActivity;
import cn.finalteam.galleryfinal.widget.crop.CropImageView;
import cn.finalteam.galleryfinal.widget.zoonview.PhotoView;
import cn.finalteam.toolsfinal.ActivityManager;
import cn.finalteam.toolsfinal.StringUtils;
import cn.finalteam.toolsfinal.io.FileUtils;
import cn.finalteam.toolsfinal.io.FilenameUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class PhotoEditActivity extends CropImageActivity implements OnItemClickListener, OnClickListener {
    static final String CROP_PHOTO_ACTION = "crop_photo_action";
    static final String EDIT_PHOTO_ACTION = "edit_photo_action";
    static final String SELECT_MAP = "select_map";
    static final String TAKE_PHOTO_ACTION = "take_photo_action";
    private final int CROP_FAIL = 2;
    private final int CROP_SUC = 1;
    private final int UPDATE_PATH = 3;
    private boolean mCropPhotoAction;
    private boolean mCropState;
    private Drawable mDefaultDrawable;
    private boolean mEditPhotoAction;
    private File mEditPhotoCacheFile;
    private FloatingActionButton mFabCrop;
    private Handler mHanlder = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String path;
            PhotoInfo photoInfo;
            if (msg.what == 1) {
                path = msg.obj;
                photoInfo = (PhotoInfo) PhotoEditActivity.this.mPhotoList.get(PhotoEditActivity.this.mSelectIndex);
                try {
                    for (Entry<Integer, PhotoTempModel> entry : PhotoEditActivity.this.mPhotoTempMap.entrySet()) {
                        if (((Integer) entry.getKey()).intValue() == photoInfo.getPhotoId()) {
                            PhotoTempModel tempModel = (PhotoTempModel) entry.getValue();
                            tempModel.setSourcePath(path);
                            tempModel.setOrientation(0);
                        }
                    }
                } catch (Exception e) {
                }
                PhotoEditActivity.this.toast(PhotoEditActivity.this.getString(R.string.crop_suc));
                Message message = PhotoEditActivity.this.mHanlder.obtainMessage();
                message.what = 3;
                message.obj = path;
                PhotoEditActivity.this.mHanlder.sendMessage(message);
            } else if (msg.what == 2) {
                PhotoEditActivity.this.toast(PhotoEditActivity.this.getString(R.string.crop_fail));
            } else if (msg.what == 3) {
                if (PhotoEditActivity.this.mPhotoList.get(PhotoEditActivity.this.mSelectIndex) != null) {
                    photoInfo = (PhotoInfo) PhotoEditActivity.this.mPhotoList.get(PhotoEditActivity.this.mSelectIndex);
                    path = (String) msg.obj;
                    try {
                        Iterator<PhotoInfo> iterator = PhotoEditActivity.this.mSelectPhotoList.iterator();
                        while (iterator.hasNext()) {
                            PhotoInfo info = (PhotoInfo) iterator.next();
                            if (info != null && info.getPhotoId() == photoInfo.getPhotoId()) {
                                info.setPhotoPath(path);
                            }
                        }
                    } catch (Exception e2) {
                    }
                    photoInfo.setPhotoPath(path);
                    PhotoEditActivity.this.loadImage(photoInfo);
                    PhotoEditActivity.this.mPhotoEditListAdapter.notifyDataSetChanged();
                }
                if (GalleryFinal.getFunctionConfig().isForceCrop() && !GalleryFinal.getFunctionConfig().isForceCropEdit()) {
                    PhotoEditActivity.this.resultAction();
                }
            }
            PhotoEditActivity.this.corpPageState(false);
            PhotoEditActivity.this.mCropState = false;
            PhotoEditActivity.this.mTvTitle.setText(R.string.photo_edit);
        }
    };
    private ImageView mIvBack;
    private ImageView mIvCrop;
    private CropImageView mIvCropPhoto;
    private ImageView mIvPreView;
    private ImageView mIvRotate;
    private PhotoView mIvSourcePhoto;
    private ImageView mIvTakePhoto;
    private LinearLayout mLlGallery;
    private HorizontalListView mLvGallery;
    private PhotoEditListAdapter mPhotoEditListAdapter;
    private ArrayList<PhotoInfo> mPhotoList;
    private LinkedHashMap<Integer, PhotoTempModel> mPhotoTempMap;
    private ProgressDialog mProgressDialog;
    private boolean mRotating;
    private int mSelectIndex = 0;
    private ArrayList<PhotoInfo> mSelectPhotoList;
    private LinearLayout mTitlebar;
    private TextView mTvEmptyView;
    private TextView mTvTitle;

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("selectPhotoMap", this.mSelectPhotoList);
        outState.putSerializable("editPhotoCacheFile", this.mEditPhotoCacheFile);
        outState.putSerializable("photoTempMap", this.mPhotoTempMap);
        outState.putInt("selectIndex", this.mSelectIndex);
        outState.putBoolean("cropState", this.mCropState);
        outState.putBoolean("rotating", this.mRotating);
        outState.putBoolean("takePhotoAction", this.mTakePhotoAction);
        outState.putBoolean("cropPhotoAction", this.mCropPhotoAction);
        outState.putBoolean("editPhotoAction", this.mEditPhotoAction);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mSelectPhotoList = (ArrayList) getIntent().getSerializableExtra("selectPhotoMap");
        this.mEditPhotoCacheFile = (File) savedInstanceState.getSerializable("editPhotoCacheFile");
        this.mPhotoTempMap = new LinkedHashMap((HashMap) getIntent().getSerializableExtra("results"));
        this.mSelectIndex = savedInstanceState.getInt("selectIndex");
        this.mCropState = savedInstanceState.getBoolean("cropState");
        this.mRotating = savedInstanceState.getBoolean("rotating");
        this.mTakePhotoAction = savedInstanceState.getBoolean("takePhotoAction");
        this.mCropPhotoAction = savedInstanceState.getBoolean("cropPhotoAction");
        this.mEditPhotoAction = savedInstanceState.getBoolean("editPhotoAction");
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (GalleryFinal.getFunctionConfig() == null || GalleryFinal.getGalleryTheme() == null) {
            resultFailureDelayed(getString(R.string.please_reopen_gf), true);
            return;
        }
        setContentView(R.layout.gf_activity_photo_edit);
        this.mDefaultDrawable = getResources().getDrawable(R.drawable.ic_gf_default_photo);
        this.mSelectPhotoList = (ArrayList) getIntent().getSerializableExtra(SELECT_MAP);
        this.mTakePhotoAction = getIntent().getBooleanExtra(TAKE_PHOTO_ACTION, false);
        this.mCropPhotoAction = getIntent().getBooleanExtra(CROP_PHOTO_ACTION, false);
        this.mEditPhotoAction = getIntent().getBooleanExtra(EDIT_PHOTO_ACTION, false);
        if (this.mSelectPhotoList == null) {
            this.mSelectPhotoList = new ArrayList();
        }
        this.mPhotoTempMap = new LinkedHashMap();
        this.mPhotoList = new ArrayList(this.mSelectPhotoList);
        this.mEditPhotoCacheFile = GalleryFinal.getCoreConfig().getEditPhotoCacheFolder();
        if (this.mPhotoList == null) {
            this.mPhotoList = new ArrayList();
        }
        Iterator it = this.mPhotoList.iterator();
        while (it.hasNext()) {
            PhotoInfo info = (PhotoInfo) it.next();
            this.mPhotoTempMap.put(Integer.valueOf(info.getPhotoId()), new PhotoTempModel(info.getPhotoPath()));
        }
        findViews();
        setListener();
        setTheme();
        this.mPhotoEditListAdapter = new PhotoEditListAdapter(this, this.mPhotoList, this.mScreenWidth);
        this.mLvGallery.setAdapter(this.mPhotoEditListAdapter);
        try {
            File nomediaFile = new File(this.mEditPhotoCacheFile, ".nomedia");
            if (!nomediaFile.exists()) {
                nomediaFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (GalleryFinal.getFunctionConfig().isCamera()) {
            this.mIvTakePhoto.setVisibility(0);
        }
        if (GalleryFinal.getFunctionConfig().isCrop()) {
            this.mIvCrop.setVisibility(0);
        }
        if (GalleryFinal.getFunctionConfig().isRotate()) {
            this.mIvRotate.setVisibility(0);
        }
        if (!GalleryFinal.getFunctionConfig().isMutiSelect()) {
            this.mLlGallery.setVisibility(8);
        }
        initCrop(this.mIvCropPhoto, GalleryFinal.getFunctionConfig().isCropSquare(), GalleryFinal.getFunctionConfig().getCropWidth(), GalleryFinal.getFunctionConfig().getCropHeight());
        if (this.mPhotoList.size() > 0 && !this.mTakePhotoAction) {
            loadImage((PhotoInfo) this.mPhotoList.get(0));
        }
        if (this.mTakePhotoAction) {
            takePhotoAction();
        }
        if (this.mCropPhotoAction) {
            this.mIvCrop.performClick();
            if (!(GalleryFinal.getFunctionConfig().isRotate() || GalleryFinal.getFunctionConfig().isCamera())) {
                this.mIvCrop.setVisibility(8);
            }
        } else {
            hasForceCrop();
        }
        if (GalleryFinal.getFunctionConfig().isEnablePreview()) {
            this.mIvPreView.setVisibility(0);
        }
    }

    private void setTheme() {
        this.mIvBack.setImageResource(GalleryFinal.getGalleryTheme().getIconBack());
        if (GalleryFinal.getGalleryTheme().getIconBack() == R.drawable.ic_gf_back) {
            this.mIvBack.setColorFilter(GalleryFinal.getGalleryTheme().getTitleBarIconColor());
        }
        this.mIvTakePhoto.setImageResource(GalleryFinal.getGalleryTheme().getIconCamera());
        if (GalleryFinal.getGalleryTheme().getIconCamera() == R.drawable.ic_gf_camera) {
            this.mIvTakePhoto.setColorFilter(GalleryFinal.getGalleryTheme().getTitleBarIconColor());
        }
        this.mIvCrop.setImageResource(GalleryFinal.getGalleryTheme().getIconCrop());
        if (GalleryFinal.getGalleryTheme().getIconCrop() == R.drawable.ic_gf_crop) {
            this.mIvCrop.setColorFilter(GalleryFinal.getGalleryTheme().getTitleBarIconColor());
        }
        this.mIvPreView.setImageResource(GalleryFinal.getGalleryTheme().getIconPreview());
        if (GalleryFinal.getGalleryTheme().getIconPreview() == R.drawable.ic_gf_preview) {
            this.mIvPreView.setColorFilter(GalleryFinal.getGalleryTheme().getTitleBarIconColor());
        }
        this.mIvRotate.setImageResource(GalleryFinal.getGalleryTheme().getIconRotate());
        if (GalleryFinal.getGalleryTheme().getIconRotate() == R.drawable.ic_gf_rotate) {
            this.mIvRotate.setColorFilter(GalleryFinal.getGalleryTheme().getTitleBarIconColor());
        }
        if (GalleryFinal.getGalleryTheme().getEditPhotoBgTexture() != null) {
            this.mIvSourcePhoto.setBackgroundDrawable(GalleryFinal.getGalleryTheme().getEditPhotoBgTexture());
            this.mIvCropPhoto.setBackgroundDrawable(GalleryFinal.getGalleryTheme().getEditPhotoBgTexture());
        }
        this.mFabCrop.setIcon(GalleryFinal.getGalleryTheme().getIconFab());
        this.mTitlebar.setBackgroundColor(GalleryFinal.getGalleryTheme().getTitleBarBgColor());
        this.mTvTitle.setTextColor(GalleryFinal.getGalleryTheme().getTitleBarTextColor());
        this.mFabCrop.setColorPressed(GalleryFinal.getGalleryTheme().getFabPressedColor());
        this.mFabCrop.setColorNormal(GalleryFinal.getGalleryTheme().getFabNornalColor());
    }

    private void findViews() {
        this.mIvTakePhoto = (ImageView) findViewById(R.id.iv_take_photo);
        this.mIvCropPhoto = (CropImageView) findViewById(R.id.iv_crop_photo);
        this.mIvSourcePhoto = (PhotoView) findViewById(R.id.iv_source_photo);
        this.mLvGallery = (HorizontalListView) findViewById(R.id.lv_gallery);
        this.mLlGallery = (LinearLayout) findViewById(R.id.ll_gallery);
        this.mIvBack = (ImageView) findViewById(R.id.iv_back);
        this.mTvEmptyView = (TextView) findViewById(R.id.tv_empty_view);
        this.mFabCrop = (FloatingActionButton) findViewById(R.id.fab_crop);
        this.mIvCrop = (ImageView) findViewById(R.id.iv_crop);
        this.mIvRotate = (ImageView) findViewById(R.id.iv_rotate);
        this.mTvTitle = (TextView) findViewById(R.id.tv_title);
        this.mTitlebar = (LinearLayout) findViewById(R.id.titlebar);
        this.mIvPreView = (ImageView) findViewById(R.id.iv_preview);
    }

    private void setListener() {
        this.mIvTakePhoto.setOnClickListener(this);
        this.mIvBack.setOnClickListener(this);
        this.mLvGallery.setOnItemClickListener(this);
        this.mFabCrop.setOnClickListener(this);
        this.mIvCrop.setOnClickListener(this);
        this.mIvRotate.setOnClickListener(this);
        this.mIvPreView.setOnClickListener(this);
    }

    protected void takeResult(PhotoInfo info) {
        if (!GalleryFinal.getFunctionConfig().isMutiSelect()) {
            this.mPhotoList.clear();
            this.mSelectPhotoList.clear();
        }
        this.mPhotoList.add(0, info);
        this.mSelectPhotoList.add(info);
        this.mPhotoTempMap.put(Integer.valueOf(info.getPhotoId()), new PhotoTempModel(info.getPhotoPath()));
        if (GalleryFinal.getFunctionConfig().isEditPhoto() || !this.mTakePhotoAction) {
            if (GalleryFinal.getFunctionConfig().isEnablePreview()) {
                this.mIvPreView.setVisibility(0);
            }
            this.mPhotoEditListAdapter.notifyDataSetChanged();
            PhotoSelectActivity activity = (PhotoSelectActivity) ActivityManager.getActivityManager().getActivity(PhotoSelectActivity.class.getName());
            if (activity != null) {
                activity.takeRefreshGallery(info, true);
            }
            loadImage(info);
            hasForceCrop();
            return;
        }
        resultAction();
    }

    private void loadImage(PhotoInfo photo) {
        this.mTvEmptyView.setVisibility(8);
        this.mIvSourcePhoto.setVisibility(0);
        this.mIvCropPhoto.setVisibility(8);
        String path = "";
        if (photo != null) {
            path = photo.getPhotoPath();
        }
        if (GalleryFinal.getFunctionConfig().isCrop()) {
            setSourceUri(Uri.fromFile(new File(path)));
        }
        GalleryFinal.getCoreConfig().getImageLoader().displayImage(this, path, this.mIvSourcePhoto, this.mDefaultDrawable, this.mScreenWidth, this.mScreenHeight);
    }

    public void deleteIndex(int position, PhotoInfo dPhoto) {
        if (dPhoto != null) {
            PhotoSelectActivity activity = (PhotoSelectActivity) ActivityManager.getActivityManager().getActivity(PhotoSelectActivity.class.getName());
            if (activity != null) {
                activity.deleteSelect(dPhoto.getPhotoId());
            }
            try {
                Iterator<PhotoInfo> iterator = this.mSelectPhotoList.iterator();
                while (iterator.hasNext()) {
                    PhotoInfo info = (PhotoInfo) iterator.next();
                    if (info != null && info.getPhotoId() == dPhoto.getPhotoId()) {
                        iterator.remove();
                        break;
                    }
                }
            } catch (Exception e) {
            }
        }
        if (this.mPhotoList.size() == 0) {
            this.mSelectIndex = 0;
            this.mTvEmptyView.setText(R.string.no_photo);
            this.mTvEmptyView.setVisibility(0);
            this.mIvSourcePhoto.setVisibility(8);
            this.mIvCropPhoto.setVisibility(8);
            this.mIvPreView.setVisibility(8);
            return;
        }
        if (position == 0) {
            this.mSelectIndex = 0;
        } else if (position == this.mPhotoList.size()) {
            this.mSelectIndex = position - 1;
        } else {
            this.mSelectIndex = position;
        }
        loadImage((PhotoInfo) this.mPhotoList.get(this.mSelectIndex));
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        this.mSelectIndex = i;
        loadImage((PhotoInfo) this.mPhotoList.get(i));
    }

    public void setCropSaveSuccess(File file) {
        Message message = this.mHanlder.obtainMessage();
        message.what = 1;
        message.obj = file.getAbsolutePath();
        this.mHanlder.sendMessage(message);
    }

    public void setCropSaveException(Throwable throwable) {
        this.mHanlder.sendEmptyMessage(2);
    }

    public void onClick(View v) {
        boolean z = true;
        int id = v.getId();
        String ext;
        if (id == R.id.fab_crop) {
            if (this.mPhotoList.size() != 0) {
                if (this.mCropState) {
                    System.gc();
                    PhotoInfo photoInfo = (PhotoInfo) this.mPhotoList.get(this.mSelectIndex);
                    try {
                        File toFile;
                        ext = FilenameUtils.getExtension(photoInfo.getPhotoPath());
                        if (GalleryFinal.getFunctionConfig().isCropReplaceSource()) {
                            toFile = new File(photoInfo.getPhotoPath());
                        } else {
                            toFile = new File(this.mEditPhotoCacheFile, Utils.getFileName(photoInfo.getPhotoPath()) + "_crop." + ext);
                        }
                        FileUtils.mkdirs(toFile.getParentFile());
                        onSaveClicked(toFile);
                        return;
                    } catch (Exception e) {
                        ILogger.e(e);
                        return;
                    }
                }
                resultAction();
            }
        } else if (id == R.id.iv_crop) {
            if (this.mPhotoList.size() > 0) {
                ext = FilenameUtils.getExtension(((PhotoInfo) this.mPhotoList.get(this.mSelectIndex)).getPhotoPath());
                if (StringUtils.isEmpty(ext) || !(ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg"))) {
                    toast(getString(R.string.edit_letoff_photo_format));
                    return;
                }
                if (this.mCropState) {
                    setCropEnabled(false);
                    corpPageState(false);
                    this.mTvTitle.setText(R.string.photo_edit);
                } else {
                    corpPageState(true);
                    setCropEnabled(true);
                    this.mTvTitle.setText(R.string.photo_crop);
                }
                if (this.mCropState) {
                    z = false;
                }
                this.mCropState = z;
            }
        } else if (id == R.id.iv_rotate) {
            rotatePhoto();
        } else if (id == R.id.iv_take_photo) {
            if (GalleryFinal.getFunctionConfig().isMutiSelect() && GalleryFinal.getFunctionConfig().getMaxSize() == this.mSelectPhotoList.size()) {
                toast(getString(R.string.select_max_tips));
            } else {
                takePhotoAction();
            }
        } else if (id == R.id.iv_back) {
            if (this.mCropState && ((!this.mCropPhotoAction || GalleryFinal.getFunctionConfig().isRotate() || GalleryFinal.getFunctionConfig().isCamera()) && GalleryFinal.getFunctionConfig().isForceCrop() && GalleryFinal.getFunctionConfig().isForceCropEdit())) {
                this.mIvCrop.performClick();
            } else {
                finish();
            }
        } else if (id == R.id.iv_preview) {
            Intent intent = new Intent(this, PhotoPreviewActivity.class);
            intent.putExtra("photo_list", this.mSelectPhotoList);
            startActivity(intent);
        }
    }

    private void resultAction() {
        resultData(this.mSelectPhotoList);
    }

    private void hasForceCrop() {
        if (GalleryFinal.getFunctionConfig().isForceCrop()) {
            this.mIvCrop.performClick();
            if (!GalleryFinal.getFunctionConfig().isForceCropEdit()) {
                this.mIvCrop.setVisibility(8);
            }
        }
    }

    private void rotatePhoto() {
        if (this.mPhotoList.size() > 0 && this.mPhotoList.get(this.mSelectIndex) != null && !this.mRotating) {
            final PhotoInfo photoInfo = (PhotoInfo) this.mPhotoList.get(this.mSelectIndex);
            final String ext = FilenameUtils.getExtension(photoInfo.getPhotoPath());
            if (StringUtils.isEmpty(ext) || !(ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg"))) {
                toast(getString(R.string.edit_letoff_photo_format));
                return;
            }
            this.mRotating = true;
            if (photoInfo != null) {
                File file;
                final PhotoTempModel photoTempModel = (PhotoTempModel) this.mPhotoTempMap.get(Integer.valueOf(photoInfo.getPhotoId()));
                final String path = photoTempModel.getSourcePath();
                if (GalleryFinal.getFunctionConfig().isRotateReplaceSource()) {
                    file = new File(path);
                } else {
                    file = new File(this.mEditPhotoCacheFile, Utils.getFileName(path) + "_rotate." + ext);
                }
                final File rotateFile = file;
                new AsyncTask<Void, Void, Bitmap>() {
                    protected void onPreExecute() {
                        super.onPreExecute();
                        PhotoEditActivity.this.mTvEmptyView.setVisibility(0);
                        PhotoEditActivity.this.mProgressDialog = ProgressDialog.show(PhotoEditActivity.this, "", PhotoEditActivity.this.getString(R.string.waiting), true, false);
                    }

                    protected Bitmap doInBackground(Void... params) {
                        int orientation;
                        if (GalleryFinal.getFunctionConfig().isRotateReplaceSource()) {
                            orientation = 90;
                        } else {
                            orientation = photoTempModel.getOrientation() + 90;
                        }
                        Bitmap bitmap = Utils.rotateBitmap(path, orientation, PhotoEditActivity.this.mScreenWidth, PhotoEditActivity.this.mScreenHeight);
                        if (bitmap != null) {
                            CompressFormat format;
                            if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
                                format = CompressFormat.JPEG;
                            } else {
                                format = CompressFormat.PNG;
                            }
                            Utils.saveBitmap(bitmap, format, rotateFile);
                        }
                        return bitmap;
                    }

                    protected void onPostExecute(Bitmap bitmap) {
                        super.onPostExecute(bitmap);
                        if (PhotoEditActivity.this.mProgressDialog != null) {
                            PhotoEditActivity.this.mProgressDialog.dismiss();
                            PhotoEditActivity.this.mProgressDialog = null;
                        }
                        if (bitmap != null) {
                            bitmap.recycle();
                            PhotoEditActivity.this.mTvEmptyView.setVisibility(8);
                            if (!GalleryFinal.getFunctionConfig().isRotateReplaceSource()) {
                                int orientation = photoTempModel.getOrientation() + 90;
                                if (orientation == 360) {
                                    orientation = 0;
                                }
                                photoTempModel.setOrientation(orientation);
                            }
                            Message message = PhotoEditActivity.this.mHanlder.obtainMessage();
                            message.what = 3;
                            message.obj = rotateFile.getAbsolutePath();
                            PhotoEditActivity.this.mHanlder.sendMessage(message);
                        } else {
                            PhotoEditActivity.this.mTvEmptyView.setText(R.string.no_photo);
                        }
                        PhotoEditActivity.this.loadImage(photoInfo);
                        PhotoEditActivity.this.mRotating = false;
                    }
                }.execute(new Void[0]);
            }
        }
    }

    private void corpPageState(boolean crop) {
        if (crop) {
            this.mIvSourcePhoto.setVisibility(8);
            this.mIvCropPhoto.setVisibility(0);
            this.mLlGallery.setVisibility(8);
            if (GalleryFinal.getFunctionConfig().isCrop()) {
                this.mIvCrop.setVisibility(0);
            }
            if (GalleryFinal.getFunctionConfig().isRotate()) {
                this.mIvRotate.setVisibility(8);
            }
            if (GalleryFinal.getFunctionConfig().isCamera()) {
                this.mIvTakePhoto.setVisibility(8);
                return;
            }
            return;
        }
        this.mIvSourcePhoto.setVisibility(0);
        this.mIvCropPhoto.setVisibility(8);
        if (GalleryFinal.getFunctionConfig().isCrop()) {
            this.mIvCrop.setVisibility(0);
        }
        if (GalleryFinal.getFunctionConfig().isRotate()) {
            this.mIvRotate.setVisibility(0);
        }
        if (GalleryFinal.getFunctionConfig().isCamera()) {
            this.mIvTakePhoto.setVisibility(0);
        }
        if (GalleryFinal.getFunctionConfig().isMutiSelect()) {
            this.mLlGallery.setVisibility(0);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        RecycleViewBitmapUtils.recycleImageView(this.mIvCropPhoto);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4 || !this.mCropState || ((this.mCropPhotoAction && !GalleryFinal.getFunctionConfig().isRotate() && !GalleryFinal.getFunctionConfig().isCamera()) || !GalleryFinal.getFunctionConfig().isForceCrop() || !GalleryFinal.getFunctionConfig().isForceCropEdit())) {
            return super.onKeyDown(keyCode, event);
        }
        this.mIvCrop.performClick();
        return true;
    }
}
