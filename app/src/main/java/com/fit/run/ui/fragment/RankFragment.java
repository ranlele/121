package com.fit.run.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fit.run.R;
import com.fit.run.adapter.RankAdapter;
import com.fit.run.bean.Account;
import com.fit.run.bean.Rank;
import com.fit.run.ui.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created on 17/11/13 14:47
 */

public class RankFragment extends BaseFragment {
    @BindView(R.id.rv_friend)
    RecyclerView mRvFriend;
    Unbinder unbinder;


    private RankAdapter mRankAdapter;
    private List<Rank> mRanks = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_rank, null, false);
        unbinder = ButterKnife.bind(this, view);
        initDatas();
        return view;
    }

    private void initDatas() {
        Account account = new Account();
        account.setAccount("Test Account");
        account.setFriend(true);
        account.setLover("yoga lover");
        account.setIntegral(999);
        Rank rank = new Rank();
        rank.setAccount(account);
        rank.setStep(1000L);
        mRanks.add(rank);
        mRanks.add(rank);
        mRanks.add(rank);
        mRankAdapter = new RankAdapter(mRanks);
        mRvFriend.setHasFixedSize(true);
        mRvFriend.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvFriend.setAdapter(mRankAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
