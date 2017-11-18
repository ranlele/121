package com.fit.run.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fit.run.R;
import com.fit.run.adapter.FriendAdapter;
import com.fit.run.bean.Account;
import com.fit.run.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created on 17/11/13 14:47
 *
 * @author
 */

public class FriendFragment extends BaseFragment {

    @BindView(R.id.rv_friend)
    RecyclerView mRvFriend;
    Unbinder unbinder;


    private FriendAdapter mFriendAdapter;
    private List<Account> mAccounts = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_friend, null, false);
        unbinder = ButterKnife.bind(this, view);
        initDatas();
        return view;
    }

    private void initDatas() {
        mRvFriend.setHasFixedSize(true);
        mRvFriend.setLayoutManager(new LinearLayoutManager(getContext()));
        Account account = new Account();
        account.setAccount("Test Account");
        account.setFriend(true);
        mAccounts.add(account);
        mAccounts.add(account);
        mAccounts.add(account);
        mFriendAdapter = new FriendAdapter(mAccounts);
        mRvFriend.setAdapter(mFriendAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
