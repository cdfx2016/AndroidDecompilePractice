package com.fanyu.boundless.view.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.home.AddGetLeaveNoticeApi;
import com.fanyu.boundless.bean.home.GetSchoolEntity;
import com.fanyu.boundless.bean.home.GetSchoolEntityApi;
import com.fanyu.boundless.bean.home.GetTeacherClassApi;
import com.fanyu.boundless.bean.home.SchoolClassEntityApi;
import com.fanyu.boundless.bean.home.UpdateUnreadMessageApi;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.config.Preferences;
import com.fanyu.boundless.presenter.home.GetSchoolPresenter;
import com.fanyu.boundless.util.SharedPreferencesUtil;
import com.fanyu.boundless.util.StringUtils;
import com.fanyu.boundless.view.base.BaseActivity;
import com.fanyu.boundless.view.myself.event.ArriveOrLeaveEvent;
import com.fanyu.boundless.view.myself.event.UpdateAddEvent;
import com.fanyu.boundless.view.myself.event.UpdateMainMessageEvent;
import com.fanyu.boundless.widget.SpinerPopWindow;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView;
import com.fanyu.boundless.widget.recyclerview.PullLoadMoreRecyclerView.PullLoadMoreListener;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.zhy.adapter.recyclerview.wrapper.HeaderAndFooterWrapper;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArriveOrLeaveSchoolActivity extends BaseActivity<GetSchoolPresenter> implements IGetSchoolView, PullLoadMoreListener {
    private OnClickListener _poptextClickListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.selectclass_pop_all:
                    ArriveOrLeaveSchoolActivity.this.mSpinerPopWindow5.dismiss();
                    return;
                default:
                    return;
            }
        }
    };
    private RecyclerArrayAdapter<GetSchoolEntity> adapter;
    private AddGetLeaveNoticeApi addGetLeaveNoticeApi;
    @Bind({2131624089})
    LinearLayout addget;
    private ArriveOrLeaveSchoolNewAdapter arriveAdapter;
    private String biaojiString;
    private List<schoolclassentity> classList = new ArrayList();
    private String className;
    private String classid;
    @Bind({2131624085})
    TextView classname;
    private List<GetSchoolEntity> datalist = new ArrayList();
    private List<schoolclassentity> fabuclassList = new ArrayList();
    private ProgressBar footProgressBar;
    private TextView footTxt;
    View footview = null;
    private GetSchoolEntity getSchoolEntity;
    private GetSchoolEntityApi getSchoolEntityApi;
    @Bind({2131624092})
    LinearLayout getnotice;
    HeaderAndFooterWrapper headerAndFooterWrapper;
    @Bind({2131624066})
    ImageView imgReturn;
    private OnItemClickListener itemClickListener5 = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            ArriveOrLeaveSchoolActivity.this.className = (String) ArriveOrLeaveSchoolActivity.this.list2.get(position);
            if (StringUtils.isEmpty(ArriveOrLeaveSchoolActivity.this.className)) {
                ArriveOrLeaveSchoolActivity.this.classname.setText(ArriveOrLeaveSchoolActivity.this.className);
            }
            ArriveOrLeaveSchoolActivity.this.mSpinerPopWindow5.dismiss();
            if (((String) ArriveOrLeaveSchoolActivity.this.list2.get(position)).contains("全部班级")) {
                ArriveOrLeaveSchoolActivity.this.selectclassid = "";
            } else {
                ArriveOrLeaveSchoolActivity.this.selectclassid = ((schoolclassentity) ArriveOrLeaveSchoolActivity.this.classList.get(position)).getId();
            }
            ArriveOrLeaveSchoolActivity.this.getSchoolEntityApi.setClassid(ArriveOrLeaveSchoolActivity.this.selectclassid);
            ArriveOrLeaveSchoolActivity.this.onRefresh();
        }
    };
    @Bind({2131624093})
    LinearLayout leavenotice;
    private List<String> list1 = new ArrayList();
    private List<String> list2 = new ArrayList();
    @Bind({2131624095})
    PullLoadMoreRecyclerView listview;
    private SpinerPopWindow<String> mSpinerPopWindow1;
    private SpinerPopWindow<String> mSpinerPopWindow5;
    @Bind({2131624067})
    TextView messageTitle;
    private List<GetSchoolEntity> morelist = new ArrayList();
    private SharedPreferencesUtil msharepreference;
    private LinearLayout mylayout;
    private int page = 1;
    private List<GetSchoolEntity> refreshlist = new ArrayList();
    @Bind({2131624094})
    RelativeLayout selectclass;
    private String selectclassid;
    private int sellectid = 0;
    private String userid;
    private String usertype;
    @Bind({2131624090})
    LinearLayout yijianget;
    @Bind({2131624091})
    LinearLayout yijianleave;

    protected void initView() {
        setContentView((int) R.layout.activity_arrive_or_leave_school);
    }

    protected void initPresenter() {
        this.mPresenter = new GetSchoolPresenter(this.mContext, this);
    }

    protected void init() {
        EventBus.getDefault().register(this);
        this.msharepreference = SharedPreferencesUtil.getsInstances(this.mContext);
        this.userid = this.msharepreference.getString(Preferences.USER_ID, "");
        this.usertype = this.msharepreference.getString(Preferences.USER_TYPE, "");
        if (this.usertype.equals("3")) {
            this.addget.setVisibility(0);
        }
        this.getSchoolEntityApi = new GetSchoolEntityApi();
        this.getSchoolEntityApi.setUserid(this.userid);
        this.getSchoolEntityApi.setPage(this.page + "");
        this.getSchoolEntityApi.setPagesize("8");
        this.getSchoolEntityApi.setClassid("");
        this.getSchoolEntityApi.setVersion("1.7");
        ((GetSchoolPresenter) this.mPresenter).startPost(this, this.getSchoolEntityApi, 0);
        selectClassList();
        this.addGetLeaveNoticeApi = new AddGetLeaveNoticeApi();
        UpdateUnreadMessageApi updateUnreadMessageApi = new UpdateUnreadMessageApi();
        updateUnreadMessageApi.setUserid(this.userid);
        updateUnreadMessageApi.setRtype("3");
        ((GetSchoolPresenter) this.mPresenter).startPost(this, updateUnreadMessageApi);
    }

    private void findfootView(View footview) {
        this.footProgressBar = (ProgressBar) footview.findViewById(R.id.loadMoreProgressBar);
        this.footTxt = (TextView) footview.findViewById(R.id.loadMoreText);
    }

    public void selectClassList() {
        SchoolClassEntityApi schoolClassEntityApi = new SchoolClassEntityApi();
        schoolClassEntityApi.setPage("1");
        schoolClassEntityApi.setPagesize("20");
        schoolClassEntityApi.setUserid(this.userid);
        ((GetSchoolPresenter) this.mPresenter).startPost(this, schoolClassEntityApi);
        GetTeacherClassApi getTeacherClassApi = new GetTeacherClassApi();
        getTeacherClassApi.setPage("1");
        getTeacherClassApi.setPagesize("20");
        getTeacherClassApi.setUserid(this.userid);
        ((GetSchoolPresenter) this.mPresenter).startPost(this, getTeacherClassApi);
    }

    @OnClick({2131624066, 2131624090, 2131624091, 2131624092, 2131624093, 2131624094})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_return:
                finish();
                return;
            case R.id.yijianget:
                if (this.list1.size() > 1) {
                    morepoupwindow(view);
                    return;
                } else if (this.fabuclassList.size() > 0) {
                    this.className = ((schoolclassentity) this.fabuclassList.get(0)).getClassname();
                    this.biaojiString = "'全体到校'";
                    showPopupWindowOne(view, "get");
                    return;
                } else {
                    return;
                }
            case R.id.yijianleave:
                if (this.list1.size() > 1) {
                    morepoupwindow(view);
                    return;
                } else if (this.fabuclassList.size() > 0) {
                    this.className = ((schoolclassentity) this.fabuclassList.get(0)).getClassname();
                    this.biaojiString = "'班级已放学'";
                    showPopupWindowOne(view, "leave");
                    return;
                } else {
                    return;
                }
            case R.id.getnotice:
                if (this.list1.size() > 1) {
                    morepoupwindow(view);
                    return;
                } else if (this.list1.size() == 1) {
                    Intent intent = new Intent(this, GetNoticeActivity.class);
                    intent.putExtra("classid", this.classid);
                    intent.putExtra("classname", this.className);
                    intent.putExtra("tittle", "getschoolentity");
                    intent.putExtra("gstype", "0");
                    startActivity(intent);
                    return;
                } else {
                    return;
                }
            case R.id.leavenotice:
                if (this.list1.size() > 1) {
                    morepoupwindow(view);
                    return;
                } else if (this.list1.size() == 1) {
                    Intent intent1 = new Intent(this, GetNoticeActivity.class);
                    intent1.putExtra("classid", this.classid);
                    intent1.putExtra("classname", this.className);
                    intent1.putExtra("tittle", "leaveschoole");
                    intent1.putExtra("gstype", "1");
                    startActivity(intent1);
                    return;
                } else {
                    return;
                }
            case R.id.selectclass:
                if (StringUtils.isEmpty(this.className)) {
                    this.mSpinerPopWindow5.setDataString(this.className);
                    this.mSpinerPopWindow5.showAtLocation(this.selectclass, 0, 0, 0);
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void morepoupwindow(final View v) {
        this.mSpinerPopWindow1 = new SpinerPopWindow((Activity) this, this.list1, new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ArriveOrLeaveSchoolActivity.this.sellectid = position;
                switch (v.getId()) {
                    case R.id.yijianget:
                        ArriveOrLeaveSchoolActivity.this.className = ((schoolclassentity) ArriveOrLeaveSchoolActivity.this.fabuclassList.get(ArriveOrLeaveSchoolActivity.this.sellectid)).getClassname();
                        ArriveOrLeaveSchoolActivity.this.biaojiString = "'全体到校'";
                        ArriveOrLeaveSchoolActivity.this.showPopupWindowOne(v, "get");
                        break;
                    case R.id.yijianleave:
                        ArriveOrLeaveSchoolActivity.this.className = ((schoolclassentity) ArriveOrLeaveSchoolActivity.this.fabuclassList.get(ArriveOrLeaveSchoolActivity.this.sellectid)).getClassname();
                        ArriveOrLeaveSchoolActivity.this.biaojiString = "'班级已放学'";
                        ArriveOrLeaveSchoolActivity.this.showPopupWindowOne(v, "leave");
                        break;
                    case R.id.getnotice:
                        ArriveOrLeaveSchoolActivity.this.className = ((schoolclassentity) ArriveOrLeaveSchoolActivity.this.fabuclassList.get(ArriveOrLeaveSchoolActivity.this.sellectid)).getClassname();
                        Intent intent = new Intent(ArriveOrLeaveSchoolActivity.this, GetNoticeActivity.class);
                        intent.putExtra("classid", ((schoolclassentity) ArriveOrLeaveSchoolActivity.this.fabuclassList.get(position)).getId());
                        intent.putExtra("tittle", "getschoolentity");
                        intent.putExtra("classname", ArriveOrLeaveSchoolActivity.this.className);
                        intent.putExtra("gstype", "0");
                        ArriveOrLeaveSchoolActivity.this.startActivity(intent);
                        break;
                    case R.id.leavenotice:
                        ArriveOrLeaveSchoolActivity.this.className = ((schoolclassentity) ArriveOrLeaveSchoolActivity.this.fabuclassList.get(ArriveOrLeaveSchoolActivity.this.sellectid)).getClassname();
                        Intent intent1 = new Intent(ArriveOrLeaveSchoolActivity.this, GetNoticeActivity.class);
                        intent1.putExtra("classid", ((schoolclassentity) ArriveOrLeaveSchoolActivity.this.fabuclassList.get(position)).getId());
                        intent1.putExtra("tittle", "leaveschoole");
                        intent1.putExtra("classname", ArriveOrLeaveSchoolActivity.this.className);
                        intent1.putExtra("gstype", "1");
                        ArriveOrLeaveSchoolActivity.this.startActivity(intent1);
                        break;
                }
                ArriveOrLeaveSchoolActivity.this.mSpinerPopWindow1.dismiss();
            }
        }, "5");
        this.mSpinerPopWindow1.showAtLocation(v, 17, 0, 0);
    }

    private void showPopupWindowOne(final View view, String type) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.showpoupwindowone, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, -1, -1, true);
        popupWindow.setTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(-1342177280));
        this.mylayout = (LinearLayout) contentView.findViewById(R.id.liebiao);
        contentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int height = ArriveOrLeaveSchoolActivity.this.mylayout.getTop();
                int bottom = ArriveOrLeaveSchoolActivity.this.mylayout.getBottom();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == 1) {
                    if (y < height) {
                        popupWindow.dismiss();
                    }
                    if (y > bottom) {
                        popupWindow.dismiss();
                    }
                }
                return true;
            }
        });
        popupWindow.showAtLocation(view, 17, 0, 0);
        TextView contentTextView = (TextView) contentView.findViewById(R.id.content);
        if (type.equals("get")) {
            contentTextView.setText(getString(R.string.banjidaoxiao));
        } else if (type.equals("leave")) {
            contentTextView.setText(getString(R.string.banjifangxue));
        }
        ((TextView) contentView.findViewById(R.id.quxiao)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        ((TextView) contentView.findViewById(R.id.queding)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                switch (view.getId()) {
                    case R.id.yijianget:
                        ArriveOrLeaveSchoolActivity.this.addGetLeave("一键到校", "0");
                        break;
                    case R.id.yijianleave:
                        ArriveOrLeaveSchoolActivity.this.addGetLeave("一键放学", "1");
                        break;
                }
                popupWindow.dismiss();
            }
        });
    }

    public void getIMyClassName(List<schoolclassentity> list) {
        this.classList.clear();
        this.classList.addAll(list);
        result();
    }

    public void getITeacherClassName(List<schoolclassentity> list) {
        this.fabuclassList.clear();
        this.fabuclassList.addAll(list);
        this.list1.clear();
        for (int i = 0; i < this.fabuclassList.size(); i++) {
            this.list1.add(((schoolclassentity) this.fabuclassList.get(i)).getClassname());
        }
        if (this.fabuclassList.size() == 1) {
            this.classid = ((schoolclassentity) this.fabuclassList.get(0)).getId();
            this.className = ((schoolclassentity) this.fabuclassList.get(0)).getClassname();
        }
    }

    public void result() {
        this.list2.clear();
        for (int i = 0; i < this.classList.size(); i++) {
            this.list2.add(((schoolclassentity) this.classList.get(i)).getClassname());
        }
        if (this.list2.size() > 0) {
            this.className = "全部班级(" + this.list2.size() + ")";
            this.list2.add(this.className);
            this.classname.setText(this.className);
        } else {
            this.className = "全部班级(0)";
            this.list2.add(this.className);
            this.classname.setText(this.className);
        }
        this.mSpinerPopWindow5 = new SpinerPopWindow(this, this.list2, this.itemClickListener5, this._poptextClickListener, this.selectclass);
    }

    public void getArriveOrLeaveList(List<GetSchoolEntity> getSchoolEntities, int state) {
        if (state == 0) {
            this.datalist.clear();
            this.datalist.addAll(getSchoolEntities);
            System.out.println("datalist ===== " + this.datalist);
            this.arriveAdapter = new ArriveOrLeaveSchoolNewAdapter(this.mContext, R.layout.adapter_getarriveorleavelist, this.datalist);
            this.footview = ((RxAppCompatActivity) this.mContext).getLayoutInflater().inflate(R.layout.footer_layout, null);
            findfootView(this.footview);
            this.headerAndFooterWrapper = new HeaderAndFooterWrapper(this.arriveAdapter);
            this.headerAndFooterWrapper.addFootView(this.footview);
            this.listview.setAdapter(this.headerAndFooterWrapper);
            this.listview.setOnPullLoadMoreListener(this);
            this.listview.setPullLoadMoreCompleted();
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (getSchoolEntities.size() > 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            showTip("暂无数据");
            this.footview.setVisibility(8);
        } else if (state == 1) {
            this.refreshlist.clear();
            this.refreshlist.addAll(getSchoolEntities);
            this.listview.setPullLoadMoreCompleted();
            this.datalist.clear();
            this.datalist.addAll(this.refreshlist);
            this.datalist.addAll(this.morelist);
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (getSchoolEntities.size() > 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            this.footview.setVisibility(8);
        } else if (state == 2) {
            this.morelist.clear();
            this.morelist.addAll(getSchoolEntities);
            this.listview.setPullLoadMoreCompleted();
            this.datalist.addAll(this.morelist);
            this.headerAndFooterWrapper.notifyDataSetChanged();
            if (getSchoolEntities.size() == 0) {
                this.footTxt.setText("(*￣ω￣) 没有更多了");
                this.footProgressBar.setVisibility(8);
                return;
            }
            this.footTxt.setText("正在加载");
        }
    }

    public void addGet(String isadd) {
        System.out.println("到校isadd ======== " + isadd);
        EventBus.getDefault().post(new ArriveOrLeaveEvent());
        EventBus.getDefault().post(new UpdateMainMessageEvent());
        onRefresh();
    }

    public void updateUnread(String result) {
        EventBus.getDefault().post(new UpdateMainMessageEvent());
    }

    public void onSuccess(List<GetSchoolEntity> result, int State) {
        if (State == 1) {
            this.adapter.clear();
            this.datalist.clear();
            this.adapter.addAll((Collection) result);
            this.datalist.addAll(result);
        } else if (State == 2) {
            this.adapter.addAll((Collection) result);
            this.datalist.addAll(result);
            closeLoadingDialog();
        }
    }

    public void onLoadMore() {
        Log.i(EasyRecyclerView.TAG, "onLoadMore");
        this.page++;
        this.getSchoolEntityApi.setPage(this.page + "");
        ((GetSchoolPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.getSchoolEntityApi, 2);
    }

    public void onRefresh() {
        this.page = 1;
        this.getSchoolEntityApi.setPage(this.page + "");
        ((GetSchoolPresenter) this.mPresenter).startPost((RxAppCompatActivity) this.mContext, this.getSchoolEntityApi, 1);
    }

    public void addGetLeave(String editcontont, String gstype) {
        if (this.list1.size() > 1) {
            this.classid = ((schoolclassentity) this.fabuclassList.get(this.sellectid)).getId();
        }
        SharedPreferencesUtil editor = SharedPreferencesUtil.getsInstances(this.mContext);
        editor.putString(Preferences.CLASS_NAME, this.className);
        editor.putString(Preferences.CLASS_ID, this.classid);
        this.addGetLeaveNoticeApi.setClassid(this.classid);
        this.addGetLeaveNoticeApi.setUserid(this.userid);
        this.addGetLeaveNoticeApi.setClassname(this.className);
        this.addGetLeaveNoticeApi.setEditcontent(editcontont);
        this.addGetLeaveNoticeApi.setGstype(gstype);
        this.addGetLeaveNoticeApi.setTittle("一键");
        this.addGetLeaveNoticeApi.setYijian("yijian");
        ((GetSchoolPresenter) this.mPresenter).startPost(this, this.addGetLeaveNoticeApi);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(UpdateAddEvent updateAddEvent) {
        onRefresh();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void helloEventBus(ArriveOrLeaveEvent arriveOrLeaveEvent) {
        onRefresh();
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
