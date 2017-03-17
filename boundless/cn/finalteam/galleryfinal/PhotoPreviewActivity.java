package cn.finalteam.galleryfinal;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.finalteam.galleryfinal.adapter.PhotoPreviewAdapter;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.galleryfinal.widget.GFViewPager;
import java.util.List;

public class PhotoPreviewActivity extends PhotoBaseActivity implements OnPageChangeListener {
    static final String PHOTO_LIST = "photo_list";
    private OnClickListener mBackListener = new OnClickListener() {
        public void onClick(View v) {
            PhotoPreviewActivity.this.finish();
        }
    };
    private ImageView mIvBack;
    private List<PhotoInfo> mPhotoList;
    private PhotoPreviewAdapter mPhotoPreviewAdapter;
    private ThemeConfig mThemeConfig;
    private RelativeLayout mTitleBar;
    private TextView mTvIndicator;
    private TextView mTvTitle;
    private GFViewPager mVpPager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mThemeConfig = GalleryFinal.getGalleryTheme();
        if (this.mThemeConfig == null) {
            resultFailureDelayed(getString(R.string.please_reopen_gf), true);
            return;
        }
        setContentView(R.layout.gf_activity_photo_preview);
        findViews();
        setListener();
        setTheme();
        this.mPhotoList = (List) getIntent().getSerializableExtra(PHOTO_LIST);
        this.mPhotoPreviewAdapter = new PhotoPreviewAdapter(this, this.mPhotoList);
        this.mVpPager.setAdapter(this.mPhotoPreviewAdapter);
    }

    private void findViews() {
        this.mTitleBar = (RelativeLayout) findViewById(R.id.titlebar);
        this.mIvBack = (ImageView) findViewById(R.id.iv_back);
        this.mTvTitle = (TextView) findViewById(R.id.tv_title);
        this.mTvIndicator = (TextView) findViewById(R.id.tv_indicator);
        this.mVpPager = (GFViewPager) findViewById(R.id.vp_pager);
    }

    private void setListener() {
        this.mVpPager.addOnPageChangeListener(this);
        this.mIvBack.setOnClickListener(this.mBackListener);
    }

    private void setTheme() {
        this.mIvBack.setImageResource(this.mThemeConfig.getIconBack());
        if (this.mThemeConfig.getIconBack() == R.drawable.ic_gf_back) {
            this.mIvBack.setColorFilter(this.mThemeConfig.getTitleBarIconColor());
        }
        this.mTitleBar.setBackgroundColor(this.mThemeConfig.getTitleBarBgColor());
        this.mTvTitle.setTextColor(this.mThemeConfig.getTitleBarTextColor());
        if (this.mThemeConfig.getPreviewBg() != null) {
            this.mVpPager.setBackgroundDrawable(this.mThemeConfig.getPreviewBg());
        }
    }

    protected void takeResult(PhotoInfo info) {
    }

    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        this.mTvIndicator.setText((position + 1) + "/" + this.mPhotoList.size());
    }

    public void onPageSelected(int position) {
    }

    public void onPageScrollStateChanged(int state) {
    }
}
