package com.fit.run.ui.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fit.run.R;
import com.fit.run.adapter.RankAdapter;
import com.fit.run.bean.Rank;
import com.fit.run.ui.base.BaseFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;



public class RankFragment extends BaseFragment {
    @BindView(R.id.rv_friend)
    RecyclerView mRvFriend;
    Unbinder unbinder;


    private RankAdapter mRankAdapter;
    private List<Rank> mRanks = new ArrayList<>();


    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_rank, null, false);
        unbinder = ButterKnife.bind(this, view);
        mRvFriend.setLayoutManager(new LinearLayoutManager(getContext()));
        mRvFriend.setHasFixedSize(true);
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        refresh();
        return view;
    }


    private void refresh() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        Query query = mDatabaseReference.child("ranks").orderByChild("step");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                mRanks.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Rank user = snapshot.getValue(Rank.class);
                    mRanks.add(user);
                }
                Collections.reverse(mRanks);
                mRankAdapter = new RankAdapter(mRanks);
                mRvFriend.setAdapter(mRankAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
