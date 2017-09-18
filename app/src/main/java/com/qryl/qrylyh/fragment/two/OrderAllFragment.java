package com.qryl.qrylyh.fragment.two;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

/**
 * Created by hp on 2017/9/12.
 */

public class OrderAllFragment extends BaseFragment {

    @Override
    public void loadData() {

    }

    @Override
    public View initView() {
        TextView textView = new TextView(mContext);
        textView.setText("全部");
        textView.setTextColor(Color.BLACK);
        return textView;
    }
}
