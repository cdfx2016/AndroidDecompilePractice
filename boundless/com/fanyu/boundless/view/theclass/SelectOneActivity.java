package com.fanyu.boundless.view.theclass;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.view.base.BaseActivity;

public class SelectOneActivity extends BaseActivity {
    @Bind({2131624220})
    GridView gridview;
    private GridAdapter mGridAdapter;
    private int[] mImgIds = new int[]{R.mipmap.banhui1, R.mipmap.banhui2, R.mipmap.banhui3, R.mipmap.banhui4, R.mipmap.banhui5, R.mipmap.banhui6, R.mipmap.banhui7, R.mipmap.banhui8, R.mipmap.banhui9, R.mipmap.banhui10, R.mipmap.banhui11, R.mipmap.banhui12, R.mipmap.banhui13, R.mipmap.banhui14, R.mipmap.banhui15, R.mipmap.banhui16, R.mipmap.banhui17, R.mipmap.banhui18, R.mipmap.banhui19, R.mipmap.banhui20};

    private class GridAdapter extends BaseAdapter {
        private Context mContext;

        public GridAdapter(Context ctx) {
            this.mContext = ctx;
        }

        public int getCount() {
            return SelectOneActivity.this.mImgIds.length;
        }

        public Integer getItem(int position) {
            return Integer.valueOf(SelectOneActivity.this.mImgIds[position]);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(this.mContext).inflate(R.layout.select_gridview, null);
            }
            ((ImageView) convertView.findViewById(R.id.img_view)).setBackgroundResource(SelectOneActivity.this.mImgIds[position]);
            return convertView;
        }
    }

    protected void initView() {
        setContentView((int) R.layout.activity_select_one);
    }

    protected void initPresenter() {
    }

    @RequiresApi(api = 23)
    protected void init() {
        this.mGridAdapter = new GridAdapter(this);
        this.gridview.setAdapter(this.mGridAdapter);
        this.gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(SelectOneActivity.this, CreateClassActivity.class);
                intent.putExtra("image", SelectOneActivity.this.mImgIds[position]);
                SelectOneActivity.this.setResult(-1, intent);
                SelectOneActivity.this.finish();
            }
        });
    }

    @OnClick({2131624066})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            default:
                return;
        }
    }
}
