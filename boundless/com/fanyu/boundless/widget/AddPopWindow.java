package com.fanyu.boundless.widget;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.fanyu.boundless.R;
import com.fanyu.boundless.bean.theclass.schoolclassentity;
import com.fanyu.boundless.view.myself.event.DeleteClassEvent;
import com.fanyu.boundless.view.theclass.DeleteStudentActivity;
import com.fanyu.boundless.view.theclass.UpdateClassActivity;
import com.fanyu.boundless.widget.Exsit.Builder;
import de.greenrobot.event.EventBus;

public class AddPopWindow extends PopupWindow {
    private View conentView;
    schoolclassentity entity;
    LinearLayout mylayout = ((LinearLayout) this.conentView.findViewById(R.id.pop_layout2));

    public AddPopWindow(final Activity context) {
        this.conentView = ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(R.layout.add_popup_dialog, null);
        setWidth(-1);
        setHeight(-1);
        setContentView(this.conentView);
        setFocusable(true);
        setOutsideTouchable(true);
        update();
        setBackgroundDrawable(new ColorDrawable(-1342177280));
        this.conentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int height = AddPopWindow.this.mylayout.getTop();
                int bottom = AddPopWindow.this.mylayout.getBottom();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == 1) {
                    if (y > height) {
                        AddPopWindow.this.dismiss();
                    }
                    if (y > bottom) {
                        AddPopWindow.this.dismiss();
                    }
                }
                return true;
            }
        });
        LinearLayout teamMemberLayout = (LinearLayout) this.conentView.findViewById(R.id.team_member_layout);
        LinearLayout deleteMemberLayout = (LinearLayout) this.conentView.findViewById(R.id.delete_member_layout);
        ((LinearLayout) this.conentView.findViewById(R.id.add_task_layout)).setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(context, UpdateClassActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("entity", AddPopWindow.this.entity);
                intent.putExtras(bundle);
                context.startActivity(intent);
                AddPopWindow.this.dismiss();
            }
        });
        deleteMemberLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, DeleteStudentActivity.class);
                intent.putExtra("classid", AddPopWindow.this.entity.getId());
                intent.putExtra("classname", AddPopWindow.this.entity.getClassname());
                context.startActivity(intent);
                AddPopWindow.this.dismiss();
            }
        });
        teamMemberLayout.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Builder alert = new Builder(context);
                alert.setTitle("解散班级？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EventBus.getDefault().post(new DeleteClassEvent());
                        dialog.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.create().show();
                AddPopWindow.this.dismiss();
            }
        });
    }

    public void showPopupWindow(View parent, schoolclassentity entity) {
        this.entity = entity;
        if (isShowing()) {
            dismiss();
        } else {
            showAsDropDown(parent, -200, 0);
        }
    }
}
