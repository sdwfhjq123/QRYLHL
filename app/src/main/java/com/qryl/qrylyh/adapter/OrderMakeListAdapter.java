package com.qryl.qrylyh.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.MakeList.DataArea;
import com.qryl.qrylyh.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinhao on 2017/9/22.
 */

public class OrderMakeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "OrderMakeListAdapter";

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;

    private List<DataArea> data = new ArrayList<>();

    public OrderMakeListAdapter(List<DataArea> data) {
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_order_no_pay, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_footer_view, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).tvTitle.setText(data.get(position).getDoctorNurse().getRealName() + "为您服务");
            ((ItemViewHolder) holder).tvMoney.setText(data.get(position).getPrice() + "");
            ((ItemViewHolder) holder).tvNote.setText(data.get(position).getDoctorNurse().getRealName() + "为您服务");
            ((ItemViewHolder) holder).tvContent.setText("点击查看单子详情...");
            //点击条目
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
            //点击支付
            ((ItemViewHolder) holder).btnPay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onPayItemClick(((ItemViewHolder) holder).btnPay, position);
                }
            });
            //点击删除订单
            ((ItemViewHolder) holder).btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onDeleteItemClick(((ItemViewHolder) holder).btnDelete, position);
                }
            });
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

    public void setData(List<DataArea> data) {
        this.data = data;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvNote;
        TextView tvTitle;
        TextView tvContent;
        TextView tvMoney;
        Button btnPay;
        Button btnDelete;

        ItemViewHolder(View itemView) {
            super(itemView);
            tvNote = (TextView) itemView.findViewById(R.id.tv_note);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvMoney = (TextView) itemView.findViewById(R.id.tv_money);
            btnPay = (Button) itemView.findViewById(R.id.btn_pay);
            btnDelete = (Button) itemView.findViewById(R.id.btn_delete);
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

        void onDeleteItemClick(View view, int position);

        void onPayItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
