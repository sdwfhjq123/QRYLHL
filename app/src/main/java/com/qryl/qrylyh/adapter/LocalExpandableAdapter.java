package com.qryl.qrylyh.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.County;
import com.qryl.qrylyh.VO.Row;
import com.qryl.qrylyh.util.UIUtils;

import java.util.ArrayList;

/**
 * Created by hp on 2017/9/15.
 */

public class LocalExpandableAdapter extends BaseExpandableListAdapter {

    private static final String TAG = "LocalExpandableAdapter";

    private ArrayList<County> counties = new ArrayList<>();
    private ArrayList<ArrayList<Row>> rows = new ArrayList<>();
    private Context context;

    private OnChooseItemClickListener mOnItemClickListener = null;

    public LocalExpandableAdapter(ArrayList<County> counties, ArrayList<ArrayList<Row>> rows, Context context) {
        this.counties = counties;
        this.rows = rows;
        this.context = context;
    }


    @Override
    public int getGroupCount() {
        return counties.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return rows.get(groupPosition).size();
    }

    @Override
    public County getGroup(int groupPosition) {
        return counties.get(groupPosition);
    }

    @Override
    public Row getChild(int groupPosition, int childPosition) {
        return rows.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //取得用于显示给定分组的视图. 这个方法仅返回分组的视图对象
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ViewHolderCounty holderCounty;
        if (convertView == null) {
            convertView = LayoutInflater.from(UIUtils.getContext()).inflate(
                    R.layout.list_exlist_group, parent, false);
            holderCounty = new ViewHolderCounty();
            holderCounty.tvGroupName = (TextView) convertView.findViewById(R.id.tv_group_name);
            convertView.setTag(holderCounty);
        } else {
            holderCounty = (ViewHolderCounty) convertView.getTag();
        }
        holderCounty.tvGroupName.setText(counties.get(groupPosition).getCounty());
        return convertView;
    }

    //取得显示给定分组给定子位置的数据用的视图
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolderRow viewHolderRow;
        if (convertView == null) {
            convertView = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.list_exlist_item, parent, false);
            viewHolderRow = new ViewHolderRow();
            viewHolderRow.cbName = (CheckBox) convertView.findViewById(R.id.cb_name);
            convertView.setTag(viewHolderRow);
        } else {
            viewHolderRow = (ViewHolderRow) convertView.getTag();
        }
        viewHolderRow.cbName.setText(rows.get(groupPosition).get(childPosition).getName());
        //final ArrayList<ArrayList<Row>> arrayLists = new ArrayList<ArrayList<Row>>();
        viewHolderRow.cbName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolderRow.cbName.isChecked()) {
                    int id = rows.get(groupPosition).get(childPosition).getId();
                    Log.i(TAG, "onClick:选中了 " + id);
                    //接口实例化后的而对象，调用重写后的方法
                    mOnItemClickListener.onItemClick(v, groupPosition, childPosition, id);
                } else if (!viewHolderRow.cbName.isChecked()) {
                    mOnItemClickListener.onDeleteItemClick(v, groupPosition, childPosition);
                    Log.i(TAG, "onClick:取消选中了 " + rows.get(groupPosition).get(childPosition).getName());
                }
            }
        });
        return convertView;
    }

    //设置子列表是否可选中
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private class ViewHolderCounty {
        private TextView tvGroupName;
    }

    private class ViewHolderRow {
        private CheckBox cbName;
    }

    //define interface
    public interface OnChooseItemClickListener {
        void onItemClick(View view, int groupPosition, int childPosition, int id);

        void onDeleteItemClick(View view, int groupPosition, int childPosition);
    }

    public void setOnChooseItemClickListener(OnChooseItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
