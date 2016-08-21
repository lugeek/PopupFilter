package com.lugeek.popupfilter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lujiaming on 16/8/21.
 */

public class FilterBarPopupWindow extends PopupWindow {
    private static final int ANIMATE_DURATION = 200;
    private Context mContext;
    private RecyclerView mRvContent;
    private List<FilterValueModel> mData = new ArrayList<>();
    private ContentAdapter mAdapter;
    private FrameLayout backContainer;
    private View backView;
    private ValueClickListener mClickListener;
    private View mAnchorView;
    private boolean isSwitch = false;

    public FilterBarPopupWindow(Context context, View anchorView) {
        mContext = context;
        mAnchorView = anchorView;
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_filter_bar_popup_window,
                null, false);
        mRvContent = (RecyclerView) view.findViewById(R.id.recyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
        mRvContent.setLayoutManager(layoutManager);
        mAdapter = new ContentAdapter(mData);
        mRvContent.setAdapter(mAdapter);
        setContentView(view);
        setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        setFocusable(false);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setAnimationStyle(R.style.SearchWindowAnimation);
        backContainer = new FrameLayout(mContext);
        backView = new View(mContext);
        backView.setBackgroundColor(Color.parseColor("#669f9f9f"));
    }

    public interface ValueClickListener {
        void onClick(String value);
    }

    public void setClickListener(ValueClickListener listener) {
        this.mClickListener = listener;
    }

    public void show(List<FilterValueModel> data) {
        if (data != null) {
            update(data);
        }
        if (!isShowing()) {
            showAsDropDown(mAnchorView);
        }
    }

    public void switchShow(final List<FilterValueModel> data) {
        isSwitch = true;
        close();
        if (!isShowing()) {
            showAsDropDown(mAnchorView);
        }
        if (data != null) {
            update(data);
        }

    }





    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        if (!isSwitch) {
            addDimBackground(anchor);
        }
        isSwitch = false;
        super.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    @Override
    public void dismiss() {
        if (!isSwitch) {
            removeDimBackground();
        }
        super.dismiss();
    }

    public void close() {
        if (this.isShowing()) {
            dismiss();
        }
    }

    public void update(List<FilterValueModel> data) {
        mAdapter.update(data);
    }

    public void addDimBackground(View anchor) {
        WindowManager.LayoutParams p = new WindowManager.LayoutParams();
        p.gravity = Gravity.START | Gravity.TOP;
        p.token = anchor.getWindowToken();
        p.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
        p.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        p.format = PixelFormat.TRANSPARENT;
        int[] xy = new int[2];
        anchor.getLocationInWindow(xy);
        Rect rect = new Rect();
        anchor.getWindowVisibleDisplayFrame(rect);
        p.height = rect.bottom - xy[1] - anchor.getHeight();
        p.width = WindowManager.LayoutParams.MATCH_PARENT;
        p.x = xy[0];
        p.y = xy[1] + anchor.getHeight();
        p.packageName = mContext.getPackageName();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (backContainer.getWindowToken() == null) {
            wm.addView(backContainer, p);
            backContainer.addView(backView);
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in);
            animation.setDuration(ANIMATE_DURATION);
            backView.startAnimation(animation);
        }
    }

    public void removeDimBackground() {
        if (backContainer != null) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.fade_out);
            animation.setDuration(ANIMATE_DURATION);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    backContainer.removeAllViews();
                    WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                    wm.removeView(backContainer);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            backView.startAnimation(animation);
        }
    }


    class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.MyViewHolder> {

        private List<FilterValueModel> mData;

        public ContentAdapter(List<FilterValueModel> data) {
            this.mData = data;
        }

        public void update(List<FilterValueModel> data) {
            mData.clear();
            mData.addAll(data);
            this.notifyDataSetChanged();
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_filter_bar_popup_item_view, parent, false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            if (mData.get(position).mIsSelected) {
                holder.mTvName.setText(mData.get(position).mValueName);
                holder.mTvName.setTextColor(Color.RED);
            } else {
                holder.mTvName.setText(mData.get(position).mValueName);
                holder.mTvName.setTextColor(Color.BLACK);
            }
            holder.mTvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClickListener.onClick(mData.get(position).mValueName);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView mTvName;
            public MyViewHolder(View itemView) {
                super(itemView);
                mTvName = (TextView)itemView;
            }
        }
    }
}
