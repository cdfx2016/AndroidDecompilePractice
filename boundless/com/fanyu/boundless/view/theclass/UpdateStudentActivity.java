package com.fanyu.boundless.view.theclass;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.GetAllClassZuListApi;
import com.fanyu.boundless.bean.home.GetTeacherClassApi;
import com.fanyu.boundless.bean.theclass.ChildItem;
import com.fanyu.boundless.bean.theclass.NewStudentListApi;
import com.fanyu.boundless.bean.theclass.StudentsModel;
import com.fanyu.boundless.bean.theclass.classzuentity;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.common.adapter.ViewHolder;
import com.fanyu.boundless.common.listener.OnItemClickListener;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.theclass.UpdateStudentPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.widget.MyExpandableListView;
import com.fanyu.boundless.widget.recyclerview.FullyGridLayoutManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateStudentActivity extends BaseActivity<UpdateStudentPresenter> implements IUpdateStudentView {
    private List<ChildItem> allclasslist = new ArrayList();
    private List<ChildItem> allzulist = new ArrayList();
    private int biaoji = 0;
    private Map<Integer, List<ChildItem>> childData;
    List<ChildItem> childItems;
    private ZuAdapter classAdapter;
    private List<schoolclassentity> dataList = new ArrayList();
    @Bind({2131624246})
    MyExpandableListView expandlist;
    private List<String> groupData;
    @Bind({2131624165})
    RecyclerView gvGridPickerView;
    @Bind({2131624066})
    ImageView imgReturn;
    private Map<Integer, Boolean> mSelectMap = new HashMap();
    @Bind({2131624067})
    TextView messageTitle;
    private Map<Integer, List> morelist = new HashMap();
    private ClassBaseExpendBaseAdapter myAdapter;
    @Bind({2131624071})
    TextView queding;
    private List<ChildItem> selcetzulist = new ArrayList();
    private Map<Integer, List<ChildItem>> selectchildMap = new HashMap();
    private List<ChildItem> selectclasslist = new ArrayList();
    SharedPreferencesUtil sharedPreferencesUtil;
    private List<StudentsModel> slist = new ArrayList();
    private List<StudentsModel> stuList;
    private String userid;
    private int zongshu;
    private ZuAdapter zuAdapter;
    @Bind({2131624168})
    RecyclerView zuGridPickerView;
    private List<classzuentity> zuList = new ArrayList();
    private Map<Integer, Boolean> zuSelectMap = new HashMap();

    public class ClassBaseExpendBaseAdapter extends BaseExpandableListAdapter {
        private Map<Integer, List<ChildItem>> childMap;
        private Button groupButton;
        private List<String> groupTitle;
        private Context mContext;
        private Map<Integer, Boolean> newSelectMap;
        ZuAdapter sAdapter;

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

        public ClassBaseExpendBaseAdapter(Context context, List<String> groupTitle, Map<Integer, List<ChildItem>> childMap) {
            this.mContext = context;
            this.groupTitle = groupTitle;
            this.childMap = childMap;
            UpdateStudentActivity.this.selectchildMap = childMap;
        }

        public Object getChild(int groupPosition, int childPosition) {
            return ((ChildItem) ((List) this.childMap.get(Integer.valueOf(groupPosition))).get(childPosition)).getTitle();
        }

        public long getChildId(int groupPosition, int childPosition) {
            return (long) childPosition;
        }

        public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.childitem, null);
            ChildHolder childHolder = new ChildHolder();
            childHolder.myGridView = (RecyclerView) convertView.findViewById(R.id.gvGridPickerView);
            List<ChildItem> m = (List) this.childMap.get(Integer.valueOf(groupPosition));
            childHolder.myGridView.setLayoutManager(new FullyGridLayoutManager(this.mContext, 3));
            final StudentAdapter adapter = new StudentAdapter(this.mContext, R.layout.grid_item, m);
            childHolder.myGridView.setAdapter(adapter);
            adapter.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(ViewHolder arg0, int position) {
                    if (((ChildItem) ((List) ClassBaseExpendBaseAdapter.this.childMap.get(Integer.valueOf(groupPosition))).get(position)).isIscheck()) {
                        ((ChildItem) ((List) ClassBaseExpendBaseAdapter.this.childMap.get(Integer.valueOf(groupPosition))).get(position)).setIscheck(false);
                        ((ChildItem) ((List) UpdateStudentActivity.this.selectchildMap.get(Integer.valueOf(groupPosition))).get(position)).setIscheck(false);
                    } else {
                        ((ChildItem) ((List) ClassBaseExpendBaseAdapter.this.childMap.get(Integer.valueOf(groupPosition))).get(position)).setIscheck(true);
                        ((ChildItem) ((List) UpdateStudentActivity.this.selectchildMap.get(Integer.valueOf(groupPosition))).get(position)).setIscheck(true);
                    }
                    adapter.notifyDataSetChanged();
                }
            });
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
    }

    class ClassListener implements OnItemClickListener {
        ClassListener() {
        }

        public void onItemClick(ViewHolder arg0, int position) {
            if (UpdateStudentActivity.this.mSelectMap.get(Integer.valueOf(position)) != null && ((Boolean) UpdateStudentActivity.this.mSelectMap.get(Integer.valueOf(position))).booleanValue()) {
                UpdateStudentActivity.this.mSelectMap.put(Integer.valueOf(position), Boolean.valueOf(false));
                ((ChildItem) UpdateStudentActivity.this.allclasslist.get(position)).setIscheck(false);
            } else {
                UpdateStudentActivity.this.mSelectMap.put(Integer.valueOf(position), Boolean.valueOf(true));
                ((ChildItem) UpdateStudentActivity.this.allclasslist.get(position)).setIscheck(true);
            }
            UpdateStudentActivity.this.classAdapter.notifyDataSetChanged();
        }
    }

    class ZuListener implements OnItemClickListener {
        ZuListener() {
        }

        public void onItemClick(ViewHolder arg0, int position) {
            if (UpdateStudentActivity.this.zuSelectMap.get(Integer.valueOf(position)) != null && ((Boolean) UpdateStudentActivity.this.zuSelectMap.get(Integer.valueOf(position))).booleanValue()) {
                UpdateStudentActivity.this.zuSelectMap.put(Integer.valueOf(position), Boolean.valueOf(false));
                ((ChildItem) UpdateStudentActivity.this.allzulist.get(position)).setIscheck(false);
            } else {
                UpdateStudentActivity.this.zuSelectMap.put(Integer.valueOf(position), Boolean.valueOf(true));
                ((ChildItem) UpdateStudentActivity.this.allzulist.get(position)).setIscheck(true);
            }
            UpdateStudentActivity.this.zuAdapter.notifyDataSetChanged();
        }
    }

    protected void initView() {
        setContentView((int) R.layout.activity_update_student);
    }

    protected void initPresenter() {
        this.mPresenter = new UpdateStudentPresenter(this.mContext, this);
    }

    protected void init() {
        this.sharedPreferencesUtil = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.sharedPreferencesUtil.getString(Preferences.USER_ID, "");
        selectClass();
        selectZu();
    }

    public void selectClass() {
        GetTeacherClassApi getTeacherClassApi = new GetTeacherClassApi();
        getTeacherClassApi.setUserid(this.userid);
        getTeacherClassApi.setPage("1");
        getTeacherClassApi.setPagesize("20");
        ((UpdateStudentPresenter) this.mPresenter).startPost(this, getTeacherClassApi);
        this.gvGridPickerView.setLayoutManager(new FullyGridLayoutManager(this.mContext, 3));
        this.classAdapter = new ZuAdapter(this.mContext, R.layout.new_grid_item, this.allclasslist);
        this.gvGridPickerView.setAdapter(this.classAdapter);
        this.classAdapter.setOnItemClickListener(new ClassListener());
    }

    public void getClassList(List<schoolclassentity> classlist) {
        this.dataList.clear();
        this.dataList.addAll(classlist);
        for (int i = 0; i < this.dataList.size(); i++) {
            this.mSelectMap.put(Integer.valueOf(i), Boolean.valueOf(false));
            if (this.selectclasslist.size() > 0) {
                for (int j = 0; j < this.selectclasslist.size(); j++) {
                    if (((ChildItem) this.selectclasslist.get(j)).getId().equals(((schoolclassentity) this.dataList.get(i)).getId())) {
                        this.mSelectMap.put(Integer.valueOf(i), Boolean.valueOf(true));
                    }
                }
            }
            this.allclasslist.add(new ChildItem(((schoolclassentity) this.dataList.get(i)).getClassname(), ((schoolclassentity) this.dataList.get(i)).getId(), ((Boolean) this.mSelectMap.get(Integer.valueOf(i))).booleanValue()));
            getChildList(((schoolclassentity) this.dataList.get(i)).getId());
        }
        this.zongshu = this.dataList.size();
        this.classAdapter.notifyDataSetChanged();
    }

    public void getChildList(String myclassid) {
        NewStudentListApi studentListApi = new NewStudentListApi();
        studentListApi.setClassid(myclassid);
        ((UpdateStudentPresenter) this.mPresenter).startPost(this, studentListApi);
    }

    public void getZuList(List<classzuentity> zulist) {
        this.zuList.clear();
        this.zuList.addAll(zulist);
        for (int i = 0; i < this.zuList.size(); i++) {
            this.zuSelectMap.put(Integer.valueOf(i), Boolean.valueOf(false));
            if (this.selcetzulist.size() > 0) {
                for (int j = 0; j < this.selcetzulist.size(); j++) {
                    if (((ChildItem) this.selcetzulist.get(j)).getId().equals(((classzuentity) this.zuList.get(i)).getId())) {
                        this.zuSelectMap.put(Integer.valueOf(i), Boolean.valueOf(true));
                    }
                }
            }
            this.allzulist.add(new ChildItem(((classzuentity) zulist.get(i)).getZuname(), ((classzuentity) zulist.get(i)).getId(), ((Boolean) this.zuSelectMap.get(Integer.valueOf(i))).booleanValue()));
        }
        this.zuAdapter.notifyDataSetChanged();
    }

    public void getClassStuList(List<StudentsModel> stulist) {
        this.stuList = new ArrayList();
        this.stuList.clear();
        this.stuList.addAll(stulist);
        this.morelist.put(Integer.valueOf(this.biaoji), this.stuList);
        this.biaoji++;
        if (this.zongshu == this.biaoji) {
            initDatas();
        }
    }

    @OnClick({2131624066, 2131624071})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.queding:
                int i;
                List<ChildItem> banjiList = new ArrayList();
                List<ChildItem> xiaozuList = new ArrayList();
                List<ChildItem> gerenList = new ArrayList();
                if (this.mSelectMap.size() > 0) {
                    for (i = 0; i < this.mSelectMap.size(); i++) {
                        if (((Boolean) this.mSelectMap.get(Integer.valueOf(i))).booleanValue()) {
                            banjiList.add(new ChildItem(((schoolclassentity) this.dataList.get(i)).getClassname(), ((schoolclassentity) this.dataList.get(i)).getId(), false));
                        }
                    }
                }
                if (this.zuSelectMap.size() > 0) {
                    for (i = 0; i < this.zuSelectMap.size(); i++) {
                        if (((Boolean) this.zuSelectMap.get(Integer.valueOf(i))).booleanValue()) {
                            xiaozuList.add(new ChildItem(((classzuentity) this.zuList.get(i)).getZuname(), ((classzuentity) this.zuList.get(i)).getId(), false));
                        }
                    }
                }
                if (this.selectchildMap.size() > 0) {
                    for (i = 0; i < this.selectchildMap.size(); i++) {
                        List<ChildItem> chaiList = new ArrayList();
                        chaiList = (List) this.selectchildMap.get(Integer.valueOf(i));
                        for (int j = 0; j < chaiList.size(); j++) {
                            if (((ChildItem) chaiList.get(j)).isIscheck()) {
                                gerenList.add(new ChildItem(((ChildItem) chaiList.get(j)).getTitle(), ((ChildItem) chaiList.get(j)).getId(), false, ((ChildItem) chaiList.get(j)).getStudentnumber()));
                            }
                        }
                    }
                }
                Intent intent = new Intent(this, PublishJobActivity.class);
                intent.putExtra("banjilist", (Serializable) banjiList);
                intent.putExtra("xiaozulist", (Serializable) xiaozuList);
                intent.putExtra("gerenlist", (Serializable) gerenList);
                setResult(8, intent);
                finish();
                return;
            default:
                return;
        }
    }

    public void selectZu() {
        GetAllClassZuListApi getAllClassZuListApi = new GetAllClassZuListApi();
        getAllClassZuListApi.setUserid(this.userid);
        ((UpdateStudentPresenter) this.mPresenter).startPost(this, getAllClassZuListApi);
        this.zuGridPickerView.setLayoutManager(new FullyGridLayoutManager(this.mContext, 3));
        this.zuAdapter = new ZuAdapter(this.mContext, R.layout.new_grid_item, this.allzulist);
        this.zuGridPickerView.setAdapter(this.zuAdapter);
        this.zuAdapter.setOnItemClickListener(new ZuListener());
    }

    private void initDatas() {
        this.groupData = new ArrayList();
        this.childData = new HashMap();
        if (this.morelist.size() > 0) {
            int b = 0;
            for (int i = 0; i < this.morelist.size(); i++) {
                this.childItems = new ArrayList();
                this.slist = (List) this.morelist.get(Integer.valueOf(i));
                if (this.slist.size() > 0) {
                    this.groupData.add(((StudentsModel) this.slist.get(0)).getClassname());
                    for (StudentsModel ss : this.slist) {
                        this.childItems.add(new ChildItem(ss.getSnickname(), ss.getUserid(), false, ss.getStudentnumber()));
                    }
                    this.childData.put(Integer.valueOf(b), this.childItems);
                    b++;
                }
            }
        }
        this.myAdapter = new ClassBaseExpendBaseAdapter(this, this.groupData, this.childData);
        this.expandlist.setGroupIndicator(null);
        this.expandlist.setAdapter(this.myAdapter);
        this.myAdapter.notifyDataSetChanged();
    }
}
