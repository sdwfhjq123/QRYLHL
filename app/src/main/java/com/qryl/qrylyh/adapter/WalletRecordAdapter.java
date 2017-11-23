package com.qryl.qrylyh.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qryl.qrylyh.R;
import com.qryl.qrylyh.VO.WalletRecordVO.Data;
import com.qryl.qrylyh.util.UIUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hp on 2017/11/16.
 */

public class WalletRecordAdapter extends RecyclerView.Adapter<WalletRecordAdapter.ViewHolder> {
    private List<Data> datas = new ArrayList<>();

    public WalletRecordAdapter(List<Data> datas) {
        this.datas = datas;
    }

    @Override
    public WalletRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(UIUtils.getContext()).inflate(R.layout.item_wallet_record, null);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(WalletRecordAdapter.ViewHolder holder, int position) {
        holder.tvAccountName.setText(String.valueOf(datas.get(position).getAccountName()));
        holder.tvWithdrawAccount.setText(datas.get(position).getWithdrawAccount());
        holder.tvCreateTime.setText(long2Date(datas.get(position).getCreateTime()));
        holder.tvModifyTime.setText(long2Date(datas.get(position).getModifyTime()));
        holder.tvAmount.setText(String.valueOf(datas.get(position).getAmount()) + "元");
        int status = datas.get(position).getStatus();
        switch (status) {
            case 0://正在审核中
                holder.tvResult.setText("正在审核中...");
                break;
            case 1://已到账
                holder.tvResult.setText("已经到账");
                break;
            case 2://审核失败
                holder.tvResult.setText("审核失败,原因:" + datas.get(position).getRejectReason());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setData(List<Data> datas) {
        this.datas = datas;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAccountName;
        TextView tvWithdrawAccount;
        TextView tvCreateTime;
        TextView tvResult;
        TextView tvModifyTime;
        TextView tvAmount;

        ViewHolder(View itemView) {
            super(itemView);
            tvAccountName = (TextView) itemView.findViewById(R.id.tv_account_name);
            tvWithdrawAccount = (TextView) itemView.findViewById(R.id.tv_withdraw_deposit);
            tvCreateTime = (TextView) itemView.findViewById(R.id.tv_create_time);
            tvResult = (TextView) itemView.findViewById(R.id.tv_result);
            tvModifyTime = (TextView) itemView.findViewById(R.id.tv_modify_time);
            tvAmount = (TextView) itemView.findViewById(R.id.tv_amount);
        }
    }

    @SuppressLint("SimpleDateFormat")
    private String long2Date(Long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }
}
