package com.fit.run.adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fit.run.Config;
import com.fit.run.R;
import com.fit.run.bean.Account;
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

        friendHolder.mTvAccount.setText(mAccounts.get(position).getAccount());
        friendHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.itemView.getContext().startActivity(new Intent(holder.itemView.getContext(), InfoActivity.class).
                        putExtra(Config.ACCOUNT, mAccounts.get(position).getAccount()));
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

        public FriendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
