package com.qryl.qrylyh.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.util.UIUtils;


/**
 * Created by hp on 2017/8/16.
 */

public class MsgFragment extends android.support.v4.app.Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = UIUtils.inflate(R.layout.fragment_msg);
        return view;
    }
}
