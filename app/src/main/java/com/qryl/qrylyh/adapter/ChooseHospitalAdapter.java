package com.qryl.qrylyh.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.Hospital;
import com.qryl.qrylyh.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2017/9/14.
 */

public class ChooseHospitalAdapter extends RecyclerView.Adapter<ChooseHospitalAdapter.ViewHolder> {

    private static final String TAG = "ChooseHospitalAdapter";
    private List<Hospital> hospitalList = new ArrayList<>();
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public ChooseHospitalAdapter(List<Hospital> hospitalList) {
        this.hospitalList = hospitalList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvArea;
        LinearLayout llRoot;

        public ViewHolder(View itemView) {
            super(itemView);
            llRoot = (LinearLayout) itemView;
            tvArea = (TextView) itemView.findViewById(R.id.tv_area);
        }
    }

    @Override
    public ChooseHospitalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_choose, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.llRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Log.i(TAG, "onClick: " + "点击了" + position + "名字是:" + hospitalList.get(position).getId());
                //将数据保存在itemView的Tag中，以便点击时进行获取
                holder.llRoot.setTag(hospitalList.get(position).getId());
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ChooseHospitalAdapter.ViewHolder holder, int position) {
        holder.tvArea.setText(hospitalList.get(position).getHospitalName());

    }

    @Override
    public int getItemCount() {
        return hospitalList.size();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int data);
    }

}
