package com.fanyu.boundless.widget;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.fanyu.boundless.R;
import java.util.List;

public class SpinerPopWindow<T> extends PopupWindow {
    private Activity context;
    private LayoutInflater inflater;
    private LinearLayout linearLayout;
    private List<T> list;
    private MyAdapter mAdapter;
    private ListView mListView;
    private TextView mtextview;
    private String niString;
    private TextView xuanzeleixing;

    public class MyAdapter extends BaseAdapter {
        public int getCount() {
            return SpinerPopWindow.this.list.size();
        }

        public Object getItem(int position) {
            return SpinerPopWindow.this.list.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = SpinerPopWindow.this.inflater.inflate(R.layout.spiner_item_layout, null);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(getItem(position).toString());
            return convertView;
        }
    }

    class RootListener implements OnTouchListener {
        RootListener() {
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            int height = SpinerPopWindow.this.linearLayout.getTop();
            int bottom = SpinerPopWindow.this.linearLayout.getBottom();
            int y = (int) motionEvent.getY();
            if (motionEvent.getAction() == 1) {
                if (y < height) {
                    SpinerPopWindow.this.dismiss();
                }
                if (y > bottom) {
                    SpinerPopWindow.this.dismiss();
                }
            }
            return true;
        }
    }

    private class ViewHolder {
        private TextView tvName;

        private ViewHolder() {
        }
    }

    public SpinerPopWindow(Activity context, List<T> list, OnItemClickListener clickListener) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        init(clickListener);
    }

    public SpinerPopWindow(Activity context, List<T> list, OnItemClickListener clickListener, int m) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        initmatch(clickListener);
    }

    public SpinerPopWindow(Activity context, List<T> list, OnItemClickListener clickListener, String ni) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        this.niString = ni;
        initlist(clickListener);
    }

    public SpinerPopWindow(Activity context, List<T> list, OnItemClickListener clickListener, OnClickListener mtextClicklistener, RelativeLayout selectclass) {
        super(context);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        init_selfadapt(clickListener, mtextClicklistener, selectclass);
    }

    private void initlist(OnItemClickListener clickListener) {
        View view = this.inflater.inflate(R.layout.newspinner, null);
        setContentView(view);
        setWidth(-1);
        setHeight(-1);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(-1342177280));
        view.setOnTouchListener(new RootListener());
        this.linearLayout = (LinearLayout) view.findViewById(R.id.nicaicai);
        this.xuanzeleixing = (TextView) view.findViewById(R.id.xuanzeleixing);
        if (this.niString.equals("5")) {
            this.xuanzeleixing.setText("请选择班级");
        }
        this.mAdapter = new MyAdapter();
        this.mListView = (ListView) view.findViewById(R.id.listview);
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setOnItemClickListener(clickListener);
    }

    public void settittle(String name) {
        if (this.xuanzeleixing != null) {
            this.xuanzeleixing.setText(name);
        }
    }

    private void init_selfadapt(OnItemClickListener clickListener, OnClickListener mtextClicklistener, View mAnchorView) {
        View view = this.inflater.inflate(R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(-1);
        setHeight(-1);
        if (mAnchorView != null) {
            LinearLayout mLayout = (LinearLayout) view.findViewById(R.id.nicaicai);
            LayoutParams lp = (LayoutParams) mLayout.getLayoutParams();
            lp.setMargins(36, mAnchorView.getTop(), 36, 10);
            mLayout.setLayoutParams(lp);
        }
        setOutsideTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(-1342177280));
        this.linearLayout = (LinearLayout) view.findViewById(R.id.nicaicai);
        view.setOnTouchListener(new RootListener());
        this.mListView = (ListView) view.findViewById(R.id.listview);
        this.mAdapter = new MyAdapter();
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setOnItemClickListener(clickListener);
        this.mtextview = (TextView) view.findViewById(R.id.selectclass_pop_all);
        this.mtextview.setOnClickListener(mtextClicklistener);
        this.mtextview.setText("全部班级(" + (this.mListView.getCount() - 1) + ")");
        setListViewHeightBasedOnChildren(this.mListView);
    }

    private void init(OnItemClickListener clickListener) {
        View view = this.inflater.inflate(R.layout.spiner_window_layout, null);
        setContentView(view);
        setWidth(-1);
        setHeight(-2);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        this.mAdapter = new MyAdapter();
        this.mListView = (ListView) view.findViewById(R.id.listview);
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setOnItemClickListener(clickListener);
        setListViewHeightBasedOnChildren(this.mListView);
    }

    private void initmatch(OnItemClickListener clickListener) {
        View view = this.inflater.inflate(R.layout.spiner_window_match, null);
        setContentView(view);
        setWidth(-1);
        setHeight(-2);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0));
        this.mAdapter = new MyAdapter();
        this.mListView = (ListView) view.findViewById(R.id.listview);
        this.mListView.setAdapter(this.mAdapter);
        this.mListView.setOnItemClickListener(clickListener);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        MyAdapter listAdapter = (MyAdapter) listView.getAdapter();
        if (listAdapter != null) {
            int totalHeight;
            if (listAdapter.getCount() < 4) {
                totalHeight = getItemHeight(listView);
            } else {
                View listItem = listAdapter.getView(0, null, listView);
                listItem.measure(0, 0);
                totalHeight = listItem.getMeasuredHeight() * 4;
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight;
            listView.setLayoutParams(params);
        }
    }

    private int getItemHeight(ListView listView) {
        int totalHeight = 0;
        MyAdapter listAdapter = (MyAdapter) listView.getAdapter();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        return totalHeight;
    }

    public void setDataString(String className2) {
        this.mtextview.setText(className2);
    }
}
