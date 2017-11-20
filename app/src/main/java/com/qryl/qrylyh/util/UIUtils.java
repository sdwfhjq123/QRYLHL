package com.qryl.qrylyh.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.qryl.qrylyh.global.QRYLHLApplication;

/**
 * Created by hp on 2017/9/14.
 */

public class UIUtils {

    public static Context getContext() {
        return QRYLHLApplication.getContext();
    }

    /**
     * 获取资源文件字符串
     *
     * @param id
     * @return
     */
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    /**
     * 加载资源文件字符串数组
     *
     * @param id
     * @return
     */
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    /**
     * 获取图片
     *
     * @param id
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id, null);
    }

    /**
     * 获取颜色
     *
     * @param id
     * @return
     */
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    /**
     * 根据id获取颜色的状态选择器
     *
     * @param id
     * @return
     */
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    /**
     * 获取尺寸
     *
     * @param id
     * @return
     */
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);//返回具体的像素值
    }

    /**
     * dip转换px
     *
     * @param dip
     * @return
     */
    public static int dip2px(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip + density + 0.5f);
    }

    /**
     * px convert dip
     *
     * @param px
     * @return
     */
    public static float px2dip(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return px / density;
    }

    /**
     * 加载布局文件
     *
     * @param id
     * @return
     */
    public static View inflate(int id) {
        return View.inflate(getContext(), id, null);
    }

}