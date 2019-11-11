package com.byted.camp.todolist.PickerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.byted.camp.todolist.ItemActivity;
import com.byted.camp.todolist.R;
import com.byted.camp.todolist.db.TodoDbHelper;

import java.util.ArrayList;
import java.util.List;

public class CustomFatherItemPicker implements View.OnClickListener, PickerView.OnSelectListener {
    private Context mContext;
    private Callback mCallback;
    private String mBeginFatherItem, mEndFatherItem, mSelectedFatherItem;
    private boolean mCanDialogShow;

    private Dialog mPickerDialog;
    private PickerView mDpvFatherItem;

    private List<String> mFatherItemUnits = new ArrayList<>();
    private List<String> mItemTitlesFromDatabase = new ArrayList<>();
    private ItemActivity mActivity;

    /**
     * 状态选择结果回调接口
     */
    public interface Callback {
        void onFatherItemPicker(String fatherItem);
    }

    /**
     * 通过状态字符串初始换时间选择器
     *
     * @param context      Activity Context
     * @param callback     选择结果回调
     */
    public CustomFatherItemPicker(Context context, Callback callback, ItemActivity activity) {
        if(context == null || callback == null){
            mCanDialogShow = false;
            return;
        }

        mActivity = activity;
        mActivity.sendCallback(new ItemActivity.databaseCallback() {
            @Override
            public void setOnDatabaseResult(List<String> itemTitlesFromDatabase) {
                Log.d("itemTitlesFromDatabase",itemTitlesFromDatabase.size()+"");
                mItemTitlesFromDatabase = itemTitlesFromDatabase;
                Log.d("mItemTitlesFromDatabase",mItemTitlesFromDatabase.size()+"");
            }
        });
        mContext = context;
        mCallback = callback;
        mBeginFatherItem = "";
        mEndFatherItem = "";
        mSelectedFatherItem = "";

        initView();
        initData();
        mCanDialogShow = true;
    }

    public void onResume(){
        initView();
        initData();
    }

    private void initView() {
        mPickerDialog = new Dialog(mContext, R.style.date_picker_dialog);
        mPickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPickerDialog.setContentView(R.layout.dialog_fatheritem_picker);

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
        mDpvFatherItem = mPickerDialog.findViewById(R.id.dpv_fatheritem);
        mDpvFatherItem.setOnSelectListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_cancel:
                break;
            case R.id.tv_confirm:
                if (mCallback != null) {
                    mCallback.onFatherItemPicker(mSelectedFatherItem);
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

        switch (view.getId()) {
            case R.id.dpv_fatheritem:
                mSelectedFatherItem = selected;
        }
    }

    private void initData() {
        mSelectedFatherItem=mBeginFatherItem;

        mBeginFatherItem = "None";
        mEndFatherItem = "";

        boolean canSpanState = mBeginFatherItem != mEndFatherItem;
        if(canSpanState)
            initStateUnits();
    }

    private void initStateUnits(){
        mFatherItemUnits.clear();
        mFatherItemUnits.add("None");
        if(mItemTitlesFromDatabase.size() == 0)
            ;
        else{
            for(String fatherItemName: mItemTitlesFromDatabase){
                mFatherItemUnits.add(fatherItemName);
            }
        }
        mDpvFatherItem.setDataList(mFatherItemUnits);
        mDpvFatherItem.setSelected(0);

        setCanScroll();
    }

    private void setCanScroll() {
        mDpvFatherItem.setCanScroll(mFatherItemUnits.size() > 1);
    }

    /**
     * 展示时间选择器
     *
     * @param dateStr 日期字符串，格式为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm
     */
    public void show(String dateStr) {
        if (!canShow() || TextUtils.isEmpty(dateStr)) return;

        // 弹窗时，考虑用户体验，不展示滚动动画
        if (setSelectedState(dateStr, false)) {
            mPickerDialog.show();
        }
    }

    private boolean canShow() {
        return mCanDialogShow && mPickerDialog != null;
    }

    public boolean setSelectedState(String mFatherItem, boolean showAnim){
        if (!canShow()) return false;

        mSelectedFatherItem = mFatherItem;
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

        mDpvFatherItem.setCanScrollLoop(canLoop);
    }
    /**
     * 销毁弹窗
     */
    public void onDestroy() {
        if (mPickerDialog != null) {
            mPickerDialog.dismiss();
            mPickerDialog = null;

            mDpvFatherItem.onDestroy();
        }
    }
}
