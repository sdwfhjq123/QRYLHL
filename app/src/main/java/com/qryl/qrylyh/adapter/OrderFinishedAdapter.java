package com.qryl.qrylyh.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.OrderVO.OrderInfoArea;
import com.qryl.qrylyh.util.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yinhao on 2017/9/22.
 */

public class OrderFinishedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "OrderUnderwayAdapter";

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_FOOTER = 1;

    private List<OrderInfoArea> data = new ArrayList<>();

    public OrderFinishedAdapter(List<OrderInfoArea> datas) {
        this.data = datas;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_order_finish, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_footer_view, parent, false);
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OrderFinishedAdapter.ItemViewHolder) {
            ((OrderFinishedAdapter.ItemViewHolder) holder).tvMoney.setText(data.get(position).getPrice() + "");
            ((OrderFinishedAdapter.ItemViewHolder) holder).tvNote.setText(data.get(position).getNote());
            ((OrderFinishedAdapter.ItemViewHolder) holder).tvContent.setText(data.get(position).getContent());
            ((OrderFinishedAdapter.ItemViewHolder) holder).tvTitle.setText(data.get(position).getTitle());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
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

    public void setData(List<OrderInfoArea> data) {
        this.data = data;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvNote;
        TextView tvTitle;
        TextView tvContent;
        TextView tvMoney;
        Button evaluate;

        public ItemViewHolder(View itemView) {
            super(itemView);
            tvNote = (TextView) itemView.findViewById(R.id.tv_note);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvContent = (TextView) itemView.findViewById(R.id.tv_content);
            tvMoney = (TextView) itemView.findViewById(R.id.tv_money);
            evaluate = (Button) itemView.findViewById(R.id.btn_evaluate);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onDeleteItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
