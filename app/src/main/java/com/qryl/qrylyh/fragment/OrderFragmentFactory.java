package com.qryl.qrylyh.fragment;


import com.qryl.qrylyh.fragment.two.OrderFinishedFragment;
import com.qryl.qrylyh.fragment.two.OrderMakeListFragment;
import com.qryl.qrylyh.fragment.two.OrderNoPayFragment;
import com.qryl.qrylyh.fragment.two.OrderUnderwayFragment;

import java.util.HashMap;

/**
 * Created by hp on 2017/8/21.
 * tab标签的切换点单派单工厂类----优化代码
 */

public class OrderFragmentFactory {

    private static HashMap<Integer, BaseFragment> mBaseFragments = new HashMap<Integer, BaseFragment>();


    public static BaseFragment createFragment(int pos) {

        BaseFragment baseFragment = mBaseFragments.get(pos);

        if (baseFragment == null) {
            switch (pos) {
                case 0:
                    baseFragment = new OrderMakeListFragment();//全部订单
                    break;
                case 1:
                    baseFragment = new OrderNoPayFragment();//未付款订单
                    break;
                case 2:
                    baseFragment = new OrderUnderwayFragment();//进行中
                    break;
                case 3:
                    baseFragment = new OrderFinishedFragment();//已结束
                    break;
            }

            mBaseFragments.put(pos, baseFragment);
        }
        return baseFragment;
    }
}
