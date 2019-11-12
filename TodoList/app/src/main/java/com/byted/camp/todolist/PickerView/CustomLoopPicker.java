package com.byted.camp.todolist.PickerView;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.byted.camp.todolist.R;

import java.util.ArrayList;
import java.util.List;


public class CustomLoopPicker implements View.OnClickListener, PickerView.OnSelectListener {
    private Context mContext;
    private Callback mCallback;
    private String mBeginLoop, mEndLoop, mSelectedLoop;
    private boolean mCanDialogShow;

    private Dialog mPickerDialog;
    private PickerView mDpvLoop;

    private List<String> mLoopUnits = new ArrayList<>();

    /**
     * 状态选择结果回调接口
     */
    public interface Callback {
        void onLoopSelected(String loop);
    }

    /**
     * 通过状态字符串初始换时间选择器
     *
     * @param context      Activity Context
     * @param callback     选择结果回调
     */
    public CustomLoopPicker(Context context, Callback callback, String beginLoop) {
        if(context == null || callback == null){
            mCanDialogShow = false;
            return;
        }

        mContext = context;
        mCallback = callback;
        mBeginLoop = beginLoop;
        mEndLoop = "";
        mSelectedLoop = "";

        initView();
        initData();
        mCanDialogShow = true;
    }

    private void initView() {
        mPickerDialog = new Dialog(mContext, R.style.date_picker_dialog);
        mPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPickerDialog.setContentView(R.layout.dialog_loop_picker);

        Window window = mPickerDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.gravity = Gravity.BOTTOM;
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }

        mPickerDialog.findViewById(R.id.tv_cancel).setOnClickListener(this);
        mPickerDialog.findViewById(R.id.tv_confirm).setOnClickListener(this);
        mDpvLoop = mPickerDialog.findViewById(R.id.dpv_loop);
        mDpvLoop.setOnSelectListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                break;
            case R.id.tv_confirm:
                if (mCallback != null) {
                    mCallback.onLoopSelected(mSelectedLoop);
                    Log.d("mCallback",mSelectedLoop);
                }
                break;
        }

        if (mPickerDialog != null && mPickerDialog.isShowing()) {
            mPickerDialog.dismiss();
        }
    }

    @Override
    public void onSelect(View view, String selected) {
        if (view == null || TextUtils.isEmpty(selected)) return;

        Log.d("selected",selected);
        switch (view.getId()) {
            case R.id.dpv_loop:
                mSelectedLoop = selected;
                Log.d("selected",selected);
        }
    }

    private void initData() {
        mSelectedLoop=mBeginLoop;

        mBeginLoop = "每天";
        mEndLoop = "每年";

        boolean canSpanLoop = mBeginLoop != mEndLoop;
        if(canSpanLoop)
            initLoopUnits();
    }

    private void initLoopUnits(){
        mLoopUnits.add("每天");
        mLoopUnits.add("每周");
        mLoopUnits.add("每月");
        mDpvLoop.setDataList(mLoopUnits);
        mDpvLoop.setSelected(mLoopUnits.indexOf(mBeginLoop));

        setCanScroll();
    }

    private void setCanScroll() {
        mDpvLoop.setCanScroll(mLoopUnits.size() > 1);
    }

    /**
     * 展示时间选择器
     *
     * @param dateStr 日期字符串，格式为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm
     */
    public void show(String dateStr) {
        if (!canShow() || TextUtils.isEmpty(dateStr)) return;

        // 弹窗时，考虑用户体验，不展示滚动动画
        if (setSelectedLoop(dateStr, false)) {
            mPickerDialog.show();
        }
    }

    private boolean canShow() {
        return mCanDialogShow && mPickerDialog != null;
    }

    public boolean setSelectedLoop(String mLoop, boolean showAnim){
        if (!canShow()) return false;

        mSelectedLoop = mLoop;
        return true;
    }

    /**
     * 设置是否允许点击屏幕或物理返回键关闭
     */
    public void setCancelable(boolean cancelable) {
        if (!canShow()) return;

        mPickerDialog.setCancelable(cancelable);
    }

    /**
     * 设置状态控件是否可以循环滚动
     */
    public void setScrollLoop(boolean canLoop) {
        if (!canShow()) return;

        mDpvLoop.setCanScrollLoop(canLoop);
    }
    /**
     * 销毁弹窗
     */
    public void onDestroy() {
        if (mPickerDialog != null) {
            mPickerDialog.dismiss();
            mPickerDialog = null;

            mDpvLoop.onDestroy();
        }
    }
}
