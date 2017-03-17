package com.fanyu.boundless.view.theclass;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.ChildItem;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import java.util.List;
import java.util.Map;

public class MyBaseExpandableListAdapter extends BaseExpandableListAdapter implements OnClickListener {
    private Map<Integer, List<ChildItem>> childMap;
    private Button groupButton;
    private List<String> groupTitle;
    private Context mContext;

    private class ChildHolder {
        RecyclerView myGridView;

        private ChildHolder() {
        }
    }

    private class GroupHolder {
        ImageView groupImg;
        TextView groupText;

        private GroupHolder() {
        }
    }

    class StudentListener implements OnItemClickListener {
        StudentListener() {
        }

        public void onItemClick(ViewHolder arg0, int position) {
        }
    }

    public MyBaseExpandableListAdapter(Context context, List<String> groupTitle, Map<Integer, List<ChildItem>> childMap) {
        this.mContext = context;
        this.groupTitle = groupTitle;
        this.childMap = childMap;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return ((ChildItem) ((List) this.childMap.get(Integer.valueOf(groupPosition))).get(childPosition)).getTitle();
    }

    public long getChildId(int groupPosition, int childPosition) {
        return (long) childPosition;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(this.mContext).inflate(R.layout.childitem, null);
        ChildHolder childHolder = new ChildHolder();
        childHolder.myGridView = (RecyclerView) convertView.findViewById(R.id.gvGridPickerView);
        List<ChildItem> m = (List) this.childMap.get(Integer.valueOf(groupPosition));
        childHolder.myGridView.setLayoutManager(new FullyGridLayoutManager(this.mContext, 3));
        StudentAdapter adapter = new StudentAdapter(this.mContext, R.layout.grid_item, m);
        childHolder.myGridView.setAdapter(adapter);
        adapter.setOnItemClickListener(new StudentListener());
        adapter.notifyDataSetChanged();
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    public Object getGroup(int groupPosition) {
        return this.groupTitle.get(groupPosition);
    }

    public int getGroupCount() {
        return this.groupTitle.size();
    }

    public long getGroupId(int groupPosition) {
        return (long) groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupHolder groupHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.groupitem, null);
            groupHolder = new GroupHolder();
            groupHolder.groupImg = (ImageView) convertView.findViewById(R.id.img_indicator);
            groupHolder.groupText = (TextView) convertView.findViewById(R.id.tv_group_text);
            convertView.setTag(groupHolder);
        } else {
            groupHolder = (GroupHolder) convertView.getTag();
        }
        if (isExpanded) {
            groupHolder.groupImg.setBackgroundResource(R.mipmap.sanjiao_zhan);
        } else {
            groupHolder.groupImg.setBackgroundResource(R.mipmap.sanjiao_he);
        }
        if (StringUtils.isEmpty((String) this.groupTitle.get(groupPosition))) {
            groupHolder.groupText.setText((CharSequence) this.groupTitle.get(groupPosition));
        }
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void onClick(View v) {
        v.getId();
    }
}
