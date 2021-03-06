package com.qryl.qrylyh.fragment;

import android.annotation.SuppressLint;

import com.qryl.qrylyh.fragment.two.HgOnTheJobFragment;
import com.qryl.qrylyh.fragment.two.HgRegisterFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yinhao on 2017/9/24.
 */

class HgFragmentFactory {
    @SuppressLint("UseSparseArrays")
    private static Map<Integer, BaseFragment> mBaseFragments = new HashMap<Integer, BaseFragment>();

    static BaseFragment createFragment(int pos) {

        BaseFragment baseFragment = mBaseFragments.get(pos);

        if (baseFragment == null) {
            switch (pos) {
                case 0:
                    baseFragment = new HgRegisterFragment();//登记
                    break;
                case 1:
                    baseFragment = new HgOnTheJobFragment();//上岗
                    break;
            }

            mBaseFragments.put(pos, baseFragment);
        }
        return baseFragment;
    }
}
