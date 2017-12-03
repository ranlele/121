package com.fit.run.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fit.run.R;
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
        final Context context = holder.itemView.getContext();
        final Rank rank = mRanks.get(position);
        final Account account = rank.getAccount();
        rankHolder.mTvAccount.setText("Account: " + account.getAccount());
        rankHolder.mTvLover.setText("Interest: " + account.getLover());
        rankHolder.mTvStep.setText("Today's step: " + account.getIntegral());
        rankHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, InfoActivity.class).
                        putExtra(Config.ACCOUNT, account.getAccount())
                        .putExtra(Config.LOVER, account.getLover())
                        .putExtra(Config.STEP, rank.getStep())
                        .putExtra(Config.INTEGRAL,account.getIntegral()));
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
        @BindView(R.id.tv_lover)
        TextView mTvLover;
        @BindView(R.id.tv_step)
        TextView mTvStep;

        public RankHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
