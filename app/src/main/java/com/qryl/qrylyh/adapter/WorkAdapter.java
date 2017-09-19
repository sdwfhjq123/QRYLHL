package com.qryl.qrylyh.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.BeGoodAtWorkVO.Data;
import com.qryl.qrylyh.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2017/9/18.
 */

public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {

    private static final String TAG = "WorkAdapter";

    private List<Data> datas = new ArrayList<>();

    public WorkAdapter(List<Data> datas) {
        this.datas = datas;
    }

    @Override
    public WorkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_be_good_at_work, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WorkAdapter.ViewHolder holder, final int position) {
        holder.cbBox.setText(datas.get(position).getName());
        holder.cbBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.cbBox.isChecked()) {
                    Log.i(TAG, "onClick: 点击了" + position);
                    onItemClickListener.onAddClickLister(v, position);
                } else if (!holder.cbBox.isChecked()) {
                    Log.i(TAG, "onClick: 取消点击了" + position);
                    onItemClickListener.onDeleteClickLister(v, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setData(List<Data> data) {
        this.datas = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbBox;

        public ViewHolder(View itemView) {
            super(itemView);
            cbBox = (CheckBox) itemView;
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onAddClickLister(View view, int position);

        void onDeleteClickLister(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
