package com.lugeek.popupfilter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    TextView mTextView;
    private List<FilterModel> mData = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        initData();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(layoutManager);
        FilterBarAdapter adapter = new FilterBarAdapter(this, mData, mRecyclerView);
        mRecyclerView.setAdapter(adapter);
        mTextView = (TextView) findViewById(R.id.tv1);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupWindow(MainActivity.this, view);
            }
        });
    }

    private void initData() {
        for (int i = 0; i < 5; i++) {
            FilterModel fm = new FilterModel();
            fm.mName = "品牌" + i;
            fm.mValues = new ArrayList<>();
            for (int j = 0; j < 13; j++) {
                FilterValueModel fvm = new FilterValueModel();
                if (j == 0) {
                    fvm.mIsSelected = true;
                    fvm.mValueName = "全部";
                } else {
                    fvm.mIsSelected = false;
                    fvm.mValueName = "属性"+i+"-"+j;
                }
                fm.mValues.add(fvm);
            }
            mData.add(fm);
        }
    }

    public void showPopupWindow(Context context, View view) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        PopupWindow popupWindow = new PopupWindow(contentView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#77ff4965")));
        popupWindow.setAnimationStyle(R.style.SearchWindowAnimation);
        popupWindow.showAsDropDown(view);
    }
}
