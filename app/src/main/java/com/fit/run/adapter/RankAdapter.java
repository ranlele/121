package com.fit.run.adapter;

import android.content.Context;
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
        rankHolder.mTvAccount.setText("User: " + rank.getAccount());
        rankHolder.mTvLover.setText("Interest: " + rank.getLover());
        rankHolder.mTvStep.setText("Today's step: " + rank.getStep());
        rankHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, InfoActivity.class).
                        putExtra(Config.ACCOUNT, rank.getAccount())
                        .putExtra(Config.LOVER, rank.getLover())
                        .putExtra(Config.STEP, rank.getStep())
                        .putExtra(Config.UID,rank.getUid())
                        .putExtra(Config.POINT, rank.getPoint()));
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
