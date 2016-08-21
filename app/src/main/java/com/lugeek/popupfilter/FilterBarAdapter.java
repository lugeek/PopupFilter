package com.lugeek.popupfilter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lujiaming on 16/8/21.
 */

public class FilterBarAdapter extends RecyclerView.Adapter<FilterBarAdapter.MyViewHolder> {

    private static final int NOT_CLICKED = -1;
    private Context mContext;
    private List<FilterModel> mData;
    private int mClickedPos = NOT_CLICKED;
    private FilterBarPopupWindow mPopupWindow;

    public FilterBarAdapter(Context context, List<FilterModel> data, final View anchorView) {
        this.mContext = context;
        this.mData = data;
        this.mPopupWindow = new FilterBarPopupWindow(mContext, anchorView);
        this.mPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int x = (int) event.getX();
                final int y = (int) event.getY();
                //拦截所有点击外部的事件,设置关闭.
                if (event.getAction() == MotionEvent.ACTION_DOWN && ((x < 0) || (x >= v.getWidth()) || (y < 0) || (y >= v.getHeight()))) {
                    if (y < 0 - anchorView.getHeight() || y >= v.getHeight()) {
                        invokePopup(NOT_CLICKED);
                    }
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    if (y < 0 - anchorView.getHeight() || y >= v.getHeight()) {
                        invokePopup(NOT_CLICKED);
                    }
                    return true;
                }

                return false;
            }
        });
        this.mPopupWindow.setClickListener(new FilterBarPopupWindow.ValueClickListener() {
            @Override
            public void onClick(String value) {
                mData.get(mClickedPos).mIsSelceted = true;
                mData.get(mClickedPos).mSelectedName = value;
                invokePopup(NOT_CLICKED);
            }
        });
    }

    public void invokePopup(int targetPos) {
        if (mClickedPos == NOT_CLICKED) {
            if (targetPos == NOT_CLICKED) return;
            mClickedPos = targetPos;
            notifyItemChanged(targetPos);
            mPopupWindow.show(mData.get(targetPos).mValues);
        } else {
            if (targetPos == NOT_CLICKED) {
                int temp = mClickedPos;
                mClickedPos = NOT_CLICKED;
                notifyItemChanged(temp);
                mPopupWindow.close();
            } else if (targetPos == mClickedPos) {
                mClickedPos = NOT_CLICKED;
                notifyItemChanged(targetPos);
                mPopupWindow.close();
            } else {
                int temp = mClickedPos;
                mClickedPos = targetPos;
                notifyItemChanged(temp);
                notifyItemChanged(targetPos);
                mPopupWindow.switchShow(mData.get(targetPos).mValues);
            }
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(mContext);
        RecyclerView.LayoutParams p = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        p.setMargins(18, 18, 18, 18);
        textView.setLayoutParams(p);
        textView.setPadding(12, 12, 12, 12);
        textView.setBackgroundColor(Color.GRAY);
        return new MyViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (mClickedPos == position) {//已打开
            if (mData.get(position).mIsSelceted) {//已打开,已选中
                holder.mTextView.setText(mData.get(position).mSelectedName);
            } else {//已打开,未选中
                holder.mTextView.setText(mData.get(position).mName);
            }
            holder.mTextView.setTextColor(Color.RED);
        } else {//未打开
            if (mData.get(position).mIsSelceted) {//未打开,已选中
                holder.mTextView.setText(mData.get(position).mSelectedName);
                holder.mTextView.setTextColor(Color.BLUE);
            } else {//未打开,未选中
                holder.mTextView.setText(mData.get(position).mName);
                holder.mTextView.setTextColor(Color.BLACK);
            }
        }

        holder.mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invokePopup(holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }
    }
}
