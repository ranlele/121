package com.fit.run.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fit.run.Config;
import com.fit.run.R;
import com.fit.run.bean.Rank;
import com.fit.run.ui.activity.InfoActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created on 17/11/13 15:52
 */

public class RankAdapter extends RecyclerView.Adapter {


    private List<Rank> mRanks;

    public RankAdapter(List<Rank> ranks) {
        mRanks = ranks;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rank, null, false);
        return new RankHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        RankHolder rankHolder = (RankHolder) holder;

        rankHolder.mTvAccount.setText(mRanks.get(position).getAccount().getAccount());
        rankHolder.mTvStep.setText(mRanks.get(position).getStep() + " step");
        rankHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), InfoActivity.class).
                        putExtra(Config.ACCOUNT, mRanks.get(position).getAccount().getAccount()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mRanks.size();
    }


    class RankHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_account)
        TextView mTvAccount;
        @BindView(R.id.tv_step)
        TextView mTvStep;

        public RankHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
