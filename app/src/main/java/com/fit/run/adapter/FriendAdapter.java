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

public class FriendAdapter extends RecyclerView.Adapter {


    private List<Account> mAccounts;

    public FriendAdapter(List<Account> accounts) {
        mAccounts = accounts;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, null, false);
        return new FriendHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        FriendHolder friendHolder = (FriendHolder) holder;

        final Context context = holder.itemView.getContext();
        final Account account = mAccounts.get(position);
        friendHolder.mTvAccount.setText("Account: " + account.getAccount());
        friendHolder.mTvLover.setText("Interest: " + account.getLover());
        friendHolder.mTvIntegral.setText("Total integral: " + account.getIntegral() );
        friendHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, InfoActivity.class).
                        putExtra(Config.ACCOUNT, account.getAccount())
                        .putExtra(Config.LOVER, account.getLover())
                        .putExtra(Config.INTEGRAL,account.getIntegral()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mAccounts.size();
    }


    class FriendHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_account)
        TextView mTvAccount;
        @BindView(R.id.tv_lover)
        TextView mTvLover;
        @BindView(R.id.tv_integral)
        TextView mTvIntegral;

        public FriendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
