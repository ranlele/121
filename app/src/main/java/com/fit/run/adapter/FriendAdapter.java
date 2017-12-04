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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;



public class FriendAdapter extends RecyclerView.Adapter {


    private List<Rank> mUsers;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public FriendAdapter(List<Rank> users) {
        mUsers = users;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("ranks");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, null, false);
        return new FriendHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final FriendHolder friendHolder = (FriendHolder) holder;

        final Context context = holder.itemView.getContext();
        final Rank user = mUsers.get(position);
        if (user==null) {
            return;
        }
        friendHolder.mTvAccount.setText("User: " + user.getAccount());
        friendHolder.mTvLover.setText("Interest: " + user.getLover());
        friendHolder.mTvPoint.setText("Total point: " + user.getPoint());
        friendHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, InfoActivity.class).
                        putExtra(Config.ACCOUNT, user.getAccount())
                        .putExtra(Config.LOVER, user.getLover())

                        .putExtra(Config.UID, user.getUid())
                        .putExtra(Config.STEP, user.getStep())
                        .putExtra(Config.POINT, user.getPoint()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    class FriendHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_account)
        TextView mTvAccount;
        @BindView(R.id.tv_lover)
        TextView mTvLover;
        @BindView(R.id.tv_point)
        TextView mTvPoint;

        public FriendHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
