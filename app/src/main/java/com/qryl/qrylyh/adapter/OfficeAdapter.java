package com.qryl.qrylyh.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinhao on 2017/9/16.
 */

public class OfficeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "OfficeAdapter";

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;

    private List<com.qryl.qrylyh.VO.WorkVO.DataArea> data = new ArrayList<>();

    public OfficeAdapter(List<com.qryl.qrylyh.VO.WorkVO.DataArea> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_be_good_at_work, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_footer_view, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((OfficeAdapter.ItemViewHolder) holder).cbWork.setText(data.get(position).getName());
            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = holder.getAdapterPosition();
                        onItemClickListener.onItemClick(v, position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return data.size() == 0 ? 0 : data.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_NORMAL;
        }
    }

    public void setData(List<com.qryl.qrylyh.VO.WorkVO.DataArea> data) {
        this.data = data;
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbWork;

        ItemViewHolder(View itemView) {
            super(itemView);
            cbWork = (CheckBox) itemView;
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}

